package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A type-generic local mapping of unqualified names to symbols */
case class GenericNameSymbolMap[S <: SymbolInterface](
  /** The map */
  val map: Map[Name.Unqualified,S] = Map()
) {

  /** Get a symbol from the map. Throw an exception if the name is not there.*/
  def apply(name: Name.Unqualified): S = map(name)

  /** Put a name and symbol into the map. */
  def put(name: Name.Unqualified, symbol: S): Result.Result[GenericNameSymbolMap[S]] = {
    map.get(name) match {
      case Some(prevSymbol) => {
        val loc = symbol.getLoc
        val prevLoc = prevSymbol.getLoc
        Left(SemanticError.RedefinedSymbol(name, loc, prevLoc))
      }
      case None => Right(GenericNameSymbolMap[S](map + (name -> symbol)))
    }
  }

  /** Get a symbol from the map. Return none if the name is not there. */
  def get(name: Name.Unqualified): Option[S] = map.get(name)

}
