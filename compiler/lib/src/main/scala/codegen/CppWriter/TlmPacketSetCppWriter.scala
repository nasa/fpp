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

  private val fileName = ComputeCppFiles.FileNames.getTlmPacketSet(name)

  private val Some(t) = s.a.topology

  private val Some(d) = s.a.dictionary

  private val topQualifiedName = s"${t.getName}_$name"

  private val qualifiedName =
    s.a.getEnclosingNames(Symbol.Topology(t.aNode)) match {
      case Nil => topQualifiedName
      case names =>
        val qualifier = CppWriterState.identFromQualifiedName(
          Name.Qualified.fromIdentList(names)
        )
        s"${qualifier}_${topQualifiedName}"
    }

  def write: CppDoc = {
    CppWriter.createCppDoc(
      s"$name telemetry packets",
      fileName,
      getIncludeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getCppChannelArraysMember: CppDoc.Member = {
    // TODO
    linesMember(Nil)
  }

  private def getCppIncludesMember: CppDoc.Member = {
    // TODO
    linesMember(Nil)
  }

  private def getCppOmittedMember: CppDoc.Member = {
    // TODO
    linesMember(Nil)
  }

  private def getCppPacketListMember: CppDoc.Member = {
    // TODO
    linesMember(Nil)
  }

  private def getCppPacketsMember: CppDoc.Member = {
    // TODO
    linesMember(Nil)
  }

  private def getHppIncludesMember: CppDoc.Member = {
    val headers = List(
      "Svc/TlmPacketizer/TlmPacketizerTypes.hpp"
    ).map(CppWriter.headerString)
    linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getHppOmittedMember: CppDoc.Member = {
    linesMember(
      lines(
        s"""|
            |//! The omitted channels
            |extern const Svc::TlmPacketizerPacket OmittedChannels;"""
      )
    )
  }

  private def getHppPacketListMember: CppDoc.Member = {
    linesMember(
      lines(
        s"""|
            |//! The list of packets
            |extern const Svc::TlmPacketizerPacketList PacketList;"""
      )
    )
  }

  private def getIncludeGuard: String = s"${qualifiedName}_HPP"

  private def getMembers: List[CppDoc.Member] = {
    val nsil = s.getNamespaceIdentList(Symbol.Topology(t.aNode)) :+ topQualifiedName
    val varMembers = List(
      getCppChannelArraysMember,
      getCppPacketsMember,
      getHppPacketListMember,
      getCppPacketListMember,
      getHppOmittedMember,
      getCppOmittedMember
    )
    getHppIncludesMember :: getCppIncludesMember ::
      wrapInNamespaces(nsil, varMembers)
  }

}
