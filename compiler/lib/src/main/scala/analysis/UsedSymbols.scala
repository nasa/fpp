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

  def symbol(s: Symbol): Set[Symbol] = {
    val a1 = Analysis()
    val Right(a) = s match {
      case Symbol.AbsType(node) => defAbsTypeAnnotatedNode(a1, node)
      case Symbol.Array(node) => defArrayAnnotatedNode(a1, node)
      case Symbol.Component(node) => defComponentAnnotatedNode(a1, node)
      case Symbol.ComponentInstance(node) => defComponentInstanceAnnotatedNode(a1, node)
      case Symbol.Constant(node) => defConstantAnnotatedNode(a1, node)
      case Symbol.Enum(node) => defEnumAnnotatedNode(a1, node)
      case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a1, node)
      case Symbol.Module(node) => defModuleAnnotatedNode(a1, node)
      case Symbol.Port(node) => defPortAnnotatedNode(a1, node)
      case Symbol.Struct(node) => defStructAnnotatedNode(a1, node)
      case Symbol.Topology(node) => defTopologyAnnotatedNode(a1, node)
      case null => Right(a1)
    }
    val symbolSet = a.usedSymbolSet
    symbolSet ++ symbolSet.flatMap(symbol)
  }

}
