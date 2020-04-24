package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A local mapping of unqualified names to symbols */
case class NameSymbolMap(map: Map[Name.Unqualified,Symbol] = Map()) {

  type Result = Result.Result[NameSymbolMap]

  /** Put a name and symbol into the map. */
  def +(ns: (Name.Unqualified, Symbol)): Result = {
    val (name, symbol) = ns
    map.get(name) match {
      case Some(prevSymbol) => {
        val loc = symbol.getLoc
        val prevLoc = prevSymbol.getLoc
        Left(SemanticError.RedefinedSymbol(name, loc, prevLoc))
      }
      case None => Right(NameSymbolMap(map + (name -> symbol)))
    }
  }

  /** Get a symbol from the map. Throw an InternalError if the name is not there.*/
  def get(name: Name.Unqualified): Symbol = getOpt(name) match {
    case Some(symbol) => symbol
    case _ => throw new InternalError(s"could not find symbol for name ${name}")
  }

  /** Get a symbol from the map. Return none if the name is not there. */
  def getOpt(name: Name.Unqualified) = map.get(name)

}
