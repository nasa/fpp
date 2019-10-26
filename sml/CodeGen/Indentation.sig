(* ----------------------------------------------------------------------
 * Indentation.sig: Line indentation
 * ----------------------------------------------------------------------*)

signature INDENTATION =
sig

  (* The type of an INDENTATION object *)
  type t

  (* Create an indentation with the specified indent amount *)
  val create : t

  (* Format the indentation as a string of spaces *)
  val toString : t -> string

  (* Get the indentation as an integer value *)
  val toInt : t -> int

  (* Get the indentation space count *)
  val getCount : t -> int

  (* Indent in by the given space count *)
  val indentIn : int -> t -> t

  (* Indent out by the given space count *)
  val indentOut : int -> t -> t

  (* Indent to an absolute amount *)
  val indent : int -> t -> t

end
