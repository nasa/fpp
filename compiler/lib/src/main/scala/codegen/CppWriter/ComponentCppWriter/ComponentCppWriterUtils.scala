package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Message type for message send logic */
sealed trait MessageType
object MessageType {
  case object Command extends MessageType {
    override def toString = "command"
  }
  case object Port extends MessageType {
    override def toString = "async input port"
  }
  case object StateMachine extends MessageType {
    override def toString = "state machine"
  }
}

/** Utilities for writing C++ component definitions */
abstract class ComponentCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends CppWriterUtils {

  val node: AstNode[Ast.DefComponent] = aNode._2

  val data: Ast.DefComponent = node.data

  val symbol: Symbol.Component = Symbol.Component(aNode)

  val component: Component = s.a.componentMap(symbol)

  val name: String = s.getName(symbol)

  val namespaceIdentList: List[String] = s.getNamespaceIdentList(symbol)

  val className: String = s"${name}ComponentBase"

  val implClassName: String = name

  val members: List[Ast.ComponentMember] = data.members

  val formalParamsCppWriter: FormalParamsCppWriter = FormalParamsCppWriter(s)

  /** Port number param as a CppDoc Function Param */
  val portNumParam: CppDoc.Function.Param = CppDoc.Function.Param(
    CppDoc.Type("FwIndexType"),
    "portNum",
    Some("The port number")
  )

  /** Opcode param as a CppDoc Function Param */
  val opcodeParam: CppDoc.Function.Param = CppDoc.Function.Param(
    CppDoc.Type("FwOpcodeType"),
    "opCode",
    Some("The opcode")
  )

  /** Command sequence param as a CppDoc Function Param */
  val cmdSeqParam: CppDoc.Function.Param = CppDoc.Function.Param(
    CppDoc.Type("U32"),
    "cmdSeq",
    Some("The command sequence number")
  )

  /** List of general port instances sorted by name */
  private val generalPorts: List[PortInstance.General] = component.portMap.toList.map((_, p) => p match {
    case i: PortInstance.General => Some(i)
    case _ => None
  }).filter(_.isDefined).map(_.get).sortBy(_.getUnqualifiedName)

  /** List of general input ports */
  private val generalInputPorts: List[PortInstance.General] =
    filterByPortDirection(generalPorts, PortInstance.Direction.Input)

  /** List of general output ports */
  private val outputPorts: List[PortInstance.General] =
    filterByPortDirection(generalPorts, PortInstance.Direction.Output)

  /** List of special port instances sorted by name */
  private val specialPorts: List[PortInstance.Special] =
    component.specialPortMap.toList.map(_._2).sortBy(_.getUnqualifiedName)

  /** List of special input port instances */
  val specialInputPorts: List[PortInstance.Special] =
    filterByPortDirection(specialPorts, PortInstance.Direction.Input)

  /** List of data product input port instances */
  val dataProductInputPorts: List[PortInstance.Special] =
    specialInputPorts.filter(pi => pi.specifier.kind == Ast.SpecPortInstance.ProductRecv)

  /** List of special output port instances */
  val specialOutputPorts: List[PortInstance.Special] =
    filterByPortDirection(specialPorts, PortInstance.Direction.Output)

  /** List of data product output port instances */
  val dataProductOutputPorts: List[PortInstance.Special] =
    specialOutputPorts.filter(pi => {
      pi.specifier.kind == Ast.SpecPortInstance.ProductGet ||
      pi.specifier.kind == Ast.SpecPortInstance.ProductRequest ||
      pi.specifier.kind == Ast.SpecPortInstance.ProductSend
    })

  /** List of typed input ports */
  val typedInputPorts: List[PortInstance.General] = filterTypedPorts(generalInputPorts)

  /** List of serial input ports */
  val serialInputPorts: List[PortInstance.General] = filterSerialPorts(generalInputPorts)

  /** List of data product async input ports */
  val dataProductAsyncInputPorts: List[PortInstance.Special] = filterAsyncSpecialPorts(dataProductInputPorts)

  /** List of typed async input ports */
  val typedAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(typedInputPorts)

  /** List of serial async input ports */
  val serialAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(serialInputPorts)

