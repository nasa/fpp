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

  private val packetsWithId = tps.packetMap.toList.sortBy(_._2.getName)

  private val packets = packetsWithId.map(_._2)

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

  private def getChannelArraySizesLines: List[Line] =
    writeStruct(
      "ChannelArraySizes",
       packets.flatMap(writeChannelArraySize)
    )

  private def getChannelArraysLines: List[Line] =
    writeStruct(
      "ChannelArrays",
       packets.flatMap(writeChannelArray)
    )

  private def getChannelIdLines: List[Line] = {
    def writeChannelId(entry: Dictionary.TlmChannelEntry) = {
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
    writeStruct("ChannelIds", channelEntries.flatMap(writeChannelId))
  }

  private def getChannelSizeLines: List[Line] = {
    def writeChannelSize(entry: Dictionary.TlmChannelEntry) = {
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
    writeStruct("ChannelSizes", channelEntries.flatMap(writeChannelSize))
  }

  private def getCppIncludesMember: CppDoc.Member = {
    val headers = List(
      "Fw/Types/Assert.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp",
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(Line.blank :: headers, CppDoc.Lines.Cpp)
  }

  private def getCppMembers: List[CppDoc.Member] =
    List(
      getDataStructuresMember,
      getCppVarsMember
    )

  private def getCppVarsMember: CppDoc.Member =
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

  private def getDataStructuresMember: CppDoc.Member =
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("File-local data strcutures"),
        addBlankPrefix(
          wrapInAnonymousNamespace(
            addBlankPostfix(
              List.concat(
                getChannelArraysLines,
                getChannelArraySizesLines,
                getPacketsLines,
                getOmittedListLines
              )
            )
          )
        )
      ),
      CppDoc.Lines.Cpp
    )

  private def getHppConstantMember: CppDoc.Member =
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Constants"),
        getSizeBoundLines,
        getChannelIdLines,
        getChannelSizeLines,
        getPacketIdLines,
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

  private def getOmittedListLines: List[Line] =
    lines(
      """|
         |// TODO: Omitted list
         |
         |// TODO: Size of omitted list"""
    )

  private def getPacketDataSizeLines: List[Line] = {
    def writePacketDataSize(tp: TlmPacket): List[Line] = {
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
            |  $name <= SizeBounds::packetMaxDataSize,
            |  "packet data must fit in max data size"
            |);"""
      )
    }
    writeStruct("PacketDataSizes", packets.flatMap(writePacketDataSize))
  }

  private def getPacketGroupLines: List[Line] = {
    def writePacketGroup(tp: TlmPacket) = {
      val name = tp.getName
      val group = tp.group
      lines(
        s"""|
            |//! The group for packet $name
            |static constexpr FwIndexType $name = $group;"""
      )
    }
    writeStruct("PacketGroups", packets.flatMap(writePacketGroup))
  }

  private def getPacketIdLines: List[Line] = {
    def writePacketId(id: TlmPacket.Id, tp: TlmPacket) = {
      val name = tp.getName
      val idStr = CppWriter.writeId(id)
      lines(
        s"""|
            |//! The identifier for packet $name
            |static constexpr FwTlmPacketizeIdType $name = $idStr;"""
      )
    }
    writeStruct("PacketIds", packetsWithId.flatMap(writePacketId))
  }

  private def getPacketsLines: List[Line] =
    writeStruct(
      "Packets",
      packets.flatMap(writePacket)
    )

  private def getSizeBoundLines: List[Line] =
    writeStruct(
      "SizeBounds",
      lines(
        """|
           |// The size of a packet header
           |static constexpr FwSizeType packetHeaderSize = Fw::Time::SERIALIZED_SIZE +
           |  sizeof(FwTlmPacketizeIdType) + sizeof(FwPacketDescriptorType);
           |
           |// A packet header must fit in a com buffer
           |static_assert(
           |  packetHeaderSize <= FW_COM_BUFFER_MAX_SIZE,
           |  "packet header must fit in com buffer"
           |);
           |
           |// The max data size in a com buffer
           |static constexpr FwSizeType packetMaxDataSize = FW_COM_BUFFER_MAX_SIZE - packetHeaderSize;"""
      )
    )

  private def writeChannelArray(tp: TlmPacket): List[Line] = {
    def writeChannelEntry(entry: Dictionary.TlmChannelEntry) = {
      val name = entry.getQualifiedName
      val nameStr = CppWriter.identFromQualifiedName(name)
      lines(s"{ ChannelIds::$nameStr, ChannelSizes::$nameStr },")
    }
    val name = tp.getName
    addBlankPrefix(
      wrapInScope(
        s"const Svc::TlmPacketizerChannelEntry $name[] = {",
        channelEntries.flatMap(writeChannelEntry),
        "};"
      )
    )
  }

  private def writeChannelArraySize(tp: TlmPacket): List[Line] = {
    val name = tp.getName
    lines(
      s"""|
          |// TODO: Channel array size for packet $name"""
    )
  }

  private def writePacket(tp: TlmPacket): List[Line] = {
    val name = tp.getName
    lines(
      s"""|
          |// TODO: Packet $name"""
    )
  }

  private def writeStruct(name: String, lines: List[Line]) =
    addBlankPrefix(
      wrapInNamedStruct(
        name,
        addBlankPostfix(lines)
      )
    )

}
