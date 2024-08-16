package fpp.compiler.syntax

import fpp.compiler.util._
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.Positional
import scala.util.parsing.input.Position

object Lexer extends RegexParsers {

  def eol: Parser[Token] = {
    newlines ^^ { _ => Token.EOL() }
  }

  def identifier: Parser[Token] = positioned {
    "\\$?[A-Za-z_][A-Za-z_0-9]*".r ^^ { s => Token.IDENTIFIER(s.replaceAll("\\$", "")) }
  }

  def ignore: Parser[Unit] = {
    // Ignore a comment not followed by a newline
    def comment: Parser[Unit] = unitParser(newlinesOpt ~ "#[^\r\n]*".r)
    def spaces: Parser[Unit] = unitParser(" +".r)
    def escapedNewline: Parser[Unit] = unitParser("\\" ~ newline)
    unitParser(rep(comment | spaces | escapedNewline))
  }

  def lexFile(file: File, includingLoc: Option[Location] = None): Result.Result[List[Token]] = {
    ParserState.file = file
    ParserState.includingLoc = includingLoc
    for {
      reader <- file.openRead(ParserState.includingLoc)
      result <- checkResult(parse(tokens, reader))
    } yield result
  }

  def lexString(s: String): Result.Result[List[Token]] = {
    ParserState.file = File.StdIn
    ParserState.includingLoc = None
    checkResult(parse(tokens, s))
  }

  def literalFloat: Parser[Token] = positioned {
    (
      "[0-9]+[Ee][+-]?[0-9]+".r |
      raw"[0-9]*\.[0-9]+([Ee][+-]?[0-9]+)?".r |
      raw"[0-9]+\.[0-9]*([Ee][+-]?[0-9]+)?".r
    ) ^^ { Token.LITERAL_FLOAT(_) }
  }

  def literalInt: Parser[Token] = positioned {
    ( "0[Xx][0-9a-fA-F]+".r | "[0-9]+".r ) ^^ { Token.LITERAL_INT(_) }
  }

  def literalString: Parser[Token] = positioned {
    literalStringMulti | literalStringSingle
  }

  def literalStringMulti: Parser[Token] = positioned {
    "\"\"\"" ~>! stringContent(3) ^^ {
      case s0 => {
        val s = "^\\r?\\n".r.replaceAllIn(s0, "")
        val numInitialSpaces = {
          def recurse(s: String, n: Int): Int =
            if (s.length == 0) n
            else if (s.head == ' ') recurse(s.tail, n + 1)
            else n
          recurse(s, 0)
        }
        def stripPrefix(s: String): String = {
          def recurse(pos: Int, s: String): String = {
            if (s.length == 0 || pos >= numInitialSpaces) s
            else if (s.head == ' ') recurse(pos + 1, s.tail)
            else s
          }
          recurse(0, s)
        }
        val ss  = s.split("\\r?\\n").dropWhile(_.length == 0).toList
        val ss1 = ss.map(stripPrefix)
        val s1 = ss1.mkString("\n")
        Token.LITERAL_STRING(s1)
      }
    }
  }

  def literalStringSingle: Parser[Token] = positioned {
    "\"" ~>! stringContent(1) ^^ { case s => Token.LITERAL_STRING(s) }
  }

  def newline: Parser[Unit] =
    // Convert a comment followed by a newline to a newline
    unitParser(" *(#[^\r\n]*)?\r?\n *".r)

