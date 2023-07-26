package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPFilenames {

  case class Options(
    files: List[File] = List(),
    template: Boolean = false,
  )

  def command(options: Options) = {
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    for {
      tul <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
      aTul <- ResolveSpecInclude.transformList(
        Analysis(),
        tul,
        ResolveSpecInclude.transUnit
      )
      files <-
        CppWriter.getMode(options.template) match {
          case CppWriter.Autocode => ComputeGeneratedFiles.getAutocodeFiles(aTul._2)
          case CppWriter.ImplTemplate => ComputeGeneratedFiles.getImplFiles(aTul._2)
        }
    }
    yield files.sorted.map(System.out.println)
  }

  def main(args: Array[String]) =
    Tool(name).mainMethod(args, oparser, Options(), command)

  val builder = OParser.builder[Options]

  val name = "fpp-filenames"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      help('h', "help").text("print this message and exit"),
      opt[Unit]('t', "template")
        .action((_, c) => c.copy(template = true))
        .text("write names of generated template files"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

}
