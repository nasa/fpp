package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check module template definitions */
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
        case None => Right(())
      }
      a <- Right(a.copy(templateDefId = Some(node.id)))
      a <- visitList(a, data.members, matchModuleMember)
      a <- Right(a.copy(templateDefId = None))
    } yield a
  }


  override def defSystemAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefSystem]]
  ) = {
    val node = aNode._2
    a.templateDefId match {
      case Some(defId) =>
        Left(SemanticError.SystemInTemplate(
          Locations.get(node.id),
          Locations.get(defId)
        ))
      case None => Right(a)
    }
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    (a.templateDefId, node.data.isDeployment) match {
      case (Some(defId), true) =>
        Left(SemanticError.DeploymentTopologyInTemplate(
          Locations.get(node.id),
          Locations.get(defId)
        ))
      case _ => Right(a)
    }
  }

}
