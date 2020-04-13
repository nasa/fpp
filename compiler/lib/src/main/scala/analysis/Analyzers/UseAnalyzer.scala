package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze uses */
trait UseAnalyzer extends TypeExpressionAnalyzer {

  override def exprNode(a: Analysis, node: AstNode[Ast.Expr]): Result = {
    System.out.println(node)
    default(a)
  }

  override def typeNameNode(a: Analysis, node: AstNode[Ast.TypeName]): Result = {
    System.out.println(node)
    default(a)
  }

  override def specCompInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    default(a)
  }

  override def specInitAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecInit]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    default(a)
  }

  override def specPortInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    default(a)
  }

  override def specTopImportAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecTopImport]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    default(a)
  }

  override def specUnusedPortsAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecUnusedPorts]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    default(a)
  }

}
