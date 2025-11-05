package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology-dependent component implementation */
case class TopComponents(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getMembers: List[CppDoc.Member] = 
    wrapMembersInIfDirective(
      "#ifdef FW_DIRECT_PORT_CALLS",
      addMemberComment(
        "Topology-dependent component implementation",
        getComponentMembers
      )
    )

  private def getComponentMembers = Nil

}
