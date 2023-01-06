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
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment(
                s"Connect ${getTypeString(ports.head)} input ports to ${getTypeString(ports.head)} output ports"
              ),
            ).flatten
          )
        )
      ),
      ports.map(p => {
        CppDoc.Class.Member.Function(
          CppDoc.Function(
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
                  |this->${portMemberName(p.getUnqualifiedName, PortInstance.Direction.Output)}[portNum].addCallPort(port);
                  |"""
            )
          )
        )
      }),
      wrapClassMembersInIfDirective(
        "\n#if FW_PORT_SERIALIZATION",
        List(
          List(
            CppDoc.Class.Member.Lines(
              CppDoc.Lines(
                List(
                  CppDocHppWriter.writeAccessTag("public"),
                  CppDocWriter.writeBannerComment("" +
                    s"Connect serial input ports to ${getTypeString(ports.head)} output ports"
                  ),
                ).flatten
              )
            )
          ),
          ports.map(p => {
            CppDoc.Class.Member.Function(
              CppDoc.Function(
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
            )
          })
        ).flatten
      )
    ).flatten
  }

   def getSerialConnectors(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else wrapClassMembersInIfDirective(
      "\n#if FW_PORT_SERIALIZATION",
      List(
        List(
          CppDoc.Class.Member.Lines(
            CppDoc.Lines(
              List(
                CppDocHppWriter.writeAccessTag("public"),
                CppDocWriter.writeBannerComment("" +
                  "Connect serial input ports to serial output ports"
                ),
              ).flatten
            )
          )
        ),
        ports.flatMap(p =>
          List(
            CppDoc.Class.Member.Function(
              CppDoc.Function(
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
              )
            ),
            CppDoc.Class.Member.Function(
              CppDoc.Function(
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
        )
      ).flatten
    )
  }

   def getInvokers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment("" +
                s"Invocation functions for ${getTypeString(ports.head)} output ports"
              ),
            ).flatten
          )
        )
      ),
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Invoke output port ${p.getUnqualifiedName}"),
            outputPortInvokerName(p.getUnqualifiedName),
            portNumParam :: getFunctionParams(p),
            getReturnType(p),
            Nil
          )
        )
      )
    ).flatten
  }

  def getConnectionStatusQueries(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment("" +
                s"Connection status queries for ${getTypeString(ports.head)} output ports"
              ),
            ).flatten
          )
        )
      ),
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
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
        )
      )
    ).flatten
  }

}
