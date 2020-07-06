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

  def command(options: Options): Result.Result[Unit] = {
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    val a = Analysis(inputFileSet = options.files.toSet)
    for {
      tulFiles <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
      aTulFiles <- ResolveSpecInclude.transformList(
        a,
        tulFiles, 
        ResolveSpecInclude.transUnit
      )
      tulFiles <- Right(aTulFiles._2)
      tulImports <- Result.map(options.imports, Parser.parseFile (Parser.transUnit) (None) _)
      a <- CheckSemantics.tuList(a, tulFiles ++ tulImports)
      a <- UsedSymbols.visitList(a, tulFiles, UsedSymbols.transUnit)
    } yield a.usedSymbolSet.flatMap(writeSymbol(a) _).map(Line.write(Line.stdout) _)
  }

  def writeSymbol(a: Analysis)(s: Symbol): List[Line] = {
    val loc = Locations.get(s.getNodeId)
    loc.file match {
      case File.Path(path) => {
        /*
        val nodeList = (name :: s.moduleNameList).reverse.map(s => AstNode.create(s))
        val qualIdentNode = AstNode.create(Ast.QualIdent.fromNodeList(nodeList))
        val baseDir = s.baseDir match {
          case Some(dir) => dir
          case None => ""
        }
        val baseDirPath = java.nio.file.Paths.get(baseDir).toAbsolutePath
        val relativePath = baseDirPath.relativize(path)
        val fileNode = AstNode.create(relativePath.normalize.toString)
        val specLocNode = AstNode.create(Ast.SpecLoc(kind, qualIdentNode, fileNode))
        val specLocAnnotatedNode = (Nil, specLocNode, Nil)
        FppWriter.specLocAnnotatedNode((), specLocAnnotatedNode)
        */
        Nil
      }
      case File.StdIn => Nil
    }
  }

  def main(args: Array[String]) = {
    Error.setTool(Tool(name))
    for { options <- OParser.parse(oparser, args, Options()) }
    yield {
      command(options) match {
        case Left(error) => {
          error.print
          System.exit(1)
        }
        case _ => ()
      }
    }
    ()
  }

  val builder = OParser.builder[Options]

  val name = "fpp-locate-uses"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, "0.1"),
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
