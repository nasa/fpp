package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology public functions */
case class TopPublicFunctions(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getMembers: List[CppDoc.Member] = List(
    getBannerComment,
    getSetupFn,
    getTeardownFn
  )

  private val params = List(
    CppDoc.Function.Param(
      CppDoc.Type("const TopologyState&"),
      "state",
      Some("The topology state")
    )
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
      params,
      CppDoc.Type("void"),
      List(
        List(
          line("initComponents(state);"),
          line("configComponents(state);"),
          line("setBaseIds();"),
          line("connectComponents();"),
        ),
        commandInstances.size match {
          case 0 => Nil
          case _ => lines("regCommands();")
        },
        paramInstances.size match {
          case 0 => Nil
          case _ => lines("loadParameters();")
        },
        activeInstances.size match {
          case 0 => Nil
          case _ => lines("startTasks(state);")
        }
      ).flatten
    )
  )

  private def getTeardownFn = CppDoc.Member.Function(
    CppDoc.Function(
      Some("Tear down the topology"),
      "teardown",
      params,
      CppDoc.Type("void"),
      List(
        activeInstances.size match {
          case 0 => Nil
          case _ => List(
            line("stopTasks(state);"),
            line("freeThreads(state);"),
          )
        },
        lines("tearDownComponents(state);"),
      ).flatten
    )
  )

}
