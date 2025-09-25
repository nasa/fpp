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
          throttledEvents.flatMap((_, event) =>
            writeEnumConstant(
              eventThrottleConstantName(event.getName),
              event.throttle.get,
              Some(s"Throttle reset count for ${event.getName}")
            )
          )
        )
      ).flatten

    if !hasEvents then Nil
    else List(
      linesClassMember(
        List(
          Line.blank :: lines(s"//! Event IDs"),
          wrapInEnum(
            sortedEvents.flatMap((id, event) =>
              writeEnumConstant(
                eventIdConstantName(event.getName),
                id,
                AnnotationCppWriter.asStringOpt(event.aNode),
                CppWriterUtils.Hex
              )
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
      "private",
      "Counter values for event throttling",
      throttledEvents.map((_, event) =>
        linesClassMember(
          Line.blank :: lines(
            s"""|//! Throttle for ${event.getName}
                |std::atomic<FwIndexType> ${eventThrottleCounterName(event.getName)};
                |"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )
  }

  private def getLoggingFunctions: List[CppDoc.Class.Member] = {
    def writeLogBody(id: Event.Id, event: Event) =
      line("// Emit the event on the log port") ::
        wrapInIf(
          s"this->${portVariableName(eventPort.get)}[0].isConnected()",
          intersperseBlankLines(
            List(
              List.concat(
                lines("Fw::LogBuffer _logBuff;"),
                eventParamTypeMap(id) match {
                  case Nil => Nil
                  case _ => lines("Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;")
                }
              ),
              List.concat(
                lines("#if FW_AMPCS_COMPATIBLE"),
                eventParamTypeMap(id) match {
                  case Nil => lines("Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;\n")
                  case _ => Nil
                },
                event.aNode._2.data.severity match {
                  case Ast.SpecEvent.Fatal if eventParamTypeMap(id).nonEmpty => lines(
                    s"""|// Serialize the number of arguments
                        |_status = _logBuff.serializeFrom(static_cast<U8>(${eventParamTypeMap(id).length} + 1));
                        |FW_ASSERT(
                        |  _status == Fw::FW_SERIALIZE_OK,
                        |  static_cast<FwAssertArgType>(_status)
                        |);
                        |
                        |// For FATAL, add stack size of 4 and a dummy entry. No support for stacks yet.
                        |_status = _logBuff.serializeFrom(static_cast<U8>(4));
                        |FW_ASSERT(
                        |  _status == Fw::FW_SERIALIZE_OK,
                        |  static_cast<FwAssertArgType>(_status)
                        |);
                        |
                        |_status = _logBuff.serializeFrom(static_cast<U32>(0));
                        |FW_ASSERT(
                        |  _status == Fw::FW_SERIALIZE_OK,
                        |  static_cast<FwAssertArgType>(_status)
                        |);
                        |"""
                  )
                  case _ => lines(
                    s"""|// Serialize the number of arguments
                        |_status = _logBuff.serializeFrom(static_cast<U8>(${eventParamTypeMap(id).length}));
                        |FW_ASSERT(
                        |  _status == Fw::FW_SERIALIZE_OK,
                        |  static_cast<FwAssertArgType>(_status)
                        |);
                        |"""
                  )
                },
                lines("#endif")
              ),
              intersperseBlankLines(
                eventParamTypeMap(id).map((name, typeName, ty) => {

                  List.concat(
                    ty.getUnderlyingType match {
                      case t: Type.String =>
                        val serialSize = writeStringSize(s, t)
                        lines(
                          s"_status = $name.serializeTo(_logBuff, FW_MIN(FW_LOG_STRING_MAX_SIZE, $serialSize));"
                        )
                      case t => lines(
                        s"""|#if FW_AMPCS_COMPATIBLE
                            |// Serialize the argument size
                            |_status = _logBuff.serializeFrom(
                            |  static_cast<U8>(${writeStaticSerializedSizeExpr(s, t, typeName)})
                            |);
                            |FW_ASSERT(
                            |  _status == Fw::FW_SERIALIZE_OK,
                            |  static_cast<FwAssertArgType>(_status)
                            |);
                            |#endif
                            |_status = _logBuff.serializeFrom($name);
                            |"""
                      )
                    },
                    lines(
                      """|FW_ASSERT(
                         |  _status == Fw::FW_SERIALIZE_OK,
                         |  static_cast<FwAssertArgType>(_status)
                         |);
                         |"""
                    )
                  )
                })
              ),
              lines(
                s"""|this->${portVariableName(eventPort.get)}[0].invoke(
                    |  _id,
                    |  _logTime,
                    |  Fw::LogSeverity::${writeSeverity(event)},
                    |  _logBuff
                    |);
                    |"""
              )
            )
          )
        )
    def writeTextLogBody(event: Event) = List.concat(
      lines(
        s"""|// Emit the event on the text log port
            |#if FW_ENABLE_TEXT_LOGGING
            |"""
      ),
      wrapInIf(
        s"this->${portVariableName(textEventPort.get)}[0].isConnected()",
        intersperseBlankLines(
          List(
            lines(
              s"""|#if FW_OBJECT_NAMES == 1
                  |const char* _formatString =
                  |  "(%s) %s: ${writeEventFormat(event)}";
                  |#else
                  |const char* _formatString =
                  |  "%s: ${writeEventFormat(event)}";
                  |#endif
                  |"""
            ),
            event.aNode._2.data.params.flatMap(param =>
              s.a.typeMap(param._2.data.typeName.id) match {
                case Type.String(_) => Nil
                case t if s.isPrimitive(t, writeFormalParamType(param._2.data)) => Nil
                case _ => lines(
                  s"""|Fw::String ${param._2.data.name}Str;
                      |${param._2.data.name}.toString(${param._2.data.name}Str);
                      |"""
                )
              }
            ),
            List.concat(
              lines(
                s"""|Fw::TextLogString _logString;
                    |_logString.format(
                    |  _formatString,
                    |#if FW_OBJECT_NAMES == 1
                    |  this->m_objName.toChar(),
                    |#endif
                    |"""
              ),
              lines(
                (s"\"${event.getName} \"" ::
                  event.aNode._2.data.params.map(param => {
                    val name = param._2.data.name
                    s.a.typeMap(param._2.data.typeName.id) match {
                      case Type.String(_) => s"$name.toChar()"
                      case t if s.isPrimitive(t, writeFormalParamType(param._2.data)) =>
                        promoteF32ToF64 (t) (name)
                      case _ => s"${name}Str.toChar()"
                    }
                  })).mkString(",\n")
              ).map(indentIn),
              lines(");")
            ),
            lines(
              s"""|this->${portVariableName(textEventPort.get)}[0].invoke(
                  |  _id,
                  |  _logTime,
                  |  Fw::LogSeverity::${writeSeverity(event)},
                  |  _logString
                  |);
                  |"""
            )
          )
        )
      ),
      lines("#endif")
    )
    def writeBody(id: Event.Id, event: Event) = intersperseBlankLines(
      List(
        event.throttle match {
          case None => Nil
          case Some(_) => lines(
            s"""|// Check throttle value
                |if (this->${eventThrottleCounterName(event.getName)} >= ${eventThrottleConstantName(event.getName)}) {
                |  return;
                |}
                |else {
                |  (void) this->${eventThrottleCounterName(event.getName)}.fetch_add(1);
                |}
                |"""
          )
        },
        lines(
          s"""|// Get the time
              |Fw::Time _logTime;
              |if (this->${portVariableName(timeGetPort.get)}[0].isConnected()) {
              |  this->${portVariableName(timeGetPort.get)}[0].invoke(_logTime);
              |}
              |
              |FwEventIdType _id = static_cast<FwEventIdType>(0);
              |
              |_id = this->getIdBase() + ${eventIdConstantName(event.getName)};
              |"""
        ),
        writeLogBody(id, event),
        writeTextLogBody(event)
      )
    )

    addAccessTagAndComment(
      "protected",
      "Event logging functions",
      sortedEvents.map((id, event) =>
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
            "Fw::StringBase",
            FormalParamsCppWriter.Value
          ),
          CppDoc.Type("void"),
          writeBody(id, event),
          CppDoc.Function.NonSV,
          event.throttle match {
            case Some(_) => CppDoc.Function.NonConst
            case None => CppDoc.Function.Const
          }
        )
      )
    )
  }

  private def getThrottleFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Event throttle reset functions",
      throttledEvents.map((_, event) =>
        functionClassMember(
          Some(s"Reset throttle value for ${event.getName}"),
          eventThrottleResetName(event),
          Nil,
          CppDoc.Type("void"),
          lines(
            s"""|// Reset throttle counter
                |this->${eventThrottleCounterName(event.getName)} = 0;
                |"""
          )
        )
      )
    )
  }

  // Get the name for an event throttle constant
  private def eventThrottleConstantName(name: String) =
    s"${eventIdConstantName(name)}_THROTTLE"

  // Get the name for an event logging function
  private def eventLogName(event: Event) =
    s"log_${writeSeverity(event)}_${event.getName}"

  // Get the name for an event throttle reset function
  private def eventThrottleResetName(event: Event) =
    s"${eventLogName(event)}_ThrottleClear"

}
