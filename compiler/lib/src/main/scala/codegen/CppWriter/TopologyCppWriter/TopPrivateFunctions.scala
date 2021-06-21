package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology private functions */
case class TopPrivateFunctions(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getLines: List[Line] = {
    List(
      addBannerComment("Initialize components", getInitComponentsLines),
      addBannerComment("Configure components", getConfigComponentsLines),
      addBannerComment("Set component base IDs", getSetBaseIDsLines),
      addBannerComment("Connect components", getConnectComponentsLines),
      addBannerComment("Register commands", getRegisterCommandsLines),
      addBannerComment("Load parameters", getLoadParametersLines),
      addBannerComment("Start tasks", getStartTasksLines),
      addBannerComment("Stop tasks", getStopTasksLines),
      addBannerComment("Free threads", getFreeThreadsLines),
      addBannerComment("Tear down components", getTearDownComponentsLines),
    ).flatten
  }

  private def getInitComponentsLines: List[Line] = {
    // TODO
    Nil
  }

  private def getConfigComponentsLines: List[Line] = {
    // TODO
    Nil
  }

  private def getRegisterCommandsLines: List[Line] = {
    // TODO
    Nil
  }

  private def getSetBaseIDsLines: List[Line] = {
    // TODO
    Nil
  }

  private def getConnectComponentsLines: List[Line] = {
    // TODO
    Nil
  }

  private def getLoadParametersLines: List[Line] = {
    // TODO
    Nil
  }

  private def getStartTasksLines: List[Line] = {
    // TODO
    Nil
  }

  private def getStopTasksLines: List[Line] = {
    // TODO
    Nil
  }

  private def getFreeThreadsLines: List[Line] = {
    // TODO
    Nil
  }

  private def getTearDownComponentsLines: List[Line] = {
    // TODO
    Nil
  }

}
