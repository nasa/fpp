(* ----------------------------------------------------------------------
 * fpp-syntax.sml
 * ----------------------------------------------------------------------*) 

structure FPPSyntax =
struct

fun compile () =
  let
    val ast = Parser.parse ("stdin", TextIO.stdIn)
  in
    (* TODO *)
    ()
  end

fun main(name,args) =
  (compile (); OS.Process.success)
  handle e => Error.handleExn e

end
