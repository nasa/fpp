package fpp.compiler.codegen

import fpp.compiler.ast._

/** Write out an FPP AST */
object AstWriter extends LineWriter {

  def defAbsType(dat: Ast.DefAbsType) = {
    val Ast.DefAbsType(id) = dat
    lines("def abs type") ++ ident(id).map(indentIn)
  }

  def defArray(da: Ast.DefArray) = {
    val Ast.DefArray(id, en, tnn, eno, sno) = da
    lines("def array") ++
    List(
      ident(id),
      expr(en.getData),
      typeName(tnn.getData),
      linesOpt((en: AstNode[Ast.Expr]) => expr(en.getData), eno),
      linesOpt((sn: AstNode[String]) => formatString(sn.getData), sno)
    ).flatten.map(indentIn)
  }

  def defComponent(dc: Ast.DefComponent) = lines("[ def component ]")

  def defComponentInstance(dc: Ast.DefComponentInstance) = lines("[ def component instance ]")
  
  def defConstant(dc: Ast.DefConstant) = {
    val Ast.DefConstant(id, en) = dc
    lines("def constant") ++
    (ident(id) ++ expr(en.getData)).map(indentIn)
  }

  def defEnum(de: Ast.DefEnum) = {
    val Ast.DefEnum(id, tnno, decnal) = de
    lines("def enum") ++
    List(
      ident(id),
      linesOpt((tnn: AstNode[Ast.TypeName]) => typeName(tnn.getData), tnno),
      {
        def f(sns: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
          val (a1, node, a2) = sns
          annotate (a1) (a2) (defEnumConstant(node.getData))
        }
        decnal.map(f).flatten
      }
    ).flatten.map(indentIn)
  }

  def defEnumConstant(dec: Ast.DefEnumConstant) = {
    val Ast.DefEnumConstant(id, eno) = dec
    lines("def enum constant") ++
    List(
      ident(id),
      linesOpt((en: AstNode[Ast.Expr]) => expr(en.getData), eno)
    ).flatten.map(indentIn)
  }

  def defModule(dm: Ast.DefModule) = {
    val Ast.DefModule(id, mml) = dm
    lines("def module") ++
    (ident(id) ++ mml.map(moduleMember).flatten).map(indentIn)
  }

  def defPort(dm: Ast.DefPort) = lines("[ def port ]")

  def defStruct(ds: Ast.DefStruct) = lines("[ def struct ]")

  def defTopology(ds: Ast.DefTopology) = lines("[ def struct ]")

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

  def formatString(s: String) = lines("format " ++ s)

  def moduleMember(mm: Ast.ModuleMember) = {
    val Ast.ModuleMember((a, mmn, a1)) = mm
    def annotate1 = annotate (a) (a1) _
    mmn match {
      case Ast.ModuleMember.DefAbsType(node) => annotate1(defAbsType(node.getData))
      case Ast.ModuleMember.DefArray(node) => annotate1(defArray(node.getData))
      case Ast.ModuleMember.DefComponent(node) => annotate1(defComponent(node.getData))
      case Ast.ModuleMember.DefComponentInstance(node) => annotate1(defComponentInstance(node.getData))
      case Ast.ModuleMember.DefConstant(node) => annotate1(defConstant(node.getData))
      case Ast.ModuleMember.DefEnum(node) => annotate1(defEnum(node.getData))
      case Ast.ModuleMember.DefModule(node) => annotate1(defModule(node.getData))
      case Ast.ModuleMember.DefPort(node) => annotate1(defPort(node.getData))
      case Ast.ModuleMember.DefStruct(node) => annotate1(defStruct(node.getData))
      case Ast.ModuleMember.DefTopology(node) => annotate1(defTopology(node.getData))
      case Ast.ModuleMember.SpecInclude(node) => annotate1(specInclude(node.getData))
      case Ast.ModuleMember.SpecInit(node) => annotate1(specInit(node.getData))
      case Ast.ModuleMember.SpecLoc(node) => annotate1(specLoc(node.getData))
    }
  }

  def specInclude(sl: Ast.SpecInclude) = lines("[ spec include ]")

  def specInit(sl: Ast.SpecInit) = lines("[ spec init ]")

