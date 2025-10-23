package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component telemetry channels */
case class ComponentTelemetry (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getConstantMembers: List[CppDoc.Class.Member] = {
    lazy val channelIds = sortedChannels.flatMap((id, channel) =>
      writeEnumConstant(
        channelIdConstantName(channel.getName),
        id,
        Some(s"Channel ID for ${channel.getName}"),
        CppWriterUtils.Hex
      )
    )
    lazy val member = linesClassMember(
      Line.blank ::
      List.concat(
        lines(s"//! Channel IDs"),
        wrapInEnum(channelIds)
      )
    )
    guardedList (hasChannels) (List(member))
  }

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    getWriteFunctions
  }

  def getVariableMembers: List[CppDoc.Class.Member] = {
    List.concat(
      addAccessTagAndComment(
        "private",
        "First update flags for telemetry channels",
        updateOnChangeChannels.map((_, channel) =>
          linesClassMember(
            lines(
              s"""|
                  |//! Initialized to true; cleared when channel ${channel.getName} is first updated
                  |bool ${channelUpdateFlagName(channel.getName)} = true;
                  |"""
            )
          )
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "private",
        "Last value storage for telemetry channels",
        updateOnChangeChannels.map((_, channel) => {
          val channelName = channel.getName
          val channelType = writeChannelType(channel.channelType, "Fw::TlmString")
          val channelStoreName = channelStorageName(channel.getName)
          linesClassMember(
            lines(
              s"""|
                  |//! Records the last emitted value for channel $channelName
                  |$channelType $channelStoreName = {};
                  |"""
            )
          )
        }),
        CppDoc.Lines.Hpp
      )
    )
  }

  private def getWriteFunction(channel: TlmChannel) = {
    val body = {
      val timeGetPortName = timeGetPort.get.getUnqualifiedName
      val timeGetPortInvokerName = outputPortInvokerName(timeGetPortName)
      val timeGetPortIsConnected = outputPortIsConnectedName(timeGetPortName)
      val tlmPortName = tlmPort.get.getUnqualifiedName
      val tlmPortIsConnected = outputPortIsConnectedName(tlmPortName)
      val tlmPortInvokerName = outputPortInvokerName(tlmPort.get)
      val idConstantName = channelIdConstantName(channel.getName)
      intersperseBlankLines(
        List(
          channel.update match {
            case Ast.SpecTlmChannel.OnChange =>
              val updateFlagName = channelUpdateFlagName(channel.getName)
              val storageName = channelStorageName(channel.getName)
              lines(
                s"""|// Check to see if it is the first time
                    |if (not this->$updateFlagName) {
                    |  // Check to see if value has changed. If not, don't write it.
                    |  if (arg == this->$storageName) {
                    |    return;
                    |  }
                    |  else {
                    |    this->$storageName = arg;
                    |  }
                    |}
                    |else {
                    |  this->$updateFlagName = false;
                    |  this->$storageName = arg;
                    |}
                    |"""
              )
            case _ => Nil
          },
          wrapInIf(
            s"this->$tlmPortIsConnected(0)",
            List.concat(
              lines(
                s"""|if (
                    |  this->$timeGetPortIsConnected(0) &&
                    |  (_tlmTime ==  Fw::ZERO_TIME)
                    |) {
                    |  this->$timeGetPortInvokerName(0, _tlmTime);
                    |}
                    |
                    |Fw::TlmBuffer _tlmBuff;
                    |"""
              ),
              lines(
                channel.channelType.getUnderlyingType match {
                  case t: Type.String =>
                    val serialSize = writeStringSize(s, t)
                    s"""|Fw::SerializeStatus _stat = arg.serializeTo(
                        |  _tlmBuff,
                        |  FW_MIN(FW_TLM_STRING_MAX_SIZE, $serialSize)
                        |);
                        |"""
                  case _ =>
                    "Fw::SerializeStatus _stat = _tlmBuff.serializeFrom(arg);"
                }
              ),
              lines(
                s"""|FW_ASSERT(
                    |  _stat == Fw::FW_SERIALIZE_OK,
                    |  static_cast<FwAssertArgType>(_stat)
                    |);
                    |
                    |FwChanIdType _id;
                    |
                    |_id = this->getIdBase() + $idConstantName;
                    |
                    |this->$tlmPortInvokerName(
                    |  0,
                    |  _id,
                    |  _tlmTime,
                    |  _tlmBuff
                    |);
                    |"""
              )
            )
          )
        )
      )
    }
    functionClassMember(
      Some(
        addSeparatedString(
          s"Write telemetry channel ${channel.getName}",
          AnnotationCppWriter.asStringOpt(channel.aNode)
        )
      ),
      channelWriteFunctionName(channel.getName),
      List(
        CppDoc.Function.Param(
          CppDoc.Type(writeChannelParam(channel.channelType)),
          "arg",
          Some("The telemetry value")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("Fw::Time"),
          "_tlmTime",
          Some("Timestamp. Default: unspecified, request from getTime port"),
          Some("Fw::Time()")
        )
      ),
      CppDoc.Type("void"),
      body,
      CppDoc.Function.NonSV,
      channel.update match {
        case Ast.SpecTlmChannel.OnChange => CppDoc.Function.NonConst
        case _ => CppDoc.Function.Const
      }
    )
  }

  private def getWriteFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "protected",
      "Telemetry write functions",
      sortedChannels.map((_, channel) => getWriteFunction(channel))
    )

  private def writeChannelParam(t: Type) = {
    val typeName = writeChannelType(t)
    t match {
      case t if s.isPrimitive(t, typeName) => typeName
      case _ => s"const $typeName&"
    }
  }

  private def channelWriteFunctionName(name: String) =
    s"tlmWrite_$name"

}
