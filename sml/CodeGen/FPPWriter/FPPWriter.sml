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

  fun lines s = [ line s ]

  fun annotate pre post lines = 
  let
    fun preLine l = line ("@ "^l)
    val pre = List.map preLine pre
    fun postLine l = line ("@< "^l)
    val post = List.map postLine post
  in
    pre @ (Line.joinLists lines " " post)
  end

  and defAbsType (DefAbsType id) = lines ("type "^id)

  and defArray (DefArray _) = lines "def array"

  and defConstant (DefConstant (id, en)) = 
  let
    val lhs = lines("constant "^id^" =")
    val rhs = expr (data en)
  in
    Line.joinLists lhs " " rhs
  end

  and defEnum (DefEnum _) = lines "def enum"

  and defModule (DefModule _) = lines "def module"

  and defStruct (DefStruct _) = lines "def struct"

  and enumerator (Enumerator _) = lines "enumerator"

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
    Line.joinLists e "." id
  end

  and exprLiteralBool True = lines "true"
    | exprLiteralBool False = lines "false"

  and exprUnop Minus e = 
  let
    val minus = lines "-"
    val e = expr e
  in
    Line.joinLists minus "" e
  end

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

  and structMember (StructMember (id, en)) =
  let
    val id = lines id
    val e = expr (data en)
  in
    Line.joinLists id " = " e
  end

  and expr (ExprArray enl) = exprArray enl
    | expr (ExprDot (en, id)) = exprDot (data en) id
    | expr (ExprIdent id) = lines id
    | expr (ExprLiteralInt s) = lines s
    | expr (ExprLiteralFloat s) = lines s
    | expr (ExprLiteralString s) = lines ("\""^s^"\"")
    | expr (ExprLiteralBool lb) = exprLiteralBool lb
    | expr (ExprStruct sml) = exprStruct sml
    | expr (ExprUnop (unop, en)) = exprUnop unop (data en)

  and specLoc sl = lines "spec loc"

  and transUnit (TransUnit tuml) = tuMemberList tuml

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

  and typeName tn = lines "type name"

  and transUnitList tul = (Line.blankSeparated transUnit) tul

  and tuMemberList tuml = (Line.blankSeparated tuMember) tuml

end
