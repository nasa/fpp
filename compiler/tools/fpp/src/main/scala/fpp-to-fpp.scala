package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPToFPP {

  case class Options(
    expand: Boolean = false,
    files: List[File] = List(),
    imports: List[File] = Nil,
  )

  def command(options: Options) = {
    Error.setTool(Tool(name))
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    for {
      tul <- ToolUtils.parseFiles(files ++ options.imports)
      (a, tul) <- expandTemplates (options) (Analysis(), tul)
      tulFiles = tul.take(files.length)
      _ <- writeFpp (options) (tulFiles)
    }
    yield ()
  }

  def toolMain(args: Array[String]) =
    Tool(name).mainMethod(args, oparser, Options(), command)

  def expandTemplates(options: Options)(a: Analysis, tul: List[Ast.TransUnit]):
    Result.Result[(Analysis, List[Ast.TransUnit])] =
  {
    options.expand match {
      case true => ExpandSyntaxAndEnterSymbols.tuList(a, tul)
      case false => Right(a, tul)
    }
  }

  def writeFpp(options: Options)(tul: List[Ast.TransUnit]):
    Result.Result[List[Ast.TransUnit]] =
  {
    val lines = tul.flatMap(FppWriter.transUnit)
    lines.map(Line.write(Line.stdout))
    Right(tul)
  }

  val builder = OParser.builder[Options]

  val name = "fpp-to-fpp"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      opt[Unit]('e', "expand")
        .action((_, c) => c.copy(expand = true))
        .text("expand syntax"),
      help('h', "help").text("print this message and exit"),
      opt[Seq[String]]('i', "imports")
        .valueName("<file1>,<file2>...")
        .action((i, c) => c.copy(imports = i.toList.map(File.fromString(_))))
        .text("files to import"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

}
