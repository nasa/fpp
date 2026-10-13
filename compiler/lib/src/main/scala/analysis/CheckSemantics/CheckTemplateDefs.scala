package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check template definitions */
object CheckTemplateDefs
  extends Analyzer
  with ModuleAnalyzer
{

  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      _ <- a.templateDefId match {
        case Some(defId) => Left(
          SemanticError.TemplateDefError(
            Locations.get(node.id),
            "module template definitions cannot be nested",
            Locations.get(defId)
          )
        )
        case None => Right(a)
      }
      a <- Right(a.copy(templateDefId = Some(node.id)))
      a <- visitList(a, data.members, matchModuleMember)
      a <- Right(a.copy(templateDefId = None))
    } yield a
  }

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = Right(a)

}
