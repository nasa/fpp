package fpp.compiler.util

/** A file used in compilation */
sealed trait File {

  override def toString = this match {
    case File.Path(p) => p.normalize().toString
    case File.StdIn => "stdin"
  }

  /** Open the file */
  def open(locOpt: Option[Location]): Result.Result[java.io.Reader] = {
    try {
      val r = this match {
        case File.Path(p) => new java.io.FileReader(p.toFile)
        case File.StdIn => new java.io.BufferedReader(new java.io.InputStreamReader(System.in))
      }
      Right(r)
    }
    catch {
      case _: Exception => Left(FileError.CannotOpen(locOpt, this.toString))
    }
  }

  def open(loc: Location): Result.Result[java.io.Reader] = open(Some(loc))

  def open: Result.Result[java.io.Reader] = open(None)

}

object File {

  /** A file path */
  final case class Path(path: java.nio.file.Path) extends File

  /** Standard input */
  final case object StdIn extends File

  /** Construct a file from a string representing a file path */
  def fromString(s: String): File = {
    val p = java.nio.file.Paths.get(s).toAbsolutePath.normalize
    File.Path(p)
  }

}
