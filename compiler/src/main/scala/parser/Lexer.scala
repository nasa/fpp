package fpp.compiler.parser

import fpp.compiler.util._
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.Positional

object Lexer extends RegexParsers {

  def apply(file: File, code: String): Result.Result[List[Token]] = {
    parse(tokens, code) match {
      case NoSuccess(msg, next) => Left(SyntaxError(Location(file, next.pos),msg))
      case Success(result, _) => Right(result)
    }
  }

  def eol: Parser[Token] = {
    newlines ^^ { _ => Token.EOL }
  }

  def identifier: Parser[Token] = positioned {
    "[A-Za-z_][A-Za-z_0-9]*".r ^^ { Token.IDENTIFIER(_) }
  }

  def ignore: Parser[Unit] = {
    def comment: Parser[Unit] = unitParser("#[^\r\n]*".r ~ newlinesOpt)
    def spaces: Parser[Unit] = unitParser(" +".r)
    def escapedNewline: Parser[Unit] = unitParser("\\" ~ newline)
    unitParser(rep(comment | spaces | escapedNewline))
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
    final case class PositionedString(s: String) extends Positional
    def positionedString: Parser[PositionedString] = positioned {
      "\"\"\"([^\\\\\"]*(\\\\(\")?)?)*\"\"\"".r ^^ { 
        case s => {
          val s1 = "\\\\\"".r.replaceAllIn(s, "\"")
          val s2 = s1.drop(3).dropRight(3)
          val s3 = "^\\n".r.replaceAllIn(s2, "")
          PositionedString(s3)
        }
      }
    }
    positionedString ^^ { 
      case ps @ PositionedString(s) => {
        val col = ps.pos.column
        def stripPrefix(s: String): String = {
          def recurse(pos: Int, s: String): String = {
            if (s.length == 0 || pos >= col) { s }
            else if (s.head == ' ') { recurse(pos+1, s.tail) }
            else { s }
          }
          recurse(0, s)
        }
        val ss  = s.split("\\r?\\n").toList
        val ss1 = ss.head :: ss.tail.map(stripPrefix)
        val s1 = ss1.mkString("\n")
        Token.LITERAL_STRING(s1)
      }
    }
  }

  def literalStringSingle: Parser[Token] = positioned {
    "\"([^\\\\\"\r\n]*(\\\\(\")?)?)*\"".r ^^ { 
      case s => {
        val s1 = "\\\\\"".r.replaceAllIn(s, "\"")
        val s2 = s1.drop(1).dropRight(1)
        Token.LITERAL_STRING(s2)
      }
    }
  }

