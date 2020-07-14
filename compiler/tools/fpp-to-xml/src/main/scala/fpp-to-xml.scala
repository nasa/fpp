package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPToXml {

  case class Options(
    dir: Option[String] = None,
    files: List[File] = Nil,
    imports: List[File] = Nil,
    names: Option[String] = None,
    prefixes: List[String] = Nil,
    defaultStringSize: Int = 80,
  )

  def command(options: Options) = {
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
      xmlFileMap <- ComputeXmlFiles.visitList(Map(), tulFiles, ComputeXmlFiles.transUnit)
      tulImports <- Result.map(options.imports, Parser.parseFile (Parser.transUnit) (None) _)
      a <- CheckSemantics.tuList(a, tulFiles ++ tulImports)
      _ <- options.names match {
        case Some(fileName) => writeXmlFileNames(xmlFileMap.toList.map(_._1), fileName)
        case None => Right(())
      }
      _ <- {
        val dir = options.dir match {
          case Some(dir1) => dir1
          case None => "."
        }
        val state = XmlWriterState(a, dir, options.prefixes, options.defaultStringSize)
        XmlWriter.visitList(state, tulFiles, XmlWriter.transUnit)
      }
    } yield ()
  }

  def writeXmlFileNames(xmlFiles: List[String], fileName: String) = {
    val file = File.fromString(fileName)
    for { writer <- file.openWrite() 
    } yield { 
      xmlFiles.map(writer.println(_))
      writer.close()
    }
  }

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

  val name = "fpp-to-xml"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, "0.1"),
      help('h', "help").text("print this message and exit"),
      opt[String]('d', "directory")
        .valueName("<dir>")
        .action((d, c) => c.copy(dir = Some(d)))
        .text("output directory"),
      opt[Seq[String]]('i', "imports")
        .valueName("<file1>,<file2>...")
        .action((i, c) => c.copy(imports = i.toList.map(File.fromString(_))))
        .text("files to import"),
      opt[String]('n', "names")
        .valueName("<file>")
        .action((n, c) => c.copy(names = Some(n)))
        .text("write names of generated files to <file>"),
      opt[Seq[String]]('p', "prefixes")
        .valueName("<prefix1>,<prefix2>...")
        .action((p, c) => c.copy(prefixes = p.toList))
        .text("path prefixes to delete from imported files"),
      opt[Int]('s', "size")
        .valueName("<size>")
        .validate(s => if (s > 0 && s <= 1024) success else failure("<size> must be between 1 and 1024"))
        .action((s, c) => c.copy(defaultStringSize = s))
        .text("default string size"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("files to translate"),
    )
  }

}
