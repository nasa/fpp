package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology public functions */
case class TopPublicFunctions(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) {

  def getMembers: List[CppDoc.Member] = List(
    getBannerComment,
    getSetupFn,
    getTeardownFn
  )

  private def getBannerComment = CppDoc.Member.Lines(
    CppDoc.Lines(
      CppDocWriter.writeBannerComment("Public interface functions"),
      CppDoc.Lines.Both
    )
  )

  private def getSetupFn = CppDoc.Member.Function(
    CppDoc.Function(
      Some("Set up the topology"),
      "setup",
      Nil,
      CppDoc.Type("void"),
      Nil
    )
  )

  private def getTeardownFn = CppDoc.Member.Function(
    CppDoc.Function(
      Some("Tear down the topology"),
      "teardown",
      Nil,
      CppDoc.Type("void"),
      Nil
    )
  )

}
