package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A local mapping of unqualified names to symbols */
type NameSymbolMap = GenericNameSymbolMap[Symbol]

object NameSymbolMap {

  val empty = GenericNameSymbolMap[Symbol]()

}
