package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP data product containter */
final case class StateMachineInstance(
  aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
) {

  /** Gets the location of the state machine */
  def getLoc: Location = Locations.get(aNode._2.id)

  /** Gets the unqualified name of the state machine instance */
  def getUnqualifiedName = aNode._2.data.name


}

object StateMachineInstance {

  /** Creates a state machine instance from a state machine instance specifier */
  def fromSpecStateMachine(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]):
    StateMachineInstance = {
      StateMachineInstance(aNode)
    }

}
