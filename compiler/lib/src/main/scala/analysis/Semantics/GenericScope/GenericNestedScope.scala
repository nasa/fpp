package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A type-generic stack of scopes */
case class GenericNestedScope[NG, S <: SymbolInterface](
  scopes: List[GenericScope[NG,S]]
) {

  /** Get a symbol from the map. Throw an InternalError if the name is not there.*/
  def apply (nameGroup: NG) (name: Name.Unqualified): S = get(nameGroup)(name) match {
    case Some(symbol) => symbol
    case _ => throw new InternalError(s"could not find symbol for name ${name}")
  }


  /** Push a new scope onto the stack */
  def push(scope: GenericScope[NG,S]): GenericNestedScope[NG, S] =
    GenericNestedScope[NG, S](scope :: this.scopes)

  /** Pop a scope off the stack */
  def pop: GenericNestedScope[NG, S] = {
    val (_, tail) = splitScopes
    GenericNestedScope[NG, S](tail)
  }

  /** Put a name and symbol into the map. */
  def put (nameGroup: NG) (name: Name.Unqualified, symbol: S): Result.Result[GenericNestedScope[NG, S]] = {
    val (head, tail) = splitScopes
    for (scope <- head.put(nameGroup)(name, symbol))
      yield GenericNestedScope[NG, S](scope :: tail)
  }


  /** Get a symbol from the map. Return none if the name is not there. */
  def get (nameGroup: NG) (name: Name.Unqualified): Option[S] = {
    def helper(scopes: List[GenericScope[NG,S]]): Option[S] =
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
  def innerScope: GenericScope[NG,S] = splitScopes._1

  private def splitScopes: (GenericScope[NG,S], List[GenericScope[NG,S]]) = scopes match {
    case head :: tail => (head, tail)
    case _ => throw new InternalError("empty scope stack")
  }

}
