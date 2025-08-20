package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/**
 * Analyze uses
 * CheckUses must have already been run since we must know what
 * is and isn't a constant.
 */
trait UseAnalyzer extends BasicUseAnalyzer {

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) = {
    def nameOpt(cNode: AstNode[Ast.Expr], cE: Ast.Expr, qualifier: List[Name.Unqualified]): Result.Result[Name.Qualified] = {
      cE match {
        case Ast.ExprIdent(id) => {
          val list = id :: qualifier
          Right(Name.Qualified.fromIdentList(list))
        }
        case Ast.ExprDot(e1, id) => nameOpt(e1, e1.data, id.data :: qualifier)
        case _ => Left(SemanticError.InvalidExpression(
          Locations.get(node.id),
          "expression does not refer to a definition or struct literal"
        ))
      }
    }

    a.useDefMap.get(node.id) match {
      // Check if this entire expression is constant use
      case Some(Symbol.Constant(_) | Symbol.EnumConstant(_)) =>
        for {
          use <- nameOpt(node, e, Nil)
          a <- constantUse(a, node, use)
        } yield a

      // Analyze this expression in pieces
      case _ => exprNode(a, e.e)
    }
  }

}
