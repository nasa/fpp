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
      // TODO: Check state machine uses
      // TODO: Check initial transitions
      // TODO: Check transition graph
    }
    yield sma
  }

}
