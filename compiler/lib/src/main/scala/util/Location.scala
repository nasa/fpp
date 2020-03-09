package fpp.compiler.util

import util.parsing.input.Position

/** A location used in compilation */
final case class Location(
  file: File, /* The file */
  pos: Position, /* The position */
  includes: List[(File, Position)] = Nil /* The included files and positions, starting with a where A includes B */
) {
    override def toString = {
      def showIncludes(fpl: List[(File, Position)], s: String): String = { 
        fpl match {
          case Nil => s
          case (file, pos) :: fpl1 => showIncludes(
            fpl1, 
            s"\n  included at ${file}: ${pos}"
          )
        }
      }
      val s1 = pos match {
        case scala.util.parsing.input.NoPosition => s"${file}: end of input"
        case _ => s"${file}: ${pos.toString}\n${pos.longString}"
      }
      val s2 = showIncludes(includes, "")
      s1 ++ s2
    }
}
