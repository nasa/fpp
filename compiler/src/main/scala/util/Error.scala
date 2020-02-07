/**
 * A compilation error
 */

package fpp.compiler.util

/** An exception for signaling internal compiler errors */
final case class InternalError(private val msg: String) extends Exception

/** An algebraic data type for handling copmilation errors */
sealed trait Error {
  def print = {
    this match {
      case SyntaxError(loc, msg) => Error.print (Some(loc)) (msg)
      case FileError.CannotOpen(name) => 
        Error.print (None) (s"cannot open fle $name")
    }
  }
}

final case class SyntaxError(loc: Location, msg: String) extends Error

object FileError {
  final case class CannotOpen(name: String) extends Error
}

object Error {

  private var toolOpt: Option[Tool] = None

  def setTool(t: Tool) = { toolOpt = Some(t) }

  def printOpt[T](o: T) = {
    o match {
      case Some(t) => System.err.println(t.toString)
      case _ => ()
    }
  }

  def printTool = printOpt(toolOpt)

  def print (locOpt: Option[Location]) (msg: String) = {
    printTool
    printOpt(locOpt)
    System.err.println(msg)
  }

}
