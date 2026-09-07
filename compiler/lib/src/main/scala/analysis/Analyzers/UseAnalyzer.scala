package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

import scala.annotation.tailrec

/**
 * Analyze uses
 * This analyzer assumes that CheckUses has already been run to populate the use-def map
 */
trait UseAnalyzer extends BasicUseAnalyzer {

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    a.useDefMap.get(node.id) match {
      case Some(Symbol.Constant(_) | Symbol.EnumConstant(_)) =>
        // e is a use, so it must be a constant use
        val use = Name.Qualified.fromIdentList(e.getIdentListOpt.get)
        constantUse(a, node, use)
      case Some(_) =>
        // This is some other type of symbol, which it shouldn't be
        throw InternalError("expected a constant use")
      case None =>
        // e is not a use, so it selects a member of a struct value
        // Analyze the left-hand expression representing the struct value
        exprNode(a, e.e)
    }

}
