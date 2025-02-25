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

  private val channelEntries =
    d.tlmChannelEntryMap.values.toList.sortBy(_.tlmChannel.getName)

  def write: CppDoc = {
    CppWriter.createCppDoc(
      s"$name telemetry packets",
      fileName,
      getIncludeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getChannelArrayMember: CppDoc.Member =
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Channel arrays"),
        addBlankPrefix(
          wrapInAnonymousNamespace(
            addBlankPostfix(
              lines(
                """|
                   |// TODO"""
              )
            )
          )
        )
      ),
      CppDoc.Lines.Cpp
    )

  private def getPacketMember: CppDoc.Member =
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Packets"),
        addBlankPrefix(
          wrapInAnonymousNamespace(
            addBlankPostfix(
              lines(
                """|
                   |// TODO"""
              )
            )
          )
        )
      ),
      CppDoc.Lines.Cpp
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
      //getStaticAssertMember,
      getChannelArrayMember,
      getPacketMember,
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

  private def getChannelIdLines: List[Line] =
    addBlankPrefix(
      wrapInNamedStruct(
        "ChannelIds",
        addBlankPostfix(channelEntries.flatMap(writeChannelId))
      )
    )

  private def getChannelSizeLines: List[Line] =
    addBlankPrefix(
      wrapInNamedStruct(
        "ChannelSizes",
        addBlankPostfix(channelEntries.flatMap(writeChannelSize))
      )
    )

  private def getPacketIdLines: List[Line] = Nil

  private def getPacketLevelLines: List[Line] = Nil

  private def getPacketGroupLines: List[Line] = Nil

  private def getPacketDataSizeLines: List[Line] = 
    addBlankPrefix(
      wrapInNamedStruct(
        "PacketDataSizes",
        addBlankPostfix(packetList.flatMap(writePacketDataSize))
      )
    )

  private def getSizeBoundLines: List[Line] =
    List.concat(
      lines(
        """|
           |// The size of a packet header
           |constexpr FwSizeType packetHeaderSize = Fw::Time::SERIALIZED_SIZE +
           |  sizeof(FwTlmPacketizeIdType) + sizeof(FwPacketDescriptorType);
           |
           |// A packet header must fit in a com buffer
           |static_assert(
           |  packetHeaderSize <= FW_COM_BUFFER_MAX_SIZE,
           |  "packet header must fit in com buffer"
           |);
           |
           |// The max data size in a com buffer
           |constexpr FwSizeType packetMaxDataSize = FW_COM_BUFFER_MAX_SIZE - packetHeaderSize;"""
      )
    )

  private def getHppConstantMember: CppDoc.Member =
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Constants"),
        getSizeBoundLines,
        getChannelIdLines,
        getChannelSizeLines,
        getPacketIdLines,
        getPacketLevelLines,
        getPacketGroupLines,
        getPacketDataSizeLines
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
    List(
      getHppConstantMember,
      getHppVarMember
    )

  private def getHppVarMember: CppDoc.Member =
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
            |  PacketDataSizes::$name <= packetMaxDataSize,
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

  private def writeChannelSize(entry: Dictionary.TlmChannelEntry) = {
    val name = entry.getQualifiedName
    val nameStr = CppWriter.identFromQualifiedName(name)
    val t = entry.tlmChannel.channelType
    val tn = TypeCppWriter.getName(s, t)
    val sizeStr = writeSerializedSizeExpr(s, t, tn)
    lines(
      s"""|
          |//! The serialized size of channel $name
          |static constexpr FwSizeType $nameStr = $sizeStr;"""
    )
  }

  private def writeChannelId(entry: Dictionary.TlmChannelEntry) = {
    val name = entry.getQualifiedName
    val nameStr = CppWriter.identFromQualifiedName(name)
    val id = d.reverseTlmChannelEntryMap(entry)
    val idStr = CppWriter.writeId(id)
    lines(
      s"""|
          |//! The identifier for channel $name
          |static constexpr FwChanIdType $nameStr = $idStr;"""
    )
  }

  // TODO: Use the symbolic names for the channel sizes, once they are available
  private def writePacketDataSize(tp: TlmPacket): List[Line] = {
    def writeChannelSizeExpr(id: TlmChannel.Id) = {
      val entry = d.tlmChannelEntryMap(id)
      val name = entry.getQualifiedName
      val nameStr = CppWriter.identFromQualifiedName(name)
      s"ChannelSizes::$nameStr"
    }
    val name = tp.getName
    val idList = tp.memberIdList
    val dataSize = idList match {
      case Nil => "0"
      case _ => idList.map(writeChannelSizeExpr).mkString("\n  + ")
    }
    lines(
      s"""|
          |//! The data size for packet $name
          |static constexpr FwSizeType $name = $dataSize;
          |
          |static_assert(
          |  $name <= packetMaxDataSize,
          |  "packet data must fit in max data size"
          |);"""
    )
  }

}
