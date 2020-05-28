package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute used symbols */
object UsedSymbols extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) =
    addSymbol(a, node)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) =
    addSymbol(a, node)

  private def addSymbol[T](a: Analysis, node: AstNode[T]) = {
    val symbol = a.useDefMap(node.getId)
    Right(a.copy(usedSymbolSet = a.usedSymbolSet + symbol))
  }

}
