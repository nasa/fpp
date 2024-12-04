package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A collection of name-symbol maps, one for each name group */
type Scope = GenericScope[NameGroup,Symbol]

object Scope {

  /** Create an empty Scope */
  def empty: Scope = GenericScope[NameGroup,Symbol]()

}
