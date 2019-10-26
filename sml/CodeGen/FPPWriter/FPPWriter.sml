(* ----------------------------------------------------------------------
 * FPPWriter.sml: implement FPPWriter.sig
 * Author: Rob Bocchino
 * ----------------------------------------------------------------------*)

structure FPPWriter :> FPP_WRITER =
struct

  open Ast
  open AstNode

  val line = Line.create

  fun lines s = [ line s ]

  fun separatedList _ [] = ""
    | separatedList _ (hd :: []) = hd
    | separatedList sep (hd :: tl) = hd^sep^(separatedList sep tl)

  fun annotate pre post lines = lines

  and defAbsType (DefAbsType _) = lines "def abs type"

  and defArray (DefArray _) = lines "def array"

  and defConstant (DefConstant (id, en)) = 
  let
    val rhs = expr (data en)
  in
    lines ("constant "^id^" = "^rhs)
  end

  and defEnum (DefEnum _) = lines "def enum"

  and defModule (DefModule _) = lines "def module"

  and defStruct (DefStruct _) = lines "def struct"

  and enumerator (Enumerator _) = lines "enumerator"

  and exprArray enl =
  let
    fun f node = expr (data node)
    val eltList = List.map f enl
    val elts = separatedList ", " eltList
  in
    "[ "^elts^" ]"
  end

  and exprLiteralBool True = "true"
    | exprLiteralBool False = "false"

  and exprUnop Minus e = "-"^(expr e)

  and expr (ExprArray enl) = exprArray enl
    | expr (ExprDot (en, id)) = (expr (data en))^"."^id
    | expr (ExprIdent id) = id
    | expr (ExprLiteralInt s) = s
    | expr (ExprLiteralFloat s) = s
    | expr (ExprLiteralString s) = "\""^s^"\""
    | expr (ExprLiteralBool lb) = exprLiteralBool lb
    | expr (ExprStruct _) = "expr struct"
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

  and transUnitList tul = (Line.blankSeparatedListOf transUnit) tul

  and tuMemberList tuml = (Line.blankSeparatedListOf tuMember) tuml

end
