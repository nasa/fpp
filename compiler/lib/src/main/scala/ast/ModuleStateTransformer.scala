package fpp.compiler.ast

import fpp.compiler.syntax._

/** Visit module members */
trait ModuleStateTransformer extends AstStateTransformer {

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node, post) = aNode
    val Ast.DefModule(name, members) = node.data
    for ((s1, members1) <- transformFlatList(s, members, moduleMember))
    yield {
      val defModule = Ast.DefModule(name, members1)
      val node1 = AstNode.create(defModule, node.id)
      (s1, (pre, node1, post))
    }
  }

  override def specTemplateExpandAnnotatedNodeExpanded(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]],
    members: List[Ast.ModuleMember]
  ) = {
    val (pre, node, post) = aNode
    val Ast.SpecTemplateExpand(template, args, _) = node.data
    for ((s1, members1) <- transformFlatList(s, members, moduleMember))
      yield {
        val specTemplateExpand = Ast.SpecTemplateExpand(template, args, Some(members1))
        val node1 = AstNode.create(specTemplateExpand, node.id)
        (s1, (pre, node1, post))
      }
  }

  def moduleMember(s: State, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] =
    matchModuleMember(s, member)

}
