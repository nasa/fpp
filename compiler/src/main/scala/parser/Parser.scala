package fpp.compiler.parser

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.Positional
import scala.util.parsing.input.{NoPosition, Position, Reader}

object Parser extends Parsers {

  override type Elem = Token

  class TokenReader(tokens: Seq[Token]) extends Reader[Token] {
    override def first: Token = tokens.head
    override def atEnd: Boolean = tokens.isEmpty
    override def pos: Position = tokens.headOption.map(_.pos).getOrElse(NoPosition)
    override def rest: Reader[Token] = new TokenReader(tokens.tail)
  }

  var file = File.StdIn

  private def unitParser[T](p: Parser[T]): Parser[Unit] = { p ^^ { _ => () } }

  private def nothing: Parser[Unit] = success(())

  private def elementSequence[E,S](elt: Parser[E], sep: Parser[S]): Parser[List[E]] = {
    repsep(elt, sep | Token.EOL) <~ (unitParser(sep) | nothing)
  }

  private def identifier: Parser[String] = {
    accept("identifier", { case Token.IDENTIFIER(s) => s })
  }

  private def literalFalse: Parser[Unit] = {
    accept("false", { case Token.FALSE => () })
  }

  private def literalFloat: Parser[String] = {
    accept("floating-point literal", { case Token.LITERAL_FLOAT(s) => s })
  }

  private def literalInt: Parser[String] = {
    accept("integer literal", { case Token.LITERAL_INT(s) => s })
  }

  private def literalString: Parser[String] = {
    accept("string literal", { case Token.LITERAL_STRING(s) => s })
  }

  private def literalTrue: Parser[Unit] = {
    accept("true", { case Token.TRUE => () })
  }

  private def comma: Parser[Unit] = {
    accept("comma", { case Token.COMMA => () })
  }

  def node[T](p: Parser[T]): Parser[AstNode[T]] = {
    final case class PositionedT(t: T) extends Positional
    def positionedT: Parser[PositionedT] = positioned {
      p ^^ { case x => PositionedT(x) }
    }
    positionedT ^^ { 
      case pt @ PositionedT(t) => {
        val n = AstNode.create(t)
        val loc = Location(file, pt.pos)
        Locations.put(n.id, loc)
        n
      }
    }
  }

  def exprNode: Parser[AstNode[Ast.Expr]] = {
    def leftAssoc(e: AstNode[Ast.Expr], es: List[Token ~ AstNode[Ast.Expr]]): AstNode[Ast.Expr] = {
      def f(e1: AstNode[Ast.Expr], op_e2: Token ~ AstNode[Ast.Expr]): AstNode[Ast.Expr] = {
        val op ~ e2 = op_e2
        val binop =
          op match {
            case Token.MINUS => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Sub, e2))
            case Token.PLUS => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Add, e2))
            case Token.SLASH => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Div, e2))
            case Token.STAR => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Mul, e2))
            case _ => throw new InternalError(s"invalid binary operator ${op}")
        }
        val loc = Locations.get(e.getId)
        Locations.put(binop.getId, loc)
        binop
      }
      es.foldLeft(e)(f)
    }
    def dotSelectors(e: AstNode[Ast.Expr], ids: List[String]): AstNode[Ast.Expr] = {
      def f(e: AstNode[Ast.Expr], id: String): AstNode[Ast.Expr] = {
        val dot = AstNode.create(Ast.ExprDot(e, id))
        val loc = Locations.get(e.getId)
        Locations.put(dot.getId, loc)
        dot
      }
      ids.foldLeft(e)(f)
    }
    def arrayExpr = 
      Token.LBRACKET ~> elementSequence(exprNode, comma) <~ Token.RBRACKET ^^ { 
        case es => Ast.ExprArray(es)
      }
    def falseExpr = literalFalse ^^ { case _ => Ast.ExprLiteralBool(Ast.False) }
    def floatExpr = literalFloat ^^ { case s => Ast.ExprLiteralFloat(s) }
    def identExpr = identifier ^^ { case id => Ast.ExprIdent(id) }
    def intExpr = literalInt ^^ { case li => Ast.ExprLiteralInt(li) }
    def parenExpr = Token.LPAREN ~> exprNode <~ Token.RPAREN ^^ { case e => Ast.ExprParen(e) }
    def stringExpr = literalString ^^ { case s => Ast.ExprLiteralString(s) }
    def structMember = identifier ~ Token.EQUALS ~ exprNode ^^ {
      case id ~ _ ~ e => Ast.StructMember(id, e)
    }
    def structExpr = 
      Token.LBRACE ~> elementSequence(structMember, comma) <~ Token.RBRACE ^^ {
        case es => Ast.ExprStruct(es)
      }
    def trueExpr = literalTrue ^^ { case _ => Ast.ExprLiteralBool(Ast.True) }
    def dotOperand = node {
      arrayExpr |
      falseExpr |
      floatExpr |
      identExpr |
      intExpr |
      parenExpr |
      stringExpr |
      structExpr |
      trueExpr
    }
    def unaryMinus = node { 
      Token.MINUS ~> unaryMinusOperand ^^ { case e => Ast.ExprUnop(Ast.Unop.Minus, e) }
    }
    def unaryMinusOperand = dotOperand ~ rep(identifier) ^^ {
      case e ~ ids => dotSelectors(e, ids)
    }
    def mulDivOperand = unaryMinus | unaryMinusOperand
    def addSubOperand = mulDivOperand ~ rep((Token.STAR | Token.SLASH) ~ mulDivOperand) ^^ {
      case e ~ es => leftAssoc(e, es)
    }
    addSubOperand ~ rep((Token.PLUS | Token.MINUS) ~ addSubOperand) ^^ {
      case e ~ es => leftAssoc(e, es)
    }
  }

  def parseTokens[T](p: Parser[T], tokens: Seq[Token]): Result.Result[T] = {
    val reader = new TokenReader(tokens)
    p(reader) match {
      case NoSuccess(msg, next) => Left(SyntaxError(Location(file, next.pos),msg))
      case Success(result, next) => Right(result)
    }
  }
 
  def parseString[T](p: Parser[T], s: String): Result.Result[T] = {
    for {
      tokens <- Lexer(File.StdIn, s)
      result <- parseTokens(p, tokens)
    } yield result
  }

}
