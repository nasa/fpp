package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Writes out C++ for struct definitions */
case class TlmPacketSetCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val data = node.data

  private val name = data.name

  private val Some(t) = s.a.topology

  private val d = s.a.dictionaryMap(Symbol.Topology(t.aNode))

  private val tps = d.tlmPacketSetMap(name)

  private val packetList = tps.packetMap.values.toList.sortBy(_.getName)

  private val topQualifiedName = s"${t.getName}_$name"

  private val namespaceName = s"${topQualifiedName}Packets"

  private val fileName = ComputeCppFiles.FileNames.getTlmPacketSet(topQualifiedName)

  def write: CppDoc = {
    CppWriter.createCppDoc(
      s"$name telemetry packets",
      fileName,
      getIncludeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getAnonymousNamespaceMember: CppDoc.Member =
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("File-local data structures"),
        addBlankPrefix(
          wrapInAnonymousNamespace(
            addBlankPostfix(
              List.concat(
                getCppChannelArraysLines,
                getCppPacketsLines
              )
            )
          )
        )
      ),
      CppDoc.Lines.Cpp
    )

  private def getCppChannelArraysLines: List[Line] =
    lines(
      """|
         |// TODO: Channel arrays"""
    )

  private def getCppIncludesMember: CppDoc.Member = {
    val headers = List(
      "Fw/Types/Assert.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp",
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(Line.blank :: headers, CppDoc.Lines.Cpp)
  }

  private def getCppMembers: List[CppDoc.Member] =
    List(
      getStaticAssertMember,
      getAnonymousNamespaceMember,
      getCppVarMember
    )

  private def getCppVarMember: CppDoc.Member =
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Extern variables"),
        lines(
          """|
             |// TODO: Variables"""
        )
      ),
      CppDoc.Lines.Cpp
    )

  private def getCppPacketsLines: List[Line] =
    lines(
      """|
         |// TODO: Packets"""
    )

  private def getHppConstantMembers: List[CppDoc.Member] =
    List(
      linesMember(
        List.concat(
          CppDocWriter.writeBannerComment("Constants"),
          addBlankPrefix(
            line("//! The packet sizes") ::
            wrapInEnumClass(
              "PacketDataSize",
              packetList.flatMap(writePacketDataSizeEnum),
              Some("FwSizeType")
            )
          )
        )
      )
    )

  private def getHppIncludesMember: CppDoc.Member = {
    val headers = List(
      "Fw/Types/StringBase.hpp",
      "Fw/Time/Time.hpp",
      "Svc/TlmPacketizer/TlmPacketizerTypes.hpp"
    ).map(CppWriter.headerString)
    linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getHppMembers: List[CppDoc.Member] =
    List.concat(
      getHppConstantMembers,
      getHppVarMembers
    )

  private def getHppVarMembers: List[CppDoc.Member] =
    List(
      linesMember(
        List.concat(
          CppDocWriter.writeBannerComment("Extern variables"),
          lines(
            s"""|
                |//! The list of packets
                |extern const Svc::TlmPacketizerPacketList packetList;
                |
                |//! The omitted channels
                |extern const Svc::TlmPacketizerPacket omittedChannels;"""
          )
        )
      )
    )

  private def getIncludeGuard: String = {
    val guard = s.a.getEnclosingNames(Symbol.Topology(t.aNode)) match {
      case Nil => namespaceName
      case names =>
        val qualifier = CppWriterState.identFromQualifiedName(
          Name.Qualified.fromIdentList(names)
        )
        s"${qualifier}_${namespaceName}"
    }
    s"${guard}_HPP"
  }

  private def getMembers: List[CppDoc.Member] = {
    val nsil = s.getNamespaceIdentList(Symbol.Topology(t.aNode)) :+
      namespaceName
    val members = getHppMembers ++ getCppMembers
    getHppIncludesMember :: getCppIncludesMember ::
      wrapInNamespaces(nsil, members)
  }

  private def getStaticAssertMember: CppDoc.Member = {
    def writeStaticAssert(tp: TlmPacket): List[Line] = {
      val name = tp.getName
      lines(
        s"""|static_assert(
            |  static_cast<FwSizeType>(PacketDataSize::$name) <= packetMaxDataSize,
            |  "packet data must fit in max data size"
            |);"""
      )
    }
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Static assertions"),
        addBlankPrefix(
          wrapInAnonymousNamespace(
            List.concat(
              lines(
                """|
                   |// The size of a packet header
                   |constexpr FwSizeType packetHeaderSize = Fw::Time::SERIALIZED_SIZE + sizeof(FwTlmPacketizeIdType) +
                   |  sizeof(FwPacketDescriptorType);
                   |
                   |// A packet header must fit in a com buffer
                   |static_assert(FW_COM_BUFFER_MAX_SIZE >= packetHeaderSize, "packet header must fit in com buffer");
                   |
                   |// The max data size in a com buffer
                   |constexpr FwSizeType packetMaxDataSize = FW_COM_BUFFER_MAX_SIZE - packetHeaderSize;"""
              ),
              addBlankPrefix(addBlankPostfix(packetList.flatMap(writeStaticAssert)))
            )
          )
        )
      ),
      CppDoc.Lines.Cpp
    )
  }

  private def writeChannelSize(id: TlmChannel.Id) = {
    val entry = d.tlmChannelEntryMap(id)
    entry.tlmChannel.channelType match {
      case (ts: Type.String) =>
        val stringSize = writeStringSize(s, ts)
        s"Fw::StringBase::STATIC_SERIALIZED_SIZE(FW_MIN($stringSize, FW_TLM_STRING_MAX_SIZE))"
      case t =>
        val tn = TypeCppWriter.getName(s, t)
        writeSerializedSizeExpr(s, t, tn)
    }
  }

  private def writePacketDataSizeEnum(tp: TlmPacket): List[Line] = {
    val name = tp.getName
    val idList = tp.memberIdList
    val channelSize = idList.map(writeChannelSize).mkString("\n  + ")
    lines(s"$name = $channelSize,")
  }

}
