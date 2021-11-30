package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPDepend {

  /** Whether to sort output */
  sealed trait Sorting
  object Sorting {
    case object Yes extends Sorting
    case object No extends Sorting
  }

  case class Options(
    directFile: Option[String] = None,
    files: List[File] = List(),
    frameworkFile: Option[String] = None,
    generatedFile: Option[String] = None,
    includedFile: Option[String] = None,
    missingFile: Option[String] = None
  )

  def mapIterable[T](
    it: Iterable[T],
    f: String => Unit,
    sorting: Sorting = Sorting.Yes
  ) =
    sorting match {
      case Sorting.Yes => it.map(_.toString).toArray.sortWith(_ < _).map(f)
      case Sorting.No => it.map(_.toString).map(f)
    }

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
      a <- ComputeDependencies.tuList(a, tul)
      _ <- options.directFile match {
        case Some(file) => writeIterable(a.directDependencyFileSet, file)
        case None => Right(())
      }
      _ <- options.frameworkFile match {
        case Some(file) =>
          for {
            ds <- ComputeFrameworkDependencies.visitList(
               Set(),
               tul,
               ComputeFrameworkDependencies.transUnit
             )
          }
          yield {
            val ds1 = FrameworkDependency.sort(ds.toSeq).map(_.toString)
            // Don't sort alphabetically
            // Already sorted by dependency order
            writeIterable(ds1, file, Sorting.No)
          }
        case None => Right(())
      }
      _ <- options.generatedFile match {
        case Some(file) =>
          for (files <- ComputeGeneratedFiles.getFiles(tul))
            yield writeIterable(files, file)
        case None => Right(())
      }
      _ <- options.includedFile match {
        case Some(file) => writeIterable(a.includedFileSet, file)
        case None => Right(())
      }
      _ <- options.missingFile match {
        case Some(file) => writeIterable(a.missingDependencyFileSet, file)
        case None => Right(())
      }
    } yield mapIterable(a.dependencyFileSet, System.out.println(_))
  }

  def writeIterable[T](
    its: Iterable[T],
    fileName: String,
    sorting: Sorting = Sorting.Yes
  ): Result.Result[Unit] = {
    val file = File.fromString(fileName)
    for (writer <- file.openWrite())
      yield {
        mapIterable(its, writer.println(_), sorting)
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

  val name = "fpp-depend"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      opt[String]('d', "direct")
        .valueName("<file>")
        .action((m, c) => c.copy(directFile = Some(m)))
        .text("write direct dependencies to file"),
      opt[String]('f', "framework")
        .valueName("<file>")
        .action((m, c) => c.copy(frameworkFile = Some(m)))
        .text("write framework dependencies to file"),
      opt[String]('g', "generated")
        .valueName("<file>")
        .action((m, c) => c.copy(generatedFile = Some(m)))
        .text("write names of generated files to file"),
      opt[String]('i', "included")
        .valueName("<file>")
        .action((m, c) => c.copy(includedFile = Some(m)))
        .text("write included dependencies to file"),
      opt[String]('m', "missing")
        .valueName("<file>")
        .action((m, c) => c.copy(missingFile = Some(m)))
        .text("write missing dependencies to file"),
      help('h', "help").text("print this message and exit"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("input files"),
    )
  }

}
