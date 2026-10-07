package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the files that define the templates expanded in a list of
 *  translation units, using the location specifier map */
object TemplateDefinitionFiles {

  def get(a: Analysis, tul: List[Ast.TransUnit]): Set[File] = {
    val visitor = Visitor(a.locationSpecifierMap)
    visitor.visitList(Visitor.State(), tul, visitor.transUnit) match {
      case Right(s) => s.files
      case Left(_) => Set()
    }
  }

  private object Visitor {

    /** scopeNameList lists the enclosing scope names, with the innermost
     *  name at the head of the list */
    final case class State(
      scopeNameList: List[Name.Unqualified] = Nil,
      files: Set[File] = Set()
    )

  }

  private final case class Visitor(
    map: Map[(Ast.SpecLoc.Kind, Name.Qualified), AstNode[Ast.SpecLoc]]
  ) extends AstStateVisitor {

    type State = Visitor.State

    override def defModuleAnnotatedNode(
      s: State,
      aNode: Ast.Annotated[AstNode[Ast.DefModule]]
    ) = {
      val data = aNode._2.data
      val s1 = s.copy(scopeNameList = data.name :: s.scopeNameList)
      for (s2 <- visitList(s1, data.members, matchModuleMember))
        yield s2.copy(scopeNameList = s.scopeNameList)
    }

    override def specTemplateExpandAnnotatedNode(
      s: State,
      aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
    ) = {
      val template = aNode._2.data.template.data
      val specLoc = LocatedUseAnalyzer.findLocation(
        map,
        s.scopeNameList,
        Ast.SpecLoc.Template,
        Name.Qualified.fromQualIdent(template),
        template.isAbsolute
      )
      Right(specLoc.map(LocatedUseAnalyzer.getFile).
        map(file => s.copy(files = s.files + file)).getOrElse(s))
    }

    override def transUnit(s: State, tu: Ast.TransUnit) =
      visitList(s, tu.members, matchTuMember)

  }

}
