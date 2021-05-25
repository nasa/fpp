package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology definitions */
object TopologyCppWriter extends LineUtils {

  def defTopologyAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    val data = node.data
    val fileName = ComputeCppFiles.FileNames.getTopology(data.name)
    val symbol = Symbol.Topology(aNode)
    val includeGuard = s.includeGuardFromQualifiedName(symbol, "Topology")
    // TODO: Generate the members
    val members = Nil
    CppWriter.createCppDoc(fileName, includeGuard, members)
  }

}
