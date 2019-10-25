(* ----------------------------------------------------------------------
 *IndentationWrite.sml: Implement Indentation.sig
 * ----------------------------------------------------------------------*)

structure Indentation :> INDENTATION =
struct

  type t = int

  val create = 0

  fun toString count =
  let
    fun toString' i s =
      if i < 1
      then ""
      else toString' (i-1) (" "^s)
  in
    toString' count ""
  end

  fun getCount i = i

  fun indent incr count = count + incr

  fun unindent incr count =
    if count >= incr then count - incr else 0
  
end

