package fpp.compiler.syntax

import fpp.compiler.util.{File, Location, Result, CharBuffer}
import fpp.compiler.util.Chars.*

import scala.collection.mutable
import scala.annotation.switch
import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import fpp.compiler.syntax.TokenId
import fpp.compiler.syntax.TokenId.*
import fpp.compiler.util.SemanticError.InvalidToken

import scala.util.parsing.input.{Position, Reader}

object Lexer {

  private val keywords: Map[String, TokenId] = Map(
    ("F32", F32),
    ("F64", F64),
    ("I16", I16),
    ("I32", I32),
    ("I64", I64),
    ("I8", I8),
    ("U16", U16),
    ("U32", U32),
    ("U64", U64),
    ("U8", U8),
    ("action", ACTION),
    ("active", ACTIVE),
    ("activity", ACTIVITY),
    ("always", ALWAYS),
    ("array", ARRAY),
    ("assert", ASSERT),
    ("async", ASYNC),
    ("at", AT),
    ("base", BASE),
    ("block", BLOCK),
    ("bool", BOOL),
    ("change", CHANGE),
    ("choice", CHOICE),
    ("command", COMMAND),
    ("component", COMPONENT),
    ("connections", CONNECTIONS),
    ("constant", CONSTANT),
    ("container", CONTAINER),
    ("cpu", CPU),
    ("default", DEFAULT),
    ("diagnostic", DIAGNOSTIC),
    ("do", DO),
    ("drop", DROP),
    ("else", ELSE),
    ("enter", ENTER),
    ("entry", ENTRY),
    ("enum", ENUM),
    ("event", EVENT),
    ("every", EVERY),
    ("exit", EXIT),
    ("external", EXTERNAL),
    ("false", FALSE),
    ("fatal", FATAL),
    ("format", FORMAT),
    ("get", GET),
    ("group", GROUP),
    ("guard", GUARD),
    ("guarded", GUARDED),
    ("health", HEALTH),
    ("high", HIGH),
    ("hook", HOOK),
    ("id", ID),
    ("if", IF),
    ("import", IMPORT),
    ("include", INCLUDE),
    ("initial", INITIAL),
    ("input", INPUT),
    ("instance", INSTANCE),
    ("interface", INTERFACE),
    ("internal", INTERNAL),
    ("locate", LOCATE),
    ("low", LOW),
    ("machine", MACHINE),
    ("match", MATCH),
    ("module", MODULE),
    ("omit", OMIT),
    ("on", ON),
    ("opcode", OPCODE),
    ("orange", ORANGE),
    ("output", OUTPUT),
    ("packet", PACKET),
    ("packets", PACKETS),
    ("param", PARAM),
    ("passive", PASSIVE),
    ("phase", PHASE),
    ("port", PORT),
    ("priority", PRIORITY),
    ("private", PRIVATE),
    ("product", PRODUCT),
    ("queue", QUEUE),
    ("queued", QUEUED),
    ("record", RECORD),
    ("recv", RECV),
    ("red", RED),
    ("ref", REF),
    ("reg", REG),
    ("request", REQUEST),
    ("resp", RESP),
    ("save", SAVE),
    ("send", SEND),
    ("serial", SERIAL),
    ("set", SET),
    ("severity", SEVERITY),
    ("signal", SIGNAL),
    ("size", SIZE),
    ("stack", STACK),
    ("state", STATE),
    ("string", STRING),
    ("struct", STRUCT),
    ("sync", SYNC),
    ("telemetry", TELEMETRY),
    ("text", TEXT),
    ("throttle", THROTTLE),
    ("time", TIME),
    ("topology", TOPOLOGY),
    ("true", TRUE),
    ("type", TYPE),
    ("unmatched", UNMATCHED),
    ("update", UPDATE),
    ("warning", WARNING),
    ("with", WITH),
    ("yellow", YELLOW)
  )

  val reservedWordSet = Set.from(keywords.keys)

  trait TokenData {

    /** the next token */
    var token: TokenId

    /** the line number of the first character of the current token */
    var line: Int = 0

    /** the offset of the first character of the current token */
    var offset: Int = 0

    /** the offset of the character following the token preceding this one */
    var lastOffset: Int = 0

    /** the offset of the newline immediately preceding the token, or -1 if
     * token is not preceded by a newline.
     */
    var lineOffset: Int = 0

    /** the next identifier is forced to not be a keyword */
    var reservedWord = false

    /** the string value of a literal */
    var strVal: String = ""

    /** the base of a number */
    var base: Int = 0

