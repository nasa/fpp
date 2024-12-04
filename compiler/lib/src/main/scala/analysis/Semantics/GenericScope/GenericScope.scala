package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A generic collection of name-symbol maps, one for each name group */
case class GenericScope[NG, S <: SymbolInterface](
  map: Map[NG,GenericNameSymbolMap[S]] = Map[NG,GenericNameSymbolMap[S]]()
) {

  /** Get a symbol from the map. Throw an exception if the name is not there.*/
  def apply (nameGroup: NG) (name: Name.Unqualified): S =
    getNameSymbolMap(nameGroup)(name)

  /** Put a name and symbol into the map. */
  def put (nameGroup: NG) (name: Name.Unqualified, symbol: S): Result.Result[GenericScope[NG,S]] = {
    val nsm = getNameSymbolMap(nameGroup)
    for (nsm <- nsm.put(name, symbol))
      yield this.copy(map = this.map + (nameGroup -> nsm))
  }

  /** Get a symbol from the map. Return none if the name is not there. */
  def get (nameGroup: NG) (name: Name.Unqualified): Option[S] =
    getNameSymbolMap(nameGroup).get(name)

  /** Get the name-symbol map for a name group */
  private def getNameSymbolMap(nameGroup: NG): GenericNameSymbolMap[S] =
    map.get(nameGroup) match {
      case Some(nameSymbolMap) => nameSymbolMap
      case None => GenericNameSymbolMap[S]()
    }

}
