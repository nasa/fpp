package fpp.compiler.ast

import fpp.compiler.syntax._

/** Visit component members */
trait ComponentStateTransformer extends AstStateTransformer {

  override def defComponentAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefComponent(kind, name, members) = node1.data
    for { result <- transformList(s, members, componentMember) }
    yield {
      val (s1, members1) = result
      val defComponent = Ast.DefComponent(kind, name, members1.flatten)
      val node2 = AstNode.create(defComponent, node1.id)
      (s1, (pre, node2, post))
    }
  }

  def componentMember(s: State, member: Ast.ComponentMember): Result[List[Ast.ComponentMember]] =
    matchComponentMember(s, member)

}
