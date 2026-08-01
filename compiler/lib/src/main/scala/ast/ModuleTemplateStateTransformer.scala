package fpp.compiler.ast

import fpp.compiler.syntax._

/** Visit module template members */
trait ModuleTemplateStateTransformer extends AstStateTransformer {

  override def defModuleTemplateAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) = {
    val (pre, node, post) = aNode
    val Ast.DefModuleTemplate(name, params, members) = node.data
    for { result <- transformList(s, members, moduleTemplateMember) }
    yield {
      val (s1, members1) = result
      val defModuleTemplate = Ast.DefModuleTemplate(name, params, members1.flatten)
      val node1 = AstNode.create(defModuleTemplate, node.id)
      (s1, (pre, node1, post))
    }
  }

  def moduleTemplateMember(s: State, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] =
    matchModuleMember(s, member)

}
