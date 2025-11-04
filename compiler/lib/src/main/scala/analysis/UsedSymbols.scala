package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/**
 *  Compute used symbols
 *
 *  There are two forms of resolution:
 *
 *  1. Shallow resolution (don't follow uses from uses). This is used to
 *     generate header files. You get this by calling a visitor method
 *     of UsedSymbols.
 *
 *  2. Deep resolution (follow uses from uses). This is used to generate
 *     dictionary symbols. You get this by calling UsedSymbols.resolveUses.
 */
object UsedSymbols extends UseAnalyzer {

  // When resolving uses, if the default value of an enum definition is an enum
  // constant, then don't visit it. In this case the symbol is ignored (for
  // shallow resolution) or converted to a use of this enum (for deep resolution).
  // So it adds nothing to the resolution.
  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.data
    for
      a <- opt(typeNameNode)(a, data.typeName)
      a <- visitList(a, data.constants, defEnumConstantAnnotatedNode)
      a <- node._2.data.default match
        case Some(en) => a.useDefMap(en.id) match
          case _: Symbol.EnumConstant => Right(a)
          case _ => opt(exprNode)(a, data.default)
        case None => Right(a)
    yield a
  }

  override def componentUse(
    a: Analysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ) = addSymbol(a, node)

  override def stateMachineUse(
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

  override def interfaceUse(
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
    // We could convert enum constant symbols to enum symbols here.
    // This would convert E.A to a use of E in shallow resolution.
    // Currently we don't do this, because we convert E.A to a numeric
    // constant in the generated code, so we don't need the dependency
    // on E.
    val symbol = a.useDefMap(node.id)
    Right(a.copy(usedSymbolSet = a.usedSymbolSet + symbol))
  }

  /** Deep resolution of used symbols
   *  Replaces uses of enum constants with uses of the corresponding enums */
  def resolveUses(a: Analysis, ss: Set[Symbol]): Set[Symbol] = {
    // When resolving uses, convert an enum constant symbol to the corresponding
    // enum symbol. For example, the use E.A becomes a use of E. This is what
    // we want, because E provides the definition of E.A.
    def resolveEnumConstant(s: Symbol) =
      s match
        case Symbol.EnumConstant(node) =>
          val t @ Type.Enum(enumNode, _, _) = a.typeMap(node._2.id)
          Symbol.Enum(enumNode)
        case _ => s
    // Helper function for recursive resolution
    val a1: Analysis = a.copy(usedSymbolSet = Set())
    def resolveNode(s: Symbol): Set[Symbol] = {
      val Right(a2) = s match {
        case Symbol.AbsType(node) => defAbsTypeAnnotatedNode(a1, node)
        case Symbol.AliasType(node) => defAliasTypeAnnotatedNode(a1, node)
        case Symbol.Array(node) => defArrayAnnotatedNode(a1, node)
        case Symbol.Component(node) => defComponentAnnotatedNode(a1, node)
        case Symbol.ComponentInstance(node) => defComponentInstanceAnnotatedNode(a1, node)
        case Symbol.Constant(node) => defConstantAnnotatedNode(a1, node)
        case Symbol.Enum(node) => defEnumAnnotatedNode(a1, node)
        case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a1, node)
        case Symbol.Interface(node) => defInterfaceAnnotatedNode(a1, node)
        case Symbol.Module(node) => defModuleAnnotatedNode(a1, node)
        case Symbol.Port(node) => defPortAnnotatedNode(a1, node)
        case Symbol.StateMachine(node) => defStateMachineAnnotatedNode(a1, node)
        case Symbol.Struct(node) => defStructAnnotatedNode(a1, node)
        case Symbol.Topology(node) => defTopologyAnnotatedNode(a1, node)
      }
      a2.usedSymbolSet.flatMap(resolveNode) + resolveEnumConstant(s)
    }
    ss.flatMap(resolveNode)
  }

}
