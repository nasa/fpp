(* ----------------------------------------------------------------------
 * Loc.sml
 * ----------------------------------------------------------------------*)

structure Loc :> LOC =
struct

  datatype t = Loc of {
    file: File.t,
    pos1: int,
    pos2: int
  }

  val todo = Loc {
    file = File.File { file = "todo", dir = "" },
    pos1 = 0,
    pos2 = 0
  }

  fun show (Loc {file, pos1, pos2}) =
    let
      val sl = Int.toString pos1
      val el = Int.toString pos2
      val pos = 
        if sl = el
        then "line "^sl
        else "lines "^sl^"-"^el
    in 
      pos^" of "^(File.toPathString file)
    end

end
