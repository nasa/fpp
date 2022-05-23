package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology component instances */
case class TopComponentInstances(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  private val bannerComment = "Component instances"

  def getHppLines: List[Line] = addBannerComment(
    bannerComment,
    getDeclLines
  )

  def getCppLines: List[Line] = addBannerComment(
    bannerComment,
    getDefLines
  )

  private def getDeclLines = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val implType = getImplType(ci)
      val instanceName = getNameAsIdent(ci.qualifiedName)
      Line.addPrefixLine (line(s"//! $instanceName")) (
        lines(
          s"extern $implType $instanceName;"
        )
      )
    }
    flattenWithBlankPrefix(instances.map(getCode))
  }

  private def getDefLines = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val implType = getImplType(ci)
      val instanceName = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.instances) (ci).getOrElse(
        lines(
          s"$implType $instanceName(FW_OPTIONAL_NAME($q$instanceName$q));"
        )
      )
    }
    flattenWithBlankPrefix(instances.map(getCode))
  }

  private def getImplType(ci: ComponentInstance) = {
    val implType = ci.aNode._2.data.implType.map(_.data)
    implType.getOrElse(getComponentNameAsQualIdent(ci))
  }

}
