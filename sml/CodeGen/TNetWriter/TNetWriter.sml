(* ----------------------------------------------------------------------
 * TNetWriter.sml: implement TNetWriter.sig
 * Author: Rob Bocchino
 * ----------------------------------------------------------------------*)

structure TNetWriter :> TNET_WRITER =
struct

  open Ast
  open AstNode

  fun writeDefEnum wr (DefEnum _) =
    Write.str wr "def enum"

  and writeDefModule wr e =
    Write.str wr "def module"

  and writeDefType wr (DefType _) =
    Write.str wr "def type"

  and writeEnumerator wr (Enumerator _) =
    Write.str wr "enumerator"

  and writeExpr wr e = 
    Write.str wr "expr"

  and writeTypeName wr tn =
    Write.str wr "type name"

  and writeDefConstant wr (DefConstant _) = 
    Write.str wr "def constant"

end
