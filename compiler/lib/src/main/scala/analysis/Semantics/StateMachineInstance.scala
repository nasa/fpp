package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP state machine instance */
final case class StateMachineInstance(
  aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]],
  symbol: Symbol.StateMachine,
  priority: Option[BigInt],
  queueFull: Ast.QueueFull
) {

  /** Gets the location of the state machine instance*/
  def getLoc: Location = Locations.get(aNode._2.id)

  def getNodeId = aNode._2.id

  /** Gets the name of the state machine instance */
  def getName = aNode._2.data.name

}

object StateMachineInstance {

  /** Creates a state machine instance from a state machine instance specifier */
  def fromSpecStateMachine(a: Analysis, 
                           aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ) : Result.Result[StateMachineInstance] = {
    val data = aNode._2.data
    val qid = data.stateMachine
    val priorityNode = data.priority
    val priority = a.getBigIntValueOpt(priorityNode)
    val queueFull = Analysis.getQueueFull(data.queueFull)

    for {
      symbol <- a.useDefMap(qid.id) match {
        case symbol @ Symbol.StateMachine(_) => Right(symbol)
        case symbol => Left(SemanticError.InvalidSymbol(
          symbol.getUnqualifiedName,
          Locations.get(qid.id),
          "not a state machine symbol",
          symbol.getLoc
        ))
      }
    }
    yield StateMachineInstance(aNode, symbol, priority, queueFull)
  }

}
