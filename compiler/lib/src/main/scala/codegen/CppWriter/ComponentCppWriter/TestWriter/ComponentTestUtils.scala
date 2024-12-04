package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Utilities for writing C++ component test harness classes */
abstract class ComponentTestUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val name = componentName

  val testerBaseClassName: String = s"${name}TesterBase"

  val gTestClassName: String = s"${name}GTestBase"

  val testImplClassName: String = s"${name}Tester"

  val historySizeConstantName: String = "MAX_HISTORY_SIZE"

  val queueDepthConstantName: String = "TEST_INSTANCE_QUEUE_DEPTH"

  val idConstantName: String = "TEST_INSTANCE_ID"

  val hasTypedOutputPorts = typedOutputPorts.nonEmpty

  val hasHistories: Boolean =
    hasTypedOutputPorts ||
    hasCommands ||
    hasParameters ||
    hasEvents ||
    hasCommands ||
    hasDataProducts

  val inputPorts: List[PortInstance] = List.concat(
    specialInputPorts,
    typedInputPorts,
    serialInputPorts,
  )

  val outputPorts: List[PortInstance] = List.concat(
    specialOutputPorts,
    typedOutputPorts,
    serialOutputPorts,
  )

  val constructorParams: List[CppDoc.Function.Param] = List(
    CppDoc.Function.Param(
      CppDoc.Type("const char* const"),
      "compName",
      Some("The component name")
    ),
    CppDoc.Function.Param(
      CppDoc.Type("const U32"),
      "maxHistorySize",
      Some("The maximum size of each history")
    )
  )

  val timeTagParam: CppDoc.Function.Param = CppDoc.Function.Param(
    CppDoc.Type("const Fw::Time&"),
    "timeTag",
    Some("The time")
  )

  private val callSiteParams: List[CppDoc.Function.Param] = List(
    CppDoc.Function.Param(
      CppDoc.Type("const char* const"),
      "__callSiteFileName",
      Some("The name of the file containing the call site")
    ),
    CppDoc.Function.Param(
      CppDoc.Type("const U32"),
      "__callSiteLineNumber",
      Some("The line number of the call site")
    )
  )

  val sizeAssertionFunctionParams: List[CppDoc.Function.Param] =
    callSiteParams ++ List(
      CppDoc.Function.Param(
        CppDoc.Type("const U32"),
        "size",
        Some("The asserted size")
      )
    )

  val assertionFunctionParams: List[CppDoc.Function.Param] =
    callSiteParams ++ List(
      CppDoc.Function.Param(
        CppDoc.Type("const U32"),
        "__index",
        Some("The index")
      )
    )

  def wrapClassMemberInTextLogGuard(
    member: CppDoc.Class.Member,
    output: CppDoc.Lines.Output = CppDoc.Lines.Both
  ): List[CppDoc.Class.Member] =
    wrapClassMembersInIfDirective(
      "\n#if FW_ENABLE_TEXT_LOGGING",
      List(member),
      output
    )

  def wrapClassMembersInTextLogGuard(
    members: List[CppDoc.Class.Member],
    output: CppDoc.Lines.Output = CppDoc.Lines.Both
  ): List[CppDoc.Class.Member] =
    wrapClassMembersInIfDirective(
      "\n#if FW_ENABLE_TEXT_LOGGING",
      members,
      output
    )

  def writeValue(value: String, t: Type): String =
    t match {
      case Type.String(_) => s"$value.toChar()"
      case _ => value
    }

  def writeAssertEq(t: Type): String =
    t match {
      case Type.String(_) => "ASSERT_STREQ"
      case _ => "ASSERT_EQ"
    }

  def writeEventValue(value: String, typeName: String): String =
    typeName match {
      case "Fw::StringBase" => s"$value.toChar()"
      case _ => value
    }

  def writeEventAssertEq(typeName: String): String =
    typeName match {
      case "Fw::StringBase" => "ASSERT_STREQ"
      case _ => "ASSERT_EQ"
    }

  def writeCppType(t: Type, stringRepOpt: Option[String] = None): String = {
    val typeName = stringRepOpt match {
      case Some(stringRep) => TypeCppWriter.getName(s, t, stringRep)
      case None => TypeCppWriter.getName(s, t)
    }
    t match {
      case t if s.isPrimitive(t, typeName) => s"const $typeName"
      case _: Type.String => stringRepOpt match {
        case Some(stringRep) => s"const $stringRep&"
        case _ => s"const char* const"
      }
      case _ => s"const $typeName&"
    }
  }

  /** Append a trailing backslash at the end of every line */
  def writeMacro(s: String): List[Line] =
    Line.addSuffix(lines(s), "\\")

  /** Get the name for the corresponding input port for an output port */
  def inputPortName(name: String) =
    s"from_$name"

  /** Get the name for the corresponding output port for an input port */
  def outputPortName(name: String) =
    s"to_$name"

    /** Get the corresponding tester port name for a port in the component under test */
  def testerPortName(p: PortInstance) =
    p.getDirection.get match {
      case PortInstance.Direction.Input => outputPortName(p.getUnqualifiedName)
      case PortInstance.Direction.Output => inputPortName(p.getUnqualifiedName)
    }

  /** Get the name for a tester port number getter function */
  def testerPortNumGetterName(p: PortInstance) = s"getNum_${testerPortName(p)}"

  /** Get the name for a tester variable */
  def testerPortVariableName(p: PortInstance) = s"m_${testerPortName(p)}"

  /** Get the name for a to port connection status query function */
  def toPortIsConnectedName(name: String) =
    s"isConnected_${outputPortName(name)}"

  /** Get the name for a to port connector function */
  def toPortConnectorName(name: String) =
    s"connect_${outputPortName(name)}"

  /** Get the name for a to port invocation function */
  def toPortInvokerName(name: String) =
    s"invoke_${outputPortName(name)}"

  /** Get the name for a from port getter function */
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

  /** Get the name for a from port history size variable */
  def fromPortHistorySizeName(name: String) =
    s"fromPortHistorySize_$name"

  /** Get the name for a from port history variable */
  def fromPortHistoryName(name: String) =
    s"fromPortHistory_$name"

  /** Get the name for a from port entry struct */
  def fromPortEntryName(name: String) =
    s"FromPortEntry_$name"

  /** Get the name for a from port assertion function */
  def fromPortAssertionFuncName(name: String) =
    s"assert_${inputPortName(name)}"

  /** Get the name for a from port size assertion function */
  def fromPortSizeAssertionFuncName(name: String) =
    s"${fromPortAssertionFuncName(name)}_size"

  /** Get the name for a send command function */
  def commandSendName(name: String) =
    s"sendCmd_$name"

  /** Get the name for an event handler function */
  def eventHandlerName(event: Event) =
    s"logIn_${writeSeverity(event)}_${event.getName}"

  /** Get the name for an event size variable */
  def eventSizeName(name: String) =
    s"eventsSize_$name"

  /** Get the name for an event history variable */
  def eventHistoryName(name: String) =
    s"eventHistory_$name"

  /** Get the name for an event entry struct */
  def eventEntryName(name: String) =
    s"EventEntry_$name"

  /** Get the name for an event assertion function */
  def eventAssertionFuncName(name: String) =
    s"assertEvents_$name"

  /** Get the name for an event size assertion function */
  def eventSizeAssertionFuncName(name: String) =
    s"${eventAssertionFuncName(name)}_size"

  /** Get the name for a telemetry handler function */
  def tlmHandlerName(name: String) =
    s"tlmInput_$name"

  /** Get the name for a telemetry history variable */
  def tlmHistoryName(name: String) =
    s"tlmHistory_$name"

  /** Get the name for a telemetry entry struct */
  def tlmEntryName(name: String) =
    s"TlmEntry_$name"

  /** Get the name for a telemetry assertion function */
  def tlmAssertionFuncName(name: String) =
    s"assertTlm_$name"

  /** Get the name for a telemetry size assertion function */
  def tlmSizeAssertionFuncName(name: String) =
    s"${tlmAssertionFuncName(name)}_size"

  /** Get the name for a parameter variable */
  def paramVariableName(name: String) =
    s"m_param_$name"

  /** Get the name for a parameter valid variable */
  override def paramValidityFlagName(name: String) =
    s"m_param_${name}_valid"

  /** Get the name for a parameter setter */
  def paramSetName(name: String) =
    s"paramSet_$name"

  /** Get the name for a parameter send function */
  def paramSendName(name: String) =
    s"paramSend_$name"

  /** Get the name for a parameter save function */
  def paramSaveName(name: String) =
    s"paramSave_$name"

  /** Queries whether a port instance is used for testing */
  def portInstanceIsUsed(p: PortInstance): Boolean =
    p match {
      case PortInstance.Special(aNode, _, _, _, _) =>
        import Ast.SpecPortInstance._
        val spec @ Special(_, kind, _, _, _) = aNode._2.data
        kind match {
          case CommandRecv => hasCommands
          case CommandReg => hasCommands
          case CommandResp => hasCommands
          case Event => hasEvents
          case ParamGet => hasParameters
          case ParamSet => hasParameters
          case ProductGet => component.hasDataProducts
          case ProductRecv => component.hasDataProducts
          case ProductRequest => component.hasDataProducts
          case ProductSend => component.hasDataProducts
          case Telemetry => hasTelemetry
          case TextEvent => hasEvents
          case TimeGet => true
        }
      case _ => true
    }

}
