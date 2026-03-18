package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze state machine members */
trait StateMachineAnalyzer extends Analyzer {

  override def defStateMachineAnnotatedNodeInternal(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = {
    val name = aNode._2.data.name
    val a1 = a.copy(scopeNameList = name :: a.scopeNameList)
    for { a2 <- visitList(a1, members, matchStateMachineMember) }
    yield a2.copy(scopeNameList = a.scopeNameList)
  }

}
