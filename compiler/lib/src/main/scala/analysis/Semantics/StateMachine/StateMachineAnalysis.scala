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
  /** The parent state */
  parentState: Option[StateMachineSymbol.State] = None,
  /** The transition graph */
  transitionGraph: TransitionGraph = TransitionGraph(),
  /** The reverse transition graph */
  reverseTransitionGraph: TransitionGraph = TransitionGraph(),
  /** Map from typed elements to optional types */
  typeOptionMap: Map[StateMachineTypedElement, Option[Type]] = Map()
  // TODO
) {

  /** Gets the qualified name of a symbol */
  val getQualifiedName = Analysis.getQualifiedNameFromMap (parentSymbolMap)

  /** Gets the common type of two typed elements at a junction */
  def commonTypeAtJunction(
    te: StateMachineTypedElement.Junction,
    te1: StateMachineTypedElement,
    to1: Option[Type],
    te2: StateMachineTypedElement
  ): Result.Result[Option[Type]] = {
    val to2 = typeOptionMap(te2)
    TypeOption.commonType(to1, to2) match {
      case Some(to) => Right(to)
      case None => Left(
        SemanticError.StateMachine.JunctionTypeMismatch(
          Locations.get(te.getNodeId),
          Locations.get(te1.getNodeId),
          TypeOption.show(to1),
          Locations.get(te2.getNodeId),
          TypeOption.show(to2)
        )
      )
    }
  }

  /** Convert one type option to another at a call site */
  def convertTypeOptionsAtCallSite(
    loc: Location,
    teName: String,
    teTo: Option[Type],
    siteName: String,
    siteTo: Option[Type]
  ): Result.Result[Option[Type]] =
    if TypeOption.isConvertibleTo(teTo, siteTo)
    then Right(siteTo)
    else Left(
      SemanticError.StateMachine.CallSiteTypeMismatch(
        loc,
        teName,
        TypeOption.show(teTo),
        siteName,
        TypeOption.show(siteTo)
      )
    )

}
