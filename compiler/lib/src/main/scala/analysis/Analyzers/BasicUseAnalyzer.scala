package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.ast.Ast.Annotated
import fpp.compiler.ast.Ast.DefModuleTemplate

/**
 * Basic use analysis
 * Assumes all qualified identifiers are constant uses
 */
trait BasicUseAnalyzer extends TypeExpressionAnalyzer {

  /** A use of a component definition */
  def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a interface instance (topology def or component instance def) */
  def interfaceInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a constant definition or enumerated constant definition */
  def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified): Result = default(a)

  /** A use of a port definition */
  def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of an interface definition */
  def interfaceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a type definition */
  def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified): Result = default(a)

  /** A use of a state machine definition */
  def stateMachineUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a template definition */
  def templateUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a template constant parameter */
  def templateConstantParam(a: Analysis, param: Symbol.TemplateConstantParam) = {
    exprNode(a, param.value)
  }

  /** A use of a template type parameter */
  def templateTypeParam(a: Analysis, param: Symbol.TemplateTypeParam) = {
    typeNameNode(a, param.value)
  }

  /** A use of a template interface parameter */
  def templateInterfaceParam(a: Analysis, param: Symbol.TemplateInterfaceParam) = {
    qualIdentNode (interfaceInstanceUse) (a, param.value)
  }

  override def defComponentInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) = {
    val (_, node1, _) = node
    val data = node1.data
    for {
      a <- qualIdentNode (componentUse) (a, data.component)
      a <- super.defComponentInstanceAnnotatedNode(a, node)
    } yield a
  }

  override def defTopologyAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefTopology]]) = {
    val id = node._2.id
    for {
      a <- visitImpliedUses(a, id)

      // Visit port interface uses in the implements clause
      a <- {
        Result.foldLeft (node._2.data.implements) (a) ((a, impl) =>
          qualIdentNode(interfaceUse) (a, impl)
      )}

      a <- super.defTopologyAnnotatedNode(a, node)
    } yield a
  }

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) = {
    def nameOpt(e: Ast.Expr, qualifier: List[Name.Unqualified]): Option[Name.Qualified] = {
      e match {
        case Ast.ExprIdent(id) => {
          val list = id :: qualifier
          val use = Name.Qualified.fromIdentList(list)
          Some(use)
        }
        case Ast.ExprDot(e1, id) => nameOpt(e1.data, id.data :: qualifier)
        case _ => None
      }
    }

    nameOpt(e, Nil) match {
      // Assume the entire qualified identifier is a use
      case Some(use) => constantUse(a, node, use)
      // This is some other type of dot expression (not a qual ident)
      // Analyze the left side, which may contain constant uses
      case None => exprNode(a, e.e)
    }
  }

  override def exprIdentNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprIdent) = {
    val use = Name.Qualified(Nil, e.value)
    constantUse(a, node, use)
  }

  override def specInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecInstance]]) = {
    val (_, node1, _) = node
    val data = node1.data
    qualIdentNode (interfaceInstanceUse) (a, data.instance)
  }

  override def specStateMachineInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]) = {
    val (_, node1, _) = node
    val data = node1.data
    for {
      a <- qualIdentNode(stateMachineUse)(a, data.stateMachine)
      a <- super.specStateMachineInstanceAnnotatedNode(a, node)
    } yield a
  }

  override def specConnectionGraphAnnotatedNode(
    a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]) = {
    def connection(a: Analysis, connection: Ast.SpecConnectionGraph.Connection): Result = {
      for {
        a <- portInstanceIdentifierNode(a, connection.fromPort)
        a <- opt(exprNode)(a, connection.fromIndex)
        a <- portInstanceIdentifierNode(a, connection.toPort)
        a <- opt(exprNode)(a, connection.toIndex)
      } yield a
    }
    val (_, node1, _) = node
    val data = node1.data
    data match {
      case direct : Ast.SpecConnectionGraph.Direct => visitList(a, direct.connections, connection)
      case pattern : Ast.SpecConnectionGraph.Pattern => for {
        a <- qualIdentNode (interfaceInstanceUse) (a, pattern.source)
        a <- visitList(a, pattern.targets, qualIdentNode(interfaceInstanceUse))
      } yield a
    }
  }

  override def specPortInstanceAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node, _) = aNode
    val data = node.data
    data match {
      case general : Ast.SpecPortInstance.General =>
        for {
          a <- opt(exprNode)(a, general.size)
          a <- opt(qualIdentNode(portUse))(a, general.port)
          a <- opt(exprNode)(a, general.priority)
        } yield a
      case special : Ast.SpecPortInstance.Special =>
        // Construct the use implied by the special port
        val name = special.kind match {
          case Ast.SpecPortInstance.CommandRecv => "Cmd"
          case Ast.SpecPortInstance.CommandReg => "CmdReg"
          case Ast.SpecPortInstance.CommandResp => "CmdResponse"
          case Ast.SpecPortInstance.Event => "Log"
          case Ast.SpecPortInstance.ParamGet => "PrmGet"
          case Ast.SpecPortInstance.ParamSet => "PrmSet"
          case Ast.SpecPortInstance.ProductGet => "DpGet"
          case Ast.SpecPortInstance.ProductRecv => "DpResponse"
          case Ast.SpecPortInstance.ProductRequest => "DpRequest"
          case Ast.SpecPortInstance.ProductSend => "DpSend"
          case Ast.SpecPortInstance.Telemetry => "Tlm"
          case Ast.SpecPortInstance.TextEvent => "LogText"
          case Ast.SpecPortInstance.TimeGet => "Time"
        }
        val identList = List("Fw", name)
        val impliedUse = ImpliedUse.fromIdentListAndId(identList, node.id).asQualIdentNode
        for {
          a <- opt(exprNode)(a, special.priority)
          a <- qualIdentNode(portUse)(a, impliedUse)
        } yield a
    }
  }

  override def specTlmPacketAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]]
  ) = for {
    a <- super.specTlmPacketAnnotatedNode(a, aNode)
    a <- visitList(a, aNode._2.data.members, tlmPacketMember)
  } yield a

  override def specTlmPacketSetAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
  ) = for {
    a <- super.specTlmPacketSetAnnotatedNode(a, aNode)
    a <- visitList(a, aNode._2.data.omitted, tlmChannelIdentifierNode)
  } yield a

  override def specInterfaceImportAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecImport]]) = {
    val (_, node1, _) = node
    val data = node1.data
    qualIdentNode(interfaceUse)(a, data.sym)
  }

  override def specTopPortAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecTopPort]]) = {
    val (_, node1, _) = node
    val data = node1.data
    portInstanceIdentifierNode(a, data.underlyingPort)
  }

  override def typeNameQualIdentNode(a: Analysis, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent) = {
    val use = Name.Qualified.fromQualIdent(tn.name.data)
    typeUse(a, node, use)
  }

  def templateParam(a: Analysis, param: Symbol.TemplateParam) = {
    param match {
      case param: Symbol.TemplateConstantParam => templateConstantParam(a, param)
      case param: Symbol.TemplateTypeParam => templateTypeParam(a, param)
      case param: Symbol.TemplateInterfaceParam => templateInterfaceParam(a, param)
    }
  }

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- templateUse(
        a,
        data.template,
        Name.Qualified.fromQualIdent(data.template.data)
      )

      // Analyze the paramters on this template expansion
      a <- {
        val expansion = a.templateExpansionMap(node.id)
        Result.foldLeft (expansion.params.values.toList) (a) (templateParam)
      }

      a <- super.specTemplateExpandAnnotatedNode(a, aNode)
    } yield a
  }

  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) = {
    def templateParam(
      a: Analysis,
      pNode: Ast.Annotated[AstNode[Ast.DefTemplateParam.Node]]
    ) = {
      val (_, node, _) = pNode
      node.data match {
        case Ast.DefTemplateParam.Constant(_, tn) => typeNameNode(a, tn)
        case Ast.DefTemplateParam.Interface(_, interface) =>
          qualIdentNode(interfaceUse)(a, interface)
        case _: Ast.DefTemplateParam.Type => Right(a)
      }
    }

    Result.foldLeft (aNode._2.data.params) (a) (templateParam)
  }

  private def portInstanceIdentifierNode(a: Analysis, node: AstNode[Ast.PortInstanceIdentifier]): Result =
    qualIdentNode (interfaceInstanceUse) (a, node.data.interfaceInstance)

  protected def qualIdentNode
    (f: (Analysis, AstNode[Ast.QualIdent], Name.Qualified) => Result) 
    (a: Analysis, qualIdent: AstNode[Ast.QualIdent]): Result = {
    val use = Name.Qualified.fromQualIdent(qualIdent.data)
    f(a, qualIdent, use)
  }

  private def tlmChannelIdentifierNode (
    a: Analysis,
    node: AstNode[Ast.TlmChannelIdentifier]
  ): Result =
    qualIdentNode (interfaceInstanceUse) (a, node.data.componentInstance)

  private def tlmPacketMember(a: Analysis, member: Ast.TlmPacketMember) =
    member match {
      case Ast.TlmPacketMember.SpecInclude(node) => Right(a)
      case Ast.TlmPacketMember.TlmChannelIdentifier(node) =>
        tlmChannelIdentifierNode(a, node)
    }

  private def visitImpliedConstantUses(a: Analysis, id: AstNode.Id) = {
    val uses = a.getImpliedUses(ImpliedUse.Kind.Constant, id).toList
    def visit(a: Analysis, iu: ImpliedUse) = exprNode(a, iu.asExprNode)
    visitList(a, uses, visit)
  }

  private def visitImpliedTypeUses(a: Analysis, id: AstNode.Id) = {
    val uses = a.getImpliedUses(ImpliedUse.Kind.Type, id).toList
    def visit(a: Analysis, iu: ImpliedUse) = typeUse(a, iu.asTypeNameNode, iu.name)
    visitList(a, uses, visit)
  }

  private def visitImpliedUses(a: Analysis, id: AstNode.Id) = {
    for {
      a <- visitImpliedConstantUses(a, id)
      a <- visitImpliedTypeUses(a, id)
    } yield a
  }

}
