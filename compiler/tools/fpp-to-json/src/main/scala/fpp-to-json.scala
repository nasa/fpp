package fpp.compiler

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
      files: List[File] = Nil
  )

  def command(options: Options) = {
    fpp.compiler.util.Error.setTool(Tool(name))
    val files = options.files.reverse match {
      case Nil  => List(File.StdIn)
      case list => list
    }
    for {
      tul <- Result.map(files, Parser.parseFile(Parser.transUnit)(None) _)
      tul <- resolveIncludes(tul)
      _ <- writeAst (options) (tul)
      _ <- writeLocMap (options)
      _ <- writeAnalysis (options) (tul)
    } yield ()
  }

  def main(args: Array[String]) =
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
      writer.println(json)
      writer.close()
    }
  }

  def writeAst (options: Options) (tul: List[Ast.TransUnit]):
    Result.Result[Unit] =
      writeJson(options, "fpp-ast.json", AstJsonEncoder.astToJson(tul))

  def writeLocMap (options: Options): Result.Result[Unit] =
    writeJson(options, "fpp-loc-map.json", LocMapJsonEncoder.locMapToJson)

  def writeAnalysis (options: Options) (tul: List[Ast.TransUnit]):
    Result.Result[Unit] =
    options.syntaxOnly match {
      case false =>
        val files = options.files.reverse match {
          case Nil  => List(File.StdIn)
          case list => list
        }
        val a = Analysis(inputFileSet = options.files.toSet)
        for {
          a <- CheckSemantics.tuList(a, tul)
          _ <- writeJson(options, "fpp-analysis.json", AnalysisJsonEncoder.analysisToJson(a))
        } yield ()
      case true => Right(())
    }

  def resolveIncludes(tul: List[Ast.TransUnit]):
    Result.Result[List[Ast.TransUnit]] =
  for {
    result <- ResolveSpecInclude.transformList(
      Analysis(),
      tul,
      ResolveSpecInclude.transUnit
    )
  } yield result._2

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
