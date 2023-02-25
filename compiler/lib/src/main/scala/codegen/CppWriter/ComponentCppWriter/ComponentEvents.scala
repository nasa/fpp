package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component events */
case class ComponentEvents (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getConstantMembers: List[CppDoc.Class.Member] = {
    val throttleEnum =
      if throttledEvents.isEmpty then Nil
      else List(
        Line.blank :: lines(
          s"//! Event throttle values: sets initial value of countdown variables"
        ),
        wrapInEnum(
          lines(
            throttledEvents.map((_, event) =>
              writeEnumConstant(
                eventThrottleConstantName(event.getName),
                event.throttle.get,
                Some(s"Throttle reset count for ${event.getName}")
              )
            ).mkString("\n")
          )
        )
      ).flatten

    if !hasEvents then Nil
    else List(
      linesClassMember(
        List(
          Line.blank :: lines(s"//! Event IDs"),
          wrapInEnum(
            lines(
              sortedEvents.map((id, event) =>
                writeEnumConstant(
                  eventIdConstantName(event.getName),
                  id,
                  AnnotationCppWriter.asStringOpt(event.aNode),
                  CppWriterUtils.Hex
                )
              ).mkString("\n")
            )
          ),
          throttleEnum
        ).flatten
      )
    )
  }

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      getLoggingFunctions,
      getThrottleFunctions
    ).flatten
  }

  def getVariableMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PRIVATE",
      "Counter values for event throttling",
      throttledEvents.map((_, event) =>
        linesClassMember(
          Line.blank :: lines(
            s"""|//! Throttle for ${event.getName}
                |NATIVE_UINT_TYPE ${eventThrottleCounterName(event.getName)};
                |"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )
  }

  private def getLoggingFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Event logging functions",
      sortedEvents.map((_, event) =>
        functionClassMember(
          Some(
            addSeparatedString(
              s"Log event ${event.getName}",
              AnnotationCppWriter.asStringOpt(event.aNode)
            )
          ),
          eventLogName(event),
          formalParamsCppWriter.write(
            event.aNode._2.data.params,
            Nil,
            Some("Fw::LogStringArg"),
            FormalParamsCppWriter.Value
          ),
          CppDoc.Type("void"),
          Nil
        )
      )
    )
  }

  private def getThrottleFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Event throttle reset functions",
      throttledEvents.map((_, event) =>
        functionClassMember(
          Some(s"Reset throttle value for ${event.getName}"),
          eventThrottleResetName(event),
          Nil,
          CppDoc.Type("void"),
          Nil
        )
      )
    )
  }

  // Get the name for an event ID constant
  private def eventIdConstantName(name: String) =
    s"EVENTID_${name.toUpperCase}"

  // Get the name for an event throttle constant
  private def eventThrottleConstantName(name: String) =
    s"${eventIdConstantName(name)}_THROTTLE"

  // Get the name for an event logging function
  private def eventLogName(event: Event) = {
    val severity = event.aNode._2.data.severity
    val severityStr = severity match {
      case Ast.SpecEvent.ActivityHigh => "ACTIVITY_HI"
      case Ast.SpecEvent.ActivityLow => "ACTIVITY_LO"
      case Ast.SpecEvent.WarningHigh => "WARNING_HI"
      case Ast.SpecEvent.WarningLow => "WARNING_LO"
      case _ => severity.toString.toUpperCase.replace(' ', '_')
    }
    s"log_${severityStr}_${event.getName}"
  }

  // Get the name for an event throttle reset function
  private def eventThrottleResetName(event: Event) =
    s"${eventLogName(event)}_ThrottleClear"

}