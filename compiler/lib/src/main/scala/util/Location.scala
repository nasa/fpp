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
  def tuLocation: Location = this.includingLoc match {
    case None => this
    case Some(loc) => loc.tuLocation
  }

  /** Get the path of a file that is a neighbor to this location */
  def getNeighborPath(fileName: String): Result.Result[java.nio.file.Path] =
    for (dir <- dirPath)
      yield java.nio.file.Paths.get(dir.toString, fileName)

  /** Get the directory path associated with the location */
  def dirPath: Result.Result[java.nio.file.Path] = 
    try {
      val path = file match {
        case File.Path(p) => p.getParent()
        case File.StdIn => java.nio.file.Paths.get("").toAbsolutePath().normalize()
      }
      Right(path)
    }
    catch {
      case _: Exception => Left(FileError.CannotResolvePath(this, "directory"))
    }

  /** Resolve a path relative to the path of this location */
  def getRelativePath(path: String): java.nio.file.Path = {
    val p = Result.expectRight(this.dirPath)
    p.resolve(path).normalize()
  }

  /** Resolve a path relative to the path of this location */
  def relativePath(path: String): Result.Result[java.nio.file.Path] = {
    val p = Result.expectRight(this.dirPath)
    Right(p.resolve(path).normalize())
  }
//    try {
//      for { p <- this.dirPath }
//      yield { p.resolve(path).normalize() }
//    }
//    catch {
//      case _: Exception => Left(FileError.CannotResolvePath(this, path))
//    }

}
