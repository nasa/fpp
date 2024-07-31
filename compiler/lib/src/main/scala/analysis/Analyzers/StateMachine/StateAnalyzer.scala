package fpp.compiler.analysis

import fpp.compiler.ast._

/** Analyze state definition members */
trait StateAnalyzer extends StateMachineAnalysisVisitor {

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val sma1 = sma.copy(scopeNameList = data.name :: sma.scopeNameList)
    for { sma2 <- visitList(sma1, data.members, matchStateMember) }
      yield sma2.copy(scopeNameList = sma.scopeNameList)
  }

}
