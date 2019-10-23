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

fun parseFiles fileNames =
let
  val tool = Tool.Tool { name = "fpp-syntax" }
  val _ = Error.setTool tool
  fun file fileName =
  let
    val file = File.fromPathString fileName
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
  (parseFiles args; OS.Process.success)
  handle e as IO.Io { name=name, ... } => (
           Error.displayStr NONE ("cannot read file "^name);
           raise e
         )
       | e => Error.handleExn e

end
