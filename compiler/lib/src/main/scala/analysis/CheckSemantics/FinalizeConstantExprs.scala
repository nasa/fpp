package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Finalize constant expressions. Fill in any missing anonymous array sizes */
object FinalizeConstantExprs extends UseAnalyzer
{
  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.id)
    val emptyListError = SemanticError.EmptyArray(loc)
    for {
      a <- super.exprArrayNode(a, node, e)
      eltType <- a.commonType(e.elts.map(_.id), emptyListError)
      _ <- {
        val typeNames = e.elts.map(elt => a.typeMap(elt.id).toString)
        val msg = "cannot compute common type of array value with elements: " + typeNames.mkString(", ")
        eltType match {
          case Type.AnonArray(None, _) => Left(
            SemanticError.TypeMismatch(loc, msg)
          )
          case _ => Right(())
        }
      }
    } yield {
      def f(node: AstNode[Ast.Expr]) = {
        val v = a.valueMap(node.id)
        Analysis.convertValueToType(v, eltType)
      }
      val elts = e.elts.map(f)
      val v = Value.AnonArray(elts)
      a.assignType(node -> Type.AnonArray(Some(e.elts.size), eltType))
        .assignValue(node -> v)
    }
  }

  // FIXME(tumbar) This is probably not needed
  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    val symbol = a.useDefMap(node.id)
    for {
      a <- symbol match {
        // Constant symbol: visit the constant definition
        // to ensure it has a type
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        // Enum symbol: if this is in scope, then we are in
        // the enum definition, so it already has a type
        case Symbol.EnumConstant(node) => Right(a)
        // Template parameter symbol: we are already inside the template expansion
        // therefore this already has a type
        case Symbol.TemplateConstantArg(_, _) => Right(a)
        // Invalid use of a symbol in an expression
        case _ =>
          Left(SemanticError.InvalidSymbol(
            symbol.getUnqualifiedName,
            Locations.get(node.id),
            "not a constant symbol",
            symbol.getLoc
          ))
      }
    } yield {
      val t = a.typeMap(symbol.getNodeId)
      a.assignType(node -> t)
    }
  }
}
