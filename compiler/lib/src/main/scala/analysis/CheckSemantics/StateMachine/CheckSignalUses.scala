package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check signal uses */
object CheckSignalUses
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val initialMap = Map[StateMachineSymbol, Ast.SpecStateTransition]()
    for {
      _ <- Result.foldLeft (aNode._2.data.members) (initialMap) (
        (map, member) => member.node._2 match {
          case Ast.StateMember.SpecStateTransition(node) =>
            val st = node.data
            val sym = sma.useDefMap(st.signal.id)
            map.get(sym) match {
              case Some(prevSt) => Left(
                SemanticError.StateMachine.DuplicateSignal(
                  sym.getUnqualifiedName,
                  aNode._2.data.name,
                  Locations.get(st.signal.id),
                  Locations.get(prevSt.signal.id)
                )
              )
              case None => Right(map + (sym -> st))
            }
          case _ => Right(map)
        }
      )
      // Visit members
      _ <- super.defStateAnnotatedNode(sma, aNode)
    }
    yield sma
  }

}
