package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Utilities for writing C++ component definitions */
abstract class ComponentCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends CppWriterUtils {

  val node: AstNode[Ast.DefComponent] = aNode._2

  val data: Ast.DefComponent = node.data

  val symbol: Symbol.Component = Symbol.Component(aNode)

  val component: Component = s.a.componentMap(symbol)

  val members: List[Ast.ComponentMember] = data.members

  /** Port number param as a CppDoc Function Param */
  val portNumParam: CppDoc.Function.Param = CppDoc.Function.Param(
    CppDoc.Type("NATIVE_INT_TYPE"),
    "portNum",
    Some("The port number")
  )

  /** List of general port instances sorted by name */
  val generalPorts: List[PortInstance.General] = component.portMap.toList.map((_, p) => p match {
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
  val specialPorts: List[PortInstance.Special] =
    component.specialPortMap.toList.map(_._2).sortBy(_.getUnqualifiedName)

  /** List of special input port instances */
  val specialInputPorts: List[PortInstance.Special] =
    filterByPortDirection(specialPorts, PortInstance.Direction.Input)

  /** List of special output port instances */
  val specialOutputPorts: List[PortInstance.Special] =
    filterByPortDirection(specialPorts, PortInstance.Direction.Output)

  /** List of typed input ports */
  val typedInputPorts: List[PortInstance.General] = filterTypedPorts(generalInputPorts)

  /** List of serial input ports */
  val serialInputPorts: List[PortInstance.General] = filterSerialPorts(generalInputPorts)

  /** List of typed async input ports */
  val typedAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(typedInputPorts)

  /** List of serial async input ports */
  val serialAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(serialInputPorts)

  /** List of typed output ports */
  val typedOutputPorts: List[PortInstance.General] = filterTypedPorts(outputPorts)

  /** List of serial output ports */
  val serialOutputPorts: List[PortInstance.General] = filterSerialPorts(outputPorts)

  /** List of internal port instances */
  val internalPorts: List[PortInstance.Internal] = members.map(member =>
    member.node._2 match {
      case Ast.ComponentMember.SpecInternalPort(node) => component.portMap(node.data.name) match {
        case i: PortInstance.Internal => Some(i)
        case _ => None
      }
      case _ => None
    }).filter(_.isDefined).map(_.get)

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

  /** Command response port */
  val cmdRespPort: Option[PortInstance.Special] = specialPorts.find(p =>
    p.aNode._2.data match {
      case Ast.SpecPortInstance.Special(kind, _) => kind match {
        case Ast.SpecPortInstance.CommandResp => true
        case _ => false
      }
      case _ => false
    })

  /** Time get port */
  val timeGetPort: Option[PortInstance.Special] = specialPorts.find(p =>
    p.aNode._2.data match {
      case Ast.SpecPortInstance.Special(kind, _) => kind match {
        case Ast.SpecPortInstance.TimeGet => true
        case _ => false
      }
      case _ => false
    })

  // Component properties

  val hasGuardedInputPorts: Boolean = generalInputPorts.exists(p =>
    p.kind match {
      case PortInstance.General.Kind.GuardedInput => true
      case _ => false
    }
  )

  val hasSerialAsyncInputPorts: Boolean = serialAsyncInputPorts.nonEmpty

  val hasInternalPorts: Boolean = internalPorts.nonEmpty

  val hasTimeGetPort: Boolean = timeGetPort.isDefined

  val hasCommands: Boolean = component.commandMap.nonEmpty

  val hasEvents: Boolean = component.eventMap.nonEmpty

  val hasChannels: Boolean = component.tlmChannelMap.nonEmpty

  val hasParameters: Boolean = component.paramMap.nonEmpty

  val portParamTypeMap: Map[String, List[(String, String)]] =
    List(
      specialInputPorts,
      typedInputPorts,
      specialOutputPorts,
      typedOutputPorts,
      internalPorts
    ).flatten.map(p =>
      p.getType match {
        case Some(PortInstance.Type.DefPort(symbol)) => Some((
          p.getUnqualifiedName,
          symbol.node._2.data.params.map(param => {
            val typeName = writeCppTypeName(
              s.a.typeMap(param._2.data.typeName.id),
              s,
              PortCppWriter.getPortNamespaces(symbol.node._2.data.name)
            )

            (param._2.data.name, typeName)
          })
        ))
        case None => p match {
          case PortInstance.Internal(node, _, _) => Some((
            p.getUnqualifiedName,
            node._2.data.params.map(param => {
              val typeName = writeCppTypeName(
                s.a.typeMap(param._2.data.typeName.id),
                s,
                Nil,
                Some("Fw::CmdStringArg")
              )

              (param._2.data.name, typeName)
            })
          ))
          case _ => None
        }
        case _ => None
      }
    ).filter(_.isDefined).map(_.get).toMap

  val cmdParamTypeMap: Map[Command.Opcode, List[(String, String)]] =
    nonParamCmds.map((opcode, cmd) => (
      opcode,
      cmd.aNode._2.data.params.map(param => {
        val typeName = writeCppTypeName(
          s.a.typeMap(param._2.data.typeName.id),
          s,
          Nil,
          Some("Fw::InternalInterfaceString")
        )

        (param._2.data.name, typeName)
      })
    )).toMap

  // Map from a port instance name to param list
  private val portParamMap =
    List(
      specialInputPorts,
      typedInputPorts,
      specialOutputPorts,
      typedOutputPorts,
      internalPorts
    ).flatten.map(p =>
      p.getType match {
        case Some(PortInstance.Type.DefPort(symbol)) => Some((
          p.getUnqualifiedName,
          writeFormalParamList(
            symbol.node._2.data.params,
            s,
            PortCppWriter.getPortNamespaces(symbol.node._2.data.name)
          )
        ))
        case None => p match {
          case PortInstance.Internal(node, _, _) => Some((
            p.getUnqualifiedName,
            writeFormalParamList(
              node._2.data.params,
              s,
              Nil,
              Some("Fw::InternalInterfaceString")
            )
          ))
          case _ => None
        }
        case _ => None
      }
    ).filter(_.isDefined).map(_.get).toMap

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
    writePort: PortInstance => List[CppDoc.Class.Member]
  ): List[CppDoc.Class.Member] = {
    ports.flatMap(p => p match {
      case PortInstance.Special(aNode, _, _) => aNode._2.data match {
        case Ast.SpecPortInstance.Special(kind, _) => kind match {
          case Ast.SpecPortInstance.TextEvent => wrapClassMembersInIfDirective(
            "\n#if FW_ENABLE_TEXT_LOGGING == 1",
            writePort(p)
          )
          case _ => writePort(p)
        }
        case _ => writePort(p)
      }
      case _ => writePort(p)
    })
  }

  /** Get port params as CppDoc Function Params */
  def getPortFunctionParams(p: PortInstance): List[CppDoc.Function.Param] =
    p.getType match {
      case Some(PortInstance.Type.DefPort(_)) | None =>
        portParamMap(p.getUnqualifiedName)
      case Some(PortInstance.Type.Serial) =>
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerializeBufferBase&"),
            "buffer",
            Some("The serialization buffer")
          )
        )
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

