(* ----------------------------------------------------------------------
 * FPPWriter.sig
 * Write out FPP AST as source
 * ----------------------------------------------------------------------*)

signature FPP_WRITER =
sig

  val writeExpr : Write.t -> Ast.expr -> unit

  val writeDefConstant : Write.t -> Ast.def_constant -> unit

  val writeDefEnum : Write.t -> Ast.def_enum -> unit

  val writeDefModule : Write.t -> Ast.def_module -> unit

  val writeDefType : Write.t -> Ast.def_type -> unit

  val writeEnumerator : Write.t -> Ast.enumerator -> unit

  val writeTypeName : Write.t -> Ast.type_name -> unit

end
