package fpp.compiler.transform

import fpp.compiler.ast._
import fpp.compiler.syntax._

/** Visit module members */
trait ModuleStateTransformer extends AstStateTransformer {

  override def defModuleAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefModule(name, members) = node1.data
    for { result <- transformList(s, members, moduleMember) }
    yield {
      val (s1, members1) = result
      val defModule = Ast.DefModule(name, members1.flatten)
      val node2 = AstNode.create(defModule, node1.id)
      (s1, (pre, node2, post))
    }
  }

  def moduleMember(s: State, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] =
    matchModuleMember(s, member)

}