  def parseAllInput[T](p: Parser[T]): parseAllInput[T] = new parseAllInput[T](p)
  class parseAllInput[T](p: Parser[T]) extends Parser[T] {
    def dropWhile(in: Input, p: Char => Boolean): Input = {
      if (in.atEnd) in
      else if (p(in.first)) dropWhile(in.rest, p)
      else in
    }
    def apply(in: Input) = p(in) match {
      case s @ Success(out, in1) =>
        if (in1.atEnd) s
        else {
          // We hit an illegal character
          val in2 = dropWhile(in1, _ == ' ')
          val c = in2.first
          val errMsg = {
            // Print out the unicode value, in case the console can't
            // display it as a character
            val prefix =
              s"illegal character (unicode value ${c.toInt}, hex 0x${c.toInt.toHexString})"
            // Generate a special error message for tab characters
            // These are especially tricky and also somewhat common
            if (c == '\t')
              List(
                prefix,
                "note: embedded tab characters are not allowed in FPP source files",
                "try configuring your editor to convert tabs to spaces"
              ).mkString("\n")
            else prefix
          }
          Failure(errMsg, in2)
        }
      case other => other
    }
  }

  def postAnnotation: Parser[Token] = {
    "@<[^\r\n]*".r <~ newlinesOpt ^^ {
      case s => {
        val s1 = s.stripPrefix("@<").trim
        Token.POST_ANNOTATION(s1)
      }
    }
  }

  def preAnnotation: Parser[Token] = {
    "@[^\r\n]*".r <~ newlinesOpt ^^ {
      case s => {
        val s1 = s.stripPrefix("@").trim
        Token.PRE_ANNOTATION(s1)
      }
    }
  }

  def reservedWord: Parser[Token] = positioned {
    type ST = (String, Unit => Token)
    def f(st: ST): Parser[Token] = {
      val (s, t) = st
      (s ++ raw"\b").r ^^ { _ => t(()) }
    }
    (reservedWords map f).foldRight (internalError) ((x, y) => y | x)
  }

  def stringContent(numEndMarks: Int): Parser[String] =
    new Parser[String] {
      /** The state of the string parser */
      trait State
      /** Reading state */
      case class Reading(
        /** The number of consecutive marks read so far */
        marks: String
      ) extends State
      /** The escaping state immediately after \ */
      case object Escaping extends State
      def parse(s: State, in: Input, out: StringBuilder): ParseResult[String] =
        if (in.atEnd)
          Failure("unterminated string at end of input", in)
        else if (numEndMarks == 1 && in.first == '\n')
          Failure("unterminated string before newline", in.rest)
        else s match {
          case Reading(marks) => in.first match {
            case '"' =>
              if (marks.length >= numEndMarks)
                throw new InternalError(
                  s"marks.length=$marks.length, numEndMarks=$numEndMarks"
                )
              else if (marks.length + 1 == numEndMarks)
                Success(out.result(), in.rest)
              else parse(Reading(marks + "\""), in.rest, out)
            case '\\' => parse(Escaping, in.rest, out.append(marks))
            case c =>
              parse(Reading(""), in.rest, out.append(marks).append(c))
          }
          case Escaping => parse(Reading(""), in.rest, out.append(in.first))
        }
      def apply(in: Input) = parse(Reading(""), in, new StringBuilder())
    }

  def symbol: Parser[Token] = positioned {
    type PSTP = (Parser[Unit], String, Unit => Token, Parser[Unit])
    def f(pstp: PSTP): Parser[Token] = {
      val (p1, s, t, p2) = pstp
      p1 ~> accept(s.toList) <~ p2 ^^ { _ => t(()) }
    }
    (symbols map f).foldRight (internalError) ((x, y) => y | x)
  }

  def token: Parser[Token] = positioned {
    reservedWord |
    identifier |
    literalFloat |
    literalInt |
    literalString |
    postAnnotation |
    preAnnotation |
    symbol |
    eol
  }

  def tokens: Parser[List[Token]] = {
    val p = ignore ~> repsep(token, ignore) <~ ignore
    parseAllInput(p)
  }

  def unitParser[T](p: Parser[T]): Parser[Unit] = { p ^^ { _ => () } }

  override def skipWhitespace = false

