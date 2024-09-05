package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check semantics for a state machine definition */
object CheckStateMachineSemantics {

  def defStateMachineAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ): Result.Result[StateMachineAnalysis] =
    aNode._2.data.members match {
      case Some(_) =>
        for {
          sma <- EnterStateMachineSymbols.defStateMachineAnnotatedNode(sma, aNode)
          sma <- CheckStateMachineUses.defStateMachineAnnotatedNode(sma, aNode)
          sma <- CheckInitialTransitions.defStateMachineAnnotatedNode(sma, aNode)
          sma <- CheckSignalUses.defStateMachineAnnotatedNode(sma, aNode)
          sma <- CheckTransitionGraph.defStateMachineAnnotatedNode(sma, aNode)
          // TODO: Check typed elements
          // TODO: Compute flattened state transition map
          // TODO: Compute flattened junction transition map
        }
        yield sma
      case None => Right(sma)
    }

}
