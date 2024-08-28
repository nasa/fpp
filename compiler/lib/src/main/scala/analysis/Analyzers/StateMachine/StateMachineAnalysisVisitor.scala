package fpp.compiler.analysis

import fpp.compiler.ast._

/** A generic analysis visitor for state machine semantics */
trait StateMachineAnalysisVisitor extends AstStateVisitor {

  type State = StateMachineAnalysis

  /** Apply an analysis to an option value */
  final def opt[T] (f: (StateMachineAnalysis, T) => Result) (sma: StateMachineAnalysis, o: Option[T]): Result =
    o.map(x => f(sma, x)).getOrElse(Right(sma))

  override def defStateMachineAnnotatedNodeInternal(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = visitList(sma, members, matchStateMachineMember)

}
