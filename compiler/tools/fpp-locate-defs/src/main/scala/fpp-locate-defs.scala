package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPLocateDefs {

  case class Options(
    dir: Option[String] = None,
    files: List[File] = Nil,
  )

  def mapSeq[T](seq: Seq[T], f: String => Unit) =
    seq.map(_.toString).sortWith(_ < _).map(f)

  def command(options: Options) = {
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    for {
      tul <- Result.map(
        files,
        Parser.parseFile (Parser.transUnit) (None) _
      )
      a_tul <- ResolveSpecInclude.transformList(
        Analysis(),
        tul,
        ResolveSpecInclude.transUnit
      )
    }
    yield {
      val (_, tul) = a_tul
      val config = LocateDefsFppWriter.State(options.dir)
      val lines = tul.map(LocateDefsFppWriter.transUnit(config, _)).flatten
      mapSeq(lines, System.out.println(_))
    }
  }

  def main(args: Array[String]) = {
    Error.setTool(Tool(name))
    for (options <- OParser.parse(oparser, args, Options()))
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

  val name = "fpp-locate-defs"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
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
