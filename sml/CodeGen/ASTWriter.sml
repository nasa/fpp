(* ----------------------------------------------------------------------
 * FPPWriter.sml: implement FPPWriter.sig
 * Author: Rob Bocchino
 * ----------------------------------------------------------------------*)

structure ASTWriter :> LINE_WRITER =
struct

  open Ast
  open AstNode

  val indentIn = Line.indentIn 2

  val line = Line.create

  val joinLists = Line.joinLists Line.NoIndent

  fun lines s = [ line s ]

  fun ident id = lines ("ident "^id)

  fun annotate pre post lines = 
  let
    fun preLine l = line ("@ "^l)
    val pre = List.map preLine pre
    fun postLine l = line ("@< "^l)
    val post = List.map postLine post
  in
    pre @ lines @ post
  end

  and binop Add = lines "binop +"
    | binop Div = lines "binop /"
    | binop Mul = lines "binop *"
    | binop Sub = lines "binop -"

  and componentKind ComponentActive = "kind active"
    | componentKind ComponentPassive = "kind passive"
    | componentKind ComponentQueued = "component kind queued"

  and componentMember (ComponentMember (a, cmn, a')) =
  let
    val annotate = annotate a a'
  in
    case cmn of
       ComponentDefArray node => annotate (defArray (data node))
     | ComponentDefConstant node => annotate (defConstant (data node))
     | ComponentDefEnum node => annotate (defEnum (data node))
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
    val l2 = List.map indentIn (lines ("kind "^(componentKind ck)))
    val l3 = List.map indentIn (ident id)
    val l4 = List.concat (List.map componentMember cml)
    val l4 = List.map indentIn l3
  in
    l1 @ l2 @ l3 @ l4
  end

  and defConstant (DefConstant (id, en)) = 
  let
    val l1 = lines "def constant"
    val l2 = List.map indentIn (ident id)
    val l3 = List.map indentIn (expr (data en))
  in
    l1 @ l2 @ l3
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

  and expr (ExprArray enl) = exprArray enl
    | expr (ExprBinop (en, b, en')) = exprBinop (data en) b (data en')
    | expr (ExprDot (en, id)) = exprDot (data en) id
    | expr (ExprIdent id) = ident id
    | expr (ExprLiteralInt s) = lines ("literal int "^s)
    | expr (ExprLiteralFloat s) = lines ("literal float "^s)
    | expr (ExprLiteralString s) = lines ("literal string \""^s^"\"")
    | expr (ExprLiteralBool lb) = exprLiteralBool lb
    | expr (ExprParen en) = exprParen (data en)
    | expr (ExprStruct sml) = exprStruct sml
    | expr (ExprUnop (u, en)) = exprUnop u (data en)

  and exprArray enl =
  let
    val l1 = lines "expr array"
    val l2 = List.map (expr o data) enl
    val l2 = List.concat l2
    val l2 = List.map indentIn l2
  in
    l1 @ l2
  end

  and exprBinop e b e' =
  let
    val l1 = lines "expr binop"
    val l2 = List.map indentIn (expr e)
    val l3 = List.map indentIn (binop b)
    val l4 = List.map indentIn (expr e')
  in
    l1 @ l2 @ l3 @ l4
  end

  and exprDot e id =
  let
    val l1 = lines "expr dot"
    val l2 = List.map indentIn (expr e)
    val l3 = List.map indentIn (ident id)
  in
    l1 @ l2 @ l3
  end

  and exprLiteralBool True = lines "literal bool true"
    | exprLiteralBool False = lines "literal bool false"

  and exprParen e =
  let
    val l1 = lines "expr paren"
    val l2 = List.map indentIn (expr e)
  in
    l1 @ l2
  end

  and exprStruct sml =
  let
    val l1 = lines "expr struct"
    val l2 = List.map structMember sml
    val l2 = List.concat l2
    val l2 = List.map indentIn l2
  in
    l1 @ l2
  end

  and exprUnop u e = 
  let
    val l1 = lines "expr unop"
    val l2 = List.map indentIn (unop u)
    val l3 = List.map indentIn (expr e)
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

  and transUnitList tul = List.concat (List.map transUnit tul)

  and tuMember (TUMember (a, tumn, a')) =
  let
    val annotate = annotate a a'
  in
    case tumn of
       TUDefAbsType node => annotate (defAbsType (data node))
     | TUDefArray node => annotate (defArray (data node))
     | TUDefComponent node => annotate (defComponent (data node))
     | TUDefConstant node => annotate (defConstant (data node))
     | TUDefEnum node => annotate (defEnum (data node))
     | TUDefModule node => annotate (defModule (data node))
     | TUDefPort node => annotate (defPort (data node))
     | TUDefStruct node => annotate (defStruct (data node))
     | TUSpecLoc node => specLoc (data node)
  end

  and tuMemberList tuml = List.concat (List.map tuMember tuml)

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

  and unop Minus = lines "unop -"

end
