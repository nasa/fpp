package fpp.compiler.syntax

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
    node(defArray) ^^ { case n => Ast.ComponentMember.DefArray(n) } |
    node(defConstant) ^^ { case n => Ast.ComponentMember.DefConstant(n) } |
    node(defEnum) ^^ { case n => Ast.ComponentMember.DefEnum(n) }
    node(defStruct) ^^ { case n => Ast.ComponentMember.DefStruct(n) }
    node(specCommand) ^^ { case n => Ast.ComponentMember.SpecCommand(n) }
    node(specEvent) ^^ { case n => Ast.ComponentMember.SpecEvent(n) }
    node(specInclude) ^^ { case n => Ast.ComponentMember.SpecInclude(n) }
    node(specInternalPort) ^^ { case n => Ast.ComponentMember.SpecInternalPort(n) }
    node(specParam) ^^ { case n => Ast.ComponentMember.SpecParam(n) }
    node(specPortInstance) ^^ { case n => Ast.ComponentMember.SpecPortInstance(n) }
    node(specTlmChannel) ^^ { case n => Ast.ComponentMember.SpecTlmChannel(n) }
  }

  def connection: Parser[Ast.SpecConnectionGraph.Connection] = {
    def connectionPort = node(qualIdent) ~ (Token.DOT ~> ident) ~ opt(index)
    connectionPort ~ (Token.RARROW ~> connectionPort) ^^ {
      case (qid1 ~ id1 ~ e1) ~ (qid2 ~ id2 ~ e2) => {
        Ast.SpecConnectionGraph.Connection(qid1, id1, e1, qid2, id2, e2)
      }
    }
  }

  def defAbsType: Parser[Ast.DefAbsType] = {
    (Token.TYPE ~> ident) ^^ { case id => Ast.DefAbsType(id) }
  }

  def defArray: Parser[Ast.DefArray] = {
    (Token.ARRAY ~> ident <~ Token.EQUALS) ~ 
    index ~ node(typeName) ~ 
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

  def defStruct: Parser[Ast.DefStruct] = {
    def id(x: Ast.Annotated[AstNode[Ast.StructTypeMember]]) = x
    def members = annotatedElementSequence(node(structTypeMember), comma, id)
    (Token.STRUCT ~> ident) ~ (Token.LBRACE ~> members <~ Token.RBRACE) ~ opt(Token.DEFAULT ~> exprNode) ^^ {
      case name ~ members ~ default => Ast.DefStruct(name, members, default)
    }
  }

  def defTopology: Parser[Ast.DefTopology] = {
    def members = annotatedElementSequence(topologyMemberNode, semi, Ast.TopologyMember(_))
    (Token.TOPOLOGY ~> ident) ~ (Token.LBRACE ~> members <~ Token.RBRACE) ^^ {
      case name ~ members => Ast.DefTopology(name, members)
    }
  }

  def exprNode: Parser[AstNode[Ast.Expr]] = {
    def leftAssoc(e: AstNode[Ast.Expr], es: List[Token ~ AstNode[Ast.Expr]]) = {
      def f(e1: AstNode[Ast.Expr], op_e2: Token ~ AstNode[Ast.Expr]) = {
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
      def dotIds(e: AstNode[Ast.Expr], ids: List[String]) = {
        def f(e: AstNode[Ast.Expr], id: String) = {
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
    def kind = {
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

  def index: Parser[AstNode[Ast.Expr]] = Token.LBRACKET ~> exprNode <~ Token.RBRACKET

  def moduleMemberNode: Parser[Ast.ModuleMember.Node] = {
    node(defAbsType) ^^ { case n => Ast.ModuleMember.DefAbsType(n) } |
    node(defArray) ^^ { case n => Ast.ModuleMember.DefArray(n) } |
    node(defComponent) ^^ { case n => Ast.ModuleMember.DefComponent(n) } |
    node(defComponentInstance) ^^ { case n => Ast.ModuleMember.DefComponentInstance(n) } |
    node(defConstant) ^^ { case n => Ast.ModuleMember.DefConstant(n) } |
    node(defEnum) ^^ { case n => Ast.ModuleMember.DefEnum(n) } |
    node(defModule) ^^ { case n => Ast.ModuleMember.DefModule(n) } |
    node(defPort) ^^ { case n => Ast.ModuleMember.DefPort(n) } |
    node(defStruct) ^^ { case n => Ast.ModuleMember.DefStruct(n) } |
    node(defTopology) ^^ { case n => Ast.ModuleMember.DefTopology(n) } |
    node(specInclude) ^^ { case n => Ast.ModuleMember.SpecInclude(n) } |
    node(specLoc) ^^ { case n => Ast.ModuleMember.SpecLoc(n) }
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

  def qualIdent: Parser[List[Ast.Ident]] = rep1sep(ident, dot)

  def queueFull: Parser[Ast.QueueFull] = {
    accept("assert", { case Token.ASSERT => Ast.QueueFull.Assert }) |
    accept("block", { case Token.BLOCK => Ast.QueueFull.Block }) |
    accept("drop", { case Token.DROP => Ast.QueueFull.Drop })
  }

  def specCommand: Parser[Ast.SpecCommand] = {
    def kind = {
      accept("async", { case Token.ASYNC => Ast.SpecCommand.Async })
      accept("guarded", { case Token.GUARDED => Ast.SpecCommand.Guarded })
      accept("sync", { case Token.SYNC => Ast.SpecCommand.Sync })
    }
    kind ~ (Token.COMMAND ~> ident) ~ formalParamList ~ 
    opt(Token.OPCODE ~> exprNode) ~ opt(Token.PRIORITY ~> exprNode) ~ opt(node(queueFull)) ^^ {
      case kind ~ name ~ params ~ opcode ~ priority ~ queueFull => 
        Ast.SpecCommand(kind, name, params, opcode, priority, queueFull)
    }
  }

  def specCompInstance: Parser[Ast.SpecCompInstance] = {
    visibility ~ (instance ~> node(qualIdent)) ^^ {
      case visibility ~ instance => Ast.SpecCompInstance(visibility, instance)
    }
  }

  def specConnectionGraph: Parser[Ast.SpecConnectionGraph] = {
    def direct = {
      (connections ~> ident) ~ (Token.LBRACE ~> elementSequence(connection, comma) <~ Token.RBRACE) ^^ { 
        case ident ~ connections => Ast.SpecConnectionGraph.Direct(ident, connections) 
      }
    }
    def pattern = {
      def instanceSequence = {
        opt(Token.LBRACE ~> elementSequence(node(qualIdent), comma) <~ Token.RBRACE) ^^ {
          case Some(elements) => elements
          case None => Nil
        }
      }
      (connections ~ instance ~> node(qualIdent)) ~ instanceSequence ~ (Token.PATTERN ~> exprNode) ^^ {
        case source ~ targets ~ pattern => Ast.SpecConnectionGraph.Pattern(source, targets, pattern)
      }
    }
    direct | pattern
  }

  def specEvent: Parser[Ast.SpecEvent] = {
    def severity = {
      activity ~ high ^^ { case _ => Ast.SpecEvent.ActivityHigh } |
      activity ~ low ^^ { case _ => Ast.SpecEvent.ActivityLow } |
      command ^^ { case _ => Ast.SpecEvent.Command } |
      diagnostic ^^ { case _ => Ast.SpecEvent.Diagnostic } |
      fatal ^^ { case _ => Ast.SpecEvent.Fatal } |
      warning ~ high ^^ { case _ => Ast.SpecEvent.WarningHigh } |
      warning ~ low ^^ { case _ => Ast.SpecEvent.WarningLow }
    }
    (Token.EVENT ~> ident) ~ formalParamList ~ (Token.SEVERITY ~> severity) ~
    opt(Token.ID ~> exprNode) ~
    opt(Token.FORMAT ~> node(literalString)) ~
    opt(Token.THROTTLE ~> exprNode) ^^ {
      case name ~ params ~ severity ~ id ~ format ~ throttle => 
        Ast.SpecEvent(name, params, severity, id, format, throttle)
    }
  }

  def specInclude: Parser[Ast.SpecInclude] = {
    include ~> literalString ^^ { case file => Ast.SpecInclude(file) }
  }

  def specInternalPort: Parser[Ast.SpecInternalPort] = {
    (internal ~ port ~> ident) ~ formalParamList ~
    opt(Token.PRIORITY ~> exprNode) ~
    opt(queueFull) ^^ {
      case name ~ params ~ priority ~ queueFull => 
        Ast.SpecInternalPort(name, params, priority, queueFull)
    }
  }

  def specLoc: Parser[Ast.SpecLoc] = {
    def kind = {
      accept("constant", { case Token.CONSTANT => Ast.SpecLoc.Constant }) |
      accept("port", { case Token.PORT => Ast.SpecLoc.Port }) |
      accept("topology", { case Token.TOPOLOGY => Ast.SpecLoc.Topology }) |
      accept("type", { case Token.TYPE => Ast.SpecLoc.Type }) |
      component ~ instance ^^ { case _ => Ast.SpecLoc.ComponentInstance } |
      component ^^ { case _ => Ast.SpecLoc.Component }
    }
    (Token.LOCATE ~> kind) ~ node(qualIdent) ~ (Token.AT ~> literalString) ^^ {
      case kind ~ symbol ~ file => Ast.SpecLoc(kind, symbol, file)
    }
  }

  def specParam: Parser[Ast.SpecParam] = {
    (Token.PARAM ~> ident) ~ (Token.COLON ~> node(typeName)) ~
    opt(Token.DEFAULT ~> exprNode) ~
    opt(Token.ID ~> exprNode) ~
    opt(Token.SET ~ Token.OPCODE ~> exprNode) ~
    opt(Token.SAVE ~ Token.OPCODE ~> exprNode) ^^ {
      case name ~ typeName ~ default ~ id ~ setOpcode ~ saveOpcode =>
        Ast.SpecParam(name, typeName, default, id, setOpcode, saveOpcode)
    }
  }

  def specPortInstance: Parser[Ast.SpecPortInstance] = {
    def generalKind = {
      async ~ input ^^ { case _ => Ast.SpecPortInstance.AsyncInput } |
      guarded ~ input ^^ { case _ => Ast.SpecPortInstance.GuardedInput } |
      output ^^ { case _ => Ast.SpecPortInstance.Output } |
      sync ~ input ^^ { case _ => Ast.SpecPortInstance.SyncInput }
    }
    def instanceType = {
      node(qualIdent) ^^ { case qi => Some(qi) } |
      serial ^^ { case _ => None}
    }
    def specialKind = {
      command ~ recv ^^ { case _ => Ast.SpecPortInstance.CommandRecv } |
      command ~ reg ^^ { case _ => Ast.SpecPortInstance.CommandReg } |
      command ~ resp ^^ { case _ => Ast.SpecPortInstance.CommandResp } |
      event ^^ { case _ => Ast.SpecPortInstance.Event } |
      param ~ get ^^ { case _ => Ast.SpecPortInstance.ParamGet } |
      param ~ set ^^ { case _ => Ast.SpecPortInstance.ParamSet } |
      telemetry ^^ { case _ => Ast.SpecPortInstance.Telemetry } |
      text ~ event ^^ { case _ => Ast.SpecPortInstance.TextEvent } |
      time ~ get ^^ { case _ => Ast.SpecPortInstance.TimeGet }
    }
    def general = {
      generalKind ~ (Token.PORT ~> ident) ~
      (Token.COLON ~> opt(index)) ~
      instanceType ~
      opt(Token.PRIORITY ~> exprNode) ~
      opt(queueFull) ^^ {
        case kind ~ name ~ size ~ ty ~ priority ~ queueFull =>
          Ast.SpecPortInstance.General(kind, name, size, ty, priority, queueFull)
      }
    }
    def special: Parser[Ast.SpecPortInstance] = {
      specialKind ~ (Token.PORT ~> ident) ^^ {
        case kind ~ name => Ast.SpecPortInstance.Special(kind, name)
      }
    }
    general | special
  }

  def specTlmChannel: Parser[Ast.SpecTlmChannel] = {
    val Channel = Ast.SpecTlmChannel
    def update = {
      always ^^ { case _ => Ast.SpecTlmChannel.Always } |
      on ~ change ^^ { case _ => Ast.SpecTlmChannel.OnChange }
    }
    def kind = {
      orange ^^ { case _ => Ast.SpecTlmChannel.Orange } |
      red ^^ { case _ => Ast.SpecTlmChannel.Red } |
      yellow ^^ { case _ => Ast.SpecTlmChannel.Yellow }
    }
    def limit = {
      kind ~ exprNode ^^ { case kind ~ e => (kind, e) }
    }
    def limitSequence = {
      opt(Token.LOW ~ Token.LBRACE ~> elementSequence(limit, comma <~ Token.RBRACE)) ^^ {
        case Some(seq) => seq
        case None => Nil
      }
    }
    (Token.TELEMETRY ~> ident) ~ 
    (Token.COLON ~> node(typeName)) ~
    opt(Token.ID ~> exprNode) ~ 
    opt(Token.UPDATE ~> update) ~
    opt(Token.FORMAT ~> node(literalString)) ~
    limitSequence ~ limitSequence ^^ {
      case name ~ typeName ~ id ~ update ~ format ~ low ~ high => Ast.SpecTlmChannel(
        name,
        typeName,
        id,
        update,
        format,
        low,
        high
      )
    }
  }

  def specTopImport: Parser[Ast.SpecTopImport] = {
    token("import", Token.IMPORT) ~> node(qualIdent) ^^ { 
      case top => Ast.SpecTopImport(top)
    }
  }

  def specUnusedPorts: Parser[Ast.SpecUnusedPorts] = {
    def unusedPort = node(qualIdent) ~ (Token.DOT ~> ident) ^^ {
      case instance ~ port => Ast.SpecUnusedPorts.Port(instance,port)
    }
    token("unused", Token.UNUSED) ~ 
    Token.LBRACE ~> elementSequence(unusedPort, comma) <~ Token.RBRACE ^^ {
      case ports => Ast.SpecUnusedPorts(ports)
    }
  }

  def structTypeMember: Parser[Ast.StructTypeMember] = {
    ident ~ (Token.COLON ~> node(typeName)) ~ opt(Token.FORMAT ~> node(literalString)) ^^ {
      case name ~ typeName ~ format => Ast.StructTypeMember(name, typeName, format)
    }
  }

  def topologyMemberNode: Parser[Ast.TopologyMember.Node] = {
    node(specCompInstance) ^^ { case n => Ast.TopologyMember.SpecCompInstance(n) }
    node(specConnectionGraph) ^^ { case n => Ast.TopologyMember.SpecConnectionGraph(n) }
    node(specInclude) ^^ { case n => Ast.TopologyMember.SpecInclude(n) }
    node(specTopImport) ^^ { case n => Ast.TopologyMember.SpecTopImport(n) }
    node(specUnusedPorts) ^^ { case n => Ast.TopologyMember.SpecUnusedPorts(n) }
  }

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
    accept("string", { case Token.STRING => Ast.TypeNameString }) |
    typeNameFloat |
    typeNameInt |
    node(qualIdent) ^^ { case qidn => Ast.TypeNameQualIdent(qidn) }
  }

  def visibility: Parser[Ast.Visibility] = {
    opt(token("private", Token.PRIVATE)) ^^ {
      case Some(_) => Ast.Visibility.Private
      case None => Ast.Visibility.Public
    }
  }

  override type Elem = Token

  private def activity: Parser[Unit] = token("activity", Token.ACTIVITY)

  private def always: Parser[Unit] = token("always", Token.ALWAYS)

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

  private def async = token("async", Token.ASYNC)

  private def change = token("change", Token.CHANGE)

  private def comma = token("comma", Token.COMMA)

  private def command = token("command", Token.COMMAND)

  private def component = token("component", Token.COMPONENT)

  private def connections = token("connections", Token.CONNECTIONS)

  private def diagnostic = token("diagnostic", Token.DIAGNOSTIC)
  
  private def event = token("event", Token.EVENT)

  private def dot = token("dot", Token.DOT)

  private def elementSequence[E,S](elt: Parser[E], sep: Parser[S]): Parser[List[E]] =
    repsep(elt, sep | Token.EOL) <~ opt(sep)

  private def fatal = token("fatal", Token.FATAL)

  private def guarded = token("guarded", Token.GUARDED)

  private def high = token("high", Token.HIGH)

  private def ident: Parser[Ast.Ident] =
    accept("identifier", { case Token.IDENTIFIER(s) => s })

  private def include = token("include", Token.INCLUDE)

  private def input = token("input", Token.INPUT)

  private def instance = token("instance", Token.INSTANCE)

  private def internal = token("internal", Token.INTERNAL)

  private def literalFalse = token("false", Token.FALSE)

  private def literalFloat: Parser[String] =
    accept("floating-point literal", { case Token.LITERAL_FLOAT(s) => s })

  private def literalInt: Parser[String] = 
    accept("integer literal", { case Token.LITERAL_INT(s) => s })

  private def literalString: Parser[String] =
    accept("string literal", { case Token.LITERAL_STRING(s) => s })

  private def literalTrue = token("true", Token.TRUE)

  private def low = token("low", Token.LOW)

  private def on = token("on", Token.ON)

  private def orange = token("orange", Token.ORANGE)

  private def output = token("output", Token.OUTPUT)

  private def param = token("param", Token.PARAM)
  
  private def get = token("get", Token.GET)

  private def port = token("port", Token.PORT)

  private def postAnnotation: Parser[String] =
    accept("post annotation", { case Token.POST_ANNOTATION(s) => s })

  private def preAnnotation: Parser[String] =
    accept("pre annotation", { case Token.PRE_ANNOTATION(s) => s })

  private def recv = token("recv", Token.RECV)

  private def red = token("red", Token.RED)

  private def ref = token("ref", Token.REF)

  private def reg = token("reg", Token.REG)

  private def resp = token("resp", Token.RESP)

  private def semi = token("semicolon", Token.SEMI)

  private def serial = token("serial", Token.SERIAL)

  private def set = token("set", Token.SET)

  private def sync = token("sync", Token.SYNC)

  private def telemetry = token("telemetry", Token.TELEMETRY)

  private def text = token("text", Token.TEXT)

  private def time = token("time", Token.TIME)

  private def token(s: String, t: Token): Parser[Unit] =
    accept(s, { case t => () })

  private def warning = token("warning", Token.WARNING)

  private def yellow = token("yellow", Token.YELLOW)

}
