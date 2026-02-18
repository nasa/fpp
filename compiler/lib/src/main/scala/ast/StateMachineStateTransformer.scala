package fpp.compiler.ast

import fpp.compiler.syntax._

/** Visit state machine members */
trait StateMachineStateTransformer extends AstStateTransformer {

  override def defStateAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefState(name, members) = node1.data
    for { result <- transformList(s, members, stateMember) }
    yield {
      val (s1, members1) = result
      val defState = Ast.DefState(name, members1.flatten)
      val node2 = AstNode.create(defState, node1.id)
      (s1, (pre, node2, post))
    }
  }

  override def defStateMachineAnnotatedNodeInternal(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = {
    val (pre, node1, post) = node
    val name = node1.data.name
    for { result <- transformList(s, members, stateMachineMember) }
    yield {
      val (s1, members1) = result
      val defStateMachine = Ast.DefStateMachine(name, Some(members1.flatten))
      val node2 = AstNode.create(defStateMachine, node1.id)
      (s1, (pre, node2, post))
    }
  }

  def stateMachineMember(s: State, member: Ast.StateMachineMember): Result[List[Ast.StateMachineMember]] =
    matchStateMachineMember(s, member)

  def stateMember(s: State, member: Ast.StateMember): Result[List[Ast.StateMember]] =
    matchStateMember(s, member)

}
