package fpp.compiler.codegen

import fpp.compiler.ast._

/** Write out an FPP AST */
object AstWriter extends LineWriter {

  def defAbsType(dat: Ast.DefAbsType) = lines("[ def abs type ]")

  def defArray(da: Ast.DefArray) = lines("[ def array ]")

  def defComponent(dc: Ast.DefComponent) = lines("[ def component ]")

  def defComponentInstance(dc: Ast.DefComponentInstance) = lines("[ def component instance ]")
  
  def defConstant(dc: Ast.DefConstant) = {
    val Ast.DefConstant(id, en) = dc
    lines("def constant") ++
    ident(id).map(indentIn) ++
    expr(en.getData).map(indentIn)
  }

  def defEnum(de: Ast.DefEnum) = lines("[ def enum ]")

  def defEnumConstant(dec: Ast.DefEnumConstant) = lines("[ def enum constant ]")

  def defModule(dm: Ast.DefModule) = lines("[ def module ]")

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

  def specInclude(sl: Ast.SpecInclude) = lines("[ spec include ]")

  def specInit(sl: Ast.SpecInit) = lines("[ spec init ]")

  def specLoc(sl: Ast.SpecLoc) = lines("[ spec loc ]")

  def structMember(sm: Ast.StructMember) = lines("[ struct member ]")

  def transUnit(tu: Ast.TransUnit) = {
    val Ast.TransUnit(members) = tu
    members.map(tuMember).flatten
  }

  def tuMember(tum: Ast.TUMember) = {
    val Ast.TUMember((a, tumn, a1)) = tum
    def annotate1 = annotate (a) (a1) _
    tumn match {
      case Ast.TUMember.DefAbsType(node) => annotate1(defAbsType(node.getData))
      case Ast.TUMember.DefArray(node) => annotate1(defArray(node.getData))
      case Ast.TUMember.DefComponent(node) => annotate1(defComponent(node.getData))
      case Ast.TUMember.DefComponentInstance(node) => annotate1(defComponentInstance(node.getData))
      case Ast.TUMember.DefConstant(node) => annotate1(defConstant(node.getData))
      case Ast.TUMember.DefEnum(node) => annotate1(defEnum(node.getData))
      case Ast.TUMember.DefModule(node) => annotate1(defModule(node.getData))
      case Ast.TUMember.DefPort(node) => annotate1(defPort(node.getData))
      case Ast.TUMember.DefStruct(node) => annotate1(defStruct(node.getData))
      case Ast.TUMember.DefTopology(node) => annotate1(defTopology(node.getData))
      case Ast.TUMember.SpecInclude(node) => annotate1(specInclude(node.getData))
      case Ast.TUMember.SpecInit(node) => annotate1(specInit(node.getData))
      case Ast.TUMember.SpecLoc(node) => annotate1(specLoc(node.getData))
    }
  }

  def typeName(tn: Ast.TypeName) = lines("[ type name ]")

  private def annotate (pre: List[String]) (post: List[String]) (lines: List[Line]) = {
    def preLine(s: String) = line("@ " ++ s)
    val pre1 = pre.map(preLine)
    def postLine(s: String) = line("@< " ++ s)
    val post1 = post.map(postLine)
    pre1 ++ lines ++ post1
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
    expr(e1).map(indentIn) ++
    binop(op).map(indentIn) ++
    expr(e2).map(indentIn)
  
  private def exprDot(e: Ast.Expr, id: Ast.Ident) =
    lines("expr dot") ++
    expr(e).map(indentIn) ++
    ident(id).map(indentIn)

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
    unop(op).map(indentIn) ++
    expr(e).map(indentIn)

  private def ident(s: String) = lines("ident " ++ s)

  private def indentIn(line: Line) = line.indentIn(2)

  private def joinLists = Line.joinLists(Line.NoIndent) _

  private def line(s: String) = Line(string = s)

  private def lines(s: String) = List(line(s))

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

  and defAbsType (DefAbsType id) = 
  let
    val l1 = lines "def abs type"
    val l2 = List.map indentIn (ident id)
  in
    l1 @ l2
  end

  and defArray (DefArray (id, en, tnn, eno)) =
  let
    val l1 = lines "def array"
    val l2 = List.map indentIn (ident id)
    val l3 = List.map indentIn (expr (data en))
    val l4 = List.map indentIn (typeName (data tnn))
    val l5 = case eno of
         SOME en => List.map indentIn (expr (data en))
       | NONE => []
  in
    l1 @ l2 @ l3 @ l4 @ l5
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

  and defEnum (DefEnum (id, tnno, enal)) = 
  let
    val l1 = lines "def enum"
    val l2 = List.map indentIn (ident id)
    val l3 = case tnno of
                  SOME tnn => typeName (data tnn)
                | NONE => []
    fun f (a, x, a') = annotate a a' (defEnumConstant (data x))
    val l4 = List.concat (List.map f enal)
    val l4 = List.map indentIn l4
  in
    l1 @ l2 @ l3 @ l4
  end

  and defEnumConstant (DefEnumConstant (id, eno)) =
  let
    val l1 = lines "def enum constant"
    val l2 = List.map indentIn (ident id)
    val l3 = case eno of
                  SOME en => List.map indentIn (expr (data (en)))
                | NONE => []
  in
    l1 @ l2 @ l3
  end

  and defModule (DefModule (id, tuml)) = 
  let
    val l1 = lines "def module"
    val l2 = List.map indentIn (ident id)
    val l3 = tuMemberList tuml
    val l3 = List.map indentIn l3
  in
    l1 @ l2 @ l3
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

  and qualIdent [] = ""
    | qualIdent (id :: []) = id
    | qualIdent (id :: ids) = id^"."^(qualIdent ids)

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

  and transUnit (TransUnit tuml) = tuMemberList tuml

  and typeName TypeNameBool = lines "type name bool"
    | typeName (TypeNameFloat F32) = lines "type name F32"
    | typeName (TypeNameFloat F64) = lines "type name F64"
    | typeName (TypeNameInt I8) = lines "type name I8"
    | typeName (TypeNameInt I16) = lines "type name I16"
    | typeName (TypeNameInt I32) = lines "type name I32"
    | typeName (TypeNameInt I64) = lines "type name I64"
    | typeName (TypeNameInt U8) = lines "type name U8"
    | typeName (TypeNameInt U16) = lines "type name U16"
    | typeName (TypeNameInt U32) = lines "type name U32"
    | typeName (TypeNameInt U64) = lines "type name U64"
    | typeName (TypeNameQualIdent il) = lines ("type name "^(qualIdent il))
    | typeName (TypeNameString) = lines "type name string"

  */

}

