package fpp.compiler.codegen

import fpp.compiler.ast._

/** Write out an FPP AST */
object AstWriter extends AstUnitVisitor[List[Line]] {

  def componentMember(cm: Ast.ComponentMember) = {
    val (a, cmn, a1) = cm.node
    val l = matchComponentMemberNode(cmn)
    annotate (a) (a1) (l)
  }

  def defAbsType(dat: Ast.DefAbsType) =
    lines("def abs type") ++ ident(dat.name).map(indentIn)

  def defArray(da: Ast.DefArray) =
    lines("def array") ++
    List(
      ident(da.name),
      expr(da.size.getData),
      typeName(da.eltType.getData),
      linesOpt(applyToData(expr), da.default),
      linesOpt(applyToData(formatString), da.format)
    ).flatten.map(indentIn)

  def defComponent(dc: Ast.DefComponent) = {
    val Ast.DefComponent(ck, id, cml) = dc
    val kind = ck match {
      case Ast.ComponentKind.Active => "active"
      case Ast.ComponentKind.Passive => "passive"
      case Ast.ComponentKind.Queued => "queued"
    }
    lines("def component") ++
    (
      lines("kind " ++ kind) ++ 
      ident(id) ++ 
      cml.map(componentMember).flatten
    ).map(indentIn)
  }

  def defComponentInstance(dc: Ast.DefComponentInstance) = todo
  
  def defConstant(dc: Ast.DefConstant) =
    lines("def constant") ++
    (ident(dc.name) ++ expr(dc.value.getData)).map(indentIn)

  def defEnum(de: Ast.DefEnum) =
    lines("def enum") ++
    List(
      ident(de.name),
      linesOpt(applyToData(typeName), de.typeName),
      {
        def f(ana: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
          val (a1, node, a2) = ana
          annotate (a1) (a2) (defEnumConstant(node.getData))
        }
        de.constants.map(f).flatten
      }
    ).flatten.map(indentIn)

  def defEnumConstant(dec: Ast.DefEnumConstant) =
    lines("def enum constant") ++
    List(
      ident(dec.name),
      linesOpt(applyToData(expr), dec.value)
    ).flatten.map(indentIn)

  def defModule(dm: Ast.DefModule) =
    lines("def module") ++
    (ident(dm.name) ++ dm.members.map(moduleMember).flatten).map(indentIn)

  def defPort(dp: Ast.DefPort) =
    todo
  /*
  and defPort (DefPort (id, fpnal, tnno)) =
  let
    val l1 = lines "def port"
    val l2 = List.map indentIn (ident id)
    fun f (a, fpn, a') = annotate a a' (formalParam (data fpn))
    val l3 = List.concat (List.map f fpnal)
    val l3 = List.map indentIn l3
    val l4 = case tnno of
                  SOME tnn => List.map indentIn (typeName (data tnn))
                | NONE => []
  in
    l1 @ l2 @ l3 @ l4
  end
  */

  def defStruct(ds: Ast.DefStruct) = {
    def f(ana: Ast.Annotated[AstNode[Ast.StructTypeMember]]) = {
      val (a1, node, a2) = ana
      annotate (a1) (a2) (structTypeMember(node.getData))
    }
    lines("def struct") ++ 
    ident(ds.name) ++
    (
      ds.members.map(f).flatten ++ 
      linesOpt(applyToData(expr), ds.default)
    ).map(indentIn) }

  def defTopology(ds: Ast.DefTopology) = todo

  def expr(e: Ast.Expr) = e match {
    case Ast.ExprBinop(e1, op, e2) => exprBinop(e1.getData, op, e2.getData)
    case Ast.ExprArray(enl) => exprArray(enl)
    case Ast.ExprDot(en, id) => exprDot(en.getData, id)
    case Ast.ExprIdent(id) => ident(id)
    case Ast.ExprLiteralInt(s) => lines("literal int " ++ s)
    case Ast.ExprLiteralFloat(s) => lines("literal float " ++ s)
    case Ast.ExprLiteralString(s) => lines("literal string " ++ s)
    case Ast.ExprLiteralBool(lb) => exprLiteralBool(lb)
    case Ast.ExprParen(en) => exprParen(en.getData)
    case Ast.ExprStruct(sml) => exprStruct(sml)
    case Ast.ExprUnop(op, en) => exprUnop(op, en.getData)
  }

