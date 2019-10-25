(* ----------------------------------------------------------------------
 * Indentation.sig: Line indentation
 * ----------------------------------------------------------------------*)

signature INDENTATION =
sig

  (* The type of an INDENTATION object *)
  type t

  (* Create an indentation object *)
  val create : t

  (* Format the indentation as a string of spaces *)
  val toString : t -> string

  (* Get the indentation space count *)
  val getCount : t -> int

  (* Indent by the given space count *)
  val indent : int -> t -> t

  (* Un-indent by the given space count *)
  val unindent : int -> t -> t

end
