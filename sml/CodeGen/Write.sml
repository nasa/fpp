(* ----------------------------------------------------------------------
 * Write.sml: implement Write.sig
 * Author: Rob Bocchino
 * ----------------------------------------------------------------------*)

structure Write :> WRITE =
struct

  type t = {
    ind : int,
    os : TextIO.outstream,
    modules : string list
  }

  fun create os = { ind=0, os=os, modules=[] }

  and indent (wr : t) = str wr (indentSpace wr)

  and indentIn { ind, os, modules } = { 
    ind=ind+1, 
    os=os, 
    modules=modules
  }

  and indentOut { ind, os, modules } = {
    ind = if ind >= 1 then ind - 1 else 0,
    os = os,
    modules=modules
  }

  and indentSpace (wr : t) = spaces (#ind wr)

  and line wr f = (indent wr; f (); newline wr)

  and newline wr = str wr "\n"

  and spaces n = if n <= 0 then "" else "  "^(spaces (n-1))

  and str (wr : t) s = TextIO.output (#os wr, s)

  and strLine wr s = line wr (fn () => str wr s)

end

