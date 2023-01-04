package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Utilities for writing C++ component definitions */
abstract class ComponentCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends CppWriterLineUtils {

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
  private val specialPorts: List[PortInstance.Special] = members.map(member =>
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

  val specialInputPorts: List[PortInstance.Special] =
    filterByPortDirection(specialPorts, PortInstance.Direction.Input)

  val specialOutputPorts: List[PortInstance.Special] =
    filterByPortDirection(specialPorts, PortInstance.Direction.Output)

  /** List of general port instances */
  private val generalPorts: List[PortInstance.General] = members.map(member =>
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

  /** List of input ports */
  private val generalInputPorts: List[PortInstance.General] =
    filterByPortDirection(generalPorts, PortInstance.Direction.Input)

  /** List of typed input ports */
  val typedInputPorts: List[PortInstance.General] = filterTypedPorts(generalInputPorts)

  /** List of serial input ports */
  val serialInputPorts: List[PortInstance.General] = filterSerialPorts(generalInputPorts)

  /** List of guarded input ports */
  val guardedInputPorts: List[PortInstance.General] = filterGuardedInputPorts(generalInputPorts)

  /** List of typed async input ports */
  val typedAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(typedInputPorts)

  /** List of serial async input ports */
  val serialAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(serialInputPorts)

  /** List of general output ports */
  private val outputPorts: List[PortInstance.General] =
    filterByPortDirection(generalPorts, PortInstance.Direction.Output)

  /** List of typed output ports */
  val typedOutputPorts: List[PortInstance.General] = filterTypedPorts(outputPorts)

  /** List of serial output ports */
  val serialOutputPorts: List[PortInstance.General] = filterSerialPorts(outputPorts)

  // Map from a port instance name to a PortCppWriter
  private val portWriterMap =
    List(
      specialInputPorts,
      typedInputPorts,
      specialOutputPorts,
      typedOutputPorts
    ).flatten.map(p =>
      p.getType match {
        case Some(PortInstance.Type.DefPort(symbol)) =>
          Some((p.getUnqualifiedName, PortCppWriter(s, symbol.node)))
        case _ => None
      }
    ).filter(_.isDefined).map(_.get).toMap

  val hasGuardedInputPorts: Boolean = guardedInputPorts.nonEmpty

  val hasSerialAsyncInputPorts: Boolean = serialAsyncInputPorts.nonEmpty

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

  /** Get port params as CppDoc Function Params */
  def getFunctionParams(p: PortInstance): List[CppDoc.Function.Param] =
    p.getType.get match {
      case PortInstance.Type.DefPort(_) =>
        portWriterMap(p.getUnqualifiedName).functionParams
      case PortInstance.Type.Serial =>
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerializeBufferBase&"),
            "buffer",
            Some("The serialization buffer")
          )
        )
    }

  /** Get a port return type as a CppDoc Type */
  def getReturnType(p: PortInstance): CppDoc.Type =
    p.getType.get match {
      case PortInstance.Type.DefPort(_) => CppDoc.Type(
        portWriterMap(p.getUnqualifiedName).returnType
      )
      case PortInstance.Type.Serial => CppDoc.Type(
        "Fw::SerializeStatus"
      )
    }

  def getTypeString(p: PortInstance): String =
    p match {
      case _: PortInstance.General => p.getType.get match {
        case PortInstance.Type.DefPort(_) => "typed"
        case PortInstance.Type.Serial => "serial"
      }
      case _: PortInstance.Special => "special"
      case _: PortInstance.Internal => "internal"
    }

  /** Get the name for a port enumerated constant */
  def portEnumName(name: String, direction: PortInstance.Direction) =
    s"NUM_${name.toUpperCase}_${direction.toString.toUpperCase}_PORTS"

  /** Get the name for a port number getter function */
  def portNumGetterName(name: String, direction: PortInstance.Direction) =
    s"getNum_${name}_${direction.toString.capitalize}Ports"

  /** Get the name for a port member */
  def portMemberName(name: String, direction: PortInstance.Direction) =
    s"m_${name}_${direction.toString.capitalize}Port"

  /** Get the name for an input port getter function */
  def inputPortGetterName(name: String) =
    s"get_${name}_InputPort"

  /** Get the name for an input port handler function */
  def inputPortHandlerName(name: String) =
    s"${name}_handler"

  /** Get the name for an input port handler base-class function */
  def inputPortHandlerBaseName(name: String) =
    s"${name}_handlerBase"

  /** Get the name for an input port callback function */
  def inputPortCallbackName(name: String) =
    s"m_p_${name}_in"

  /** Get the name for an async input port pre-message hook function */
  def asyncInputPortHookName(name: String) =
    s"${name}_preMsgHook"

  /** Get the name for an output port connector function */
  def outputPortConnectorName(name: String) =
    s"set_${name}_OutputPort"

  /** Get the name for an output port connection status function */
  def outputPortIsConnectedName(name: String) =
    s"isConnected_${name}_OutputPort"

  /** Get the name for an output port invocation function */
  def outputPortInvokerName(name: String) =
    s"${name}_out"

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

  private def filterGuardedInputPorts(ports: List[PortInstance.General]) =
    ports.filter(p =>
      p.kind match {
        case PortInstance.General.Kind.GuardedInput => true
        case _ => false
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