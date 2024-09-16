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
  /** The current parent state */
  parentState: Option[StateMachineSymbol.State] = None,
  /** The mapping from symbols to their parent symbols */
  parentStateMap: Map[StateMachineSymbol,StateMachineSymbol.State] = Map(),
  /** The mapping from symbols with scopes to their scopes */
  symbolScopeMap: Map[StateMachineSymbol,StateMachineScope] = Map(),
  /** The mapping from uses (by node ID) to their definitions */
  useDefMap: Map[AstNode.Id, StateMachineSymbol] = Map(),
  /** The transition graph */
  transitionGraph: TransitionGraph = TransitionGraph(),
  /** The reverse transition graph */
  reverseTransitionGraph: TransitionGraph = TransitionGraph(),
  /** Map from typed elements to optional types */
  typeOptionMap: Map[StateMachineTypedElement, Option[Type]] = Map(),
  /** The current signal-transition map */
  signalTransitionMap: StateMachineAnalysis.SignalTransitionMap = Map(),
  /** The flattened state transtiion map */
  flattenedStateTransitionMap: StateMachineAnalysis.FlattenedStateTransitionMap = Map(),
  /** The flattened junction transtiion map */
  flattenedJunctionTransitionMap: StateMachineAnalysis.FlattenedJunctionTransitionMap = Map()
) {

  /** Gets the list of parent states, highest first */
  def getParentStateList(s: StateMachineSymbol): List[StateMachineSymbol.State] = {
    def helper(
      s: StateMachineSymbol,
      result: List[StateMachineSymbol.State]
    ): List[StateMachineSymbol.State] =
      parentStateMap.get(s) match {
        case Some(state) => helper(state, state :: result)
        case None => result
      }
    helper(s, Nil)
  }

  /** Gets the qualified name of a symbol */
  val getQualifiedName = Analysis.getQualifiedNameFromMap (parentStateMap)

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
    teKind: String,
    teTo: Option[Type],
    siteKind: String,
    siteTo: Option[Type]
  ): Result.Result[Option[Type]] =
    if TypeOption.isConvertibleTo(teTo, siteTo)
    then Right(siteTo)
    else Left(
      SemanticError.StateMachine.CallSiteTypeMismatch(
        loc,
        teKind,
        TypeOption.show(teTo),
        siteKind,
        TypeOption.show(siteTo)
      )
    )

  // Get an action symbol from an identifier node
  def getActionSymbol(action: AstNode[Ast.Ident]):
  StateMachineSymbol.Action = {
    val sym = useDefMap(action.id)
    val actionSym @ StateMachineSymbol.Action(_) = sym
    actionSym
  }

  // Get a guard symbol from an identifier node
  def getGuardSymbol(guard: AstNode[Ast.Ident]):
  StateMachineSymbol.Guard = {
    val sym = useDefMap(guard.id)
    val guardSym @ StateMachineSymbol.Guard(_) = sym
    guardSym
  }

  // Get a signal symbol from an identifier node
  def getSignalSymbol(signal: AstNode[Ast.Ident]):
  StateMachineSymbol.Signal = {
    val sym = useDefMap(signal.id)
    val signalSym @ StateMachineSymbol.Signal(_) = sym
    signalSym
  }

  // Get a state or junction from a qualified identifier node
  def getStateOrJunction(soj: AstNode[Ast.QualIdent]):
  StateOrJunction =
    useDefMap(soj.id) match {
      case state: StateMachineSymbol.State => StateOrJunction.State(state)
      case junction: StateMachineSymbol.Junction => StateOrJunction.Junction(junction)
      case _ => throw new InternalError("expected state or junction")
    }

}

object StateMachineAnalysis {

  /** A signal-transition map */
  type SignalTransitionMap =
    Map[StateMachineSymbol.Signal, Transition.Guarded]

  /** A flattened state transition map */
  type FlattenedStateTransitionMap =
    Map[StateMachineSymbol.State, StateMachineAnalysis.SignalTransitionMap]

  /** A flattened junction transition map */
  type FlattenedJunctionTransitionMap =
    Map[AstNode[Ast.TransitionExpr], Transition]

}
