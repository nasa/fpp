package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component test harness base classes */
case class ComponentTesterBaseWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponentTesterBase(name)

  private val relativeFileName = s.getRelativePath(fileName).toString

  private val componentFileName = ComputeCppFiles.FileNames.getComponent(name)

  private val componentRelativeFileName = s.getRelativePath(componentFileName).toString

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
      s"$componentRelativeFileName.hpp",
      "Fw/Comp/PassiveComponentBase.hpp",
      "Fw/Port/InputSerializePort.hpp",
      "Fw/Types/Assert.hpp"
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
    val userHeader = lines(CppWriter.headerString(s"$relativeFileName.hpp"))
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
      // Types
      historyWriter.getClassMember,
      historyWriter.getTypeMembers,

      // Public function members
      getInitFunction,
      getPortConnectors,
      getPortGetters,

      // Protected function members
      getConstructorMembers,
      getPortHandlers,
      getPortHandlerBases,
      getPortInvocationFunctions,
      getPortNumGetters,
      getPortConnectionStatusQueries,
      guardedList (hasCommands) (getCmdFunctions),
      guardedList (hasEvents) (getEventFunctions),
      guardedList (hasTelemetry) (getTlmFunctions),
      guardedList (hasParameters) (getPrmFunctions),
      guardedList (hasTimeGetPort) (getTimeFunctions),
      historyWriter.getFunctionMembers,

      // Private function members
      getPortStaticFunctions,

      // Member variables
      historyWriter.getVariableMembers,
      getVariableMembers,
    )
  }

  private def getInitFunction: List[CppDoc.Class.Member] = {
    def writePortConnections(port: PortInstance) = {
      lazy val code = ComponentCppWriter.writePortConnections(
        port,
        portNumGetterName,
        portVariableName,
        fromPortCallbackName,
        portName,
        true
      )
      guardedList (portInstanceIsActive(port)) (code)
    }

    val body = intersperseBlankLines(
      List(
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
          constructorParams,
          "Fw::PassiveComponentBase(compName)" :: sortedParams.map((_, param) =>
            s"${paramValidityFlagName(param.getName)}(Fw::ParamValid::UNINIT)"
          ),
          {
            lazy val portHistories = line("// Initialize port histories") ::
              typedOutputPorts.map(p => {
                val historyName = fromPortHistoryName(p.getUnqualifiedName)
                val entryName = fromPortEntryName(p.getUnqualifiedName)
                line(s"this->$historyName = new History<$entryName>(maxHistorySize);")
              })
            lazy val commandHistory = lines(
              """|// Initialize command history
                 |this->cmdResponseHistory = new History<CmdResponse>(maxHistorySize);
                 |"""
            )
            lazy val eventHistories = List.concat(
              lines(
                """|// Initialize event histories
                   |#if FW_ENABLE_TEXT_LOGGING
                   |this->textLogHistory = new History<TextLogEntry>(maxHistorySize);
                   |#endif
                   |"""
              ),
              sortedEvents.flatMap((id, event) =>
                eventParamTypeMap(id) match {
                  case Nil => Nil
                  case _ => 
                    val historyName = eventHistoryName(event.getName)
                    val entryName = eventEntryName(event.getName)
                    lines(
                      s"this->$historyName = new History<$entryName>(maxHistorySize);"
                    )
                }
              )
            )
            lazy val tlmHistories = {
              line("// Initialize telemetry histories") ::
              sortedChannels.map((_, channel) => {
                val historyName = tlmHistoryName(channel.getName)
                val entryName = tlmEntryName(channel.getName)
                line(s"this->$historyName = new History<$entryName>(maxHistorySize);")
              })
            }
            lazy val clearHistory = lines(
              """|// Clear history
                 |this->clearHistory();
                 |"""
            )
            intersperseBlankLines(
              List(
                guardedList (hasTypedOutputPorts) (portHistories),
                guardedList (hasCommands) (commandHistory),
                guardedList (hasEvents) (eventHistories),
                guardedList (hasTelemetry) (tlmHistories),
                guardedList (hasHistories) (clearHistory)
              )
            )
          }
        ),
        destructorClassMember(
          Some(s"Destroy object $testerBaseClassName"),
          {
            lazy val destroyPortHistories = line("// Destroy port histories") ::
              typedOutputPorts.map(p => {
                val portHistoryName = fromPortHistoryName(p.getUnqualifiedName)
                line(s"delete this->$portHistoryName;")
              })
            lazy val destroyCommandHistory = lines(
              """|// Destroy command history
                 |delete this->cmdResponseHistory;
                 |"""
            )
            lazy val destroyEventHistories = List.concat(
              lines(
                """|// Destroy event histories
                   |#if FW_ENABLE_TEXT_LOGGING
                   |delete this->textLogHistory;
                   |#endif
                   |"""
              ),
              sortedEvents.flatMap(
                (id, event) => eventParamTypeMap(id) match {
                  case Nil => Nil
                  case _ => 
                    val historyName = eventHistoryName(event.getName)
                    lines( s"delete this->$historyName;")
                }
              )
            )
            lazy val destroyTlmHistories = line("// Destroy telemetry histories") ::
              sortedChannels.map((_, channel) => {
                val historyName = tlmHistoryName(channel.getName)
                line(s"delete this->$historyName;")
              })
            intersperseBlankLines(
              List(
                guardedList (hasTypedOutputPorts) (destroyPortHistories),
                guardedList (hasCommands) (destroyCommandHistory),
                guardedList (hasEvents) (destroyEventHistories),
                guardedList (hasChannels) (destroyTlmHistories)
              )
            )
          },
          CppDoc.Class.Destructor.Virtual
        )
      )
    )
  }

  private def getPortConnectors: List[CppDoc.Class.Member] = {
    ComponentOutputPorts(s, aNode).generateConnectors(
      inputPorts,
      "Connectors for to ports",
      toPortConnectorName,
      portNumGetterName,
      portVariableName
    )
  }

  private def getPortGetters: List[CppDoc.Class.Member] = {
    ComponentInputPorts(s, aNode).generateGetters(
      outputPorts,
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
        val handlerName = fromPortHandlerName(p.getUnqualifiedName)
        val handlerCall = writeFunctionCall(
          s"${returnKeyword}this->$handlerName",
          List("portNum"),
          getPortParams(p).map(_._1)
        )

        functionClassMember(
          {
            val portName = inputPortName(p.getUnqualifiedName)
            Some(s"Handler base-class function for $portName")
          },
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
        val returnKeywordOpt = getPortReturnType(p).map(_ => "return ").getOrElse("")
        val invokeFunction = p.getType.get match {
          case PortInstance.Type.DefPort(_) => "invoke"
          case PortInstance.Type.Serial => "invokeSerial"
        }
        val invokeCall = writeFunctionCall(
          s"${returnKeywordOpt}this->${portVariableName(p)}[portNum].$invokeFunction",
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
        inputPorts ++ outputPorts,
        portName,
        portNumGetterName,
        portVariableName
      )
    )
  }

  private def getPortConnectionStatusQueries: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Connection status queries for to ports",
      ComponentOutputPorts(s, aNode).generateConnectionStatusQueries(
        inputPorts,
        outputPortName,
        toPortIsConnectedName,
        portNumGetterName,
        portVariableName
      )
    )
  }

  private def getCmdFunctions: List[CppDoc.Class.Member] = {
    val cmdPortInvocation = cmdRecvPort.map(
      p => {
        val varName = portVariableName(p)
        lines(
          s"""|if (this->$varName[0].isConnected()) {
              |  this->$varName[0].invoke(
              |    _opcode,
              |    cmdSeq,
              |    buf
              |  );
              |}
              |else {
              |  printf("Test Command Output port not connected!\\n");
              |}
              |"""
        )
      }
    ).getOrElse(Nil)

    def writeCmdSendFunc(opcode: Command.Opcode, cmd: Command) = functionClassMember(
      Some(s"Send a ${cmd.getName} command"),
      commandSendName(cmd.getName),
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
          if cmdParamMap(opcode).isEmpty then lines("Fw::CmdArgBuffer buf;")
          else List.concat(
            lines(
              """|// Serialize arguments
                 |Fw::CmdArgBuffer buf;
                 |Fw::SerializeStatus _status;
                 |"""
            ),
            Line.blank :: intersperseBlankLines(
              cmdParamTypeMap(opcode).map((name, _) =>
                lines(
                  s"""|_status = buf.serialize($name);
                      |FW_ASSERT(
                      |  _status == Fw::FW_SERIALIZE_OK,
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
          guardedList (hasCommands) (cmdPortInvocation)
        )
      )
    )

    addAccessTagAndComment(
      "protected",
      "Functions for testing commands",
      {
        lazy val handleCmdResponse = functionClassMember(
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
            """|CmdResponse e = { opCode, cmdSeq, response };
               |this->cmdResponseHistory->push_back(e);
               |"""
          ),
          CppDoc.Function.Virtual
        )
        lazy val sendCmdBuffers = functionClassMember(
          Some("Send command buffers directly (used for intentional command encoding errors)"),
          "sendRawCmd",
          List(
            opcodeParam,
            cmdSeqParam,
            CppDoc.Function.Param(
              CppDoc.Type("Fw::CmdArgBuffer&"),
              "buf",
              Some("The command argument buffer")
            )
          ),
          CppDoc.Type("void"),
          List.concat(
            lines(
              s"""|const U32 idBase = this->getIdBase();
                  |FwOpcodeType _opcode = opCode + idBase;
                  |
                  |"""
            ),
            cmdPortInvocation
          )
        )
        List.concat(
          guardedList (hasCommands) (List(handleCmdResponse, sendCmdBuffers)),
          nonParamCmds.map((opcode, cmd) => writeCmdSendFunc(opcode, cmd))
        )
      }
    )
  }

  private def getEventFunctions: List[CppDoc.Class.Member] = {
    def writeSwitchCase(id: Event.Id, event: Event) = {
      val params = eventParamTypeMap(id)

      wrapInScope(
        s"case $className::${eventIdConstantName(event.getName)}: {",
        List.concat(
          params match {
            case Nil => lines(
              """|#if FW_AMPCS_COMPATIBLE
                 |// For AMPCS, decode zero arguments
                 |Fw::SerializeStatus _zero_status = Fw::FW_SERIALIZE_OK;
                 |U8 _noArgs;
                 |_zero_status = args.deserialize(_noArgs);
                 |FW_ASSERT(
                 |  _zero_status == Fw::FW_SERIALIZE_OK,
                 |  static_cast<FwAssertArgType>(_zero_status)
                 |);
                 |#endif
                 |"""
            )
            case _ => List.concat(
              lines(
                """|Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                   |
                   |#if FW_AMPCS_COMPATIBLE
                   |// Deserialize the number of arguments.
                   |U8 _numArgs;
                   |_status = args.deserialize(_numArgs);
                   |FW_ASSERT(
                   |  _status == Fw::FW_SERIALIZE_OK,
                   |  static_cast<FwAssertArgType>(_status)
                   |);
                   |// Verify they match expected.
                   |"""
              ),
              {
                val length = params.length
                event.aNode._2.data.severity match {
                  case Ast.SpecEvent.Fatal => lines(
                    s"""|FW_ASSERT(_numArgs == $length + 1, _numArgs, $length + 1);
                        |
                        |// For FATAL, there is a stack size of 4 and a dummy entry
                        |U8 stackArgLen;
                        |_status = args.deserialize(stackArgLen);
                        |FW_ASSERT(
                        |    _status == Fw::FW_SERIALIZE_OK,
                        |    static_cast<FwAssertArgType>(_status)
                        |);
                        |FW_ASSERT(stackArgLen == 4, stackArgLen);
                        |
                        |U32 dummyStackArg;
                        |_status = args.deserialize(dummyStackArg);
                        |FW_ASSERT(
                        |    _status == Fw::FW_SERIALIZE_OK,
                        |    static_cast<FwAssertArgType>(_status)
                        |);
                        |FW_ASSERT(dummyStackArg == 0, dummyStackArg);
                        |"""
                  )
                  case _ => lines(s"FW_ASSERT(_numArgs == $length, _numArgs, $length);")
                }
              },
              lines("#endif")
            )
          },
          event.aNode._2.data.params.flatMap(aNode => {
            val data = aNode._2.data
            val name = data.name
            val tn = getEventParam(data)
            val paramType = s.a.typeMap(data.typeName.id)
            val serializedSizeExpr = s.getSerializedSizeExpr(paramType, tn)

            lines(
              s"""|
                                |$tn $name;
                  |#if FW_AMPCS_COMPATIBLE
                  |{
                  |  // Deserialize the argument size
                  |  U8 _argSize;
                  |  _status = args.deserialize(_argSize);
                  |  FW_ASSERT(
                  |    _status == Fw::FW_SERIALIZE_OK,
                  |    static_cast<FwAssertArgType>(_status)
                  |  );
                  |  FW_ASSERT(_argSize == $serializedSizeExpr, _argSize, $serializedSizeExpr);
                  |}
                  |#endif
                  |_status = args.deserialize($name);
                  |FW_ASSERT(
                  |  _status == Fw::FW_SERIALIZE_OK,
                  |  static_cast<FwAssertArgType>(_status)
                  |);
                  |"""
            )
          }),
          {
            val handlerName = eventHandlerName(event)
            val paramString = params.map(_._1).mkString(", ")
            lines(
              s"""|this->$handlerName($paramString);
                  |break;
                  |"""
            )
          }
        ),
        "}"
      )
    }

    lazy val switchStatement = Line.blank :: wrapInSwitch(
      "(id - idBase)",
      intersperseBlankLines(
        sortedEvents.map((id, event) => writeSwitchCase(id, event)) ++ List(
          lines(
            """|default: {
               |  FW_ASSERT(0, id);
               |  break;
               |}
               |"""
          )
        )
      )
    )

    addAccessTagAndComment(
      "protected",
      "Functions for testing events",
      {
        lazy val dispatchEvent = functionClassMember(
          Some("Dispatch an event"),
          "dispatchEvents",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("FwEventIdType"),
              "id",
              Some("The event ID")
            ),
            timeTagParam,
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
          List.concat(
            lines(
              """|args.resetDeser();
                 |
                 |const U32 idBase = this->getIdBase();
                 |FW_ASSERT(id >= idBase, id, idBase);
                 |"""
            ),
            switchStatement
          )
        )
        lazy val handleTextEvent = wrapClassMemberInTextLogGuard(
          functionClassMember(
            Some("Handle a text event"),
            "textLogIn",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("FwEventIdType"),
                "id",
                Some("The event ID")
              ),
              timeTagParam,
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
        )
        List.concat(
          guardedList (hasEvents) (dispatchEvent :: handleTextEvent),
          sortedEvents.map((id, event) => {
            val name= event.getName
            val sizeName = eventSizeName(name)
            val entryName = eventEntryName(name)
            val historyName = eventHistoryName(name)
            functionClassMember(
              Some(s"Handle event $name"),
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
                  case Nil => lines(s"this->$sizeName++;")
                  case params => List.concat(
                    wrapInScope(
                      s"$entryName _e = {",
                      lines(params.map(_._1).mkString(",\n")),
                      "};"
                    ),
                    lines(s"$historyName->push_back(_e);")
                  )
                },
                lines("this->eventsSize++;")
              ),
              CppDoc.Function.Virtual
            )
          })
        )
      }
    )
  }

  private def getTlmFunctions: List[CppDoc.Class.Member] = {
    val switchStatement = wrapInSwitch(
      "id - idBase",
      intersperseBlankLines(
        sortedChannels.map((_, channel) =>
          wrapInScope(
            s"case $className::${channelIdConstantName(channel.getName)}: {",
            lines(
              s"""|${getChannelType(channel.channelType)} arg;
                  |const Fw::SerializeStatus _status = val.deserialize(arg);
                  |
                  |if (_status != Fw::FW_SERIALIZE_OK) {
                  |  printf("Error deserializing ${channel.getName}: %d\\n", _status);
                  |  return;
                  |}
                  |
                  |this->${tlmHandlerName(channel.getName)}(timeTag, arg);
                  |break;
                  |"""
            ),
            "}"
          )
        ) ++ List(
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

    addAccessTagAndComment(
      "protected",
      "Functions for testing telemetry",
      {
        lazy val dispatchTlm = functionClassMember(
          Some("Dispatch telemetry"),
          "dispatchTlm",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("FwChanIdType"),
              "id",
              Some("The channel id")
            ),
            timeTagParam,
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
            Line.blank :: switchStatement
          )
        )
        List.concat(
          guardedList (hasTelemetry) (List(dispatchTlm)),
          sortedChannels.map((_, channel) => {
            val channelType = getChannelType(channel.channelType)
            val entryName = tlmEntryName(channel.getName)
            val historyName = tlmHistoryName(channel.getName)
            functionClassMember(
              Some(s"Handle channel ${channel.getName}"),
              tlmHandlerName(channel.getName),
              List(
                timeTagParam,
                CppDoc.Function.Param(
                  CppDoc.Type(s"const $channelType&"),
                  "val",
                  Some("The channel value")
                )
              ),
              CppDoc.Type("void"),
              lines(
                s"""|$entryName e = { timeTag, val };
                    |this->$historyName->push_back(e);
                    |this->tlmSize++;
                    |"""
              )
            )
          })
        )
      }
    )
  }

  private def getTimeFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "protected",
      "Functions to test time",
      {
        lazy val setTestTime = functionClassMember(
          Some("Set the test time for events and telemetry"),
          "setTestTime",
          List(timeTagParam),
          CppDoc.Type("void"),
          lines("this->m_testTime = timeTag;")
        )
        guardedList (hasTimeGetPort) (List(setTestTime))
      }
    )

  private def getPrmFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Functions to test parameters",
      sortedParams.flatMap((_, prm) =>
        List(
          functionClassMember(
            Some(s"Set parameter ${prm.getName}"),
            paramSetName(prm.getName),
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
              s"""|this->${paramVariableName(prm.getName)} = val;
                  |this->${paramValidityFlagName(prm.getName)} = valid;
                  |"""
            )
          ),
          functionClassMember(
            Some(s"Send parameter ${prm.getName}"),
            paramSendName(prm.getName),
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
                  |  args.serialize(this->${paramVariableName(prm.getName)}) == Fw::FW_SERIALIZE_OK
                  |);
                  |
                  |const U32 idBase = this->getIdBase();
                  |FwOpcodeType _prmOpcode =  $className::${paramCommandConstantName(prm.getName, Command.Param.Set)} + idBase;
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
          ),
          functionClassMember(
            Some(s"Save parameter ${prm.getName}"),
            paramSaveName(prm.getName),
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
                  |const U32 idBase = this->getIdBase();
                  |FwOpcodeType _prmOpcode = $className::${paramCommandConstantName(prm.getName, Command.Param.Save)} + idBase;
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

  private def getPortStaticFunctions: List[CppDoc.Class.Member] = {

    val testerBaseDecl = s"$testerBaseClassName* _testerBase = static_cast<$testerBaseClassName*>(callComp);"

    def paramGetBody(id: String, value: String) = intersperseBlankLines(
      List(
        lines(testerBaseDecl),
        guardedList (hasParameters) (lines("Fw::SerializeStatus _status;")),
        lines(
          s"""|Fw::ParamValid _ret = Fw::ParamValid::VALID;
              |$value.resetSer();
              |
              |const U32 idBase = _testerBase->getIdBase();
              |FW_ASSERT($id >= idBase, $id, idBase);
              |"""
        ),
        wrapInSwitch(
          s"$id - idBase",
          intersperseBlankLines(
            sortedParams.map((_, prm) =>
              wrapInScope(
                s"case $className::${paramIdConstantName(prm.getName)}: {",
                lines(
                  s"""|_status = $value.serialize(_testerBase->${paramVariableName(prm.getName)});
                      |_ret = _testerBase->${paramValidityFlagName(prm.getName)};
                      |FW_ASSERT(
                      |  _status == Fw::FW_SERIALIZE_OK,
                      |  static_cast<FwAssertArgType>(_status)
                      |);
                      |break;
                      |"""
                ),
                "};"
              )
            ) ++ List(
              lines(
                """|default:
                   |  FW_ASSERT(id);
                   |  break;
                   |"""
              )
            )
          )
        ),
        lines("return _ret;")
      )
    )

    def paramSetBody(id: String, value: String) = List.concat(
      lines(s"$testerBaseDecl\n"),
      guardedList (hasParameters) (lines("Fw::SerializeStatus _status;")),
      lines(
        s"""|$value.resetSer();
            |
            |const U32 idBase = _testerBase->getIdBase();
            |FW_ASSERT($id >= idBase, $id, idBase);
            |"""
      ),
      Line.blank :: wrapInSwitch(
        s"$id - idBase",
        intersperseBlankLines(
          sortedParams.map((_, prm) =>
            wrapInScope(
              s"case $className::${paramIdConstantName(prm.getName)}: {",
              lines(
                s"""|${writeParamType(prm.paramType)} ${prm.getName}Val;
                    |_status = $value.deserialize(${prm.getName}Val);
                    |FW_ASSERT(
                    |  _status == Fw::FW_SERIALIZE_OK,
                    |  static_cast<FwAssertArgType>(_status)
                    |);
                    |FW_ASSERT(
                    |  ${prm.getName}Val ==
                    |  _testerBase->${paramVariableName(prm.getName)}
                    |);
                    |break;
                    |"""
              ),
              "};"
            )
          ) ++ List(
            lines(
              s"""|default:
                  |  FW_ASSERT($id);
                  |  break;
                  |"""
            )
          )
        )
      )
    )

    def getPortFunction(p: PortInstance) = {
      val params = getPortFunctionParams(p)
      lazy val paramNames = params.map(_.name).toVector
      lazy val paramNamesString = paramNames.mkString(", ")
      def getParamName(i: Int) = if (i >= 0 && i < paramNames.size)
        paramNames(i) else s"missingParam$i"
      lazy val body = p match {
        case i: PortInstance.General => List.concat(
          lines(
            s"""|FW_ASSERT(callComp);
                |$testerBaseDecl
                |"""
          ),
          writeFunctionCall(
            addReturnKeyword(s"_testerBase->${fromPortHandlerBaseName(p.getUnqualifiedName)}", i),
            List("portNum"),
            getPortParams(i).map(_._1)
          )
        )
        case PortInstance.Special(aNode, _, _, _, _) =>
          import Ast.SpecPortInstance._
          val spec @ Special(_, kind, _, _, _) = aNode._2.data
          kind match {
            case CommandRecv => Nil
            case CommandReg => Nil
            case CommandResp => lines(
              s"""|$testerBaseDecl
                  |_testerBase->cmdResponseIn($paramNamesString);
                  |"""
            )
            case Event => lines(
              s"""|$testerBaseDecl
                  |_testerBase->dispatchEvents($paramNamesString);
                  |"""
            )
            case ParamGet => paramGetBody(getParamName(0), getParamName(1))
            case ParamSet => paramSetBody(getParamName(0), getParamName(1))
            case Telemetry => lines(
              s"""|$testerBaseDecl
                  |_testerBase->dispatchTlm($paramNamesString);
                  |"""
            )
            case TextEvent => lines(
              s"""|$testerBaseDecl
                  |_testerBase->textLogIn($paramNamesString);
                  |"""
            )
            case TimeGet => lines(
              s"""|$testerBaseDecl
                  |${getParamName(0)} = _testerBase->m_testTime;
                  |"""
            )
          }
        case _: PortInstance.Internal => Nil
      }
      lazy val member = functionClassMember(
        Some(s"Static function for port ${portName(p)}"),
        fromPortCallbackName(p.getUnqualifiedName),
        List.concat(
          List(
            CppDoc.Function.Param(
              CppDoc.Type("Fw::PassiveComponentBase* const"),
              "callComp",
              Some("The component instance")
            ),
            portNumParam
          ),
          params
        ),
        getPortReturnTypeAsCppDocType(p),
        body,
        CppDoc.Function.Static
      )
      guardedList (portInstanceIsActive(p)) (List(member))
    }

    addAccessTagAndComment(
      "private",
      "Static functions for output ports",
      mapPorts(outputPorts, getPortFunction)
    )

  }

  private def getVariableMembers: List[CppDoc.Class.Member] = {
    List.concat(
      addAccessTagAndComment(
        "private",
        "To ports",
        inputPorts.map(p =>
          linesClassMember(
            Line.blank :: lines(
              s"""|//! To port connected to ${p.getUnqualifiedName}
                  |${getQualifiedPortTypeName(p, PortInstance.Direction.Output)} ${portVariableName(p)}[${p.getArraySize}];
                  |"""
            ),
            CppDoc.Lines.Hpp
          )
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "From ports",
        mapPorts(
          outputPorts,
          p => List(
            linesClassMember(
              Line.blank :: lines(
                s"""|//! From port connected to ${p.getUnqualifiedName}
                    |${getQualifiedPortTypeName(p, PortInstance.Direction.Input)} ${portVariableName(p)}[${p.getArraySize}];
                    |"""
              ),
              CppDoc.Lines.Hpp
            )
          ),
          CppDoc.Lines.Hpp
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "Parameter validity flags",
        sortedParams.map((_, prm) =>
          linesClassMember(
            Line.blank :: lines(
              s"""|//! True if parameter ${prm.getName} was successfully received
                  |Fw::ParamValid ${paramValidityFlagName(prm.getName)};
                  |"""
            ),
            CppDoc.Lines.Hpp
          )
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "Parameter variables",
        sortedParams.map((_, prm) =>
          linesClassMember(
            Line.blank :: lines(
              s"""|//! Parameter ${prm.getName}
                  |${writeParamType(prm.paramType)} ${paramVariableName(prm.getName)};
                  |"""
            ),
            CppDoc.Lines.Hpp
          )
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "Time variables",
        if hasTimeGetPort then List(
          linesClassMember(
            Line.blank :: lines(
              s"""|//! Test time stamp
                  |Fw::Time m_testTime;
                  |"""
            ),
            CppDoc.Lines.Hpp
          )
        )
        else Nil,
        CppDoc.Lines.Hpp
      ),
    )
  }

}
