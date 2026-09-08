package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPtoJson {
  case class Options(
      syntaxOnly: Boolean = false,
      dir: Option[String] = None,
      files: List[File] = Nil,
      format: Boolean = false,
  )

  def command(options: Options) = {
    fpp.compiler.util.Error.setTool(Tool(name))
    val files = options.files.reverse match {
      case Nil  => List(File.StdIn)
      case list => list
    }
    for {
      tul <- ToolUtils.parseFilesAndResolveAsts(Analysis(), files).map(_._2)
      aTulOpt <- analyze (options) (tul)
      _ <- writeAst (options) (aTulOpt.map(_._2).getOrElse(tul))
      _ <- writeLocMap (options)
      _ <- writeAnalysis (options) (aTulOpt.map(_._1))
    } yield ()
  }

  def toolMain(args: Array[String]) =
    Tool(name).mainMethod(args, oparser, Options(), command)

  def writeJson (
    options: Options,
    fileName: String,
    json: io.circe.Json
  ): Result.Result[Unit] = {
    val path =
      java.nio.file.Paths.get(options.dir.getOrElse("."), fileName)
    val file = File.Path(path)
    for (writer <- file.openWrite()) yield {
      writer.println(
        if options.format then
          json
        else
          json.noSpaces
      )
      writer.close()
    }
  }

  def writeAst (options: Options) (tul: List[Ast.TransUnit]):
    Result.Result[Unit] =
      writeJson(options, "fpp-ast.json", AstJsonEncoder.astToJson(tul))

  def writeLocMap (options: Options): Result.Result[Unit] =
    writeJson(options, "fpp-loc-map.json", LocMapJsonEncoder.locMapToJson)

  /** Analyze the model, returning the analysis and the translation units with
   *  templates expanded. Return None if only the syntax was requested. */
  def analyze (options: Options) (tul: List[Ast.TransUnit]):
    Result.Result[Option[(Analysis, List[Ast.TransUnit])]] =
    options.syntaxOnly match {
      case false =>
        val a = Analysis(inputFileSet = options.files.toSet)
        for (aTul <- CheckSemantics.tuList(a, tul)) yield Some(aTul)
      case true => Right(None)
    }

  def writeAnalysis (options: Options) (aOpt: Option[Analysis]):
    Result.Result[Unit] =
    aOpt match {
      case Some(a) =>
        writeJson(options, "fpp-analysis.json", AnalysisJsonEncoder.analysisToJson(a))
      case None => Right(())
    }

  val builder = OParser.builder[Options]

  val name = "fpp-to-json"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      opt[Unit]('s', "syntax only")
        .action((_, c) => c.copy(syntaxOnly = true))
        .text("emit syntax only (location map and abstract syntax tree)"),
      opt[Unit]('f', "format")
        .action((_, c) => c.copy(format = true))
        .text("format JSON with whitespace indentation and newlines"),
      opt[String]('d', "directory")
        .valueName("<dir>")
        .action((d, c) => c.copy(dir = Some(d)))
        .text("output directory"),
      help('h', "help").text("print this message and exit"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files")
    )
  }

}
