(* ----------------------------------------------------------------------
 * File.sml
 * An FPP file location
 * ----------------------------------------------------------------------*)

structure File =
struct

  datatype t = File of {
    (* The file name *)
    file: string,
    (* The directory path *)
    dir: string
  }

  val stdin = File { file = "stdin", dir = "" }

  fun fromPathString ps =
  let
    val ps = OS.FileSys.fullPath ps
    val file = OS.Path.splitDirFile ps
  in
    File file
  end

end
