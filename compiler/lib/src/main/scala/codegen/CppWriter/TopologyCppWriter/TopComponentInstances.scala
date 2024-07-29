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

  def getHppMembers: List[CppDoc.Member] = {
    val declMembers = getDeclMembers
    lazy val commentMember =
      linesMember(CppDocWriter.writeBannerComment(bannerComment))
    lazy val members = commentMember :: declMembers
    guardedList (!declMembers.isEmpty) (members)
  }

  def getCppMembers: List[CppDoc.Member] = {
    val defMembers = getDefMembers
    lazy val commentMember = 
      linesMember(CppDocWriter.writeBannerComment(bannerComment), CppDoc.Lines.Cpp)
    lazy val members = commentMember :: defMembers
    guardedList(!defMembers.isEmpty) (members)
  }

  private def getDeclMembers = {
    def getCode(ci: ComponentInstance): List[CppDoc.Member] = {
      val implType = getImplType(ci)
      val instanceName = ci.getUnqualifiedName
      val instLines = lines(
        s"""|
            |//! $instanceName
            |extern $implType $instanceName;"""
      )
      wrapInNamespaces(ci.qualifiedName.qualifier, List(linesMember(instLines)))
    }
    instances.flatMap(getCode)
  }

  private def getDefMembers = {
    def getCode(ci: ComponentInstance): List[CppDoc.Member] = {
      val implType = getImplType(ci)
      val instanceName = ci.getUnqualifiedName
      val instLines = getCodeLinesForPhase (CppWriter.Phases.instances) (ci).getOrElse(
        lines(
          s"""|
              |$implType $instanceName(FW_OPTIONAL_NAME($q$instanceName$q));"""
        )
      )
      wrapInNamespaces(ci.qualifiedName.qualifier, List(linesMember(instLines, CppDoc.Lines.Cpp)))
    }
    instances.flatMap(getCode)
  }

  private def getImplType(ci: ComponentInstance) = {
    val implType = ci.aNode._2.data.implType.map(_.data)
    implType.getOrElse(getComponentNameAsQualIdent(ci))
  }

}
