package fpp.compiler.analysis

import fpp.compiler.ast._

/** A generic analysis visitor */
trait Analyzer extends AstStateVisitor {

  type State = Analysis

  /** Apply an analysis to an option value */
  final def opt[T] (f: (Analysis, T) => Result) (a: Analysis, o: Option[T]): Result =
    o match {
      case Some(x) => f(a, x)
      case None => Right(a)
    }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

}
