package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component definitions */
case class ComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponent(name)

  private val dpWriter = ComponentDataProducts(s, aNode)

  private val portWriter = ComponentPorts(s, aNode)

  private val cmdWriter = ComponentCommands(s, aNode)

  private val internalPortWriter = ComponentInternalPort(s, aNode)

  private val eventWriter = ComponentEvents(s, aNode)

  private val tlmWriter = ComponentTelemetry(s, aNode)

  private val paramWriter = ComponentParameters(s, aNode)

  private val stateMachineWriter = ComponentStateMachines(s, aNode)

  private val kindStr = data.kind match {
    case Ast.ComponentKind.Active => "Active"
    case Ast.ComponentKind.Passive => "Passive"
    case Ast.ComponentKind.Queued => "Queued"
  }

  private val baseClassName = s"${kindStr}ComponentBase"

  private val exitConstantName = s"${name.toUpperCase}_COMPONENT_EXIT"

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defComponentAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component base class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val smInterfaces = stateMachineWriter.getSmInterfaces
    val cls = classMember(
      Some(
        addSeparatedString(
          s"\\class $className\n\\brief Auto-generated base for $name component",
          AnnotationCppWriter.asStringOpt(aNode)
        )
      ),
      className,
      Some(s"public Fw::$baseClassName$smInterfaces"),
      getClassMembers
    )
    List(
      List(hppIncludes, cppIncludes),
      getStaticAssertion,
      wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    // Conditional headers
    val dpHeaders =
      guardedList (hasDataProducts) (List("Fw/Dp/DpContainer.hpp"))
    val mutexHeaders =
      guardedList (hasGuardedInputPorts || hasGuardedCommands || hasParameters) (
        List("Os/Mutex.hpp")
      )
    val cmdStrHeaders =
      guardedList (hasCommands || hasParameters) (List("Fw/Cmd/CmdString.hpp"))
    val tlmStrHeaders =
      guardedList (hasChannels) (List("Fw/Tlm/TlmString.hpp"))
    val prmStrHeaders =
      guardedList (hasParameters) (List("Fw/Prm/PrmString.hpp"))
    val logStrHeaders =
      guardedList (hasEvents) (List("Fw/Log/LogString.hpp"))
    val internalStrHeaders =
      guardedList (hasInternalPorts) (List("Fw/Types/InternalInterfaceString.hpp"))
    val stateMachineEventHeaders =
      guardedList (hasStateMachineInstances) (List("Fw/Types/SMSignalsSerializableAc.hpp"))

    val standardHeaders = List.concat(
      List(
        "FpConfig.hpp",
        "Fw/Port/InputSerializePort.hpp",
        "Fw/Port/OutputSerializePort.hpp",
        "Fw/Comp/ActiveComponentBase.hpp"
      ),
      dpHeaders,
      mutexHeaders,
      cmdStrHeaders,
      tlmStrHeaders,
      prmStrHeaders,
      logStrHeaders,
      internalStrHeaders,
      stateMachineEventHeaders
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.sorted.flatMap({
      case s: "#include \"Fw/Log/LogTextPortAc.hpp\"" =>
        lines(
          s"""|#if FW_ENABLE_TEXT_LOGGING == 1
              |$s
              |#endif
              |""".stripMargin
        )
      case s => lines(s)
    })))
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/ExternalString.hpp",
      "Fw/Types/String.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp"
    ).sorted.map(CppWriter.headerString).flatMap({
      case s: "#include \"Fw/Types/String.hpp\"" =>
        lines(
          s"""|#if FW_ENABLE_TEXT_LOGGING
              |$s
              |#endif
              |""".stripMargin
        )
      case s => lines(s)
    })
    linesMember(Line.blank :: userHeaders, CppDoc.Lines.Cpp)
  }

  private def getStaticAssertion: List[CppDoc.Member] = {
    if serialInputPorts.isEmpty && serialOutputPorts.isEmpty then Nil
    else List(
      linesMember(
        Line.blank :: lines(
          s"""|static_assert(
              |  FW_PORT_SERIALIZATION == 1,
              |  \"$name component requires serialization\"
              |);
              |"""
        )
      )
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = {
    List.concat(
      // Friend classes
      getFriendClassMembers,

      // Constants
      getConstantMembers,

      // Anonymous namespace members
      getAnonymousNamespaceMembers,

      // Types
      dpWriter.getTypeMembers,

      // Public function members
      getPublicComponentFunctionMembers,
      portWriter.getPublicFunctionMembers,
      cmdWriter.getPublicFunctionMembers,
      paramWriter.getPublicFunctionMembers,

      // Protected function members
      getProtectedComponentFunctionMembers,
      portWriter.getProtectedFunctionMembers,
      internalPortWriter.getFunctionMembers,
      stateMachineWriter.getFunctionMembers,
      cmdWriter.getProtectedFunctionMembers,
      eventWriter.getFunctionMembers,
      tlmWriter.getFunctionMembers,
      paramWriter.getProtectedFunctionMembers,
      dpWriter.getProtectedDpFunctionMembers,
      dpWriter.getVirtualFunctionMembers,
      getTimeFunctionMember,
      getMutexOperationMembers,

      // Protected/private function members
      getDispatchFunctionMember,

      // Private function members
      portWriter.getPrivateFunctionMembers,
      paramWriter.getPrivateFunctionMembers,
      dpWriter.getPrivateDpFunctionMembers,

      // Member variables
      portWriter.getVariableMembers,
      eventWriter.getVariableMembers,
      tlmWriter.getVariableMembers,
      paramWriter.getVariableMembers,
      stateMachineWriter.getVariableMembers,
      getMsgSizeVariableMember,
      getMutexVariableMembers,
    )
  }

  private def getConstantMembers: List[CppDoc.Class.Member] = {
    val constants = List(
      portWriter.getConstantMembers,
      cmdWriter.getConstantMembers,
      eventWriter.getConstantMembers,
      tlmWriter.getConstantMembers,
      paramWriter.getConstantMembers,
      dpWriter.getConstantMembers,
      stateMachineWriter.getConstantMembers
    ).flatten

    if constants.isEmpty then Nil
    else List(
      List(
        linesClassMember(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              "Constants"
            ),
          ).flatten
        )
      ),
      constants
    ).flatten
  }

  private def getFriendClassMembers: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
        List(
          CppDocWriter.writeBannerComment(
            "Friend classes"
          ),
          lines(
            s"""|
                |//! Friend class for white-box testing
                |friend class ${className}Friend;
                |"""
          )
        ).flatten
      )
    )
  }

  private def getAnonymousNamespaceMembers: List[CppDoc.Class.Member] =
    data.kind match {
      case Ast.ComponentKind.Passive => Nil
      case _ => {
        val buffUnion = getBuffUnion
        List(
          linesClassMember(
            Line.blank :: wrapInAnonymousNamespace(
              intersperseBlankLines(
                List(
                  getMsgTypeEnum,
                  buffUnion,
                  getComponentIpcSerializableBufferClass(buffUnion)
                )
              )
            ),
            CppDoc.Lines.Cpp
          )
        )
      }
    }

  private def getMsgTypeEnum: List[Line] = {
    wrapInScope(
      "enum MsgTypeEnum {",
      List.concat(
        lines(s"$exitConstantName = Fw::ActiveComponentBase::ACTIVE_COMPONENT_EXIT"),
        dataProductAsyncInputPorts.map(portCppConstantName),
        typedAsyncInputPorts.map(portCppConstantName),
        serialAsyncInputPorts.map(portCppConstantName),
        asyncCmds.map((_, cmd) => commandCppConstantName(cmd)),
        internalPorts.map(internalPortCppConstantName),
        guardedList (hasStateMachineInstances) (List(stateMachineCppConstantName))
      ).map(s => line(s"$s,")),
      "};"
    )
  }

  /** Generates a union type that lets the compiler calculate
   *  the max serialized size of any list of arguments that goes
   *  on the queue */
  private def getBuffUnion: List[Line] = {
    // Collect the serialized sizes of all the async port arguments
    // For each one, add a byte array of that size as a member
    val internalPortsWithFormalParams: List[PortInstance.Internal] =
      internalPorts.filter(p => getPortParams(p).size > 0)
    val asyncInputPortsWithFormalParams =
      (dataProductAsyncInputPorts ++ typedAsyncInputPorts).
        filter(p => getPortParams(p).size > 0)
    val members = List.concat(
      // Data product and typed async input ports
      asyncInputPortsWithFormalParams.flatMap(p => {
        val portName = p.getUnqualifiedName
        val portTypeName = getQualifiedPortTypeName(p, p.getDirection.get)
        lines(s"BYTE ${portName}PortSize[${portTypeName}::SERIALIZED_SIZE];")
      }),
      // Command input port
      guardedList (cmdRecvPort.isDefined)
        (lines(s"BYTE cmdPortSize[Fw::InputCmdPort::SERIALIZED_SIZE];")),
      // Internal ports
      // Sum the sizes of the port arguments
      internalPortsWithFormalParams.flatMap(p =>
        line(s"// Size of ${p.getUnqualifiedName} argument list") ::
        wrapInScope(
          s"BYTE ${p.getUnqualifiedName}IntIfSize[",
          lines(
            p.aNode._2.data.params.map(param =>
              writeSerializedSizeExpr(
                s,
                s.a.typeMap(param._2.data.typeName.id),
                writeInternalPortParamType(param._2.data)
              )
            ).mkString(" +\n")
          ),
          "];"
        )
      ),
      guardedList (hasStateMachineInstances) (
        lines(
          s"""|// Size of statemachine sendSignals
              |BYTE sendSignalsStatemachineSize[
              |  Fw::SMSignals::SERIALIZED_SIZE
              |];
              |"""
        )
      )
    )
    wrapInScope(
      """|// Get the max size by constructing a union of the async input, command, and
         |// internal port serialization sizes
         |union BuffUnion {""",
      members,
      "};"
    )
  }

  private def getComponentIpcSerializableBufferClass(buffUnion: List[Line]): List[Line] = {
    lines(
      s"""|// Define a message buffer class large enough to handle all the
          |// asynchronous inputs to the component
          |class ComponentIpcSerializableBuffer :
          |  public Fw::SerializeBufferBase
          |{
          |
          |  public:
          |
          |    enum {
          |      // Max. message size = size of data + message id + port
          |      SERIALIZATION_SIZE =${if (buffUnion.nonEmpty) """
          |        sizeof(BuffUnion) +""" else "" }
          |        sizeof(FwEnumStoreType) +
          |        sizeof(FwIndexType)
          |    };
          |
          |    Fw::Serializable::SizeType getBuffCapacity() const {
          |      return sizeof(m_buff);
          |    }
          |
          |    U8* getBuffAddr() {
          |      return m_buff;
          |    }
          |
          |    const U8* getBuffAddr() const {
          |      return m_buff;
          |    }
          |
          |  private:
          |    // Should be the max of all the input ports serialized sizes...
          |    U8 m_buff[SERIALIZATION_SIZE];
          |
          |};
          |"""
    )
  }

  private def getPublicComponentFunctionMembers: List[CppDoc.Class.Member] = {
    def writePortConnections(port: PortInstance) =
      ComponentCppWriter.writePortConnections(
        port,
        portNumGetterName,
        portVariableName,
        inputPortCallbackName,
        (p: PortInstance) => s"${p.getUnqualifiedName}_${p.getDirection.get.toString.capitalize}Port"
      )

    val body = intersperseBlankLines(
      List(
        lines(
          s"""|// Initialize base class
              |Fw::$baseClassName::init(instance);
              |"""
        ),
        smInstancesByName.map((name, _) => line(s"m_stateMachine_$name.init(STATE_MACHINE_${name.toUpperCase});")),
        intersperseBlankLines(specialInputPorts.map(writePortConnections)),
        intersperseBlankLines(typedInputPorts.map(writePortConnections)),
        intersperseBlankLines(serialInputPorts.map(writePortConnections)),
        intersperseBlankLines(specialOutputPorts.map(writePortConnections)),
        intersperseBlankLines(typedOutputPorts.map(writePortConnections)),
        intersperseBlankLines(serialOutputPorts.map(writePortConnections)),
        data.kind match {
          case Ast.ComponentKind.Passive => Nil
          case _ => List.concat(
            if hasSerialAsyncInputPorts then lines(
              """|// Passed-in size added to port number and message type enumeration sizes.
                 |this->m_msgSize = FW_MAX(
                 |  msgSize +
                 |  static_cast<FwSizeType>(sizeof(FwIndexType)) +
                 |  static_cast<FwSizeType>(sizeof(FwEnumStoreType)),
                 |  static_cast<FwSizeType>(ComponentIpcSerializableBuffer::SERIALIZATION_SIZE)
                 |);
                 |
                 |Os::Queue::QueueStatus qStat = this->createQueue(queueDepth, this->m_msgSize);
                 |"""
            )
            else lines(
              """|Os::Queue::QueueStatus qStat = this->createQueue(
                 |  queueDepth,
                 |  ComponentIpcSerializableBuffer::SERIALIZATION_SIZE
                 |);
                 |"""
            ),
            lines(
              """|FW_ASSERT(
                 |  Os::Queue::QUEUE_OK == qStat,
                 |  static_cast<FwAssertArgType>(qStat)
                 |);
                 |"""
            )
          )
        }
      )
    )

    addAccessTagAndComment(
      "public",
      "Component initialization",
      List(
        functionClassMember(
          Some(s"Initialize $className object"),
          "init",
          initParams,
          CppDoc.Type("void"),
          body
        )
      )
    )
  }

  private def getProtectedComponentFunctionMembers: List[CppDoc.Class.Member] = {
    def writeChannelInit(channel: TlmChannel) = {
      List(
        lines(
          s"""|// Write telemetry channel ${channel.getName}
              |this->${channelUpdateFlagName(channel.getName)} = true;
              |"""
        ),
        channel.channelType match {
          case t if s.isPrimitive(t, writeChannelType(t)) => lines(
            s"this->${channelStorageName(channel.getName)} = 0;"
          )
          case _ => Nil
        }
      ).flatten
    }

    addAccessTagAndComment(
      "PROTECTED",
      "Component construction and destruction",
      List(
        constructorClassMember(
          Some(s"Construct $className object"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const char*"),
              "compName",
              Some("The component name"),
              Some("\"\"")
            )
          ),
          s"Fw::${kindStr}ComponentBase(compName)" :: smInstancesByName.map((name, _) => s"m_stateMachine_$name(this)"),
          intersperseBlankLines(
            List(
              intersperseBlankLines(
                updateOnChangeChannels.map((_, channel) =>
                  writeChannelInit(channel)
                )
              ),
              throttledEvents.map((_, event) => line(
                s"this->${eventThrottleCounterName(event.getName)} = 0;"
              )),
              sortedParams.map((_, param) => line(
                s"this->${paramValidityFlagName(param.getName)} = Fw::ParamValid::UNINIT;"
              ))
            )
          )
        ),
        destructorClassMember(
          Some(s"Destroy $className object"),
          Nil,
          CppDoc.Class.Destructor.Virtual
        )
      )
    )
  }

  private def getMutexOperationMembers: List[CppDoc.Class.Member] = {
    if !(hasGuardedInputPorts || hasGuardedCommands) then Nil
    else addAccessTagAndComment(
      "PROTECTED",
      """|Mutex operations for guarded ports
         |
         |You can override these operations to provide more sophisticated
         |synchronization
         |""",
      List(
        functionClassMember(
          Some("Lock the guarded mutex"),
          "lock",
          Nil,
          CppDoc.Type("void"),
          lines(
            "this->m_guardedPortMutex.lock();"
          ),
          CppDoc.Function.Virtual
        ),
        functionClassMember(
          Some("Unlock the guarded mutex"),
          "unLock",
          Nil,
          CppDoc.Type("void"),
          lines(
            "this->m_guardedPortMutex.unLock();"
          ),
          CppDoc.Function.Virtual
        )
      )
    )
  }

  private def getDispatchFunctionMember: List[CppDoc.Class.Member] = {
    def writeAsyncPortDispatch(p: PortInstance) = {
      val body = p.getType.get match {
        case PortInstance.Type.DefPort(_) =>
          List(
            intersperseBlankLines(
              portParamTypeMap(p.getUnqualifiedName).map((n, tn, t) => {
                val varDecl = writeVarDecl(s, tn, n, t)
                lines(
                  s"""|// Deserialize argument $n
                      |$varDecl
                      |deserStatus = msg.deserialize($n);
                      |FW_ASSERT(
                      |  deserStatus == Fw::FW_SERIALIZE_OK,
                      |  static_cast<FwAssertArgType>(deserStatus)
                      |);
                      |"""
                )
              })
            ),
            line("// Call handler function") ::
              writeFunctionCall(
                s"this->${inputPortHandlerName(p.getUnqualifiedName)}",
                List("portNum"),
                getPortParams(p).map(_._1)
              ),
            Line.blank :: lines("break;")
          ).flatten
        case PortInstance.Type.Serial => lines(
          s"""|// Deserialize serialized buffer into new buffer
              |U8 handBuff[this->m_msgSize];
              |Fw::ExternalSerializeBuffer serHandBuff(handBuff,this->m_msgSize);
              |deserStatus = msg.deserialize(serHandBuff);
              |FW_ASSERT(
              |  deserStatus == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(deserStatus)
              |);
              |this->${inputPortHandlerName(p.getUnqualifiedName)}(portNum, serHandBuff);
              |
              |break;
              |"""
        )
      }

      line(s"// Handle async input port ${p.getUnqualifiedName}") ::
        wrapInScope(
          s"case ${portCppConstantName(p)}: {",
          body,
          "}"
        )
    }
    def writeAsyncCommandDispatch(opcode: Command.Opcode, cmd: Command) = {
      val cmdRespVarName = portVariableName(cmdRespPort.get)
      val body = intersperseBlankLines(
        List(
          lines(
            """|// Deserialize opcode
               |FwOpcodeType opCode = 0;
               |deserStatus = msg.deserialize(opCode);
               |FW_ASSERT (
               |  deserStatus == Fw::FW_SERIALIZE_OK,
               |  static_cast<FwAssertArgType>(deserStatus)
               |);
               |
               |// Deserialize command sequence
               |U32 cmdSeq = 0;
               |deserStatus = msg.deserialize(cmdSeq);
               |FW_ASSERT (
               |  deserStatus == Fw::FW_SERIALIZE_OK,
               |  static_cast<FwAssertArgType>(deserStatus)
               |);
               |
               |// Deserialize command argument buffer
               |Fw::CmdArgBuffer args;
               |deserStatus = msg.deserialize(args);
               |FW_ASSERT (
               |  deserStatus == Fw::FW_SERIALIZE_OK,
               |  static_cast<FwAssertArgType>(deserStatus)
               |);
               |
               |// Reset buffer
               |args.resetDeser();
               |"""
          ),
          intersperseBlankLines(
            cmdParamTypeMap(opcode).map((n, tn, _) =>
              lines(
                s"""|// Deserialize argument $n
                    |$tn $n;
                    |deserStatus = args.deserialize($n);
                    |if (deserStatus != Fw::FW_SERIALIZE_OK) {
                    |  if (this->$cmdRespVarName[0].isConnected()) {
                    |    this->cmdResponse_out(
                    |        opCode,
                    |        cmdSeq,
                    |        Fw::CmdResponse::FORMAT_ERROR
                    |    );
                    |  }
                    |  // Don't crash the task if bad arguments were passed from the ground
                    |  break;
                    |}
                    |"""
              )
            )
          ),
          lines(
            s"""|// Make sure there was no data left over.
                |// That means the argument buffer size was incorrect.
                |#if FW_CMD_CHECK_RESIDUAL
                |if (args.getBuffLeft() != 0) {
                |  if (this->$cmdRespVarName[0].isConnected()) {
                |    this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::FORMAT_ERROR);
                |  }
                |  // Don't crash the task if bad arguments were passed from the ground
                |  break;
                |}
                |#endif
                |"""
          ),
          line("// Call handler function") ::
            writeFunctionCall(
              s"this->${commandHandlerName(cmd.getName)}",
              List("opCode, cmdSeq"),
              cmdParamTypeMap(opcode).map(_._1)
            ),
          lines("break;")
        )
      )

      line(s"// Handle command ${cmd.getName}") ::
        wrapInScope(
          s"case ${commandCppConstantName(cmd)}: {",
          body,
          "}"
        )
    }
    def writeInternalPortDispatch(p: PortInstance.Internal) = {
      val body = intersperseBlankLines(
        List(
          intersperseBlankLines(
            portParamTypeMap(p.getUnqualifiedName).map((n, tn, _) =>
              lines(
                s"""|$tn $n;
                    |deserStatus = msg.deserialize($n);
                    |
                    |// Internal interface should always deserialize
                    |FW_ASSERT(
                    |  Fw::FW_SERIALIZE_OK == deserStatus,
                    |  static_cast<FwAssertArgType>(deserStatus)
                    |);
                    |"""
              )
            )
          ),
          lines(
            """|// Make sure there was no data left over.
               |// That means the buffer size was incorrect.
               |FW_ASSERT(
               |  msg.getBuffLeft() == 0,
               |  static_cast<FwAssertArgType>(msg.getBuffLeft())
               |);
               |"""
          ),
          line("// Call handler function") ::
            writeFunctionCall(
              s"this->${internalInterfaceHandlerName(p.getUnqualifiedName)}",
              Nil,
              getPortParams(p).map(_._1)
            ),
          lines("break;")
        )
      )

      line(s"// Handle internal interface ${p.getUnqualifiedName}") ::
        wrapInScope(
          s"case ${internalPortCppConstantName(p)}: {",
          body,
          "}"
        )
    }

    if data.kind == Ast.ComponentKind.Passive then Nil
    else {
      val assertMsgStatus = lines(
        """|FW_ASSERT(
           |  msgStatus == Os::Queue::QUEUE_OK,
           |  static_cast<FwAssertArgType>(msgStatus)
           |);
           |"""
      )

      addAccessTagAndComment(
        data.kind match {
          case Ast.ComponentKind.Active => "PRIVATE"
          case Ast.ComponentKind.Queued => "PROTECTED"
          case _ => ""
        },
        "Message dispatch functions",
        List(
          functionClassMember(
            Some("Called in the message loop to dispatch a message from the queue"),
            "doDispatch",
            Nil,
            CppDoc.Type(
              "MsgDispatchStatus",
              Some("Fw::QueuedComponentBase::MsgDispatchStatus")
            ),
            List(
              if hasSerialAsyncInputPorts then lines(
                """|U8 msgBuff[this->m_msgSize];
                   |Fw::ExternalSerializeBuffer msg(msgBuff,this->m_msgSize);
                   |"""
              )
              else lines("ComponentIpcSerializableBuffer msg;"),
              lines(
                s"""|FwQueuePriorityType priority = 0;
                    |
                    |Os::Queue::QueueStatus msgStatus = this->m_queue.receive(
                    |  msg,
                    |  priority,
                    |  Os::Queue::QUEUE_${if data.kind == Ast.ComponentKind.Queued then "NON" else ""}BLOCKING
                    |);
                    |""".stripMargin
              ),
              if data.kind == Ast.ComponentKind.Queued then wrapInIfElse(
                "Os::Queue::QUEUE_NO_MORE_MSGS == msgStatus",
                lines("return Fw::QueuedComponentBase::MSG_DISPATCH_EMPTY;"),
                assertMsgStatus
              )
              else assertMsgStatus,
              lines(
                """|
                   |// Reset to beginning of buffer
                   |msg.resetDeser();
                   |
                   |FwEnumStoreType desMsg = 0;
                   |Fw::SerializeStatus deserStatus = msg.deserialize(desMsg);
                   |FW_ASSERT(
                   |  deserStatus == Fw::FW_SERIALIZE_OK,
                   |  static_cast<FwAssertArgType>(deserStatus)
                   |);
                   |
                   |MsgTypeEnum msgType = static_cast<MsgTypeEnum>(desMsg);
                   |"""
              ),
              Line.blank :: wrapInIf(
                s"msgType == $exitConstantName",
                lines("return MSG_DISPATCH_EXIT;")
              ),
              lines(
                """|
                   |FwIndexType portNum = 0;
                   |deserStatus = msg.deserialize(portNum);
                   |FW_ASSERT(
                   |  deserStatus == Fw::FW_SERIALIZE_OK,
                   |  static_cast<FwAssertArgType>(deserStatus)
                   |);
                   |"""
              ),
              Line.blank :: wrapInSwitch(
                "msgType",
                intersperseBlankLines(
                  List(
                    intersperseBlankLines(dataProductAsyncInputPorts.map(writeAsyncPortDispatch)),
                    intersperseBlankLines(typedAsyncInputPorts.map(writeAsyncPortDispatch)),
                    intersperseBlankLines(serialAsyncInputPorts.map(writeAsyncPortDispatch)),
                    intersperseBlankLines(asyncCmds.map(writeAsyncCommandDispatch)),
                    intersperseBlankLines(internalPorts.map(writeInternalPortDispatch)),
                    stateMachineWriter.writeDispatch,
                    lines(
                      """|default:
                         |  return MSG_DISPATCH_ERROR;
                         |"""
                    )
                  )
                )
              ),
              Line.blank :: lines("return MSG_DISPATCH_OK;")
            ).flatten,
            CppDoc.Function.Virtual
          )
        )
      )
    }
  }

  private def getTimeFunctionMember: List[CppDoc.Class.Member] =
    if !hasTimeGetPort then Nil
    else {
      val name = portVariableName(timeGetPort.get)

      addAccessTagAndComment(
        "PROTECTED",
        "Time",
        List(
          functionClassMember(
            Some(
              """| Get the time
                 |
                 |\\return The current time
                 |"""
            ),
            "getTime",
            Nil,
            CppDoc.Type("Fw::Time"),
            wrapInIfElse(
              s"this->$name[0].isConnected()",
              lines(
                s"""|Fw::Time _time;
                    |this->$name[0].invoke(_time);
                    |return _time;
                    |"""
              ),
              lines(
                "return Fw::Time(TB_NONE, 0, 0);"
              )
            )
          )
        )
      )
    }

  private def getMsgSizeVariableMember: List[CppDoc.Class.Member] = {
    if !hasSerialAsyncInputPorts then Nil
    else List(
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("PRIVATE"),
          lines(
            """|
               |//! Stores max message size
               |FwSizeType m_msgSize;
               |"""
          )
        ).flatten
      )
    )
  }

  private def getMutexVariableMembers: List[CppDoc.Class.Member] = {
    if !(hasGuardedInputPorts || hasGuardedCommands || hasParameters) then Nil
    else List(
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("PRIVATE"),
          CppDocWriter.writeBannerComment(
            "Mutexes"
          ),
          if !(hasGuardedInputPorts || hasGuardedCommands) then Nil
          else lines(
            """|
               |//! Mutex for guarded ports
               |Os::Mutex m_guardedPortMutex;
               |"""
          ),
          if !hasParameters then Nil
          else lines(
            """|
               |//! Mutex for locking parameters during sets and saves
               |Os::Mutex m_paramLock;
               |"""
          )
        ).flatten
      )
    )
  }

}

