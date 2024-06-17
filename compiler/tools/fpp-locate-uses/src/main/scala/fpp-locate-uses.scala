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
      tulFiles <- Result.map(
        files,
        Parser.parseFile (Parser.transUnit) (None) _
      )
      aTulFiles <- ResolveSpecInclude.transformList(
        a,
        tulFiles, 
        ResolveSpecInclude.transUnit
      )
      tulFiles <- Right(aTulFiles._2)
      tulImports <- Result.map(
        options.imports,
        Parser.parseFile (Parser.transUnit) (None) _
      )
      a <- CheckSemantics.tuList(a, tulFiles ++ tulImports)
      a <- UsedSymbols.visitList(a, tulFiles, UsedSymbols.transUnit)
    } yield {
      val list = a.usedSymbolSet.flatMap(writeUsedSymbol(a, options) _).toList
      mapSeq(list, System.out.println(_))
    }
  }

  def writeUsedSymbol(a: Analysis, options: Options)(s: Symbol): List[Line] = {
    val loc = Locations.get(s.getNodeId)
    loc.file match {
      case File.Path(path) => {
        val name = a.getQualifiedName(s)
        val nameList = {
          s match {
            case _: Symbol.EnumConstant => name.qualifier
            case _ => name.qualifier :+ name.base
          }
        }
        val nodeList = nameList.map(s => AstNode.create(s))
        val qualIdentNode = AstNode.create(Ast.QualIdent.fromNodeList(nodeList))
        val baseDir = options.dir match {
          case Some(dir) => dir
          case None => ""
        }
        val baseDirPath = java.nio.file.Paths.get(baseDir).toAbsolutePath
        val relativePath = baseDirPath.relativize(path)
        val fileNode = AstNode.create(relativePath.normalize.toString)
        val kind = s match {
          case _: Symbol.AbsType => Ast.SpecLoc.Type
          case _: Symbol.Array => Ast.SpecLoc.Type
          case _: Symbol.Component => Ast.SpecLoc.Component
          case _: Symbol.ComponentInstance => Ast.SpecLoc.ComponentInstance
          case _: Symbol.Constant => Ast.SpecLoc.Constant
          case _: Symbol.Enum => Ast.SpecLoc.Type
          case _: Symbol.EnumConstant => Ast.SpecLoc.Type
          case _: Symbol.Module => throw InternalError("use should not be module symbol")
          case _: Symbol.Port => Ast.SpecLoc.Port
          case _: Symbol.StateMachine => Ast.SpecLoc.StateMachine
          case _: Symbol.Struct => Ast.SpecLoc.Type
          case _: Symbol.Topology => Ast.SpecLoc.Topology
        }
        val specLocNode = AstNode.create(Ast.SpecLoc(kind, qualIdentNode, fileNode))
        val specLocAnnotatedNode = (Nil, specLocNode, Nil)
        FppWriter.specLocAnnotatedNode((), specLocAnnotatedNode)
      }
      case File.StdIn => Nil
    }
  }

  def main(args: Array[String]) =
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
