package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** Compute used symbols */
object UsedSymbols extends UseAnalyzer {

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
    val symbol = a.useDefMap(node.id)
    Right(a.copy(usedSymbolSet = a.usedSymbolSet + symbol))
  }

  /** Resolves used symbols recursively
   *  Replaces uses of enum constants with uses of the corresponding enums */
  def resolveUses(a: Analysis, ss: Set[Symbol]): Set[Symbol] = {
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
      a2.usedSymbolSet.flatMap(resolveNode) + s
    }
    // When resolving uses, convert an enum constant use to a
    // use of the corresponding enum. For example, the use
    // E.A becomes a use of E. This is what we want, because
    // E provides the definition of E.A.
    def resolveEnumConstant(s: Symbol) =
      s match
        case Symbol.EnumConstant(node) =>
          val t @ Type.Enum(enumNode, _, _) = a.typeMap(node._2.id)
          Set(Symbol.Enum(enumNode))
        case _ => Set(s)
    // Iterate to a fixed point.
    // We can't do the resolution recursively, because there is a
    // cycle: the default value of E may contain a resolved use of E.
    def resolveSet(prev: Set[Symbol], input: Set[Symbol]): Set[Symbol] =
      if prev.size == input.size
      then input
      else resolveSet(input, input.flatMap(resolveNode).flatMap(resolveEnumConstant))
    resolveSet(Set(), ss)
  }

}
