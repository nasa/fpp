package fpp.compiler.util

import scala.util.parsing.input.Position

sealed trait LocationOrigin
final case class LocationIncluded(loc: Location) extends LocationOrigin
final case class LocationExpanded(loc: Location) extends LocationOrigin

/** A location used in compilation */
final case class Location(
  file: File, /* The file */
  pos: Position, /* The position */
  originLoc: Option[LocationOrigin] = None, /* Location where this location is included/expanded */
) {

  override def toString = {
    def showIncludes(locOpt: Option[LocationOrigin], s: String): String = { 
      locOpt match {
        case None => s
        case Some(LocationIncluded(loc)) => showIncludes(
          loc.originLoc, 
          s ++ s"\n  included at ${loc.file}:${loc.pos}"
        )
        case Some(LocationExpanded(loc)) => showIncludes(
          loc.originLoc,
          s ++ s"\n  template expanded at ${loc.file}:${loc.pos}"
        )
      }
    }
    val s1 = pos match {
      case scala.util.parsing.input.NoPosition => s"${file}: end of input"
      case _ => s"${file}:${pos.toString}\n${pos.longString}"
    }
    val s2 = showIncludes(originLoc, "")
    s1 ++ s2
  }

  /** Get the location of the associated translation unit */
  def tuLocation: Location = this.originLoc match {
    case None => this
    case Some(LocationIncluded(loc)) => loc.tuLocation
    case Some(LocationExpanded(loc)) => loc.tuLocation
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
