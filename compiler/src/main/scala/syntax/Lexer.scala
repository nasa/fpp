package fpp.compiler.syntax

import fpp.compiler.error._
import scala.util.parsing.combinator.RegexParsers

object Lexer extends RegexParsers {

  def apply(code: String): Either[LexerError, List[Token]] = {
    parse(tokens, code) match {
      case NoSuccess(msg, next) => Left(LexerError(Location(next.pos),msg))
      case Success(result, next) => Right(result)
    }
  }

  def eol: Parser[Token] = {
    newlines ^^ { _ => Token.EOL }
  }

  def identifier: Parser[Token] = positioned {
    "[A-Za-z_][A-Za-z_0-9]*".r ^^ { Token.IDENTIFIER(_) }
  }

  def ignore: Parser[Unit] = {
    def comment: Parser[Unit] = unitParser("#[^\n]*".r ~ newlinesOpt)
    def spaces: Parser[Unit] = unitParser(" +".r)
    def escapedNewline: Parser[Unit] = unitParser("\\" ~ newline)
    unitParser(rep(comment | spaces | escapedNewline))
  }

  def keyword: Parser[Token] = positioned {
    type ST = Tuple2[String, Token]
    def f(st: ST): Parser[Token] = {
      val (s, t) = st
      accept((s ++ raw"\b").toList) ^^ { _ => t }
    }
    (keywords map f).foldRight (internalError) ((x, y) => y | x)
  }

  def literalFloat: Parser[Token] = positioned {
    (
      "[0-9]+[Ee][+-]?[0-9]+".r | 
      raw"[0-9]*\.[0-9]+([Ee][+-]?[0-9]+)?".r |
      raw"[0-9]+\.[0-9]*([Ee][+-]?[0-9]+)?".r
    ) ^^ { Token.LITERAL_FLOAT(_) }
  }

  def literalInt: Parser[Token] = positioned {
    ("[0-9]+".r | "0[Xx][0-9a-fA-F]+".r) ^^ { Token.LITERAL_INT(_) }
  }

  def literalString: Parser[Token] = positioned {
    "\"([^\\\\\"]+(\\\\(\")?)?)*\"".r ^^ { 
      case s => {
        val s1 = "\\\\\"".r.replaceAllIn(s, "\"")
        val s2 = s1.drop(1).dropRight(1)
        Token.LITERAL_STRING(s2)
      }
    }
  }

  def newline: Parser[Unit] = unitParser(" *\r?\n *".r)

  def preAnnotation: Parser[Token] = {
    "@[^\n]*".r <~ newlinesOpt ^^ {
      case s => {
        val s1 = s.stripPrefix("@").trim
        Token.PRE_ANNOTATION(s1)
      }
    }
  }

  def postAnnotation: Parser[Token] = {
    "@<[^\n]*".r <~ newlinesOpt ^^ {
      case s => {
        val s1 = s.stripPrefix("@<").trim
        Token.POST_ANNOTATION(s1)
      }
    }
  }

  def symbol: Parser[Token] = positioned {
    type PSTP = Tuple4[Parser[Unit], String, Token, Parser[Unit]]
    def f(pstp: PSTP): Parser[Token] = {
      val (p1, s, t, p2) = pstp
      p1 ~> accept(s.toList) <~ p2 ^^ { _ => t }
    }
    (symbols map f).foldRight (internalError) ((x, y) => y | x)
  }

  def token: Parser[Token] = positioned {
    keyword |
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

  val keywords = List(
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
    ("array", Token.ARRAY),
    ("assert", Token.ASSERT),
    ("async", Token.ASYNC),
    ("at", Token.AT),
    ("block", Token.BLOCK),
    ("bool", Token.BOOL),
    ("command", Token.COMMAND),
    ("component", Token.COMPONENT),
    ("constant", Token.CONSTANT),
    ("default", Token.DEFAULT),
    ("drop", Token.DROP),
    ("enum", Token.ENUM),
    ("event", Token.EVENT),
    ("false", Token.FALSE),
    ("get", Token.GET),
    ("guarded", Token.GUARDED),
    ("input", Token.INPUT),
    ("instance", Token.INSTANCE),
    ("internal", Token.INTERNAL),
    ("locate", Token.LOCATE),
    ("module", Token.MODULE),
    ("output", Token.OUTPUT),
    ("param", Token.PARAM),
    ("passive", Token.PASSIVE),
    ("port", Token.PORT),
    ("priority", Token.PRIORITY),
    ("queued", Token.QUEUED),
    ("ref", Token.REF),
    ("reg", Token.REG),
    ("resp", Token.RESP),
    ("set", Token.SET),
    ("size", Token.SIZE),
    ("string", Token.STRING),
    ("struct", Token.STRUCT),
    ("sync", Token.SYNC),
    ("telemetry", Token.TELEMETRY),
    ("time", Token.TIME),
    ("topology", Token.TOPOLOGY),
    ("true", Token.TRUE),
    ("type", Token.TYPE),
  )

  val newlines = unitParser(rep1(newline))

  val newlinesOpt = unitParser(rep(newline))

  val nothing = success(())

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
    (nothing, "}", Token.LBRACE, newlinesOpt),
  )

}
