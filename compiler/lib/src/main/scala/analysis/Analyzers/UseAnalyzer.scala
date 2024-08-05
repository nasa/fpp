package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze uses */
trait UseAnalyzer extends TypeExpressionAnalyzer {

  /** A use of a component definition */
  def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)
 
  /** A use of a component instance definition */
  def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a constant definition or enumerated constant definition */
  def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified): Result = default(a)

  /** A use of a port definition */
  def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a topology definition */
  def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  /** A use of a type definition */
  def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified): Result = default(a)

  /** A use of a state machine definition*/
  def stateMachineUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(a)

  override def defComponentInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) = {
    val (_, node1, _) = node
    val data = node1.data
    for {
      a <- qualIdentNode (componentUse) (a, data.component)
      a <- super.defComponentInstanceAnnotatedNode(a, node)
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
      case Some(use) => constantUse(a, node, use)
      case None => Right(a)
    }
  }

  override def exprIdentNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprIdent) = {
    val use = Name.Qualified(Nil, e.value)
    constantUse(a, node, use)
  }

  override def specCompInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]) = {
    val (_, node1, _) = node
    val data = node1.data
    qualIdentNode (componentInstanceUse) (a, data.instance)
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
        a <- qualIdentNode (componentInstanceUse) (a, pattern.source)
        a <- visitList(a, pattern.targets, qualIdentNode (componentInstanceUse) _)
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
        val nodeList = identList.map(AstNode.create(_, node.id))
        val qualIdent = Ast.QualIdent.fromNodeList(nodeList)
        val impliedUse = AstNode.create(qualIdent, node.id)
        for {
          a <- opt(exprNode)(a, special.priority)
          a <- qualIdentNode(portUse)(a, impliedUse)
        } yield a
    }
  }

  override def specTopImportAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecTopImport]]) = {
    val (_, node1, _) = node
    val data = node1.data
    qualIdentNode(topologyUse)(a, data.top)
  }

  override def typeNameQualIdentNode(a: Analysis, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent) = {
    val use = Name.Qualified.fromQualIdent(tn.name.data)
    typeUse(a, node, use)
  }

  private def portInstanceIdentifierNode(a: Analysis, node: AstNode[Ast.PortInstanceIdentifier]): Result =
    qualIdentNode (componentInstanceUse) (a, node.data.componentInstance)

  private def qualIdentNode
    (f: (Analysis, AstNode[Ast.QualIdent], Name.Qualified) => Result) 
    (a: Analysis, qualIdent: AstNode[Ast.QualIdent]): Result = {
    val use = Name.Qualified.fromQualIdent(qualIdent.data)
    f(a, qualIdent, use)
  }

}
