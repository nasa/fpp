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
      mapPorts(ports, p => {
        val connectionFunction = p.getType.get match {
          case PortInstance.Type.DefPort(_) => "addCallPort"
          case PortInstance.Type.Serial => "registerSerialPort"
        }

        List(
          functionClassMember(
            Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
            outputPortConnectorName(p.getUnqualifiedName),
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
                  |  portNum < this->${portNumGetterName(p)}(),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |this->${portVariableName(p)}[portNum].$connectionFunction(port);
                  |"""
            )
          )
        )
      })
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
                    |  portNum < this->${portNumGetterName(p)}(),
                    |  static_cast<FwAssertArgType>(portNum)
                    |);
                    |
                    |this->${portVariableName(p)}[portNum].registerSerialPort(port);
                    |"""
              ),
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
      ports.map(p => {
        val invokeFunction = p.getType.get match {
          case PortInstance.Type.DefPort(_) => "invoke"
          case PortInstance.Type.Serial => "invokeSerial"
        }

        functionClassMember(
          Some(s"Invoke output port ${p.getUnqualifiedName}"),
          outputPortInvokerName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getReturnType(p),
          List.concat(
            lines(
              s"""|FW_ASSERT(
                  |  portNum < this->${portNumGetterName(p)}(),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |"""
            ),
            lines(
              addReturnKeyword(
                s"this->${portVariableName(p)}[portNum].$invokeFunction(${getPortParams(p).map(_._1).mkString(", ")});",
                p
              )
            )
          )
        )
      })
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
          lines(
            s"""|FW_ASSERT(
                |  portNum < this->${portNumGetterName(p)}(),
                |  static_cast<FwAssertArgType>(portNum)
                |);
                |
                |return this->${portVariableName(p)}[portNum].isConnected();
                |"""
          )
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

}
