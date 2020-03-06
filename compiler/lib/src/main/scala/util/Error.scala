package fpp.compiler.util

/** An exception for signaling internal compiler errors */
final case class InternalError(private val msg: String) extends Exception

/** A data type for handling compilation errors */
sealed trait Error {

  /*** Print the error */
  def print = {
    this match {
      case SyntaxError(loc, msg) => Error.print (Some(loc)) (msg)
      case FileError.CannotOpen(name) => 
        Error.print (None) (s"cannot open fle $name")
    }
  }

}

/** A syntax error */
final case class SyntaxError(loc: Location, msg: String) extends Error

/** A file error */
object FileError {
  /** Cannot open file */
  final case class CannotOpen(name: String) extends Error
}

object Error {

  private var toolOpt: Option[Tool] = None

  /** Set the tool */
  def setTool(t: Tool) = { toolOpt = Some(t) }

  /** Print an optional value */
  def printOpt[T](opt: T) = {
    opt match {
      case Some(t) => System.err.println(t.toString)
      case _ => ()
    }
  }

  /** Print the tool */
  def printTool = printOpt(toolOpt)

  /** Print an optioanl location and a message */
  def print (locOpt: Option[Location]) (msg: String) = {
    printTool
    printOpt(locOpt)
    System.err.print("error: ")
    System.err.println(msg)
  }

}
