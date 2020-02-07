/**
 * A file used in compilation
 */

package fpp.compiler.util

sealed trait File {

  override def toString = this match {
    case File.Path(p) => p.toString
    case File.StdIn => "stdin"
  }

  def open: Result.Result[java.io.Reader] = {
    try {
      val r = this match {
        case File.Path(p) => new java.io.FileReader(p.toFile)
        case File.StdIn => new java.io.BufferedReader(new java.io.InputStreamReader(System.in))
      }
      Right(r)
    }
    catch {
      case _: Exception => Left(FileError.CannotOpen(this.toString))
    }
  }

}

object File {

  final case class Path(path: java.nio.file.Path) extends File
  final case object StdIn extends File

  def fromString(s: String): File = {
    val p = java.nio.file.Paths.get(s).toAbsolutePath
    File.Path(p)
  }

}
