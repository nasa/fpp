/**
 * A compilation error
 */

package fpp.compiler.util

sealed trait Error {
  def print = {
    this match {
      case SyntaxError(loc, msg) => Error.print (Some(loc)) (msg)
      case FileError.CannotOpen(name) => 
        Error.print (None) (s"cannot open fle $name")
    }
  }
}

case class SyntaxError(loc: Location, msg: String) extends Error

object FileError {
  case class CannotOpen(name: String) extends Error
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
