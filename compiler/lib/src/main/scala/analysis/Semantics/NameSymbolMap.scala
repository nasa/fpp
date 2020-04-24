package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A local mapping of unqualified names to symbols */
sealed trait NameSymbolMap {

  /** Put a name and symbol into the map. */
  def put(name: Name.Unqualified, symbol: Symbol): Result.Result[NameSymbolMap]

  /** Get a symbol from the map. Throw an InternalError if the name is not there.*/
  def get(name: Name.Unqualified): Symbol

  /** Get a symbol from the map. Return none if the name is not there. */
  def getOpt(name: Name.Unqualified): Option[Symbol]

}

object NameSymbolMap {

  /** Create an empty NameSymbolMap */
  def empty: NameSymbolMap = NameSymbolMapImpl()

}

private case class NameSymbolMapImpl(map: Map[Name.Unqualified,Symbol] = Map()) 
  extends NameSymbolMap
{

  def put(name: Name.Unqualified, symbol: Symbol) = {
    map.get(name) match {
      case Some(prevSymbol) => {
        val loc = symbol.getLoc
        val prevLoc = prevSymbol.getLoc
        Left(SemanticError.RedefinedSymbol(name, loc, prevLoc))
      }
      case None => Right(NameSymbolMapImpl(map + (name -> symbol)))
    }
  }

  def get(name: Name.Unqualified) = getOpt(name) match {
    case Some(symbol) => symbol
    case _ => throw new InternalError(s"could not find symbol for name ${name}")
  }

  def getOpt(name: Name.Unqualified) = map.get(name)

}
