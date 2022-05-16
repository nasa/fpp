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
  case object StdIn extends File

  /** Get the Java path associated with a string */
  def getJavaPath(s: String): JavaPath =
    java.nio.file.Paths.get(s).toAbsolutePath.normalize

  /** Construct a file from a string representing a file path */
  def fromString(s: String): File = Path(getJavaPath(s))

  /** Remove the longest prefix from a Java path */
  def removeLongestPrefix(prefixes: List[String])(path: JavaPath): JavaPath = {
    def removePrefix(s: String) = {
      val prefix = java.nio.file.Paths.get(s)
      if (path.startsWith(prefix)) prefix.relativize(path) else path
    }
    prefixes.map(removePrefix(_)) match {
      case Nil => path
      case head :: tail => {
        def min(p1: JavaPath, p2: JavaPath) = 
          if (p1.getNameCount < p2.getNameCount) p1 else p2
        tail.fold(head)(min)
      }
    }
  }

}
