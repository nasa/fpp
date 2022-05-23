package fpp.compiler.codegen

/** Utilities for writing lines */
trait LineUtils {

  def indentIn(line: Line): Line = line.indentIn(indentIncrement)

  def line(s: String): Line = Line(s)

  def lines(s: String): List[Line] = s.stripMargin.split("\n").map(line(_)).toList

  def linesOpt[T](f: T => List[Line], o: Option[T]): List[Line] =
    o match {
      case Some(x) => f(x)
      case None => Nil
    }

  val q = "\""

  val indentIncrement = 2

  val addBlankPrefix: List[Line] => List[Line] = Line.addPrefixLine (Line.blank) _

  val addBlankPostfix: List[Line] => List[Line] = Line.addPostfixLine (Line.blank) _

  val flattenWithBlankPrefix: List[List[Line]] => List[Line] = Line.flattenWithPrefixLine (Line.blank) _

}
