package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute used symbols */
object UsedSymbols extends UseAnalyzer {

  override def componentUse(
    a: Analysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ) = addSymbol(a, node)

  override def componentInstanceUse(
    a: Analysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ) = addSymbol(a, node)

  override def constantUse(
    a: Analysis,
    node: AstNode[Ast.Expr],
    use: Name.Qualified
  ) = addSymbol(a, node)

  override def topologyUse(
    a: Analysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ) = addSymbol(a, node)

  override def typeUse(
    a: Analysis,
    node: AstNode[Ast.TypeName],
    use: Name.Qualified
  ) = addSymbol(a, node)
  
  override def portUse(
    a: Analysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ) = addSymbol(a, node)

  private def addSymbol[T](a: Analysis, node: AstNode[T]) = {
    val symbol = a.useDefMap(node.id)
    Right(a.copy(usedSymbolSet = a.usedSymbolSet + symbol))
  }

}
