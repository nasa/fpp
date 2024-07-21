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

  override def defStateMachineAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = {
    val (_, node, _) = aNode
    node.data match {
      case Ast.DefStateMachine(_, Some(members)) =>
        visitList(sma, members, matchStateMachineMember)
      case _ => Right(sma)
    }
  }

}
