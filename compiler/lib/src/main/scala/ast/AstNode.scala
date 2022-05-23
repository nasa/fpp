package fpp.compiler.ast

/** An AST node with an identifier */
final case class AstNode[+T] private(data: T, id: AstNode.Id)

object AstNode {

  /** A node identifier */
  type Id = Int

  /** Create a new node with a fresh identifier */
  def create[T](data: T): AstNode[T] = {
    val node = AstNode(data, id)
    id = id + 1
    node
  }

  /** Create a new node with an existing identifier  */
  def create[T](data: T, id: Id): AstNode[T] = AstNode(data, id)

  /** The next identifier */
  private var id: Id = 0

}