  def newline: Parser[Unit] = unitParser(" *\r?\n *".r)

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
    type ST = (String, Token)
    def f(st: ST): Parser[Token] = {
      val (s, t) = st
      (s ++ raw"\b").r ^^ { _ => t }
    }
    (reservedWords map f).foldRight (internalError) ((x, y) => y | x)
  }

  def symbol: Parser[Token] = positioned {
    type PSTP = (Parser[Unit], String, Token, Parser[Unit])
    def f(pstp: PSTP): Parser[Token] = {
      val (p1, s, t, p2) = pstp
      p1 ~> accept(s.toList) <~ p2 ^^ { _ => t }
    }
    (symbols map f).foldRight (internalError) ((x, y) => y | x)
  }

  def token: Parser[Token] = positioned {
    reservedWord |
    eol |
    identifier | 
    literalFloat |
    literalInt | 
    literalString |
    postAnnotation |
    preAnnotation |
    symbol
  }

  def tokens: Parser[List[Token]] = {
    def parseAllInput[T](p: Parser[T]) = new Parser[T] {
      def dropWhile(in: Input, p: Char => Boolean): Input = {
        if (in.atEnd) in
        else if (p(in.first)) dropWhile(in.rest, p)
        else in
      }
      def apply(in: Input) = p(in) match {
        case s @ Success(out, in1) =>
          if (in1.atEnd) s
          else Failure("illegal character", dropWhile(in1, _ == ' '))
        case other => other
      }
    }
    val p = ignore ~> repsep(token, ignore) <~ ignore
    parseAllInput(p)
  }

  def unitParser[T](p: Parser[T]): Parser[Unit] = { p ^^ { _ => () } }

  override def skipWhitespace = false

  val internalError = failure("internal error"): Parser[Token]

  val newlines = unitParser(rep1(newline))

  val newlinesOpt = unitParser(rep(newline))

  val nothing = success(())

  val reservedWords = List(
    ("F32", Token.F32),
    ("F64", Token.F64),
    ("I16", Token.I16),
    ("I32", Token.I32),
    ("I64", Token.I64),
    ("I8", Token.I8),
    ("U16", Token.U16),
    ("U32", Token.U32),
    ("U64", Token.U64),
    ("U8", Token.U8),
    ("active", Token.ACTIVE),
    ("activity", Token.ACTIVITY),
    ("always", Token.ALWAYS),
    ("array", Token.ARRAY),
    ("assert", Token.ASSERT),
    ("async", Token.ASYNC),
    ("at", Token.AT),
    ("base", Token.BASE),
    ("block", Token.BLOCK),
    ("bool", Token.BOOL),
    ("change", Token.CHANGE),
    ("command", Token.COMMAND),
    ("component", Token.COMPONENT),
    ("connections", Token.CONNECTIONS),
    ("constant", Token.CONSTANT),
    ("default", Token.DEFAULT),
    ("diagnostic", Token.DIAGNOSTIC),
    ("drop", Token.DROP),
    ("enum", Token.ENUM),
    ("event", Token.EVENT),
    ("false", Token.FALSE),
    ("fatal", Token.FATAL),
    ("format", Token.FORMAT),
    ("get", Token.GET),
    ("guarded", Token.GUARDED),
    ("high", Token.HIGH),
    ("id", Token.ID),
    ("import", Token.IMPORT),
    ("include", Token.INCLUDE),
    ("init", Token.INIT),
    ("input", Token.INPUT),
    ("instance", Token.INSTANCE),
    ("internal", Token.INTERNAL),
    ("locate", Token.LOCATE),
    ("low", Token.LOW),
    ("module", Token.MODULE),
    ("on", Token.ON),
    ("opcode", Token.OPCODE),
    ("orange", Token.ORANGE),
    ("output", Token.OUTPUT),
    ("param", Token.PARAM),
    ("passive", Token.PASSIVE),
    ("pattern", Token.PATTERN),
    ("phase", Token.PHASE),
    ("port", Token.PORT),
    ("priority", Token.PRIORITY),
    ("private", Token.PRIVATE),
    ("queue", Token.QUEUE),
    ("queued", Token.QUEUED),
    ("recv", Token.RECV),
    ("red", Token.RED),
    ("ref", Token.REF),
    ("reg", Token.REG),
    ("resp", Token.RESP),
    ("save", Token.SAVE),
    ("serial", Token.SERIAL),
    ("set", Token.SET),
    ("size", Token.SIZE),
    ("stack", Token.STACK),
    ("string", Token.STRING),
    ("struct", Token.STRUCT),
    ("sync", Token.SYNC),
    ("telemetry", Token.TELEMETRY),
    ("text", Token.TEXT),
    ("throttle", Token.THROTTLE),
    ("time", Token.TIME),
    ("topology", Token.TOPOLOGY),
    ("true", Token.TRUE),
    ("type", Token.TYPE),
    ("unused", Token.UNUSED),
    ("update", Token.UPDATE),
    ("warning", Token.WARNING),
    ("yellow", Token.YELLOW),
  )

  val symbols = List(
    (newlinesOpt, ")", Token.RPAREN, nothing),
    (newlinesOpt, "]", Token.RBRACKET, nothing),
    (newlinesOpt, "}", Token.RBRACE, nothing),
    (nothing, "(", Token.LPAREN, newlinesOpt),
    (nothing, "*", Token.STAR, newlinesOpt),
    (nothing, "+", Token.PLUS, newlinesOpt),
    (nothing, ",", Token.COMMA, newlinesOpt),
    (nothing, "-", Token.MINUS, newlinesOpt),
    (nothing, "->", Token.RARROW, newlinesOpt),
    (nothing, ".", Token.DOT, nothing),
    (nothing, "/", Token.SLASH, newlinesOpt),
    (nothing, ":", Token.COLON, newlinesOpt),
    (nothing, ";", Token.SEMI, newlinesOpt),
    (nothing, "=", Token.EQUALS, newlinesOpt),
    (nothing, "[", Token.LBRACKET, newlinesOpt),
    (nothing, "{", Token.LBRACE, newlinesOpt),
  )

}
