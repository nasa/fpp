package fpp.compiler.syntax

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.Positional
import scala.util.parsing.input.{NoPosition, Position, Reader}

object Parser extends Parsers {

  class TokenReader(tokens: Seq[Token]) extends Reader[Token] {
    override def first = tokens.head
    override def atEnd = tokens.isEmpty
    override def pos = tokens.headOption.map(_.pos).getOrElse(NoPosition)
    override def rest = new TokenReader(tokens.tail)
  }

  def componentKind: Parser[Ast.ComponentKind] = {
    active ^^ { case _ => Ast.ComponentKind.Active } |
    passive ^^ { case _ => Ast.ComponentKind.Passive } |
    queued ^^ { case _ => Ast.ComponentKind.Queued } |
    failure("component kind expected")
  }

  def componentMemberNode: Parser[Ast.ComponentMember.Node] = {
    node(defAbsType) ^^ { case n => Ast.ComponentMember.DefAbsType(n) } |
    node(defArray) ^^ { case n => Ast.ComponentMember.DefArray(n) } |
    node(defConstant) ^^ { case n => Ast.ComponentMember.DefConstant(n) } |
    node(defEnum) ^^ { case n => Ast.ComponentMember.DefEnum(n) } |
    node(defStateMachine) ^^ { case n => Ast.ComponentMember.DefStateMachine(n) } |
    node(defStruct) ^^ { case n => Ast.ComponentMember.DefStruct(n) } |
    node(specCommand) ^^ { case n => Ast.ComponentMember.SpecCommand(n) } |
    node(specContainer) ^^ { case n => Ast.ComponentMember.SpecContainer(n) } |
    node(specEvent) ^^ { case n => Ast.ComponentMember.SpecEvent(n) } |
    node(specInclude) ^^ { case n => Ast.ComponentMember.SpecInclude(n) } |
    node(specInternalPort) ^^ { case n => Ast.ComponentMember.SpecInternalPort(n) } |
    node(specPortInstance) ^^ { case n => Ast.ComponentMember.SpecPortInstance(n) } |
    node(specPortMatching) ^^ { case n => Ast.ComponentMember.SpecPortMatching(n) } |
    node(specParam) ^^ { case n => Ast.ComponentMember.SpecParam(n) } |
    node(specRecord) ^^ { case n => Ast.ComponentMember.SpecRecord(n) } |
    node(specStateMachineInstance) ^^ { case n => Ast.ComponentMember.SpecStateMachineInstance(n) } |
    node(specTlmChannel) ^^ { case n => Ast.ComponentMember.SpecTlmChannel(n) } |
    failure("component member expected")
  }

  def componentMembers: Parser[List[Ast.ComponentMember]] =
    annotatedElementSequence(componentMemberNode, semi, Ast.ComponentMember(_))

  def connection: Parser[Ast.SpecConnectionGraph.Connection] = {
    def connectionPort = node(portInstanceIdentifier) ~! opt(index)
    connectionPort ~! (rarrow ~>! connectionPort) ^^ {
      case (fromPort ~ fromIndex) ~ (toPort ~ toIndex) => {
        Ast.SpecConnectionGraph.Connection(
          fromPort,
          fromIndex,
          toPort,
          toIndex
        )
      }
    }
  }

  def defAbsType: Parser[Ast.DefAbsType] = {
    (typeToken ~>! ident) ^^ { case id => Ast.DefAbsType(id) }
  }

  def defAction: Parser[Ast.DefAction] = {
    (action ~> ident) ~! opt(colon ~>! node(typeName)) ^^ {
      case ident ~ typeName => Ast.DefAction(ident, typeName)
    }
  }

  def defArray: Parser[Ast.DefArray] = {
    (array ~>! ident <~! equals) ~!
    index ~! node(typeName) ~!
    opt(default ~>! exprNode) ~!
    opt(format ~>! node(literalString)) ^^ {
      case name ~ size ~ eltType ~ default ~ format => Ast.DefArray(name, size, eltType, default, format)
    }
  }

  def defComponent: Parser[Ast.DefComponent] = {
    componentKind ~! (component ~>! ident) ~! (lbrace ~>! componentMembers <~! rbrace) ^^ {
      case kind ~ name ~ members => Ast.DefComponent(kind, name, members)
    }
  }

  def defComponentInstance: Parser[Ast.DefComponentInstance] = {
    def initSpecSequence = {
      def id(x: Ast.Annotated[AstNode[Ast.SpecInit]]) = x
      opt(lbrace ~>! annotatedElementSequence(node(specInit), semi, id) <~! rbrace) ^^ {
        case Some(elements) => elements
        case None => Nil
      }
    }
    (instance ~>! ident) ~! (colon ~>! node(qualIdent)) ~! (base ~! id ~>! exprNode) ~!
    opt(typeToken ~>! node(literalString)) ~!
    opt(at ~>! node(literalString)) ~!
    opt(queue ~! size ~>! exprNode) ~!
    opt(stack ~! size ~>! exprNode) ~!
    opt(priority ~>! exprNode) ~!
    opt(cpu ~>! exprNode) ~!
    initSpecSequence ^^ {
      case name ~ typeName ~ baseId ~ implType ~ file ~ queueSize ~ stackSize ~ priority ~ cpu ~ initSpecSequence =>
        Ast.DefComponentInstance(
          name,
          typeName,
          baseId,
          implType,
          file,
          queueSize,
          stackSize,
          priority,
          cpu,
          initSpecSequence
        )
    }
  }

  def defConstant: Parser[Ast.DefConstant] = {
    (constant ~>! ident) ~! (equals ~>! exprNode) ^^ {
      case id ~ e => Ast.DefConstant(id, e)
    }
  }

