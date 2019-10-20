(* ----------------------------------------------------------------------
 * Loc.sig
 * A location for analysis and error reporting
 * ----------------------------------------------------------------------*)

signature LOC =
sig

  datatype t = Loc of {
    file: File.t,
    pos1: int,
    pos2: int
  }

  val show : t -> string

  (* Placeholder location *)
  val todo : t

end
