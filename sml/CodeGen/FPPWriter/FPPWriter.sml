(* ----------------------------------------------------------------------
 * FPPWriter.sml: implement FPPWriter.sig
 * Author: Rob Bocchino
 * ----------------------------------------------------------------------*)

structure FPPWriter :> FPP_WRITER =
struct

  open Ast
  open AstNode

  val line = Line.create 2

  fun lines s = [ line s ]

  fun defEnum (DefEnum _) = lines "def enum"

  and defModule e = lines "def module"

  and enumerator (Enumerator _) = lines "enumerator"

  and expr e = lines "expr"

  and typeName tn = lines "type name"

  and defConstant (DefConstant _) = lines "def constant"

  and transUnit (TransUnit _) = lines "trans unit"

  and transUnitList _ = lines "trans unit list"

end