  /** List of typed overflow hook ports */
  val typedHookPorts: List[PortInstance.General] = filterOverflowHookPorts(typedAsyncInputPorts)

  /** List of serial overflow hook ports */
  val serialHookPorts: List[PortInstance.General] = filterOverflowHookPorts(serialInputPorts)

  /** List of serial overflow hook ports */
  val dataProductHookPorts: List[PortInstance.Special] =
    dataProductAsyncInputPorts.filter(p =>
      p.queueFull match {
        case Some(Ast.QueueFull.Hook) => true
        case _ => false
      }
    )

  /** List of typed output ports */
  val typedOutputPorts: List[PortInstance.General] = filterTypedPorts(outputPorts)

  /** List of serial output ports */
  val serialOutputPorts: List[PortInstance.General] = filterSerialPorts(outputPorts)

  /** List of state machine instances */
  val stateMachineInstances: List[StateMachineInstance] =
    component.stateMachineInstanceMap.toList.map((_, sm) => sm).sortBy(_.getName)

  /** List of internal port instances sorted by name */
  val internalPorts: List[PortInstance.Internal] = component.portMap.toList.map((_, p) => p match {
    case i: PortInstance.Internal => Some(i)
    case _ => None
  }).filter(_.isDefined).map(_.get).sortBy(_.getUnqualifiedName)

  /** List of internal overflow hook ports */
  val internalHookPorts: List[PortInstance.Internal] =
    internalPorts.filter(p =>
      p.queueFull match {
        case Ast.QueueFull.Hook => true
        case _ => false
      }
    )

  /** List of commands sorted by opcode */
  val sortedCmds: List[(Command.Opcode, Command)] = component.commandMap.toList.sortBy(_._1)

  /** List of non-parameter commands */
  val nonParamCmds: List[(Command.Opcode, Command.NonParam)] = sortedCmds.map((opcode, cmd) => cmd match {
    case c: Command.NonParam => Some((opcode, c))
    case _ => None
  }).filter(_.isDefined).map(_.get)

  /** List of async commands */
  val asyncCmds: List[(Command.Opcode, Command.NonParam)] = nonParamCmds.filter((_, cmd) => cmd.kind match {
    case Command.NonParam.Async(_, _) => true
    case _ => false
  })

  /** List of async commands */
  val hookCmds: List[(Command.Opcode, Command.NonParam)] = nonParamCmds.filter((_, cmd) => cmd.kind match {
    case Command.NonParam.Async(_, Ast.QueueFull.Hook) => true
    case _ => false
  })

  /** List of guarded commands */
  val guardedCmds: List[(Command.Opcode, Command.NonParam)] = nonParamCmds.filter((_, cmd) => cmd.kind match {
    case Command.NonParam.Guarded => true
    case _ => false
  })

  /** Get the CppDoc formal parameters for a non-parameter command */
  def getNonParamCmdFormalParams(cmd: Command.NonParam, stringRep: String): List[CppDoc.Function.Param] =
    formalParamsCppWriter.write(
      cmd.aNode._2.data.params,
      stringRep,
      FormalParamsCppWriter.Value
    )

  /** Map from command opcodes to command parameters */
  val cmdParamMap: Map[Command.Opcode, List[CppDoc.Function.Param]] = nonParamCmds.map((opcode, cmd) => {(
    opcode,
    getNonParamCmdFormalParams(cmd, "Fw::CmdStringArg")
  )}).toMap

  /** List of events sorted by ID */
  val sortedEvents: List[(Event.Id, Event)] = component.eventMap.toList.sortBy(_._1)

  /** List of throttled events */
  val throttledEvents: List[(Event.Id, Event)] = sortedEvents.filter((_, event) =>
    event.throttle match {
      case Some(_) => true
      case None => false
    }
  )

  /** List of channels sorted by ID */
  val sortedChannels: List[(TlmChannel.Id, TlmChannel)] = component.tlmChannelMap.toList.sortBy(_._1)

  /** List of channels updated on change */
  val updateOnChangeChannels: List[(TlmChannel.Id, TlmChannel)] = sortedChannels.filter((_, channel) =>
    channel.update match {
      case Ast.SpecTlmChannel.OnChange => true
      case _ => false
    }
  )

