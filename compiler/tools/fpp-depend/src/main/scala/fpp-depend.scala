package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._
import scopt.OParser

object FPPDepend {

  case class Options(
    include: Boolean = false,
    files: List[File] = List()
  )

  def command(options: Options) = {
    val files = options.files match {
      case Nil => List(File.StdIn)
      case _ => options.files
    }
    val a = Analysis(inputFileSet = options.files.toSet)
    for {
      tul <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
      a <- ComputeDependencies.tuList(a, tul)
    }
    yield {
      a.dependencyFileSet.map(System.out.println(_))
      options.include match {
        case true => a.includedFileSet.map(System.out.println(_))
        case false => ()
      }
    }
  }

  def main(args: Array[String]) = {
    Error.setTool(Tool(name))
    val options = OParser.parse(oparser, args, Options())
    for { result <- options } yield {
      command(result) match {
        case Left(error) => {
          error.print
          System.exit(1)
        }
        case _ => ()
      }
    }
    ()
  }

  val builder = OParser.builder[Options]

  val name = "fpp-depend"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, "0.1"),
      opt[Unit]('i', "include")
        .action((_, c) => c.copy(include = true))
        .text("count included files as dependencies"),
      help('h', "help").text("print this message and exit"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

}