  /** Write an enumerated constant */
  def writeEnumConstant(
    name: String,
    value: BigInt,
    comment: Option[String] = None,
    radix: ComponentCppWriterUtils.Radix = ComponentCppWriterUtils.Decimal
  ): String = {
    val valueStr = radix match {
      case ComponentCppWriterUtils.Decimal => value.toString
      case ComponentCppWriterUtils.Hex => s"0x${value.toString(16)}"
    }
    val commentStr = comment match {
      case Some(s) => s" //! $s"
      case None => ""
    }

    s"$name = $valueStr,$commentStr"
  }

  /** Write a channel type as a C++ type */
  def writeChannelType(t: Type) =
    writeCppTypeName(t, s, Nil, Some("Fw::TlmString"))

  /** Add an optional string separated by two newlines */
  def addSeparatedString(str: String, strOpt: Option[String]): String = {
    strOpt match {
      case Some(s) => s"$str\n\n$s"
      case None => str
    }
  }

  /** Add an optional pre comment separated by two newlines */
  def addSeparatedPreComment(str: String, commentOpt: Option[String]): String = {
    commentOpt match {
      case Some(s) => s"//! $str\n//!\n//! $s"
      case None => str
    }
  }

  /** Write a banner comment in cpp and hpp files, with description and access tag in hpp file only */
  def writeAccessTagAndComment(
    accessTag: String,
    comment: String,
    description: Option[String] = None
  ): List[CppDoc.Class.Member] = {
    description match {
      case Some(s) => List(
        linesClassMember(
          List(
            CppDocHppWriter.writeAccessTag(accessTag),
            CppDocWriter.writeBannerComment(
              s"$comment\n\n$s"
            )
          ).flatten
        ),
        linesClassMember(
          CppDocWriter.writeBannerComment(
            comment
          ),
          CppDoc.Lines.Cpp
        ),
      )
      case None => List(
        linesClassMember(
          CppDocHppWriter.writeAccessTag(accessTag)
        ),
        linesClassMember(
          CppDocWriter.writeBannerComment(
            comment
          ),
          CppDoc.Lines.Both
        )
      )
    }
  }

  /** Get the name for a port number getter function */
  def portNumGetterName(name: String, direction: PortInstance.Direction) =
    s"getNum_${name}_${direction.toString.capitalize}Ports"

  /** Get the name for a port variable */
  def portVariableName(name: String, direction: PortInstance.Direction) =
    s"m_${name}_${direction.toString.capitalize}Port"

  /** Get the name for an input port handler function */
  def inputPortHandlerName(name: String) =
    s"${name}_handler"

  /** Get the name for an input port callback function */
  def inputPortCallbackName(name: String) =
    s"m_p_${name}_in"

  /** Get the name for an async input port pre-message hook function */
  def inputPortHookName(name: String) =
    s"${name}_preMsgHook"

  /** Get the name for an internal interface handler */
  def internalInterfaceHandlerName(name: String) =
    s"${name}_internalInterfaceHandler"

  /** Get the name for a command handler */
  def commandHandlerName(name: String) =
    s"${name}_cmdHandler"

  /** Get the name for an event throttle counter variable */
  def eventThrottleCounterName(name: String) =
    s"m_${name}Throttle"

  /** Get the name for a telemetry channel update flag variable */
  def channelUpdateFlagName(name: String) =
    s"m_first_update_$name"

  /** Get the name for a telemetry channel storage variable */
  def channelStorageName(name: String) =
    s"m_last_$name"

  /** Get the name for a parameter validity flag variable */
  def paramValidityFlagName(name: String) =
    s"m_param_${name}_valid"

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

}

object ComponentCppWriterUtils {

  sealed trait Radix

  case object Decimal extends Radix

  case object Hex extends Radix

}