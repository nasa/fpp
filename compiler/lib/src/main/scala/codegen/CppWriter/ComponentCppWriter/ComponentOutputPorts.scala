package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component output port instances */
case class ComponentOutputPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getTypedConnectors(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      writeAccessTagAndComment(
        "public",
        s"Connect ${getPortTypeString(ports.head)} input ports to ${getPortTypeString(ports.head)} output ports"
      ),
      mapPorts(ports, p => List(
        functionClassMember(
          Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
          outputPortConnectorName(p.getUnqualifiedName),
          List(
            portNumParam,
            CppDoc.Function.Param(
              CppDoc.Type(s"${getQualifiedPortTypeName(p, PortInstance.Direction.Input)}*"),
              "port",
              Some("The input port")
            )
          ),
          CppDoc.Type("void"),
          lines(
            s"""|FW_ASSERT(
                |  portNum < this->${portNumGetterName(p.getUnqualifiedName, PortInstance.Direction.Output)}(),
                |  static_cast<FwAssertArgType>(portNum)
                |);
                |
                |this->${portVariableName(p.getUnqualifiedName, PortInstance.Direction.Output)}[portNum].addCallPort(port);
                |"""
          )
        )
      )),
      wrapClassMembersInIfDirective(
        "\n#if FW_PORT_SERIALIZATION",
        List(
          writeAccessTagAndComment(
            "public",
            s"Connect serial input ports to ${getPortTypeString(ports.head)} output ports"
          ),
          mapPorts(ports, p => List(
            functionClassMember(
              Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
              outputPortConnectorName(p.getUnqualifiedName),
              List(
                portNumParam,
                CppDoc.Function.Param(
                  CppDoc.Type(s"Fw::InputSerializePort*"),
                  "port",
                  Some("The port")
                )
              ),
              CppDoc.Type("void"),
              Nil
            )
          ))
        ).flatten
      )
    ).flatten
  }

  def getSerialConnectors(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else wrapClassMembersInIfDirective(
      "\n#if FW_PORT_SERIALIZATION",
      List(
        writeAccessTagAndComment(
          "public",
          "Connect serial input ports to serial output ports"
        ),
        ports.flatMap(p =>
          List(
            functionClassMember(
              Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
              outputPortConnectorName(p.getUnqualifiedName),
              List(
                portNumParam,
                CppDoc.Function.Param(
                  CppDoc.Type("Fw::InputSerializePort*"),
                  "port",
                  Some("The port")
                )
              ),
              CppDoc.Type("void"),
              Nil
            ),
            functionClassMember(
              Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
              outputPortConnectorName(p.getUnqualifiedName),
              List(
                portNumParam,
                CppDoc.Function.Param(
                  CppDoc.Type("Fw::InputPortBase*"),
                  "port",
                  Some("The port")
                )
              ),
              CppDoc.Type("void"),
              Nil
            )
          )
        )
      ).flatten
    )
  }

  def getInvokers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      writeAccessTagAndComment(
        "PROTECTED",
        s"Invocation functions for ${getPortTypeString(ports.head)} output ports"
      ),
      ports.map(p =>
        functionClassMember(
          Some(s"Invoke output port ${p.getUnqualifiedName}"),
          outputPortInvokerName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getReturnType(p),
          Nil
        )
      )
    ).flatten
  }

  def getConnectionStatusQueries(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      writeAccessTagAndComment(
        "PROTECTED",
        s"Connection status queries for ${getPortTypeString(ports.head)} output ports"
      ),
      mapPorts(ports, p => List(
        functionClassMember(
          Some(
            s"""|Check whether port ${p.getUnqualifiedName} is connected
                |
                |\\return Whether port ${p.getUnqualifiedName} is connected
                |"""
          ),
          outputPortIsConnectedName(p.getUnqualifiedName),
          List(portNumParam),
          CppDoc.Type("bool"),
          Nil
        )
      ))
    ).flatten
  }

  // Get a port return type as a CppDoc Type
  private def getReturnType(p: PortInstance): CppDoc.Type =
    p.getType.get match {
      case PortInstance.Type.DefPort(symbol) => CppDoc.Type(
        PortCppWriter(s, symbol.node).returnType
      )
      case PortInstance.Type.Serial => CppDoc.Type(
        "Fw::SerializeStatus"
      )
    }

  // Get the name for an output port connector function
  private def outputPortConnectorName(name: String) =
    s"set_${name}_OutputPort"

  // Get the name for an output port connection status function
  private def outputPortIsConnectedName(name: String) =
    s"isConnected_${name}_OutputPort"

  // Get the name for an output port invocation function
  private def outputPortInvokerName(name: String) =
    s"${name}_out"

}