    def copyFrom(td: TokenData): Unit = {
      this.token = td.token
      this.offset = td.offset
      this.lastOffset = td.lastOffset
      this.lineOffset = td.lineOffset
      this.strVal = td.strVal
      this.base = td.base
    }
  }

  class TokenPosition(
                       val line: Int,
                       val column: Int,
                       lineStr: String
                     ) extends Position {
    protected def lineContents: String = lineStr
  }

  class Scanner(
                 file: File,
                 content: Array[Char],
                 includingLoc: Option[Location] = None
               )(using ctx: Context)
    extends CharArrayReader
      with TokenData
      with Iterator[Token] {

    /** File to scan */
    val buf: Array[Char] = content

    /** Current token to return */
    var token: TokenId = EOF

    val lines = String(content).split("\n", -1)

    def list(): Result.Result[List[Token]] = {
      val out = mutable.ArrayBuffer[Token]()
      var tok = next()
      while (tok != Token.EOF) {
        out.append(tok)
        tok = next()
      }

      ctx.result() match {
        case Left(err) => Left(err)
        case Right(_) => Right(out.toList)
      }
    }

    def hasNext: Boolean = !isAtEnd

    def next(): Token = {
      if token == EOF then nextChar()
      fetchToken()

      val out = token match {
        case EOF => return Token.EOF
        case ACTION => Token.ACTION()
        case ACTIVE => Token.ACTIVE()
        case ACTIVITY => Token.ACTIVITY()
        case ALWAYS => Token.ALWAYS()
        case ARRAY => Token.ARRAY()
        case ASSERT => Token.ASSERT()
        case ASYNC => Token.ASYNC()
        case AT => Token.AT()
        case BASE => Token.BASE()
        case BLOCK => Token.BLOCK()
        case BOOL => Token.BOOL()
        case CHANGE => Token.CHANGE()
        case COLON => Token.COLON()
        case COMMA => Token.COMMA()
        case COMMAND => Token.COMMAND()
        case COMPONENT => Token.COMPONENT()
        case CONNECTIONS => Token.CONNECTIONS()
        case CONSTANT => Token.CONSTANT()
        case CONTAINER => Token.CONTAINER()
        case CPU => Token.CPU()
        case DEFAULT => Token.DEFAULT()
        case DIAGNOSTIC => Token.DIAGNOSTIC()
        case DO => Token.DO()
        case DOT => Token.DOT()
        case DROP => Token.DROP()
        case ELSE => Token.ELSE()
        case ENTER => Token.ENTER()
        case ENTRY => Token.ENTRY()
        case ENUM => Token.ENUM()
        case EOL => Token.EOL()
        case EQUALS => Token.EQUALS()
        case EVENT => Token.EVENT()
        case EVERY => Token.EVERY()
        case EXIT => Token.EXIT()
        case EXTERNAL => Token.EXTERNAL()
        case F32 => Token.F32()
        case F64 => Token.F64()
        case FALSE => Token.FALSE()
        case FATAL => Token.FATAL()
        case FORMAT => Token.FORMAT()
        case GET => Token.GET()
        case GROUP => Token.GROUP()
        case GUARD => Token.GUARD()
        case GUARDED => Token.GUARDED()
        case HEALTH => Token.HEALTH()
        case HIGH => Token.HIGH()
        case HOOK => Token.HOOK()
        case I16 => Token.I16()
        case I32 => Token.I32()
        case I64 => Token.I64()
        case I8 => Token.I8()
        case ID => Token.ID()
        case IDENTIFIER => Token.IDENTIFIER(strVal)
        case IF => Token.IF()
        case IMPORT => Token.IMPORT()
        case INCLUDE => Token.INCLUDE()
        case INITIAL => Token.INITIAL()
        case INPUT => Token.INPUT()
        case INSTANCE => Token.INSTANCE()
        case INTERFACE => Token.INTERFACE()
        case INTERNAL => Token.INTERNAL()
        case CHOICE => Token.CHOICE()
        case LBRACE => Token.LBRACE()
        case LBRACKET => Token.LBRACKET()
        case LITERAL_FLOAT => Token.LITERAL_FLOAT(strVal)
        case LITERAL_INT => Token.LITERAL_INT(strVal)
        case LITERAL_STRING => Token.LITERAL_STRING(strVal)
        case LOCATE => Token.LOCATE()
        case LOW => Token.LOW()
        case LPAREN => Token.LPAREN()
        case MACHINE => Token.MACHINE()
        case MATCH => Token.MATCH()
        case MINUS => Token.MINUS()
        case MODULE => Token.MODULE()
        case OMIT => Token.OMIT()
        case ON => Token.ON()
        case OPCODE => Token.OPCODE()
        case ORANGE => Token.ORANGE()
        case OUTPUT => Token.OUTPUT()
        case PACKET => Token.PACKET()
        case PACKETS => Token.PACKETS()
        case PARAM => Token.PARAM()
        case PASSIVE => Token.PASSIVE()
        case PHASE => Token.PHASE()
        case PLUS => Token.PLUS()
        case PORT => Token.PORT()
        case POST_ANNOTATION => Token.POST_ANNOTATION(strVal)
        case PRE_ANNOTATION => Token.PRE_ANNOTATION(strVal)
        case PRIORITY => Token.PRIORITY()
        case PRIVATE => Token.PRIVATE()
        case PRODUCT => Token.PRODUCT()
        case QUEUE => Token.QUEUE()
        case QUEUED => Token.QUEUED()
        case RARROW => Token.RARROW()
        case RBRACE => Token.RBRACE()
        case RBRACKET => Token.RBRACKET()
        case RECORD => Token.RECORD()
        case RECV => Token.RECV()
        case RED => Token.RED()
        case REF => Token.REF()
        case REG => Token.REG()
        case REQUEST => Token.REQUEST()
        case RESP => Token.RESP()
        case RPAREN => Token.RPAREN()
        case SAVE => Token.SAVE()
        case SEMI => Token.SEMI()
        case SEND => Token.SEND()
        case SERIAL => Token.SERIAL()
        case SET => Token.SET()
        case SEVERITY => Token.SEVERITY()
        case SIGNAL => Token.SIGNAL()
        case SIZE => Token.SIZE()
        case SLASH => Token.SLASH()
        case STACK => Token.STACK()
        case STAR => Token.STAR()
        case STATE => Token.STATE()
        case STRING => Token.STRING()
        case STRUCT => Token.STRUCT()
        case SYNC => Token.SYNC()
        case TELEMETRY => Token.TELEMETRY()
        case TEXT => Token.TEXT()
        case THROTTLE => Token.THROTTLE()
        case TIME => Token.TIME()
        case TOPOLOGY => Token.TOPOLOGY()
        case TRUE => Token.TRUE()
        case TYPE => Token.TYPE()
        case U16 => Token.U16()
        case U32 => Token.U32()
        case U64 => Token.U64()
        case U8 => Token.U8()
        case UNMATCHED => Token.UNMATCHED()
        case UPDATE => Token.UPDATE()
        case WARNING => Token.WARNING()
        case WITH => Token.WITH()
        case YELLOW => Token.YELLOW()
      }

      out.setPos(pos())
    }

    def pos() = {
      new TokenPosition(
        line + 1,
        offset - lineOffset + 1,
        lines(Math.max(0, Math.min(line, lines.length - 1)))
      )
    }

    /** Generate an error at the current position */
    def error(msg: String): Unit = {
      ctx.report(InvalidToken(Location(file, pos(), includingLoc), msg))
    }

    // Setting token data ----------------------------------------------------

    private def initialCharBufferSize = 1024

    /** A character buffer for literals */
    private val litBuf = CharBuffer(initialCharBufferSize)

    /** append Unicode character to "litBuf" buffer
     */
    private def putChar(c: Char): Unit = litBuf.append(c)

    /** Finish up construction of identifier */
    private def finishIdent(): Unit = {
      setStrVal()
      if reservedWord then token = IDENTIFIER
      else {
        token = keywords.get(strVal) match {
          case Some(key) => key
          case None => IDENTIFIER
        }
      }
    }

    /** Clear buffer and set string */
    private def setStrVal(): Unit = {
      strVal = litBuf.toString
      litBuf.clear()
    }

    @tailrec
    private final def fetchToken(): Unit = {
      offset = charOffset - 1
      lineOffset = if (lastOffset < lineStartOffset) lineStartOffset else 0
      reservedWord = false
      line = lineNo
      (ch: @switch) match {
        case SU => token = EOF
        // Skip whitespace
        case ' ' | FF =>
          nextChar()
          fetchToken()
        case '$' =>
          val lch = lookaheadChar()
          // Reserved words (identifier that overlaps a keyword)
          (lch: @switch) match {
            case 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' |
                 'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' |
                 'W' | 'X' | 'Y' | 'Z' | '_' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f' |
                 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' |
                 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' =>
              nextChar()
              reservedWord = true
              fetchIdentRest()
            case _ =>
              error("invalid usage of '$', expected identifier")
              nextChar()
              fetchToken()
          }
        // Identifier or keyword
        case 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' |
             'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' |
             'W' | 'X' | 'Y' | 'Z' | '_' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f' |
             'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' |
             'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' =>
          putChar(ch)
          nextChar()
          fetchIdentRest()
        // Numeric literal
        case '0' =>
          nextChar()
          ch match {
            case 'x' | 'X' =>
              base = 16
              putChar('0')
              putChar('x')
              nextChar()
            case _ => base = 10; putChar('0')
          }
          if (base != 10 && digit2int(ch, base) < 0)
            error("invalid literal number")

          fetchNumber()
        // More numeric literals
        case '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' =>
          base = 10
          fetchNumber()

        // Escaped newline
        case '\\' =>
          nextChar()
          // Absorb spaces until newline
          while (ch == ' ') {
            nextChar()
          }
          if (ch == LF) {
            nextChar()
            fetchToken()
          } else {
            offset = charOffset - 1
            error("expected line continuation")
            while (ch != LF) {
              nextChar()
            }

            nextChar()
            fetchToken()
          }
        // String Literals
        case '\"' =>
          nextChar()
          if (ch == '\"') {
            nextChar()
            if (ch == '\"') {
              nextChar()

              // Skip the initial newline if it exists
              if ch == LF then nextChar()

              val indent = fetchStringLitMultiIndent()
              fetchStringLitMulti(indent, indent)
            } else {
              strVal = ""
              token = LITERAL_STRING
            }
          } else fetchStringLitSingle()
        // Fractional floating
        case '.' =>
          nextChar()
          // Check if this is actually part of a float
          if ('0' <= ch && ch <= '9') {
            putChar('.')
            fetchFraction()
            setStrVal()
          } else token = DOT
        // Newline (or line comment)
        case LF | '#' =>
          eatNewlines()

          // Check the next token
          // Some tokens eat newlines and others don't
          (ch: @switch) match {
            case ')' | ']' | '}' =>
              // Fetch the next token to avoid this newline
              fetchToken()
            case _ => token = EOL
          }
        case '@' =>
          nextChar()
          if (ch == '<') {
            token = POST_ANNOTATION
            nextChar()
          } else token = PRE_ANNOTATION
          fetchAnnotationRest()
          eatNewlines()
        // Token Symbols
        case '*' =>
          nextChar()
          token = STAR
          eatNewlines()
        case '-' =>
          nextChar()
          if (ch == '>') {
            token = RARROW
            nextChar()
          } else token = MINUS
          eatNewlines()
        case '+' =>
          nextChar()
          token = PLUS
          eatNewlines()
        case '/' =>
          nextChar()
          token = SLASH
          eatNewlines()
        case '=' =>
          nextChar()
          token = EQUALS
          eatNewlines()
        case ';' =>
          nextChar()
          token = SEMI
          eatNewlines()
        case ':' =>
          nextChar()
          token = COLON
          eatNewlines()
        case ',' =>
          nextChar()
          token = COMMA
          eatNewlines()
        case '(' =>
          nextChar()
          token = LPAREN
          eatNewlines()
        case '{' =>
          nextChar()
          token = LBRACE
          eatNewlines()
        case ')' =>
          nextChar()
          token = RPAREN
        case '}' =>
          nextChar()
          token = RBRACE
        case '[' =>
          nextChar()
          token = LBRACKET
          eatNewlines()
        case ']' =>
          nextChar()
          token = RBRACKET
        case _ =>
          val errMsg = {
            // Print out the unicode value, in case the console can't
            // display it as a character
            val prefix =
              s"unicode value ${ch.toInt}, hex 0x${ch.toInt.toHexString}"
            // Generate a special error message for tab characters
            // These are especially tricky and also somewhat common
            if (ch == '\t')
              List(
                prefix,
                "note: embedded tab characters are not allowed in FPP source files",
                "try configuring your editor to convert tabs to spaces"
              ).mkString("\n")
            else prefix
          }

          error(errMsg)
          nextChar()
          fetchToken()
      }
    }

    @tailrec
    private final def eatLineComment(): Unit = (ch: @switch) match {
      case LF | SU =>
      case _ =>
        nextChar()
        eatLineComment()
    }

    /**
     * Read through all whitespace, comments, and newlines until another token is reached
     * This does not update any state of the current token
     */
    @tailrec
    private final def eatNewlines(): Unit = {
      (ch: @switch) match {
        // Skip whitespace
        case LF | ' ' =>
          nextChar()
          eatNewlines()
        // Skip comments
        case '#' =>
          nextChar()
          eatLineComment()
          eatNewlines()
        case _ =>
      }
    }

    @tailrec
    private def fetchIdentRest(): Unit = (ch: @switch) match {
      // Valid identifier characters
      case 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' |
           'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' |
           'W' | 'X' | 'Y' | 'Z' | '_' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f' |
           'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' |
           'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' | '0' | '1' |
           '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' =>
        putChar(ch)
        nextChar()
        fetchIdentRest()
      case _ => finishIdent()
    }

    @tailrec
    private def fetchAnnotationRest(): Unit = (ch: @switch) match {
      case LF | SU =>
        setStrVal()
        nextChar()
        strVal = strVal.trim()
      case _ =>
        putChar(ch)
        nextChar()
        fetchAnnotationRest()
    }

    private inline def putStringEscapeChar(): Unit = {
      (ch: @switch) match {
        case '\\' => putChar('\\')
        case '\"' => putChar('\"')
        // TODO(tumbar) We could put in more string escape sequences
        case _ => putChar(ch)
      }

      nextChar()
    }

    /** Compute the initial space indent */
    @tailrec
    private def fetchStringLitMultiIndent(indent: Int = 0): Int = {
      (ch: @switch) match {
        case ' ' =>
          nextChar()
          fetchStringLitMultiIndent(indent + 1)
        case _ => indent
      }
    }

    @tailrec
    private def fetchStringLitMulti(fullIndent: Int, skip: Int): Unit = {
      (ch: @switch) match {
        case ' ' =>
          if skip <= 0 then putChar(' ')
          nextChar()
          fetchStringLitMulti(fullIndent, skip - 1)
        case LF | SU =>
          putChar(LF)
          nextChar()
          fetchStringLitMulti(fullIndent, fullIndent)
        case '\"' =>
          nextChar()
          if (ch == '\"') {
            nextChar()
            if (ch == '\"') {
              setStrVal()
              nextChar()
              token = LITERAL_STRING
              return
            } else {
              putChar('\"')
              putChar('\"')
            }
          } else putChar('\"')

          fetchStringLitMulti(fullIndent, 0)
        case '\\' =>
          nextChar()
          putStringEscapeChar()
          fetchStringLitMulti(fullIndent, 0)
        case _ =>
          putChar(ch)
          nextChar()
          fetchStringLitMulti(fullIndent, 0)
      }
    }

    @tailrec
    private def fetchStringLitSingle(): Unit = (ch: @switch) match {
      case LF | SU =>
        nextChar()
        setStrVal()
        error("missing string double quote closure")
      case '\\' =>
        nextChar()
        putStringEscapeChar()
        fetchStringLitSingle()
      case '\"' =>
        nextChar()
        setStrVal()
        token = LITERAL_STRING
      case _ =>
        putChar(ch)
        nextChar()
        fetchStringLitSingle()
    }

    /** Read a number into strVal and set base */
    private def fetchNumber(): Unit = {
      while (digit2int(ch, base) >= 0) {
        putChar(ch)
        nextChar()
      }

      token = LITERAL_INT
      if (base == 10 && ch == '.') {
        putChar('.')
        nextChar()
        fetchFraction()
      } else
        (ch: @switch) match {
          case 'e' | 'E' =>
            if (base == 10) fetchFraction()
          case _ =>
        }

      setStrVal()
    }

    /** read fractional part and exponent of floating point number if one is
     * present.
     */
    private def fetchFraction(): Unit = {
      token = LITERAL_FLOAT
      while ('0' <= ch && ch <= '9') {
        putChar(ch)
        nextChar()
      }

      if (ch == 'e' || ch == 'E') {
        val lookahead = lookaheadReader()
        lookahead.nextChar()
        if (lookahead.ch == '+' || lookahead.ch == '-')
          lookahead.nextChar()
        if ('0' <= lookahead.ch && lookahead.ch <= '9') {
          putChar(ch)
          nextChar()
          if (ch == '+' || ch == '-') {
            putChar(ch)
            nextChar()
          }
          while ('0' <= ch && ch <= '9') {
            putChar(ch)
            nextChar()
          }
        }
      }

      checkNoLetter()
    }

    private def checkNoLetter(): Unit = {
      if (isIdentifierPart(ch) && ch >= ' ')
        error("Invalid literal number")
    }
  }
}
