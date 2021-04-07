package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._
import scopt.OParser

object FPPCheck {

  case class Options(
    files: List[File] = List(),
    unconnectedFile: Option[String] = None
  )

  def command(options: Options) = {
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    val a = Analysis(inputFileSet = options.files.toSet)
    for {
      tul <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
      a <- CheckSemantics.tuList(a, tul)
      _ <- options.unconnectedFile match {
        case Some(file) => writeUnconnectedPorts(a, file)
        case None => Right(())
      }
    } yield a
  }

  def writeUnconnectedPorts(a: Analysis, fileName: String): Result.Result[Unit] = {
    val file = File.fromString(fileName)
    for (writer <- file.openWrite())
      yield { 
        a.topologyMap.map({ case (s, t) => {
          val set = t.unconnectedPortSet
          if (set.size > 0) {
            val name = a.getQualifiedName(s)
            writer.println(s"Topology ${name}:")
            set.map(_.getQualifiedName.toString).
              toArray.sortWith(_ < _).
              map(str => writer.println(s"  $str"))
            writer.println("")
          }
          else
            ()
        }})
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

  val name = "fpp-check"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      help('h', "help").text("print this message and exit"),
      opt[String]('u', "unconnected")
        .valueName("<file>")
        .action((m, c) => c.copy(unconnectedFile = Some(m)))
        .text("write unconnected ports to file"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

}
