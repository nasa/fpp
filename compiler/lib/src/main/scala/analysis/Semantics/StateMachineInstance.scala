package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state machine instance */
final case class StateMachineInstance(
  aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]],
  specifier: Ast.SpecStateMachineInstance
) {

  /** Gets the location of the state machine instance*/
  def getLoc: Location = Locations.get(aNode._2.id)

  def getNodeId = aNode._2.id

  /** Gets the unqualified name of the state machine instance */
  def getUnqualifiedName = aNode._2.data.name

}

object StateMachineInstance {

  /** Creates a state machine instance from a state machine instance specifier */
  def fromSpecStateMachine(a: Analysis, 
                           aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ) :
    Result.Result[StateMachineInstance] = {
      val node = aNode._2
      val data = node.data
      data match {
          case specifier: Ast.SpecStateMachineInstance =>
              createStateMachineInstance(a, aNode, specifier)
        }
    }

  private def createStateMachineInstance(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]],
    specifier: Ast.SpecStateMachineInstance
  ): Result.Result[StateMachineInstance] = {
    val qid = specifier.stateMachine
    a.useDefMap(qid.id) match {
      case symbol @ Symbol.StateMachine(_) =>
        Right(StateMachineInstance(aNode, specifier))
      case symbol => Left(SemanticError.InvalidSymbol(
        symbol.getUnqualifiedName,
        Locations.get(qid.id),
        "not a state machine symbol",
        symbol.getLoc
      ))
    }
  }

}
