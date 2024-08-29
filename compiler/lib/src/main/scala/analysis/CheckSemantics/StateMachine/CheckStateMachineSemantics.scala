package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check semantics for a state machine definition */
object CheckStateMachineSemantics {

  def defStateMachineAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ): Result.Result[StateMachineAnalysis] = {
    for {
      sma <- EnterStateMachineSymbols.defStateMachineAnnotatedNode(sma, aNode)
      sma <- CheckStateMachineUses.defStateMachineAnnotatedNode(sma, aNode)
      sma <- CheckInitialTransitions.defStateMachineAnnotatedNode(sma, aNode)
      // TODO: Check signal uses
      // TODO: Check transition graph
      // TODO: Check typed elements
      // TODO: Compute flattened state transition map
      // TODO: Compute flattened junction transition map
    }
    yield sma
  }

}
