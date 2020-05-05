package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._

/** General helpers */
object Helpers {

  def annotate[T](x: T) = (List(), x, List())

  def annotatedNode[T](x: T, id: AstNode.Id) = annotate(AstNode.create(x, id))

  def duplicate[T](x: T) = (x, x)

}
