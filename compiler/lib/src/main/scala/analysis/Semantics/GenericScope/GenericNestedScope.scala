package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A type-generic stack of scopes */
case class GenericNestedScope[NG, S <: SymbolInterface](
  scopes: List[GenericScope[NG,S]]
) {

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

  /** Get a relative or absolute symbol from the nested scope */
  def get (isAbsolute: Boolean): NG => Name.Unqualified => Option[S] =
    isAbsolute match {
      case false => getRelative
      case true => getAbsolute
    }

  /** Get a relative symbol from the nested scope. Start searching at the
   *  bottom of the stack, working upwards. Return None if the name is not there. */
  def getRelative (nameGroup: NG) (name: Name.Unqualified): Option[S] = {
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

  /** Get an absolute symbol from the nested scope. Start searching at
   *  the top of the stack. Return None if the name is not there */
  def getAbsolute (nameGroup: NG) (name: Name.Unqualified): Option[S] =
    this.scopes.reverse match {
      case Nil => None
      case head :: tail => head.get(nameGroup)(name)
    }

  /** Get the innermost nested scope */
  def innerScope: GenericScope[NG,S] = splitScopes._1

  private def splitScopes: (GenericScope[NG,S], List[GenericScope[NG,S]]) = scopes match {
    case head :: tail => (head, tail)
    case _ => throw new InternalError("empty scope stack")
  }

}
