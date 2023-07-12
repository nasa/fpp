package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Utilities for writing C++ component test harness classes */
abstract class ComponentTestUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  val testerBaseClassName: String = s"${name}TesterBase"

  val gTestClassName: String = s"${name}GTestBase"

  val testImplClassName: String = s"${name}Tester"

  val historySizeConstantName: String = "MAX_HISTORY_SIZE"

  val queueDepthConstantName: String = "TEST_INSTANCE_QUEUE_DEPTH"

  val idConstantName: String = "TEST_INSTANCE_ID"

  val hasHistories: Boolean =
    typedOutputPorts.nonEmpty ||
    hasCommands ||
    hasParameters ||
    hasEvents ||
    hasCommands

  /** Get the name for the corresponding input port for an output port */
  def inputPortName(name: String) =
    s"from_$name"

  /** Get the name for the corresponding output port for an input port */
  def outputPortName(name: String) =
    s"to_$name"

    /** Get the corresponding tester port name for a port in the component under test */
  def portName(p: PortInstance) =
    p.getDirection.get match {
      case PortInstance.Direction.Input => outputPortName(p.getUnqualifiedName)
      case PortInstance.Direction.Output => inputPortName(p.getUnqualifiedName)
    }

  /** Get the name for a port number getter function */
  override def portNumGetterName(p: PortInstance) =
    s"getNum_${portName(p)}"

  /** Get the name for a port variable */
  override def portVariableName(p: PortInstance) =
    s"m_${portName(p)}"

  /** Get the name for a to port connection status query function */
  def toPortIsConnectedName(name: String) =
    s"isConnected_${outputPortName(name)}"

  /** Get the name for a to port connector function */
  def toPortConnectorName(name: String) =
    s"connect_${outputPortName(name)}"

  /** Get the name for a to port invocation function */
  def toPortInvokerName(name: String) =
    s"invoke_${outputPortName(name)}"

  /** Get the name for a from getter function */
  def fromPortGetterName(name: String) =
    s"get_${inputPortName(name)}"

  /** Get the name for a from port handler function */
  def fromPortHandlerName(name: String): String =
    inputPortHandlerName(inputPortName(name))

  /** Get the name for a from port handler base function */
  def fromPortHandlerBaseName(name: String): String =
    inputPortHandlerBaseName(inputPortName(name))

  /** Get the name for a from port handler function */
  def fromPortCallbackName(name: String): String =
    s"${inputPortName(name)}_static"

  /** Get the name for a from port push entry function */
  def fromPortPushEntryName(name: String): String =
    s"pushFromPortEntry_$name"

  /** Get the name for a from port history variable */
  def fromPortHistoryName(name: String) =
    s"fromPortHistory_$name"

  /** Get the name for a from port entry struct */
  def fromPortEntryName(name: String) =
    s"FromPortEntry_$name"


  /** Get the name for a send command function */
  def sendCmdName(name: String) =
    s"sendCmd_$name"

  /** Get the name for an event history variable */
  def eventHistoryName(name: String) =
    s"eventHistory_$name"

  /** Get the name for an event entry struct */
  def eventEntryName(name: String) =
    s"EventEntry_$name"

  /** Get the name for a telemetry history variable */
  def tlmHistoryName(name: String) =
    s"tlmHistory_$name"

  /** Get the name for a telemetry entry struct */
  def tlmEntryName(name: String) =
    s"TlmEntry_$name"

}