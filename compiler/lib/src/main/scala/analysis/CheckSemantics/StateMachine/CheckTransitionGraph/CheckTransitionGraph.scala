package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute and check the transition graph */
object CheckTransitionGraph {

  def defStateMachineAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ): Result.Result[StateMachineAnalysis] = {
    for {
      sma <- Right(sma)
      // TODO: Construct transition graph
      // TODO: Check transition graph for reachability
      // TODO: Check for illegal cycles in the transition graph
      // TODO: Construct the reverse transition graph
    }
    yield sma
  }

}