  private def checkResult[T](pr: ParseResult[T]): Result.Result[T] = {
    pr match {
      case NoSuccess(msg, next) => {
        val loc = Location(ParserState.file, next.pos, ParserState.includingLoc)
        Left(SyntaxError(loc, msg))
      }
      case Success(result, _) => Right(result)
      // Suppress false compiler warning
      case _ => throw new InternalError("This cannot happen")
    }
  }

  val internalError: Parser[Token] = failure("internal error"): Parser[Token]

  val newlines: Parser[Unit] = unitParser(rep1(newline))

  val newlinesOpt: Parser[Unit] = unitParser(rep(newline))

  val nothing: Parser[Unit] = success(())

  val reservedWords: List[(String, Unit => Token)] = List(
    ("F32", (u: Unit) => Token.F32()),
    ("F64", (u: Unit) => Token.F64()),
    ("I16", (u: Unit) => Token.I16()),
    ("I32", (u: Unit) => Token.I32()),
    ("I64", (u: Unit) => Token.I64()),
    ("I8", (u: Unit) => Token.I8()),
    ("U16", (u: Unit) => Token.U16()),
    ("U32", (u: Unit) => Token.U32()),
    ("U64", (u: Unit) => Token.U64()),
    ("U8", (u: Unit) => Token.U8()),
    ("active", (u: Unit) => Token.ACTIVE()),
    ("activity", (u: Unit) => Token.ACTIVITY()),
    ("always", (u: Unit) => Token.ALWAYS()),
    ("array", (u: Unit) => Token.ARRAY()),
    ("assert", (u: Unit) => Token.ASSERT()),
    ("async", (u: Unit) => Token.ASYNC()),
    ("at", (u: Unit) => Token.AT()),
    ("base", (u: Unit) => Token.BASE()),
    ("block", (u: Unit) => Token.BLOCK()),
    ("bool", (u: Unit) => Token.BOOL()),
    ("change", (u: Unit) => Token.CHANGE()),
    ("command", (u: Unit) => Token.COMMAND()),
    ("component", (u: Unit) => Token.COMPONENT()),
    ("connections", (u: Unit) => Token.CONNECTIONS()),
    ("constant", (u: Unit) => Token.CONSTANT()),
    ("container", (u: Unit) => Token.CONTAINER()),
    ("cpu", (u: Unit) => Token.CPU()),
    ("default", (u: Unit) => Token.DEFAULT()),
    ("diagnostic", (u: Unit) => Token.DIAGNOSTIC()),
    ("drop", (u: Unit) => Token.DROP()),
    ("enum", (u: Unit) => Token.ENUM()),
    ("event", (u: Unit) => Token.EVENT()),
    ("false", (u: Unit) => Token.FALSE()),
    ("fatal", (u: Unit) => Token.FATAL()),
    ("format", (u: Unit) => Token.FORMAT()),
    ("get", (u: Unit) => Token.GET()),
    ("guarded", (u: Unit) => Token.GUARDED()),
    ("health", (u: Unit) => Token.HEALTH()),
    ("high", (u: Unit) => Token.HIGH()),
    ("hook", (u: Unit) => Token.HOOK()),
    ("id", (u: Unit) => Token.ID()),
    ("import", (u: Unit) => Token.IMPORT()),
    ("include", (u: Unit) => Token.INCLUDE()),
    ("input", (u: Unit) => Token.INPUT()),
    ("instance", (u: Unit) => Token.INSTANCE()),
    ("internal", (u: Unit) => Token.INTERNAL()),
    ("locate", (u: Unit) => Token.LOCATE()),
    ("low", (u: Unit) => Token.LOW()),
    ("machine", (u: Unit) => Token.MACHINE()),
    ("match", (u: Unit) => Token.MATCH()),
    ("module", (u: Unit) => Token.MODULE()),
    ("on", (u: Unit) => Token.ON()),
    ("opcode", (u: Unit) => Token.OPCODE()),
    ("orange", (u: Unit) => Token.ORANGE()),
    ("output", (u: Unit) => Token.OUTPUT()),
    ("param", (u: Unit) => Token.PARAM()),
    ("passive", (u: Unit) => Token.PASSIVE()),
    ("phase", (u: Unit) => Token.PHASE()),
    ("port", (u: Unit) => Token.PORT()),
    ("priority", (u: Unit) => Token.PRIORITY()),
    ("private", (u: Unit) => Token.PRIVATE()),
    ("product", (u: Unit) => Token.PRODUCT()),
    ("queue", (u: Unit) => Token.QUEUE()),
    ("queued", (u: Unit) => Token.QUEUED()),
    ("record", (u: Unit) => Token.RECORD()),
    ("recv", (u: Unit) => Token.RECV()),
    ("red", (u: Unit) => Token.RED()),
    ("ref", (u: Unit) => Token.REF()),
    ("reg", (u: Unit) => Token.REG()),
    ("request", (u: Unit) => Token.REQUEST()),
    ("resp", (u: Unit) => Token.RESP()),
    ("save", (u: Unit) => Token.SAVE()),
    ("send", (u: Unit) => Token.SEND()),
    ("serial", (u: Unit) => Token.SERIAL()),
    ("set", (u: Unit) => Token.SET()),
    ("severity", (u: Unit) => Token.SEVERITY()),
    ("size", (u: Unit) => Token.SIZE()),
    ("stack", (u: Unit) => Token.STACK()),
    ("state", (u: Unit) => Token.STATE()),
    ("string", (u: Unit) => Token.STRING()),
    ("struct", (u: Unit) => Token.STRUCT()),
    ("sync", (u: Unit) => Token.SYNC()),
    ("telemetry", (u: Unit) => Token.TELEMETRY()),
    ("text", (u: Unit) => Token.TEXT()),
    ("throttle", (u: Unit) => Token.THROTTLE()),
    ("time", (u: Unit) => Token.TIME()),
    ("topology", (u: Unit) => Token.TOPOLOGY()),
    ("true", (u: Unit) => Token.TRUE()),
    ("type", (u: Unit) => Token.TYPE()),
    ("update", (u: Unit) => Token.UPDATE()),
    ("warning", (u: Unit) => Token.WARNING()),
    ("with", (u: Unit) => Token.WITH()),
    ("yellow", (u: Unit) => Token.YELLOW()),
  )

