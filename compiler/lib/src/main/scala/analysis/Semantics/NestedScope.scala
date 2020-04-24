package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A stack of scopes */
sealed trait NestedScope extends Scope {

  /** Push a new scope onto the stack */
  def push: NestedScope

  /** Pop a scope off the stack */
  def pop: NestedScope

}

object NestedScope {

  /** Create an empty NestedScope */
  def empty: NestedScope = NestedScopeImpl()

}

private case class NestedScopeImpl(scopes: List[Scope] = List(Scope.empty))
  extends NestedScope
{

  def splitScopes = scopes match {
    case head :: tail => (head, tail)
    case _ => throw new InternalError("empty scope stack")
  }

  def push = NestedScopeImpl(Scope.empty :: this.scopes)

  def pop = {
    val (_, tail) = splitScopes
    NestedScopeImpl(tail)
  }

  def put (nameGroup: NameGroup) (name: Name.Unqualified, symbol: Symbol) = {
    val (head, tail) = splitScopes
    for (scope <- head.put(nameGroup)(name, symbol)) 
      yield NestedScopeImpl(scope :: tail)
  }

  def getOpt (nameGroup: NameGroup) (name: Name.Unqualified) = {
    def helper(scopes: List[Scope]): Option[Symbol] =
      scopes match {
        case Nil => None
        case head :: tail => head.getOpt(nameGroup)(name) match {
          case s @ Some(_) => s
          case None => helper(tail)
        }
      }
    helper(this.scopes)
  }

}
