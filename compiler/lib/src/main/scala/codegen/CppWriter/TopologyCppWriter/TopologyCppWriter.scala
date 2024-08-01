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

  private def getMembers: List[CppDoc.Member] =
    List.concat(
      getIncludeMembers,
      getComponentInstanceMembers,
      getTopologyMembers
    )

  private def getComponentInstanceMembers =
    TopComponentInstances(s, aNode).getMembers

  private def getIncludeMembers: List[CppDoc.Member] = {
    val hpp = {
      val strings = (
        TopComponentIncludes(s, aNode).getHeaderStrings :+
        CppWriter.headerString(
          s.getRelativePath(s"${name}TopologyDefs.hpp").toString
        )
      ).sorted
      linesMember(Line.blank :: strings.map(line))
    }
    val cpp = {
      val fileName = s"${ComputeCppFiles.FileNames.getTopology(name)}.hpp"
      linesMember(
        List(
          Line.blank,
          CppWriter.headerLine(s.getRelativePath(fileName).toString)
        ),
        CppDoc.Lines.Cpp
      )
    }
    List(hpp, cpp)
  }

  private def getTopologyMembers: List[CppDoc.Member] = {
    val hppLines = linesMember(
      TopConstants(s, aNode).getLines ++
      TopConfigObjects(s, aNode).getHppLines
    )
    val cppLines = linesMember(
      Line.blank :: addBlankPostfix(TopConfigObjects(s, aNode).getCppLines),
      CppDoc.Lines.Cpp
    )
    val (helperFnNames, helperFns) = TopHelperFns(s, aNode).getMembers
    val setupTeardownFns =
      TopSetupTeardownFns(s, aNode, helperFnNames).getMembers
    val defs = hppLines :: cppLines :: (helperFns ++ setupTeardownFns)
    wrapInNamespaces(namespaceIdentList, defs)
  }

}
