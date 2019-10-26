(* ----------------------------------------------------------------------
 * Line.sml: Implement Line.sig
 * ----------------------------------------------------------------------*)

structure Line :> LINE =
struct

  datatype indent_mode = 
    Indent
  | NoIndent

  datatype t = Line of {
    indent : Indentation.t,
    str : string
  }

  fun create s = Line {
    indent = Indentation.create,
    str = s
  }

  val blank = create ""

  fun getIndentation (Line l) = (#indent l)

  fun getFormatString (Line l) =
  let
    val is = Indentation.toString (#indent l)
  in
    is^(#str l)^"\n"
  end

  fun getString (Line l) = (#str l)

  fun indentIn amount (Line l) = Line {
    indent = Indentation.indentIn amount (#indent l),
    str = (#str l)
  }

  fun indentOut amount (Line l) = Line {
    indent = Indentation.indentOut amount (#indent l),
    str = (#str l)
  }

  fun indent amount (Line l) = Line {
    indent = Indentation.indent amount (#indent l),
    str = (#str l)
  }

  fun write os l = TextIO.output (os, getFormatString l)

  fun blankSeparated _ [] = []
    | blankSeparated f (head :: []) = f head
    | blankSeparated f (head :: tail) =
      let
        val head = f head
        val tail = blankSeparated f tail
      in
        head @ (blank :: tail)
      end

  fun getSize (Line l) =
  let
    val is = Indentation.toInt (#indent l)
    val ss = String.size (#str l)
  in
    is + ss
  end

  fun join sep (Line l1) (Line l2) = Line {
    indent = #indent l1,
    str = (#str l1)^sep^(#str l2)
  }

  fun concat _ [] = blank
    | concat sep (head :: []) = head
    | concat sep (head :: tail) =
      let
        val tail = concat sep tail
      in
        join sep head tail
      end

  fun joinLists _ l _ [] = l
    | joinLists _ [] _ l = l
    | joinLists mode l1 sep (hd2 :: tl2) =
    let
      val hd1 :: tl1 = List.rev l1
      val part1 = List.rev tl1
      val part2 = join sep hd1 hd2
      val part3 = case mode of
                       Indent => 
                       let
                         val indentIn = indentIn ((getSize hd1) + (String.size sep))
                       in
                         List.map indentIn tl2
                       end
                     | NoIndent => tl2
    in
      part1 @ (part2 :: part3)
    end

end

