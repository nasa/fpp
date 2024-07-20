package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze state machine members */
trait StateMachineAnalyzer extends Analyzer {

  override def defStateMachineAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = {
    val (_, node, _) = aNode
    node.data match {
      case Ast.DefStateMachine(_, Some(members)) =>
        visitList(a, members, matchStateMachineMember)
      case _ => Right(a)
    }
  }

}
