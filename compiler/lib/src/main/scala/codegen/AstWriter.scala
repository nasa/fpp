package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out an FPP AST */
object AstWriter extends AstUnitVisitor[List[Line]] {

  override def defAbsTypeNode(node: AstNode[Ast.DefAbsType]) =
    lines("def abs type") ++ ident(node.getData.name).map(indentIn)

  override def defArrayNode(node: AstNode[Ast.DefArray]) = {
    val da = node.getData
    lines("def array") ++
    List(
      ident(da.name),
      addPrefix("size", expr) (da.size.getData),
      typeName(da.eltType.getData),
      linesOpt(addPrefix("default", applyToData(expr)), da.default),
      linesOpt(applyToData(formatString), da.format)
    ).flatten.map(indentIn)
  }

  override def defComponentNode(node: AstNode[Ast.DefComponent]) = {
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

  override def defComponentInstanceNode(node: AstNode[Ast.DefComponentInstance]) = {
    val dci = node.getData
    lines("def component instance") ++
    List(
      ident(dci.name),
      addPrefix("component", qualIdent) (dci.typeName.getData),
      addPrefix("base id", applyToData(expr)) (dci.baseId),
      addPrefix("queue size", applyToData(expr)) (dci.baseId),
      addPrefix("stack size", applyToData(expr)) (dci.baseId),
      addPrefix("priority", applyToData(expr)) (dci.baseId),
    ).flatten.map(indentIn)
  }

  override def defConstantNode(node: AstNode[Ast.DefConstant]) = {
    val dc = node.getData
    lines("def constant") ++
    (ident(dc.name) ++ expr(dc.value.getData)).map(indentIn)
  }

  override def defEnumNode(node: AstNode[Ast.DefEnum]) = {
    val de = node.getData
    lines("def enum") ++
    List(
      ident(de.name),
      linesOpt(applyToData(typeName), de.typeName),
      de.constants.map(annotateNode(defEnumConstant)).flatten
    ).flatten.map(indentIn)
  }

  override def defModuleNode(node: AstNode[Ast.DefModule]) = {
    val dm = node.getData
    lines("def module") ++
    (ident(dm.name) ++ dm.members.map(moduleMember).flatten).map(indentIn)
  }

  override def defPortNode(node: AstNode[Ast.DefPort]) = {
    val dp = node.getData
    lines("def port") ++
    List(
      ident(dp.name),
      formalParamList(dp.params),
      linesOpt(addPrefix("return", applyToData(typeName)), dp.returnType)
    ).flatten.map(indentIn)
  }

  override def defStructNode(node: AstNode[Ast.DefStruct]) = {
    val ds = node.getData
    lines("def struct") ++ 
    (
      ident(ds.name) ++
      ds.members.map(annotateNode(structTypeMember)).flatten ++ 
      linesOpt(applyToData(expr), ds.default)
    ).map(indentIn) 
  }

  override def defTopologyNode(node: AstNode[Ast.DefTopology]) = {
    val dt = node.getData
    lines("def topology") ++
    (ident(dt.name) ++ dt.members.map(topologyMember).flatten).map(indentIn)
  }

  override def default = throw new InternalError("AstWriter: Visitor not implemented")

  override def exprArray(enl: List[AstNode[Ast.Expr]]) =
    lines("expr array") ++
    enl.map(applyToData(expr)).flatten.map(indentIn)

  override def exprBinop(e1: Ast.Expr, op: Ast.Binop, e2: Ast.Expr) =
    lines("expr binop") ++
    (expr(e1) ++ binop(op) ++ expr(e2)).map(indentIn)
  
  override def exprDot(e: Ast.Expr, id: Ast.Ident) =
    lines("expr dot") ++
    (expr(e) ++ ident(id)).map(indentIn)

  override def exprIdent(id: Ast.Ident) = ident(id)

  override def exprLiteralBool(lb: Ast.LiteralBool) = {
    val s = lb match {
      case Ast.LiteralBool.True => "true"
      case Ast.LiteralBool.False => "false"
    }
    lines("literal bool " ++ s)
  }

  override def exprLiteralFloat(s: String) = lines("literal float " ++ s)

  override def exprLiteralInt(s: String) = lines("literal int " ++ s)

  override def exprLiteralString(s: String) = lines("literal string " ++ s)

  override def exprParen(e: Ast.Expr) =
    lines("expr paren") ++
    expr(e).map(indentIn)

  override def exprStruct(sml: List[Ast.StructMember]) =
    lines("expr struct") ++
    sml.map(structMember).flatten.map(indentIn)

  override def exprUnop(op: Ast.Unop, e: Ast.Expr) =
    lines("expr unop") ++
    (unop(op) ++ expr(e)).map(indentIn)

  override def specCommandNode(node: AstNode[Ast.SpecCommand]) = {
    def kind(k: Ast.SpecCommand.Kind) = k match {
      case Ast.SpecCommand.Async => "async"
      case Ast.SpecCommand.Guarded => "guarded"
      case Ast.SpecCommand.Sync => "sync"
    }
    val sc = node.getData
    lines("spec command node") ++
    List(
      lines("kind " ++ kind(sc.kind)),
      addPrefix("name", ident) (sc.name),
      formalParamList(sc.params),
      linesOpt(addPrefix("opcode", applyToData(expr)), sc.opcode),
      linesOpt(addPrefix("priority", applyToData(expr)), sc.priority),
      linesOpt(applyToData(queueFull), sc.queueFull)
    ).flatten.map(indentIn)
  }

  override def specCompInstanceNode(node: AstNode[Ast.SpecCompInstance]) =  {
    val ci = node.getData
    lines("spec comp instance") ++ (
      lines(visibility(ci.visibility)) ++
      qualIdent(ci.instance.getData)
    ).map(indentIn)
  }

  override def specConnectionGraphNode(node: AstNode[Ast.SpecConnectionGraph]) = {
    def direct(g: Ast.SpecConnectionGraph.Direct) = {
      def connection(c: Ast.SpecConnectionGraph.Connection) = {
        lines("connection") ++ (
          addPrefix("from port", qualIdent) (c.fromPort.getData) ++
          linesOpt(addPrefix("index", applyToData(expr)), c.fromIndex) ++
          addPrefix("to port", qualIdent) (c.toPort.getData) ++
          linesOpt(addPrefix("index", applyToData(expr)), c.toIndex)
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
        addPrefix("pattern", applyToData(expr)) (g.pattern)
      ).map(indentIn)
    }
    node.getData match {
      case g @ Ast.SpecConnectionGraph.Direct(_, _) => direct(g)
      case g @ Ast.SpecConnectionGraph.Pattern(_, _, _) => pattern(g)
    }
  }

  override def specEventNode(node: AstNode[Ast.SpecEvent]) = {
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
      linesOpt(addPrefix("id", applyToData(expr)), se.id),
      linesOpt(applyToData(formatString), se.format),
      linesOpt(addPrefix("throttle", applyToData(expr)), se.throttle),
    ).flatten.map(indentIn)
  }

  override def specIncludeNode(node: AstNode[Ast.SpecInclude]) = {
    val si = node.getData
    lines("spec include") ++ fileString(si.file).map(indentIn)
  }

  override def specInitNode(node: AstNode[Ast.SpecInit]) = {
    val si = node.getData
    lines("spec init") ++
    List(
      addPrefix("instance", applyToData(qualIdent)) (si.instance),
      addPrefix("phase", applyToData(expr)) (si.phase),
      {
        val code = si.code.split('\n').map(line).toList
        Line.joinLists (Line.Indent) (lines("code")) (" ") (code)
      }
    ).flatten.map(indentIn)
  }

  override def specInternalPortNode(node: AstNode[Ast.SpecInternalPort]) = {
    val sip = node.getData
    lines("spec internal port") ++
    List(
      ident(sip.name),
      formalParamList(sip.params),
      linesOpt(addPrefix("priority", applyToData(expr)), sip.priority),
      linesOpt(queueFull, sip.queueFull)
    ).flatten.map(indentIn)
  }

  override def specLocNode(node: AstNode[Ast.SpecLoc]) = {
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
      fileString(sl.file)
    ).map(indentIn)
  }

