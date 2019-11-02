(* ----------------------------------------------------------------------
 * fpp-syntax.sml
 * ----------------------------------------------------------------------*) 

structure FPPSyntax =
struct

fun parse fileNames =
let
  val tool = Tool.Tool { name = "fpp-syntax" }
  val _ = Error.setTool tool
  fun file fileName = File.fromPathString fileName
  val tul = case fileNames of
                 [] => [ Parser.parse (File.StdIn) ]
               | _ => List.map (Parser.parse o file) fileNames
  val lines = ASTWriter.transUnitList tul
in
  List.map (Line.write TextIO.stdOut) lines
end

fun main(name,args) =
  (parse args; OS.Process.success)
  handle e => Error.handleExn e

end