  def fileString(s: String) = lines("file " ++ s)

  def formatString(s: String) = lines("format " ++ s)

  def moduleMember(mm: Ast.ModuleMember) = {
    val (a, mmn, a1) = mm.node
    val l = matchModuleMemberNode(mmn)
    annotate (a) (a1) (l)
  }

  def specCommand(sc: Ast.SpecCommand) = todo

  def specEvent(se: Ast.SpecEvent) = todo

  def specInclude(si: Ast.SpecInclude) =
    lines("spec include") ++ fileString(si.file).map(indentIn)

  def specInit(sl: Ast.SpecInit) = todo

  def specInternalPort(sip: Ast.SpecInternalPort) = todo

  def specLoc(sl: Ast.SpecLoc) = {
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
      lines("symbol " ++ qualIdent(sl.symbol.getData)) ++ 
      fileString(sl.file)
    ).map(indentIn)
  }

  def specParam(sp: Ast.SpecParam) = todo

  def specPortInstance(spi: Ast.SpecPortInstance) = todo

  def specTlmChannel(stc: Ast.SpecTlmChannel) = todo

  def structMember(sm: Ast.StructMember) =
    lines("struct member") ++ 
    (ident(sm.name) ++ expr(sm.value.getData)).map(indentIn)

  def structTypeMember(stm: Ast.StructTypeMember) = {
    val Ast.StructTypeMember(id, tnn, sno) = stm
    val l1 = joinLists (lines(id)) (": ") (typeName(tnn.data))
    val l2 = linesOpt((sn: AstNode[String]) => formatString(sn.getData), sno)
    joinLists (l1) (" ") (l2)
  }

  def transUnit(tu: Ast.TransUnit) = tu.members.map(tuMember).flatten

  def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  def typeName(tn: Ast.TypeName) = {
    val s = tn match {
      case Ast.TypeNameBool => "bool"
      case Ast.TypeNameFloat(Ast.F32) => "F32"
      case Ast.TypeNameFloat(Ast.F64) => "F64"
      case Ast.TypeNameInt(Ast.I8) => "I8"
      case Ast.TypeNameInt(Ast.I16) => "I16"
      case Ast.TypeNameInt(Ast.I32) => "I32"
      case Ast.TypeNameInt(Ast.I64) => "I64"
      case Ast.TypeNameInt(Ast.U8) => "U8"
      case Ast.TypeNameInt(Ast.U16) => "U16"
      case Ast.TypeNameInt(Ast.U32) => "U32"
      case Ast.TypeNameInt(Ast.U64) => "U64"
      case Ast.TypeNameQualIdent(qidn) => qualIdent(qidn.getData)
      case Ast.TypeNameString => "string"
    }
    lines("type name " ++ s)
  }

  private def applyToData[A,B](f: A => B): AstNode[A] => B = (a: AstNode[A]) => f(a.getData)

  private def binop(op: Ast.Binop) =
    op match {
      case Ast.Binop.Add => lines("binop +")
      case Ast.Binop.Div => lines("binop /")
      case Ast.Binop.Mul => lines("binop *")
      case Ast.Binop.Sub => lines("binop -")
    }

  private def exprArray(enl: List[AstNode[Ast.Expr]]) =
    lines("expr array") ++
    enl.map((en: AstNode[Ast.Expr]) => expr(en.getData)).flatten.map(indentIn)

  private def exprBinop(e1: Ast.Expr, op: Ast.Binop, e2: Ast.Expr) =
    lines("expr binop") ++
    (expr(e1) ++ binop(op) ++ expr(e2)).map(indentIn)
  
  private def exprDot(e: Ast.Expr, id: Ast.Ident) =
    lines("expr dot") ++
    (expr(e) ++ ident(id)).map(indentIn)

  private def exprLiteralBool(lb: Ast.LiteralBool) = {
    val s = lb match {
      case Ast.LiteralBool.True => "true"
      case Ast.LiteralBool.False => "false"
    }
    lines("literal bool " ++ s)
  }

  private def exprParen(e: Ast.Expr) =
    lines("expr paren") ++
    expr(e).map(indentIn)

  private def exprStruct(sml: List[Ast.StructMember]) =
    lines("expr struct") ++
    sml.map(structMember).flatten.map(indentIn)

  private def exprUnop(op: Ast.Unop, e: Ast.Expr) =
    lines("expr unop") ++
    (unop(op) ++ expr(e)).map(indentIn)

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

  private def qualIdent(qid: Ast.QualIdent): String =
    qid match {
      case Nil => ""
      case id :: Nil => id
      case (id :: qid1) => id ++ "." ++ qualIdent(qid1)
    }
    
  private def annotate (pre: List[String]) (post: List[String]) (lines: List[Line]) = {
    def preLine(s: String) = line("@ " ++ s)
    val pre1 = pre.map(preLine)
    def postLine(s: String) = line("@< " ++ s)
    val post1 = post.map(postLine)
    pre1 ++ lines ++ post1
  }

  private def unop(op: Ast.Unop) =
    op match {
      case Ast.Unop.Minus => lines("unop -")
    }

  private val todo = lines("TODO")

  /*
  and defPort (DefPort (id, fpnal, tnno)) =
  let
    val l1 = lines "def port"
    val l2 = List.map indentIn (ident id)
    fun f (a, fpn, a') = annotate a a' (formalParam (data fpn))
    val l3 = List.concat (List.map f fpnal)
    val l3 = List.map indentIn l3
    val l4 = case tnno of
                  SOME tnn => List.map indentIn (typeName (data tnn))
                | NONE => []
  in
    l1 @ l2 @ l3 @ l4
  end

  and defPortInstance (DefPortInstanceGeneral (dpigk, id, eno, il, eno', qfo)) =
      let
        val l1 = lines "def port instance general"
        val l2 = List.map indentIn (lines (defPortInstanceGeneralKind dpigk))
        val l3 = List.map indentIn (ident id)
        val l4 = case eno of
                      SOME en => 
                      let
                        val note = lines "[ array size ]"
                        val l = joinLists note " " (expr (data en))
                      in
                        List.map indentIn l
                      end
                    | NONE => []
        val l5 = List.map indentIn (lines ("qual ident "^(qualIdent il)))
        val l6 = case eno' of
                      SOME en => 
                      let
                        val note =  lines "[ priority ]"
                        val l = joinLists note " " (expr (data en))
                      in
                        List.map indentIn l
                      end
                    | NONE => []
        val l7 = case qfo of
                      SOME qf => List.map indentIn (lines (queueFull qf))
                    | NONE => []
      in
        l1 @ l2 @ l3 @ l4 @ l5 @ l6 @ l7
      end
    | defPortInstance (DefPortInstanceSpecial (dpisk, id)) =
      let
        val l1 = lines "def port instance special"
        val l2 = List.map indentIn (lines (defPortInstanceSpecialKind dpisk))
        val l3 = List.map indentIn (ident id)
      in
        l1 @ l2 @ l3
      end

  and defPortInstanceGeneralKind AsyncInput = "kind async input"
    | defPortInstanceGeneralKind GuardedInput = "kind guarded input"
    | defPortInstanceGeneralKind InternalInput = "kind internal input"
    | defPortInstanceGeneralKind Output = "kind output"
    | defPortInstanceGeneralKind SyncInput = "kind sync input"

  and defPortInstanceSpecialKind Command = "kind command"
    | defPortInstanceSpecialKind CommandReg = "kind command reg"
    | defPortInstanceSpecialKind CommandResp = "kind command resp"
    | defPortInstanceSpecialKind Event = "kind event"
    | defPortInstanceSpecialKind ParamGet = "kind param get"
    | defPortInstanceSpecialKind ParamSet = "kind param set"
    | defPortInstanceSpecialKind Telemetry = "kind telemetry"
    | defPortInstanceSpecialKind Time = "kind time"


  and formalParam (FormalParam (fpk, id, tnn, eno)) =
  let
    val l1 = lines "formal param"
    val l2 = List.map indentIn (lines (formalParamKind fpk))
    val l3 = List.map indentIn (ident id)
    val l4 = List.map indentIn (typeName (data tnn))
    val l5 = case eno of
                  SOME en => List.map indentIn (expr (data en))
                | NONE => []
  in
    l1 @ l2 @ l3 @ l4 @ l5
  end

  and formalParamKind (FormalParamRef) = "kind ref"
    | formalParamKind (FormalParamValue) = "kind value"

  and queueFull Assert = "queue full assert"
    | queueFull Block = "queue full block"
    | queueFull Drop = "queue full drop"


  */

}

