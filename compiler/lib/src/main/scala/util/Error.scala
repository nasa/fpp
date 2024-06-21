package fpp.compiler.util

/** An exception for signaling internal compiler errors */
final case class InternalError(val msg: String) extends Exception {
  override def toString = s"internal error: $msg"
}

/** A data type for handling compilation errors */
sealed trait Error {

  /** Print the location of a port matching */
  def printMatchingLoc(matchingLoc: Location): Unit = {
    System.err.println("port matching is specified here:")
    System.err.println(matchingLoc)
  }

  /** Print the location of a previous occurrence */
  def printPrevLoc(prevLoc: Location): Unit = {
    System.err.println("previous occurrence is here:")
    System.err.println(prevLoc)
  }

  /*** Print the error */
  def print: Unit = {
    this match {
      case SyntaxError(loc, msg) => Error.print (Some(loc)) (msg)
      case CodeGenError.DuplicateCppFile(file, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate C++ file ${file}")
        System.err.println("previous file would be generated here:")
        System.err.println(prevLoc)
      case CodeGenError.DuplicateJsonFile(file, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate JSON file ${file}")
        System.err.println("previous file would be generated here:")
        System.err.println(prevLoc)
      case CodeGenError.DuplicateXmlFile(file, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate XML file ${file}")
        System.err.println("previous file would be generated here:")
        System.err.println(prevLoc)
      case CodeGenError.EmptyStruct(loc) =>
        Error.print (Some(loc)) (s"cannot write XML for an empty struct")
      case IncludeError.Cycle(loc, msg) => Error.print (Some(loc)) (msg)
      case FileError.CannotOpen(locOpt, name) => 
        Error.print (locOpt) (s"cannot open file $name")
      case FileError.CannotResolvePath(loc, name) => 
        Error.print (Some(loc)) (s"cannot resolve path $name")
      case SemanticError.DivisionByZero(loc) =>
        Error.print (Some(loc)) ("division by zero")
      case SemanticError.DuplicateDictionaryName(kind, name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate ${kind} name ${name}")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateEnumValue(value, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate enum value ${value}")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateIdValue(value, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate identifier value ${value}")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateInitSpecifier(phase, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate initialization for phase ${phase}")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateInstance(name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate instance $name")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateLimit(loc, prevLoc) =>
        Error.print (Some(loc)) ("duplicate limit")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateMatchedConnection(
        loc,
        prevLoc,
        matchingLoc
      ) => 
        Error.print (Some(loc)) ("duplicate connection between a matched port array and a single instance")
        printPrevLoc(prevLoc)
        printMatchingLoc(matchingLoc)
        System.err.println("note: each port in a matched port array must be connected to a separate instance")
      case SemanticError.DuplicateOpcodeValue(value, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate opcode value ${value}")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateOutputConnection(loc, portNum, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate connection at output port $portNum")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateParameter(name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate parameter ${name}")
        System.err.println("previous parameter is here:")
        System.err.println(prevLoc)
      case SemanticError.DuplicatePattern(kind, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate $kind pattern")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicatePortInstance(name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate port instance ${name}")
        System.err.println("previous instance is here:")
        System.err.println(prevLoc)
      case SemanticError.DuplicateStateMachineInstance(name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate state machine instance name ${name}")
        printPrevLoc(prevLoc)
      case SemanticError.DuplicateStructMember(name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate struct member ${name}")
        System.err.println("previous member is here:")
        System.err.println(prevLoc)
      case SemanticError.DuplicateTopology(name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"duplicate topology ${name}")
        printPrevLoc(prevLoc)
      case SemanticError.EmptyArray(loc) => 
        Error.print (Some(loc)) ("array expression may not be empty")
      case SemanticError.InconsistentSpecLoc(loc, path, prevLoc, prevPath) =>
        Error.print (Some(loc)) (s"inconsistent location path ${path}")
        System.err.println(prevLoc)
        System.err.println(s"previous path is ${prevPath}")
      case SemanticError.IncorrectSpecLoc(loc, specifiedPath, actualLoc) =>
        Error.print (Some(loc)) (s"incorrect location path ${specifiedPath}")
        System.err.println(s"actual location is ${actualLoc}")
      case SemanticError.InvalidArraySize(loc, size) =>
        Error.print (Some(loc)) (s"invalid array size $size")
      case SemanticError.InvalidCommand(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidComponentInstance(loc, instanceName, topName) =>
        Error.print (Some(loc)) (s"instance $instanceName is not a member of topology $topName")
      case SemanticError.InvalidConnection(loc, msg, fromLoc, toLoc, fromPortDefLoc, toPortDefLoc) =>
        Error.print (Some(loc)) (msg)
        System.err.println("from port is specified here:")
        System.err.println(fromLoc)
        System.err.println("to port is specified here:")
        System.err.println(toLoc)
        fromPortDefLoc match {
          case Some(loc) =>
            System.err.println("from port type is defined here:")
            System.err.println(loc)
          case _ => ()
        }
        toPortDefLoc match {
          case Some(loc) =>
            System.err.println("to port type is defined here:")
            System.err.println(loc)
          case _ => ()
        }
      case SemanticError.InvalidDataProducts(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidDefComponentInstance(name, loc, msg) =>
        Error.print (Some(loc)) (s"invalid component instance definition $name: $msg")
      case SemanticError.InvalidEnumConstants(loc) =>
        Error.print (Some(loc)) ("enum constants must be all explicit or all implied")
      case SemanticError.InvalidEvent(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidFormatString(loc, msg) =>
        Error.print (Some(loc)) (s"invalid format string: $msg")
      case SemanticError.InvalidIntValue(loc, v, msg) =>
        Error.print (Some(loc)) (s"invalid integer value $v")
        System.err.println(msg)
      case SemanticError.InvalidInternalPort(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidPattern(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidPortInstance(loc, msg, defLoc) =>
        Error.print (Some(loc)) (msg)
        System.err.println(s"port definition is here:")
        System.err.println(defLoc)
      case SemanticError.InvalidPortInstanceId(loc, portName, componentName) =>
        Error.print (Some(loc)) (s"$portName is not a port instance of component $componentName")
      case SemanticError.InvalidPortKind(loc, msg, specLoc) =>
        Error.print (Some(loc)) (msg)
        System.err.println(s"port instance is specified here:")
        System.err.println(specLoc)
      case SemanticError.InvalidPortMatching(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidPortNumber(loc, portNumber, port, arraySize, specLoc) =>
        Error.print (Some(loc)) (s"invalid port number $portNumber for port $port (max is ${arraySize - 1})")
        System.err.println(s"port instance is specified here:")
        System.err.println(specLoc)
      case SemanticError.InvalidPriority(loc) =>
        Error.print (Some(loc)) ("only async input may have a priority")
      case SemanticError.InvalidQueueFull(loc) =>
        Error.print (Some(loc)) ("only async input may have queue full behavior")
      case SemanticError.InvalidSpecialPort(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.InvalidStringSize(loc, size) =>
        Error.print (Some(loc)) (s"invalid string size $size")
      case SemanticError.InvalidSymbol(name, loc, msg, defLoc) =>
        Error.print (Some(loc)) (s"invalid symbol $name: $msg")
        System.err.println("symbol is defined here:")
        System.err.println(defLoc)
      case SemanticError.InvalidType(loc, msg) =>
        Error.print (Some(loc)) (msg)
      case SemanticError.MismatchedPortNumbers(
        p1Loc: Location,
        p1Number: Int,
        p2Loc: Location,
        p2Number: Int,
        matchingLoc: Location
      ) =>
        Error.print (Some(p1Loc)) (s"mismatched port numbers ($p1Number vs. $p2Number)")
        System.err.println("conflicting port number is here:")
        System.err.println(p2Loc)
        printMatchingLoc(matchingLoc)
      case SemanticError.MissingAsync(kind, loc) =>
        Error.print (Some(loc)) (s"$kind component must have async input")
      case SemanticError.MissingConnection(loc, matchingLoc) =>
        Error.print (Some(loc)) ("no match for this connection")
        printMatchingLoc(matchingLoc)
      case SemanticError.MissingPort(loc, specMsg, portMsg) =>
        Error.print (Some(loc)) (s"component with $specMsg must have $portMsg")
      case SemanticError.OverlappingIdRanges(
        maxId1, name1, loc1, baseId2, name2, loc2
      ) =>
        Error.print (None) (
          s"max ID $maxId1 for instance $name1 conflicts with base ID $baseId2 for instance $name2"
        )
        System.err.println(s"$name1 is defined here")
        System.err.println(loc1)
        System.err.println(s"$name2 is defined here")
        System.err.println(loc2)
      case SemanticError.PassiveAsync(loc) =>
        Error.print (Some(loc)) ("passive component may not have async input")
      case SemanticError.PassiveStateMachine(loc) =>
        Error.print (Some(loc)) ("passive component may not have a state machine instance")
      case SemanticError.RedefinedSymbol(name, loc, prevLoc) =>
        Error.print (Some(loc)) (s"redefinition of symbol ${name}")
        System.err.println("previous definition is here:")
        System.err.println(prevLoc)
      case SemanticError.TooManyOutputPorts(loc, numPorts, arraySize, instanceLoc) =>
        Error.print (Some(loc)) (s"too many ports connected here (found $numPorts, max is $arraySize)")
        System.err.println("for this component instance:")
        System.err.println(instanceLoc)
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
  /** Duplicate JSON file path */
  final case class DuplicateJsonFile(file: String, loc: Location, prevLoc: Location) extends Error
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
  /** Duplicate name in dictionary */
  final case class DuplicateDictionaryName(
    kind: String,
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate enum value */
  final case class DuplicateEnumValue(
    value: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Dupliate ID value */
  final case class DuplicateIdValue(
    value: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate init specifier */
  final case class DuplicateInitSpecifier(
    phase: Int,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate instance */
  final case class DuplicateInstance(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate telemetry channel limit */
  final case class DuplicateLimit(
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate matched connection */
  final case class DuplicateMatchedConnection(
    loc: Location,
    prevLoc: Location,
    matchingLoc: Location
  ) extends Error
  /** Duplicate opcode value */
  final case class DuplicateOpcodeValue(
    value: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate output port */
  final case class DuplicateOutputConnection(
    loc: Location,
    portNum: Int,
    prevLoc: Location
  ) extends Error
  /** Duplicate parameter */
  final case class DuplicateParameter(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate pattern */
  final case class DuplicatePattern(
    kind: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate port instance */
  final case class DuplicatePortInstance(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Duplicate state machine instance */
  final case class DuplicateStateMachineInstance(
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
  /** Duplicate topology */
  final case class DuplicateTopology(
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
  /** Invalid command */
  final case class InvalidCommand(loc: Location, msg: String) extends Error
  /** Invalid component instance */
  final case class InvalidComponentInstance(
    loc: Location,
    instanceName: String,
    topName: String
  ) extends Error
  /** Invalid connection */
  final case class InvalidConnection(
    loc: Location,
    msg: String,
    fromLoc: Location,
    toLoc: Location,
    fromPortDefLoc: Option[Location] = None,
    toPortDefLoc: Option[Location] = None
  ) extends Error
  /** Invalid data products */
  final case class InvalidDataProducts(
    loc: Location,
    msg: String
  ) extends Error
  /** Invalid component instance definition */
  final case class InvalidDefComponentInstance(
    name: String,
    loc: Location,
    msg: String
  ) extends Error
  /** Invalid enum constants */
  final case class InvalidEnumConstants(loc: Location) extends Error
  /** Invalid event */
  final case class InvalidEvent(loc: Location, msg: String) extends Error
  /** Invalid format string  */
  final case class InvalidFormatString(loc: Location, msg: String) extends Error
  /** Invalid integer value */
  final case class InvalidIntValue(
    loc: Location,
    v: BigInt,
    msg: String
  ) extends Error
  /** Invalid internal port */
  final case class InvalidInternalPort(loc: Location, msg: String) extends Error
  /** Invalid connection pattern */
  final case class InvalidPattern(
    loc: Location,
    msg: String
  ) extends Error
  /** Invalid port instance */
  final case class InvalidPortInstance(
    loc: Location,
    msg: String,
    defLoc: Location
  ) extends Error
  /** Invalid port instance identifier */
  final case class InvalidPortInstanceId(
    loc: Location,
    portName: String,
    componentName: String
  ) extends Error
  /** Invalid port kind */
  final case class InvalidPortKind(
    loc: Location,
    msg: String,
    specLoc: Location
  ) extends Error
  /** Invalid port matching */
  final case class InvalidPortMatching(loc: Location, msg: String) extends Error
  /** Invalid port number */
  final case class InvalidPortNumber(
    loc: Location,
    portNumber: Int,
    port: String,
    size: Int,
    specLoc: Location
  ) extends Error
  /** Invalid priority specifier */
  final case class InvalidPriority(loc: Location) extends Error
  /** Invalid queue full specifier */
  final case class InvalidQueueFull(loc: Location) extends Error
  /** Invalid special port */
  final case class InvalidSpecialPort(loc: Location, msg: String) extends Error
  /** Invalid string size */
  final case class InvalidStringSize(loc: Location, size: BigInt) extends Error
  /** Invalid symbol */
  final case class InvalidSymbol(
    name: String,
    loc: Location,
    msg: String,
    defLoc: Location
  ) extends Error
  /** Invalid type */
  final case class InvalidType(loc: Location, msg: String) extends Error
  /** Mismatched port numbers */
  final case class MismatchedPortNumbers(
    p1Loc: Location,
    p1Number: Int,
    p2Loc: Location,
    p2Number: Int,
    matchingLoc: Location
  ) extends Error
  /** Missing async input */
  final case class MissingAsync(kind: String, loc: Location) extends Error
  /** Missing connection */
  final case class MissingConnection(
    loc: Location,
    matchingLoc: Location
  ) extends Error
  /** Missing port */
  final case class MissingPort(
    loc: Location,
    specMsg: String,
    portmsg: String
  ) extends Error
  /** Overlapping ID ranges */
  final case class OverlappingIdRanges(
    maxId1: BigInt,
    name1: String,
    loc1: Location,
    baseId2: BigInt,
    name2: String,
    loc2: Location
  ) extends Error
  /** Passive async input */
  final case class PassiveAsync(loc: Location) extends Error
  final case class PassiveStateMachine(loc: Location) extends Error
  /** Redefined symbol */
  final case class RedefinedSymbol(
    name: String,
    loc: Location,
    prevLoc: Location
  ) extends Error
  /** Too many output ports */
  final case class TooManyOutputPorts(
    loc: Location,
    numPorts: Int,
    arraySize: Int,
    instanceLoc: Location
  ) extends Error
  /** Type mismatch */
  final case class TypeMismatch(loc: Location, msg: String) extends Error
  /** Undefined symbol */
  final case class UndefinedSymbol(name: String, loc: Location) extends Error
  /** Use-def cycle */
  final case class UseDefCycle(loc: Location, msg: String) extends Error
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
  val maxArraySize = 256

  private var toolOpt: Option[Tool] = None

  /** Set the tool */
  def setTool(t: Tool): Unit = { toolOpt = Some(t) }

  /** Print an optional value */
  def printOpt[T](opt: T): Unit = {
    opt match {
      case Some(t) => System.err.println(t.toString)
      case _ => ()
    }
  }

  /** Print the tool */
  def printTool: Unit = printOpt(toolOpt)

  /** Print an optional location and a message */
  def print (locOpt: Option[Location]) (msg: String): Unit = {
    printTool
    printOpt(locOpt)
    System.err.print("error: ")
    System.err.println(msg)
  }

  /** Print an XML file and a message */
  def printXml (file: String) (msg: String): Unit = {
    printTool
    System.err.println(s"file: $file")
    System.err.print("error: ")
    System.err.println(msg)
  }

}
