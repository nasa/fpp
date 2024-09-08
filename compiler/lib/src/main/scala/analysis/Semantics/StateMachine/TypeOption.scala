package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP type option */
object TypeOption {

  type T = Option[Type]

  def show(to: T) = to.map(_.toString).getOrElse("None")

}
