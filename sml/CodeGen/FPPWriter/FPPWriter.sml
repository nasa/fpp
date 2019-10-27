(* ----------------------------------------------------------------------
 * FPPWriter.sml: implement FPPWriter.sig
 * Author: Rob Bocchino
 * ----------------------------------------------------------------------*)

structure FPPWriter :> FPP_WRITER =
struct

  open Ast
  open AstNode

  val indentIn = Line.indentIn 2

  val line = Line.create

  val joinLists = Line.joinLists Line.NoIndent

  fun lines s = [ line s ]

  fun annotate pre post lines = 
  let
    fun preLine l = line ("@ "^l)
    val pre = List.map preLine pre
    fun postLine l = line ("@< "^l)
    val post = List.map postLine post
  in
    pre @ (Line.joinLists Line.Indent lines " " post)
  end

  and defAbsType (DefAbsType id) = lines ("type "^id)

  and defArray (DefArray (id, en, tnn, eno)) =
  let
    val def = lines ("array "^id^" = [")
    val def = joinLists def "" (expr (data en))
    val def = joinLists def "" (lines "]")
    val def = joinLists def " " (typeName (data tnn))
  in
    case eno of
         SOME en => 
         let
           val def = joinLists def " " (lines "default")
           val def = joinLists def " " (expr (data en))
         in
           def
         end
       | NONE => def
  end

  and defConstant (DefConstant (id, en)) = 
  let
    val lhs = lines("constant "^id^" =")
    val rhs = expr (data en)
  in
    joinLists lhs " " rhs
  end

  and defEnum (DefEnum (id, tnno, enal)) = 
  let
    val start = lines ("enum "^id)
    val start = case tnno of
                     SOME tnn => joinLists start " " (typeName (data tnn))
                   | NONE => start
    val start = joinLists start " " (lines "{")
    fun f (a, x, a') = annotate a a' (enumerator (data x))
    val enums = List.concat (List.map f enal)
    val enums = List.map indentIn enums
    val rbrace = lines "}"
  in
    start @ enums @ rbrace
  end

  and defModule (DefModule (id, tuml)) = 
  let
    val start = lines ("module "^id^" {")
    val members = tuMemberList tuml
    val members = List.map indentIn members
    val rbrace = lines "}"
  in
    start @ members @ rbrace
  end

  and defStruct (DefStruct (id, stmnal, eno)) =
  let
    val start = lines ("struct "^id^" {")
    fun f (a, x, a') = annotate a a' (structTypeMember (data x))
    val members = List.concat (List.map f stmnal)
    val members = List.map indentIn members
    val rbrace = lines "}"
  in
    start @ members @ rbrace
  end

  and enumerator (Enumerator (id, NONE)) = lines id
    | enumerator (Enumerator (id, SOME en)) =
        joinLists (lines id) " = " ((expr o data) en)

  and expr (ExprArray enl) = exprArray enl
    | expr (ExprDot (en, id)) = exprDot (data en) id
    | expr (ExprIdent id) = lines id
    | expr (ExprLiteralInt s) = lines s
    | expr (ExprLiteralFloat s) = lines s
    | expr (ExprLiteralString s) = lines ("\""^s^"\"")
    | expr (ExprLiteralBool lb) = exprLiteralBool lb
    | expr (ExprStruct sml) = exprStruct sml
    | expr (ExprUnop (unop, en)) = exprUnop unop (data en)

  and exprArray enl =
  let
    val lbracket = line "["
    val elts = List.map (expr o data) enl
    val elts = List.concat elts
    val elts = List.map indentIn elts
    val rbracket = line "]"
  in
    (lbracket :: elts) @ [ rbracket ]
  end

  and exprDot e id =
  let
    val e = expr e
    val id = lines id
  in
    joinLists e "." id
  end

  and exprLiteralBool True = lines "true"
    | exprLiteralBool False = lines "false"

  and exprStruct sml =
  let
    val lbrace = line "{"
    val members = List.map structMember sml
    val members = List.concat members
    val members = List.map indentIn members
    val rbrace = line "}"
  in
    (lbrace :: members) @ [ rbrace ]
  end

  and exprUnop Minus e = 
  let
    val minus = lines "-"
    val e = expr e
  in
    joinLists minus "" e
  end

  and qualIdent [] = ""
    | qualIdent (id :: []) = id
    | qualIdent (id :: ids) = id^"."^(qualIdent ids)

  and specLocKind SpecLocComponent = "component"
    | specLocKind SpecLocConstant = "constant"
    | specLocKind SpecLocComponentInstance = "component instance"
    | specLocKind SpecLocPort = "port"
    | specLocKind SpecLocType = "type"
    | specLocKind Topology = "topology"

  and specLoc (SpecLoc (slk, il, s)) = 
  let
    val kind = specLocKind slk
    val qi = qualIdent il
    val location = "\""^s^"\""
  in
    lines ("locate "^kind^" "^qi^" at "^location)
  end

  and structMember (StructMember (id, en)) =
  let
    val id = lines id
    val e = expr (data en)
  in
    joinLists id " = " e
  end

  and structTypeMember (StructTypeMember (id, tnn)) =
  let
    val id = lines id
    val tn = typeName (data tnn)
  in
    joinLists id " : " tn
  end

  and transUnit (TransUnit tuml) = tuMemberList tuml

  and transUnitList tul = (Line.blankSeparated transUnit) tul

  and tuMember (TUMember (a, tumn, a')) =
  let
    val annotate = annotate a a'
  in
    case tumn of
       TUDefAbsType node => annotate (defAbsType (data node))
     | TUDefArray node => annotate (defArray (data node))
     | TUDefConstant node => annotate (defConstant (data node))
     | TUDefEnum node => annotate (defEnum (data node))
     | TUDefModule node => annotate (defModule (data node))
     | TUDefStruct node => annotate (defStruct (data node))
     | TUSpecLoc node => specLoc (data node)
  end

  and tuMemberList tuml = (Line.blankSeparated tuMember) tuml

  and typeName TypeNameBool = lines "bool"
    | typeName (TypeNameFloat F32) = lines "F32"
    | typeName (TypeNameFloat F64) = lines "F64"
    | typeName (TypeNameInt I8) = lines "I8"
    | typeName (TypeNameInt I16) = lines "I16"
    | typeName (TypeNameInt I32) = lines "I32"
    | typeName (TypeNameInt I64) = lines "I64"
    | typeName (TypeNameInt U8) = lines "U8"
    | typeName (TypeNameInt U16) = lines "U16"
    | typeName (TypeNameInt U32) = lines "U32"
    | typeName (TypeNameInt U64) = lines "U64"
    | typeName (TypeNameQualIdent il) = lines (qualIdent il)
    | typeName (TypeNameString) = lines "string"

end
