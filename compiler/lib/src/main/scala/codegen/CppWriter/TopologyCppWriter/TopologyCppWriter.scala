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
      val fileName = s"${ComputeCppFiles.FileNames.getTopology(name)}.hpp"
      CppWriter.linesMember(
        List(
          Line.blank,
          CppWriter.headerLine(s.getRelativePath(fileName).toString)
        ),
        CppDoc.Lines.Cpp
      )
    }
    val (privateFnNames, privateFnLines) = TopPrivateFunctions(s, aNode).getLines
    val cppLines = CppWriter.linesMember(
      Line.blank ::
      wrapInAnonymousNamespace(
        addBlankPostfix(
          List(
            TopConfigObjects(s, aNode).getLines,
            TopComponentInstances(s, aNode).getLines,
            privateFnLines
          ).flatten
        )
      ),
      CppDoc.Lines.Cpp
    )
    val (helperFnNames, helperFns) = TopHelperFns(s, aNode).getMembers
    val fnNames = privateFnNames ++ helperFnNames
    val setupTeardownFns = TopSetupTeardownFns(s, aNode, fnNames).getMembers
    val defs = hppLines :: cppLines :: (helperFns ++ setupTeardownFns)
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, defs)
    ).flatten
  }

}
