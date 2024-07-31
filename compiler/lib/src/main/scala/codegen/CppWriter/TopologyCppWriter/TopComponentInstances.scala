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
    val hppLines = getHppLines
    lazy val cppLines = getCppLines
    lazy val comment = CppDocWriter.writeBannerComment(bannerComment)
    hppLines match {
      case Nil => Nil
      case _ => List(
        linesMember(comment, CppDoc.Lines.Both),
        linesMember(hppLines, CppDoc.Lines.Hpp),
        linesMember(cppLines, CppDoc.Lines.Cpp)
      )
    }
  }

  private def getHppLines = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val implType = getImplType(ci)
      val instanceName = ci.getUnqualifiedName
      val instLines = lines(
        s"""|//! $instanceName
            |extern $implType $instanceName;"""
      )
      wrapInNamespaceLines(ci.qualifiedName.qualifier, instLines)
    }
    flattenWithBlankPrefix(instances.map(getCode))
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
      wrapInNamespaceLines(ci.qualifiedName.qualifier, instLines)
    }
    flattenWithBlankPrefix(instances.map(getCode))
  }

  private def getImplType(ci: ComponentInstance) = {
    val implType = ci.aNode._2.data.implType.map(_.data)
    implType.getOrElse(getComponentNameAsQualIdent(ci))
  }

}
