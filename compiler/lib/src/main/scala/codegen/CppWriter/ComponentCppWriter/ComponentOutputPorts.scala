package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component output port instances */
case class ComponentOutputPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private def generateConnector(
    p: PortInstance,
    connectorName: String,
    numGetterName: String,
    variableName: String
  ) = {
    val connectionFunction = p.getType.get match {
      case PortInstance.Type.DefPort(_) => "addCallPort"
      case PortInstance.Type.Serial => "registerSerialPort"
    }
    val portName = p.getUnqualifiedName
    val typeName = getQualifiedPortTypeName(p, PortInstance.Direction.Input)

    List(
      functionClassMember(
        Some(s"Connect port to $portName[portNum]"),
        connectorName,
        List(
          portNumParam,
          CppDoc.Function.Param(
            p.getType.get match {
              case PortInstance.Type.DefPort(_) =>
                CppDoc.Type(s"$typeName*")
              case PortInstance.Type.Serial =>
                CppDoc.Type("Fw::InputSerializePort*")
            },
            "port",
            Some("The input port")
          )
        ),
        CppDoc.Type("void"),
        lines(
          s"""|FW_ASSERT(
              |  (0 <= portNum) && (portNum < this->$numGetterName()),
              |  static_cast<FwAssertArgType>(portNum)
              |);
              |
              |this->$variableName[portNum].$connectionFunction(port);
              |"""
        )
      )
    )
  }

  /** Generates connectors for a list of ports.
   *  Ports may be typed or serial. */
  def generateConnectors(
    ports: List[PortInstance],
    comment: String,
    getConnectorName: String => String,
    getNumGetterName: PortInstance => String,
    getVariableName: PortInstance => String,
  ): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "public",
      comment,
      mapPorts(
        ports,
        p => generateConnector(
          p,
          getConnectorName(p.getUnqualifiedName),
          getNumGetterName(p),
          getVariableName(p)
        )
      )
    )
  }

  /** Get connectors for a list of typed ports */
  def getTypedConnectors(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val typeStr = getPortListTypeString(ports)
    val comment =
      if (typeStr == "special")
        s"Connect input ports to $typeStr output ports"
      else
        s"Connect $typeStr input ports to $typeStr output ports"
    wrapClassMembersInIfDirective(
      "#if !FW_DIRECT_PORT_CALLS",
      generateConnectors(
        ports,
        comment,
        outputPortConnectorName,
        portNumGetterName,
        portVariableName
      )
    )
  }

  /** Get connectors for a list of serial (untyped) ports */
  def getSerialConnectors(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val typeStr = getPortListTypeString(ports)

    wrapClassMembersInIfDirective(
      "#if !FW_DIRECT_PORT_CALLS && FW_PORT_SERIALIZATION",
      addAccessTagAndComment(
        "public",
        s"Connect serial input ports to $typeStr output ports",
        mapPorts(ports, p =>
          getPortReturnType(p) match {
            case None => List(
              functionClassMember(
                Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
                outputPortConnectorName(p.getUnqualifiedName),
                List(
                  portNumParam,
                  CppDoc.Function.Param(
                    p.getType.get match {
                      case PortInstance.Type.DefPort(_) =>
                        CppDoc.Type("Fw::InputSerializePort*")
                      case PortInstance.Type.Serial =>
                        CppDoc.Type("Fw::InputPortBase*")
                    },
                    "port",
                    Some("The port")
                  )
                ),
                CppDoc.Type("void"),
                lines(
                  s"""|FW_ASSERT(
                      |  (0 <= portNum) && (portNum < this->${portNumGetterName(p)}()),
                      |  static_cast<FwAssertArgType>(portNum)
                      |);
                      |
                      |this->${portVariableName(p)}[portNum].registerSerialPort(port);
                      |"""
                ),
              )
            )
            case Some(_) => Nil
          }
        )
      )
    )
  }

  def getInvokers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    def getInvokerForPortInstance(p: PortInstance) = {
      val invokeFunction = p.getType.get match {
        case PortInstance.Type.DefPort(_) => "invoke"
        case PortInstance.Type.Serial => "invokeSerial"
      }

      functionClassMember(
        Some(s"Invoke output port ${p.getUnqualifiedName}"),
        outputPortInvokerName(p),
        portNumParam :: getPortFunctionParams(p),
        getReturnType(p),
        List.concat(
          lines(
            s"""|FW_ASSERT(
                |  (0 <= portNum) && (portNum < this->${portNumGetterName(p)}()),
                |  static_cast<FwAssertArgType>(portNum)
                |);
                |
                |FW_ASSERT(
                |  this->${portVariableName(p)}[portNum].isConnected(),
                |  static_cast<FwAssertArgType>(portNum)
                |);
                |"""
          ),
          writeFunctionCall(
            addReturnKeyword(
              s"this->${portVariableName(p)}[portNum].$invokeFunction",
              p
            ),
            Nil,
            getPortParams(p).map(_._1)
          )
        )
      )
    }
    wrapClassMembersInIfDirective(
      "#if !FW_DIRECT_PORT_CALLS",
      addAccessTagAndComment(
        "protected",
        s"Invocation functions for ${getPortListTypeString(ports)} output ports",
        ports.map(getInvokerForPortInstance)
      ),
      CppDoc.Lines.Both
    )
  }

  private def generateConnectionStatusQuery(
    portName: String,
    isConnectedName: String,
    numGetterName: String,
    variableName: String
  ) = functionClassMember(
    Some(
      s"""|Check whether port $portName is connected
          |
          |\\return Whether port $portName is connected
          |"""
    ),
    isConnectedName,
    List(portNumParam),
    CppDoc.Type("bool"),
    lines(
      s"""|FW_ASSERT(
          |  (0 <= portNum) && (portNum < this->$numGetterName()),
          |  static_cast<FwAssertArgType>(portNum)
          |);
          |
          |return this->$variableName[portNum].isConnected();
          |"""
    ),
    CppDoc.Function.NonSV,
    CppDoc.Function.Const
  )

  def generateConnectionStatusQueries(
    ports: List[PortInstance],
    portName: String => String,
    getIsConnectedName: String => String,
    getNumGetterName: PortInstance => String,
    getVariableName: PortInstance => String
  ): List[CppDoc.Class.Member] = mapPorts(
    ports,
    p => List(
      generateConnectionStatusQuery(
        portName(p.getUnqualifiedName),
        getIsConnectedName(p.getUnqualifiedName),
        getNumGetterName(p),
        getVariableName(p)
      )
    )
  )

  def getConnectionStatusQueries(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      s"Connection status queries for ${getPortListTypeString(ports)} output ports",
      wrapClassMembersInIfDirective(
        "#if !FW_DIRECT_PORT_CALLS",
        generateConnectionStatusQueries(
          ports,
          (s: String) => s,
          outputPortIsConnectedName,
          portNumGetterName,
          portVariableName
        ),
        CppDoc.Lines.Cpp
      )
    )
  }

  // Get a port return type as a CppDoc Type
  private def getReturnType(p: PortInstance): CppDoc.Type =
    p.getType.get match {
      case PortInstance.Type.DefPort(_) => getPortReturnTypeAsCppDocType(p)
      case PortInstance.Type.Serial => CppDoc.Type(
        "Fw::SerializeStatus"
      )
    }

  // Get the name for an output port connection status function
  private def outputPortIsConnectedName(name: String) =
    s"isConnected_${name}_OutputPort"

}
