package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/**
 * Analyze uses
 * This analyzer assumes that CheckUses has already been run to populate the use-def map
 */
trait UseAnalyzer extends BasicUseAnalyzer {

  /** Gets a qualified name from a dot expression */
  private def getQualifiedName(
    e: Ast.Expr,
    qualifier: List[Name.Unqualified] = Nil
  ): Name.Qualified = e match {
    case Ast.ExprIdent(id) =>
      Name.Qualified.fromIdentList(id :: qualifier)
    case Ast.ExprDot(e1, id) =>
      getQualifiedName(e1.data, id.data :: qualifier)
    case _ => throw new InternalError("expected a qualified name")
  }

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    a.useDefMap.get(node.id) match {
      case Some(_) =>
        // e is a use, so it must be a constant use
        val use = getQualifiedName(e)
        constantUse(a, node, use)
      case None =>
        // e is not a use, so it selects a member of a struct value
        // Analyze the left-hand expression representing the struct value
        exprNode(a, e.e)
    }

}
