package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology component includes */
case class TopComponentIncludes(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getHeaderStrings: List[String] = {
    val t = s.a.topologyMap(Symbol.Topology(aNode))
    t.instanceMap.keys.toList.map(getHeaderString).distinct
  }

  private def getHeaderString(ci: ComponentInstance): String =
    CppWriter.headerString(getIncludePath(ci))

  private def getIncludePath(ci: ComponentInstance): String = {
    val path = ci.file match {
      case Some(file) => File.getJavaPath(file)
      case None =>
        val c = ci.component
        val node = c.aNode._2
        val loc = Locations.get(node.id)
        loc.getNeighborPath(s"${node.data.name}.hpp")
    }
    s.removeLongestPathPrefix(path).toString
  }

}