  def defEnum: Parser[Ast.DefEnum] = {
    def id(x: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = x
    def constants = annotatedElementSequence(node(defEnumConstant), comma, id)
    (enumeration ~>! ident) ~!
    opt(colon ~>! node(typeName)) ~!
    (lbrace ~>! constants <~! rbrace) ~!
    opt(default ~>! exprNode) ^^ {
      case name ~ typeName ~ constants ~ default => Ast.DefEnum(name, typeName, constants, default)
    }
  }

  def defEnumConstant: Parser[Ast.DefEnumConstant] = {
    ident ~! opt(equals ~>! exprNode) ^^ {
      case id ~ e => Ast.DefEnumConstant(id, e)
    }
  }

  def defGuard: Parser[Ast.DefGuard] = {
    (guard ~> ident) ~! opt(colon ~>! node(typeName)) ^^ {
      case ident ~ typeName => Ast.DefGuard(ident, typeName)
    }
  }

  def defJunction: Parser[Ast.DefJunction] = {
    (junction ~> ident) ~! (lbrace ~> ifToken ~> node(ident)) ~! node(transitionExpr) ~!
      (elseToken ~> node(transitionExpr)) <~! rbrace ^^ {
      case ident ~ guard ~ ifTransition ~ elseTransition =>
        Ast.DefJunction(ident, guard, ifTransition, elseTransition)
    }
  }

  def defModule: Parser[Ast.DefModule] = {
    (module ~>! ident) ~! (lbrace ~>! moduleMembers <~! rbrace) ^^ {
      case name ~ members => Ast.DefModule(name, members)
    }
  }

  def defPort: Parser[Ast.DefPort] = {
    (port ~>! ident) ~! formalParamList ~! opt(rarrow ~>! node(typeName)) ^^ {
      case ident ~ formalParamList ~ returnType => Ast.DefPort(ident, formalParamList, returnType)
    }
  }

  def defSignal: Parser[Ast.DefSignal] = {
    (signal ~> ident) ~! opt(colon ~>! node(typeName)) ^^ {
      case ident ~ typeName => Ast.DefSignal(ident, typeName)
    }
  }

  def defState: Parser[Ast.DefState] = {
    state ~> ident ~! opt(lbrace ~>! stateMembers <~! rbrace) ^^ {
      case ident ~ members => Ast.DefState(ident, members.getOrElse(Nil))
    }
  }

  def defStateMachine: Parser[Ast.DefStateMachine] = {
    state ~> (machine ~> ident) ~! opt(lbrace ~>! stateMachineMembers <~! rbrace) ^^ {
      case name ~ members => Ast.DefStateMachine(name, members)
    }
  }

  def defStruct: Parser[Ast.DefStruct] = {
    def id(x: Ast.Annotated[AstNode[Ast.StructTypeMember]]) = x
    def members = annotatedElementSequence(node(structTypeMember), comma, id)
    (struct ~>! ident) ~! (lbrace ~>! members <~! rbrace) ~! opt(default ~>! exprNode) ^^ {
      case name ~ members ~ default => Ast.DefStruct(name, members, default)
    }
  }

  def defTopology: Parser[Ast.DefTopology] = {
    (topology ~>! ident) ~! (lbrace ~>! topologyMembers <~! rbrace) ^^ {
      case name ~ members => Ast.DefTopology(name, members)
    }
  }

  def doExpr: Parser[List[AstNode[Ast.Ident]]] = {
    def elts = elementSequence(node(ident), comma)
    doToken ~>! lbrace ~>! elts <~! rbrace ^^ { case elts => elts }
  }

  def exprNode: Parser[AstNode[Ast.Expr]] = {
    def leftAssoc(e: AstNode[Ast.Expr], es: List[Token ~ AstNode[Ast.Expr]]) = {
      def f(e1: AstNode[Ast.Expr], op_e2: Token ~ AstNode[Ast.Expr]) = {
        val op ~ e2 = op_e2
        val binop =
          op match {
            case Token.MINUS() => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Sub, e2))
            case Token.PLUS() => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Add, e2))
            case Token.SLASH() => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Div, e2))
            case Token.STAR() => AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Mul, e2))
            case _ => throw new InternalError(s"invalid binary operator ${op}")
        }
        val loc = Location(ParserState.file, op.pos, ParserState.includingLoc)
        Locations.put(binop.id, loc)
        binop
      }
      es.foldLeft(e)(f)
    }
    def dotOperand = node {
      def arrayExpr =
        lbracket ~>! elementSequence(exprNode, comma) <~! rbracket ^^ {
          case es => Ast.ExprArray(es)
        }
      def falseExpr = falseToken ^^ { case _ => Ast.ExprLiteralBool(Ast.LiteralBool.False) }
      def floatExpr = literalFloat ^^ { case s => Ast.ExprLiteralFloat(s) }
      def identExpr = ident ^^ { case id => Ast.ExprIdent(id) }
      def intExpr = literalInt ^^ { case li => Ast.ExprLiteralInt(li) }
      def parenExpr = lparen ~> exprNode <~ rparen ^^ { case e => Ast.ExprParen(e) }
      def stringExpr = literalString ^^ { case s => Ast.ExprLiteralString(s) }
      def structMember = ident ~! (equals ~>! exprNode) ^^ {
        case id ~ e => Ast.StructMember(id, e)
      }
      def structExpr =
        lbrace ~>! elementSequence(node(structMember), comma) <~! rbrace ^^ {
          case es => Ast.ExprStruct(es)
        }
      def trueExpr = trueToken ^^ { case _ => Ast.ExprLiteralBool(Ast.LiteralBool.True) }
      arrayExpr |
      falseExpr |
      floatExpr |
      identExpr |
      intExpr |
      parenExpr |
      stringExpr |
      structExpr |
      trueExpr |
      failure("expression expected")
    }
    def unaryMinus = node {
      minus ~>! unaryMinusOperand ^^ { case e => Ast.ExprUnop(Ast.Unop.Minus, e) }
    }
    def unaryMinusOperand = {
      def dotSelectors(e: AstNode[Ast.Expr], ss: List[Token ~ AstNode[String]]) = {
        def f(e: AstNode[Ast.Expr], s: Token ~ AstNode[String]) = {
          val _ ~ id = s
          val dot = AstNode.create(Ast.ExprDot(e, id))
          val loc = Locations.get(e.id)
          Locations.put(dot.id, loc)
          dot
        }
        ss.foldLeft(e)(f)
      }
      dotOperand ~ rep(dot ~! node(ident)) ^^ { case e ~ ss => dotSelectors(e, ss) }
    }
    def mulDivOperand = unaryMinus | unaryMinusOperand
    def addSubOperand = mulDivOperand ~ rep((star | slash) ~! mulDivOperand) ^^ {
      case e ~ es => leftAssoc(e, es)
    }
    addSubOperand ~ rep((plus | minus) ~! addSubOperand) ^^ {
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
    kind ~ ident ~! (colon ~>! node(typeName)) ^^ {
      case kind ~ id ~ tn => Ast.FormalParam(kind, id, tn)
    }
  }

  def formalParamList: Parser[Ast.FormalParamList] = {
    def id(x: Ast.Annotated[AstNode[Ast.FormalParam]]) = x
    def params = annotatedElementSequence(node(formalParam), comma, id)
    opt(lparen ~>! params <~! rparen) ^^ {
      case Some(params) => params
      case None => Nil
    }
  }

  def index: Parser[AstNode[Ast.Expr]] = lbracket ~>! exprNode <~! rbracket

  def lexAndParse[T](
    tokenStream: => Result.Result[List[Token]],
    parser: Parser[T]
  ): Result.Result[T] = {
    for {
      tokens <- tokenStream
      result <- parseTokens(parser)(tokens)
    } yield result
  }

  def moduleMemberNode: Parser[Ast.ModuleMember.Node] = {
    node(defAbsType) ^^ { case n => Ast.ModuleMember.DefAbsType(n) } |
    node(defArray) ^^ { case n => Ast.ModuleMember.DefArray(n) } |
    node(defComponent) ^^ { case n => Ast.ModuleMember.DefComponent(n) } |
    node(defComponentInstance) ^^ { case n => Ast.ModuleMember.DefComponentInstance(n) } |
    node(defConstant) ^^ { case n => Ast.ModuleMember.DefConstant(n) } |
    node(defEnum) ^^ { case n => Ast.ModuleMember.DefEnum(n) } |
    node(defModule) ^^ { case n => Ast.ModuleMember.DefModule(n) } |
    node(defPort) ^^ { case n => Ast.ModuleMember.DefPort(n) } |
    node(defStateMachine) ^^ { case n => Ast.ModuleMember.DefStateMachine(n) } |
    node(defStruct) ^^ { case n => Ast.ModuleMember.DefStruct(n) } |
    node(defTopology) ^^ { case n => Ast.ModuleMember.DefTopology(n) } |
    node(specInclude) ^^ { case n => Ast.ModuleMember.SpecInclude(n) } |
    node(specLoc) ^^ { case n => Ast.ModuleMember.SpecLoc(n) } |
    failure("module member expected")
  }

  def moduleMembers: Parser[List[Ast.ModuleMember]] = annotatedElementSequence(moduleMemberNode, semi, Ast.ModuleMember(_))

  def node[T](p: Parser[T]): Parser[AstNode[T]] = {
    final case class Positioned(t: T) extends Positional
    def positionedT: Parser[Positioned] = positioned {
      p ^^ { case x => Positioned(x) }
    }
    positionedT ^^ {
      case pt @ Positioned(t) => {
        val n = AstNode.create(t)
        val loc = Location(ParserState.file, pt.pos, ParserState.includingLoc)
        Locations.put(n.id, loc)
        n
      }
    }
  }

  def parseAllInput[T](p: Parser[T]): Parser[T] = new Parser[T] {
    def apply(in: Input) = {
      val r = p(in)
      r match {
        case s @ Success(out, in1) =>
          error match {
            case Some(e) => e
            case None => if (in1.atEnd) s else Failure("unexpected token", in1)
          }
        case other => other
      }
    }
  }

  def parseFile[T] (p: Parser[T]) (includingLoc: Option[Location]) (f: File): Result.Result[T] =
    lexAndParse(Lexer.lexFile(f, includingLoc), p)

  def parseString[T](p: Parser[T])(s: String): Result.Result[T] = lexAndParse(Lexer.lexString(s), p)

  def parseTokens[T](p: Parser[T])(tokens: Seq[Token]): Result.Result[T] = {
    error = None
    val reader = new TokenReader(tokens)
    parseAllInput(p)(reader) match {
      case NoSuccess(msg, next) => {
        val loc = Location(ParserState.file, next.pos, ParserState.includingLoc)
        Left(SyntaxError(loc,msg))
      }
      case Success(result, next) => Right(result)
      // Suppress false compiler warning
      case _ => throw new InternalError("This cannot happen")
    }
  }

  def portInstanceIdentifier: Parser[Ast.PortInstanceIdentifier] =
    node(ident) ~! (dot ~>! qualIdentNodeList) ^^ {
      case id ~ qid => {
        val portName :: tail = qid.reverse
        val componentInstance = id :: tail.reverse
        val node = Ast.QualIdent.Node.fromNodeList(componentInstance)
        Ast.PortInstanceIdentifier(node, portName)
      }
    }

  def qualIdent: Parser[Ast.QualIdent] =
    qualIdentNodeList ^^ { case qid => Ast.QualIdent.fromNodeList(qid) }

  def qualIdentNodeList: Parser[Ast.QualIdent.NodeList] = rep1sep(node(ident), dot)

  def queueFull: Parser[Ast.QueueFull] = {
    assert ^^ { case _ => Ast.QueueFull.Assert } |
    block ^^ { case _ => Ast.QueueFull.Block } |
    drop ^^ { case _ => Ast.QueueFull.Drop } |
    hook ^^ { case _ => Ast.QueueFull.Hook } |
    failure("queue full expected")
  }

  def specCommand: Parser[Ast.SpecCommand] = {
    def kind = {
      async ^^ { case _ => Ast.SpecCommand.Async } |
      guarded ^^ { case _ => Ast.SpecCommand.Guarded } |
      sync ^^ { case _ => Ast.SpecCommand.Sync } |
      failure("command kind expected")
    }
    kind ~ (command ~> ident) ~! formalParamList ~!
    opt(opcode ~>! exprNode) ~! opt(priority ~>! exprNode) ~! opt(node(queueFull)) ^^ {
      case kind ~ name ~ params ~ opcode ~ priority ~ queueFull =>
        Ast.SpecCommand(kind, name, params, opcode, priority, queueFull)
    }
  }

  def specCompInstance: Parser[Ast.SpecCompInstance] = {
    visibility ~ (instance ~>! node(qualIdent)) ^^ {
      case visibility ~ instance => Ast.SpecCompInstance(visibility, instance)
    }
  }

  def specConnectionGraph: Parser[Ast.SpecConnectionGraph] = {
    def directGraph = {
      (connections ~> ident) ~! (lbrace ~>! elementSequence(connection, comma) <~! rbrace) ^^ {
        case ident ~ connections => Ast.SpecConnectionGraph.Direct(ident, connections)
      }
    }
    def patternGraph = {
      def patternKind = {
        command ^^ { case _ => Ast.SpecConnectionGraph.Pattern.Command } |
        event ^^ { case _ => Ast.SpecConnectionGraph.Pattern.Event } |
        health ^^ { case _ => Ast.SpecConnectionGraph.Pattern.Health } |
        param ^^ { case _ => Ast.SpecConnectionGraph.Pattern.Param } |
        telemetry ^^ { case _ => Ast.SpecConnectionGraph.Pattern.Telemetry } |
        text ~ event ^^ { case _ => Ast.SpecConnectionGraph.Pattern.TextEvent } |
        time ^^ { case _ => Ast.SpecConnectionGraph.Pattern.Time }
      }
      def instanceSequence = {
        opt(lbrace ~>! elementSequence(node(qualIdent), comma) <~! rbrace) ^^ {
          case Some(elements) => elements
          case None => Nil
        }
      }
      patternKind ~! (connections ~! instance ~>! node(qualIdent)) ~! instanceSequence ^^ {
        case kind ~ source ~ targets => Ast.SpecConnectionGraph.Pattern(
          kind,
          source,
          targets
        )
      }
    }
    directGraph | patternGraph | failure("connection graph expected")
  }

  def specContainer: Parser[Ast.SpecContainer] = {
    ((product ~ container) ~>! ident) ~!
    opt(id ~>! exprNode) ~!
    opt((default ~ priority) ~>! exprNode) ^^ {
      case name ~ id ~ defaultPriority => Ast.SpecContainer(
        name,
        id,
        defaultPriority
      )
    }
  }

  def specEvent: Parser[Ast.SpecEvent] = {
    def severityLevel = {
      activity ~ high ^^ { case _ => Ast.SpecEvent.ActivityHigh } |
      activity ~ low ^^ { case _ => Ast.SpecEvent.ActivityLow } |
      command ^^ { case _ => Ast.SpecEvent.Command } |
      diagnostic ^^ { case _ => Ast.SpecEvent.Diagnostic } |
      fatal ^^ { case _ => Ast.SpecEvent.Fatal } |
      warning ~ high ^^ { case _ => Ast.SpecEvent.WarningHigh } |
      warning ~ low ^^ { case _ => Ast.SpecEvent.WarningLow } |
      failure("severity level expected")
    }
    (event ~> ident) ~! formalParamList ~! (severity ~>! severityLevel) ~!
    opt(id ~>! exprNode) ~!
    (format ~>! node(literalString)) ~!
    opt(throttle ~>! exprNode) ^^ {
      case name ~ params ~ severity ~ id ~ format ~ throttle =>
        Ast.SpecEvent(name, params, severity, id, format, throttle)
    }
  }

  def specInclude: Parser[Ast.SpecInclude] = {
    include ~>! node(literalString) ^^ { case file => Ast.SpecInclude(file) }
  }

  def specInit: Parser[Ast.SpecInit] = {
    (phase ~>! exprNode) ~! literalString ^^ {
      case phase ~ code => Ast.SpecInit(phase, code)
    }
  }

  def specInitialTransition: Parser[Ast.SpecInitialTransition] = {
    initial ~> transitionExpr ^^ {
      case transition => Ast.SpecInitialTransition(transition)
    }
  }

  def specEntry: Parser[Ast.SpecEntry] = {
    entry ~> doExpr ^^ {
      case actions => Ast.SpecEntry(actions)
    }
  }

  def specExit: Parser[Ast.SpecExit] = {
    exit ~> doExpr ^^ {
      case actions => Ast.SpecExit(actions)
    }
  }

  def specInternalPort: Parser[Ast.SpecInternalPort] = {
    (internal ~! port ~>! ident) ~! formalParamList ~!
    opt(priority ~>! exprNode) ~!
    opt(queueFull) ^^ {
      case name ~ params ~ priority ~ queueFull =>
        Ast.SpecInternalPort(name, params, priority, queueFull)
    }
  }

  def specLoc: Parser[Ast.SpecLoc] = {
    def kind = {
      component ^^ { case _ => Ast.SpecLoc.Component } |
      constant ^^ { case _ => Ast.SpecLoc.Constant } |
      instance ^^ { case _ => Ast.SpecLoc.ComponentInstance } |
      port ^^ { case _ => Ast.SpecLoc.Port } |
      state ~! machine ^^ { case _ => Ast.SpecLoc.StateMachine } |
      topology ^^ { case _ => Ast.SpecLoc.Topology } |
      typeToken ^^ { case _ => Ast.SpecLoc.Type } |
      failure("location kind expected")
    }
    (locate ~>! kind) ~! node(qualIdent) ~! (at ~>! node(literalString)) ^^ {
      case kind ~ symbol ~ file => Ast.SpecLoc(kind, symbol, file)
    }
  }

  def specParam: Parser[Ast.SpecParam] = {
    (param ~>! ident) ~ (colon ~>! node(typeName)) ~!
    opt(default ~>! exprNode) ~!
    opt(id ~>! exprNode) ~!
    opt(set ~! opcode ~>! exprNode) ~!
    opt(save ~! opcode ~>! exprNode) ^^ {
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
      serial ^^ { case _ => None} |
      failure("port type expected")
    }
    def specialInputKind = {
      async ^^ { case _ => Ast.SpecPortInstance.Async } |
      guarded ^^ { case _ => Ast.SpecPortInstance.Guarded } |
      sync ^^ { case _ => Ast.SpecPortInstance.Sync }
    }
    def specialKind = {
      command ~ recv ^^ { case _ => Ast.SpecPortInstance.CommandRecv } |
      command ~ reg ^^ { case _ => Ast.SpecPortInstance.CommandReg } |
      command ~ resp ^^ { case _ => Ast.SpecPortInstance.CommandResp } |
      event ^^ { case _ => Ast.SpecPortInstance.Event } |
      param ~ get ^^ { case _ => Ast.SpecPortInstance.ParamGet } |
      param ~ set ^^ { case _ => Ast.SpecPortInstance.ParamSet } |
      product ~ get ^^ { case _ => Ast.SpecPortInstance.ProductGet } |
      product ~ recv ^^ { case _ => Ast.SpecPortInstance.ProductRecv } |
      product ~ request ^^ { case _ => Ast.SpecPortInstance.ProductRequest } |
      product ~ send ^^ { case _ => Ast.SpecPortInstance.ProductSend } |
      telemetry ^^ { case _ => Ast.SpecPortInstance.Telemetry } |
      text ~ event ^^ { case _ => Ast.SpecPortInstance.TextEvent } |
      time ~ get ^^ { case _ => Ast.SpecPortInstance.TimeGet }
    }
    def general = {
      generalKind ~! (port ~>! ident) ~!
      (colon ~>! opt(index)) ~!
      instanceType ~!
      opt(priority ~>! exprNode) ~!
      opt(node(queueFull)) ^^ {
        case kind ~ name ~ size ~ ty ~ priority ~ queueFull =>
          Ast.SpecPortInstance.General(kind, name, size, ty, priority, queueFull)
      }
    }
    def special: Parser[Ast.SpecPortInstance] = {
      opt(specialInputKind) ~
      specialKind ~
      (port ~>! ident) ~!
      opt(priority ~>! exprNode) ~!
      opt(node(queueFull)) ^^ {
        case inputKind ~ kind ~ name ~ priority ~ queueFull =>
          Ast.SpecPortInstance.Special(
            inputKind,
            kind,
            name,
            priority,
            queueFull
          )
      }
    }
    general | special
  }

  def specPortMatching: Parser[Ast.SpecPortMatching] = {
    fppMatch ~>! node(ident) ~! (fppWith ~>! node(ident)) ^^ {
      case port1 ~ port2 => Ast.SpecPortMatching(port1, port2)
    }
  }

  def specRecord: Parser[Ast.SpecRecord] = {
    def arrayOpt = opt(array) ^^ {
      case Some(_) => true
      case None => false
    }
    ((product ~ record) ~>! ident) ~!
    (colon ~>! node(typeName)) ~!
    arrayOpt ~!
    opt(id ~>! exprNode) ^^ {
      case name ~ recordType ~ arrayOpt ~ id => Ast.SpecRecord(
        name,
        recordType,
        arrayOpt,
        id
      )
    }
  }

  def specStateMachineInstance: Parser[Ast.SpecStateMachineInstance] = {
    (state ~> machine ~> (instance ~>! ident) ~! (colon ~>! node(qualIdent)) ~!
    opt(priority ~>! exprNode) ~!
    opt(queueFull)) ^^ {
      case name ~ statemachine ~ priority ~ queueFull => 
        Ast.SpecStateMachineInstance(name, statemachine, priority, queueFull)
    }
  }

  def specTlmChannel: Parser[Ast.SpecTlmChannel] = {
    def updateSetting = {
      always ^^ { case _ => Ast.SpecTlmChannel.Always } |
      on ~! change ^^ { case _ => Ast.SpecTlmChannel.OnChange } |
      failure("update kind expected")
    }
    def kind = {
      orange ^^ { case _ => Ast.SpecTlmChannel.Orange } |
      red ^^ { case _ => Ast.SpecTlmChannel.Red } |
      yellow ^^ { case _ => Ast.SpecTlmChannel.Yellow }
    }
    def limit = {
      node(kind) ~! exprNode ^^ { case kind ~ e => (kind, e) }
    }
    def limitSequence(p: Parser[Token]) = {
      opt(p ~! lbrace ~>! elementSequence(limit, comma) <~! rbrace) ^^ {
        case Some(seq) => seq
        case None => Nil
      }
    }
    (telemetry ~> ident) ~!
    (colon ~>! node(typeName)) ~!
    opt(id ~>! exprNode) ~!
    opt(update ~>! updateSetting) ~!
    opt(format ~>! node(literalString)) ~!
    limitSequence(low) ~! limitSequence(high) ^^ {
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

  def specTopImport: Parser[Ast.SpecTopImport] =
    importToken ~>! node(qualIdent) ^^ { case top => Ast.SpecTopImport(top) }

  def specStateTransition: Parser[Ast.SpecStateTransition] = {
    (on ~> node(ident)) ~! opt(ifToken ~> node(ident)) ~ transitionOrDo ^^ {
      case signal ~ guard ~ transitionOrDo =>
        Ast.SpecStateTransition(signal, guard, transitionOrDo)
    }
  }

  def stateMachineMemberNode: Parser[Ast.StateMachineMember.Node] = {
    node(specInitialTransition) ^^ { case n => Ast.StateMachineMember.SpecInitialTransition(n) } |
    node(defState) ^^ { case n => Ast.StateMachineMember.DefState(n) } |
    node(defSignal) ^^ { case n => Ast.StateMachineMember.DefSignal(n) } |
    node(defAction) ^^ { case n => Ast.StateMachineMember.DefAction(n) } |
    node(defGuard) ^^ { case n => Ast.StateMachineMember.DefGuard(n) } |
    node(defJunction) ^^ { case n => Ast.StateMachineMember.DefJunction(n) } |
    failure("state machine member expected")
  }

  def stateMachineMembers: Parser[List[Ast.StateMachineMember]] =
    annotatedElementSequence(stateMachineMemberNode, semi, Ast.StateMachineMember(_))

  def stateMemberNode: Parser[Ast.StateMember.Node] = {
    node(defJunction) ^^ { case n => Ast.StateMember.DefJunction(n) } |
    node(defState) ^^ { case n => Ast.StateMember.DefState(n) } |
    node(specEntry) ^^ { case n => Ast.StateMember.SpecEntry(n) } |
    node(specExit) ^^ { case n => Ast.StateMember.SpecExit(n) } |
    node(specInitialTransition) ^^ { case n => Ast.StateMember.SpecInitialTransition(n) } |
    node(specStateTransition) ^^ { case n => Ast.StateMember.SpecStateTransition(n) } |
    failure("state member expected")
  }

  def stateMembers: Parser[List[Ast.StateMember]] =
    annotatedElementSequence(stateMemberNode, semi, Ast.StateMember(_))

  def structTypeMember: Parser[Ast.StructTypeMember] = {
    ident ~! (colon ~>! opt(index)) ~! node(typeName) ~! opt(format ~>! node(literalString)) ^^ {
      case name ~ size ~ typeName ~ format => Ast.StructTypeMember(name, size, typeName, format)
    }
  }

  def topologyMemberNode: Parser[Ast.TopologyMember.Node] = {
    node(specCompInstance) ^^ { case n => Ast.TopologyMember.SpecCompInstance(n) } |
    node(specConnectionGraph) ^^ { case n => Ast.TopologyMember.SpecConnectionGraph(n) } |
    node(specInclude) ^^ { case n => Ast.TopologyMember.SpecInclude(n) } |
    node(specTopImport) ^^ { case n => Ast.TopologyMember.SpecTopImport(n) } |
    failure("topology member expected")
  }

  def topologyMembers: Parser[List[Ast.TopologyMember]] =
    annotatedElementSequence(topologyMemberNode, semi, Ast.TopologyMember(_))

  def transUnit: Parser[Ast.TransUnit] = {
    tuMembers ^^ { case members => Ast.TransUnit(members) }
  }

  def transitionExpr: Parser[Ast.TransitionExpr] =
    opt(doExpr) ~ (enter ~> node(qualIdent)) ^^ {
      case actionsOpt ~ destination => Ast.TransitionExpr(
        actionsOpt.getOrElse(Nil),
        destination
      )
    }

  def transitionOrDo: Parser[Ast.TransitionOrDo] = {
    def transitionParser: Parser[Ast.TransitionOrDo.Transition] = transitionExpr ^^ {
      case e => Ast.TransitionOrDo.Transition(e)
    }
    def doParser: Parser[Ast.TransitionOrDo.Do] = doExpr ^^ {
      case actions => Ast.TransitionOrDo.Do(actions)
    }
    transitionParser | doParser
  }

  def tuMembers = moduleMembers

  def typeName: Parser[Ast.TypeName] = {
    def typeNameFloat =
      accept("F32()", { case Token.F32() => Ast.TypeNameFloat(Ast.F32()) }) |
      accept("F64()", { case Token.F64() => Ast.TypeNameFloat(Ast.F64()) })
    def typeNameInt =
      accept("I8()", { case Token.I8() => Ast.TypeNameInt(Ast.I8()) }) |
      accept("I16()", { case Token.I16() => Ast.TypeNameInt(Ast.I16()) }) |
      accept("I32()", { case Token.I32() => Ast.TypeNameInt(Ast.I32()) }) |
      accept("I64()", { case Token.I64() => Ast.TypeNameInt(Ast.I64()) }) |
      accept("U8()", { case Token.U8() => Ast.TypeNameInt(Ast.U8()) }) |
      accept("U16()", { case Token.U16() => Ast.TypeNameInt(Ast.U16()) }) |
      accept("U32()", { case Token.U32() => Ast.TypeNameInt(Ast.U32()) }) |
      accept("U64()", { case Token.U64() => Ast.TypeNameInt(Ast.U64()) })
    accept("bool", { case Token.BOOL() => Ast.TypeNameBool }) |
    string ~> opt(size ~>! exprNode) ^^ { case e => Ast.TypeNameString(e) } |
    typeNameFloat |
    typeNameInt |
    node(qualIdent) ^^ { case qid => Ast.TypeNameQualIdent(qid) } |
    failure("type name expected")
  }

  def visibility: Parser[Ast.Visibility] = {
    opt(accept("private", { case Token.PRIVATE() => () })) ^^ {
      case Some(_) => Ast.Visibility.Private
      case None => Ast.Visibility.Public
    }
  }

  override def commit[T](p: => Parser[T]) = Parser{ in =>
    def setError(e: Error) = {
      error match {
        case None => { error = Some(e) }
        case Some(_) => ()
      }
      e
    }
    val r = p(in)
    r match{
      case s @ Success(_, _) => s
      case e : Error => setError(e)
      case Failure(msg, next) => setError(Error(msg, next))
    }
  }

  override type Elem = Token

  private def action = accept("action", { case t : Token.ACTION => t })

  private def active = accept("active", { case t : Token.ACTIVE => t })

  private def activity = accept("activity", { case t : Token.ACTIVITY => t })

  private def always = accept("always", { case t : Token.ALWAYS => t })

  private def annotatedElementSequence[E, S, T](
    elt: Parser[E],
    punct: Parser[S],
    constructor: Ast.Annotated[E] => T
  ): Parser[List[T]] = {
    def terminator = punct | eol
    def prefix0 = elt ^^ { case elt => (Nil, elt) }
    def prefix1 = rep1(preAnnotation) ~! elt ^^ { case al ~ elt => (al, elt) }
    def prefix = prefix0 | prefix1
    def punctTerminatedElt = (prefix <~ terminator) ~ rep(postAnnotation) ^^ {
      case (al1, elt) ~ al2 => (al1, elt, al2)
    }
    def annotationTerminatedElt = prefix ~ rep1(postAnnotation) ^^ {
      case (al1, elt) ~ al2 => (al1, elt, al2)
    }
    def terminatedElt = punctTerminatedElt | annotationTerminatedElt
    def unterminatedElt = rep(preAnnotation) ~ elt ^^ {
      case al ~ elt => (al, elt, Nil)
    }
    def elts = rep(terminatedElt) ~ opt(unterminatedElt) ^^ {
      case elts ~ Some(elt) => elts :+ elt
      case elts ~ None => elts
    }
    (rep(eol) ~> elts) ^^ { elts => elts.map(constructor) }
  }

  private def array = accept("array", { case t : Token.ARRAY => t })

  private def assert = accept("assert", { case t : Token.ASSERT => t })

  private def async = accept("async", { case t : Token.ASYNC => t })

  private def at = accept("at", { case t : Token.AT => t })

  private def base = accept("base", { case t : Token.BASE => t })

  private def block = accept("block", { case t : Token.BLOCK => t })

  private def change = accept("change", { case t : Token.CHANGE => t })

  private def colon = accept(":", { case t : Token.COLON => t })

  private def comma = accept(",", { case t : Token.COMMA => t })

  private def command = accept("command", { case t : Token.COMMAND => t })

  private def component = accept("component", { case t : Token.COMPONENT => t })

  private def connections = accept("connections", { case t : Token.CONNECTIONS => t })

  private def constant = accept("constant", { case t : Token.CONSTANT => t })

  private def container = accept("container", { case t : Token.CONTAINER => t })

  private def cpu = accept("cpu", { case t : Token.CPU => t })

  private def default = accept("default", { case t : Token.DEFAULT => t })

  private def diagnostic = accept("diagnostic", { case t : Token.DIAGNOSTIC => t })

  private def doToken = accept("do", { case t : Token.DO => t })

  private def dot = accept(".", { case t : Token.DOT => t })

  private def drop = accept("drop", { case t : Token.DROP => t })

  private def elementSequence[E,S](elt: Parser[E], sep: Parser[S]): Parser[List[E]] =
    repsep(elt, sep | eol) <~ opt(sep)

  private def elseToken = accept("else", { case t : Token.ELSE => t })

  private def enter = accept("enter", { case t : Token.ENTER => t })

  private def entry = accept("entry", { case t : Token.ENTRY => t })

  private def enumeration = accept("enum", { case t : Token.ENUM => t })

  private def eol = accept("end of line", { case t : Token.EOL => t })

  private def equals = accept("=", { case t : Token.EQUALS => t })

  private def event = accept("event", { case t : Token.EVENT => t })

  private def exit = accept("exit", { case t : Token.EXIT => t })

  private def falseToken = accept("false", { case t : Token.FALSE => t })

  private def fatal = accept("fatal", { case t : Token.FATAL => t })

  private def format = accept("format", { case t : Token.FORMAT => t })

  private def fppMatch = accept("match", { case t : Token.MATCH => t })

  private def fppWith = accept("with", { case t : Token.WITH => t })

  private def get = accept("get", { case t : Token.GET => t })

  private def guard = accept("guard", { case t : Token.GUARD => t })

  private def guarded = accept("guarded", { case t : Token.GUARDED => t })

  private def health = accept("health", { case t : Token.HEALTH => t })

  private def high = accept("high", { case t : Token.HIGH => t })

  private def hook = accept("hook", { case t : Token.HOOK => t })

  private def id = accept("id", { case t : Token.ID => t })

  private def ident: Parser[Ast.Ident] =
    accept("identifier", { case Token.IDENTIFIER(s) => s })

  private def ifToken = accept("if", { case t : Token.IF => t })

  private def importToken = accept("import", { case t : Token.IMPORT => t })

  private def include = accept("include", { case t : Token.INCLUDE => t })

  private def initial = accept("initial", { case t : Token.INITIAL => t })

  private def input = accept("input", { case t : Token.INPUT => t })

  private def instance = accept("instance", { case t : Token.INSTANCE => t })

  private def internal = accept("internal", { case t : Token.INTERNAL => t })

  private def junction = accept("junction", { case t : Token.JUNCTION => t })

  private def lbrace = accept("{", { case t : Token.LBRACE => t })

  private def lbracket = accept("[", { case t : Token.LBRACKET => t })

  private def literalFloat: Parser[String] =
    accept("floating-point literal", { case Token.LITERAL_FLOAT(s) => s })

  private def literalInt: Parser[String] =
    accept("integer literal", { case Token.LITERAL_INT(s) => s })

  private def literalString: Parser[String] =
    accept("string literal", { case Token.LITERAL_STRING(s) => s })

  private def locate = accept("locate", { case t : Token.LOCATE => t })

  private def low = accept("low", { case t : Token.LOW => t })

  private def lparen = accept("(", { case t : Token.LPAREN => t })

  private def machine = accept("machine", { case t : Token.MACHINE => t })

  private def minus = accept("-", { case t : Token.MINUS => t })

  private def module = accept("module", { case t : Token.MODULE => t })

  private def on = accept("on", { case t : Token.ON => t })

  private def opcode = accept("opcode", { case t : Token.OPCODE => t })

  private def orange = accept("orange", { case t : Token.ORANGE => t })

  private def output = accept("output", { case t : Token.OUTPUT => t })

  private def param = accept("param", { case t : Token.PARAM => t })

  private def passive = accept("passive", { case t : Token.PASSIVE => t })

  private def phase = accept("phase", { case t : Token.PHASE => t })

  private def plus = accept("+", { case t : Token.PLUS => t })

  private def port = accept("port", { case t : Token.PORT => t })

  private def postAnnotation: Parser[String] =
    accept("post annotation", { case Token.POST_ANNOTATION(s) => s })

  private def preAnnotation: Parser[String] =
    accept("pre annotation", { case Token.PRE_ANNOTATION(s) => s })

  private def priority = accept("priority", { case t : Token.PRIORITY => t })

  private def product = accept("product", { case t : Token.PRODUCT => t })

  private def queue = accept("queue", { case t : Token.QUEUE => t })

  private def queued = accept("queued", { case t : Token.QUEUED => t })

  private def rarrow = accept("->", { case t : Token.RARROW => t })

  private def rbrace = accept("}", { case t : Token.RBRACE => t })

  private def rbracket = accept("]", { case t : Token.RBRACKET => t })

  private def record = accept("record", { case t : Token.RECORD => t })

  private def recv = accept("recv", { case t : Token.RECV => t })

  private def red = accept("red", { case t : Token.RED => t })

  private def ref = accept("ref", { case t : Token.REF => t })

  private def reg = accept("reg", { case t : Token.REG => t })

  private def request = accept("request", { case t : Token.REQUEST => t })

  private def resp = accept("resp", { case t : Token.RESP => t })

  private def rparen = accept(")", { case t : Token.RPAREN => t })

  private def save = accept("save", { case t : Token.SAVE => t })

  private def semi = accept(";", { case t : Token.SEMI => t })

  private def send = accept("send", { case t : Token.SEND => t })

  private def serial = accept("serial", { case t : Token.SERIAL => t })

  private def set = accept("set", { case t : Token.SET => t })

  private def severity = accept("severity", { case t : Token.SEVERITY => t })

  private def signal = accept("signal", { case t : Token.SIGNAL => t })

  private def size = accept("size", { case t : Token.SIZE => t })

  private def slash = accept("/", { case t : Token.SLASH => t })

  private def stack = accept("stack", { case t : Token.STACK => t })

  private def star = accept("*", { case t : Token.STAR => t  })

  private def state = accept("state", { case t : Token.STATE => t })

  private def string = accept("string", { case t : Token.STRING => t })

  private def struct = accept("struct", { case t : Token.STRUCT => t })

  private def sync = accept("sync", { case t : Token.SYNC => t })

  private def telemetry = accept("telemetry", { case t : Token.TELEMETRY => t })

  private def text = accept("text", { case t : Token.TEXT => t })

  private def throttle = accept("throttle", { case t : Token.THROTTLE => t })

  private def time = accept("time", { case t : Token.TIME => t })

  private def topology = accept("topology", { case t : Token.TOPOLOGY => t })

  private def trueToken = accept("true", { case t : Token.TRUE => t })

  private def typeToken = accept("type", { case t : Token.TYPE => t })

  private def update = accept("update", { case t : Token.UPDATE => t })

  private def warning = accept("warning", { case t : Token.WARNING => t })

  private def yellow = accept("yellow", { case t : Token.YELLOW => t })

  /** The first error seen */
  private var error: Option[Error] = None

}
