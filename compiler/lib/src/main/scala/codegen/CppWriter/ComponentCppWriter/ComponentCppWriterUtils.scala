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

  /** List of general port instances */
  val generalPorts: List[PortInstance.General] = members.map(member =>
    member.node._2 match {
      case Ast.ComponentMember.SpecPortInstance(node) => node.data match {
        case p: Ast.SpecPortInstance.General => component.portMap(p.name) match {
          case i: PortInstance.General => Some(i)
          case _ => None
        }
        case _  => None
      }
      case _ => None
    }).filter(_.isDefined).map(_.get)

  /** List of general input ports */
  private val generalInputPorts: List[PortInstance.General] =
    filterByPortDirection(generalPorts, PortInstance.Direction.Input)

  /** List of general output ports */
  private val outputPorts: List[PortInstance.General] =
    filterByPortDirection(generalPorts, PortInstance.Direction.Output)

  /** List of special port instances */
  val specialPorts: List[PortInstance.Special] = members.map(member =>
    member.node._2 match {
      case Ast.ComponentMember.SpecPortInstance(node) => node.data match {
        case p: Ast.SpecPortInstance.Special => component.portMap(p.name) match {
          case i: PortInstance.Special => Some(i)
          case _ => None
        }
        case _ => None
      }
      case _ => None
    }).filter(_.isDefined).map(_.get)

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

  /** List of events sorted by ID */
  val sortedEvents = component.eventMap.toList.sortBy(_._1)

  /** List of throttled events */
  val throttledEvents = sortedEvents.filter((_, event) =>
    event.throttle match {
      case Some(_) => true
      case None => false
    }
  )

  /** List of channels sorted by ID */
  val sortedChannels = component.tlmChannelMap.toList.sortBy(_._1)

  /** List of channels updated on change */
  val updateOnChangeChannels = sortedChannels.filter((_, channel) =>
    channel.update match {
      case Ast.SpecTlmChannel.OnChange => true
      case _ => false
    }
  )

  /** List of parameters sorted by ID */
  val sortedParams = component.paramMap.toList.sortBy(_._1)

  // Component properties

  val hasGuardedInputPorts: Boolean = generalInputPorts.exists(p =>
    p.kind match {
      case PortInstance.General.Kind.GuardedInput => true
      case _ => false
    }
  )

  val hasSerialAsyncInputPorts: Boolean = serialAsyncInputPorts.nonEmpty

  val hasInternalPorts: Boolean = internalPorts.nonEmpty

  val hasTimeGetPort: Boolean = specialPorts.exists(p =>
    p.aNode._2.data match {
      case Ast.SpecPortInstance.Special(kind, _) => kind match {
        case Ast.SpecPortInstance.TimeGet => true
        case _ => false
      }
      case _ => false
    })

  val hasCommands: Boolean = component.commandMap.nonEmpty

  val hasEvents: Boolean = component.eventMap.nonEmpty

  val hasChannels: Boolean = component.tlmChannelMap.nonEmpty

  val hasParameters: Boolean = component.paramMap.nonEmpty

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

  /** Get the name for a port number getter function */
  def portNumGetterName(name: String, direction: PortInstance.Direction) =
    s"getNum_${name}_${direction.toString.capitalize}Ports"

  /** Get the name for a port variable */
  def portVariableName(name: String, direction: PortInstance.Direction) =
    s"m_${name}_${direction.toString.capitalize}Port"

  /** Get the name for an input port callback function */
  def inputPortCallbackName(name: String) =
    s"m_p_${name}_in"

  /** Get the name for an async input port pre-message hook function */
  def inputPortHookName(name: String) =
    s"${name}_preMsgHook"

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