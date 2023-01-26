package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component events */
case class ComponentEvents (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val sortedEvents = component.eventMap.toList.sortBy(_._1)

  private val throttledEvents = sortedEvents.filter((_, event) =>
    event.throttle match {
      case Some(_) => true
      case None => false
    }
  )

  def getEventConstants: List[CppDoc.Class.Member] = {
    val throttleEnum =
      if throttledEvents.isEmpty then Nil
      else List(
        Line.blank :: lines(
          s"//! Event throttle values: sets initial value of countdown variables"
        ),
        wrapInEnum(
          lines(
            throttledEvents.map((_, event) =>
              writeEnumeratedConstant(
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
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            Line.blank :: lines(s"//! Event IDs"),
            wrapInEnum(
              lines(
                sortedEvents.map((id, event) =>
                  writeEnumeratedConstant(
                    eventIdConstantName(event.getName),
                    id,
                    AnnotationCppWriter.asStringOpt(aNode),
                    ComponentCppWriterUtils.Hex
                  )
                ).mkString("\n")
              )
            ),
            throttleEnum
          ).flatten
        )
      )
    )
  }

  def getEventFunctionMembers: List[CppDoc.Class.Member] = {
    if !hasEvents then Nil
    else List(
      getLoggingFunctions,
      getThrottleFunctions
    ).flatten
  }

  def getEventVariableMembers: List[CppDoc.Class.Member] = {
    if throttledEvents.isEmpty then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PRIVATE"),
            CppDocWriter.writeBannerComment(
              "Counter values for event throttling"
            ),
            Line.blank :: throttledEvents.map((_, event) =>
              line(
                s"NATIVE_UINT_TYPE ${eventThrottleCounterName(event.getName)}; //! Throttle for ${event.getName}"
              )
            )
          ).flatten
        )
      ),
    )
  }

  private def getLoggingFunctions: List[CppDoc.Class.Member] = {
    if !hasEvents then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                "Event logging functions"
              ),
            ).flatten
          )
        ),
      ),
      sortedEvents.map((_, event) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(
              s"Log event ${event.getName}" +
                (AnnotationCppWriter.asStringOpt(event.aNode) match {
                  case Some(s) => s"\n\n$s"
                  case None => ""
                })
            ),
            eventLogName(event),
            writeFormalParamList(
              event.aNode._2.data.params,
              s,
              Nil,
              Some("Fw::LogStringArg"),
              CppWriterLineUtils.Value
            ),
            CppDoc.Type("void"),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getThrottleFunctions: List[CppDoc.Class.Member] = {
    if throttledEvents.isEmpty then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                "Event throttle reset functions"
              ),
            ).flatten
          )
        ),
      ),
      throttledEvents.map((_, event) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Reset throttle value for ${event.getName}"),
            eventThrottleResetName(event),
            Nil,
            CppDoc.Type("void"),
            Nil
          )
        )
      )
    ).flatten
  }

}