package fpp.compiler.codegen

import fpp.compiler.analysis.*
import fpp.compiler.ast.*
import fpp.compiler.codegen.CppDoc.Lines.Cpp
import fpp.compiler.util.*

/** Writes out C++ for component test harness base classes */
case class ComponentTesterBaseWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponentTesterBase(name)

  private val historyWriter = ComponentHistory(s, aNode)

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component test harness base class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val cls = classMember(
      Some(
        s"\\class $testerBaseClassName\n\\brief Auto-generated base for $name component test harness",
      ),
      testerBaseClassName,
      Some("public Fw::PassiveComponentBase"),
      getClassMembers
    )
    List.concat(
      List(getHppIncludes, getCppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    )
  }

  private def getHppIncludes: CppDoc.Member = {
    val userHeaders = List(
      s"${s.getRelativePath(ComputeCppFiles.FileNames.getComponent(name)).toString}.hpp",
      "Fw/Types/Assert.hpp",
      "Fw/Comp/PassiveComponentBase.hpp",
      "Fw/Port/InputSerializePort.hpp"
    ).sorted.map(CppWriter.headerString).map(line)
    val systemHeader = lines(CppWriter.systemHeaderString("cstdio"))
    linesMember(
      List.concat(
        Line.blank :: systemHeader,
        Line.blank :: userHeaders
      )
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeader = lines(CppWriter.headerString(s"${s.getRelativePath(fileName).toString}.hpp"))
    val systemHeaders = List(
      "cstdlib",
      "cstring"
    ).sorted.map(CppWriter.systemHeaderString).map(line)
    linesMember(
      List.concat(
        Line.blank :: systemHeaders,
        Line.blank :: userHeader
      ),
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = {
    List.concat(
      // Public members
      getInitFunction,
      getPortConnectors,
      getPortGetters,

      // Protected members
      getConstructorMembers,
      getPortHandlers,
      getPortHandlerBases,
      getPortInvocationFunctions,
      getPortNumGetters,
      getPortConnectionStatusQueries,
      getCmdFunctions,

      // History members
      historyWriter.getHistoryClass,
      historyWriter.getMembers,
    )
  }

  private def getInitFunction: List[CppDoc.Class.Member] = {
    def writePortConnections(port: PortInstance) =
      ComponentCppWriter.writePortConnections(
        port,
        portNumGetterName,
        portVariableName,
        fromPortCallbackName,
        portName,
        true
      )

    val body = intersperseBlankLines(
      List(
        sortedParams.map((_, param) => line(
          s"this->${paramValidityFlagName(param.getName)} = Fw::ParamValid::UNINIT;"
        )),
        lines(
          """|// Initialize base class
             |Fw::PassiveComponentBase::init(instance);
             |"""
        ),
        intersperseBlankLines(specialOutputPorts.map(writePortConnections)),
        intersperseBlankLines(typedOutputPorts.map(writePortConnections)),
        intersperseBlankLines(serialOutputPorts.map(writePortConnections)),
        intersperseBlankLines(specialInputPorts.map(writePortConnections)),
        intersperseBlankLines(typedInputPorts.map(writePortConnections)),
        intersperseBlankLines(serialInputPorts.map(writePortConnections)),
      )
    )

    addAccessTagAndComment(
      "public",
      "Component initialization",
      List(
        functionClassMember(
          Some(s"Initialize object $testerBaseClassName"),
          "init",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("NATIVE_INT_TYPE"),
              "instance",
              Some("The instance number"),
              Some("0")
            )
          ),
          CppDoc.Type("void"),
          body,
          CppDoc.Function.Virtual
        )
      )
    )
  }

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Component construction and destruction",
      List(
        constructorClassMember(
          Some(s"Construct object $testerBaseClassName"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const char* const"),
              "compName",
              Some("The component name")
            ),
            CppDoc.Function.Param(
              CppDoc.Type("U32"),
              "maxHistorySize",
              Some("The maximum size of each history")
            )
          ),
          List(
            "Fw::PassiveComponentBase(compName)"
          ),
          intersperseBlankLines(
            List(
              if typedOutputPorts.nonEmpty then line("// Initialize port histories") ::
                typedOutputPorts.map(p => line(
                  s"this->${fromPortHistoryName(p.getUnqualifiedName)} = new History<${fromPortEntryName(p.getUnqualifiedName)}>(maxHistorySize);"
                ))
              else Nil,
              if hasCommands || hasParameters then lines(
                """|// Initialize command history
                   |this->cmdResponseHistory = new History<CmdResponse>(maxHistorySize);
                   |"""
              )
              else Nil,
              if hasEvents then List.concat(
                lines(
                  """|// Initialize event histories
                     |#if FW_ENABLE_TEXT_LOGGING
                     |this->textLogHistory = new History<TextLogEntry>(maxHistorySize);
                     |#endif
                     |"""
                ),
                sortedEvents.map((_, event) => line(
                  s"this->${eventHistoryName(event.getName)} = new History<${eventEntryName(event.getName)}>(maxHistorySize);"
                ))
              )
              else Nil,
              if hasChannels then line("// Initialize telemetry histories") ::
                sortedChannels.map((_, channel) => line(
                  s"this->${tlmHistoryName(channel.getName)} = new History<${tlmEntryName(channel.getName)}>(maxHistorySize);"
                ))
              else Nil,
              if hasHistories then lines(
                """|// Clear history
                   |this->clearHistory();
                   |"""
              )
              else Nil,
            )
          )
        ),
        destructorClassMember(
          Some(s"Destroy object $testerBaseClassName"),
          intersperseBlankLines(
            List(
              if typedOutputPorts.nonEmpty then line("// Destroy port histories") ::
                typedOutputPorts.map(p => line(
                  s"delete this->${fromPortHistoryName(p.getUnqualifiedName)};"
                ))
              else Nil,
              if hasCommands || hasParameters then lines(
                """|// Destroy command history
                   |delete this->cmdResponseHistory;
                   |"""
              )
              else Nil,
              if hasEvents then List.concat(
                lines(
                  """|// Destroy event histories
                     |#if FW_ENABLE_TEXT_LOGGING
                     |delete this->textLogHistory;
                     |#endif
                     |"""
                ),
                sortedEvents.map((_, event) => line(
                  s"delete this->${eventHistoryName(event.getName)};"
                ))
              )
              else Nil,
              if hasChannels then line("// Destroy telemetry histories") ::
                sortedChannels.map((_, channel) => line(
                  s"delete this->${tlmHistoryName(channel.getName)};"
                ))
              else Nil,
            )
          ),
          CppDoc.Class.Destructor.Virtual
        )
      )
    )
  }

  private def getPortConnectors: List[CppDoc.Class.Member] = {
    ComponentOutputPorts(s, aNode).generateTypedConnectors(
      List.concat(
        specialInputPorts,
        typedInputPorts,
        serialInputPorts,
      ),
      "Connectors for to ports",
      toPortConnectorName,
      portNumGetterName,
      portVariableName
    )
  }

  private def getPortGetters: List[CppDoc.Class.Member] = {
    ComponentInputPorts(s, aNode).generateGetters(
      List.concat(
        specialOutputPorts,
        typedOutputPorts,
        serialOutputPorts,
      ),
      "from",
      inputPortName,
      fromPortGetterName,
      portNumGetterName,
      portVariableName
    )
  }

  private def getPortHandlers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      s"Handlers to implement for from ports",
      ComponentInputPorts(s, aNode).generateHandlers(
        List.concat(
          typedOutputPorts,
          serialOutputPorts,
        ),
        inputPortName,
        fromPortHandlerName
      ),
      CppDoc.Lines.Hpp
    )
  }

  private def getPortHandlerBases: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Handler base-class functions for from ports",
      List.concat(
        typedOutputPorts,
        serialOutputPorts,
      ).map(p => {
        val returnKeyword = getPortReturnType(p) match {
          case Some(_) => "return "
          case None => ""
        }
        val handlerCall = writeFunctionCall(
          s"${returnKeyword}this->${fromPortHandlerName(p.getUnqualifiedName)}",
          List("portNum"),
          getPortParams(p).map(_._1)
        )

        functionClassMember(
          Some(s"Handler base-class function for ${inputPortName(p.getUnqualifiedName)}"),
          fromPortHandlerBaseName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getPortReturnTypeAsCppDocType(p),
          List.concat(
            lines(
              s"""|// Make sure port number is valid
                  |FW_ASSERT(
                  |  portNum < this->${portNumGetterName(p)}(),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |"""
            ),
            handlerCall
          )
        )
      })
    )
  }

  private def getPortInvocationFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Invocation functions for to ports",
      List.concat(
        typedInputPorts,
        serialInputPorts,
      ).map(p => {
        val returnKeyword = getPortReturnType(p) match {
          case Some(_) => "return "
          case None => ""
        }
        val invokeCall = writeFunctionCall(
          s"${returnKeyword}this->${portVariableName(p)}[portNum].invoke",
          Nil,
          getPortParams(p).map(_._1)
        )

        functionClassMember(
          Some(s"Invoke the to port connected to ${p.getUnqualifiedName}"),
          toPortInvokerName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getPortReturnTypeAsCppDocType(p),
          List.concat(
            lines(
              s"""|// Make sure port number is valid
                  |FW_ASSERT(
                  |  portNum < this->${portNumGetterName(p)}(),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |"""
            ),
            invokeCall
          )
        )
      })
    )
  }

  private def getPortNumGetters: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Getters for port counts",
      ComponentPorts(s, aNode).generateNumGetters(
        List.concat(
          typedInputPorts,
          serialInputPorts,
          typedOutputPorts,
          serialOutputPorts,
        ),
        portNumGetterName,
        portName,
        portVariableName
      )
    )
  }

  private def getPortConnectionStatusQueries: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Connection status queries for to ports",
      ComponentOutputPorts(s, aNode).generateConnectionStatusQueries(
        List.concat(
          typedInputPorts,
          serialInputPorts,
          specialInputPorts,
        ),
        outputPortName,
        toPortIsConnectedName,
        portNumGetterName,
        portVariableName
      )
    )
  }

  private def getCmdFunctions: List[CppDoc.Class.Member] = {
    val cmdPortInvocation = lines(
      s"""|if (this->${portVariableName(cmdRecvPort.get)}[0].isConnected()) {
          |  this->${portVariableName(cmdRecvPort.get)}[0].invoke(
          |    _opcode,
          |    cmdSeq,
          |    args
          |  );
          |}
          |else {
          |  printf("Test Command Output port not connected!\\n");
          |}
          |"""
    )

    addAccessTagAndComment(
      "protected",
      "Functions for testing commands",
      List.concat(
        List(
          functionClassMember(
            Some("Handle a command response"),
            "cmdResponseIn",
            List(
              opcodeParam,
              cmdSeqParam,
              CppDoc.Function.Param(
                CppDoc.Type("Fw::CmdResponse"),
                "response",
                Some("The command response")
              )
            ),
            CppDoc.Type("void"),
            lines(
              """|CmdResponse e = { opCode, seq, response };
                 |this->cmdResponseHistory->push_back(e);
                 |"""
            ),
            CppDoc.Function.Virtual
          ),
          functionClassMember(
            Some("Send command buffers directly (used for intentional command encoding errors)"),
            "sendRawCmd",
            List(
              opcodeParam,
              cmdSeqParam,
              CppDoc.Function.Param(
                CppDoc.Type("Fw::CmdBufferArg&"),
                "args",
                Some("The command argument buffer")
              )
            ),
            CppDoc.Type("void"),
            List.concat(
              lines(
                s"""|const U32 idBase = this->getIdBase();
                    |FwOpcodeType _opcode = opcode + idBase;
                    |
                    |"""
              ),
              cmdPortInvocation
            )
          )
        ),
        nonParamCmds.map((opcode, cmd) =>
          functionClassMember(
            Some(s"Send a ${cmd.getName} command"),
            sendCmdName(cmd.getName),
            List.concat(
              List(
                CppDoc.Function.Param(
                  CppDoc.Type("const NATIVE_INT_TYPE"),
                  "instance",
                  Some("The instance number")
                ),
                cmdSeqParam
              ),
              cmdParamMap(opcode)
            ),
            CppDoc.Type("void"),
            intersperseBlankLines(
              List(
                if cmdParamMap(opcode).isEmpty then Nil
                else List.concat(
                  lines(
                    """|// Serialize arguments
                       |Fw::CmdArgBuffer buf;
                       |Fw::SerializeStatus _status;
                       |
                       |"""
                  ),
                  intersperseBlankLines(
                    cmdParamTypeMap(opcode).map((name, _) =>
                      lines(
                        s"""|_status = buf.serialize($name);
                            |FW_ASSERT(
                            |  status == FW::FW_SERIALIZE_OK,
                            |  static_cast<FwAssertArgType>(_status)
                            |);
                            |"""
                      )
                    )
                  )
                ),
                lines(
                  s"""|// Call output command port
                      |FwOpcodeType _opcode;
                      |const U32 idBase = this->getIdBase();
                      |_opcode = $className::${commandConstantName(cmd)} + idBase;
                      |"""
                ),
                cmdPortInvocation
              )
            )
          )
        )
      )
    )
  }

  private def getEventFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Functions for testing events",
      List.concat(
        List(
          functionClassMember(
            Some("Dispatch an event"),
            "dispatchEvents",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("FwEventIdType"),
                "id",
                Some("The event ID")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("Fw::Time&"),
                "timeTag",
                Some("The time")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("const Fw::LogSeverity"),
                "severity",
                Some("The severity")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("Fw::LogBuffer&"),
                "args",
                Some("The serialized arguments")
              )
            ),
            CppDoc.Type("void"),
            Nil
          )
        ),
        wrapClassMemberInTextLogGuard(
          functionClassMember(
            Some("Handle a text event"),
            "textLogIn",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("FwEventIdType"),
                "id",
                Some("The event ID")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("Fw::Time&"),
                "timeTag",
                Some("The time")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("const Fw::LogSeverity"),
                "severity",
                Some("The severity")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("const Fw::TextLogString&"),
                "text",
                Some("The event string")
              )
            ),
            CppDoc.Type("void"),
            lines(
              s"""|TextLogEntry e = { id, timeTag, severity, text };
                  |textLogHistory->push_back(e);
                  |"""
            )
          )
        ),
        sortedEvents.map((id, event) =>
          functionClassMember(
            Some(s"Handle event ${event.getName}"),
            eventHandlerName(event),
            formalParamsCppWriter.write(
              event.aNode._2.data.params,
              Nil,
              Some("Fw::LogStringArg"),
              FormalParamsCppWriter.Value
            ),
            CppDoc.Type("void"),
            List.concat(
              eventParamTypeMap(id) match {
                case Nil => lines(
                  s"this->${eventSizeName(event.getName)}++;"
                )
                case params => List.concat(
                  wrapInScope(
                    s"${eventEntryName(event.getName)} e = {",
                    lines(params.map(_._1).mkString(",\n")),
                    "};"
                  ),
                  lines(s"${eventHistoryName(event.getName)}->push_back(e);")
                )
              },
              lines("this->eventsSize++;")
            ),
            CppDoc.Function.Virtual
          )
        )
      )
    )
  }

  private def getTlmFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Functions for testing telemetry",
      List.concat(
        List(
          functionClassMember(
            Some("Dispatch telemetry"),
            "dispatchTlm",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("FwChanIdType"),
                "id",
                Some("The channel id")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("const Fw::Time&"),
                "timeTag",
                Some("The time")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("Fw::TlmBuffer&"),
                "val",
                Some("The channel value")
              )
            ),
            CppDoc.Type("void"),
            List.concat(
              lines(
                """|val.resetDeser();
                   |
                   |const U32 idBase = this->getIdBase();
                   |FW_ASSERT(id >= idBase, id, idBase);
                   |"""
              ),
              wrapInSwitch(
                "id - idBase",
                List.concat(
                  sortedChannels.flatMap((_, channel) =>
                    wrapInScope(
                      s"case $className::${channelIdConstantName(channel.getName)}: {",
                      lines(
                        s"""|${getChannelType(channel.channelType)} arg;
                            |const Fw::SerializeStatus _status = val.deserialize(arg);
                            |
                            |if (_status != Fw::FW_SERIALIZE_OK) {
                            |  printf("Error deserializing ${channel.getName}: %d\n", _status);
                            |  return;
                            |}
                            |
                            |this->${tlmHandlerName(channel.getName)}(timeTag, arg);
                            |break;
                            |"""
                      ),
                      "}"
                    )
                  ),
                  wrapInScope(
                    "default: {",
                    lines(
                      """|FW_ASSERT(0, id);
                         |break;
                         |"""
                    ),
                    "}"
                  )
                )
              )
            )
          )
        ),
        sortedChannels.map((_, channel) =>
          functionClassMember(
            Some(s"Handle channel ${channel.getName}"),
            tlmHandlerName(channel.getName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("const Fw::Time&"),
                "timeTag",
                Some("The time")
              ),
            ),
            CppDoc.Type("void"),
            lines(
              s"""|${tlmEntryName(channel.getName)} e = { timeTag, val };
                  |this->${tlmHistoryName(channel.getName)}->push_back(e);
                  |this->tlmSize++;
                  |"""
            )
          )
        )
      )
    )
  }

  private def getTimeFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Functions to test time",
      List(
        functionClassMember(
          Some("Set the test time for events and telemetry"),
          "testTime",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const Fw::Time&"),
              "timeTag",
              Some("The time"),
            )
          ),
          CppDoc.Type("void"),
          lines("this->m_testTime = timeTag;")
        )
      )
    )
  }

  private def getPrmFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Functions to test parameters",
      sortedParams.flatMap((_, prm) =>
        List(
          functionClassMember(
            Some(s"Set parameter ${prm.getName}"),
            prmSetName(prm.getName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type(s"const ${writeParamType(prm.paramType)}&"),
                "val",
                Some("The parameter value")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("Fw::ParamValid"),
                "valid",
                Some("The parameter valid flag")
              )
            ),
            CppDoc.Type("void"),
            lines(
              s"""|this->${prmVariableName(prm.getName)} = val;
                  |this->${prmValidVariableName(prm.getName)} = valid;
                  |"""
            )
          ),
          functionClassMember(
            Some(s"Send parameter ${prm.getName}"),
            prmSetName(prm.getName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "instance",
                Some("The component instance")
              ),
              cmdSeqParam
            ),
            CppDoc.Type("void"),
            lines(
              s"""|// Build command for parameter set
                  |Fw::CmdArgBuffer args;
                  |FW_ASSERT(
                  |  args.serialize(this->${prmVariableName(prm.getName)}) == Fw::FW_SERIALIZE_OK
                  |);
                  |
                  |const U32 idBase = this->getIdBase();
                  |FwOpcodeType _prmOpcode =  $className::${paramCommandConstantName(prm.getName, Command.Param.Set)} + idBase;
                  |
                  |if (not this->${portVariableName(cmdRecvPort.get)}[0].isConnected()) {
                  |  printf("Test Command Output port not connected!\\n");
                  |}
                  |else {
                  |  this->m_to_${portVariableName(cmdRecvPort.get)}[0].invoke(
                  |    _prmOpcode,
                  |    cmdSeq,
                  |    args
                  |  );
                  |}
                  |"""
            )
          ),
          functionClassMember(
            Some(s"Save parameter ${prm.getName}"),
            prmSaveName(prm.getName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "instance",
                Some("The component instance")
              ),
              cmdSeqParam
            ),
            CppDoc.Type("void"),
            lines(
              s"""|Fw::CmdArgBuffer args;
                  |FwOpcodeType _prmOpcode = $className::${paramCommandConstantName(prm.getName, Command.Param.Save)} + idBase;
                  |const U32 idBase = this->getIdBase();
                  |
                  |if (not this->${portVariableName(cmdRecvPort.get)}[0].isConnected()) {
                  |  printf("Test Command Output port not connected!\\n");
                  |}
                  |else {
                  |  this->${portVariableName(cmdRecvPort.get)}[0].invoke(
                  |    _prmOpcode,
                  |    cmdSeq,
                  |    args
                  |  );
                  |}
                  |"""
            )
          )
        )
      )
    )
  }

}
