package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Visit translation unit members and module members */
trait ModuleAnalyzer extends Analyzer {

  override def defModuleAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val Ast.DefModule(name, members) = node.data
    val a1 = a.copy(scopeNameList = name :: a.scopeNameList)
    for { a2 <- visitList(a1, members, matchModuleMember) }
    yield a2.copy(scopeNameList = a.scopeNameList)
  }

  override def specTemplateExpandAnnotatedNodeExpanded(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]],
    members: List[Ast.ModuleMember]
  ) = {
    val (_, node, _) = aNode
    a.templateExpansionMap.get(node.id) match {
      case Some(expansion) =>
        // This node has been expanded by analysis.
        // Mark the analysis with the proper expansion.
        val a1 = a.copy(templateExpansion = Some(expansion))
        for { a2 <- visitList(a1, members, matchModuleMember) }
        yield a2.copy(templateExpansion = a.templateExpansion)
      case None =>
        // This node has not yet been expanded by analysis.
        // The expansion is provided in an expand { ... } clause.
        Right(a)
    }
  }

}
