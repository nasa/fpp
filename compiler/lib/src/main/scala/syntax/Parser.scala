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
    active ^^ (_ => Ast.ComponentKind.Active) |
      passive ^^ (_ => Ast.ComponentKind.Passive) |
      queued ^^ (_ => Ast.ComponentKind.Queued) |
      failure("component kind expected")
  }

  private def componentMemberNode: Parser[Ast.ComponentMember.Node] = {
    node(defAliasType) ^^ (n => Ast.ComponentMember.DefAliasType(n)) |
      node(defAbsType) ^^ (n => Ast.ComponentMember.DefAbsType(n)) |
      node(defArray) ^^ (n => Ast.ComponentMember.DefArray(n)) |
      node(defConstant) ^^ (n => Ast.ComponentMember.DefConstant(n)) |
      node(defEnum) ^^ (n => Ast.ComponentMember.DefEnum(n)) |
      node(defStateMachine) ^^ (n =>
        Ast.ComponentMember.DefStateMachine(n)) |
      node(defStruct) ^^ (n => Ast.ComponentMember.DefStruct(n)) |
      node(specCommand) ^^ (n => Ast.ComponentMember.SpecCommand(n)) |
      node(specContainer) ^^ (n =>
        Ast.ComponentMember.SpecContainer(n)) |
      node(specEvent) ^^ (n => Ast.ComponentMember.SpecEvent(n)) |
      node(specInclude) ^^ (n => Ast.ComponentMember.SpecInclude(n)) |
      node(specInternalPort) ^^ (n =>
        Ast.ComponentMember.SpecInternalPort(n)) |
      node(specPortInstance) ^^ (n =>
        Ast.ComponentMember.SpecPortInstance(n)) |
      node(specPortMatching) ^^ (n =>
        Ast.ComponentMember.SpecPortMatching(n)) |
      node(specParam) ^^ (n => Ast.ComponentMember.SpecParam(n)) |
      node(specRecord) ^^ (n => Ast.ComponentMember.SpecRecord(n)) |
      node(specStateMachineInstance) ^^ (n =>
        Ast.ComponentMember.SpecStateMachineInstance(n)) |
      node(specTlmChannel) ^^ (n =>
        Ast.ComponentMember.SpecTlmChannel(n)) |
      node(specImport) ^^ (n =>
        Ast.ComponentMember.SpecImportInterface(n)) |
      failure("component member expected")
  }

  def componentMembers: Parser[List[Ast.ComponentMember]] =
    annotatedElementSequence(componentMemberNode, semi, Ast.ComponentMember(_))

  def connection: Parser[Ast.SpecConnectionGraph.Connection] = {
    def connectionPort = node(portInstanceIdentifier) ~! opt(index)

    opt(unmatched) ~ connectionPort ~! (rarrow ~>! connectionPort) ^^ {
      case unmatched ~ (fromPort ~ fromIndex) ~ (toPort ~ toIndex) =>
        Ast.SpecConnectionGraph.Connection(
          unmatched.isDefined,
          fromPort,
          fromIndex,
          toPort,
          toIndex
        )
    }
  }

  def defAliasType: Parser[Ast.DefAliasType] = {
    (opt(dictionary) ~ (typeToken ~> ident) ~ (equals ~> node(typeName))) ^^ {
      case dictionary ~ ident ~ typeName =>
        Ast.DefAliasType(ident, typeName, dictionary.isDefined)
    }
  }

  def defAbsType: Parser[Ast.DefAbsType] = {
    (typeToken ~> ident) ^^ (id => Ast.DefAbsType(id))
  }

  private def defAction: Parser[Ast.DefAction] = {
    (action ~> ident) ~! opt(colon ~>! node(typeName)) ^^ {
      case ident ~ typeName => Ast.DefAction(ident, typeName)
    }
  }

  def defArray: Parser[Ast.DefArray] = {
    opt(dictionary) ~ (array ~>! ident <~! equals) ~!
      index ~! node(typeName) ~!
      opt(default ~>! exprNode) ~!
      opt(format ~>! node(literalString)) ^^ {
      case dictionary ~ name ~ size ~ eltType ~ default ~ format =>
        Ast.DefArray(name, size, eltType, default, format, dictionary.isDefined)
    }
  }

  private def defChoice: Parser[Ast.DefChoice] = {
    (choice ~> ident) ~! (lbrace ~> ifToken ~> node(ident)) ~! node(
      transitionExpr
    ) ~!
      (elseToken ~> node(transitionExpr)) <~! rbrace ^^ {
      case ident ~ guard ~ ifTransition ~ elseTransition =>
        Ast.DefChoice(ident, guard, ifTransition, elseTransition)
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

      opt(
        lbrace ~>! annotatedElementSequence(node(specInit), semi, id) <~! rbrace
      ) ^^ {
        case Some(elements) => elements
        case None => Nil
      }
    }

    (instance ~>! ident) ~! (colon ~>! node(
      qualIdent
    )) ~! (base ~! id ~>! exprNode) ~!
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

  private def interfaceMemberNode: Parser[Ast.InterfaceMember.Node] = {
      node(specPortInstance) ^^ (n =>
        Ast.InterfaceMember.SpecPortInstance(n)) |
      node(specImport) ^^ (n =>
        Ast.InterfaceMember.SpecImportInterface(n)) |
      failure("component member expected")
  }

  def interfaceMembers: Parser[List[Ast.InterfaceMember]] =
    annotatedElementSequence(interfaceMemberNode, semi, Ast.InterfaceMember(_))

  def defInterface: Parser[Ast.DefInterface] = {
    (interface ~>! ident) ~! (lbrace ~>! interfaceMembers <~! rbrace) ^^ {
      case name ~ members => Ast.DefInterface(name, members)
    }
  }

  def defConstant: Parser[Ast.DefConstant] = {
    opt(dictionary) ~ (constant ~>! ident) ~! (equals ~>! exprNode) ^^ {
      case dictionary ~ id ~ e =>
        Ast.DefConstant(id, e, dictionary.isDefined)
    }
  }

  def defEnum: Parser[Ast.DefEnum] = {
    def id(x: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = x

    def constants = annotatedElementSequence(node(defEnumConstant), comma, id)

    opt(dictionary) ~ (enumeration ~>! ident) ~!
      opt(colon ~>! node(typeName)) ~!
      (lbrace ~>! constants <~! rbrace) ~!
      opt(default ~>! exprNode) ^^ {
      case dictionary ~ name ~ typeName ~ constants ~ default =>
        Ast.DefEnum(name, typeName, constants, default, dictionary.isDefined)
    }
  }

  def defEnumConstant: Parser[Ast.DefEnumConstant] = {
    ident ~! opt(equals ~>! exprNode) ^^ { case id ~ e =>
      Ast.DefEnumConstant(id, e)
    }
  }

  private def defGuard: Parser[Ast.DefGuard] = {
    (guard ~> ident) ~! opt(colon ~>! node(typeName)) ^^ {
      case ident ~ typeName => Ast.DefGuard(ident, typeName)
    }
  }

  def defModule: Parser[Ast.DefModule] = {
    (module ~>! ident) ~! (lbrace ~>! moduleMembers <~! rbrace) ^^ {
      case name ~ members => Ast.DefModule(name, members)
    }
  }

  def defPort: Parser[Ast.DefPort] = {
    (port ~>! ident) ~! formalParamList ~! opt(rarrow ~>! node(typeName)) ^^ {
      case ident ~ formalParamList ~ returnType =>
        Ast.DefPort(ident, formalParamList, returnType)
    }
  }

  private def defSignal: Parser[Ast.DefSignal] = {
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
    state ~> (machine ~> ident) ~! opt(
      lbrace ~>! stateMachineMembers <~! rbrace
    ) ^^ { case name ~ members =>
      Ast.DefStateMachine(name, members)
    }
  }

  def defStruct: Parser[Ast.DefStruct] = {
    def id(x: Ast.Annotated[AstNode[Ast.StructTypeMember]]) = x

    def members = annotatedElementSequence(node(structTypeMember), comma, id)

    opt(dictionary) ~ (struct ~>! ident) ~! (lbrace ~>! members <~! rbrace) ~! opt(
      default ~>! exprNode
    ) ^^ { case dictionary ~ name ~ members ~ default =>
      Ast.DefStruct(name, members, default, dictionary.isDefined)
    }
  }

  def specTlmPacketSet: Parser[Ast.SpecTlmPacketSet] = {
    def omitted: Parser[List[AstNode[Ast.TlmChannelIdentifier]]] = {
      opt(
        omit ~>! lbrace ~>! elementSequence(
          node(tlmChannelIdentifier),
          comma
        ) <~! rbrace
      ) ^^ {
        case Some(elements) => elements
        case None => Nil
      }
    }

    (telemetry ~> packets) ~>! ident ~! (lbrace ~>! tlmPacketSetMembers <~! rbrace) ~! omitted ^^ {
      case name ~ members ~ omitted =>
        Ast.SpecTlmPacketSet(name, members, omitted)
    }
  }

  def defTopology: Parser[Ast.DefTopology] = {
    (topology ~>! ident) ~! opt(implements ~>! elementSequence(node(qualIdent), comma)) ~! (lbrace ~>! topologyMembers <~! rbrace) ^^ {
      case name ~ Some(implements) ~ members => Ast.DefTopology(name, members, implements)
      case name ~ None ~ members => Ast.DefTopology(name, members, Nil)
    }
  }

  private def doExpr: Parser[List[AstNode[Ast.Ident]]] = {
    def elts = elementSequence(node(ident), comma)

    doToken ~>! lbrace ~>! elts <~! rbrace ^^ (elts => elts)
  }

  def exprNode: Parser[AstNode[Ast.Expr]] = {
    def leftAssoc(e: AstNode[Ast.Expr], es: List[Token ~ AstNode[Ast.Expr]]) = {
      def f(e1: AstNode[Ast.Expr], op_e2: Token ~ AstNode[Ast.Expr]) = {
        val op ~ e2 = op_e2
        val binop =
          op match {
            case Token.MINUS() =>
              AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Sub, e2))
            case Token.PLUS() =>
              AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Add, e2))
            case Token.SLASH() =>
              AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Div, e2))
            case Token.STAR() =>
              AstNode.create(Ast.ExprBinop(e1, Ast.Binop.Mul, e2))
            case _ => throw new InternalError(s"invalid binary operator ${op}")
          }
        val loc = Location(ParserState.file, op.pos, ParserState.includingLoc)
        Locations.put(binop.id, loc)
        binop
      }

      es.foldLeft(e)(f)
    }

    def primaryExpr = node {
      def arrayExpr =
        lbracket ~>! elementSequence(exprNode, comma) <~! rbracket ^^ (es => Ast.ExprArray(es))

      def falseExpr = falseToken ^^ (_ =>
        Ast.ExprLiteralBool(Ast.LiteralBool.False))

      def floatExpr = literalFloat ^^ (s => Ast.ExprLiteralFloat(s))

      def identExpr = ident ^^ (id => Ast.ExprIdent(id))

      def intExpr = literalInt ^^ (li => Ast.ExprLiteralInt(li))

      def parenExpr = lparen ~> exprNode <~ rparen ^^ (e =>
        Ast.ExprParen(e))

      def stringExpr = literalString ^^ (s => Ast.ExprLiteralString(s))

      def structMember = ident ~! (equals ~>! exprNode) ^^ { case id ~ e =>
        Ast.StructMember(id, e)
      }

      def structExpr =
        lbrace ~>! elementSequence(node(structMember), comma) <~! rbrace ^^ (es => Ast.ExprStruct(es))

      def trueExpr = trueToken ^^ (_ =>
        Ast.ExprLiteralBool(Ast.LiteralBool.True))

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

    def postFixExpr =
      def memberSelectors(
                        e: AstNode[Ast.Expr],
                        ss: List[AstNode[String | Ast.Expr]]
                      ) = {
        def f(e: AstNode[Ast.Expr], s: AstNode[String | Ast.Expr]) = {
          val o = s.data match {
            // Array subscript
            case i: Ast.Expr => Ast.ExprArraySubscript(e, AstNode.create(i, s.id))

            // Member/Dot
            case id: String => Ast.ExprDot(e, AstNode.create(id, s.id))
          }

          val node = AstNode.create(o)
          val loc = Locations.get(e.id)
          Locations.put(node.id, loc)
          node
        }

        ss.foldLeft(e)(f)
      }

      primaryExpr ~ rep(dot ~> node(ident) | (lbracket ~> exprNode <~! rbracket)) ^^ { case e ~ ss =>
        memberSelectors(e, ss)
      }

    def unaryMinus = node {
      minus ~>! postFixExpr ^^ (e =>
        Ast.ExprUnop(Ast.Unop.Minus, e))
    }

    def mulDivOperand = unaryMinus | postFixExpr

    def addSubOperand =
      mulDivOperand ~ rep((star | slash) ~! mulDivOperand) ^^ { case e ~ es =>
        leftAssoc(e, es)
      }

    addSubOperand ~ rep((plus | minus) ~! addSubOperand) ^^ { case e ~ es =>
      leftAssoc(e, es)
    }
  }

  def formalParam: Parser[Ast.FormalParam] = {
    def kind = {
      opt(ref) ^^ {
        case Some(_) => Ast.FormalParam.Ref
        case None => Ast.FormalParam.Value
      }
    }

    kind ~ ident ~! (colon ~>! node(typeName)) ^^ { case kind ~ id ~ tn =>
      Ast.FormalParam(kind, id, tn)
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

  def moduleMemberNode: Parser[Ast.ModuleMember.Node] = {
    node(defAliasType) ^^ (n => Ast.ModuleMember.DefAliasType(n)) |
      node(defAbsType) ^^ (n => Ast.ModuleMember.DefAbsType(n)) |
      node(defArray) ^^ (n => Ast.ModuleMember.DefArray(n)) |
      node(defComponent) ^^ (n => Ast.ModuleMember.DefComponent(n)) |
      node(defInterface) ^^ (n => Ast.ModuleMember.DefInterface(n)) |
      node(defComponentInstance) ^^ (n =>
        Ast.ModuleMember.DefComponentInstance(n)) |
      node(defConstant) ^^ (n => Ast.ModuleMember.DefConstant(n)) |
      node(defEnum) ^^ (n => Ast.ModuleMember.DefEnum(n)) |
      node(defModule) ^^ (n => Ast.ModuleMember.DefModule(n)) |
      node(defPort) ^^ (n => Ast.ModuleMember.DefPort(n)) |
      node(defStateMachine) ^^ (n =>
        Ast.ModuleMember.DefStateMachine(n)) |
      node(defStruct) ^^ (n => Ast.ModuleMember.DefStruct(n)) |
      node(defTopology) ^^ (n => Ast.ModuleMember.DefTopology(n)) |
      node(specInclude) ^^ (n => Ast.ModuleMember.SpecInclude(n)) |
      node(specLoc) ^^ (n => Ast.ModuleMember.SpecLoc(n)) |
      failure("module member expected")
  }

  def moduleMembers: Parser[List[Ast.ModuleMember]] =
    annotatedElementSequence(moduleMemberNode, semi, Ast.ModuleMember(_))

  def node[T](p: Parser[T]): Parser[AstNode[T]] = {
    final case class Positioned(t: T) extends Positional

    def positionedT: Parser[Positioned] = positioned {
      p ^^ (x => Positioned(x))
    }

    positionedT ^^ {
      case pt@Positioned(t) =>
        val n = AstNode.create(t)
        val loc = Location(ParserState.file, pt.pos, ParserState.includingLoc)
        Locations.put(n.id, loc)
        n
    }
  }

  def parseAllInput[T](p: Parser[T]): Parser[T] = (in: Input) => {
    val r = p(in)
    r match {
      case s@Success(out, in1) =>
        error match {
          case Some(e) => e
          case None => if (in1.atEnd) s else Failure("unexpected token", in1)
        }
      case other => other
    }
  }

  def parseFile[T](
                    p: Parser[T]
                  )(includingLoc: Option[Location])(f: File): Result.Result[T] = {
    ParserState.file = f
    ParserState.includingLoc = includingLoc

    for {
      // Read the file
      file <- f.openRead(includingLoc)

      ctx = Context()

      // Parse the file
      ast <- parseTokens(p)(new Lexer.Scanner(f, file, includingLoc)(using ctx))
    } yield ast
  }

  def parseString[T](p: Parser[T])(s: String): Result.Result[T] = {
    val ctx = Context()
    for {
      ast <- parseTokens(p)(new Lexer.Scanner(ParserState.file, s.toCharArray)(using ctx))
    } yield ast
  }

  private def parseTokens[T](p: Parser[T])(scanner: Lexer.Scanner): Result.Result[T] = {
    error = None
    val tokens = scanner.list() match {
      case Left(err) => return Left(err)
      case Right(t) => t
    }

    val reader = new TokenReader(tokens)
    parseAllInput(p)(reader) match {
      case NoSuccess(msg, next) =>
        val loc = Location(ParserState.file, next.pos, ParserState.includingLoc)
        Left(SyntaxError(loc, msg))
      case Success(result, next) => Right(result)
      // Suppress false compiler warning
      case _ => throw new InternalError("This cannot happen")
    }
  }

  def portInstanceIdentifier: Parser[Ast.PortInstanceIdentifier] =
    node(ident) ~! (dot ~>! qualIdentNodeList) ^^ {
      case id ~ qid =>
        val portName :: tail = qid.reverse
        val componentInstance = id :: tail.reverse
        val node = Ast.QualIdent.Node.fromNodeList(componentInstance)
        Ast.PortInstanceIdentifier(node, portName)
    }

  def qualIdent: Parser[Ast.QualIdent] =
    qualIdentNodeList ^^ (qid => Ast.QualIdent.fromNodeList(qid))

  private def qualIdentNodeList: Parser[Ast.QualIdent.NodeList] =
    rep1sep(node(ident), dot)

  def queueFull: Parser[Ast.QueueFull] = {
    assert ^^ (_ => Ast.QueueFull.Assert) |
      block ^^ (_ => Ast.QueueFull.Block) |
      drop ^^ (_ => Ast.QueueFull.Drop) |
      hook ^^ (_ => Ast.QueueFull.Hook) |
      failure("queue full expected")
  }

  def specCommand: Parser[Ast.SpecCommand] = {
    def kind = {
      async ^^ (_ => Ast.SpecCommand.Async) |
        guarded ^^ (_ => Ast.SpecCommand.Guarded) |
        sync ^^ (_ => Ast.SpecCommand.Sync) |
        failure("command kind expected")
    }

    kind ~ (command ~> ident) ~! formalParamList ~!
      opt(opcode ~>! exprNode) ~! opt(priority ~>! exprNode) ~! opt(
      node(queueFull)
    ) ^^ { case kind ~ name ~ params ~ opcode ~ priority ~ queueFull =>
      Ast.SpecCommand(kind, name, params, opcode, priority, queueFull)
    }
  }

  def specCompInstance: Parser[Ast.SpecCompInstance] = {
    instance ~>! node(qualIdent) ^^ {
      case instance => Ast.SpecCompInstance(instance)
    }
  }

  def specConnectionGraph: Parser[Ast.SpecConnectionGraph] = {
    def directGraph = {
      (connections ~> ident) ~! (lbrace ~>! elementSequence(
        connection,
        comma
      ) <~! rbrace) ^^ { case ident ~ connections =>
        Ast.SpecConnectionGraph.Direct(ident, connections)
      }
    }

    def patternGraph = {
      def patternKind = {
        command ^^ (_ => Ast.SpecConnectionGraph.Pattern.Command) |
          event ^^ (_ => Ast.SpecConnectionGraph.Pattern.Event) |
          health ^^ (_ => Ast.SpecConnectionGraph.Pattern.Health) |
          param ^^ (_ => Ast.SpecConnectionGraph.Pattern.Param) |
          telemetry ^^ (_ => Ast.SpecConnectionGraph.Pattern.Telemetry) |
          text ~ event ^^ (_ =>
            Ast.SpecConnectionGraph.Pattern.TextEvent) |
          time ^^ (_ => Ast.SpecConnectionGraph.Pattern.Time)
      }

      def instanceSequence = {
        opt(lbrace ~>! elementSequence(node(qualIdent), comma) <~! rbrace) ^^ {
          case Some(elements) => elements
          case None => Nil
        }
      }

      patternKind ~ (connections ~! instance ~>! node(
        qualIdent
      )) ~! instanceSequence ^^ { case kind ~ source ~ targets =>
        Ast.SpecConnectionGraph.Pattern(
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
      case name ~ id ~ defaultPriority =>
        Ast.SpecContainer(
          name,
          id,
          defaultPriority
        )
    }
  }

  def specEvent: Parser[Ast.SpecEvent] = {
    def severityLevel = {
      activity ~ high ^^ (_ => Ast.SpecEvent.ActivityHigh) |
        activity ~ low ^^ (_ => Ast.SpecEvent.ActivityLow) |
        command ^^ (_ => Ast.SpecEvent.Command) |
        diagnostic ^^ (_ => Ast.SpecEvent.Diagnostic) |
        fatal ^^ (_ => Ast.SpecEvent.Fatal) |
        warning ~ high ^^ (_ => Ast.SpecEvent.WarningHigh) |
        warning ~ low ^^ (_ => Ast.SpecEvent.WarningLow) |
        failure("severity level expected")
    }

    def throttleClause = {
      (throttle ~>! exprNode) ~! opt(every ~>! exprNode) ^^ {
        case throttle ~ duration => Ast.EventThrottle(throttle, duration)
      }
    }

    (event ~> ident) ~! formalParamList ~! (severity ~>! severityLevel) ~!
      opt(id ~>! exprNode) ~!
      (format ~>! node(literalString)) ~!
      opt(node(throttleClause)) ^^ {
      case name ~ params ~ severity ~ id ~ format ~ throttle =>
        Ast.SpecEvent(name, params, severity, id, format, throttle)
    }
  }

  def specInclude: Parser[Ast.SpecInclude] = {
    include ~>! node(literalString) ^^ (file => Ast.SpecInclude(file))
  }

  def specInit: Parser[Ast.SpecInit] = {
    (phase ~>! exprNode) ~! literalString ^^ { case phase ~ code =>
      Ast.SpecInit(phase, code)
    }
  }

  private def specInitialTransition: Parser[Ast.SpecInitialTransition] = {
    initial ~> node(transitionExpr) ^^ (transition =>
      Ast.SpecInitialTransition(transition))
  }

  private def specStateEntry: Parser[Ast.SpecStateEntry] = {
    entry ~> doExpr ^^ (actions =>
      Ast.SpecStateEntry(actions))
  }

  private def specStateExit: Parser[Ast.SpecStateExit] = {
    exit ~> doExpr ^^ (actions =>
      Ast.SpecStateExit(actions))
  }

  def specInternalPort: Parser[Ast.SpecInternalPort] = {
    (internal ~! port ~>! ident) ~! formalParamList ~!
      opt(priority ~>! exprNode) ~!
      opt(queueFull) ^^ { case name ~ params ~ priority ~ queueFull =>
      Ast.SpecInternalPort(name, params, priority, queueFull)
    }
  }

  def specLoc: Parser[Ast.SpecLoc] =
    def maybeDictKind =
      constant ^^ (_ => Ast.SpecLoc.Constant) |
      typeToken ^^ (_ => Ast.SpecLoc.Type)
    def nonDictKind =
      component ^^ (_ => Ast.SpecLoc.Component) |
      instance ^^ (_ => Ast.SpecLoc.ComponentInstance) |
      port ^^ (_ => Ast.SpecLoc.Port) |
      state ~! machine ^^ (_ => Ast.SpecLoc.StateMachine) |
      topology ^^ (_ => Ast.SpecLoc.Topology) |
      interface ^^ (_ => Ast.SpecLoc.Interface)
    def maybeDictPair =
      opt(dictionary) ~ maybeDictKind ^^ {
        case dictOpt ~ kind => (dictOpt.isDefined, kind)
      }
    def nonDictPair =
      nonDictKind ^^ { case kind => (false, kind) }
    def isDictAndKind =
      maybeDictPair |
      nonDictPair |
      failure("dictionary specifier or location kind expected")
    (locate ~>! isDictAndKind) ~! node(qualIdent) ~! (at ~>! node(literalString)) ^^ {
      case (isDict, kind) ~ symbol ~ file => {
        Ast.SpecLoc(kind, symbol, file, isDict)
      }
    }

  def specParam: Parser[Ast.SpecParam] = {
    opt(external) ~ (param ~>! ident) ~ (colon ~>! node(typeName)) ~!
      opt(default ~>! exprNode) ~!
      opt(id ~>! exprNode) ~!
      opt(set ~! opcode ~>! exprNode) ~!
      opt(save ~! opcode ~>! exprNode) ^^ {
        case external ~ name ~ typeName ~ default ~ id ~ setOpcode ~ saveOpcode =>
          Ast.SpecParam(name, typeName, default, id, setOpcode, saveOpcode, external.isDefined)
    }
  }

  def specPortInstance: Parser[Ast.SpecPortInstance] = {
    def generalKind = {
      async ~ input ^^ (_ => Ast.SpecPortInstance.AsyncInput) |
        guarded ~ input ^^ (_ => Ast.SpecPortInstance.GuardedInput) |
        output ^^ (_ => Ast.SpecPortInstance.Output) |
        sync ~ input ^^ (_ => Ast.SpecPortInstance.SyncInput)
    }

    def instanceType = {
      node(qualIdent) ^^ (qi => Some(qi)) |
        serial ^^ (_ => None) |
        failure("port type expected")
    }

    def specialInputKind = {
      async ^^ (_ => Ast.SpecPortInstance.Async) |
        guarded ^^ (_ => Ast.SpecPortInstance.Guarded) |
        sync ^^ (_ => Ast.SpecPortInstance.Sync)
    }

    def specialKind = {
      command ~ recv ^^ (_ => Ast.SpecPortInstance.CommandRecv) |
        command ~ reg ^^ (_ => Ast.SpecPortInstance.CommandReg) |
        command ~ resp ^^ (_ => Ast.SpecPortInstance.CommandResp) |
        event ^^ (_ => Ast.SpecPortInstance.Event) |
        param ~ get ^^ (_ => Ast.SpecPortInstance.ParamGet) |
        param ~ set ^^ (_ => Ast.SpecPortInstance.ParamSet) |
        product ~ get ^^ (_ => Ast.SpecPortInstance.ProductGet) |
        product ~ recv ^^ (_ => Ast.SpecPortInstance.ProductRecv) |
        product ~ request ^^ (_ => Ast.SpecPortInstance.ProductRequest) |
        product ~ send ^^ (_ => Ast.SpecPortInstance.ProductSend) |
        telemetry ^^ (_ => Ast.SpecPortInstance.Telemetry) |
        text ~ event ^^ (_ => Ast.SpecPortInstance.TextEvent) |
        time ~ get ^^ (_ => Ast.SpecPortInstance.TimeGet)
    }

    def general = {
      generalKind ~! (port ~>! ident) ~!
        (colon ~>! opt(index)) ~!
        instanceType ~!
        opt(priority ~>! exprNode) ~!
        opt(node(queueFull)) ^^ {
        case kind ~ name ~ size ~ ty ~ priority ~ queueFull =>
          Ast.SpecPortInstance.General(
            kind,
            name,
            size,
            ty,
            priority,
            queueFull
          )
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
      opt(id ~>! exprNode) ^^ { case name ~ recordType ~ arrayOpt ~ id =>
      Ast.SpecRecord(
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
      opt(queueFull)) ^^ { case name ~ statemachine ~ priority ~ queueFull =>
      Ast.SpecStateMachineInstance(name, statemachine, priority, queueFull)
    }
  }

  def specTlmChannel: Parser[Ast.SpecTlmChannel] = {
    def updateSetting = {
      always ^^ (_ => Ast.SpecTlmChannel.Always) |
        on ~! change ^^ (_ => Ast.SpecTlmChannel.OnChange) |
        failure("update kind expected")
    }

    def kind = {
      orange ^^ (_ => Ast.SpecTlmChannel.Orange) |
        red ^^ (_ => Ast.SpecTlmChannel.Red) |
        yellow ^^ (_ => Ast.SpecTlmChannel.Yellow)
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
      case name ~ typeName ~ id ~ update ~ format ~ low ~ high =>
        Ast.SpecTlmChannel(
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

  def specTlmPacket: Parser[Ast.SpecTlmPacket] = {
    packet ~>! ident ~! opt(id ~>! exprNode) ~! (group ~>! exprNode) ~!
      (lbrace ~>! tlmPacketMembers <~! rbrace) ^^ {
      case name ~ id ~ group ~ members =>
        Ast.SpecTlmPacket(name, id, group, members)
    }
  }

  def specTopPort: Parser[Ast.SpecTopPort] =
    port ~>! ident ~! (equals ~>! node(qualIdent)) ^^ {
      case name ~ underlying => Ast.SpecTopPort(name, underlying)
    }

  def specImport: Parser[Ast.SpecImport] =
    importToken ~>! node(qualIdent) ^^ (top => Ast.SpecImport(top))

  private def specStateTransition: Parser[Ast.SpecStateTransition] = {
    (on ~> node(ident)) ~! opt(ifToken ~> node(ident)) ~ transitionOrDo ^^ {
      case signal ~ guard ~ transitionOrDo =>
        Ast.SpecStateTransition(signal, guard, transitionOrDo)
    }
  }

  private def stateMachineMemberNode: Parser[Ast.StateMachineMember.Node] = {
    node(specInitialTransition) ^^ (n =>
      Ast.StateMachineMember.SpecInitialTransition(n)) |
      node(defState) ^^ (n => Ast.StateMachineMember.DefState(n)) |
      node(defSignal) ^^ (n => Ast.StateMachineMember.DefSignal(n)) |
      node(defAction) ^^ (n => Ast.StateMachineMember.DefAction(n)) |
      node(defGuard) ^^ (n => Ast.StateMachineMember.DefGuard(n)) |
      node(defChoice) ^^ (n => Ast.StateMachineMember.DefChoice(n)) |
      failure("state machine member expected")
  }

  private def stateMachineMembers: Parser[List[Ast.StateMachineMember]] =
    annotatedElementSequence(
      stateMachineMemberNode,
      semi,
      Ast.StateMachineMember(_)
    )

  private def stateMemberNode: Parser[Ast.StateMember.Node] = {
    node(defChoice) ^^ (n => Ast.StateMember.DefChoice(n)) |
      node(defState) ^^ (n => Ast.StateMember.DefState(n)) |
      node(specInitialTransition) ^^ (n =>
        Ast.StateMember.SpecInitialTransition(n)) |
      node(specStateEntry) ^^ (n => Ast.StateMember.SpecStateEntry(n)) |
      node(specStateExit) ^^ (n => Ast.StateMember.SpecStateExit(n)) |
      node(specStateTransition) ^^ (n =>
        Ast.StateMember.SpecStateTransition(n)) |
      failure("state member expected")
  }

  private def stateMembers: Parser[List[Ast.StateMember]] =
    annotatedElementSequence(stateMemberNode, semi, Ast.StateMember(_))

  def structTypeMember: Parser[Ast.StructTypeMember] = {
    ident ~! (colon ~>! opt(index)) ~! node(typeName) ~! opt(
      format ~>! node(literalString)
    ) ^^ { case name ~ size ~ typeName ~ format =>
      Ast.StructTypeMember(name, size, typeName, format)
    }
  }

  def tlmChannelIdentifier: Parser[Ast.TlmChannelIdentifier] =
    node(ident) ~! (dot ~>! qualIdentNodeList) ^^ {
      case id ~ qid =>
        val channelName :: tail = qid.reverse
        val componentInstance = id :: tail.reverse
        val node = Ast.QualIdent.Node.fromNodeList(componentInstance)
        Ast.TlmChannelIdentifier(node, channelName)
    }

  private def tlmPacketSetMemberNode: Parser[Ast.TlmPacketSetMember.Node] = {
    node(specInclude) ^^ (n => Ast.TlmPacketSetMember.SpecInclude(n)) |
      node(specTlmPacket) ^^ (n =>
        Ast.TlmPacketSetMember.SpecTlmPacket(n)) |
      failure("telemetry packet set member expected")
  }

  def tlmPacketSetMembers: Parser[List[Ast.TlmPacketSetMember]] =
    annotatedElementSequence(
      tlmPacketSetMemberNode,
      comma,
      Ast.TlmPacketSetMember(_)
    )

  def tlmPacketMember: Parser[Ast.TlmPacketMember] = {
    node(specInclude) ^^ (n => Ast.TlmPacketMember.SpecInclude(n)) |
      node(tlmChannelIdentifier) ^^ (n =>
        Ast.TlmPacketMember.TlmChannelIdentifier(n)) |
      failure("telemetry packet member expected")
  }

  def tlmPacketMembers: Parser[List[Ast.TlmPacketMember]] =
    elementSequence(tlmPacketMember, comma)

  private def topologyMemberNode: Parser[Ast.TopologyMember.Node] = {
    node(specCompInstance) ^^ (n =>
      Ast.TopologyMember.SpecCompInstance(n)) |
      node(specConnectionGraph) ^^ (n =>
        Ast.TopologyMember.SpecConnectionGraph(n)) |
      node(specInclude) ^^ (n => Ast.TopologyMember.SpecInclude(n)) |
      node(specTopPort) ^^ (n => Ast.TopologyMember.SpecTopPort(n)) |
      node(specTlmPacketSet) ^^ (n =>
        Ast.TopologyMember.SpecTlmPacketSet(n)) |
      node(specImport) ^^ (n => Ast.TopologyMember.SpecTopImport(n)) |
      failure("topology member expected")
  }

  def topologyMembers: Parser[List[Ast.TopologyMember]] =
    annotatedElementSequence(topologyMemberNode, semi, Ast.TopologyMember(_))

  def transUnit: Parser[Ast.TransUnit] = {
    tuMembers ^^ (members => Ast.TransUnit(members))
  }

  def transitionExpr: Parser[Ast.TransitionExpr] =
    opt(doExpr) ~ (enter ~> node(qualIdent)) ^^ { case actionsOpt ~ target =>
      Ast.TransitionExpr(
        actionsOpt.getOrElse(Nil),
        target
      )
    }

  def transitionOrDo: Parser[Ast.TransitionOrDo] = {
    def transitionParser: Parser[Ast.TransitionOrDo.Transition] =
      node(transitionExpr) ^^ (e =>
        Ast.TransitionOrDo.Transition(e))

    def doParser: Parser[Ast.TransitionOrDo.Do] = doExpr ^^ (actions =>
      Ast.TransitionOrDo.Do(actions))

    transitionParser | doParser
  }

  private def tuMembers = moduleMembers

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
      string ~> opt(size ~>! exprNode) ^^ (e => Ast.TypeNameString(e)) |
      typeNameFloat |
      typeNameInt |
      node(qualIdent) ^^ (qid => Ast.TypeNameQualIdent(qid)) |
      failure("type name expected")
  }

  override def commit[T](p: => Parser[T]) = Parser { in =>
    def setError(e: Error) = {
      error match {
        case None => error = Some(e)
        case Some(_) => ()
      }
      e
    }

    val r = p(in)
    r match {
      case s@Success(_, _) => s
      case e: Error => setError(e)
      case Failure(msg, next) => setError(Error(msg, next))
    }
  }

  override type Elem = Token

  private def action = accept("action", { case t: Token.ACTION => t })

  private def active = accept("active", { case t: Token.ACTIVE => t })

  private def activity = accept("activity", { case t: Token.ACTIVITY => t })

  private def always = accept("always", { case t: Token.ALWAYS => t })

  private def annotatedElementSequence[E, S, T](
                                                 elt: Parser[E],
                                                 punct: Parser[S],
                                                 constructor: Ast.Annotated[E] => T
                                               ): Parser[List[T]] = {
    def terminator = punct | eol

    def prefix0 = elt ^^ (elt => (Nil, elt))

    def prefix1 = rep1(preAnnotation) ~! elt ^^ { case al ~ elt => (al, elt) }

    def prefix = prefix0 | prefix1

    def punctTerminatedElt = (prefix <~ terminator) ~ rep(postAnnotation) ^^ {
      case (al1, elt) ~ al2 => (al1, elt, al2)
    }

    def annotationTerminatedElt = prefix ~ rep1(postAnnotation) ^^ {
      case (al1, elt) ~ al2 => (al1, elt, al2)
    }

    def terminatedElt = punctTerminatedElt | annotationTerminatedElt

    def unterminatedElt = rep(preAnnotation) ~ elt ^^ { case al ~ elt =>
      (al, elt, Nil)
    }

    def elts = rep(terminatedElt) ~ opt(unterminatedElt) ^^ {
      case elts ~ Some(elt) => elts :+ elt
      case elts ~ None => elts
    }

    (rep(eol) ~> elts) ^^ { elts => elts.map(constructor) }
  }

  private def array = accept("array", { case t: Token.ARRAY => t })

  private def assert = accept("assert", { case t: Token.ASSERT => t })

  private def async = accept("async", { case t: Token.ASYNC => t })

  private def at = accept("at", { case t: Token.AT => t })

  private def base = accept("base", { case t: Token.BASE => t })

  private def block = accept("block", { case t: Token.BLOCK => t })

  private def change = accept("change", { case t: Token.CHANGE => t })

  private def choice = accept("choice", { case t: Token.CHOICE => t })

  private def lbrace = accept("{", { case t: Token.LBRACE => t })

  private def colon = accept(":", { case t: Token.COLON => t })

  private def comma = accept(",", { case t: Token.COMMA => t })

  private def command = accept("command", { case t: Token.COMMAND => t })

  private def component = accept("component", { case t: Token.COMPONENT => t })

  private def connections =
    accept("connections", { case t: Token.CONNECTIONS => t })

  private def constant = accept("constant", { case t: Token.CONSTANT => t })

  private def container = accept("container", { case t: Token.CONTAINER => t })

  private def cpu = accept("cpu", { case t: Token.CPU => t })

  private def default = accept("default", { case t: Token.DEFAULT => t })

  private def diagnostic =
    accept("diagnostic", { case t: Token.DIAGNOSTIC => t })

  private def dictionary = accept("dictionary", { case t: Token.DICTIONARY => t })

  private def doToken = accept("do", { case t: Token.DO => t })

  private def dot = accept(".", { case t: Token.DOT => t })

  private def drop = accept("drop", { case t: Token.DROP => t })

  private def elementSequence[E, S](
                                     elt: Parser[E],
                                     sep: Parser[S]
                                   ): Parser[List[E]] =
    repsep(elt, sep | eol) <~ opt(sep | eol)

  private def elseToken = accept("else", { case t: Token.ELSE => t })

  private def enter = accept("enter", { case t: Token.ENTER => t })

  private def entry = accept("entry", { case t: Token.ENTRY => t })

  private def enumeration = accept("enum", { case t: Token.ENUM => t })

  private def eol = accept("end of line", { case t: Token.EOL => t })

  private def equals = accept("=", { case t: Token.EQUALS => t })

  private def event = accept("event", { case t: Token.EVENT => t })

  private def every = accept("every", { case t: Token.EVERY => t })

  private def exit = accept("exit", { case t: Token.EXIT => t })

  private def external = accept("external", { case t : Token.EXTERNAL => t })

  private def falseToken = accept("false", { case t : Token.FALSE => t })

  private def fatal = accept("fatal", { case t: Token.FATAL => t })

  private def format = accept("format", { case t: Token.FORMAT => t })

  private def fppMatch = accept("match", { case t: Token.MATCH => t })

  private def fppWith = accept("with", { case t: Token.WITH => t })

  private def get = accept("get", { case t: Token.GET => t })

  private def group = accept("group", { case t: Token.GROUP => t })

  private def guard = accept("guard", { case t: Token.GUARD => t })

  private def guarded = accept("guarded", { case t: Token.GUARDED => t })

  private def health = accept("health", { case t: Token.HEALTH => t })

  private def high = accept("high", { case t: Token.HIGH => t })

  private def hook = accept("hook", { case t: Token.HOOK => t })

  private def id = accept("id", { case t: Token.ID => t })

  private def ident: Parser[Ast.Ident] =
    accept("identifier", { case Token.IDENTIFIER(s) => s })

  private def ifToken = accept("if", { case t: Token.IF => t })

  private def implements = accept("implements", { case t: Token.IMPLEMENTS => t })

  private def importToken = accept("import", { case t: Token.IMPORT => t })

  private def include = accept("include", { case t: Token.INCLUDE => t })

  private def initial = accept("initial", { case t: Token.INITIAL => t })

  private def input = accept("input", { case t: Token.INPUT => t })

  private def instance = accept("instance", { case t: Token.INSTANCE => t })

  private def internal = accept("internal", { case t: Token.INTERNAL => t })

  private def interface = accept("interface", { case t: Token.INTERFACE => t })

  private def lbracket = accept("[", { case t: Token.LBRACKET => t })

  private def literalFloat: Parser[String] =
    accept("floating-point literal", { case Token.LITERAL_FLOAT(s) => s })

  private def literalInt: Parser[String] =
    accept("integer literal", { case Token.LITERAL_INT(s) => s })

  private def literalString: Parser[String] =
    accept("string literal", { case Token.LITERAL_STRING(s) => s })

  private def locate = accept("locate", { case t: Token.LOCATE => t })

  private def low = accept("low", { case t: Token.LOW => t })

  private def lparen = accept("(", { case t: Token.LPAREN => t })

  private def machine = accept("machine", { case t: Token.MACHINE => t })

  private def minus = accept("-", { case t: Token.MINUS => t })

  private def module = accept("module", { case t: Token.MODULE => t })

  private def omit = accept("omit", { case t: Token.OMIT => t })

  private def on = accept("on", { case t: Token.ON => t })

  private def opcode = accept("opcode", { case t: Token.OPCODE => t })

  private def orange = accept("orange", { case t: Token.ORANGE => t })

  private def output = accept("output", { case t: Token.OUTPUT => t })

  private def packet = accept("packet", { case t: Token.PACKET => t })

  private def packets = accept("packets", { case t: Token.PACKETS => t })

  private def param = accept("param", { case t: Token.PARAM => t })

  private def passive = accept("passive", { case t: Token.PASSIVE => t })

  private def phase = accept("phase", { case t: Token.PHASE => t })

  private def plus = accept("+", { case t: Token.PLUS => t })

  private def port = accept("port", { case t: Token.PORT => t })

  private def postAnnotation: Parser[String] =
    accept("post annotation", { case Token.POST_ANNOTATION(s) => s })

  private def preAnnotation: Parser[String] =
    accept("pre annotation", { case Token.PRE_ANNOTATION(s) => s })

  private def priority = accept("priority", { case t: Token.PRIORITY => t })

  private def product = accept("product", { case t: Token.PRODUCT => t })

  private def queue = accept("queue", { case t: Token.QUEUE => t })

  private def queued = accept("queued", { case t: Token.QUEUED => t })

  private def rarrow = accept("->", { case t: Token.RARROW => t })

  private def rbrace = accept("}", { case t: Token.RBRACE => t })

  private def rbracket = accept("]", { case t: Token.RBRACKET => t })

  private def record = accept("record", { case t: Token.RECORD => t })

  private def recv = accept("recv", { case t: Token.RECV => t })

  private def red = accept("red", { case t: Token.RED => t })

  private def ref = accept("ref", { case t: Token.REF => t })

  private def reg = accept("reg", { case t: Token.REG => t })

  private def request = accept("request", { case t: Token.REQUEST => t })

  private def resp = accept("resp", { case t: Token.RESP => t })

  private def rparen = accept(")", { case t: Token.RPAREN => t })

  private def save = accept("save", { case t: Token.SAVE => t })

  private def semi = accept(";", { case t: Token.SEMI => t })

  private def send = accept("send", { case t: Token.SEND => t })

  private def serial = accept("serial", { case t: Token.SERIAL => t })

  private def set = accept("set", { case t: Token.SET => t })

  private def severity = accept("severity", { case t: Token.SEVERITY => t })

  private def signal = accept("signal", { case t: Token.SIGNAL => t })

  private def size = accept("size", { case t: Token.SIZE => t })

  private def slash = accept("/", { case t: Token.SLASH => t })

  private def stack = accept("stack", { case t: Token.STACK => t })

  private def star = accept("*", { case t: Token.STAR => t })

  private def state = accept("state", { case t: Token.STATE => t })

  private def string = accept("string", { case t: Token.STRING => t })

  private def struct = accept("struct", { case t: Token.STRUCT => t })

  private def sync = accept("sync", { case t: Token.SYNC => t })

  private def telemetry = accept("telemetry", { case t: Token.TELEMETRY => t })

  private def text = accept("text", { case t: Token.TEXT => t })

  private def throttle = accept("throttle", { case t: Token.THROTTLE => t })

  private def time = accept("time", { case t: Token.TIME => t })

  private def topology = accept("topology", { case t: Token.TOPOLOGY => t })

  private def trueToken = accept("true", { case t: Token.TRUE => t })

  private def typeToken = accept("type", { case t: Token.TYPE => t })

  private def unmatched = accept("unmatched", { case t: Token.UNMATCHED => t })

  private def update = accept("update", { case t: Token.UPDATE => t })

  private def warning = accept("warning", { case t: Token.WARNING => t })

  private def yellow = accept("yellow", { case t: Token.YELLOW => t })

  /** The first error seen */
  private var error: Option[Error] = None

}
