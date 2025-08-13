package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component output port instances */
case class ComponentOutputPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  /** Generates connectors for a list of ports.
   *  Ports may be typed or serial. */
  def generateConnectors(
    ports: List[PortInstance],
    comment: String,
    connectorName: String => String,
    numGetterName: PortInstance => String,
    variableName: PortInstance => String,
  ): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "public",
      comment,
      mapPorts(ports, p => {
        val connectionFunction = p.getType.get match {
          case PortInstance.Type.DefPort(_) => "addCallPort"
          case PortInstance.Type.Serial => "registerSerialPort"
        }

        List(
          functionClassMember(
            Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
            connectorName(p.getUnqualifiedName),
            List(
              portNumParam,
              CppDoc.Function.Param(
                p.getType.get match {
                  case PortInstance.Type.DefPort(_) =>
                    CppDoc.Type(s"${getQualifiedPortTypeName(p, PortInstance.Direction.Input)}*")
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
                  |  (0 <= portNum) && (portNum < this->${numGetterName(p)}()),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |this->${variableName(p)}[portNum].$connectionFunction(port);
                  |"""
            )
          )
        )
      })
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
    generateConnectors(
      ports,
      comment,
      outputPortConnectorName,
      portNumGetterName,
      portVariableName
    )
  }

  /** Get connectors for a list of serial (untyped) ports */
  def getSerialConnectors(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val typeStr = getPortListTypeString(ports)

    wrapClassMembersInIfDirective(
      "\n#if FW_PORT_SERIALIZATION",
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
    addAccessTagAndComment(
      "protected",
      s"Invocation functions for ${getPortListTypeString(ports)} output ports",
      ports.map(p => {
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
                  |
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
      })
    )
  }

  def generateConnectionStatusQueries(
    ports: List[PortInstance],
    portName: String => String,
    isConnectedName: String => String,
    numGetterName: PortInstance => String,
    variableName: PortInstance => String
  ): List[CppDoc.Class.Member] = {
    mapPorts(ports, p => List(
      functionClassMember(
        Some(
          s"""|Check whether port ${portName(p.getUnqualifiedName)} is connected
              |
              |\\return Whether port ${portName(p.getUnqualifiedName)} is connected
              |"""
        ),
        isConnectedName(p.getUnqualifiedName),
        List(portNumParam),
        CppDoc.Type("bool"),
        lines(
          s"""|FW_ASSERT(
              |  (0 <= portNum) && (portNum < this->${numGetterName(p)}()),
              |  static_cast<FwAssertArgType>(portNum)
              |);
              |
              |return this->${variableName(p)}[portNum].isConnected();
              |"""
        )
      )
    ))
  }

  def getConnectionStatusQueries(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      s"Connection status queries for ${getPortListTypeString(ports)} output ports",
      generateConnectionStatusQueries(
        ports,
        (s: String) => s,
        outputPortIsConnectedName,
        portNumGetterName,
        portVariableName
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
