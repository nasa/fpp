(* ----------------------------------------------------------------------
 * parser.sig
 * Author: Rob Bocchino 
 * ----------------------------------------------------------------------*) 

signature PARSER =
sig

  val parse : string * TextIO.instream -> Ast.trans_unit

end

