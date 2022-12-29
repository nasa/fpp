package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component output port instances */
case class ComponentOutputPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def write: List[CppDoc.Class.Member] = {
    if !hasOutputPorts then Nil
    else List(
      getTypedConnectors,
      getSerialConnectors,
      getInvokers(typedOutputPorts),
      getInvokers(serialOutputPorts),
      getNumGetters,
      getEnum,
      getConnectionStatusQueries,
      getPortMembers(typedOutputPorts),
      getPortMembers(serialOutputPorts)
    ).flatten
  }

  private def getTypedConnectors: List[CppDoc.Class.Member] = {
    if !hasTypedOutputPorts then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment("" +
                "Connect typed input ports to typed output ports"
              ),
            ).flatten
          )
        )
      ),
      typedOutputPorts.map(p => {
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
            outputConnectorName(p.getUnqualifiedName),
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
                  |  portNum < this->${outputNumGetterName(p.getUnqualifiedName)}(),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |this->${outputMemberName(p.getUnqualifiedName)}[portNum].addCallPort(port);
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
                    "Connect serial input ports to typed output ports"
                  ),
                ).flatten
              )
            )
          ),
          typedOutputPorts.map(p => {
            CppDoc.Class.Member.Function(
              CppDoc.Function(
                Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
                outputConnectorName(p.getUnqualifiedName),
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

  private def getSerialConnectors: List[CppDoc.Class.Member] = {
    if !hasSerialOutputPorts then Nil
    else wrapClassMembersInIfDirective(
      "\n#if FW_PORT_SERIALIZATION",
      List(
        List(
          CppDoc.Class.Member.Lines(
            CppDoc.Lines(
              List(
                CppDocHppWriter.writeAccessTag("public"),
                CppDocWriter.writeBannerComment("" +
                  "Connect serial input ports to typed output ports"
                ),
              ).flatten
            )
          )
        ),
        serialOutputPorts.flatMap(p =>
          List(
            CppDoc.Class.Member.Function(
              CppDoc.Function(
                Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
                outputConnectorName(p.getUnqualifiedName),
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
                outputConnectorName(p.getUnqualifiedName),
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

  private def getInvokers(ports: List[PortInstance.General]): List[CppDoc.Class.Member] = {
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
            outputInvokerName(p.getUnqualifiedName),
            portNumParam :: getFunctionParams(p),
            getReturnType(p),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getNumGetters: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment("" +
                "Getters for numbers of output ports"
              ),
            ).flatten
          )
        )
      ),
      outputPorts.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Get the number of ${p.getUnqualifiedName} output ports"),
            outputNumGetterName(p.getUnqualifiedName),
            Nil,
            CppDoc.Type("NATIVE_INT_TYPE"),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getEnum: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment("" +
              "Enumerations for numbers of output ports"
            ),
            Line.blank :: wrapInEnum(
              outputPorts.map(p =>
                line(s"${outputEnumName(p.getUnqualifiedName)} = ${p.getArraySize};")
              )
            )
          ).flatten
        )
      )
    )
  }

  private def getConnectionStatusQueries: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment("" +
                "Connection status queries for output ports"
              ),
            ).flatten
          )
        )
      ),
      outputPorts.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Check whether port ${p.getUnqualifiedName} is connected"),
            outputIsConnectedName(p.getUnqualifiedName),
            List(portNumParam),
            CppDoc.Type("bool"),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getPortMembers(ports: List[PortInstance.General]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PRIVATE"),
            CppDocWriter.writeBannerComment("" +
              s"${getTypeString(ports.head).capitalize} output ports"
            ),
            ports.flatMap(p => {
              val typeName = getQualifiedPortTypeName(p, PortInstance.Direction.Output)
              val name = outputMemberName(p.getUnqualifiedName)
              val num = outputEnumName(p.getUnqualifiedName)

              lines(
                s"""|
                    |//! Output port ${p.getUnqualifiedName}
                    |$typeName $name[$num];
                    |"""
              )
            })
          ).flatten
        )
      )
    )
  }

}