  val reservedWordSet = reservedWords.map(_._1).toSet

  val symbols: List[(Parser[Unit], String, Unit => Token, Parser[Unit])] = List(
    (newlinesOpt, ")", (u: Unit) => Token.RPAREN(), nothing),
    (newlinesOpt, "]", (u: Unit) => Token.RBRACKET(), nothing),
    (newlinesOpt, "}", (u: Unit) => Token.RBRACE(), nothing),
    (nothing, "(", (u: Unit) => Token.LPAREN(), newlinesOpt),
    (nothing, "*", (u: Unit) => Token.STAR(), newlinesOpt),
    (nothing, "+", (u: Unit) => Token.PLUS(), newlinesOpt),
    (nothing, ",", (u: Unit) => Token.COMMA(), newlinesOpt),
    (nothing, "-", (u: Unit) => Token.MINUS(), newlinesOpt),
    (nothing, "->", (u: Unit) => Token.RARROW(), newlinesOpt),
    (nothing, ".", (u: Unit) => Token.DOT(), nothing),
    (nothing, "/", (u: Unit) => Token.SLASH(), newlinesOpt),
    (nothing, ":", (u: Unit) => Token.COLON(), newlinesOpt),
    (nothing, ";", (u: Unit) => Token.SEMI(), newlinesOpt),
    (nothing, "=", (u: Unit) => Token.EQUALS(), newlinesOpt),
    (nothing, "[", (u: Unit) => Token.LBRACKET(), newlinesOpt),
    (nothing, "{", (u: Unit) => Token.LBRACE(), newlinesOpt),
  )

}
