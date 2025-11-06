package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out topology C++ for component definitions */
case class TopComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]],
  portNameMap: TopComponents.PortNameMap
) extends ComponentCppWriterUtils(s, aNode) {

  private val sortedPortNameList = portNameMap.toList.sortWith(_._1 < _._1)

  def writeIsConnectedFns =
    sortedPortNameList.flatMap(writeIsConnectedFnForPort)

  def writeInvocationFns =
    sortedPortNameList.flatMap(writeInvocationFnForPort)

  private def writeIsConnectedFnForPort(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val shortName = outputPortIsConnectedName(portName)
    val name = s"$componentClassName::$shortName"
    lines(
      s"""|
          |// TODO: $name"""
    )
  }

  private def writeInvocationFnForPort(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val shortName = outputPortInvokerName(portName)
    val name = s"$componentClassName::$shortName"
    lines(
      s"""|
          |// TODO: $name"""
    )
  }

}
