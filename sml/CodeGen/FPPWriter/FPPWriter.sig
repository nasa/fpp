(* ----------------------------------------------------------------------
 * FPPWriter.sig
 * Write out FPP AST as source
 * ----------------------------------------------------------------------*)

signature FPP_WRITER =
sig

  val expr : Ast.expr -> Line.t list

  val defAbsType : Ast.def_abs_type -> Line.t list

  val defArray : Ast.def_array -> Line.t list

  val defConstant : Ast.def_constant -> Line.t list

  val defEnum : Ast.def_enum -> Line.t list

  val defModule : Ast.def_module -> Line.t list

  val defStruct : Ast.def_struct -> Line.t list

  val enumerator : Ast.enumerator -> Line.t list

  val qualIdent : Ast.ident list -> Line.t list

  val specLoc : Ast.spec_loc -> Line.t list

  val structMember : Ast.struct_member -> Line.t list

  val typeName : Ast.type_name -> Line.t list

  val transUnit : Ast.trans_unit -> Line.t list

  val transUnitList : Ast.trans_unit list -> Line.t list

  val tuMember : Ast.tu_member -> Line.t list

  val tuMemberList : Ast.tu_member list -> Line.t list

end
