package fpp.compiler.parser

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.Positional
import scala.util.parsing.input.{NoPosition, Position, Reader}

object Parser extends Parsers {

  class TokenReader(tokens: Seq[Token]) extends Reader[Token] {
    override def first: Token = tokens.head
    override def atEnd: Boolean = tokens.isEmpty
    override def pos: Position = tokens.headOption.map(_.pos).getOrElse(NoPosition)
    override def rest: Reader[Token] = new TokenReader(tokens.tail)
  }

  def componentKind: Parser[Ast.ComponentKind] = {
    accept("active", { case Token.ACTIVE => Ast.ComponentKind.Active })
    accept("passive", { case Token.PASSIVE => Ast.ComponentKind.Passive })
    accept("queued", { case Token.QUEUED => Ast.ComponentKind.Queued })
  }

  def componentMemberNode: Parser[Ast.ComponentMember.Node] = {
    node(defConstant) ^^ { case d => Ast.ComponentMember.DefConstant(d) }
    // TODO
  }

  def defAbsType: Parser[Ast.DefAbsType] = {
    (Token.TYPE ~> ident) ^^ { case id => Ast.DefAbsType(id) }
  }

  def defArray: Parser[Ast.DefArray] = {
    (Token.ARRAY ~> ident <~ Token.EQUALS) ~ 
    (Token.LBRACKET ~> exprNode <~ Token.RBRACKET) ~ node(typeName) ~ 
    opt(Token.DEFAULT ~> exprNode) ~
    opt(Token.FORMAT ~> node(literalString)) ^^ {
      case name ~ size ~ eltType ~ default ~ format => Ast.DefArray(name, size, eltType, default, format)
    }
  }

  def defComponent: Parser[Ast.DefComponent] = {
    def members = annotatedElementSequence(componentMemberNode, semi, Ast.ComponentMember(_))
    componentKind ~ (Token.COMPONENT ~> ident) ~ (Token.LBRACE ~> members <~ Token.RBRACE) ^^ {
      case kind ~ name ~ members => Ast.DefComponent(kind, name, members)
    }
  }

  def defComponentInstance: Parser[Ast.DefComponentInstance] = {
    (Token.INSTANCE ~> ident) ~ (Token.COLON ~> node(qualIdent)) ~ (Token.BASE ~ Token.ID ~> exprNode) ~
    opt(Token.QUEUE ~ Token.SIZE ~> exprNode) ~
    opt(Token.STACK ~ Token.SIZE ~> exprNode) ~
    opt(Token.PRIORITY ~> exprNode) ^^ {
      case name ~ typeName ~ baseId ~ queueSize ~ stackSize ~ priority => 
        Ast.DefComponentInstance(name, typeName, baseId, queueSize, stackSize, priority)
    }
  }

  def defConstant: Parser[Ast.DefConstant] = {
    (Token.CONSTANT ~> ident) ~ (Token.EQUALS ~> exprNode) ^^ {
      case id ~ e => Ast.DefConstant(id, e)
    }
  }

