package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology definitions */
case class TopologyCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def write: CppDoc = {
    val node = aNode._2
    val data = node.data
    val fileName = ComputeCppFiles.FileNames.getTopology(data.name)
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"${data.name} topology",
      fileName,
      includeGuard,
      getMembers
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = {
      val strings = (
        TopComponentIncludes(s, aNode).getHeaderStrings :+
        CppWriter.headerString(
          s.getRelativePath(s"${name}TopologyDefs.hpp").toString
        )
      ).sorted
      CppWriter.linesMember(Line.blank :: strings.map(line))
    }
    val hppLines = CppWriter.linesMember(
      TopConstants(s, aNode).getLines
    )
    val cppIncludes = {
      CppWriter.linesMember(
        List(
          Line.blank,
          CppWriter.headerLine(
            s.getRelativePath(s"${name}TopologyAc.hpp").toString
          )
        ),
        CppDoc.Lines.Cpp
      )
    }
    val cppLines = CppWriter.linesMember(
      Line.blank ::
      line("namespace {") ::
      List(
        flattenWithBlankPrefix(
          List(
            TopConfigObjects(s, aNode).getLines,
            TopComponentInstances(s, aNode).getLines,
            TopPrivateFunctions(s, aNode).getLines
          )
        ).map(indentIn),
        Line.blank :: lines("}")
      ).flatten,
      CppDoc.Lines.Cpp
    )
    val publicFunctions = TopPublicFunctions(s, aNode).getMembers
    val defs = hppLines :: cppLines :: publicFunctions
    namespace match {
      case Some(ns) => List(
        hppIncludes,
        cppIncludes,
        CppWriter.namespaceMember(ns, defs)
      )
      case None => hppIncludes :: cppIncludes :: defs
    }
  }

}
