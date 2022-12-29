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
  val generalPorts: List[PortInstance.General] = members.map(member =>
    member.node._2 match {
      case Ast.ComponentMember.SpecPortInstance(node) => node.data match {
        case p: Ast.SpecPortInstance.General => component.portMap(p.name) match {
          case i: PortInstance.General => Some(i)
          case _ => None
        }
        case _ => None
      }
      case _ => None
    }).filter(_.isDefined).map(_.get)

  /** List of general input ports */
  val inputPorts: List[PortInstance.General] = generalPorts.filter(p =>
    p.getDirection.get match {
      case PortInstance.Direction.Input => true
      case PortInstance.Direction.Output => false
    }
  )

  /** List of typed input ports */
  val typedInputPorts: List[PortInstance.General] = filterTypedPorts(inputPorts)

  /** List of serial input ports */
  val serialInputPorts: List[PortInstance.General] = filterSerialPorts(inputPorts)

  /** List of guarded input ports */
  val guardedInputPorts: List[PortInstance.General] = filterGuardedInputPorts(inputPorts)

  /** List of typed async input ports */
  val typedAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(typedInputPorts)

  /** List of serial async input ports */
  val serialAsyncInputPorts: List[PortInstance.General] = filterAsyncInputPorts(serialInputPorts)

  /** List of general output ports */
  val outputPorts: List[PortInstance.General] = generalPorts.filter(p =>
    p.getDirection.get match {
      case PortInstance.Direction.Input => false
      case PortInstance.Direction.Output => true
    }
  )

  /** List of typed output ports */
  val typedOutputPorts: List[PortInstance.General] = filterTypedPorts(outputPorts)

  /** List of serial output ports */
  val serialOutputPorts: List[PortInstance.General] = filterSerialPorts(outputPorts)

  // Map from a port instance name to a PortCppWriter
  private val portWriterMap =
    (typedInputPorts ++ typedOutputPorts).map(p =>
      p.getType.get match {
        case PortInstance.Type.DefPort(symbol) =>
          Some((p.getUnqualifiedName, PortCppWriter(s, symbol.node)))
        case _ => None
      }
    ).filter(_.isDefined).map(_.get).toMap

  val hasInputPorts: Boolean = inputPorts.nonEmpty

  val hasTypedInputPorts: Boolean = typedInputPorts.nonEmpty

  val hasSerialInputPorts: Boolean = serialInputPorts.nonEmpty

  val hasGuardedInputPorts: Boolean = guardedInputPorts.nonEmpty

  val hasTypedAsyncInputPorts: Boolean = typedAsyncInputPorts.nonEmpty

  val hasSerialAsyncInputPorts: Boolean = serialAsyncInputPorts.nonEmpty

  val hasOutputPorts: Boolean = outputPorts.nonEmpty

  val hasTypedOutputPorts: Boolean = typedOutputPorts.nonEmpty

  val hasSerialOutputPorts: Boolean = serialOutputPorts.nonEmpty

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
  def getFunctionParams(p: PortInstance.General): List[CppDoc.Function.Param] =
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
  def getReturnType(p: PortInstance.General): CppDoc.Type =
    p.getType.get match {
      case PortInstance.Type.DefPort(_) => CppDoc.Type(
        portWriterMap(p.getUnqualifiedName).returnType
      )
      case PortInstance.Type.Serial => CppDoc.Type(
        "Fw::SerializeStatus"
      )
    }

  def getTypeString(p: PortInstance.General): String =
    p.getType.get match {
      case PortInstance.Type.DefPort(_) => "typed"
      case PortInstance.Type.Serial => "serial"
    }

  /** Get the name for an input port getter function */
  def inputGetterName(name: String) =
    s"get_${name}_InputPort"

  /** Get the name for an input port enumerated constant */
  def inputEnumName(name: String) =
    s"NUM_${name.toUpperCase}_INPUT_PORTS"

  /** Get the name for an input port number getter function */
  def inputNumGetterName(name: String) =
    s"getNum_${name}_InputPorts"

  /** Get the name for an input port handler function */
  def inputHandlerName(name: String) =
    s"${name}_handler"

  /** Get the name for an input port handler base-class function */
  def inputHandlerBaseName(name: String) =
    s"${name}_handlerBase"

  /** Get the name for an input port callback function */
  def inputCallbackName(name: String) =
    s"m_p_${name}_in"

  /** Get the name for an async input port pre-message hook function */
  def asyncInputHookName(name: String) =
    s"${name}_preMsgHook"

  /** Get the name for an input port member */
  def inputMemberName(name: String) =
    s"m_${name}_InputPort"

  /** Get the name for an output port number getter function */
  def outputNumGetterName(name: String) =
    s"getNum_${name}_OutputPorts"

  /** Get the name for an output port enumerated constant function */
  def outputEnumName(name: String) =
    s"NUM_${name.toUpperCase}_OUTPUT_PORTS"

  /** Get the name for an output port connector function */
  def outputConnectorName(name: String) =
    s"set_${name}_OutputPort"

  /** Get the name for an output port connection status function */
  def outputIsConnectedName(name: String) =
    s"isConnected_${name}_OutputPort"

  /** Get the name for an output port invocation function */
  def outputInvokerName(name: String) =
    s"${name}_out"

  /** Get the name for an output port member */
  def outputMemberName(name: String) =
    s"m_${name}_OutputPort"

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