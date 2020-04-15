package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out an FPP AST */
object AstWriter extends AstVisitor {

  def transUnit(tu: Ast.TransUnit): Out = transUnit((), tu)

  override def defAbsTypeAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val (_, node, _) = an
    lines("def abs type") ++ ident(node.getData.name).map(indentIn)
  }

  override def defArrayAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = an
    val da = node.getData
    lines("def array") ++
    List(
      ident(da.name),
      addPrefix("size", exprNode) (da.size),
      typeNameNode(da.eltType),
      linesOpt(addPrefix("default", exprNode), da.default),
      linesOpt(addPrefix("format", applyToData(string)), da.format)
    ).flatten.map(indentIn)
  }

  override def defComponentAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefComponent]]) = {
    val (_, node, _) = an
    val dc = node.getData
    val kind = dc.kind match {
      case Ast.ComponentKind.Active => "active"
      case Ast.ComponentKind.Passive => "passive"
      case Ast.ComponentKind.Queued => "queued"
    }
    lines("def component") ++
    (
      lines("kind " ++ kind) ++ 
      ident(dc.name) ++ 
      dc.members.map(componentMember).flatten
    ).map(indentIn)
  }

  override def defComponentInstanceAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) = {
    val (_, node, _) = an
    val dci = node.getData
    lines("def component instance") ++
    List(
      ident(dci.name),
      addPrefix("component", qualIdent) (dci.typeName.getData),
      addPrefix("base id", exprNode) (dci.baseId),
      addPrefix("queue size", exprNode) (dci.baseId),
      addPrefix("stack size", exprNode) (dci.baseId),
      addPrefix("priority", exprNode) (dci.baseId),
    ).flatten.map(indentIn)
  }

  override def defConstantAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = an
    val dc = node.getData
    lines("def constant") ++
    (ident(dc.name) ++ exprNode(dc.value)).map(indentIn)
  }

  override def defEnumAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = an
    val de = node.getData
    lines("def enum") ++
    List(
      ident(de.name),
      linesOpt(typeNameNode, de.typeName),
      de.constants.map(annotateNode(defEnumConstant)).flatten
    ).flatten.map(indentIn)
  }

  override def defModuleAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefModule]]) = {
    val (_, node, _) = an
    val dm = node.getData
    lines("def module") ++
    (ident(dm.name) ++ dm.members.map(moduleMember).flatten).map(indentIn)
  }

  override def defPortAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node, _) = an
    val dp = node.getData
    lines("def port") ++
    List(
      ident(dp.name),
      formalParamList(dp.params),
      linesOpt(addPrefix("return", typeNameNode), dp.returnType)
    ).flatten.map(indentIn)
  }

  override def defStructAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = an
    val ds = node.getData
    lines("def struct") ++ 
    (
      ident(ds.name) ++
      ds.members.map(annotateNode(structTypeMember)).flatten ++ 
      linesOpt(exprNode, ds.default)
    ).map(indentIn) 
  }

  override def defTopologyAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.DefTopology]]) = {
    val (_, node, _) = an
    val dt = node.getData
    lines("def topology") ++
    (ident(dt.name) ++ dt.members.map(topologyMember).flatten).map(indentIn)
  }

  override def default(in: Unit) = throw new InternalError("AstWriter: Visitor not implemented")

  override def exprArrayNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprArray) =
    lines("expr array") ++
    e.elts.map(exprNode).flatten.map(indentIn)

  override def exprBinopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprBinop) =
    lines("expr binop") ++
    (exprNode(e.e1) ++ binop(e.op) ++ exprNode(e.e2)).map(indentIn)
  
  override def exprDotNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    lines("expr dot") ++
    (exprNode(e.e) ++ ident(e.id)).map(indentIn)

  override def exprIdentNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprIdent) = 
    ident(e.value)

  override def exprLiteralBoolNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) = {
    val s = e.value match {
      case Ast.LiteralBool.True => "true"
      case Ast.LiteralBool.False => "false"
    }
    lines("literal bool " ++ s)
  }

  override def exprLiteralFloatNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    lines("literal float " ++ e.value)
  
  override def exprLiteralIntNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    lines("literal int " ++ e.value)

  override def exprLiteralStringNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    addPrefix("literal string", string) (e.value)

  override def exprParenNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprParen) =
    lines("expr paren") ++
    exprNode(e.e).map(indentIn)

  override def exprStructNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    lines("expr struct") ++
    e.members.map(structMember).flatten.map(indentIn)

  override def exprUnopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprUnop) =
    lines("expr unop") ++
    (unop(e.op) ++ exprNode(e.e)).map(indentIn)

  override def specCommandAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecCommand]]) = {
    val (_, node, _) = an
    def kind(k: Ast.SpecCommand.Kind) = k match {
      case Ast.SpecCommand.Async => "async"
      case Ast.SpecCommand.Guarded => "guarded"
      case Ast.SpecCommand.Sync => "sync"
    }
    val sc = node.getData
    lines("spec command") ++
    List(
      lines("kind " ++ kind(sc.kind)),
      addPrefix("name", ident) (sc.name),
      formalParamList(sc.params),
      linesOpt(addPrefix("opcode", exprNode), sc.opcode),
      linesOpt(addPrefix("priority", exprNode), sc.priority),
      linesOpt(applyToData(queueFull), sc.queueFull)
    ).flatten.map(indentIn)
  }

  override def specCompInstanceAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecCompInstance]]) =  {
    val (_, node, _) = an
    val ci = node.getData
    lines("spec comp instance") ++ (
      lines(visibility(ci.visibility)) ++
      qualIdent(ci.instance.getData)
    ).map(indentIn)
  }

  override def specConnectionGraphAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]) =  {
    def direct(g: Ast.SpecConnectionGraph.Direct) = {
      def connection(c: Ast.SpecConnectionGraph.Connection) = {
        lines("connection") ++ (
          addPrefix("from port", portInstanceIdentifier) (c.fromPort.getData) ++
          linesOpt(addPrefix("index", exprNode), c.fromIndex) ++
          addPrefix("to port", portInstanceIdentifier) (c.toPort.getData) ++
          linesOpt(addPrefix("index", exprNode), c.toIndex)
        ).map(indentIn)
      }
      lines("spec connection graph direct") ++ (
        ident(g.name) ++
        g.connections.map(connection).flatten
      ).map(indentIn)
    }
    def pattern(g: Ast.SpecConnectionGraph.Pattern) = {
      def target(qid: Ast.QualIdent) = addPrefix("target", qualIdent) (qid)
      lines("spec connection graph pattern") ++ (
        addPrefix("source", qualIdent) (g.source.getData) ++
        g.targets.map(applyToData(target)).flatten ++
        addPrefix("pattern", exprNode) (g.pattern)
      ).map(indentIn)
    }
    val (_, node, _) = an
    node.getData match {
      case g @ Ast.SpecConnectionGraph.Direct(_, _) => direct(g)
      case g @ Ast.SpecConnectionGraph.Pattern(_, _, _) => pattern(g)
    }
  }

  override def specEventAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecEvent]]) = {
    val (_, node, _) = an
    def severity(s: Ast.SpecEvent.Severity) = s match {
      case Ast.SpecEvent.ActivityHigh => "activity high"
      case Ast.SpecEvent.ActivityLow => "activity low"
      case Ast.SpecEvent.Command => "command"
      case Ast.SpecEvent.Diagnostic => "diagnostic"
      case Ast.SpecEvent.Fatal => "fatal"
      case Ast.SpecEvent.WarningHigh => "warning high"
      case Ast.SpecEvent.WarningLow => "warning low"
    }
    val se = node.getData
    lines("spec event") ++
    List(
      ident(se.name),
      formalParamList(se.params),
      lines("severity " ++ severity(se.severity)),
      linesOpt(addPrefix("id", exprNode), se.id),
      linesOpt(addPrefix("format", applyToData(string)), se.format),
      linesOpt(addPrefix("throttle", exprNode), se.throttle),
    ).flatten.map(indentIn)
  }

  override def specIncludeAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecInclude]]) =  {
    val (_, node, _) = an
    val si = node.getData
    lines("spec include") ++ fileString(si.file.getData).map(indentIn)
  }

  override def specInitAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecInit]]) = {
    val (_, node, _) = an
    val si = node.getData
    lines("spec init") ++
    List(
      addPrefix("instance", applyToData(qualIdent)) (si.instance),
      addPrefix("phase", exprNode) (si.phase),
      addPrefix("code", string) (si.code)
    ).flatten.map(indentIn)
  }

  override def specInternalPortAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecInternalPort]]) = {
    val (_, node, _) = an
    val sip = node.getData
    lines("spec internal port") ++
    List(
      ident(sip.name),
      formalParamList(sip.params),
      linesOpt(addPrefix("priority", exprNode), sip.priority),
      linesOpt(queueFull, sip.queueFull)
    ).flatten.map(indentIn)
  }

  override def specLocAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecLoc]]) = {
    val (_, node, _) = an
    val sl = node.getData
    val kind = sl.kind match {
      case Ast.SpecLoc.Component => "component"
      case Ast.SpecLoc.ComponentInstance => "component instance"
      case Ast.SpecLoc.Constant => "constant"
      case Ast.SpecLoc.Port => "port"
      case Ast.SpecLoc.Topology => "topology"
      case Ast.SpecLoc.Type => "type"
    }
    lines("spec loc") ++
    (
      lines("kind " ++ kind) ++
      addPrefix("symbol", qualIdent) (sl.symbol.getData) ++ 
      fileString(sl.file.getData)
    ).map(indentIn)
  }

  override def specParamAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecParam]]) = {
    val (_, node, _) = an
    val sp = node.getData
    lines("spec param") ++
    List(
      ident(sp.name),
      typeNameNode(sp.typeName),
      linesOpt(addPrefix("default", exprNode), sp.default),
      linesOpt(addPrefix("id", exprNode), sp.id),
      linesOpt(addPrefix("set opcode", exprNode), sp.setOpcode),
      linesOpt(addPrefix("save opcode", exprNode), sp.saveOpcode),
    ).flatten.map(indentIn)
  }

  override def specPortInstanceAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node, _) = an
    def general(i: Ast.SpecPortInstance.General) = {
      def kind(k: Ast.SpecPortInstance.GeneralKind) = {
        val s = k match {
          case Ast.SpecPortInstance.AsyncInput => "async input"
          case Ast.SpecPortInstance.GuardedInput => "guarded input"
          case Ast.SpecPortInstance.Output => "output"
          case Ast.SpecPortInstance.SyncInput => "sync input"
        }
        lines("kind " ++ s)
      }
      lines("spec port instance general") ++
      List(
        kind(i.kind),
        ident(i.name),
        linesOpt(addPrefix("array size", exprNode), i.size),
        linesOpt(addPrefix("port type", applyToData(qualIdent)), i.port),
        linesOpt(addPrefix("priority", exprNode), i.priority),
        linesOpt(queueFull, i.queueFull)
      ).flatten.map(indentIn)
    }
    def special(i: Ast.SpecPortInstance.Special) = {
      def kind(k: Ast.SpecPortInstance.SpecialKind) = {
        val s = k match {
          case Ast.SpecPortInstance.CommandRecv => "command recv"
          case Ast.SpecPortInstance.CommandReg => "command reg"
          case Ast.SpecPortInstance.CommandResp => "command resp"
          case Ast.SpecPortInstance.Event => "event"
          case Ast.SpecPortInstance.ParamGet => "param get"
          case Ast.SpecPortInstance.ParamSet => "param set"
          case Ast.SpecPortInstance.Telemetry => "telemetry"
          case Ast.SpecPortInstance.TextEvent => "text event"
          case Ast.SpecPortInstance.TimeGet => "time get"
        }
        lines("kind " ++ s)
      }
      lines("spec port instance special") ++
      (kind(i.kind) ++ ident(i.name)).map(indentIn)
    }
    node.getData match {
      case i @ Ast.SpecPortInstance.General(_, _, _, _, _, _) => general(i)
      case i @ Ast.SpecPortInstance.Special(_, _) => special(i)
    }
  }

  override def specTlmChannelAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]) = {
    val (_, node, _) = an
    def update(u: Ast.SpecTlmChannel.Update) = {
      val s = u match {
        case Ast.SpecTlmChannel.Always => "always"
        case Ast.SpecTlmChannel.OnChange => "on change"
      }
      lines("update " + s)
    }
    def kind(k: Ast.SpecTlmChannel.LimitKind) = {
      val s = k match {
        case Ast.SpecTlmChannel.Red => "red"
        case Ast.SpecTlmChannel.Orange => "orange"
        case Ast.SpecTlmChannel.Yellow => "yellow"
      }
      lines(s)
    }
    def limit(l: Ast.SpecTlmChannel.Limit) = {
      val (k, en) = l
      lines("limit") ++ (
        kind(k) ++
        exprNode(en)
      ).map(indentIn)
    }
    def limits(name: String, ls: List[Ast.SpecTlmChannel.Limit]) =
      ls.map(addPrefixNoIndent(name, limit))
    val tc = node.getData
    lines("spec tlm channel") ++
    List(
      ident(tc.name),
      typeNameNode(tc.typeName),
      linesOpt(addPrefix("id", exprNode), tc.id),
      linesOpt(update, tc.update),
      linesOpt(addPrefix("format", applyToData(string)), tc.format),
      limits("low", tc.low).flatten,
      limits("high", tc.high).flatten,
    ).flatten.map(indentIn)
  }

  override def specTopImportAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecTopImport]]) =  {
    val (_, node, _) = an
    val ti = node.getData
    lines("spec top import") ++
    qualIdent(ti.top.getData).map(indentIn)
  }

  override def specUnusedPortsAnnotatedNode(in: Unit, an: Ast.Annotated[AstNode[Ast.SpecUnusedPorts]]) =  {
    val (_, node, _) = an
    val up = node.getData
    lines("spec unused ports") ++
    up.ports.map(applyToData(portInstanceIdentifier)).flatten.map(indentIn)
  }

  override def transUnit(in: Unit, tu: Ast.TransUnit) = tu.members.map(tuMember).flatten

  override def typeNameBoolNode(in: Unit, node: AstNode[Ast.TypeName]) = lines("bool")

  override def typeNameFloatNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat) = {
    val s = tn.name match {
      case Ast.F32() => "F32"
      case Ast.F64() => "F64"
    }
    lines(s)
  }

  override def typeNameIntNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt) = {
    val s = tn.name match {
      case Ast.I8() => "I8"
      case Ast.I16() => "I16"
      case Ast.I32() => "I32"
      case Ast.I64() => "I64"
      case Ast.U8() => "U8"
      case Ast.U16() => "U16"
      case Ast.U32() => "U32"
      case Ast.U64() => "U64"
    }
    lines(s)
  }

  override def typeNameQualIdentNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent) = 
    qualIdent(tn.name)

  override def typeNameStringNode(in: Unit, node: AstNode[Ast.TypeName]) = lines("string")

  private def addPrefixNoIndent[T](s: String, f: T => List[Line]): T => List[Line] =
    (t: T) => Line.joinLists (Line.NoIndent) (lines(s)) (" ") (f(t))

  private def addPrefix[T](s: String, f: T => List[Line]): T => List[Line] =
    (t: T) => Line.joinLists (Line.Indent) (lines(s)) (" ") (f(t))

  private def annotate(pre: List[String], lines: List[Line], post: List[String]) = {
    def preLine(s: String) = line("@ " ++ s)
    val pre1 = pre.map(preLine)
    def postLine(s: String) = line("@< " ++ s)
    val post1 = post.map(postLine)
    pre1 ++ lines ++ post1
  }

  private def annotateNode[T](f: T => List[Line]): Ast.Annotated[AstNode[T]] => List[Line] =
    (ana: Ast.Annotated[AstNode[T]]) => {
      val (a1, node, a2) = ana
      annotate(a1, f(node.getData), a2)
    }

  private def applyToData[A,B](f: A => B): AstNode[A] => B = 
    (a: AstNode[A]) => f(a.getData)

  private def binop(op: Ast.Binop) = op match {
    case Ast.Binop.Add => lines("binop +")
    case Ast.Binop.Div => lines("binop /")
    case Ast.Binop.Mul => lines("binop *")
    case Ast.Binop.Sub => lines("binop -")
  }

  private def componentMember(member: Ast.ComponentMember) = {
    val (a1, _, a2) = member.node
    val l = matchComponentMember((), member)
    annotate(a1, l, a2)
  }

  private def defEnumConstant(dec: Ast.DefEnumConstant) =
    lines("def enum constant") ++
    List(
      ident(dec.name),
      linesOpt(exprNode, dec.value)
    ).flatten.map(indentIn)

  private def exprNode(node: AstNode[Ast.Expr]): List[Line] = matchExprNode((), node)

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
    List(
      lines(kind(fp.kind)),
      ident(fp.name),
      typeNameNode(fp.typeName),
      linesOpt(exprNode, fp.size)
    ).flatten.map(indentIn)
  }

  private def formalParamList(params: Ast.FormalParamList) =
    params.map(annotateNode(formalParam)).flatten

  private def ident(s: String) = lines("ident " ++ s)

  private def indentIn(line: Line) = line.indentIn(2)

  private def line(s: String) = Line(string = s)

  private def lines(s: String) = List(line(s))

  private def linesOpt[T](f: T => List[Line], o: Option[T]) =
    o match {
      case Some(x) => f(x)
      case None => Nil
    }

  private def moduleMember(member: Ast.ModuleMember) = {
    val (a1, _, a2) = member.node
    val l = matchModuleMember((), member)
    annotate(a1, l, a2)
  }

  private def portInstanceIdentifier(piid: Ast.PortInstanceIdentifier): List[Line] = {
    val qid = piid.componentInstance.getData ++ List(piid.portName)
    qualIdent(qid)
  }

  private def qualIdent(qid: Ast.QualIdent): List[Line] =
    lines("qual ident " ++ qualIdentString(qid))
    
  private def qualIdentString(qid: Ast.QualIdent): String =
    qid match {
      case Nil => ""
      case id :: Nil => id
      case (id :: qid1) => id ++ "." ++ qualIdentString(qid1)
    }

  private def queueFull(qf: Ast.QueueFull) = {
    val s = qf match {
      case Ast.QueueFull.Assert => "assert"
      case Ast.QueueFull.Block => "block"
      case Ast.QueueFull.Drop => "drop"
    }
    lines("queue full " ++ s)
  }

  private def string(s: String) = s.split('\n').map(line).toList

  private def structMember(sm: Ast.StructMember) =
    lines("struct member") ++ 
    (ident(sm.name) ++ exprNode(sm.value)).map(indentIn)

  private def structTypeMember(stm: Ast.StructTypeMember) = {
    lines("struct type member") ++ 
    List(
      ident(stm.name),
      typeNameNode(stm.typeName),
      linesOpt(addPrefix("format", applyToData(string)), stm.format)
    ).flatten.map(indentIn)
  }

  private def todo = lines("TODO")

  private def topologyMember(tm: Ast.TopologyMember) = {
    val l = matchTopologyMember((), tm)
    val (a1, _, a2) = tm.node
    annotate(a1, l, a2)
  }

  private def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  private def typeNameNode(node: AstNode[Ast.TypeName]) =
    addPrefix("type name", matchTypeNameNode((), _)) (node)

  private def unop(op: Ast.Unop) =
    op match {
      case Ast.Unop.Minus => lines("unop -")
    }

  private def visibility(v: Ast.Visibility) = v match {
    case Ast.Visibility.Private => "private"
    case Ast.Visibility.Public => "public"
  }

  type In = Unit

  type Out = List[Line]

}
