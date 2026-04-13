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

  private def writeInstanceSwitch(
    componentInstanceMap: TopComponents.ComponentInstanceMap,
    writeInnerCaseLines: (Int, Connection) => List[Line],
    innerDefaultLines: List[Line] = Nil
  ) = {
    val sortedList = componentInstanceMapToSortedList(componentInstanceMap)
    sortedList match {
      case head :: Nil =>
        // In the case of one instance, omit the instance switch
        val (_, portNumberMap) = head
        writePortNumSwitch (writeInnerCaseLines, innerDefaultLines, portNumberMap)
      case _ =>
        line("const auto instance = this->getInstance();") ::
        wrapInSwitch(
          "instance",
          List.concat(
            lines(
              """|default:
                 |#ifdef FW_STRICT_ASSERTIONS
                 |  FW_ASSERT(0, static_cast<FwAssertArgType>(instance));
                 |  break;
                 |#else
                 |  // Fall through
                 |#endif"""

            ),
            sortedList.flatMap(writeInstanceCase (writeInnerCaseLines) (innerDefaultLines)),
          )
        )
    }
  }

  private def writeIsConnectedFnBody(
    portName: Name.Unqualified,
    componentInstanceMap: TopComponents.ComponentInstanceMap
  ) = {
    val portInstance = component.portMap(portName)
    val numPorts = numPortsConstantName(portInstance)
    List.concat(
      writePortNumAssertion(numPorts),
      lines("bool result = false;"),
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
    val fromPortInstance = fromPort.portInstance
    val toPort = connection.to.port
    val toPortInstance = toPort.portInstance
    val toPortNum = topology.toPortNumberMap(connection)
    val functionName = {
      // Connections must be flattened at this point
      val _ @ InterfaceInstance.InterfaceComponentInstance(toComponentInstance) =
        toPort.interfaceInstance
      val componentInstanceName = CppWriter.writeQualifiedName(toComponentInstance.qualifiedName)
      val toPortName = toPortInstance.getUnqualifiedName
      val handlerBaseName = inputPortHandlerBaseName(toPortName)
      s"$componentInstanceName.$handlerBaseName"
    }
    (fromPortInstance.getType.get, toPortInstance.getType.get) match {
      case (
        PortInstance.Type.DefPort(fromPortSymbol),
        PortInstance.Type.Serial
      ) =>
        // Typed to serial connection
        writeTypedToSerialConnection(fromPortSymbol, functionName, toPortNum)
      case (
        PortInstance.Type.Serial,
        PortInstance.Type.DefPort(toPortSymbol)
      ) =>
        // Serial to typed connection
        writeSerialToTypedConnection(toPortSymbol, functionName, toPortNum)
      case _ =>
        // Typed to typed connection or serial to serial connection
        val returnType = getHandlerReturnTypeAsString(toPortInstance)
        val addResultPrefix = addConditionalPrefix (returnType != "void") ("_result =")
        writeFunctionCall(
          addResultPrefix(functionName),
          List(toPortNum.toString),
          getPortParams(fromPortInstance).map(_._1)
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

  private def writePortNumAssertion(numPorts: String) =
    lines(
      s"""|FW_ASSERT(
          |  (0 <= portNum) && (portNum < $numPorts),
          |  static_cast<FwAssertArgType>(portNum),
          |  static_cast<FwAssertArgType>($numPorts)
          |);"""
    )

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

  private def writeSerialToTypedConnection(
    toPortSymbol: Symbol.Port,
    functionName: String,
    toPortNum: Int
  ) = {
    val toPortParams = toPortSymbol.node._2.data.params
    val toPortName = s.getName(toPortSymbol)
    val portSerializerName = PortCppWriterUtils.getPortSerializerName(toPortName)
    val serializerParamNames = PortCppWriterUtils.getSerializerParamNames(toPortParams)
    wrapInBlock(
      List.concat(
        if (!toPortParams.isEmpty)
        then lines(
          s"""|$portSerializerName _serializer;
              |Fw::SerializeStatus _status = _serializer.deserializePortArgs($serialPortParamName);
              |FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));"""
        )
        else lines(s"|(void) $serialPortParamName;"),
        writeFunctionCall(
          functionName,
          List(toPortNum.toString),
          serializerParamNames
        )
      )
    )
  }

  private def writeTypedToSerialConnection(
    fromPortSymbol: Symbol.Port,
    functionName: String,
    toPortNum: Int
  ) = {
    val params = fromPortSymbol.node._2.data.params
    val portName = s.getName(fromPortSymbol)
    val portBufferName = PortCppWriterUtils.getPortBufferName(portName)
    val portSerializerName = PortCppWriterUtils.getPortSerializerName(portName)
    val portParamNames = PortCppWriterUtils.writeParamNames(params)
    wrapInBlock(
      List.concat(
        lines(s"$portBufferName _buffer;"),
        guardedList (!params.isEmpty) (
          lines(
            s"""|Fw::SerializeStatus _status = $portSerializerName::serializePortArgs($portParamNames, _buffer);
                |FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));"""
          )
        ),
        writeFunctionCall(
          functionName,
          List(toPortNum.toString),
          List("_buffer")
        )
      )
    )
  }

}
