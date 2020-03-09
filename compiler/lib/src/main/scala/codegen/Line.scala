package fpp.compiler.codegen

/** A line of formatted output */
object Line {

  sealed trait IndentMode
  case object Indent extends IndentMode
  case object NoIndent extends IndentMode

  def blank: Line = Line(string = "")

  def write (writer: java.io.PrintWriter) (line: Line) =
    writer.println(line.toString)

  /** Construct a blank-separated list of lines */
  def blankSeparated[T] (f: T => List[Line]) (ts: List[T]): List[Line] = 
    ts match {
      case Nil => Nil
      case head :: Nil => f(head)
      case head :: tail => {
        val head1 = f(head)
        val tail1 = blankSeparated (f) (tail)
        head1 ++ (blank :: tail1)
      }
    }

  /* Join two lines with separator string */
  def join (sep: String) (l1: Line) (l2: Line): Line = {
    val indent = l1.indent
    val string = l1.string ++ sep ++ l2.string
    Line(indent, string)
  }

  /* Concatenate a list of lines with separtor string */
  def concat (sep: String) (lines: List[Line]): Line =
    lines match {
      case Nil => blank
      case (head :: Nil) => head
      case (head :: tail) => {
        val tail1 = concat (sep) (tail)
        join (sep) (head) (tail1)
      }
    }

  /* Join two lists of lines with separator string */
  def joinLists (mode: IndentMode) (lines1: List[Line]) (sep: String) (lines2: List[Line]): List[Line] =
    (lines1, lines2) match {
      case (l, Nil) => l
      case (Nil, l) => l
      case (l1, hd2 :: tl2) => {
        val hd1 :: tl1 = l1.reverse
        val part1 = tl1.reverse
        val part2 = join (sep) (hd1) (hd2)
        val part3 = mode match {
          case Indent => {
            val indent = hd1.getSize + sep.length
            tl2.map(_.indentIn(indent))
          }
          case NoIndent => tl2
        }
        part1 ++ (part2 :: part3)
      }
    }
}

case class Line(indent: Indentation = Indentation(0), string: String) {

  /** Convert the line to a formatted string */
  override def toString = indent.toString ++ string

  /** Indent in */
  def indentIn(n: Int) = this.copy(indent = indent.indentIn(n))

  /** Indent out */
  def indentOut(n: Int) = this.copy(indent = indent.indentOut(n))

  /** Absolute indent */
  def indent(n: Int) = this.copy(indent = Indentation(n))

  /** Get the size of a line.
   *  Includes the indentation, but not the newline */
  def getSize: Int = indent.toInt + string.length

}
