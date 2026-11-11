package fpp.compiler.util

import scala.util.parsing.input.Position

/** A location used in compilation */
final case class Location(
  file: File, /* The file */
  pos: Position, /* The position */
  /* Location where this location is included */
  includingLoc: Option[Location] = None,
  /* Location where this location is expanded */
  expandingLoc: Option[Location] = None,
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
              loc.includingLoc, loc.expandingLoc,
              s ++ s"\n  included at ${loc.file}:${loc.pos}"
            )
            
        }

        val s2 = expandLocOpt match {
          case None => s1
          case Some(loc) =>
            showIncludesExpands(
              loc.includingLoc, loc.expandingLoc,
              s1 ++ s"\n  expanded at ${loc.file}:${loc.pos}"
            )
            
        }

        s2
    }
    val s1 = pos match {
      case scala.util.parsing.input.NoPosition => s"${file}: end of input"
      case _ => s"${file}:${pos.toString}\n${pos.longString}"
    }
    val s2 = showIncludesExpands(includingLoc, expandingLoc, "")
    s1 ++ s2
  }

  /** Get the location of the associated translation unit */
  def tuLocation: Location = (expandingLoc, includingLoc) match {
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

  def compare(that: Location): Int = {
    val fileCompare = this.file.toString().compare(that.file.toString())
    if (fileCompare != 0) fileCompare
    else if (this.pos.line != that.pos.line) this.pos.line - that.pos.line
    else if (this.pos.column != that.pos.column) this.pos.column - that.pos.column
    else {
      val includingCompare =
        Location.compareOpt(this.includingLoc, that.includingLoc)
      if (includingCompare != 0) includingCompare
      else Location.compareOpt(this.expandingLoc, that.expandingLoc)
    }
  }

}

object Location {

  /** Compare two optional locations, ordering None before Some */
  def compareOpt(loc1: Option[Location], loc2: Option[Location]): Int =
    (loc1, loc2) match {
      case (None, None) => 0
      case (None, Some(_)) => -1
      case (Some(_), None) => 1
      case (Some(l1), Some(l2)) => l1.compare(l2)
    }

}
