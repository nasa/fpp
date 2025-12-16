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

  def writeOutFns = {
    val nameList = sortedPortNameList.filter {
      case (n, _) => invokerRequired(component.portMap(n))
    }
    nameList.flatMap(writeOutFnForPort)
  }

  private def componentInstanceMapToSortedList(
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = componentInstanceMap.toList.sortWith {
    case ((ci1, _), (ci2, _)) =>
      ci1.qualifiedName.toString < ci2.qualifiedName.toString
  }

  private def writeInstanceSwitch(
    componentInstanceMap: TopComponents.ComponentInstanceMap,
    writeInnerCaseLines: (Int, Connection) => List[Line],
    innerDefaultLines: List[Line] = Nil
  ) = {
    val sortedList = componentInstanceMapToSortedList(componentInstanceMap)
    wrapInSwitch(
      "instance",
      List.concat(
        sortedList.flatMap(writeInstanceCase (writeInnerCaseLines) (innerDefaultLines)),
        lines(
          """|default:
             |  FW_ASSERT(0, static_cast<FwAssertArgType>(instance));
             |  break;"""
        )
      )
    )
  }

  private def writePortNumAssertion(numPorts: String) =
    lines(
      s"""|FW_ASSERT(
          |  (0 <= portNum) && (portNum < $numPorts),
          |  static_cast<FwAssertArgType>(portNum),
          |  static_cast<FwAssertArgType>($numPorts)
          |);"""
    )

  private def writeIsConnectedFnBody(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val numPorts = numPortsConstantName(portInstance)
    List.concat(
      writePortNumAssertion(numPorts),
      lines(
        """|bool result = false;
           |const auto instance = this->getInstance();"""
      ),
      writeInstanceSwitch(
        componentInstanceMap,
        { case _ => lines("result = true;") }
      ),
      lines("return result;")
    )
  }

  private def writeIsConnectedFnForPort(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val shortName = outputPortIsConnectedName(portName)
    val name = s"$componentClassName::$shortName"
    val prototype = s"bool $name(FwIndexType portNum) const"
    addGuardForPort(
      portInstance,
      Line.blank ::
      wrapInScope(
        s"$prototype {",
        writeIsConnectedFnBody(portName, componentInstanceMap),
        "}"
      )
    )
  }

  private def writeOutFnBody(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val numPorts = numPortsConstantName(portInstance)
    val returnType = getInvokerReturnTypeAsString(portInstance)
    val nonVoidReturn = returnType != "void"
    List.concat(
      writePortNumAssertion(numPorts),
      lines("const auto instance = this->getInstance();"),
      guardedList (nonVoidReturn) (lines(s"$returnType _result = {};")),
      writeInstanceSwitch(
        componentInstanceMap,
        writeOutFnCase,
        lines("FW_ASSERT(0, static_cast<FwAssertArgType>(portNum));")
      ),
      guardedList (nonVoidReturn) (lines(s"return _result;"))
    )
  }

  private def writeOutFnCase(fromPortNum: Int, connection: Connection) = {
    val fromPort = connection.from.port
    val fromPortInstance = connection.from.port.portInstance
    val toPort = connection.to.port
    val toPortInstance = connection.to.port.portInstance
    val toPortNum = topology.toPortNumberMap(connection)
    val componentInstanceName = CppWriter.writeQualifiedName(toPort.componentInstance.qualifiedName)
    val portName = toPortInstance.getUnqualifiedName
    val handlerBaseName = inputPortHandlerBaseName(portName)
    val fnName = s"$componentInstanceName.$handlerBaseName"
    val returnType = getInvokerReturnTypeAsString(toPortInstance)
    val addResultPrefix = addConditionalPrefix (returnType != "void") ("_result =")
    (fromPortInstance.getType.get, toPortInstance.getType.get) match {
      case (_: PortInstance.Type.DefPort, _: PortInstance.Type.DefPort) =>
        writeFunctionCall(
          addResultPrefix(fnName),
          List(toPortNum.toString),
          getPortParams(fromPort.portInstance).map(_._1)
        )
      case (_: PortInstance.Type.DefPort, _) =>
        lines(
          """|// TODO: Typed to serial connection
             |FW_ASSERT(0);"""
        )
      case (_, _: PortInstance.Type.DefPort) =>
        lines(
          """|// TODO: Serial to typed connection
             |FW_ASSERT(0);"""
        )
      case _ =>
        lines(
          """|// TODO: Serial to serial connection
             |FW_ASSERT(0);"""
        )
    }
  }

  private def writeOutFnForPort(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val returnType = getInvokerReturnTypeAsString(portInstance)
    val shortName = outputPortInvokerName(portName)
    val name = s"$componentClassName::$shortName"
    val prototypeLines = {
      val ll = CppDocCppWriter.writeParams(
        s"$returnType $name",
        portNumParam :: getPortFunctionParams(portInstance)
      )
      portInstance.getType.get match {
        case PortInstance.Type.DefPort(_) => Line.addSuffix(ll, " const")
        case PortInstance.Type.Serial => ll
      }
    }
    addGuardForPort(
      portInstance,
      List.concat(
        Line.blank ::
        Line.addSuffix(prototypeLines, " {"),
        writeOutFnBody(portName, componentInstanceMap).map(indentIn),
        lines("}")
      )
    )
  }

  private def writeInstanceCase
    (writeInnerCaseLines: (Int, Connection) => List[Line])
    (innerDefaultLines: List[Line])
    (pair: (ComponentInstance, TopComponents.PortNumberMap)) =
  {
    val (componentInstance, portNumberMap) = pair
    val ident = CppWriterState.identFromQualifiedName(componentInstance.qualifiedName)
    val instanceIds = s"${topologyQualifierPrefix}InstanceIds"
    List.concat(
      line(s"case $instanceIds::$ident:") ::
      (writePortNumSwitch (writeInnerCaseLines, innerDefaultLines, portNumberMap)).map(indentIn),
      lines("  break;")
    )
  }

  private def writePortNumSwitch(
    writeCaseLines: (Int, Connection) => List[Line],
    defaultLines: List[Line],
    portNumberMap: TopComponents.PortNumberMap
  ) = {
    val portNumberList = portNumberMap.toList.sortWith(_._1 < _._1)
    wrapInSwitch(
      "portNum",
      List.concat(
        portNumberList.flatMap ((n, c) =>
          List.concat(
            line(s"case $n:") ::
            writeCaseLines(n, c).map(indentIn),
            lines("  break;")
          )
        ),
        lines("default:"),
        defaultLines.map(indentIn),
        lines("  break;")
      )
    )
  }

}
