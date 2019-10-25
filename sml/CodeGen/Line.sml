(* ----------------------------------------------------------------------
 * Line.sml: Implement Line.sig
 * ----------------------------------------------------------------------*)

structure Line :> LINE =
struct

  datatype t = Line of {
    incr : int,
    indent : Indentation.t,
    str : string
  }

  fun create i s = Line {
    incr = i,
    indent = Indentation.create,
    str = s
  }

  fun getIndentation (Line l) = (#indent l)

  fun getFormatString (Line l) =
  let
    val is = Indentation.toString (#indent l)
  in
    is^(#str l)^"\n"
  end

  fun getString (Line l) = (#str l)

  fun getIndentIncrement (Line l) = (#incr l)

  fun indent (Line l) = Line {
    incr = (#incr l),
    indent = Indentation.indent (#incr l) (#indent l),
    str = (#str l)
  }

  fun unindent (Line l) = Line {
    incr = #incr l,
    indent = Indentation.unindent (#incr l) (#indent l),
    str = (#str l)
  }

  fun write os l = TextIO.output (os, getFormatString l)

end

