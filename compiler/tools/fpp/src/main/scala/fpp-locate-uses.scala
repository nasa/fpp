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
    imports: List[File] = Nil,
  )

  def mapSeq[T](seq: Seq[T], f: String => Unit) =
    seq.map(_.toString).sortWith(_ < _).map(f)

  def command(options: Options): Result.Result[Unit] = {
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    val a = Analysis(inputFileSet = options.files.toSet)
    for {
      aTulTul <- ToolUtils.parseFilesAndResolveAsts(a, files, options.imports)
      a <- Right(aTulTul._1)
      tulFiles <- Right(aTulTul._2)
      tulImports <- Right(aTulTul._3)
      aTul <- ResolveTemplates.tuList(a, tulFiles ++ tulImports)
      a <- Right(aTul._1)
      tul <- Right(aTul._2)
      a <- CheckSemantics.tuList(a, tul)
      tulFiles <- Right(tul.take(tulFiles.length))
      a <- UsedSymbols.visitList(a, tulFiles, UsedSymbols.transUnit)
    } yield {
      val list = a.usedSymbolSet.flatMap(writeUsedSymbol(a, options)).toList
      mapSeq(list, System.out.println(_))
    }
  }

  def writeUsedSymbol(a: Analysis, options: Options)(s: Symbol): List[Line] = {
    val loc = Locations.get(s.getNodeId)
    (loc.file, specLocKind(s)) match {
      case (File.Path(path), Some(kind)) => {
        val name = a.getQualifiedName(s)
        val nameList = {
          s match {
            case _: Symbol.EnumConstant => name.qualifier
            case _ => name.qualifier :+ name.base
          }
        }
        val nodeList = nameList.map(s => AstNode.create(s))
        val qualIdentNode = AstNode.create(Ast.QualIdent.fromNodeList(nodeList, false))
        val baseDir = options.dir match {
          case Some(dir) => dir
          case None => ""
        }
        val baseDirPath = java.nio.file.Paths.get(baseDir).toAbsolutePath
        val relativePath = baseDirPath.relativize(path)
        val fileNode = AstNode.create(relativePath.normalize.toString)
        val isDictionaryDef = s match {
          case Symbol.Array(aNode) => aNode._2.data.isDictionaryDef
          case Symbol.AliasType(aNode) => aNode._2.data.isDictionaryDef
          case Symbol.Struct(aNode) => aNode._2.data.isDictionaryDef
          case Symbol.Enum(aNode) => aNode._2.data.isDictionaryDef
          case Symbol.Constant(aNode) => aNode._2.data.isDictionaryDef
          case _ => false
        }
        val specLocNode = AstNode.create(Ast.SpecLoc(kind, qualIdentNode, fileNode, isDictionaryDef))
        val specLocAnnotatedNode = (Nil, specLocNode, Nil)
        FppWriter.specLocAnnotatedNode((), specLocAnnotatedNode)
      }
      case _ => Nil
    }
  }

  /** Gets the kind of location specifier to write for a used symbol.
   *  Returns None if the use requires no location specifier. */
  def specLocKind(s: Symbol): Option[Ast.SpecLoc.Kind] =
    s match {
      case _: Symbol.AbsType => Some(Ast.SpecLoc.Type)
      case _: Symbol.AliasType => Some(Ast.SpecLoc.Type)
      case _: Symbol.Array => Some(Ast.SpecLoc.Type)
      case _: Symbol.Component => Some(Ast.SpecLoc.Component)
      case _: Symbol.ComponentInstance => Some(Ast.SpecLoc.Instance)
      case _: Symbol.Constant => Some(Ast.SpecLoc.Constant)
      case _: Symbol.Enum => Some(Ast.SpecLoc.Type)
      case _: Symbol.EnumConstant => Some(Ast.SpecLoc.Type)
      case _: Symbol.Interface => Some(Ast.SpecLoc.Interface)
      case _: Symbol.Module => throw InternalError("use should not be module symbol")
      case _: Symbol.Port => Some(Ast.SpecLoc.Port)
      case _: Symbol.StateMachine => Some(Ast.SpecLoc.StateMachine)
      case _: Symbol.Struct => Some(Ast.SpecLoc.Type)
      case _: Symbol.System => throw InternalError("use should not be system symbol")
      case _: Symbol.Topology => Some(Ast.SpecLoc.Instance)
      case _: Symbol.Template => Some(Ast.SpecLoc.Template)
      case _: Symbol.TemplateConstantArg => None
      case _: Symbol.TemplateTypeArg => None
      case _: Symbol.TemplateInterfaceArg => None
    }

  def toolMain(args: Array[String]) =
    Tool(name).mainMethod(args, oparser, Options(), command)

  val builder = OParser.builder[Options]

  val name = "fpp-locate-uses"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      opt[String]('d', "directory")
        .valueName("<dir>")
        .action((d, c) => c.copy(dir = Some(d)))
        .text("base directory"),
      opt[Seq[String]]('i', "imports")
        .valueName("<file1>,<file2>...")
        .action((i, c) => c.copy(imports = i.toList.map(File.fromString(_))))
        .text("files to import"),
      help('h', "help").text("print this message and exit"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("files to analyze"),
    )
  }

}
