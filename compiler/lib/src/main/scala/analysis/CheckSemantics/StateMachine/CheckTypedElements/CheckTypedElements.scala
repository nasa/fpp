package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check typed elements */
object CheckTypedElements {

  def defStateMachineAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ): Result.Result[StateMachineAnalysis] = {
    for {
      sma <- ComputeTypeOptionMap.defStateMachineAnnotatedNode(sma, aNode)
      _ <- CheckActionAndGuardTypes.defStateMachineAnnotatedNode(sma, aNode)
    }
    yield sma
  }

}
