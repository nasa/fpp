package fpp.compiler.tools

import fpp.compiler.util._
import scopt.OParser

object FPP {
  case class Options(
      args: Array[String],
      command: String
  )

  def command(options: Options) = {
    options.command match {
      case "check"       => FPPCheck.toolMain(options.args)
      case "depend"      => FPPDepend.toolMain(options.args)
      case "filenames"   => FPPFilenames.toolMain(options.args)
      case "format"      => FPPFormat.toolMain(options.args)
      case "from-xml"    => FPPFromXml.toolMain(options.args)
      case "locate-defs" => FPPLocateDefs.toolMain(options.args)
      case "locate-uses" => FPPLocateUses.toolMain(options.args)
      case "syntax"      => FPPSyntax.toolMain(options.args)
      case "to-cpp"      => FPPToCpp.toolMain(options.args)
      case "to-dict"     => FPPToDict.toolMain(options.args)
      case "to-json"     => FPPtoJson.toolMain(options.args)
      case "to-layout"   => FPPToLayout.toolMain(options.args)
    }

    Right(None)
  }

  def main(args: Array[String]) = {
    if (args.length >= 1) {
      Tool(name).mainMethod(
        args.slice(0, 1),
        oparser,
        Options(args.slice(1, args.length), ""),
        command
      )
    } else {
      Tool(name).mainMethod(
        args,
        oparser,
        Options(args, ""),
        command
      )
    }
  }

  val builder = OParser.builder[Options]
  val name = "fpp"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      note("""FPP Commands:

check       performs semantic checking of FPP models
depend      computes dependencies for FPP source files
filenames   writes out the names of C++ files generated from FPP source files
format      parses FPP source files and writes out formatted source files
from-xml    parses older F Prime XML files and converts them to FPP files
locate-defs parses FPP source files and reports the locations of symbol definitions
locate-uses parses FPP source files and reports the locations of symbols used in the files
syntax      parses FPP source files into an abstract syntax tree (AST) and optionally writes out the AST
to-cpp      parses FPP models, performs semantic checking on them, and writes out C++ files
to-dict     writes JSON dictionaries corresponding to F Prime topologies
to-json     parses an FPP model, performs semantic checking on it, and writes out the model in JSON
to-layout   writes layout text files for connection graphs within F Prime topologies
"""),
      help('h', "help").text("print this message and exit"),
      version('v', "version").text("print version and exit"),
      arg[String]("COMMAND")
        .required()
        .action((f, c) => c.copy(command = f))
        .validate(c =>
          c match {
            case "check" | "depend" | "filenames" | "format" | "from-xml" |
                "locate-defs" | "locate-uses" | "syntax" |
                "to-cpp" | "to-dict" | "to-json" | "to-layout" =>
              Right(None)
            case _ => Left(s"invalid fpp command '$c'")
          }
        ),
      note("""
Run 'fpp COMMAND --help' for more information on a command.
""")
    )
  }

}
