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
      getMembers,
      s.toolName
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
      linesMember(Line.blank :: strings.map(line))
    }
    val hppLines = linesMember(
      TopConstants(s, aNode).getLines ++
      TopConfigObjects(s, aNode).getHppLines
    )
    val hppConstantLines = linesMember(TopConstants(s, aNode).getLines)
    val hppComponentInstanceMembers = TopComponentInstances(s, aNode).getHppMembers
    val cppIncludes = {
      val fileName = s"${ComputeCppFiles.FileNames.getTopology(name)}.hpp"
      linesMember(
        List(
          Line.blank,
          CppWriter.headerLine(s.getRelativePath(fileName).toString)
        ),
        CppDoc.Lines.Cpp
      )
    }
    val cppComponentInstanceMembers = TopComponentInstances(s, aNode).getCppMembers
    val cppLines = linesMember(
      Line.blank ::
      addBlankPostfix(
        TopConfigObjects(s, aNode).getCppLines,
      ),
      CppDoc.Lines.Cpp
    )
    val (helperFnNames, helperFns) = TopHelperFns(s, aNode).getMembers
    val setupTeardownFns = TopSetupTeardownFns(s, aNode, helperFnNames).
      getMembers
    val defs = hppLines :: cppLines :: (helperFns ++ setupTeardownFns)
    List(
      List(hppIncludes, cppIncludes),
      hppComponentInstanceMembers,
      cppComponentInstanceMembers,
      wrapInNamespaces(namespaceIdentList, defs)
    ).flatten
  }

}
