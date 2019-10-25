(* ----------------------------------------------------------------------
 * parser.sig
 * Author: Rob Bocchino 
 * ----------------------------------------------------------------------*) 

signature PARSER =
sig

  val parse : File.t -> Ast.trans_unit

end

