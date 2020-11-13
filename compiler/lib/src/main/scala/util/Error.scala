package fpp.compiler.util

/** An exception for signaling internal compiler errors */
final case class InternalError(val msg: String) extends Exception {
  override def toString = s"internal error: $msg"
}

/** A data type for handling compilation errors */
sealed trait Error {

  /*** Print the error */
  def print = {
    this match {
      case SyntaxError(loc, msg) => Error.print (Some(loc)) (msg)
      case CodeGenError.DuplicateCppFile(file, loc, prevLoc) => {
        Error.print (Some(loc)) (s"duplicate C++ file ${file}")
        System.err.println(s"previous file would be generated here:")
        System.err.println(prevLoc)
      }
      case CodeGenError.DuplicateXmlFile(file, loc, prevLoc) => {
        Error.print (Some(loc)) (s"duplicate XML file ${file}")
        System.err.println(s"previous file would be generated here:")
        System.err.println(prevLoc)
      }
      case CodeGenError.EmptyStruct(loc) =>
        Error.print (Some(loc)) (s"cannot write XML for an empty struct")
      case IncludeError.Cycle(loc, msg) => Error.print (Some(loc)) (msg)
      case FileError.CannotOpen(locOpt, name) => 
        Error.print (locOpt) (s"cannot open file $name")
      case FileError.CannotResolvePath(loc, name) => 
        Error.print (Some(loc)) (s"cannot resolve path $name")
      case SemanticError.DivisionByZero(loc) =>
        Error.print (Some(loc)) ("division by zero")
      case SemanticError.DuplicateOpcodeValue(value, loc, prevLoc) => {
        Error.print (Some(loc)) (s"duplicate opcode value ${value}")
        System.err.println(s"previous occurrence is here:")
        System.err.println(prevLoc)
      }
      case SemanticError.DuplicateEnumValue(value, loc, prevLoc) => {
        Error.print (Some(loc)) (s"duplicate enum value ${value}")
        System.err.println(s"previous occurrence is here:")
        System.err.println(prevLoc)
      }
      case SemanticError.DuplicateParameter(name, loc, prevLoc) => {
        Error.print (Some(loc)) (s"duplicate parameter ${name}")
        System.err.println(s"previous parameter is here:")
        System.err.println(prevLoc)
      }
      case SemanticError.DuplicatePortInstance(name, loc, prevLoc) => {
        Error.print (Some(loc)) (s"duplicate port instance ${name}")
        System.err.println(s"previous instance is here:")
        System.err.println(prevLoc)
      }
      case SemanticError.DuplicateStructMember(name, loc, prevLoc) => {
        Error.print (Some(loc)) (s"duplicate struct member ${name}")
        System.err.println(s"previous member is here:")
        System.err.println(prevLoc)
      }
      case SemanticError.EmptyArray(loc) => 
        Error.print (Some(loc)) ("array expression may not be empty")
      case SemanticError.InconsistentSpecLoc(loc, path, prevLoc, prevPath) => {
        Error.print (Some(loc)) (s"inconsistent location path ${path}")
        System.err.println(prevLoc)
        System.err.println(s"previous path is ${prevPath}")
      }
      case SemanticError.IncorrectSpecLoc(loc, specifiedPath, actualLoc) => {
        Error.print (Some(loc)) (s"incorrect location path ${specifiedPath}")
        System.err.println(s"actual location is ${actualLoc}")
      }
      case SemanticError.InvalidArraySize(loc, size) =>
        Error.print (Some(loc)) (s"invalid array size $size")
      case SemanticError.InvalidEnumConstants(loc) =>
        Error.print (Some(loc)) ("enum constants must be all explicit or all implied")
      case SemanticError.InvalidIntValue(loc, v) =>
        Error.print (Some(loc)) (s"invalid integer value $v")
      case SemanticError.InvalidFormatString(loc, msg) =>
        Error.print (Some(loc)) (s"invalid format string: $msg")
      case SemanticError.InvalidInternalPort(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidPortInstance(loc, msg, defLoc) => {
        Error.print (Some(loc)) (msg)
        System.err.println(s"port definition is here:")
        System.err.println(defLoc)
      }
      case SemanticError.InvalidPriority(loc) =>
        Error.print (Some(loc)) ("only async input may have a priority")
      case SemanticError.InvalidQueueFull(loc) =>
        Error.print (Some(loc)) ("only async input may have queue full behavior")
      case SemanticError.InvalidStringSize(loc, size) =>
        Error.print (Some(loc)) (s"invalid string size $size")
      case SemanticError.InvalidSymbol(name, loc, msg) =>
        Error.print (Some(loc)) (s"invalid symbol $name: $msg")
      case SemanticError.InvalidType(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.NotImplemented(loc) =>
        Error.print (Some(loc)) ("language feature is not yet implemented")
      case SemanticError.RedefinedSymbol(name, loc, prevLoc) => {
        Error.print (Some(loc)) (s"redefinition of symbol ${name}")
        System.err.println(s"previous definition is here:")
        System.err.println(prevLoc)
      }
      case SemanticError.TypeMismatch(loc, msg) => Error.print (Some(loc)) (msg)
      case SemanticError.UndefinedSymbol(name, loc) =>
        Error.print (Some(loc)) (s"undefined symbol ${name}")
      case SemanticError.UseDefCycle(loc, msg) => Error.print (Some(loc)) (msg)
      case XmlError.ParseError(file, msg) => Error.printXml (file) (msg)
      case XmlError.SemanticError(file, msg) => Error.printXml (file) (msg)
    }
  }

}

/** A syntax error */
final case class SyntaxError(loc: Location, msg: String) extends Error

/** A code generation error */
object CodeGenError {
  /** Duplicate C++ file path */
  final case class DuplicateCppFile(file: String, loc: Location, prevLoc: Location) extends Error
  /** Duplicate XML file path */
  final case class DuplicateXmlFile(file: String, loc: Location, prevLoc: Location) extends Error
  /** Empty struct */
  final case class EmptyStruct(loc: Location) extends Error
}

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
  /** Empty array */
  final case class EmptyArray(loc: Location) extends Error
  /** Division by zero */
  final case class DivisionByZero(loc: Location) extends Error
  /** Duplicate opcode */
  final case class DuplicateOpcodeValue(
    value: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate enum value */
  final case class DuplicateEnumValue(
    value: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate parameter */
  final case class DuplicateParameter(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate port instance */
  final case class DuplicatePortInstance(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate struct member */
  final case class DuplicateStructMember(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Inconsistent location specifiers */
  final case class InconsistentSpecLoc(
    loc: Location,
    path: String,
    prevLoc: Location,
    prevPath: String
  ) extends Error
  /** Incorrect location specifiers */
  final case class IncorrectSpecLoc(
    loc: Location,
    specifiedPath: String,
    actualLoc: Location
  ) extends Error
  /** Invalid array size */
  final case class InvalidArraySize(loc: Location, size: BigInt) extends Error
  /** Invalid enum constants */
  final case class InvalidEnumConstants(loc: Location) extends Error
  /** Invalid format string  */
  final case class InvalidFormatString(loc: Location, msg: String) extends Error
  /** Invalid integer value */
  final case class InvalidIntValue(loc: Location, v: BigInt) extends Error
  /** Invalid internal port */
  final case class InvalidInternalPort(loc: Location, msg: String) extends Error
  /** Invalid port instance */
  final case class InvalidPortInstance(loc: Location, msg: String, defLoc: Location) extends Error
  /** Invalid priority specifier */
  final case class InvalidPriority(loc: Location) extends Error
  /** Invalid queue full specifier */
  final case class InvalidQueueFull(loc: Location) extends Error
  /** Invalid string size */
  final case class InvalidStringSize(loc: Location, size: BigInt) extends Error
  /** Invalid symbol */
  final case class InvalidSymbol(name: String, loc: Location, msg: String) extends Error
  /** Invalid type */
  final case class InvalidType(loc: Location, msg: String) extends Error
  /** Feature not implemented */
  final case class NotImplemented(loc: Location) extends Error
  /** Redefined symbol */
  final case class RedefinedSymbol(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Undefined symbol */
  final case class UndefinedSymbol(name: String, loc: Location) extends Error
  /** Use-def cycle */
  final case class UseDefCycle(loc: Location, msg: String) extends Error
  /** Type mismatch */
  final case class TypeMismatch(loc: Location, msg: String) extends Error
}

/** An F Prime XML error */
object XmlError {
  /** A parse error */
  final case class ParseError(file: String, msg: String) extends Error
  /** A semantic error */
  final case class SemanticError(file: String, msg: String) extends Error
}

object Error {

  /** The max array size */
  val maxArraySize = 1000

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

  /** Print an XML file and a message */
  def printXml (file: String) (msg: String) = {
    printTool
    System.err.println(s"file: $file")
    System.err.print("error: ")
    System.err.println(msg)
  }

}
