package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology definitions */
object TopologyCppWriter {

  def defTopologyAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    val data = node.data
    val fileName = ComputeCppFiles.FileNames.getTopology(data.name)
    val symbol = Symbol.Topology(aNode)
    val includeGuard = s.includeGuardFromQualifiedName(symbol, "Topology")
    val members = generateMembers(s, aNode)
    CppWriter.createCppDoc(fileName, includeGuard, members)
  }

  private def generateMembers(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ): List[CppDoc.Member] = List(
    TopComponentIncludes(s, aNode).generateMembers,
    TopConstants(s, aNode).generateMembers,
    TopConfigObjects(s, aNode).generateMembers,
    TopComponentInstances(s, aNode).generateMembers,
    TopPrivateFunctions(s, aNode).generateMembers,
    TopPublicFunctions(s, aNode).generateMembers
  ).flatten

}
