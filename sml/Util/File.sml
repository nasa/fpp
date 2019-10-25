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
  | StdIn
  | StdOut

  exception DoesNotExist of string
  exception CannotOpen of string

  fun resolvePathString ps =
    OS.FileSys.fullPath ps
    handle _ => raise DoesNotExist ps

  fun fromPathString ps =
  let
    val ps = resolvePathString ps
    val file = OS.Path.splitDirFile ps
  in
    File file
  end

  fun toPathString (File file) = OS.Path.joinDirFile file
    | toPathString StdIn = "stdin"
    | toPathString StdOut = "stdout"

  fun openIn (File f) =
        let
          val ps = OS.Path.joinDirFile f
        in
          TextIO.openIn ps
          handle _ => raise CannotOpen ps
        end
    | openIn StdIn = TextIO.stdIn
    | openIn StdOut = raise CannotOpen "stdout"

end
