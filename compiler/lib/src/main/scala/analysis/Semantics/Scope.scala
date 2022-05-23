package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A collection of name-symbol maps, one for each name group */
sealed trait Scope {

  /** Get a symbol from the map. Throw an exception if the name is not there.*/
  def apply (nameGroup: NameGroup) (name: Name.Unqualified): Symbol

  /** Put a name and symbol into the map. */
  def put (nameGroup: NameGroup) (name: Name.Unqualified, symbol: Symbol): Result.Result[Scope]

  /** Get a symbol from the map. Return none if the name is not there. */
  def get (nameGroup: NameGroup) (name: Name.Unqualified): Option[Symbol]

}

object Scope {

  /** Create an empty Scope */
  def empty: Scope = ScopeImpl()

}

private case class ScopeImpl(map: Map[NameGroup,NameSymbolMap] = Map()) 
  extends Scope
{

  override def apply (nameGroup: NameGroup) (name: Name.Unqualified) =
    getNameSymbolMap(nameGroup)(name)

  /** Get the name-symbol map for a name group */
  def getNameSymbolMap(nameGroup: NameGroup): NameSymbolMap =
    map.get(nameGroup) match {
      case Some(nameSymbolMap) => nameSymbolMap
      case None => NameSymbolMap.empty
    }

  override def put (nameGroup: NameGroup) (name: Name.Unqualified, symbol: Symbol) = {
    val nsm = getNameSymbolMap(nameGroup)
    for (nsm <- nsm.put(name, symbol)) 
      yield this.copy(map = this.map + (nameGroup -> nsm))
  }

  override def get (nameGroup: NameGroup) (name: Name.Unqualified) =
    getNameSymbolMap(nameGroup).get(name)

}
