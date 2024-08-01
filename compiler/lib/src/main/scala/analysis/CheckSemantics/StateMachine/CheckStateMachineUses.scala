package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Match uses to their definitions */
object CheckStateMachineUses extends StateMachineUseAnalyzer {

  val helpers = CheckUsesHelpers(
    (sma: StateMachineAnalysis) => sma.nestedScope,
    (sma: StateMachineAnalysis, ns: StateMachineNestedScope) =>
      sma.copy(nestedScope = ns),
    (sma: StateMachineAnalysis) => sma.symbolScopeMap,
    (sma: StateMachineAnalysis) => sma.useDefMap,
    (sma: StateMachineAnalysis, udm: Map[AstNode.Id, StateMachineSymbol]) =>
      sma.copy(useDefMap = udm)
  )

  override def actionUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = helpers.visitIdentNode (StateMachineNameGroup.Action) (sma, node)

  override def guardUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = helpers.visitIdentNode (StateMachineNameGroup.Guard) (sma, node)

  override def signalUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = helpers.visitIdentNode (StateMachineNameGroup.Signal) (sma, node)

  override def stateOrJunctionUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ): Result = helpers.visitQualIdentNode (StateMachineNameGroup.State) (sma, node)

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      sma <- {
        val symbol = StateMachineSymbol.State(aNode)
        val scope = sma.symbolScopeMap(symbol)
        val newNestedScope = sma.nestedScope.push(scope)
        val sma1 = sma.copy(nestedScope = newNestedScope)
        super.defStateAnnotatedNode(sma1, aNode)
      }
    } yield sma.copy(nestedScope = sma.nestedScope.pop)
  }

}
