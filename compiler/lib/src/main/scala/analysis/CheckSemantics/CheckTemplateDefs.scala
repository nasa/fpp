package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** Check module template definitions */
object CheckTemplateDefs
  extends Analyzer
  with ModuleAnalyzer
{

  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) = {
    val node = aNode._2
    val a1 = a.copy(templateDefinition = Some(node.id))
    for (a1 <- visitList(a1, node.data.members, matchModuleMember))
      yield a1.copy(templateDefinition = a.templateDefinition)
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    (a.templateDefinition, node.data.isDeployment) match {
      case (Some(templateDefinition), true) =>
        Left(SemanticError.DeploymentTopologyInTemplate(
          Locations.get(node.id),
          Locations.get(templateDefinition)
        ))
      case _ => Right(a)
    }
  }

}
