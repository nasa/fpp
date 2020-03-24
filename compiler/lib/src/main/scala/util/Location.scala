package fpp.compiler.util

import scala.util.parsing.input.Position

/** A location used in compilation */
final case class Location(
  file: File, /* The file */
  pos: Position, /* The position */
  includingLoc: Option[Location] = None /* Location where this location is included */
) {

  override def toString = {
    def showIncludes(locOpt: Option[Location], s: String): String = { 
      locOpt match {
        case None => s
        case Some(loc) => showIncludes(
          loc.includingLoc, 
          s ++ s"\n  included at ${loc.file}: ${loc.pos}"
        )
      }
    }
    val s1 = pos match {
      case scala.util.parsing.input.NoPosition => s"${file}: end of input"
      case _ => s"${file}: ${pos.toString}\n${pos.longString}"
    }
    val s2 = showIncludes(includingLoc, "")
    s1 ++ s2
  }

  /** Get the location of the associated translation unit */
  def getTULocation: Location = this.includingLoc match {
    case None => this
    case Some(loc) => loc.getTULocation
  }

}
