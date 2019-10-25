(* ----------------------------------------------------------------------
 * fpp-format.sml
 * ----------------------------------------------------------------------*) 

structure FPPFormat =
struct

fun parse fileNames =
let
  val tool = Tool.Tool { name = "fpp-format" }
  val _ = Error.setTool tool
  fun file fileName = File.fromPathString fileName
  val tul = case fileNames of
                 [] => [ Parser.parse (File.StdIn) ]
               | _ => List.map (Parser.parse o file) fileNames
  val lines = FPPWriter.transUnitList tul
in
  List.map (Line.write TextIO.stdOut) lines
end

fun main(name,args) =
  (parse args; OS.Process.success)
  handle e => Error.handleExn e

end
