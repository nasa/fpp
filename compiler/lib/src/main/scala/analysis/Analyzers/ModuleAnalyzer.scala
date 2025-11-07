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

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    // Check if this template has been expanded
    node.data.members match {
      case Some(members) => {
        // Mark the analysis with the proper expansion
        val a1 = a.copy(templateExpansion = Some(a.templateExpansionMap(node.id)))
        for { a2 <- visitList(a, members, matchModuleMember) }
        yield a2.copy(templateExpansion = a.templateExpansion)
      }
      case None => Right(a)
    }
  }

}
