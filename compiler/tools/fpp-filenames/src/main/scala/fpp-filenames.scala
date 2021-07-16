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
    files: List[File] = List(),
  )

  def command(options: Options) = {
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    val a = Analysis(inputFileSet = options.files.toSet)
    for {
      tul <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
      aTul <- ResolveSpecInclude.transformList(
        a,
        tul,
        ResolveSpecInclude.transUnit
      )
      a <- Right(aTul._1)
      tul <- Right(aTul._2)
      a <- EnterSymbols.visitList(a, tul, EnterSymbols.transUnit)
      xmlFiles <- getXmlFiles(a, tul)
      cppFiles <- getCppFiles(tul)
    } yield {
      val files = xmlFiles ++ cppFiles
      files.sorted.map(System.out.println)
    }
  }

  def getCppFiles(tul: List[Ast.TransUnit]) =
    for {
      s <- ComputeCppFiles.visitList(Map(), tul, ComputeCppFiles.transUnit)
    }
    yield s.toList.map(_._1)

  def getXmlFiles(a: Analysis, tul: List[Ast.TransUnit]) =
    for {
      s <- ComputeXmlFiles.visitList(
        XmlWriterState(a),
        tul,
        ComputeXmlFiles.transUnit
      )
    }
    yield s.locationMap.toList.map(_._1)

  def main(args: Array[String]) = {
    Error.setTool(Tool(name))
    val options = OParser.parse(oparser, args, Options())
    for { result <- options } yield {
      command(result) match {
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

  val name = "fpp-filenames"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      help('h', "help").text("print this message and exit"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

}
