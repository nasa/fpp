package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A stack of scopes */
type NestedScope = GenericNestedScope[NameGroup, Symbol]

object NestedScope {

  /** Create an empty NestedScope */
  def empty: NestedScope = GenericNestedScope(List(Scope.empty))

}
