package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology component instances */
case class TopComponentInstances(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getLines: List[Line] = {
    addBannerComment(
      "Component instances",
      getComponentInstanceLines
    )
  }

  private def getComponentInstanceLines: List[Line] = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val componentName = getNameAsQualIdent(
        s.a.getQualifiedName(Symbol.Component(ci.component.aNode))
      )
      val instanceName = getNameAsIdent(ci.qualifiedName)
      Line.addPrefixLine (line(s"// $instanceName")) (
        getCodeLinesForPhase (CppWriter.Phases.instances) (ci).getOrElse(
          lines(
            s"$componentName $instanceName(FW_OPTIONAL_NAME($q$instanceName$q));"
          )
        )
      )
    }
    val lll = instances.map(getCode)
    flattenWithBlankPrefix(lll)
  }

}
