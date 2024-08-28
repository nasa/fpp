package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out an FPP AST */
object AstWriter extends AstVisitor with LineUtils {

  type In = Unit

  type Out = List[Line]

  def transUnit(tu: Ast.TransUnit): Out = transUnit((), tu)

  override def defAbsTypeAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]
  ) = {
    val (_, node, _) = aNode
    lines("def abs type") ++ ident(node.data.name).map(indentIn)
  }

  override def defActionAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefAction]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def action") ++
    (
      ident(data.name) ++
      linesOpt(typeNameNode, data.typeName)
    ).map(indentIn)
  }

  override def defArrayAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def array") ++
    List.concat(
      ident(data.name),
      addPrefix("size", exprNode) (data.size),
      typeNameNode(data.eltType),
      linesOpt(addPrefix("default", exprNode), data.default),
      linesOpt(addPrefix("format", applyToData(string)), data.format)
    ).map(indentIn)
  }

  override def defComponentAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val kind = data.kind.toString
    lines("def component") ++
    (
      lines("kind " ++ kind) ++
      ident(data.name) ++
      data.members.flatMap(componentMember)
    ).map(indentIn)
  }

  override def defComponentInstanceAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def component instance") ++
    List.concat(
      ident(data.name),
      addPrefix("component", qualIdent) (data.component.data),
      addPrefix("base id", exprNode) (data.baseId),
      linesOpt(addPrefix("type", applyToData(string)), data.implType),
      linesOpt(applyToData(fileString), data.file),
      linesOpt(addPrefix("queue size", exprNode), data.queueSize),
      linesOpt(addPrefix("stack size", exprNode), data.stackSize),
      linesOpt(addPrefix("priority", exprNode), data.priority),
      linesOpt(addPrefix("cpu", exprNode), data.cpu),
      data.initSpecs.flatMap(annotateNode(specInit))
    ).map(indentIn)
  }

  override def defConstantAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def constant") ++
    (ident(data.name) ++ exprNode(data.value)).map(indentIn)
  }

  override def defEnumAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def enum") ++
    List.concat(
      ident(data.name),
      linesOpt(typeNameNode, data.typeName),
      data.constants.flatMap(annotateNode(defEnumConstant))
    ).map(indentIn)
  }

  override def defGuardAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefGuard]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def guard") ++
    (
      ident(data.name) ++
      linesOpt(typeNameNode, data.typeName)
    ).map(indentIn)
  }

  override def defJunctionAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefJunction]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val guard = data.guard
    lines("def junction") ++
    (ident(data.name) ++
    addPrefix("guard", applyToData(ident)) (data.guard) ++
    transitionExpr(data.ifTransition.data) ++
    transitionExpr(data.elseTransition.data)).map(indentIn)
  }

  override def defModuleAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def module") ++
    (ident(data.name) ++ data.members.flatMap(moduleMember)).map(indentIn)
  }

  override def defPortAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def port") ++
    List.concat(
      ident(data.name),
      formalParamList(data.params),
      linesOpt(addPrefix("return", typeNameNode), data.returnType)
    ).map(indentIn)
  }

  override def defSignalAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefSignal]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def signal") ++
    (
      ident(data.name) ++
      linesOpt(typeNameNode, data.typeName)
    ).map(indentIn)
  }

  override def defStateAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def state") ++
    (
      ident(data.name) ++
      data.members.flatMap(stateMember)
    ).map(indentIn)
  }

  override def defStateMachineAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def state machine") ++
    (
      ident(data.name) ++
      data.members.getOrElse(Nil).flatMap(stateMachineMember)
    ).map(indentIn)
  }

  override def defStructAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def struct") ++
    (
      ident(data.name) ++
      data.members.flatMap(annotateNode(structTypeMember)) ++
      linesOpt(exprNode, data.default)
    ).map(indentIn)
  }

  override def defTopologyAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("def topology") ++
    (ident(data.name) ++ data.members.flatMap(topologyMember)).map(indentIn)
  }

  override def default(in: In) =
    throw new InternalError("AstWriter: Visitor not implemented")

  override def exprArrayNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprArray
  ) =
    lines("expr array") ++
    e.elts.flatMap(exprNode).map(indentIn)

  override def exprBinopNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprBinop
  ) =
    lines("expr binop") ++
    (exprNode(e.e1) ++ binop(e.op) ++ exprNode(e.e2)).map(indentIn)

  override def exprDotNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprDot
  ) =
    lines("expr dot") ++
    (exprNode(e.e) ++ ident(e.id.data)).map(indentIn)

  override def exprIdentNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprIdent
  ) =
    ident(e.value)

  override def exprLiteralBoolNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprLiteralBool
  ) = {
    val s = e.value match {
      case Ast.LiteralBool.True => "true"
      case Ast.LiteralBool.False => "false"
    }
    lines("literal bool " ++ s)
  }

  override def exprLiteralFloatNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprLiteralFloat
  ) =
    lines("literal float " ++ e.value)

  override def exprLiteralIntNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprLiteralInt
  ) =
    lines("literal int " ++ e.value)

  override def exprLiteralStringNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprLiteralString
  ) =
    addPrefix("literal string", string) (e.value)

  override def exprParenNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprParen
  ) =
    lines("expr paren") ++
    exprNode(e.e).map(indentIn)

  override def exprStructNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprStruct
  ) =
    lines("expr struct") ++
    e.members.flatMap(applyToData(structMember)).map(indentIn)

  override def exprUnopNode(
    in: In,
    node: AstNode[Ast.Expr],
    e: Ast.ExprUnop
  ) =
    lines("expr unop") ++
    (unop(e.op) ++ exprNode(e.e)).map(indentIn)

  override def specCommandAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec command") ++
    List.concat(
      lines(s"kind ${data.kind.toString}"),
      addPrefix("name", ident) (data.name),
      formalParamList(data.params),
      linesOpt(addPrefix("opcode", exprNode), data.opcode),
      linesOpt(addPrefix("priority", exprNode), data.priority),
      linesOpt(applyToData(queueFull), data.queueFull)
    ).map(indentIn)
  }

  override def specCompInstanceAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecCompInstance]]
  ) =  {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec comp instance") ++ (
      lines(visibility(data.visibility)) ++
      qualIdent(data.instance.data)
    ).map(indentIn)
  }

  override def specConnectionGraphAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]
  ) =  {
    def direct(g: Ast.SpecConnectionGraph.Direct) = {
      def connection(c: Ast.SpecConnectionGraph.Connection) = {
        lines("connection") ++ (
          addPrefix("from port", portInstanceIdentifier) (c.fromPort.data) ++
          linesOpt(addPrefix("index", exprNode), c.fromIndex) ++
          addPrefix("to port", portInstanceIdentifier) (c.toPort.data) ++
          linesOpt(addPrefix("index", exprNode), c.toIndex)
        ).map(indentIn)
      }
      lines("spec connection graph direct") ++ (
        ident(g.name) ++
        g.connections.flatMap(connection)
      ).map(indentIn)
    }
    def pattern(g: Ast.SpecConnectionGraph.Pattern) = {
      def target(qid: AstNode[Ast.QualIdent]) = addPrefix("target", qualIdent) (qid.data)
      lines("spec connection graph pattern") ++ (
        lines("kind " ++ g.kind.toString) ++
        addPrefix("source", qualIdent) (g.source.data) ++
        g.targets.flatMap(target)
      ).map(indentIn)
    }
    val (_, node, _) = aNode
    node.data match {
      case g : Ast.SpecConnectionGraph.Direct => direct(g)
      case g : Ast.SpecConnectionGraph.Pattern => pattern(g)
    }
  }

  override def specContainerAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecContainer]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec container") ++
    List.concat(
      ident(data.name),
      linesOpt(addPrefix("id", exprNode), data.id),
      linesOpt(addPrefix("default priority", exprNode), data.defaultPriority)
    ).map(indentIn)
  }

  override def specEntryAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecEntry]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec entry") ++
    actionList(data.actions).map(indentIn)
  }

  override def specEventAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec event") ++
    List.concat(
      ident(data.name),
      formalParamList(data.params),
      lines(s"severity ${data.severity.toString}"),
      linesOpt(addPrefix("id", exprNode), data.id),
      addPrefix("format", string) (data.format.data),
      linesOpt(addPrefix("throttle", exprNode), data.throttle),
    ).map(indentIn)
  }

  override def specExitAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecExit]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec exit") ++
    actionList(data.actions).map(indentIn)
  }

  override def specIncludeAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInclude]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec include") ++ fileString(data.file.data).map(indentIn)
  }

  override def specInitialTransitionAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec initial") ++
    transitionExpr(data.transition).map(indentIn)
  }

  override def specInternalPortAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec internal port") ++
    List.concat(
      ident(data.name),
      formalParamList(data.params),
      linesOpt(addPrefix("priority", exprNode), data.priority),
      linesOpt(queueFull, data.queueFull)
    ).map(indentIn)
  }

  override def specLocAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val kind = data.kind.toString
    lines("spec loc") ++
    (
      lines("kind " ++ kind) ++
      addPrefix("symbol", qualIdent) (data.symbol.data) ++
      fileString(data.file.data)
    ).map(indentIn)
  }

  override def specParamAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecParam]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec param") ++
    List.concat(
      ident(data.name),
      typeNameNode(data.typeName),
      linesOpt(addPrefix("default", exprNode), data.default),
      linesOpt(addPrefix("id", exprNode), data.id),
      linesOpt(addPrefix("set opcode", exprNode), data.setOpcode),
      linesOpt(addPrefix("save opcode", exprNode), data.saveOpcode),
    ).map(indentIn)
  }

  override def specPortInstanceAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) = {
    val (_, node, _) = aNode
    def general(i: Ast.SpecPortInstance.General) = {
      val kind = lines(s"kind ${i.kind.toString}")
      lines("spec port instance general") ++
      List.concat(
        kind,
        ident(i.name),
        linesOpt(addPrefix("array size", exprNode), i.size),
        linesOpt(addPrefix("port type", applyToData(qualIdent)), i.port),
        linesOpt(addPrefix("priority", exprNode), i.priority),
        linesOpt(applyToData(queueFull), i.queueFull)
      ).map(indentIn)
    }
    def special(i: Ast.SpecPortInstance.Special) = {
      val kind = lines(s"kind ${i.kind.toString}")
      lines("spec port instance special") ++
      List.concat(
        linesOpt(
          addPrefix("input kind", string),
          i.inputKind.map(_.toString)
        ),
        kind,
        ident(i.name),
        linesOpt(addPrefix("priority", exprNode), i.priority),
        linesOpt(applyToData(queueFull), i.queueFull)
      ).map(indentIn)
    }
    node.data match {
      case i : Ast.SpecPortInstance.General => general(i)
      case i : Ast.SpecPortInstance.Special => special(i)
    }
  }

  override def specPortMatchingAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortMatching]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec port matching") ++
    List.concat(
        ident(data.port1.data),
        ident(data.port2.data),
    ).map(indentIn)
  }

  override def specRecordAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecRecord]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val writeRecordType = if data.isArray
      then addSuffix(typeNameNode, "array")
      else typeNameNode
    lines("spec record") ++
    List.concat(
      ident(data.name),
      writeRecordType(data.recordType),
      linesOpt(addPrefix("id", exprNode), data.id)
    ).map(indentIn)
  }

  override def specStateMachineInstanceAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec state machine instance") ++
    List.concat(
      ident(node.data.name),
      addPrefix("state machine", qualIdent) (data.stateMachine.data),
      linesOpt(addPrefix("priority", exprNode), data.priority),
      linesOpt(queueFull, data.queueFull)
    ).map(indentIn)
  }

  override def specTlmChannelAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]
  ) = {
    val (_, node, _) = aNode
    def update(u: Ast.SpecTlmChannel.Update) =
      lines(s"update ${u.toString}")
    def kind(k: Ast.SpecTlmChannel.LimitKind) =
      lines(k.toString)
    def limit(l: Ast.SpecTlmChannel.Limit) = {
      val (k, en) = l
      lines("limit") ++ (
        kind(k.data) ++
        exprNode(en)
      ).map(indentIn)
    }
    def limits(name: String, ls: List[Ast.SpecTlmChannel.Limit]) =
      ls.map(addPrefixNoIndent(name, limit))
    val tc = node.data
    lines("spec tlm channel") ++
    List.concat(
      ident(tc.name),
      typeNameNode(tc.typeName),
      linesOpt(addPrefix("id", exprNode), tc.id),
      linesOpt(update, tc.update),
      linesOpt(addPrefix("format", applyToData(string)), tc.format),
      limits("low", tc.low).flatten,
      limits("high", tc.high).flatten,
    ).map(indentIn)
  }

  override def specTopImportAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecTopImport]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec top import") ++
    qualIdent(data.top.data).map(indentIn)
  }

  override def specStateTransitionAnnotatedNode(
    in: In,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    lines("spec transition") ++
    (addPrefix("signal", applyToData(ident)) (data.signal) ++
    linesOpt(addPrefix("guard", applyToData(ident)), data.guard) ++
    transitionOrDo(data.transitionOrDo)).map(indentIn)
  }

  override def transUnit(in: In, tu: Ast.TransUnit) =
    tu.members.flatMap(tuMember)

  override def typeNameBoolNode(
    in: In,
    node: AstNode[Ast.TypeName]
  ) = lines("bool")

  override def typeNameFloatNode(
    in: In,
    node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat
  ) = lines(tn.name.toString)

  override def typeNameIntNode(
    in: In,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameInt
  ) = lines(tn.name.toString)

  override def typeNameQualIdentNode(
    in: In,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameQualIdent
  ) = qualIdent(tn.name.data)

  override def typeNameStringNode(
    in: In,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameString
  ) =
    lines("string") ++ linesOpt(addPrefix("size", exprNode), tn.size).map(indentIn)

  private def addPrefixNoIndent[T](
    s: String,
    f: T => Out
  ): T => Out =
    (t: T) => Line.joinLists (Line.NoIndent) (lines(s)) (" ") (f(t))

  private def addPrefix[T](
    s: String,
    f: T => Out
  ): T => Out =
    (t: T) => Line.joinLists (Line.Indent) (lines(s)) (" ") (f(t))

  private def addSuffix[T](
    f: T => Out,
    s: String
  ): T => Out =
    (t: T) => Line.joinLists (Line.Indent) (f(t)) (" ") (lines(s))

  private def annotate(
    pre: List[String],
    lines: Out,
    post: List[String]
  ) = {
    def preLine(s: String) = line("@ " ++ s)
    val pre1 = pre.map(preLine)
    def postLine(s: String) = line("@< " ++ s)
    val post1 = post.map(postLine)
    pre1 ++ lines ++ post1
  }

  private def annotateNode[T](f: T => Out): Ast.Annotated[AstNode[T]] => Out =
    (ana: Ast.Annotated[AstNode[T]]) => {
      val (a1, node, a2) = ana
      annotate(a1, f(node.data), a2)
    }

  private def applyToData[A,B](f: A => B): AstNode[A] => B =
    (a: AstNode[A]) => f(a.data)

  private def binop(op: Ast.Binop) = lines(s"binop ${op.toString}")

  private def componentMember(member: Ast.ComponentMember) = {
    val (a1, _, a2) = member.node
    val l = matchComponentMember((), member)
    annotate(a1, l, a2)
  }

  private def actionList(actions: List[AstNode[Ast.Ident]]): List[Line] =
    actions.map(node => line(s"action ident ${node.data}"))

  private def defEnumConstant(dec: Ast.DefEnumConstant) =
    lines("def enum constant") ++
    List.concat(
      ident(dec.name),
      linesOpt(exprNode, dec.value)
    ).map(indentIn)


  private def exprNode(node: AstNode[Ast.Expr]): Out =
    matchExprNode((), node)

  private def fileString(s: String) = lines("file " ++ s)

  private def formalParam(fp: Ast.FormalParam) = {
    def kind(k: Ast.FormalParam.Kind) = {
      val s = k match {
        case Ast.FormalParam.Ref => "ref"
        case Ast.FormalParam.Value => "value"
      }
      "kind " ++ s
    }
    lines("formal param") ++
    List.concat(
      lines(kind(fp.kind)),
      ident(fp.name),
      typeNameNode(fp.typeName),
    ).map(indentIn)
  }

  private def formalParamList(params: Ast.FormalParamList) =
    params.flatMap(annotateNode(formalParam))

  private def ident(s: String) = lines("ident " ++ s)

  private def moduleMember(member: Ast.ModuleMember) = {
    val (a1, _, a2) = member.node
    val l = matchModuleMember((), member)
    annotate(a1, l, a2)
  }

  private def portInstanceIdentifier(pii: Ast.PortInstanceIdentifier): Out = {
    val qid = Ast.QualIdent.Qualified(pii.componentInstance, pii.portName)
    qualIdent(qid)
  }

  private def qualIdent(qid: Ast.QualIdent): Out =
    lines("qual ident " ++ qualIdentString(qid))

  private def qualIdentString(qid: Ast.QualIdent): String =
    qid match {
      case Ast.QualIdent.Unqualified(name) => name
      case Ast.QualIdent.Qualified(qualifier, name) =>
        qualIdentString(qualifier.data) ++ "." ++ name.data
    }

  private def queueFull(qf: Ast.QueueFull) = {
    val s = qf.toString
    lines(s"queue full $s")
  }

  private def specInit(si: Ast.SpecInit) = {
    lines("spec init") ++
    List.concat(
      addPrefix("phase", exprNode) (si.phase),
      addPrefix("code", string) (si.code)
    ).map(indentIn)
  }

  private def stateMachineMember(member: Ast.StateMachineMember) = {
    val (a1, _, a2) = member.node
    val l = matchStateMachineMember((), member)
    annotate(a1, l, a2)
  }

  private def stateMember(member: Ast.StateMember) = {
    val (a1, _, a2) = member.node
    val l = matchStateMember((), member)
    annotate(a1, l, a2)
  }

  private def string(s: String) = s.split('\n').map(line).toList

  private def structMember(sm: Ast.StructMember) =
    lines("struct member") ++
    (ident(sm.name) ++ exprNode(sm.value)).map(indentIn)

  private def structTypeMember(stm: Ast.StructTypeMember) = {
    lines("struct type member") ++
    List.concat(
      ident(stm.name),
      linesOpt(addPrefix("array size", exprNode), stm.size),
      typeNameNode(stm.typeName),
      linesOpt(addPrefix("format", applyToData(string)), stm.format)
    ).map(indentIn)
  }

  private def todo = lines("TODO")

  private def topologyMember(tm: Ast.TopologyMember) = {
    val l = matchTopologyMember((), tm)
    val (a1, _, a2) = tm.node
    annotate(a1, l, a2)
  }

  private def transitionExpr(transition: Ast.TransitionExpr) =
    List.concat(
      actionList(transition.actions),
      addPrefix("destination", applyToData(qualIdent)) (transition.destination)
    )

  private def transitionOrDo(tod: Ast.TransitionOrDo) =
    tod match {
      case Ast.TransitionOrDo.Transition(transition) => transitionExpr(transition)
      case Ast.TransitionOrDo.Do(actions) => actionList(actions)
    }

  private def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  private def typeNameNode(node: AstNode[Ast.TypeName]) =
    addPrefix("type name", matchTypeNameNode((), _)) (node)

  private def unop(op: Ast.Unop) = lines(s"unop ${op.toString}")

  private def visibility(v: Ast.Visibility) = v.toString

}
