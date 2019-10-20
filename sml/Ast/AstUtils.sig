(* ----------------------------------------------------------------------
 * AstUtils.sig
 * ----------------------------------------------------------------------*)

 signature AST_UTILS =
 sig

   (* Deannotate an AST node *)
   val deannotate : 'a Ast.annotated -> 'a

 end
