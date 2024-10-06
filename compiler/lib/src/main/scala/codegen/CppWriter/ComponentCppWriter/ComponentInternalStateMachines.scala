package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentInternalStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  /** Gets the function members */
  def getFunctionMembers: List[CppDoc.Class.Member] =
    // TODO
    Nil

  def getTypeMembers: List[CppDoc.Class.Member] =
    // TODO: Signal buffer
    // TODO: State machine implementations
    Nil

  /** Writes the dispatch case, if any, for internal state machine instances */
  def writeDispatchCase: List[Line] =
    // TODO
    Nil

  def getOverflowHooks: List[CppDoc.Class.Member] =
    // TODO
    Nil

}
