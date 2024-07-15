package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A stack of scopes */
case class AbstractNestedScope[NG, S <: SymbolInterface](
  scopes: List[AbstractScope[NG,S]]
) {

  /** Get a symbol from the map. Throw an InternalError if the name is not there.*/
  def apply (nameGroup: NG) (name: Name.Unqualified): S = get(nameGroup)(name) match {
    case Some(symbol) => symbol
    case _ => throw new InternalError(s"could not find symbol for name ${name}")
  }


  /** Push a new scope onto the stack */
  def push(scope: AbstractScope[NG,S]): AbstractNestedScope[NG, S] =
    AbstractNestedScope[NG, S](scope :: this.scopes)

  /** Pop a scope off the stack */
  def pop: AbstractNestedScope[NG, S] = {
    val (_, tail) = splitScopes
    AbstractNestedScope[NG, S](tail)
  }

  /** Put a name and symbol into the map. */
  def put (nameGroup: NG) (name: Name.Unqualified, symbol: S): Result.Result[AbstractNestedScope[NG, S]] = {
    val (head, tail) = splitScopes
    for (scope <- head.put(nameGroup)(name, symbol)) 
      yield AbstractNestedScope[NG, S](scope :: tail)
  }


  /** Get a symbol from the map. Return none if the name is not there. */
  def get (nameGroup: NG) (name: Name.Unqualified): Option[S] = {
    def helper(scopes: List[AbstractScope[NG,S]]): Option[S] =
      scopes match {
        case Nil => None
        case head :: tail => head.get(nameGroup)(name) match {
          case s @ Some(_) => s
          case None => helper(tail)
        }
      }
    helper(this.scopes)
  }

  /** Get the innermost nested scope */
  def innerScope: AbstractScope[NG,S] = splitScopes._1

  private def splitScopes: (AbstractScope[NG,S], List[AbstractScope[NG,S]]) = scopes match {
    case head :: tail => (head, tail)
    case _ => throw new InternalError("empty scope stack")
  }

}

type NestedScope = AbstractNestedScope[NameGroup, Symbol]

object NestedScope {

  /** Create an empty NestedScope */
  def empty: NestedScope = AbstractNestedScope(List(Scope.empty))

}
