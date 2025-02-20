package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component test harness base classes */
case class ComponentTesterBaseWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val className = componentClassName

  private val componentFileName = ComputeCppFiles.FileNames.getComponent(componentName)

  private val componentRelativeFileName = s.getRelativePath(componentFileName).toString

  private val fileName = ComputeCppFiles.FileNames.getComponentTesterBase(componentName)

  private val historyWriter = ComponentHistory(s, aNode)

  private val name = componentName

  private val namespaceIdentList = componentNamespaceIdentList

  private val relativeFileName = s.getRelativePath(fileName).toString

  private val symbol = componentSymbol

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

  private  def returnOrEmptyString(pi: PortInstance) =
    getPortReturnType(pi).map(_ => "return ").getOrElse("")

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
    val standardHeaders = List(
      s"$componentRelativeFileName.hpp",
      "Fw/Comp/PassiveComponentBase.hpp",
      "Fw/Port/InputSerializePort.hpp",
      "Fw/Types/Assert.hpp",
      "Fw/Types/ExternalString.hpp"
    )
    val dpHeaders = guardedList (hasDataProducts) (
      List("Fw/Dp/test/util/DpContainerHeader.hpp")
    )
    val userHeaders = List.concat(
      standardHeaders,
      dpHeaders
    ).sorted.map(CppWriter.headerString).map(line)
    val systemHeaders = lines(CppWriter.systemHeaderString("cstdio"))
    linesMember(
      List.concat(
        Line.blank :: systemHeaders,
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
      guardedList (hasDataProducts) (getDpFunctions),
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
        testerPortNumGetterName,
        testerPortVariableName,
        fromPortCallbackName,
        testerPortName,
        ComponentCppWriter.ConnectionSense.Reversed
      )
      guardedList (portInstanceIsUsed(port)) (code)
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
              CppDoc.Type("FwEnumStoreType"),
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
          "Fw::PassiveComponentBase(compName)" :: sortedParams.map(
            (_, param) => {
              val flagName = paramValidityFlagName(param.getName)
              s"$flagName(Fw::ParamValid::UNINIT)"
            }),
          {
            lazy val portHistories = line("// Initialize port histories") ::
              typedOutputPorts.filter(hasPortParams).map(p => {
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
            lazy val dpHistories = {
              lazy val productGetHistory = lines(
                "this->productGetHistory = new History<DpGet>(maxHistorySize);"
              )
              lazy val productRequestHistory = lines(
                "this->productRequestHistory = new History<DpRequest>(maxHistorySize);"
              )
              val productSendHistory = lines(
                "this->productSendHistory = new History<DpSend>(maxHistorySize);"
              )
              line("// Initialize data product histories") ::
              List.concat(
                guardedList (hasProductGetPort) (productGetHistory),
                guardedList (hasProductRequestPort) (productRequestHistory),
                productSendHistory
              )
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
                guardedList (hasDataProducts) (dpHistories),
                guardedList (hasHistories) (clearHistory)
              )
            )
          }
        ),
        destructorClassMember(
          Some(s"Destroy object $testerBaseClassName"),
          {
            lazy val destroyPortHistories = line("// Destroy port histories") ::
              typedOutputPorts.filter(hasPortParams).map(p => {
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
            lazy val destroyDpHistories = {
              lazy val destroyProductGetHistory = lines(
                "delete this->productGetHistory;"
              )
              lazy val destroyProductRequestHistory = lines(
                "delete this->productRequestHistory;"
              )
              val destroyProductSendHistory = lines(
                "delete this->productSendHistory;"
              )
              line("// Destroy data product histories") ::
              List.concat(
                guardedList (hasProductGetPort) (destroyProductGetHistory),
                guardedList (hasProductRequestPort) (destroyProductRequestHistory),
                destroyProductSendHistory
              )
            }
            intersperseBlankLines(
              List(
                guardedList (hasTypedOutputPorts) (destroyPortHistories),
                guardedList (hasCommands) (destroyCommandHistory),
                guardedList (hasEvents) (destroyEventHistories),
                guardedList (hasChannels) (destroyTlmHistories),
                guardedList (hasDataProducts) (destroyDpHistories)
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
      testerPortNumGetterName,
      testerPortVariableName
    )
  }

  private def getPortGetters: List[CppDoc.Class.Member] = {
    ComponentInputPorts(s, aNode).generateGetters(
      outputPorts,
      "from",
      inputPortName,
      fromPortGetterName,
      testerPortNumGetterName,
      testerPortVariableName
    )
  }

  /** Get the two port handler groups */
  private def getPortHandlers = List.concat(
    getPortHandlerGroup(typedOutputPorts),
    getPortHandlerGroup(serialOutputPorts)
  )

  /** Get a group of port handlers with a tag and comment */
  private def getPortHandlerGroup(ports: List[PortInstance]): List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "protected",
      s"""|Default handler implementations for ${getPortListTypeString(ports)} from ports
          |You can override these implementation with more specific behavior""",
      ports.map(getPortHandler)
    )

  /** Get a single port handler */
  private def getPortHandler(pi: PortInstance): CppDoc.Class.Member.Function = {
    val portName = pi.getUnqualifiedName
    val fromPortName = inputPortName(portName)
    val body = {
      // if needed, generate code to push values on the history
      val callOpt = portParamTypeMap.get(portName) match {
        // Handle a typed port with arguments
        case Some(pairs) =>
          val pushFunctionArgs = pairs.map(_._1).mkString(", ")
          val pushFunctionName = fromPortPushEntryName(portName)
          lines(s"this->$pushFunctionName($pushFunctionArgs);")
        // Handle a serial port
        case None => lines("// Default behavior is to do nothing")
      }
      // If needed, generate a return statement.
      // In the default implementation, we return the default value
      // for the return type.
      val returnOpt = getPortReturnTypeSemantic(pi) match {
        case Some(ty) =>
          val defaultValue = ValueCppWriter.write(s, ty.getDefaultValue.get)
          lines(s"return $defaultValue;")
        case None => Nil
      }
      List.concat(callOpt, returnOpt)
    }
    functionClassMember(
      Some(s"Default handler implementation for $fromPortName"),
      fromPortHandlerName(portName),
      portNumParam :: getPortFunctionParams(pi),
      getPortReturnTypeAsCppDocType(pi),
      body,
      CppDoc.Function.Virtual
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
        val handlerName = fromPortHandlerName(p.getUnqualifiedName)
        val handlerCall = writeFunctionCall(
          s"${returnOrEmptyString(p)}this->$handlerName",
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
                  |  portNum < this->${testerPortNumGetterName(p)}(),
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
      List.concat(typedInputPorts, serialInputPorts).map(p => {
        val invokeFunction = p.getType.get match {
          case PortInstance.Type.DefPort(_) => "invoke"
          case PortInstance.Type.Serial => "invokeSerial"
        }
        val invokeCall = {
          val variableName = testerPortVariableName(p)
          writeFunctionCall(
            s"${returnOrEmptyString(p)}this->$variableName[portNum].$invokeFunction",
            Nil,
            getPortParams(p).map(_._1)
          )
        }

        functionClassMember(
          Some(s"Invoke the to port connected to ${p.getUnqualifiedName}"),
          toPortInvokerName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getPortReturnTypeAsCppDocType(p),
          List.concat(
            lines(
              s"""|// Make sure port number is valid
                  |FW_ASSERT(
                  |  portNum < this->${testerPortNumGetterName(p)}(),
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
        testerPortName,
        testerPortNumGetterName,
        testerPortVariableName
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
        testerPortNumGetterName,
        testerPortVariableName
      )
    )
  }

  private def getDpFunctions: List[CppDoc.Class.Member] = {
    lazy val pushProductGet = functionClassMember(
      Some("Push an entry on the product get history"),
      "pushProductGetEntry",
      getPortFunctionParams(productGetPort.get).take(2),
      CppDoc.Type("void"),
      lines(
        """|DpGet e = { id, dataSize };
           |this->productGetHistory->push_back(e);"""
      )
    )
    lazy val handleProductGet = functionClassMember(
      Some(
        """|Handle a data product get from the component under test
           |
           |By default, (1) call pushProductGetEntry; (2) do not allocate a buffer
           |and return FAILURE. You can override this behavior, e.g., to call
           |pushProductGetEntry, allocate a buffer and return SUCCESS.""".stripMargin
      ),
      "productGet_handler",
      getPortFunctionParams(productGetPort.get),
      CppDoc.Type("Fw::Success::T"),
      lines(
        """|(void) buffer;
           |this->pushProductGetEntry(id, dataSize);
           |return Fw::Success::FAILURE;
           |"""
      ),
      CppDoc.Function.Virtual
    )
    lazy val pushProductRequest = functionClassMember(
      Some("Push an entry on the product request history"),
      "pushProductRequestEntry",
      getPortFunctionParams(productRequestPort.get),
      CppDoc.Type("void"),
      lines(
        """|DpRequest e = { id, dataSize };
           |this->productRequestHistory->push_back(e);"""
      )
    )
    lazy val handleProductRequest = functionClassMember(
      Some(
        """|Handle a data product request from the component under test
           |
           |By default, call pushProductRequestEntry. You can override
           |this behavior.""".stripMargin
      ),
      "productRequest_handler",
      getPortFunctionParams(productRequestPort.get),
      CppDoc.Type("void"),
      lines("this->pushProductRequestEntry(id, dataSize);"),
      CppDoc.Function.Virtual
    )
    lazy val sendProductResponse = functionClassMember(
      Some(s"Send a data product response to the component under test"),
      "sendProductResponse",
      getPortFunctionParams(productRecvPort.get),
      CppDoc.Type("void"),
      {
        val pi = productRecvPort.get
        val portNumGetter = testerPortNumGetterName(pi)
        val varName = testerPortVariableName(pi)
        lines(
          s"""|FW_ASSERT(this->$portNumGetter() > 0);
              |FW_ASSERT(this->$varName[0].isConnected());
              |this->$varName[0].invoke(id, buffer, status);"""
        )
      }
    )
    lazy val pushProductSend = functionClassMember(
      Some("Push an entry on the product send history"),
      "pushProductSendEntry",
      getPortFunctionParams(productSendPort.get),
      CppDoc.Type("void"),
      lines(
        """|DpSend e = { id, buffer };
           |this->productSendHistory->push_back(e);"""
      )
    )
    lazy val handleProductSend = functionClassMember(
      Some(
        """|Handle a data product send from the component under test
           |
           |By default, call pushProductSendEntry. You can override
           |this behavior.""".stripMargin
      ),
      "productSend_handler",
      getPortFunctionParams(productSendPort.get),
      CppDoc.Type("void"),
      lines("this->pushProductSendEntry(id, buffer);"),
      CppDoc.Function.Virtual
    )
    addAccessTagAndComment(
      "protected",
      "Functions for testing data products",
      {
        List.concat(
          guardedList (hasProductGetPort) (
            List(pushProductGet, handleProductGet)
          ),
          guardedList (hasProductRequestPort) (
            List(pushProductRequest, handleProductRequest)
          ),
          guardedList (hasProductRecvPort) (List(sendProductResponse)),
          List(pushProductSend, handleProductSend)
        )
      }
    )
  }

  private def getCmdFunctions: List[CppDoc.Class.Member] = {
    val cmdPortInvocation = {
      val varName = testerPortVariableName(cmdRecvPort.get)
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

    def writeCmdSendFunc(opcode: Command.Opcode, cmd: Command.NonParam) = functionClassMember(
      Some(s"Send a ${cmd.getName} command"),
      commandSendName(cmd.getName),
      List.concat(
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const FwEnumStoreType"),
            "instance",
            Some("The instance number")
          ),
          cmdSeqParam
        ),
        getNonParamCmdFormalParams(cmd, "Fw::StringBase")
      ),
      CppDoc.Type("void"),
      {
        val cmdFormalParams = cmd.aNode._2.data.params
        intersperseBlankLines(
          List(
            if cmdFormalParams.isEmpty then lines("Fw::CmdArgBuffer buf;")
            else List.concat(
              lines(
                """|// Serialize arguments
                   |Fw::CmdArgBuffer buf;
                   |Fw::SerializeStatus _status;
                   |"""
              ),
              Line.blank :: intersperseBlankLines(
                cmdFormalParams.map(param => {
                  val t = s.a.typeMap(param._2.data.typeName.id)
                  val varName = param._2.data.name
                  t match {
                    case ts: Type.String =>
                      lines(
                        s"""|_status = $varName.serialize(buf, FW_CMD_STRING_MAX_SIZE);
                            |FW_ASSERT(
                            |  _status == Fw::FW_SERIALIZE_OK,
                            |  static_cast<FwAssertArgType>(_status)
                            |);
                            |"""
                      )
                    case _ =>
                      lines(
                        s"""|_status = buf.serialize($varName);
                            |FW_ASSERT(
                            |  _status == Fw::FW_SERIALIZE_OK,
                            |  static_cast<FwAssertArgType>(_status)
                            |);
                            |"""
                      )
                  }
                })
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
      }
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
                    s"""|FW_ASSERT(
                        |  _numArgs == $length + 1,
                        |  static_cast<FwAssertArgType>(_numArgs),
                        |  static_cast<FwAssertArgType>($length + 1)
                        |);
                        |
                        |// For FATAL, there is a stack size of 4 and a dummy entry
                        |U8 stackArgLen;
                        |_status = args.deserialize(stackArgLen);
                        |FW_ASSERT(
                        |    _status == Fw::FW_SERIALIZE_OK,
                        |    static_cast<FwAssertArgType>(_status)
                        |);
                        |FW_ASSERT(
                        |  stackArgLen == 4,
                        |  static_cast<FwAssertArgType>(stackArgLen)
                        |);
                        |
                        |U32 dummyStackArg;
                        |_status = args.deserialize(dummyStackArg);
                        |FW_ASSERT(
                        |    _status == Fw::FW_SERIALIZE_OK,
                        |    static_cast<FwAssertArgType>(_status)
                        |);
                        |FW_ASSERT(
                        |  dummyStackArg == 0,
                        |  static_cast<FwAssertArgType>(dummyStackArg)
                        |);
                        |"""
                  )
                  case _ => lines(
                    s"""|FW_ASSERT(
                        |  _numArgs == $length,
                        |  static_cast<FwAssertArgType>(_numArgs),
                        |  static_cast<FwAssertArgType>($length)
                        |);"""
                  )
                }
              },
              lines("#endif")
            )
          },
          event.aNode._2.data.params.flatMap(aNode => {
            val data = aNode._2.data
            val name = s"_event_arg_${data.name}"
            val tn = writeFormalParamType(data, "Fw::LogStringArg")
            val paramType = s.a.typeMap(data.typeName.id)
            val serializedSizeExpr = writeSerializedSizeExpr(s, paramType, tn)

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
                  |  FW_ASSERT(
                  |    _argSize == $serializedSizeExpr,
                  |    static_cast<FwAssertArgType>(_argSize),
                  |    static_cast<FwAssertArgType>($serializedSizeExpr)
                  |  );
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
            val paramString = params.map(p => s"_event_arg_${p._1}").mkString(", ")
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
      "id - idBase",
      intersperseBlankLines(
        sortedEvents.map((id, event) => writeSwitchCase(id, event)) ++ List(
          lines(
            """|default: {
               |  FW_ASSERT(0, static_cast<FwAssertArgType>(id));
               |  break;
               |}
               |"""
          )
        )
      )
    )

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
             |FW_ASSERT(
             |  id >= idBase,
             |  static_cast<FwAssertArgType>(id),
             |  static_cast<FwAssertArgType>(idBase)
             |);
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
        ),
        CppDoc.Function.Virtual
      )
    )

    def handleEvent(id: Event.Id, event: Event) = {
      val name= event.getName
      val sizeName = eventSizeName(name)
      val entryName = eventEntryName(name)
      val historyName = eventHistoryName(name)
      functionClassMember(
        Some(s"Handle event $name"),
        eventHandlerName(event),
        formalParamsCppWriter.write(
          event.aNode._2.data.params,
          "Fw::StringBase",
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
    }

    addAccessTagAndComment(
      "protected",
      "Functions for testing events",
      List.concat(
        guardedList (hasEvents) (dispatchEvent :: handleTextEvent),
        sortedEvents.map(handleEvent)
      )
    )

  }

  private def getTlmFunctions: List[CppDoc.Class.Member] = {
    val switchStatement = wrapInSwitch(
      "id - idBase",
      intersperseBlankLines(
        sortedChannels.map((_, channel) => {
          val channelName = channel.getName
          val constantName = channelIdConstantName(channelName)
          val handlerName = tlmHandlerName(channelName)
          val channelType = writeChannelType(channel.channelType, "Fw::TlmString")
          wrapInScope(
            s"case $className::$constantName: {",
            lines(
              s"""|$channelType arg;
                  |const Fw::SerializeStatus _status = val.deserialize(arg);
                  |
                  |if (_status != Fw::FW_SERIALIZE_OK) {
                  |  printf("Error deserializing $channelName: %d\\n", _status);
                  |  return;
                  |}
                  |
                  |this->$handlerName(timeTag, arg);
                  |break;
                  |"""
            ),
            "}"
          )}
        ) ++ List(
          wrapInScope(
            "default: {",
            lines(
              """|FW_ASSERT(0, static_cast<FwAssertArgType>(id));
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
                 |FW_ASSERT(
                 |  id >= idBase,
                 |  static_cast<FwAssertArgType>(id),
                 |  static_cast<FwAssertArgType>(idBase)
                 |);
                 |"""
            ),
            Line.blank :: switchStatement
          )
        )
        List.concat(
          guardedList (hasTelemetry) (List(dispatchTlm)),
          sortedChannels.map((_, channel) => {
            val channelName = channel.getName
            val channelType = writeCppType(channel.channelType, Some("Fw::StringBase"))
            val entryName = tlmEntryName(channelName)
            val historyName = tlmHistoryName(channelName)
            functionClassMember(
              Some(s"Handle channel $channelName"),
              tlmHandlerName(channelName),
              List(
                timeTagParam,
                CppDoc.Function.Param(
                  CppDoc.Type(channelType),
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

  private def getPrmFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "protected",
      "Functions to test parameters",
      sortedParams.flatMap((_, prm) => {
        val paramName = prm.getName
        val paramType = writeParamType(prm.paramType)
        val paramVarName = paramVariableName(paramName)
        val paramValidityFlag = paramValidityFlagName(paramName)
        List(
          functionClassMember(
            Some(s"Set parameter $paramName"),
            paramSetName(prm.getName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type(s"const $paramType&"),
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
              s"""|this->$paramVarName = val;
                  |this->$paramValidityFlag = valid;
                  |"""
            )
          ),
          functionClassMember(
            Some(s"Send parameter $paramName"),
            paramSendName(prm.getName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("FwEnumStoreType"),
                "instance",
                Some("The component instance")
              ),
              cmdSeqParam
            ),
            CppDoc.Type("void"),
            {
              val paramVar = paramVariableName(prm.getName)
              val constantName = paramCommandConstantName(prm.getName, Command.Param.Set)
              val portVar = testerPortVariableName(cmdRecvPort.get)
              lines(
                s"""|// Build command for parameter set
                    |Fw::CmdArgBuffer args;
                    |FW_ASSERT(
                    |  args.serialize(this->$paramVar) == Fw::FW_SERIALIZE_OK
                    |);
                    |
                    |const U32 idBase = this->getIdBase();
                    |FwOpcodeType _prmOpcode =  $className::$constantName + idBase;
                    |
                    |if (not this->$portVar[0].isConnected()) {
                    |  printf("Test Command Output port not connected!\\n");
                    |}
                    |else {
                    |  this->$portVar[0].invoke(
                    |    _prmOpcode,
                    |    cmdSeq,
                    |    args
                    |  );
                    |}
                    |"""
              )
            }
          ),
          functionClassMember(
            Some(s"Save parameter ${prm.getName}"),
            paramSaveName(prm.getName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("FwEnumStoreType"),
                "instance",
                Some("The component instance")
              ),
              cmdSeqParam
            ),
            CppDoc.Type("void"),
            {
              val constantName = paramCommandConstantName(prm.getName, Command.Param.Save)
              val varName = testerPortVariableName(cmdRecvPort.get)
            lines(
              s"""|Fw::CmdArgBuffer args;
                  |const U32 idBase = this->getIdBase();
                  |FwOpcodeType _prmOpcode = $className::$constantName + idBase;
                  |
                  |if (not this->$varName[0].isConnected()) {
                  |  printf("Test Command Output port not connected!\\n");
                  |}
                  |else {
                  |  this->$varName[0].invoke(
                  |    _prmOpcode,
                  |    cmdSeq,
                  |    args
                  |  );
                  |}
                  |"""
            )
            }
          )
        )
      })
    )

  private def getPortStaticFunctions: List[CppDoc.Class.Member] = {

    val testerBaseDecl =
      s"$testerBaseClassName* _testerBase = static_cast<$testerBaseClassName*>(callComp);"

    def paramGetBody(id: String, value: String) = intersperseBlankLines(
      List(
        lines(testerBaseDecl),
        guardedList (hasParameters) (lines("Fw::SerializeStatus _status;")),
        lines(
          s"""|Fw::ParamValid _ret = Fw::ParamValid::VALID;
              |$value.resetSer();
              |
              |const U32 idBase = _testerBase->getIdBase();
              |FW_ASSERT(
              |  $id >= idBase,
              |  static_cast<FwAssertArgType>($id),
              |  static_cast<FwAssertArgType>(idBase)
              |);
              |"""
        ),
        wrapInSwitch(
          s"$id - idBase",
          intersperseBlankLines(
            sortedParams.map((_, prm) => {
              val prmName = prm.getName
              val idConstantName = paramIdConstantName(prmName)
              val varName = paramVariableName(prmName)
              val validityFlagName = paramValidityFlagName(prmName)
              wrapInScope(
                s"case $className::$idConstantName: {",
                lines(
                  s"""|_status = $value.serialize(_testerBase->$varName);
                      |_ret = _testerBase->$validityFlagName;
                      |FW_ASSERT(
                      |  _status == Fw::FW_SERIALIZE_OK,
                      |  static_cast<FwAssertArgType>(_status)
                      |);
                      |break;
                      |"""
                ),
                "};"
              )}
            ) ++ List(
              lines(
                """|default:
                   |  FW_ASSERT(0, static_cast<FwAssertArgType>(id));
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
            |FW_ASSERT(
            |  $id >= idBase,
            |  static_cast<FwAssertArgType>($id),
            |  static_cast<FwAssertArgType>(idBase)
            |);
            |"""
      ),
      Line.blank :: wrapInSwitch(
        s"$id - idBase",
        intersperseBlankLines(
          sortedParams.map((_, prm) => {
            val paramName = prm.getName
            val idConstantName = paramIdConstantName(paramName)
            val paramNameVal = s"${paramName}Val"
            val paramVarName = paramVariableName(paramName)
            val paramType = writeParamType(prm.paramType, "Fw::ParamString")
            wrapInScope(
              s"case $className::$idConstantName: {",
              lines(
                s"""|$paramType $paramNameVal;
                    |_status = $value.deserialize($paramNameVal);
                    |FW_ASSERT(
                    |  _status == Fw::FW_SERIALIZE_OK,
                    |  static_cast<FwAssertArgType>(_status)
                    |);
                    |FW_ASSERT(
                    |  $paramNameVal ==
                    |  _testerBase->$paramVarName
                    |);
                    |break;
                    |"""
              ),
              "};"
            )
          }) ++ List(
            lines(
              s"""|default:
                  |  FW_ASSERT(0, static_cast<FwAssertArgType>($id));
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
        case i: PortInstance.General =>
          val baseName = fromPortHandlerBaseName(p.getUnqualifiedName)
          List.concat(
            lines(
              s"""|FW_ASSERT(callComp != nullptr);
                  |$testerBaseDecl
                  |"""
            ),
            writeFunctionCall(
              addReturnKeyword(s"_testerBase->$baseName", i),
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
            case ProductGet => lines(
              s"""|$testerBaseDecl
                  |return _testerBase->productGet_handler($paramNamesString);
                  |"""
            )
            case ProductRecv => Nil
            case ProductRequest => lines(
              s"""|$testerBaseDecl
                  |_testerBase->productRequest_handler($paramNamesString);
                  |"""
            )
            case ProductSend => lines(
              s"""|$testerBaseDecl
                  |_testerBase->productSend_handler($paramNamesString);
                  |"""
            )
          }
        case _: PortInstance.Internal => Nil
      }
      lazy val member = functionClassMember(
        Some(s"Static function for port ${testerPortName(p)}"),
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
      guardedList (portInstanceIsUsed(p)) (List(member))
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
        inputPorts.map(p => {
          val unqualifiedName = p.getUnqualifiedName
          val qualifiedName = getQualifiedPortTypeName(
            p, PortInstance.Direction.Output
          )
          val varName = testerPortVariableName(p)
          val arraySize = p.getArraySize
          linesClassMember(
            Line.blank :: lines(
              s"""|//! To port connected to ${p.getUnqualifiedName}
                  |$qualifiedName $varName[$arraySize];
                  |"""
            ),
            CppDoc.Lines.Hpp
          )
        }),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "From ports",
        mapPorts(
          outputPorts,
          p => {
            val unqualifiedName = p.getUnqualifiedName
            val qualifiedName = getQualifiedPortTypeName(
              p,
              PortInstance.Direction.Input
            )
            val varName = testerPortVariableName(p)
            val arraySize = p.getArraySize
            List(
              linesClassMember(
                Line.blank :: lines(
                  s"""|//! From port connected to $unqualifiedName
                      |$qualifiedName $varName[$arraySize];
                      |"""
                ),
                CppDoc.Lines.Hpp
              )
            )
          },
          CppDoc.Lines.Hpp
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "Parameter validity flags",
        sortedParams.map((_, prm) => {
          val paramName = prm.getName
          val validityFlagName = paramValidityFlagName(paramName)
          linesClassMember(
            Line.blank :: lines(
              s"""|//! True if parameter $paramName was successfully received
                  |Fw::ParamValid $validityFlagName;
                  |"""
            ),
            CppDoc.Lines.Hpp
          )
        }),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "Parameter variables",
        sortedParams.map((_, prm) => {
          val paramName = prm.getName
          val paramType = writeParamType(prm.paramType, "Fw::ParamString")
          val varName = paramVariableName(paramName)
          linesClassMember(
            Line.blank :: lines(
              s"""|//! Parameter $paramName
                  |$paramType $varName;
                  |"""
            ),
            CppDoc.Lines.Hpp
          )
        }),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "Time variables",
        guardedList (hasTimeGetPort) (
          List(
            linesClassMember(
              Line.blank :: lines(
                s"""|//! Test time stamp
                    |Fw::Time m_testTime;
                    |"""
              ),
              CppDoc.Lines.Hpp
            )
          )
        ),
        CppDoc.Lines.Hpp
      ),
    )
  }

}
