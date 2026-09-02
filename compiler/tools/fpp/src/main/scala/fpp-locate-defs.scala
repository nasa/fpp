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
      aTul <- ToolUtils.parseFilesAndResolveAsts(Analysis(), files)
      a <- Right(aTul._1)
      tul <- Right(aTul._2)
      tul <- expandTemplates(a, tul)
    }
    yield {
      val config = LocateDefsFppWriter.State(options.dir)
      val lines = tul.map(LocateDefsFppWriter.transUnit(config, _)).flatten
      mapSeq(lines, System.out.println(_))
    }
  }

  /** Expand templates, returning the translation units with expanded members.
   *  If expansion fails (for example, because the model is incomplete), fall
   *  back to the unexpanded translation units so that the tool still reports
   *  the locations it can determine. */
  private def expandTemplates(
    a: Analysis,
    tul: List[Ast.TransUnit]
  ): Result.Result[List[Ast.TransUnit]] = {
    val result = for {
      a <- EnterSymbols.visitList(a, tul, EnterSymbols.transUnit)
      aTul <- ResolveTemplates.transUnit(a, tul)
    } yield aTul._2
    result match {
      case Right(tul) => Right(tul)
      case Left(_) => Right(tul)
    }
  }

  def toolMain(args: Array[String]) =
    Tool(name).mainMethod(args, oparser, Options(), command)

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
