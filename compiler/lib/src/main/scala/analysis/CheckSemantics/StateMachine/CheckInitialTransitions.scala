package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check initial transitions */
object CheckInitialTransitions
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  override def defStateMachineAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = {
    // TODO
    super.defStateMachineAnnotatedNode(sma, aNode)
  }

}
