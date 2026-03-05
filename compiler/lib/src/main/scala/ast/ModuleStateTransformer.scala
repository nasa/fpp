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
    for { result <- transformList(s, members, moduleMember) }
    yield {
      val (s1, members1) = result
      val defModule = Ast.DefModule(name, members1.flatten)
      val node1 = AstNode.create(defModule, node.id)
      (s1, (pre, node1, post))
    }
  }

  def moduleMember(s: State, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] =
    matchModuleMember(s, member)

}
