package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._
import scala.xml.Node

/** Writes out an F Prime XML telemetry packet set as FPP source */
object TlmPacketSetXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (topologyMember <- FppBuilder.topologyMember(file))
      yield FppWriter.topologyMember(topologyMember)

  /** Builds FPP for translating telemetry packet XML */
  private object FppBuilder {

    /** Generates the topology member */
    def topologyMember(file: XmlFppWriter.File): Result.Result[Ast.TopologyMember] =
      for (tps <- specTlmPacketSetAnnotated(file))
        yield {
          val node = XmlFppWriter.transformNode (Ast.TopologyMember.SpecTlmPacketSet.apply) (tps)
          Ast.TopologyMember(node)
        }

    /** Extracts a telemetry packet set member */
    def tlmPacketSetMember(
      file: XmlFppWriter.File,
      xmlNode: scala.xml.Node
    ): Result.Result[Ast.TlmPacketSetMember] =
      for {
        name <- file.getAttribute(xmlNode, "name")
        group <- file.translateInteger(xmlNode, "level")
        members <- tlmPacketMemberList(file, xmlNode)
      }
      yield {
        val id = XmlFppWriter.FppBuilder.translateIntegerOpt(xmlNode, "id")
        val data = Ast.SpecTlmPacket(
          name,
          id,
          group,
          members
        )
        val node = Ast.TlmPacketSetMember.SpecTlmPacket(AstNode.create(data))
        val aNode = (Nil, node, Nil)
        Ast.TlmPacketSetMember(aNode)
      }

    /** Extracts a telemetry channel identifier */
    def tlmChannelIdentifier(
      file: XmlFppWriter.File,
      name: String
    ): Result.Result[Ast.TlmChannelIdentifier] =
      name.split("\\.").toList.reverse match {
        case head :: tail =>
          val nodeList = tail.reverse.map(AstNode.create)
          val instance = Ast.QualIdent.fromNodeList(nodeList)
          Right(
            Ast.TlmChannelIdentifier(
              AstNode.create(instance),
              AstNode.create(head)
            )
          )
        case _ => Left(file.semanticError(s"channel name $name is not well-formed"))
      }

    /** Extracts a telemetry packet member */
    def tlmPacketMember(
      file: XmlFppWriter.File,
      xmlNode: scala.xml.Node
    ): Result.Result[Ast.TlmPacketMember] =
      for {
        name <- file.getAttribute(xmlNode, "name")
        tci <- tlmChannelIdentifier(file, name)
      }
      yield {
        val node = AstNode.create(tci)
        Ast.TlmPacketMember.TlmChannelIdentifier(node)
      }

    /** Extracts telemetry packet members */
    def tlmPacketMemberList(file: XmlFppWriter.File, xmlNode: scala.xml.Node):
      Result.Result[List[Ast.TlmPacketMember]] = {
        val channels = xmlNode \ "channel"
        Result.map(channels.toList, tlmPacketMember(file, _))
      }

    /** Extracts telemetry packet set members */
    def tlmPacketSetMemberList(file: XmlFppWriter.File):
      Result.Result[List[Ast.TlmPacketSetMember]] = {
        val packets = file.elem \ "packet"
        Result.map(packets.toList, tlmPacketSetMember(file, _))
      }

    /** Extracts omitted channels */
    def omittedChannelList(file: XmlFppWriter.File):
      Result.Result[List[AstNode[Ast.TlmChannelIdentifier]]] =
      Right(Nil)

    /** Translates the telemetry packet set */
    def specTlmPacketSetAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.SpecTlmPacketSet]] =
      for {
        name <- file.getAttribute(file.elem, "name")
        members <- tlmPacketSetMemberList(file)
        omitted <- omittedChannelList(file)
      }
      yield (Nil, Ast.SpecTlmPacketSet(name, members, omitted), Nil)

  }

}
