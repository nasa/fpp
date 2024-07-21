package fpp.compiler.analysis

import fpp.compiler.ast._

/** A generic analysis visitor for state machine semantics */
trait StateMachineAnalysisVisitor extends AstStateVisitor {

  type State = StateMachineAnalysis

  /** Apply an analysis to an option value */
  final def opt[T] (f: (StateMachineAnalysis, T) => Result) (a: StateMachineAnalysis, o: Option[T]): Result =
    o match {
      case Some(x) => f(a, x)
      case None => Right(a)
    }

  override def transUnit(a: StateMachineAnalysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

}
