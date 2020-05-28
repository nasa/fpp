package fpp.compiler.util

/** A file used in compilation */
sealed trait File {

  override def toString = this match {
    case File.Path(p) => p.normalize().toString
    case File.StdIn => "stdin"
  }

  /** Open the file for reading */
  def openRead(locOpt: Option[Location] = None): Result.Result[java.io.Reader] = {
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

  /** Open the file for writing */
  def openWrite(locOpt: Option[Location] = None): Result.Result[java.io.PrintWriter] = {
    val error = FileError.CannotOpen(locOpt, this.toString)
    try {
      this match {
        case File.Path(p) => {
          val overwrite = false
          val fileWriter = new java.io.FileWriter(p.toFile, overwrite)
          val printWriter = new java.io.PrintWriter(fileWriter)
          Right(printWriter)
        }
        case _ => Left(error)
      }
    }
    catch {
      case _: Exception => Left(error)
    }
  }

}

object File {

  type JavaPath = java.nio.file.Path

  /** A file path */
  final case class Path(path: JavaPath) extends File

  /** Standard input */
  final case object StdIn extends File

  /** Construct a file from a string representing a file path */
  def fromString(s: String): File = {
    val p = java.nio.file.Paths.get(s).toAbsolutePath.normalize
    File.Path(p)
  }

}
