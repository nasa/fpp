(* ----------------------------------------------------------------------
 * parser.sig
 * Author: Rob Bocchino 
 * ----------------------------------------------------------------------*) 

signature PARSER =
sig

  val parse : File.t * TextIO.instream -> Ast.trans_unit

end

