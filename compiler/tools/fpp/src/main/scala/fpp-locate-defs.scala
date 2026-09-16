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
   *  We must expand templates, because the expansions generate definitions
   *  that we have to locate. */
  private def expandTemplates(
    a: Analysis,
    tul: List[Ast.TransUnit]
  ): Result.Result[List[Ast.TransUnit]] = {
    ResolveTemplates.tuList(a, tul) match {
      case Right(aTul) => Right(aTul._2)
      case Left(error) =>
        if HasTemplateExpansion.check(tul)
        then Left(error)
        else AddStateEnums.transUnitList(tul)
    }
  }

  /** Does a model contain a template expansion specifier? */
  private object HasTemplateExpansion extends AstStateVisitor {

    type State = Boolean

    /** Check a list of translation units */
    def check(tul: List[Ast.TransUnit]): Boolean =
      visitList(false, tul, transUnit) match {
        case Right(result) => result
        case Left(_) => true
      }

    override def defModuleAnnotatedNode(
      s: State,
      aNode: Ast.Annotated[AstNode[Ast.DefModule]]
    ) = visitList(s, aNode._2.data.members, matchModuleMember)

    override def defModuleTemplateAnnotatedNode(
      s: State,
      aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
    ) = visitList(s, aNode._2.data.members, matchModuleMember)

    override def specTemplateExpandAnnotatedNode(
      s: State,
      aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
    ) = Right(true)

    override def transUnit(s: State, tu: Ast.TransUnit) =
      visitList(s, tu.members, matchTuMember)

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
