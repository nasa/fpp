(* ----------------------------------------------------------------------
 * File.sml
 * An FPP file location
 * ----------------------------------------------------------------------*)

structure File =
struct

  datatype t = File of {
    (* The file name *)
    name: string,
    (* The directory path, represented as a list of directories
       The directory containing the file is at the head of the list. *)
    dirPath : string list
  }

  val stdin = File { name = "stdin", dirPath = [] }

end
