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
      case Ast.DefStateMachine(name, Some(members)) =>
        val a1 = a.copy(scopeNameList = name :: a.scopeNameList)
        for { a2 <- visitList(a1, members, matchStateMachineMember) }
        yield a2.copy(scopeNameList = a.scopeNameList)
      case _ => Right(a)
    }
  }

}
