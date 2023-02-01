package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component telemetry channels */
case class ComponentTelemetry (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val sortedChannels = component.tlmChannelMap.toList.sortBy(_._1)

  private val updateOnChangeChannels = sortedChannels.filter((_, channel) =>
    channel.update match {
      case Ast.SpecTlmChannel.OnChange => true
      case _ => false
    }
  )

  def getConstantMembers: List[CppDoc.Class.Member] = {
    if !hasChannels then Nil
    else List(
      linesClassMember(
        List(
          Line.blank :: lines(s"//! Channel IDs"),
          wrapInEnum(
            lines(
              sortedChannels.map((id, channel) =>
                writeEnumConstant(
                  channelIdConstantName(channel.getName),
                  id,
                  Some(s"Channel ID for ${channel.getName}"),
                  ComponentCppWriterUtils.Hex
                )
              ).mkString("\n")
            )
          )
        ).flatten
      )
    )
  }

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    if !hasChannels then Nil
    else getWriteFunctions
  }

  def getVariableMembers: List[CppDoc.Class.Member] = {
    if !hasChannels then Nil
    else List(
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("PRIVATE"),
          CppDocWriter.writeBannerComment(
            "First update flags for telemetry channels"
          ),
          Line.blank :: updateOnChangeChannels.flatMap((_, channel) =>
            lines(
              s"""|//! Initialized to true; cleared when channel ${channel.getName} is first updated
                  |bool ${channelUpdateFlagName(channel.getName)};
                  |"""
            ),
          ),
          CppDocHppWriter.writeAccessTag("PRIVATE"),
          CppDocWriter.writeBannerComment(
            "Last value storage for telemetry channels"
          ),
          Line.blank :: updateOnChangeChannels.flatMap((_, channel) =>
            lines(
              s"""|//! Records the last emitted value for channel ${channel.getName}
                  |${writeChannelType(channel.channelType)} ${channelStorageName(channel.getName)};
                  |"""
            )
          )
        ).flatten
      )
    )
  }

  private def getWriteFunctions: List[CppDoc.Class.Member] = {
    List(
      List(
        linesClassMember(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              "Telemetry write functions"
            ),
          ).flatten
        )
      ),
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
          Nil
        )
      )
    ).flatten
  }

  private def writeChannelType(t: Type) =
    writeCppTypeName(t, s, Nil, Some("Fw::TlmString"))

  private def writeChannelParam(t: Type) = {
    val typeName = writeChannelType(t)

    t match {
      case t if s.isPrimitive(t, typeName) => typeName
      case _ => s"const $typeName&"
    }
  }

  private def channelIdConstantName(name: String) =
    s"CHANNELID_${name.toUpperCase}"

  private def channelWriteFunctionName(name: String) =
    s"tlmWrite_$name"

  private def channelUpdateFlagName(name: String) =
    s"m_first_update_$name"

  private def channelStorageName(name: String) =
    s"m_last_$name"

}