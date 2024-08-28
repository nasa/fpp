package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** The state machine analysis data structure */
case class StateMachineAnalysis(
  /** The analysis so far */
  a: Analysis,
  /** The state machine symbol */
  symbol: Symbol.StateMachine,
  /** A list of unqualified names representing the enclosing scope names, **/
  scopeNameList: List[Name.Unqualified] = List(),
  /** The current state machine nested scope for symbol lookup */
  nestedScope: StateMachineNestedScope = StateMachineNestedScope.empty,
  /** The current parent symbol */
  parentSymbol: Option[StateMachineSymbol] = None,
  /** The mapping from symbols to their parent symbols */
  parentSymbolMap: Map[StateMachineSymbol,StateMachineSymbol] = Map(),
  /** The mapping from symbols with scopes to their scopes */
  symbolScopeMap: Map[StateMachineSymbol,StateMachineScope] = Map(),
  /** The mapping from uses (by node ID) to their definitions */
  useDefMap: Map[AstNode.Id, StateMachineSymbol] = Map(),
  /** The transition graph */
  // TODO
) {

  /** Gets the qualified name of a symbol */
  val getQualifiedName = Analysis.getQualifiedNameFromMap (parentSymbolMap)

}

