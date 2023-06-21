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
    if !hasChannels then Nil
    else List(
      linesClassMember(
        List(
          Line.blank :: lines(s"//! Channel IDs"),
          wrapInEnum(
            sortedChannels.flatMap((id, channel) =>
              writeEnumConstant(
                channelIdConstantName(channel.getName),
                id,
                Some(s"Channel ID for ${channel.getName}"),
                CppWriterUtils.Hex
              )
            )
          )
        ).flatten
      )
    )
  }

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    getWriteFunctions
  }

  def getVariableMembers: List[CppDoc.Class.Member] = {
    List(
      addAccessTagAndComment(
        "PRIVATE",
        "First update flags for telemetry channels",
        updateOnChangeChannels.map((_, channel) =>
          linesClassMember(
            lines(
              s"""|
                  |//! Initialized to true; cleared when channel ${channel.getName} is first updated
                  |bool ${channelUpdateFlagName(channel.getName)};
                  |"""
            )
          )
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "PRIVATE",
        "Last value storage for telemetry channels",
        updateOnChangeChannels.map((_, channel) =>
          linesClassMember(
            lines(
              s"""|
                  |//! Records the last emitted value for channel ${channel.getName}
                  |${getChannelType(channel.channelType)} ${channelStorageName(channel.getName)};
                  |"""
            )
          )
        ),
        CppDoc.Lines.Hpp
      )
    ).flatten
  }

  private def getWriteFunctions: List[CppDoc.Class.Member] = {
    def writeBody(channel: TlmChannel) = intersperseBlankLines(
      List(
        channel.update match {
          case Ast.SpecTlmChannel.OnChange => lines(
            s"""|// Check to see if it is the first time
                |if (not this->${channelUpdateFlagName(channel.getName)}) {
                |  // Check to see if value has changed. If not, don't write it.
                |  if (arg == this->${channelStorageName(channel.getName)}) {
                |    return;
                |  }
                |  else {
                |    this->${channelStorageName(channel.getName)} = arg;
                |  }
                |}
                |else {
                |  this->${channelUpdateFlagName(channel.getName)} = false;
                |  this->${channelStorageName(channel.getName)} = arg;
                |}
                |"""
          )
          case _ => Nil
        },
        wrapInIf(
          s"this->${portVariableName(tlmPort.get)}[0].isConnected()",
          List.concat(
            lines(
              s"""|if (
                  |  this->${portVariableName(timeGetPort.get)}[0].isConnected() &&
                  |  (_tlmTime ==  Fw::ZERO_TIME)
                  |) {
                  |  this->${portVariableName(timeGetPort.get)}[0].invoke(_tlmTime);
                  |}
                  |
                  |Fw::TlmBuffer _tlmBuff;
                  |"""
            ),
            lines(
              channel.channelType match {
                case t: Type.String =>
                  s"Fw::SerializeStatus _stat = arg.serialize(_tlmBuff, ${stringCppWriter.getSize(t)});"
                case _ =>
                  "Fw::SerializeStatus _stat = _tlmBuff.serialize(arg);"
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
                  |_id = this->getIdBase() + ${channelIdConstantName(channel.getName)};
                  |
                  |this->${portVariableName(tlmPort.get)}[0].invoke(
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

    addAccessTagAndComment(
      "PROTECTED",
      "Telemetry write functions",
      sortedChannels.map((_, channel) =>
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
          writeBody(channel)
        )
      )
    )
  }

  private def writeChannelParam(t: Type) = {
    val typeName = getChannelType(t)

    t match {
      case t if s.isPrimitive(t, typeName) => typeName
      case _ => s"const $typeName&"
    }
  }

  private def channelIdConstantName(name: String) =
    s"CHANNELID_${name.toUpperCase}"

  private def channelWriteFunctionName(name: String) =
    s"tlmWrite_$name"

}