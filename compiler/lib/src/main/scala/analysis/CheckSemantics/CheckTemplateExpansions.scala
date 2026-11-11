package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check template expansions */
object CheckTemplateExpansions
  extends Analyzer
  with ModuleAnalyzer
{

  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) =
    a.templateExpansionId match {
      case Some(expandId) => Left(
        SemanticError.TemplateExpandError(
          Locations.get(aNode._2.id),
          "module template definitions cannot appear inside a template expansion",
          Locations.get(expandId)
        )
      )
      case None => Right(a)
    }

  override def specTemplateExpandAnnotatedNodeExpanded(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]],
    members: List[Ast.ModuleMember]
  ) =
    for {
      a <- specTemplateExpandAnnotatedNodeUnexpanded(a, aNode)
      a <- Right(a.copy(templateExpansionId = Some(aNode._2.id)))
      a <- visitList(a, members, matchModuleMember)
      a <- Right(a.copy(templateExpansionId = None))
    } yield a

  override def specTemplateExpandAnnotatedNodeUnexpanded(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]],
  ) =
    a.templateExpansionId match {
      case Some(defId) => Left(
        SemanticError.TemplateExpandError(
          Locations.get(aNode._2.id),
          "template expansions cannot be nested",
          Locations.get(defId)
        )
      )
      case None => Right(a)
    }

  override def defSystemAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefSystem]]
  ) = {
    val node = aNode._2
    a.templateExpansionId match {
      case Some(expId) =>
        Left(SemanticError.TemplateExpandError(
          Locations.get(node.id),
          "system definitions cannot appear inside a module template expansion",
          Locations.get(expId)
        ))
      case None => Right(a)
    }
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    (a.templateExpansionId, node.data.isDeployment) match {
      case (Some(expId), true) =>
        Left(SemanticError.TemplateExpandError(
          Locations.get(node.id),
          "deployment topology cannot appear inside a module template expansion",
          Locations.get(expId)
        ))
      case _ => Right(a)
    }
  }

}
