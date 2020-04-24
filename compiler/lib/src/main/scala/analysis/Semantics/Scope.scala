package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A collection of name-symbol maps, one for each name group */
sealed trait Scope {

  /** Put a name and symbol into the map. */
  def put (nameGroup: NameGroup) (name: Name.Unqualified, symbol: Symbol): Result.Result[Scope]

  /** Get a symbol from the map. Throw an InternalError if the name is not there.*/
  def get (nameGroup: NameGroup) (name: Name.Unqualified): Symbol

  /** Get a symbol from the map. Return none if the name is not there. */
  def getOpt (nameGroup: NameGroup) (name: Name.Unqualified): Option[Symbol]

}

object Scope {

  /** Create an empty NameSymbolMap */
  def empty: Scope = ScopeImpl()

}

private case class ScopeImpl(map: Map[NameGroup,NameSymbolMap] = Map()) 
  extends Scope
{

  /** Get the name-symbol map for a name group */
  def getNameSymbolMap(nameGroup: NameGroup): NameSymbolMap =
    map.get(nameGroup) match {
      case Some(nameSymbolMap) => nameSymbolMap
      case None => NameSymbolMap.empty
    }

  def put (nameGroup: NameGroup) (name: Name.Unqualified, symbol: Symbol) = {
    val nsm = getNameSymbolMap(nameGroup)
    for (nsm <- nsm.put(name, symbol)) 
      yield this.copy(map = this.map + (nameGroup -> nsm))
  }

  def get (nameGroup: NameGroup) (name: Name.Unqualified) = 
    getNameSymbolMap(nameGroup).get(name)

  def getOpt (nameGroup: NameGroup) (name: Name.Unqualified) =
    getNameSymbolMap(nameGroup).getOpt(name)

}
