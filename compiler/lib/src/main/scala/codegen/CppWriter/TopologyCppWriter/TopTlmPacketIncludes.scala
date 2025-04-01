package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology telemetry packet includes */
case class TopTlmPacketIncludes(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getHeaderStrings: List[String] = {
    val d = s.a.dictionaryMap(Symbol.Topology(aNode))
    d.tlmPacketSetMap.values.toList.map(getHeaderString)
  }

  private def getHeaderString(tps: TlmPacketSet): String =
    CppWriter.headerString(getIncludePath(tps))

  private def getIncludePath(tps: TlmPacketSet): String = {
    val t = s.a.topologyMap(Symbol.Topology(aNode))
    val tpsName = s"${t.getName}_${tps.getName}"
    val fileNameBase = ComputeCppFiles.FileNames.getTlmPacketSet(tpsName)
    val fileName = s"$fileNameBase.hpp"
    s.getRelativePath(fileName).toString
  }

}
