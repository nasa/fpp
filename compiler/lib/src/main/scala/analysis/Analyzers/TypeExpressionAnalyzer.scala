package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze types and expressions */
trait TypeExpressionAnalyzer 
  extends Analyzer 
  with ComponentAnalyzer
  with EnumAnalyzer 
  with ModuleAnalyzer
  with TopologyAnalyzer
{

  override def defArrayAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- exprNode(a, data.size)
      a <- typeNameNode(a, data.eltType)
      a <- opt(exprNode)(a, data.default)
    } yield a
  }

  override def defComponentInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- qualIdentTypeNameNode(a, data.typeName)
      a <- exprNode(a, data.baseId)
      a <- opt(exprNode)(a, data.queueSize)
      a <- opt(exprNode)(a, data.stackSize)
      a <- opt(exprNode)(a, data.priority)
    } yield a
  }

  override def defConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    exprNode(a, data.value)
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    opt(exprNode)(a, data.value)
  }

  override def defPortAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- visitList(a, data.params, formalParamNode)
      a <- opt(typeNameNode)(a, data.returnType)
    } yield a
  }

  override def defStructAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- visitList(a, data.members, structTypeMemberAnnotatedNode)
      a <- opt(exprNode)(a, data.default)
    } yield a
  }

  override def specCommandAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecCommand]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- visitList(a, data.params, formalParamNode)
      a <- opt(exprNode)(a, data.opcode)
      a <- opt(exprNode)(a, data.priority)
    } yield a
  }

  override def specConnectionGraphAnnotatedNode(
    a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]) = {
    def connection(a: Analysis, connection: Ast.SpecConnectionGraph.Connection): Result = {
      for {
        a <- opt(exprNode)(a, connection.fromIndex)
        a <- opt(exprNode)(a, connection.toIndex)
      } yield a
    }
    val (_, node1, _) = node
    val data = node1.getData
    data match {
      case direct @ Ast.SpecConnectionGraph.Direct(_, _) => visitList(a, direct.connections, connection)
      case pattern @ Ast.SpecConnectionGraph.Pattern(_, _, _) => exprNode(a, pattern.pattern)
    }
  }

  override def specEventAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecEvent]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- visitList(a, data.params, formalParamNode)
      a <- opt(exprNode)(a, data.id)
      a <- opt(exprNode)(a, data.throttle)
    } yield a
  }

  override def specInitAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecInit]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    exprNode(a, data.phase)
  }

  override def specInternalPortAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecInternalPort]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- visitList(a, data.params, formalParamNode)
      a <- opt(exprNode)(a, data.priority)
    } yield a
  }

  override def specParamAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecParam]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- typeNameNode(a, data.typeName)
      a <- opt(exprNode)(a, data.default)
      a <- opt(exprNode)(a, data.id)
      a <- opt(exprNode)(a, data.setOpcode)
      a <- opt(exprNode)(a, data.saveOpcode)
    } yield a
  }

  override def specPortInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    data match {
      case general @ Ast.SpecPortInstance.General(_, _, _, _, _, _) =>
        for {
          a <- opt(exprNode)(a, general.size)
          a <- opt(exprNode)(a, general.priority)
        } yield a
      case _ => Right(a)
    }
  }

  override def specTlmChannelAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]) = {
    def limit(a: Analysis, value: Ast.SpecTlmChannel.Limit) = {
      val (_, e) = value
      exprNode(a, e)
    }
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- typeNameNode(a, data.typeName)
      a <- opt(exprNode)(a, data.id)
      a <- visitList(a, data.low, limit)
      a <- visitList(a, data.high, limit)
    } yield a
  }

  private def exprNode(a: Analysis, node: AstNode[Ast.Expr]): Result = {
    System.out.println(node)
    default(a)
  }

  private def formalParamNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.FormalParam]]): Result = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- typeNameNode(a, data.typeName)
      a <- opt(exprNode)(a, data.size)
    } yield a
  }

  private def qualIdentTypeNameNode(a: Analysis, node: AstNode[Ast.QualIdent]): Result = {
    val typeName = Ast.TypeNameQualIdent(node.data)
    val id = node.getId
    val node1 = AstNode.create(typeName, id)
    typeNameNode(a, node1)
  }

  private def structTypeMemberAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ): Result = {
    val (_, node1, _) = node
    val data = node1.getData
    typeNameNode(a, data.typeName)
  }

  private def typeNameNode(a: Analysis, node: AstNode[Ast.TypeName]): Result = {
    System.out.println(node)
    default(a)
  }

}
