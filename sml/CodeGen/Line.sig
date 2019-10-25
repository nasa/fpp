(* ----------------------------------------------------------------------
 * Line.sig: A line of formatted output
 * ----------------------------------------------------------------------*)

signature LINE =
sig

  (* The type of a LINE *)
  type t

  (* Create a line from an indent increment and a  string *)
  val create : int -> string -> t

  (* Get the indentation *)
  val getIndentation : t -> Indentation.t

  (* Get the line as a formatted string *)
  val getFormatString : t -> string

  (* Get the line as an unformatted string *)
  val getString : t -> string

  (* Get the indent increment *)
  val getIndentIncrement :t -> int

  (* Indent by one offset amount *)
  val indent : t -> t

  (* Un-itdent by one offset amount *)
  val unindent : t -> t

  (* Write the line to an outstream *)
  val write : TextIO.outstream -> t -> unit

end