  override def specParamNode(node: AstNode[Ast.SpecParam]) = {
    val sp = node.getData
    lines("spec param") ++
    List(
      ident(sp.name),
      typeName(sp.typeName.getData),
      linesOpt(addPrefix("default", applyToData(expr)), sp.default),
      linesOpt(addPrefix("id", applyToData(expr)), sp.id),
      linesOpt(addPrefix("set opcode", applyToData(expr)), sp.setOpcode),
      linesOpt(addPrefix("save opcode", applyToData(expr)), sp.saveOpcode),
    ).flatten.map(indentIn)
  }

  override def specPortInstanceNode(node: AstNode[Ast.SpecPortInstance]) = {
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
        linesOpt(addPrefix("array size", applyToData(expr)), i.size),
        linesOpt(addPrefix("port type", applyToData(qualIdent)), i.port),
        linesOpt(addPrefix("priority", applyToData(expr)), i.priority),
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

  override def specTlmChannelNode(node: AstNode[Ast.SpecTlmChannel]) = {
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
        expr(en.getData)
      ).map(indentIn)
    }
    def limits(name: String, ls: List[Ast.SpecTlmChannel.Limit]) =
      ls.map(addPrefix(name, limit))
    val tc = node.getData
    lines("spec tlm channel") ++
    List(
      ident(tc.name),
      typeName(tc.typeName.getData),
      linesOpt(addPrefix("id", applyToData(expr)), tc.id),
      linesOpt(update, tc.update),
      linesOpt(applyToData(formatString), tc.format),
      limits("low", tc.low).flatten,
      limits("high", tc.high).flatten,
    ).flatten.map(indentIn)
  }

  override def specTopImportNode(node: AstNode[Ast.SpecTopImport]) = {
    val ti = node.getData
    lines("spec top import") ++
    qualIdent(ti.top.getData).map(indentIn)
  }

  override def specUnusedPortsNode(node: AstNode[Ast.SpecUnusedPorts]) = {
    val up = node.getData
    lines("spec unused ports") ++
    up.ports.map(applyToData(qualIdent)).flatten.map(indentIn)
  }

  override def transUnit(tu: Ast.TransUnit) = tu.members.map(tuMember).flatten

  override def typeNameBool = lines("bool")

  override def typeNameFloat(tnf: Ast.TypeNameFloat) = {
    val s = tnf.name match {
      case Ast.F32() => "F32"
      case Ast.F64() => "F64"
    }
    lines(s)
  }

  override def typeNameInt(tni: Ast.TypeNameInt) = {
    val s = tni.name match {
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

  override def typeNameQualIdent(tnqid: Ast.TypeNameQualIdent) = 
    qualIdent(tnqid.name)

  override def typeNameString = lines("string")

  private def addPrefix[T](s: String, f: T => List[Line]): T => List[Line] =
    (t: T) => joinLists (lines(s)) (" ") (f(t))

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

  private def componentMember(cm: Ast.ComponentMember) = {
    val (a1, cmn, a2) = cm.node
    val l = matchComponentMemberNode(cmn)
    annotate(a1, l, a2)
  }

  private def defEnumConstant(dec: Ast.DefEnumConstant) =
    lines("def enum constant") ++
    List(
      ident(dec.name),
      linesOpt(applyToData(expr), dec.value)
    ).flatten.map(indentIn)

  private def expr(e: Ast.Expr): List[Line] = matchExpr(e)

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
      typeName(fp.typeName.getData),
      linesOpt(applyToData(expr), fp.size)
    ).flatten.map(indentIn)
  }

  private def formalParamList(params: Ast.FormalParamList) =
    params.map(annotateNode(formalParam)).flatten

  private def formatString(s: String) = lines("format " ++ s)

  private def ident(s: String) = lines("ident " ++ s)

  private def indentIn(line: Line) = line.indentIn(2)

  private def joinLists = Line.joinLists(Line.NoIndent) _

  private def line(s: String) = Line(string = s)

  private def lines(s: String) = List(line(s))

  private def linesOpt[T](f: T => List[Line], o: Option[T]) =
    o match {
      case Some(x) => f(x)
      case None => Nil
    }

  private def moduleMember(mm: Ast.ModuleMember) = {
    val (a1, mmn, a2) = mm.node
    val l = matchModuleMemberNode(mmn)
    annotate(a1, l, a2)
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

  private def structMember(sm: Ast.StructMember) =
    lines("struct member") ++ 
    (ident(sm.name) ++ expr(sm.value.getData)).map(indentIn)

  private def structTypeMember(stm: Ast.StructTypeMember) = {
    lines("struct type member") ++ 
    List(
      ident(stm.name),
      typeName(stm.typeName.getData),
      linesOpt(applyToData(formatString), stm.format)
    ).flatten.map(indentIn)
  }

  private def todo = lines("TODO")

  private def topologyMember(tm: Ast.TopologyMember) = {
    val (a1, tmn, a2) = tm.node
    val l = matchTopologyMemberNode(tmn)
    annotate(a1, l, a2)
  }

  private def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  private def typeName(tn: Ast.TypeName) =
    joinLists (lines("type name")) (" ") (matchTypeName(tn))

  private def unop(op: Ast.Unop) =
    op match {
      case Ast.Unop.Minus => lines("unop -")
    }

  private def visibility(v: Ast.Visibility) = v match {
    case Ast.Visibility.Private => "private"
    case Ast.Visibility.Public => "public"
  }

}
