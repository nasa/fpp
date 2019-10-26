(* ----------------------------------------------------------------------
 * Line.sig: A line of formatted output
 * ----------------------------------------------------------------------*)

signature LINE =
sig

  (* The type of a LINE *)
  type t

  (* Create a blank line *)
  val blank : t

  (* Create a line from a string *)
  val create : string -> t

  (* Get the indentation *)
  val getIndentation : t -> Indentation.t

  (* Get the line as a formatted string *)
  val getFormatString : t -> string

  (* Get the line as an unformatted string *)
  val getString : t -> string

  (* Indent in by the given amount *)
  val indentIn : int -> t -> t

  (* Indent out by the given amount *)
  val indentOut : int -> t -> t

  (* Indent to an absolute amount *)
  val indent : int -> t -> t

  (* Write the line to an outstream *)
  val write : TextIO.outstream -> t -> unit

  (* Construct a blank-separated list of lines *)
  val blankSeparated : ('a -> t list) -> 'a list -> t list

  (* Get the size of a line.
     Includes indentation, but not the newline. *)
  val getSize : t -> int

  (* Join two lines with separator string *)
  val join : string -> t -> t -> t

  (* Concatenate a list of lines with separtor string *)
  val concat : string -> t list -> t

  (* Join two lists of lines with separator stirng *)
  val joinLists : t list -> string -> t list -> t list

end
