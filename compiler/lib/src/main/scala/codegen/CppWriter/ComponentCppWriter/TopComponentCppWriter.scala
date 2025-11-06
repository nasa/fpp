package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out topology C++ for component definitions */
case class TopComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]],
  topology: Topology,
  portNameMap: TopComponents.PortNameMap
) extends ComponentCppWriterUtils(s, aNode) {

  private val sortedPortNameList = portNameMap.toList.sortWith(_._1 < _._1)

  private val topologyQualifiedName = s.a.getQualifiedName(Symbol.Topology(topology.aNode))

  private val topologyQualifier = topologyQualifiedName.qualifier

  private val topologyQualifierAsIdent = CppWriterState.identFromQualifier(topologyQualifier)
  
  private val topologyQualifierPrefix = topologyQualifier match {
    case Nil => ""
    case _ => s"::$topologyQualifierAsIdent::"
  }

  def writeIsConnectedFns =
    sortedPortNameList.flatMap(writeIsConnectedFnForPort)

  def writeOutFns =
    sortedPortNameList.flatMap(writeOutFnForPort)


  private def componentInstanceMapToSortedList(
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = componentInstanceMap.toList.sortWith {
    case ((ci1, _), (ci2, _)) =>
      ci1.qualifiedName.toString < ci2.qualifiedName.toString
  }

  private def writeIsConnectedCase(
    componentInstance: ComponentInstance,
    portNumberMap: TopComponents.PortNumberMap
  ) = {
    val ident = CppWriterState.identFromQualifiedName(componentInstance.qualifiedName)
    val instanceIds = s"${topologyQualifierPrefix}InstanceIds"
    List.concat(
      line(s"case $instanceIds::$ident:") ::
      writeIsConnectedPortNumCase(portNumberMap).map(indentIn),
      lines("  break;")
    )
  }

  private def writeIsConnectedFnForPort(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val shortName = outputPortIsConnectedName(portName)
    val name = s"$componentClassName::$shortName"
    val prototype = s"bool $name(FwIndexType portNum) const"
    Line.blank ::
    wrapInScope(
      s"$prototype {",
      writeIsConnectedFnBody(portName, componentInstanceMap),
      "}"
    )
  }

  private def writePortNumCase
    (f: Int => List[Line])
    (portNumberMap: TopComponents.PortNumberMap) =
  {
    val portNumberList = portNumberMap.keys.toList.sorted
    wrapInSwitch(
      "portNum",
      List.concat(
        portNumberList.flatMap (n =>
          List.concat(
            line(s"case $n") ::
            f(n).map(indentIn),
            lines("  break")
          )
        ),
        lines(
          """|default:
             |  break;"""
        )
      )
    )
  }

//  private def writeIsConnectedPortNumCase(
//    portNumberMap: TopComponents.PortNumberMap
//  ) = {
//    val portNumberList = portNumberMap.keys.toList.sorted
//    wrapInSwitch(
//      "portNum",
//      List.concat(
//        portNumberList.flatMap (n => {
//          lines(
//            s"""|case $n:
//                |  result = true;
//                |  break;"""
//          )
//        }),
//        lines(
//          """|default:
//             |  break;"""
//        )
//      )
//    )
//  }

  private val writeIsConnectedPortNumCase =
    writePortNumCase (_ => lines("result = true;"))


  private def writeIsConnectedFnBody(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val numPorts = numPortsConstantName(portInstance)
    val sortedList = componentInstanceMapToSortedList(componentInstanceMap)
    List.concat(
      lines(
      s"""|FW_ASSERT((0 <= portNum) && (portNum < $numPorts), static_cast<FwAssertArgType>(portNum));
          |bool result = false;
          |const auto instance = this->getInstance();"""
      ),
      wrapInSwitch(
        "instance",
        List.concat(
          sortedList.flatMap(writeIsConnectedCase),
          lines(
            """|default:
               |  FW_ASSERT(0, static_cast<FwAssertArgType>(instance));
               |  break;"""
          )
        )
      ),
      lines("return result;")
    )
  }

  private def writeOutFnBody(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val numPorts = numPortsConstantName(portInstance)
    val sortedList = componentInstanceMapToSortedList(componentInstanceMap)
    List.concat(
      lines(
      s"""|FW_ASSERT((0 <= portNum) && (portNum < $numPorts), static_cast<FwAssertArgType>(portNum));
          |const auto instance = this->getInstance();"""
      ),
      lines("// TODO")
    )
  }

  private def writeOutFnForPort(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val returnType = getInvokerReturnType(portInstance).getCppType
    val shortName = outputPortInvokerName(portName)
    val name = s"$componentClassName::$shortName"
    val prototypeLines = {
      val ll = CppDocCppWriter.writeParams(
        s"$returnType $name",
        portNumParam :: getPortFunctionParams(portInstance)
      )
      Line.addSuffix(ll, " const")
    }
    List.concat(
      Line.blank ::
      Line.addSuffix(prototypeLines, " {"),
      writeOutFnBody(portName, componentInstanceMap).map(indentIn),
      lines("}")
    )
  }

}
