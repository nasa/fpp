package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPLocateUses {

  case class Options(
    dir: Option[String] = None,
    files: List[File] = Nil,
  )

  def command(options: Options): Result.Result[Unit] = {
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    /*
    for {
      tul <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
      a_tul <- ResolveSpecInclude.transformList(Analysis(), tul, ResolveSpecInclude.transUnit)
    }
    yield {
      val (_, tul) = a_tul
      val config = LocateUsesFppWriter.State(options.dir)
      val lines = tul.map(LocateUsesFppWriter.transUnit(config, _)).flatten
      lines.map(Line.write(Line.stdout) _)
    }
    */
    Right(())
  }

  def main(args: Array[String]) = {
    Error.setTool(Tool(name))
    for { options <- OParser.parse(oparser, args, Options()) }
    yield {
      command(options) match {
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

  val name = "fpp-locate-uses"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, "0.1"),
      opt[String]('d', "directory")
        .valueName("<dir>")
        .action((d, c) => c.copy(dir = Some(d)))
        .text("base directory"),
      help('h', "help").text("print this message and exit"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("files to analyze"),
    )
  }

}
