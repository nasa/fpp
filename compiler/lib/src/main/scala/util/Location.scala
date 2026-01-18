package fpp.compiler.util

import scala.util.parsing.input.Position

/** A location used in compilation */
final case class Location(
  file: File, /* The file */
  pos: Position, /* The position */
  /* Location where this location is included */
  includeLoc: Option[Location] = None,
  /* Location where this location is included */
  expandLoc: Option[Location] = None,
) {

  override def toString = {
    def showIncludesExpands(
        incLocOpt: Option[Location],
        expandLocOpt: Option[Location],
        s: String
      ): String = {
        val s1 = incLocOpt match {
          case None => s
          case Some(loc) =>
            showIncludesExpands(
              loc.includeLoc, loc.expandLoc,
              s ++ s"\n  included at ${loc.file}:${loc.pos}"
            )
            
        }

        val s2 = expandLocOpt match {
          case None => s1
          case Some(loc) =>
            showIncludesExpands(
              loc.includeLoc, loc.expandLoc,
              s1 ++ s"\n  expanded at ${loc.file}:${loc.pos}"
            )
            
        }

        s2
    }
    val s1 = pos match {
      case scala.util.parsing.input.NoPosition => s"${file}: end of input"
      case _ => s"${file}:${pos.toString}\n${pos.longString}"
    }
    val s2 = showIncludesExpands(includeLoc, expandLoc, "")
    s1 ++ s2
  }

  /** Get the location of the associated translation unit */
  def tuLocation: Location = (includeLoc, expandLoc) match {
    case (None, None) => this
    case (Some(loc), _) => loc.tuLocation
    case (_, Some(loc)) => loc.tuLocation
  }

  /** Get the path of a file that is a neighbor to this location */
  def getNeighborPath(fileName: String): java.nio.file.Path =
    java.nio.file.Paths.get(getDirPath.toString, fileName)

  /** Get the directory path associated with the location */
  def getDirPath: java.nio.file.Path =
    file match {
      case File.Path(p) => p.getParent
      case File.StdIn => java.nio.file.Paths.get("").toAbsolutePath.normalize
    }

  /** Resolve a path relative to the path of this location */
  def getRelativePath(path: String): java.nio.file.Path =
    getDirPath.resolve(path).normalize

  def compare(that: Location) = {
    val fileCompare = this.file.toString().compare(that.file.toString())
    if (fileCompare != 0) fileCompare
    else if (this.pos.line != that.pos.line) this.pos.line - that.pos.line
    else this.pos.column - that.pos.column
  }

}
