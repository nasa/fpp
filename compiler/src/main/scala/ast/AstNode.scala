/**
 * An AST node with an identifier
 */

package fpp.compiler.ast

case class AstNode[T] private(data: T, id: AstNode.Id) {
  def getData: T = this.data
  def getId: AstNode.Id = this.id
}

object AstNode {
  /** A node identifier */
  type Id = Int
  /** Create a new node with a fresh identifier */
  def create[T](data: T) = {
    val node = AstNode(data, id)
    id = id + 1
    node
  }
  /** The next identifier */
  private var id: Id = 0
}