  def defEnum: Parser[Ast.DefEnum] = {
    def id(x: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = x
    def constants = annotatedElementSequence(node(defEnumConstant), comma, id)
    ident ~ opt(Token.COLON ~> node(typeName)) ~ (Token.LBRACE ~> constants <~ Token.RBRACE) ^^ {
      case name ~ typeName ~ constants => Ast.DefEnum(name, typeName, constants)
    }
  }

  def defEnumConstant: Parser[Ast.DefEnumConstant] = {
    ident ~ (Token.EQUALS ~> opt(exprNode)) ^^ { 
      case id ~ e => Ast.DefEnumConstant(id, e)
    }
  }

  def defModule: Parser[Ast.DefModule] = {
    def members = annotatedElementSequence(moduleMemberNode, semi, Ast.ModuleMember(_))
    (Token.MODULE ~> ident) ~ (Token.LBRACE ~> members <~ Token.RBRACE) ^^ {
      case name ~ members => Ast.DefModule(name, members)
    }
  }

  def defPort: Parser[Ast.DefPort] = {
    (Token.PORT ~> ident) ~ formalParamList ~ opt(Token.RARROW ~> node(typeName)) ^^ {
      case ident ~ formalParamList ~ returnType => Ast.DefPort(ident, formalParamList, returnType)
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
    def dotOperand = node {
      def arrayExpr = 
        Token.LBRACKET ~> elementSequence(exprNode, comma) <~ Token.RBRACKET ^^ { 
          case es => Ast.ExprArray(es)
        }
      def falseExpr = literalFalse ^^ { case _ => Ast.ExprLiteralBool(Ast.False) }
      def floatExpr = literalFloat ^^ { case s => Ast.ExprLiteralFloat(s) }
      def identExpr = ident ^^ { case id => Ast.ExprIdent(id) }
      def intExpr = literalInt ^^ { case li => Ast.ExprLiteralInt(li) }
      def parenExpr = Token.LPAREN ~> exprNode <~ Token.RPAREN ^^ { case e => Ast.ExprParen(e) }
      def stringExpr = literalString ^^ { case s => Ast.ExprLiteralString(s) }
      def structMember = ident ~ (Token.EQUALS ~> exprNode) ^^ {
        case id ~ e => Ast.StructMember(id, e)
      }
      def structExpr = 
        Token.LBRACE ~> elementSequence(structMember, comma) <~ Token.RBRACE ^^ {
          case es => Ast.ExprStruct(es)
        }
      def trueExpr = literalTrue ^^ { case _ => Ast.ExprLiteralBool(Ast.True) }
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
    def unaryMinusOperand = {
      def dotIds(e: AstNode[Ast.Expr], ids: List[String]): AstNode[Ast.Expr] = {
        def f(e: AstNode[Ast.Expr], id: String): AstNode[Ast.Expr] = {
          val dot = AstNode.create(Ast.ExprDot(e, id))
          val loc = Locations.get(e.getId)
          Locations.put(dot.getId, loc)
          dot
        }
        ids.foldLeft(e)(f)
      }
      dotOperand ~ rep(ident) ^^ { case e ~ ids => dotIds(e, ids) }
    }
    def mulDivOperand = unaryMinus | unaryMinusOperand
    def addSubOperand = mulDivOperand ~ rep((Token.STAR | Token.SLASH) ~ mulDivOperand) ^^ {
      case e ~ es => leftAssoc(e, es)
    }
    addSubOperand ~ rep((Token.PLUS | Token.MINUS) ~ addSubOperand) ^^ {
      case e ~ es => leftAssoc(e, es)
    }
  }

  def formalParam: Parser[Ast.FormalParam] = {
    def kind: Parser[Ast.FormalParam.Kind] = {
      opt(ref) ^^ { 
        case Some(_) => Ast.FormalParam.Ref
        case None => Ast.FormalParam.Value
      }
    }
    kind ~ ident ~ (Token.COLON ~> node(typeName)) ~ opt(Token.SIZE ~> exprNode) ^^ {
      case kind ~ id ~ tn ~ size => Ast.FormalParam(kind, id, tn, size)
    }
  }

  def formalParamList: Parser[Ast.FormalParamList] = {
    def id(x: Ast.Annotated[AstNode[Ast.FormalParam]]) = x
    def params = annotatedElementSequence(node(formalParam), comma, id)
    opt(Token.LPAREN ~> params <~ Token.RPAREN) ^^ {
      case Some(params) => params
      case None => Nil
    }
  }

  def moduleMemberNode: Parser[Ast.ModuleMember.Node] = {
    node(defAbsType) ^^ { case d => Ast.ModuleMember.DefAbsType(d) } |
    node(defArray) ^^ { case d => Ast.ModuleMember.DefArray(d) } |
    node(defComponent) ^^ { case d => Ast.ModuleMember.DefComponent(d) }
    node(defComponentInstance) ^^ { case d => Ast.ModuleMember.DefComponentInstance(d) }
    node(defConstant) ^^ { case d => Ast.ModuleMember.DefConstant(d) }
    node(defEnum) ^^ { case d => Ast.ModuleMember.DefEnum(d) }
    node(defModule) ^^ { case d => Ast.ModuleMember.DefModule(d) }
    node(defPort) ^^ { case d => Ast.ModuleMember.DefPort(d) }
    //node(defStruct) ^^ { case d => Ast.ModuleMember.DefStruct(d) }
    //node(defTopology) ^^ { case d => Ast.ModuleMember.DefTopology(d) }
    //node(specInclude) ^^ { case d => Ast.ModuleMember.SpecInclude(d) }
    //node(specLoc) ^^ { case d => Ast.ModuleMember.SpecLoc(d) }
  }

  def node[T](p: Parser[T]): Parser[AstNode[T]] = {
    final case class Positioned(t: T) extends Positional
    def positionedT: Parser[Positioned] = positioned {
      p ^^ { case x => Positioned(x) }
    }
    positionedT ^^ { 
      case pt @ Positioned(t) => {
        val n = AstNode.create(t)
        val loc = Location(ParserState.file, pt.pos)
        Locations.put(n.id, loc)
        n
      }
    }
  }

  def parseTokens[T](p: Parser[T], tokens: Seq[Token]): Result.Result[T] = {
    val reader = new TokenReader(tokens)
    p(reader) match {
      case NoSuccess(msg, next) => Left(SyntaxError(Location(ParserState.file, next.pos),msg))
      case Success(result, next) => Right(result)
    }
  }
 
  def parseString[T](p: Parser[T], s: String): Result.Result[T] = {
    for {
      tokens <- Lexer(File.StdIn, s)
      result <- parseTokens(p, tokens)
    } yield result
  }

  def qualIdent: Parser[List[Ast.Ident]] = repsep(ident, dot)

  def transUnit: Parser[Ast.TransUnit] = {
    annotatedElementSequence(tuMemberNode, semi, Ast.TUMember(_)) ^^ {
      case members => Ast.TransUnit(members)
    }
  }

  def tuMemberNode = moduleMemberNode

  def typeName: Parser[Ast.TypeName] = {
    def typeNameFloat =
      accept("F32", { case Token.F32 => Ast.TypeNameFloat(Ast.F32) }) |
      accept("F64", { case Token.F64 => Ast.TypeNameFloat(Ast.F64) })
    def typeNameInt =
      accept("I8", { case Token.I8 => Ast.TypeNameInt(Ast.I8) }) |
      accept("I16", { case Token.I16 => Ast.TypeNameInt(Ast.I16) }) |
      accept("I32", { case Token.I32 => Ast.TypeNameInt(Ast.I32) }) |
      accept("I64", { case Token.I64 => Ast.TypeNameInt(Ast.I64) }) |
      accept("U8", { case Token.U8 => Ast.TypeNameInt(Ast.U8) }) |
      accept("U16", { case Token.U16 => Ast.TypeNameInt(Ast.U16) }) |
      accept("U32", { case Token.U32 => Ast.TypeNameInt(Ast.U32) }) |
      accept("U64", { case Token.U64 => Ast.TypeNameInt(Ast.U64) })
    accept("bool", { case Token.BOOL => Ast.TypeNameBool }) |
    accept("string", { case Token.BOOL => Ast.TypeNameString }) |
    typeNameFloat |
    typeNameInt |
    node(qualIdent) ^^ { case qidn => Ast.TypeNameQualIdent(qidn) }
  }

  override type Elem = Token

  private def annotatedElementSequence[E, T](
    elt: Parser[E],
    punct: Parser[Unit],
    constructor: Ast.Annotated[E] => T
  ): Parser[List[T]] = {
    def terminator = punct | Token.EOL
    def punctTerminatedElt = rep(preAnnotation) ~ (elt <~ terminator) ~ rep(postAnnotation) ^^ {
      case al1 ~ elt ~ al2 => (al1, elt, al2)
    }
    def annotationTerminatedElt = rep(preAnnotation) ~ elt ~ rep1(postAnnotation) ^^ {
      case al1 ~ elt ~ al2 => (al1, elt, al2)
    }
    def terminatedElt = punctTerminatedElt | annotationTerminatedElt
    def unterminatedElt = rep(preAnnotation) ~ elt ^^ {
      case al ~ elt => (al, elt, Nil)
    }
    def elts = rep(terminatedElt) ~ opt(unterminatedElt) ^^ {
      case elts ~ Some(elt) => elts :+ elt
      case elts ~ None => elts
    }
    (rep(Token.EOL) ~> elts) ^^ { elts => elts.map(constructor) }
  }

  private def comma: Parser[Unit] = accept("comma", { case Token.COMMA => () })

  private def dot: Parser[Unit] = accept("dot", { case Token.DOT => () })

  private def elementSequence[E,S](elt: Parser[E], sep: Parser[S]): Parser[List[E]] =
    repsep(elt, sep | Token.EOL) <~ opt(sep)

  private def ident: Parser[Ast.Ident] =
    accept("identifier", { case Token.IDENTIFIER(s) => s })

  private def literalFalse: Parser[Unit] =
    accept("false", { case Token.FALSE => () })

  private def literalFloat: Parser[String] =
    accept("floating-point literal", { case Token.LITERAL_FLOAT(s) => s })

  private def literalInt: Parser[String] =
    accept("integer literal", { case Token.LITERAL_INT(s) => s })

  private def literalString: Parser[String] =
    accept("string literal", { case Token.LITERAL_STRING(s) => s })

  private def literalTrue: Parser[Unit] = accept("true", { case Token.TRUE => () })

  private def postAnnotation: Parser[String] =
    accept("post annotation", { case Token.POST_ANNOTATION(s) => s })

  private def preAnnotation: Parser[String] =
    accept("pre annotation", { case Token.PRE_ANNOTATION(s) => s })

  private def ref: Parser[Unit] =
    accept("ref", { case Token.REF => () })

  private def semi: Parser[Unit] =
    accept("semicolon", { case Token.SEMI => () })

}
