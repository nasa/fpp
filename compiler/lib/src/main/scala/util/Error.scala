package fpp.compiler.util

/** An exception for signaling internal compiler errors */
final case class InternalError(private val msg: String) extends Exception

/** A data type for handling compilation errors */
sealed trait Error {

  /*** Print the error */
  def print = {
    this match {
      case SyntaxError(loc, msg) => Error.print (Some(loc)) (msg)
      case IncludeError.Cycle(loc, msg) => Error.print (Some(loc)) (msg)
      case FileError.CannotOpen(locOpt, name) => 
        Error.print (locOpt) (s"cannot open file $name")
      case FileError.CannotResolvePath(loc, name) => 
        Error.print (Some(loc)) (s"cannot resolve path $name")
      case SemanticError.InconsistentSpecLoc(loc, path, prevLoc, prevPath) => {
        Error.print (Some(loc)) (s"inconsistent location path ${path}")
        System.err.println(prevLoc)
        System.err.println(s"previous path was ${prevPath}")
      }
      case SemanticError.RedefinedSymbol(name, loc, prevLoc) => {
        Error.print (Some(loc)) (s"redefinition of symbol ${name}")
        System.err.println(s"previous definition was here:")
        System.err.println(prevLoc)
      }
      case SemanticError.TypeMismatch(loc, msg) => Error.print (Some(loc)) ("type mismatch: " ++ msg)
      case SemanticError.UndefinedSymbol(name, loc) =>
        Error.print (Some(loc)) (s"undefined symbol ${name}")
      case SemanticError.UseDefCycle(loc, msg) => Error.print (Some(loc)) (msg)
    }
  }

}

/** A syntax error */
final case class SyntaxError(loc: Location, msg: String) extends Error

/** An include error */
object IncludeError {
  /** Include cycle */
  final case class Cycle(loc: Location, msg: String) extends Error
}

/** A file error */
object FileError {
  /** Cannot open file */
  final case class CannotOpen(locOpt: Option[Location], name: String) extends Error
  /** Cannot resolve path */
  final case class CannotResolvePath(loc: Location, name: String) extends Error
}

/** A semantic error */
object SemanticError {
  /** Inconsistent location specifiers */
  final case class InconsistentSpecLoc(
    loc: Location,
    path: String,
    prevLoc: Location,
    prevPath: String
  ) extends Error
  /** Redefined symbol */
  final case class RedefinedSymbol(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Undefined symbol */
  final case class UndefinedSymbol(
    name: String,
    loc: Location
  ) extends Error
  /** Use-def cycle */
  final case class UseDefCycle(loc: Location, msg: String) extends Error
  /** Type mismatch */
  final case class TypeMismatch(loc: Location, msg: String) extends Error
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

  /** Print an optional location and a message */
  def print (locOpt: Option[Location]) (msg: String) = {
    printTool
    printOpt(locOpt)
    System.err.print("error: ")
    System.err.println(msg)
  }

}
