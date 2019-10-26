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
      then s
      else toString' (i-1) (" "^s)
  in
    toString' count ""
  end

  fun toInt i = i

  fun getCount i = i

  fun indentIn incr count = count + incr

  fun indentOut incr count =
    if count >= incr then count - incr else 0

  fun indent amount _ = amount

end

