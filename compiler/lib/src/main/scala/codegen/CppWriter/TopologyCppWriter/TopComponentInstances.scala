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

  def getMembers: List[CppDoc.Member] = {
    val hppLinesMembers = getHppLinesMembers
    val cppLinesMembers = List(linesMember(getCppLines, CppDoc.Lines.Cpp))
    lazy val commentMembers = List(
      linesMember(
        CppDocWriter.writeBannerComment(bannerComment),
        CppDoc.Lines.Both
      )
    )
    List.concat(
      guardedList (!hppLinesMembers.isEmpty) (commentMembers),
      hppLinesMembers,
      cppLinesMembers
    )
  }

  private def getHppLinesMembers = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val implType = getImplType(ci)
      val instanceName = ci.getUnqualifiedName
      val instLines = lines(
        s"""|//! $instanceName
            |extern $implType $instanceName;"""
      )
      Line.blank :: wrapInNamespaceLines(ci.qualifiedName.qualifier, instLines)
    }
    val hppLines = instances.flatMap(getCode)
    guardedList (!hppLines.isEmpty) (List(linesMember(hppLines, CppDoc.Lines.Hpp)))
  }

  private def getCppLines = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val implType = getImplType(ci)
      val instanceName = ci.getUnqualifiedName
      val instLines = getCodeLinesForPhase (CppWriter.Phases.instances) (ci).getOrElse(
        lines(
          s"$implType $instanceName(FW_OPTIONAL_NAME($q$instanceName$q));"
        )
      )
      Line.blank :: wrapInNamespaceLines(ci.qualifiedName.qualifier, instLines)
    }
    instances.flatMap(getCode)
  }

  private def getImplType(ci: ComponentInstance) = {
    val implType = ci.aNode._2.data.implType.map(_.data)
    implType.getOrElse(getComponentNameAsQualIdent(ci))
  }

}