  /** List of parameters sorted by ID */
  val sortedParams: List[(Param.Id, Param)] = component.paramMap.toList.sortBy(_._1)

  /** Command receive port */
  val cmdRecvPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.CommandRecv)

  /** Command response port */
  val cmdRespPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.CommandResp)

  /** Command register port */
  val cmdRegPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.CommandReg)

  /** Time get port */
  val timeGetPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.TimeGet)

  /** Data product get port */
  val productGetPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.ProductGet)

  /** Data product request port */
  val productRequestPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.ProductRequest)

  /** Data product send port */
  val productSendPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.ProductSend)

  /** Data product receive port */
  val productRecvPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.ProductRecv)

  /** Event port */
  val eventPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.Event)

  /** Text event port */
  val textEventPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.TextEvent)

  /** Telemetry port */
  val tlmPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.Telemetry)

  /** Parameter get port */
  val prmGetPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.ParamGet)

  /** Parameter set port */
  val prmSetPort: Option[PortInstance.Special] =
    component.specialPortMap.get(Ast.SpecPortInstance.ParamSet)

  val containersById = component.containerMap.toList.sortBy(_._1)

  val containersByName = component.containerMap.toList.sortBy(_._2.getName)

  val recordsById = component.recordMap.toList.sortBy(_._1)

  val recordsByName = component.recordMap.toList.sortBy(_._2.getName)

  val smInstancesByName = component.stateMachineInstanceMap.toList.sortBy(_._1)

  val smSymbols = component.stateMachineInstanceMap.map(_._2.symbol).toSet.toList

  // Component properties

  val hasGuardedInputPorts: Boolean = generalInputPorts.exists(p =>
    p.kind match {
      case PortInstance.General.Kind.GuardedInput => true
      case _ => false
    }
  )

  val hasGuardedCommands: Boolean = guardedCmds.nonEmpty

  val hasSerialAsyncInputPorts: Boolean = serialAsyncInputPorts.nonEmpty

  val hasInternalPorts: Boolean = internalPorts.nonEmpty

  val hasTimeGetPort: Boolean = timeGetPort.isDefined

  val hasCommands: Boolean = component.commandMap.nonEmpty

  val hasEvents: Boolean = component.eventMap.nonEmpty

  val hasChannels: Boolean = component.tlmChannelMap.nonEmpty

  val hasTelemetry: Boolean = component.tlmChannelMap.nonEmpty

  val hasParameters: Boolean = component.paramMap.nonEmpty

  val hasDataProducts: Boolean = component.hasDataProducts

  val hasContainers: Boolean = containersByName != Nil

  val hasStateMachineInstances: Boolean = component.hasStateMachineInstances

  val hasProductGetPort: Boolean = productGetPort.isDefined

  val hasProductRecvPort: Boolean = productRecvPort.isDefined

  val hasProductRequestPort: Boolean = productRequestPort.isDefined

  /** Parameters for the init function */
  val initParams: List[CppDoc.Function.Param] = List.concat(
    if data.kind != Ast.ComponentKind.Passive then List(
      CppDoc.Function.Param(
        CppDoc.Type("FwQueueSizeType"),
        "queueDepth",
        Some("The queue depth")
      )
    )
    else Nil,
    if hasSerialAsyncInputPorts then List(
      CppDoc.Function.Param(
        CppDoc.Type("FwSizeType"),
        "msgSize",
        Some("The message size")
      )
    )
    else Nil,
    List(
      CppDoc.Function.Param(
        CppDoc.Type("FwEnumStoreType"),
        "instance",
        Some("The instance number"),
        Some("0")
      )
    ),
  )

  val portParamTypeMap: ComponentCppWriterUtils.PortParamTypeMap =
    List(
      specialInputPorts,
      typedInputPorts,
      specialOutputPorts,
      typedOutputPorts,
      internalPorts
    ).flatten.foldLeft (Map(): ComponentCppWriterUtils.PortParamTypeMap) ((m, p) => {
      val portName = p.getUnqualifiedName
      def makeTuple
        (typeWriter: Type => String)
        (param: Ast.Annotated[AstNode[Ast.FormalParam]])
      = {
        val data = param._2.data
        val paramName = data.name
        val paramType = s.a.typeMap(data.typeName.id)
        val paramTypeString = typeWriter(paramType)
        (paramName, paramTypeString, paramType)
      }
      (p, p.getType) match {
        case (_, Some(PortInstance.Type.DefPort(symbol))) =>
          val typeWriter = writeTypeAsGeneralPortParamType (symbol) _
          val tuples = symbol.node._2.data.params.map(makeTuple (typeWriter) _)
          m + (portName -> tuples)
        case (PortInstance.Internal(node, _, _), _) =>
          val tuples = node._2.data.params.map(makeTuple (writeTypeAsInternalPortParamType) _)
          m + (portName -> tuples)
        case _ => m
      }
    })

  val cmdParamTypeMap: ComponentCppWriterUtils.CmdParamTypeMap =
    nonParamCmds.map((opcode, cmd) => (
      opcode,
      cmd.aNode._2.data.params.map(param => {
        val data = param._2.data
        val paramName = data.name
        val paramType = s.a.typeMap(data.typeName.id)
        val paramTypeString = writeTypeAsCommandParamType(paramType)
        (paramName, paramTypeString, paramType)
      })
    )).toMap

  def getEventParamTypes(event: Event, stringRep: String = "Fw::StringBase"):
  List[(String, String, Type)] =
    event.aNode._2.data.params.map(param => {
      val data = param._2.data
      val paramName = data.name
      val paramType = s.a.typeMap(data.typeName.id)
      val paramTypeString = writeTypeAsFormalParamType(paramType, stringRep)
      (paramName, paramTypeString, paramType)
    })

  val eventParamTypeMap: ComponentCppWriterUtils.EventParamTypeMap =
    sortedEvents.map((id, event) => (id, getEventParamTypes(event))).toMap

  // Map from a port instance name to param list
  private val portParamMap: ComponentCppWriterUtils.PortParamMap =
    List(
      specialInputPorts,
      typedInputPorts,
      specialOutputPorts,
      typedOutputPorts,
      internalPorts
    ).flatten.foldLeft (Map(): ComponentCppWriterUtils.PortParamMap) ((m, p) => {
      val portName = p.getUnqualifiedName
      (p, p.getType) match {
        case (_, Some(PortInstance.Type.DefPort(symbol))) =>
          val params = formalParamsCppWriter.write(
            symbol.node._2.data.params,
            "Fw::StringBase"
          )
          m + (portName -> params)
        case (PortInstance.Internal(node, _, _), _) =>
          val params = formalParamsCppWriter.write(
            node._2.data.params,
            "Fw::InternalInterfaceString"
          )
          m + (portName -> params)
        case _ => m
      }
    })

  /** Get the qualified name of a port type */
  def getQualifiedPortTypeName(
    p: PortInstance,
    direction: PortInstance.Direction
  ): String = {
    p.getType match {
      case Some(PortInstance.Type.DefPort(symbol)) =>
        val qualifiers = s.a.getEnclosingNames(symbol)
        val cppQualifier = qualifiers match {
          case Nil => ""
          case _ => qualifiers.mkString("::") + "::"
        }
        val name = PortCppWriter.getPortName(symbol.getUnqualifiedName, direction)

        cppQualifier + name
      case Some(PortInstance.Type.Serial) =>
        s"Fw::${direction.toString.capitalize}SerializePort"
      case None => ""
    }
  }

  /** Calls writePort() on each port in ports, wrapping the result in an if directive if necessary */
  def mapPorts(
    ports: List[PortInstance],
    writePort: PortInstance => List[CppDoc.Class.Member],
    output: CppDoc.Lines.Output = CppDoc.Lines.Both
  ): List[CppDoc.Class.Member] = {
    ports.flatMap(p => p match {
      case PortInstance.Special(aNode, _, _, _, _) => aNode._2.data match {
        case Ast.SpecPortInstance.Special(_, kind, _, _, _) => kind match {
          case Ast.SpecPortInstance.TextEvent => wrapClassMembersInIfDirective(
            "\n#if FW_ENABLE_TEXT_LOGGING == 1",
            writePort(p),
            output
          )
          case _ => writePort(p)
        }
        case _ => writePort(p)
      }
      case _ => writePort(p)
    })
  }

  def getPortComment(p: PortInstance): Option[String] = {
    val aNode = p match {
      case PortInstance.General(aNode, _, _, _, _) => aNode
      case PortInstance.Special(aNode, _, _, _, _) => aNode
      case PortInstance.Internal(aNode, _, _) => aNode
    }

    AnnotationCppWriter.asStringOpt(aNode)
  }

  /** Get port params as a list of tuples containing the name and typename for each param */
  def getPortParams(p: PortInstance): List[(String, String, Option[Type])] =
    p.getType match {
      case Some(PortInstance.Type.Serial) => List(
        ("buffer", "Fw::SerializeBufferBase", None)
      )
      case _ => portParamTypeMap(p.getUnqualifiedName).map((n, tn, t) => (n, tn, Some(t)))
    }

  /** Get port params as CppDoc Function Params */
  def getPortFunctionParams(p: PortInstance): List[CppDoc.Function.Param] =
    p.getType match {
      case Some(PortInstance.Type.Serial) => List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::SerializeBufferBase&"),
          "buffer",
          Some("The serialization buffer")
        )
      )
      case _ => portParamMap(p.getUnqualifiedName)
    }

  /** Determine whether a port has params */
  def hasPortParams(p: PortInstance): Boolean = getPortFunctionParams(p) != Nil

  /** Get a port instance as a symbol option */
  def getPortSymbol(pi: PortInstance): Option[Symbol.Port] =
    pi.getType match {
      case Some(PortInstance.Type.DefPort(symbol)) => Some(symbol)
      case _ => None
    }

  /** Extract an optional return type from a port instance and transform it */
  private def transformPortReturnType[T](
    pi: PortInstance,
    transformer: Symbol.Port => AstNode[Ast.TypeName] => T
  ): Option[T] =
    getPortSymbol(pi).flatMap(
      symbol => symbol.node._2.data.returnType.map(transformer (symbol))
    )

  /** Get the semantic type of a port instance as a Type option */
  def getPortReturnTypeSemantic(pi: PortInstance): Option[Type] = {
    def transformer (sym: Symbol.Port) (node: AstNode[Ast.TypeName]) =
      s.a.typeMap(node.id)
    transformPortReturnType(pi, transformer)
  }

  /** Get the C++ return type of a port instance as a String option */
  def getPortReturnType(pi: PortInstance): Option[String] = {
    def transformer (sym: Symbol.Port) (node: AstNode[Ast.TypeName]) =
      TypeCppWriter.getName(s, s.a.typeMap(node.id))
    transformPortReturnType(pi, transformer)
  }

  /** Get a return type of a port as a CppDoc type */
  def getPortReturnTypeAsCppDocType(p: PortInstance): CppDoc.Type =
    CppDoc.Type(
      getPortReturnType(p) match {
        case Some(tn) => tn
        case None => "void"
      }
    )

  def addReturnKeyword(str: String, p: PortInstance): String =
    p.getType match {
      case Some(PortInstance.Type.Serial) => s"return $str"
      case _ => getPortReturnType(p) match {
          case Some(_) => s"return $str"
          case None => str
        }
    }

  /** Get the port type as a string */
  def getPortTypeString(p: PortInstance): String =
    p match {
      case _: PortInstance.General => p.getType.get match {
        case PortInstance.Type.DefPort(_) => "typed"
        case PortInstance.Type.Serial => "serial"
      }
      case _: PortInstance.Special => "special"
      case _: PortInstance.Internal => "internal"
    }

  def getPortListTypeString(ports: List[PortInstance]): String =
    ports match {
      case Nil => ""
      case _ => getPortTypeString(ports.head)
    }

  def getPortListDirectionString(ports: List[PortInstance]): String =
    ports match {
      case Nil => ""
      case _ => ports.head.getDirection.get.toString
    }

  /** Get the command param kind as a string */
  def getCmdParamKindString(kind: Command.Param.Kind): String =
    kind match {
      case Command.Param.Save => "save"
      case Command.Param.Set => "set"
    }

  /** Write the type of an internal port param as a C++ type */
  def writeInternalPortParamType(param: Ast.FormalParam): String =
    writeTypeAsInternalPortParamType(s.a.typeMap(param.typeName.id))

  /** Write a type as the type of an internal port param as a C++ type */
  def writeTypeAsInternalPortParamType(t: Type): String =
    TypeCppWriter.getName(s, t, "Fw::InternalInterfaceString")

  /** Write the type of a formal parameter as a C++ type */
  def writeFormalParamType(param: Ast.FormalParam, stringRep: String = "Fw::StringBase") =
    writeTypeAsFormalParamType(s.a.typeMap(param.typeName.id), stringRep)

  /** Write a type as the type of a formal parameter as a C++ type */
  def writeTypeAsFormalParamType(t: Type, stringRep: String = "Fw::StringBase") =
    TypeCppWriter.getName(s, t, stringRep)

  /** Write a channel type as a C++ type */
  def writeChannelType(t: Type, stringRep: String = "Fw::StringBase"): String =
    TypeCppWriter.getName(s, t, stringRep)

  /** Write send message logic */
  def writeSendMessageLogic(
    bufferName: String,
    queueFull: Ast.QueueFull,
    priority: Option[BigInt],
    messageType: MessageType,
    name: String,
    arguments: List[CppDoc.Function.Param]
  ): List[Line] = {
    val queueBlocking = queueFull match {
      case Ast.QueueFull.Block => "QUEUE_BLOCKING"
      case _ => "QUEUE_NONBLOCKING"
    }
    val priorityNum = priority match {
      case Some(num) => num
      case _ => BigInt(0)
    }

    intersperseBlankLines(
      List(
        lines(
          s"""|// Send message
              |Os::Queue::QueueBlocking _block = Os::Queue::$queueBlocking;
              |Os::Queue::QueueStatus qStatus = this->m_queue.send($bufferName, $priorityNum, _block);
              |"""
        ),
        queueFull match {
          case Ast.QueueFull.Drop => lines(
            """|if (qStatus == Os::Queue::QUEUE_FULL) {
               |  this->incNumMsgDropped();
               |  return;
               |}
               |"""
          )
          case Ast.QueueFull.Hook => lines(
            s"""|if (qStatus == Os::Queue::QUEUE_FULL) {
                |  this->${inputOverflowHookName(name, messageType)}(${arguments.map(_.name).mkString(", ")});
                |  return;
                |}
                |"""
          )
          case _ => Nil
        },
        lines(
          """|FW_ASSERT(
             |  qStatus == Os::Queue::QUEUE_OK,
             |  static_cast<FwAssertArgType>(qStatus)
             |);
             |"""
        )
      )
    )
  }

  /** Write an event format as C++ */
  def writeEventFormat(event: Event): String = {
    val formatList = event.format.fields zip event.aNode._2.data.params.map(_._2.data.typeName)
    formatList.foldLeft(FormatCppWriter.escapePercent(event.format.prefix))((a, s) =>
      a + (s match {
        case (f, tn) => f match {
          case (field, suffix) =>
            FormatCppWriter.writeField(field, tn) + FormatCppWriter.escapePercent(suffix)
        }
      })
    )
  }

  /** Write event severity as a string */
  def writeSeverity(event: Event) =
    event.aNode._2.data.severity match {
      case Ast.SpecEvent.ActivityHigh => "ACTIVITY_HI"
      case Ast.SpecEvent.ActivityLow => "ACTIVITY_LO"
      case Ast.SpecEvent.WarningHigh => "WARNING_HI"
      case Ast.SpecEvent.WarningLow => "WARNING_LO"
      case s => s.toString.toUpperCase.replace(' ', '_')
    }

  /** Write a parameter type as a C++ type */
  def writeParamType(t: Type, stringRep: String = "Fw::StringBase") =
    TypeCppWriter.getName(s, t, stringRep)

  /** Get the name for a general port enumerated constant in cpp file */
  def portCppConstantName(p: PortInstance) =
    s"${p.getUnqualifiedName}_${getPortTypeBaseName(p)}".toUpperCase

  /** Get the name for an internal port enumerated constant in cpp file */
  def internalPortCppConstantName(p: PortInstance.Internal) =
    s"INT_IF_${p.getUnqualifiedName.toUpperCase}"

  /** Get the name for a command enumerated constant in cpp file */
  def commandCppConstantName(cmd: Command) =
    s"CMD_${cmd.getName.toUpperCase}"

  /** Get the name for the state machine enumerated constant in cpp file */
  def stateMachineCppConstantName = "STATEMACHINE_SENDSIGNALS"

  /** Get the name for a port number getter function */
  def portNumGetterName(p: PortInstance) =
    s"getNum_${p.getUnqualifiedName}_${p.getDirection.get.toString.capitalize}Ports"

  /** Get the name for a port variable */
  def portVariableName(p: PortInstance) =
    s"m_${p.getUnqualifiedName}_${p.getDirection.get.toString.capitalize}Port"

  // Get the name for an input port getter function
  def inputPortGetterName(name: String) =
    s"get_${name}_InputPort"

  /** Get the name for an input port handler function */
  def inputPortHandlerName(name: String) =
    s"${name}_handler"

  // Get the name for an input port handler base-class function
  def inputPortHandlerBaseName(name: String) =
    s"${name}_handlerBase"

  /** Get the name for an input port callback function */
  def inputPortCallbackName(name: String) =
    s"m_p_${name}_in"

  /** Get the name for an async input port pre-message hook function */
  def inputPortHookName(name: String) =
    s"${name}_preMsgHook"

  /** Get the name for an async input port overflow hook function */
  def inputOverflowHookName(name: String, messageType: MessageType) =
    messageType match {
      case MessageType.Port => s"${name}_overflowHook"
      case MessageType.Command => s"${name}_cmdOverflowHook"
      case MessageType.StateMachine => s"${name}_stateMachineOverflowHook"
    }

  // Get the name for an output port connector function
  def outputPortConnectorName(name: String) =
    s"set_${name}_OutputPort"

  /** Get the name for an output port invocation function */
  def outputPortInvokerName(name: String) =
    s"${name}_out"

  /** Get the name for an output port invocation function */
  def outputPortInvokerName(pi: PortInstance): String =
    outputPortInvokerName(pi.getUnqualifiedName)

  /** Get the name for an internal interface handler */
  def internalInterfaceHandlerName(name: String) =
    s"${name}_internalInterfaceHandler"

  /** Get the name for a command handler */
  def commandHandlerName(name: String) =
    s"${name}_cmdHandler"

  /** Get the name for a command handler base-class function */
  def commandHandlerBaseName(name: String) =
    s"${name}_cmdHandlerBase"

  /** Get the name for a command opcode constant */
  def commandConstantName(cmd: Command): String = {
    val name = cmd match {
      case Command.NonParam(_, _) =>
        cmd.getName
      case Command.Param(aNode, kind) =>
        s"${aNode._2.data.name}_${getCmdParamKindString(kind).toUpperCase}"
    }

    s"OPCODE_${name.toUpperCase}"
  }

  /** Get the name for a param command opcode constant */
  def paramCommandConstantName(name: String, kind: Command.Param.Kind) = {
    s"OPCODE_${name.toUpperCase}_${getCmdParamKindString(kind).toUpperCase}"
  }

  /** Get the name for an event throttle counter variable */
  def eventThrottleCounterName(name: String) =
    s"m_${name}Throttle"

  /** Get the name for an event ID constant */
  def eventIdConstantName(name: String) =
    s"EVENTID_${name.toUpperCase}"

  /** Get the name for a telemetry channel update flag variable */
  def channelUpdateFlagName(name: String) =
    s"m_first_update_$name"

  /** Get the name for a telemetry channel storage variable */
  def channelStorageName(name: String) =
    s"m_last_$name"

  /** Get the name for a telemetry channel constant */
  def channelIdConstantName(name: String) =
    s"CHANNELID_${name.toUpperCase}"

  /** Get the name for a parameter handler (set/save) function */
  def paramHandlerName(name: String, kind: Command.Param.Kind) =
    s"param${getCmdParamKindString(kind).capitalize}_$name"

  /** Get the name for a parameter validity flag variable */
  def paramValidityFlagName(name: String) =
    s"m_param_${name}_valid"

  /** Get the name for a parameter id constant */
  def paramIdConstantName(name: String) =
    s"PARAMID_${name.toUpperCase}"

  /** Gets a data product receive handler */
  def getDpRecvHandler(name: String, body: List[Line] = Nil) =
    functionClassMember(
      Some(s"Receive a container of type $name"),
      s"dpRecv_${name}_handler",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("DpContainer&"),
          "container",
          Some("The container")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("Fw::Success::T"),
          "status",
          Some("The container status")
        )
      ),
      CppDoc.Type("void"),
      body,
      body match {
        case Nil => CppDoc.Function.PureVirtual
        case _ => CppDoc.Function.Override
      }
    )

  def getVirtualOverflowHook(
    name: String,
    msgType: MessageType,
    params: List[CppDoc.Function.Param]
  ) = {
    functionClassMember(
      Some(s"Overflow hook for $msgType $name"),
      inputOverflowHookName(name, msgType),
      params,
      CppDoc.Type("void"),
      Nil,
      CppDoc.Function.PureVirtual
    )
  }

  private def getPortTypeBaseName(
    p: PortInstance,
  ): String = {
    p.getType match {
      case Some(PortInstance.Type.DefPort(symbol)) => symbol.getUnqualifiedName
      case Some(PortInstance.Type.Serial) => "serial"
      case None => ""
    }
  }

  /** Write a type as the type of a general port param */
  private def writeTypeAsGeneralPortParamType (symbol: Symbol.Port) (t: Type) =
    TypeCppWriter.getName(s, t)

  /** Write a command param as a C++ type */
  private def writeCommandParamType(param: Ast.FormalParam) =
    writeTypeAsCommandParamType(s.a.typeMap(param.typeName.id))

  /** Write a type as the type of a command param */
  private def writeTypeAsCommandParamType(t: Type) =
    TypeCppWriter.getName(s, t, "Fw::CmdStringArg")

  private def filterByPortDirection[T<: PortInstance](ports: List[T], direction: PortInstance.Direction) =
    ports.filter(p =>
      p.getDirection match {
        case Some(d) if d == direction => true
        case _ => false
      }
    )

  private def filterTypedPorts(ports: List[PortInstance.General]) =
    ports.filter(p =>
      p.getType.get match {
        case PortInstance.Type.DefPort(_) => true
        case PortInstance.Type.Serial => false
      }
    )

  private def filterSerialPorts(ports: List[PortInstance.General]) =
    ports.filter(p =>
      p.getType.get match {
        case PortInstance.Type.DefPort(_) => false
        case PortInstance.Type.Serial => true
      }
    )

  private def filterAsyncInputPorts(ports: List[PortInstance.General]) =
    ports.filter(p =>
      p.kind match {
        case PortInstance.General.Kind.AsyncInput(_, _) => true
        case _ => false
      }
    )

  private def filterOverflowHookPorts(ports: List[PortInstance.General]) =
    ports.filter(p =>
      p.kind match {
        case PortInstance.General.Kind.AsyncInput(_, Ast.QueueFull.Hook) => true
        case _ => false
      }
    )

  private def filterAsyncSpecialPorts(ports: List[PortInstance.Special]) =
    ports.filter(_.specifier.inputKind == Some(Ast.SpecPortInstance.Async))

}

object ComponentCppWriterUtils {

  /** (  parameter name, parameter type name, parameter type ) **/
  type ParamTypeMapInfo = (String, String, Type)
  type CmdParamTypeMap = Map[Command.Opcode, List[ParamTypeMapInfo]]
  type PortParamMap = Map[String, List[CppDoc.Function.Param]]
  type PortParamTypeMap = Map[String, List[ParamTypeMapInfo]]
  type EventParamTypeMap = Map[Event.Id, List[ParamTypeMapInfo]]

  sealed trait Radix
  case object Decimal extends Radix
  case object Hex extends Radix

}
