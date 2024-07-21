package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check state machine definitions */
object CheckStateMachineDefs
  extends Analyzer
  with ModuleAnalyzer
  with ComponentAnalyzer
{

  override def defStateMachineAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = Right(a)
  // TODO
  //{
  //  CheckStateMachineSemantics.checkStateMachineSemantics(a, aNode)
  //}

}
