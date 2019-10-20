(* ----------------------------------------------------------------------
 * fpp-syntax.sml
 * ----------------------------------------------------------------------*) 

structure FPPSyntax =
struct

fun compile () =
  let
    val tool = Tool.Tool { name = "fpp-syntax" }
    val _ = (Error.toolOpt := SOME tool)
    val ast = Parser.parse (File.stdin, TextIO.stdIn)
  in
    (* TODO *)
    ()
  end

fun main(name,args) =
  (compile (); OS.Process.success)
  handle e => Error.handleExn e

end
