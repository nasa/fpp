package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check for use-def cycles */
object CheckUseDefCycles extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified): Result =
    visitUse(a, node, use)

  override def defArrayAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val symbol = Symbol.Array(node)
    visitDefPost(a, symbol, node, super.defArrayAnnotatedNode)
  }

  override def defConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val symbol = Symbol.Constant(node)
    visitDefPost(a, symbol, node, super.defConstantAnnotatedNode)
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) =
    visitUse(a, node, use)

  private def error(a: Analysis, symbol: Symbol): Result = {
    def msg: String = {
      def printMatching(m: UseDefMatching) = "\n  " ++ m.toString
      "encountered a use-def cycle:" ++ a.useDefMatchingList.reverse.map(printMatching).flatten
    }
    val loc = symbol.getLoc
    Left(SemanticError.UseDefCycle(loc, msg))
  }

  private def visitDefPre(a: Analysis, symbol: Symbol): Result = {
    symbol match {
      case Symbol.Array(node) => defArrayAnnotatedNode(a, node)
      case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
      case _ => Right(a)
    }
  }

  private def visitDefPost[T](
    a: Analysis,
    symbol: Symbol,
    node: T,
    visitor: (Analysis, T) => Result
  ): Result = {
    if (a.useDefSymbolSet.contains(symbol)) error(a, symbol)
    else if (!a.visitedSymbolSet.contains(symbol)) {
      val a1 = a.copy(useDefSymbolSet = a.useDefSymbolSet + symbol)
      for (_ <- visitor(a1, node))
      yield a.copy(visitedSymbolSet = a.visitedSymbolSet + symbol)
    }
    else Right(a)
  }

  private def visitUse[T](a: Analysis, node: AstNode[T], use: Name.Qualified): Result = {
    val symbol = a.useDefMap(node.getId)
    val m = UseDefMatching(node.getId, use, symbol)
    val a1 = a.copy(useDefMatchingList = m :: a.useDefMatchingList)
    visitDefPre(a1, symbol)
  }

}
