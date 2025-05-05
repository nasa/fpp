package fpp.compiler.syntax

import fpp.compiler.util.Error
import fpp.compiler.util.MultiError
import fpp.compiler.util.Result

class Context {
  private var errors: List[Error] = List()

  /** Report an error has  */
  def report(err: Error) = {
    errors = errors :+ err
  }

  def hasErrors: Boolean = errors.nonEmpty

  def print(): Unit = {
    errors.foreach(_.print)
  }

  def result(): Result.Result[Int] = {
    if hasErrors then Left(MultiError(errors))
    else Right(0)
  }
}
