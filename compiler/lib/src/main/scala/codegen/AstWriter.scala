package fpp.compiler.codegen

import fpp.compiler.ast._

/** Write out an FPP AST */
object AstWriter extends AstUnitVisitor[List[Line]] {

  override def defAbsTypeNode(node: AstNode[Ast.DefAbsType]) =
    lines("def abs type") ++ ident(node.getData.name).map(indentIn)

  override def defArrayNode(node: AstNode[Ast.DefArray]) = {
    val da = node.getData
    lines("def array") ++
    List(
      ident(da.name),
      expr(da.size.getData),
      typeName(da.eltType.getData),
      linesOpt(applyToData(expr), da.default),
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
      linesOpt(applyToData(typeName), dp.returnType)
    ).flatten.map(indentIn)
  }

  override def defStructNode(node: AstNode[Ast.DefStruct]) = {
    val ds = node.getData
    lines("def struct") ++ 
    ident(ds.name) ++
    (
      ds.members.map(annotateNode(structTypeMember)).flatten ++ 
      linesOpt(applyToData(expr), ds.default)
    ).map(indentIn) 
  }

  override def default = lines("TODO")

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

  override def specIncludeNode(node: AstNode[Ast.SpecInclude]) = {
    val si = node.getData
    lines("spec include") ++ fileString(si.file).map(indentIn)
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
      lines("symbol " ++ qualIdentString(sl.symbol.getData)) ++ 
      fileString(sl.file)
    ).map(indentIn)
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

  override def transUnit(tu: Ast.TransUnit) = tu.members.map(tuMember).flatten

  override def typeNameBool = lines("bool")

  override def typeNameFloat(tnf: Ast.TypeNameFloat) = {
    val s = tnf.name match {
      case Ast.F32() => "F32()"
      case Ast.F64() => "F64()"
    }
    lines(s)
  }

  override def typeNameInt(tni: Ast.TypeNameInt) = {
    val s = tni.name match {
      case Ast.I8() => "I8()"
      case Ast.I16() => "I16()"
      case Ast.I32() => "I32()"
      case Ast.I64() => "I64()"
      case Ast.U8() => "U8()"
      case Ast.U16() => "U16()"
      case Ast.U32() => "U32()"
      case Ast.U64() => "U64()"
    }
    lines(s)
  }

  override def typeNameQualIdent(tnqid: Ast.TypeNameQualIdent) = 
    qualIdent(tnqid.name)

  override def typeNameString = lines("string")

  private def addPrefix[T](s: String, f: T => List[Line]): T => List[Line] =
    (t: T) => joinLists (lines(s)) (" ") (f(t))

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
    lines(qualIdentString(qid))
    
  private def annotate(pre: List[String], lines: List[Line], post: List[String]) = {
    def preLine(s: String) = line("@ " ++ s)
    val pre1 = pre.map(preLine)
    def postLine(s: String) = line("@< " ++ s)
    val post1 = post.map(postLine)
    pre1 ++ lines ++ post1
  }

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
    val Ast.StructTypeMember(id, tnn, sno) = stm
    val l1 = joinLists (lines(id)) (": ") (typeName(tnn.data))
    val l2 = linesOpt(applyToData(formatString), sno)
    joinLists (l1) (" ") (l2)
  }

  private def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  private def typeName(tn: Ast.TypeName) =
    joinLists (lines("type name")) (" ") (matchTypeName(tn))

  private def unop(op: Ast.Unop) =
    op match {
      case Ast.Unop.Minus => lines("unop -")
    }

}
