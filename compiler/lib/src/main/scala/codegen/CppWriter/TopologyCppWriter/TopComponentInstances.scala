package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology component instances */
case class TopComponentInstances(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getMembers: List[CppDoc.Member] = {
    val instanceMembers = getInstanceMembers
    List.concat(
      guardedList (!instanceMembers.isEmpty) (List(getCommentMember)),
      instanceMembers
    )
  }

  private val bannerComment = "Component instances"

  private def getCommentMember = linesMember(
    CppDocWriter.writeBannerComment(bannerComment),
    CppDoc.Lines.Both
  )

  private def getInstanceMembers = {
    def getMembers(ci: ComponentInstance): List[CppDoc.Member] = {
      val implType = getImplType(ci)
      val instanceName = ci.getUnqualifiedName
      val hppMember = linesMember(
        lines(
          s"""|
              |//! $instanceName
              |extern $implType $instanceName;"""
        ),
        CppDoc.Lines.Hpp
      )
      val cppMember = {
        val instLines = getCodeLinesForPhase (CppWriter.Phases.instances) (ci).getOrElse(
          lines(
            s"""|
                |$implType $instanceName(FW_OPTIONAL_NAME($q$instanceName$q));"""
          )
        )
        linesMember(instLines, CppDoc.Lines.Cpp)
      }
      wrapInNamespaces(ci.qualifiedName.qualifier, List(hppMember, cppMember))
    }
    instances.flatMap(getMembers)
  }

  private def getImplType(ci: ComponentInstance) = {
    val implType = ci.aNode._2.data.implType.map(_.data)
    implType.getOrElse(getComponentNameAsQualIdent(ci))
  }

}
