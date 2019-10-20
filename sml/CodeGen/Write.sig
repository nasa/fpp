(* ----------------------------------------------------------------------
 * Write.sig: helper functions for writing to an ostream
 * Author: Rob Bocchino
 * ----------------------------------------------------------------------*)

signature WRITE =
sig

  (* the type of a WRITE object *)
  type t

  (* create a printer *)
  val create : TextIO.outstream -> t

  (* emit a string *)
  val str : t -> string -> unit

  (* emit string as line *)
  val strLine : t -> string -> unit

  (* produce a new printer with the indentation moved in *)
  val indentIn : t -> t

  (* produce a new printer with the indentation moved out *)
  val indentOut : t -> t

  (* emit indentation *)
  val indent : t -> unit

  (* get the current indention space *)
  val indentSpace : t -> string

  (* emit a line *)
  val line : t -> (unit -> unit) -> unit

  (* emit a newline *)
  val newline : t -> unit

end