  def specLoc(sl: Ast.SpecLoc) = lines("[ spec loc ]")

  def structMember(sm: Ast.StructMember) = lines("[ struct member ]")

  def transUnit(tu: Ast.TransUnit) = {
    val Ast.TransUnit(members) = tu
    members.map(tuMember).flatten
  }

  def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  def typeName(tn: Ast.TypeName) =
    tn match {
      case Ast.TypeNameBool => lines("type name bool")
      case Ast.TypeNameFloat(Ast.F32) => lines("type name F32")
      case Ast.TypeNameFloat(Ast.F64) => lines("type name F64")
      case Ast.TypeNameInt(Ast.I8) => lines("type name I8")
      case Ast.TypeNameInt(Ast.I16) => lines("type name I16")
      case Ast.TypeNameInt(Ast.I32) => lines("type name I32")
      case Ast.TypeNameInt(Ast.I64) => lines("type name I64")
      case Ast.TypeNameInt(Ast.U8) => lines("type name U8")
      case Ast.TypeNameInt(Ast.U16) => lines("type name U16")
      case Ast.TypeNameInt(Ast.U32) => lines("type name U32")
      case Ast.TypeNameInt(Ast.U64) => lines("type name U64")
      case Ast.TypeNameQualIdent(qidn) => lines("type name " ++ qualIdent(qidn.getData))
      case Ast.TypeNameString => lines("type name string")
    }

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

  private def exprLiteralBool(lb: Ast.LiteralBool) =
    lb match {
      case Ast.LiteralBool.True => lines("literal bool true")
      case Ast.LiteralBool.False => lines("literal bool false")
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
  and componentKind ComponentActive = "kind active"
    | componentKind ComponentPassive = "kind passive"
    | componentKind ComponentQueued = "kind queued"

  and componentMember (ComponentMember (a, cmn, a')) =
  let
    val annotate = annotate a a'
  in
    case cmn of
       ComponentDefArray node => annotate (defArray (data node))
     | ComponentDefConstant node => annotate (defConstant (data node))
     | ComponentDefEnum node => annotate (defEnum (data node))
     | ComponentDefPortInstance node => annotate (defPortInstance (data node))
     | ComponentDefStruct node => annotate (defStruct (data node))
  end

  and defComponent (DefComponent (ck, id, cml)) =
  let
    val l1 = lines "def component"
    val l2 = List.map indentIn (lines (componentKind ck))
    val l3 = List.map indentIn (ident id)
    val l4 = List.concat (List.map componentMember cml)
    val l4 = List.map indentIn l4
  in
    l1 @ l2 @ l3 @ l4
  end

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

  and defStruct (DefStruct (id, stmnal, eno)) =
  let
    val l1 = lines "def struct"
    val l2 = List.map indentIn (ident id)
    fun f (a, x, a') = annotate a a' (structTypeMember (data x))
    val l3 = List.concat (List.map f stmnal)
    val l3 = List.map indentIn l3
  in
    l1 @ l2 @ l3
  end

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

  and specLoc (SpecLoc (slk, il, s)) = 
  let
    val l1 = lines "spec loc"
    val l2 = List.map indentIn (specLocKind slk)
    val qi = qualIdent il
    val l3 = List.map indentIn (lines ("qual ident "^(qualIdent il)))
    val l4 = List.map indentIn (lines ("location \""^s^"\""))
  in
    l1 @ l2 @ l3 @ l4
  end

  and specLocKind SpecLocComponent = lines "spec loc kind component"
    | specLocKind SpecLocConstant = lines "spec loc kind constant"
    | specLocKind SpecLocComponentInstance = lines "spec loc kind component instance"
    | specLocKind SpecLocPort = lines "spec loc kind port"
    | specLocKind SpecLocType = lines "spec loc kind type"
    | specLocKind Topology = lines "spec loc kind topology"

  and structMember (StructMember (id, en)) =
  let
    val l1 = lines "struct member"
    val l2 = List.map indentIn (ident id)
    val l3 = List.map indentIn (expr (data en))
  in
    l1 @ l2 @ l3
  end

  and structTypeMember (StructTypeMember (id, tnn)) =
  let
    val id = lines id
    val tn = typeName (data tnn)
  in
    joinLists id " : " tn
  end

  */

}

