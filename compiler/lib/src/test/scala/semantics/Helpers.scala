package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._

/** General helpers */
object Helpers {

  def annotate[T](x: T): (List[Nothing], T, List[Nothing]) = (List(), x, List())

  def annotatedNode[T](x: T, id: AstNode.Id): (List[Nothing], AstNode[T], List[Nothing]) = annotate(AstNode.create(x, id))

  def duplicate[T](x: T): (T, T) = (x, x)

}