object ComponentCppWriter extends CppWriterUtils {

  sealed trait ConnectionSense
  object ConnectionSense {
    case object Forward extends ConnectionSense
    case object Reversed extends ConnectionSense
  }

  def reverseDirection(direction: PortInstance.Direction) = {
    import PortInstance.Direction._
    direction match {
      case Input => Output
      case Output => Input
    }
  }

  def writePortConnections(
    port: PortInstance,
    numGetterName: PortInstance => String,
    variableName: PortInstance => String,
    callbackName: String => String,
    printName: PortInstance => String,
    connectionSense: ConnectionSense = ConnectionSense.Forward
  ): List[Line] = {
    val d = {
      val trueDirection = port.getDirection.get
      connectionSense match {
        case ConnectionSense.Forward => trueDirection
        case ConnectionSense.Reversed => reverseDirection(trueDirection)
      }
    }

    val body = line(s"// Connect ${d.toString} port ${port.getUnqualifiedName}") ::
      wrapInForLoopStaggered(
        "FwIndexType port = 0",
        s"port < static_cast<FwIndexType>(this->${numGetterName(port)}())",
        "port++",
        List(
          lines(
            s"|this->${variableName(port)}[port].init();"
          ),
          d match {
            case PortInstance.Direction.Input => lines(
              s"""|this->${variableName(port)}[port].addCallComp(
                  |  this,
                  |  ${callbackName(port.getUnqualifiedName)}
                  |);
                  |this->${variableName(port)}[port].setPortNum(port);
                  |"""
            )
            case PortInstance.Direction.Output => Nil
          },
          Line.blank :: lines(
            s"""|#if FW_OBJECT_NAMES == 1
                |Fw::ObjectName portName;
                |portName.format(
                |  "%s_${printName(port)}[%" PRI_PlatformIntType "]",
                |  this->m_objName.toChar(),
                |  port
                |);
                |this->${variableName(port)}[port].setObjName(portName.toChar());
                |#endif
                |"""
          )
        ).flatten
      )

    port match {
      case PortInstance.Special(aNode, _, _, _, _) => aNode._2.data match {
        case Ast.SpecPortInstance.Special(_, kind, _, _, _) => kind match {
          case Ast.SpecPortInstance.TextEvent => List.concat(
            lines("#if FW_ENABLE_TEXT_LOGGING == 1"),
            body,
            lines("#endif")
          )
          case _ => body
        }
        case _ => body
      }
      case _ => body
    }
  }

}
