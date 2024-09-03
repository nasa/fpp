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
    val initialMap = Map[StateMachineSymbol, AstNode[Ast.SpecStateTransition]]()
    for {
      _ <- Result.foldLeft (aNode._2.data.members) (initialMap) (
        (map, member) => member.node._2 match {
          case Ast.StateMember.SpecStateTransition(node) =>
            val sym = sma.useDefMap(node.data.signal.id)
            map.get(sym) match {
              case Some(prevNode) => Left(
                SemanticError.StateMachine.DuplicateSignal(
                  sym.getUnqualifiedName,
                  aNode._2.data.name,
                  Locations.get(node.data.signal.id),
                  Locations.get(prevNode.data.signal.id)
                )
              )
              case None => Right(map + (sym -> node))
            }
          case _ => Right(map)
        }
      )
    }
    yield sma
  }

}
