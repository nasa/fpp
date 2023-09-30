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
    autoTestHelpers: Boolean = false,
    files: List[File] = List(),
    template: Boolean = false,
    unitTest: Boolean = false,
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
        CppWriter.getMode(options.template, options.unitTest) match {
          case CppWriter.Autocode => ComputeGeneratedFiles.getAutocodeFiles(aTul._2)
          case CppWriter.ImplTemplate => ComputeGeneratedFiles.getImplFiles(aTul._2)
          case CppWriter.UnitTest => ComputeGeneratedFiles.getTestFiles(
            aTul._2,
            CppWriter.getTestHelperMode(options.autoTestHelpers)
          )
          case CppWriter.UnitTestTemplate => ComputeGeneratedFiles.getTestImplFiles(
            aTul._2,
            CppWriter.getTestHelperMode(options.autoTestHelpers)
          )
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
      opt[Unit]('a', "auto-test-helpers")
        .action((_, c) => c.copy(autoTestHelpers = true))
        .text("enable automatic generation of test helper code"),
      opt[Unit]('t', "template")
        .action((_, c) => c.copy(template = true))
        .text("write names of generated template files"),
      opt[Unit]('u', "unit-test")
        .action((_, c) => c.copy(unitTest = true))
        .text("write names of generated unit test files"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

}
