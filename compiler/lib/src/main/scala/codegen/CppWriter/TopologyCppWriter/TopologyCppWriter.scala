package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology definitions */
case class TopologyCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends LineUtils {

  val symbol = Symbol.Topology(aNode)

  val namespace = s.getNamespace(symbol)

  def write: CppDoc = {
    val node = aNode._2
    val data = node.data
    val fileName = ComputeCppFiles.FileNames.getTopology(data.name)
    val includeGuard = s.includeGuardFromQualifiedName(symbol, "Topology")
    CppWriter.createCppDoc(fileName, includeGuard, getMembers)
  }

  private def getMembers: List[CppDoc.Member] = {
    val includes = CppWriter.linesMember(
      addBlankPrefix(TopComponentIncludes(s, aNode).getLines)
    )
    val hppLines = CppWriter.linesMember(
      addBlankPrefix(TopConstants(s, aNode).getLines)
    )
    val cppLines = CppWriter.linesMember(
      Line.blank ::
      List(
        lines("namespace {"),
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
        includes,
        CppWriter.namespaceMember(ns, defs)
      )
      case None => includes :: defs
    }
  }

}
