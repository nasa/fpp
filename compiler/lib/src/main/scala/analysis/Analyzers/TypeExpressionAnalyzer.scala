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

  private def opt[T] (f: (Analysis, T) => Result) (a: Analysis, o: Option[T]): Result =
    o match {
      case Some(x) => f(a, x)
      case None => Right(a)
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
