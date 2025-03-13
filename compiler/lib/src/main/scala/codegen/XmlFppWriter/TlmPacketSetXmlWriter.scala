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

//    /** Translates an XML type to an FPP type name */
//    def translateType(file: XmlFppWriter.File): Node => Result.Result[Ast.TypeName] = 
//      file.translateType(node => file.getAttribute(node, "type")) _
//
//    /** Extracts a struct type member */
//    def structTypeMemberAnnotatedNode(
//      file: XmlFppWriter.File,
//      node: scala.xml.Node
//    ): Result.Result[Ast.Annotated[AstNode[Ast.TlmPacketSetTypeMember]]] = {
//      for {
//        structName <- file.getAttribute(file.elem, "name")
//        memberName <- file.getAttribute(node, "name")
//        xmlType <- file.getAttribute(node, "type")
//        memberType <- translateType(file)(node)
//      }
//      yield {
//        val xmlSizeOpt = XmlFppWriter.getAttributeOpt(node, "array_size")
//        val sizeOpt = (memberType, xmlSizeOpt) match {
//          case (_, Some(size)) => Some(size)
//          case _ => None
//        }
//        val xmlFormatOpt = XmlFppWriter.getAttributeOpt(node, "format")
//        val (fppFormatOpt, pre) =
//          XmlFppWriter.FppBuilder.translateFormatOpt(xmlFormatOpt)
//        val data = Ast.TlmPacketSetTypeMember(
//          memberName,
//          sizeOpt.map(size => AstNode.create(Ast.ExprLiteralInt(size))),
//          AstNode.create(memberType),
//          fppFormatOpt.map(AstNode.create(_))
//        )
//        val astNode = AstNode.create(data)
//        val post = XmlFppWriter.getAttributeComment(node)
//        (pre, astNode, post)
//      }
//    }
//
//    /** Extracts enum definitions from struct members */
//    def defEnumAnnotatedList(file: XmlFppWriter.File):
//      Result.Result[List[Ast.Annotated[Ast.DefEnum]]] =
//      for {
//        child <- file.getSingleChild(file.elem, "members")
//        members <- Right((child \ "member").toList)
//        enumOpts <- Result.map(
//          members,
//          XmlFppWriter.FppBuilder.InlineEnumBuilder.defEnumAnnotatedOpt(file)
//        )
//      }
//      yield enumOpts.filter(_.isDefined).map(_.get)


    /** Extracts telemetry packet set members */
    def tlmPacketSetMemberList(file: XmlFppWriter.File):
      Result.Result[List[Ast.TlmPacketSetMember]] =
        Right(Nil)

    /** Extracts omitted channels */
    def omittedChannelList(file: XmlFppWriter.File):
      Result.Result[List[AstNode[Ast.TlmChannelIdentifier]]] =
        Right(Nil)

//    /** Extracts struct type members */
//    def structTypeMemberAnnotatedNodeList(file: XmlFppWriter.File): 
//      Result.Result[List[Ast.Annotated[AstNode[Ast.TlmPacketSetTypeMember]]]] =
//      for {
//        child <- file.getSingleChild(file.elem, "members")
//        result <- {
//          val members = child \ "member"
//          Result.map(members.toList, structTypeMemberAnnotatedNode(file, _))
//        } 
//      } yield result
//
//    /** Generates the list of TU members */
//    def topologyMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TopologyMember]] =
//      for {
//        enums <- defEnumAnnotatedList(file)
//        struct <- specTlmPacketSetAnnotated(file)
//      }
//      yield XmlFppWriter.topologyMemberList(
//        enums,
//        Ast.TopologyMember.DefEnum.apply,
//        Ast.ModuleMember.DefEnum.apply,
//        struct,
//        Ast.TopologyMember.SpecTlmPacketSet.apply,
//        Ast.ModuleMember.SpecTlmPacketSet.apply,
//        file,
//      )

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
