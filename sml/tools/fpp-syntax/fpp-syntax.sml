(* ----------------------------------------------------------------------
 * fpp-syntax.sml
 * ----------------------------------------------------------------------*) 

structure FPPSyntax =
struct

fun parseTU (file, instream) =
  let
    val ast = Parser.parse (file, instream)
  in
    ast
  end

fun parse fileNames =
let
  val tool = Tool.Tool { name = "fpp-syntax" }
  val _ = (Error.toolOpt := SOME tool)
  fun file fileName =
  let
    val dirPath = [ "TODO" ]
    val file = File.File { name = fileName, dirPath = dirPath }
    val instream = TextIO.openIn fileName
  in
    (file, instream)
  end
  val astl = case fileNames of
                  [] => [ parseTU (File.stdin, TextIO.stdIn) ]
                | _ => List.map (Parser.parse o file) fileNames
in
  ()
end

fun main(name,args) =
  (parse args; OS.Process.success)
  handle e => Error.handleExn e

end
