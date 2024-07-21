package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze state definition members */
trait StateAnalyzer extends StateMachineAnalysisVisitor {

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val (_, node, _) = aNode
    node.data match {
      case Ast.DefState(name, Some(members)) => {
        val sma1 = sma.copy(scopeNameList = name :: sma.scopeNameList)
        for { sma2 <- visitList(sma1, members, matchStateMember) }
        yield sma2.copy(scopeNameList = sma.scopeNameList)
      }
      case _ => Right(sma)
    }
  }

}
