package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser

object FPPToCpp {

  case class Options(
    autoTestHelpers: Boolean = false,
    dir: Option[String] = None,
    files: List[File] = Nil,
    imports: List[File] = Nil,
    guardPrefix: Option[String] = None,
    names: Option[String] = None,
    pathPrefixes: List[String] = Nil,
    defaultStringSize: Int = CppWriterState.defaultDefaultStringSize,
    template: Boolean = false,
    unitTest: Boolean = false,
  )

  def command(options: Options) = {
    val testHelperMode = CppWriter.getTestHelperMode(options.autoTestHelpers)
    val files = options.files.reverse match {
      case Nil => List(File.StdIn)
      case list => list
    }
    val a = Analysis(inputFileSet = options.files.toSet)
    val mode = CppWriter.getMode(options.template, options.unitTest)
    for {
      tulFiles <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
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
      // Compute the generated file names. This step also checks for
      // name collisions.
      s <- mode match {
        case CppWriter.Autocode => ComputeAutocodeCppFiles.visitList (
          CppWriterState(a),
          tulFiles,
          ComputeAutocodeCppFiles.transUnit
        )
        case CppWriter.ImplTemplate => ComputeImplCppFiles.visitList(
          CppWriterState(a),
          tulFiles,
          ComputeImplCppFiles.transUnit
        )
        case CppWriter.UnitTest => for {
          s <- ComputeAutocodeCppFiles.visitList(
            CppWriterState(a),
            tulFiles,
            ComputeAutocodeCppFiles.transUnit
          )
          s <- {
            val computeTestCppFiles = ComputeTestCppFiles(testHelperMode)
            computeTestCppFiles.visitList(
              s,
              tulFiles,
              computeTestCppFiles.transUnit
            )
          }
        } yield s
        case CppWriter.UnitTestTemplate => 
          val computeTestImplCppFiles = ComputeTestImplCppFiles(testHelperMode)
          computeTestImplCppFiles.visitList(
            CppWriterState(a),
            tulFiles,
            computeTestImplCppFiles.transUnit
          )
      }
      // If file name output is requested, then write it now
      _ <- options.names match {
        case Some(fileName) => writeCppFileNames(
          s.locationMap.toList.map(_._1), fileName
        )
        case None => Right(())
      }
      _ <- {
        val dir = options.dir match {
          case Some(dir1) => dir1
          case None => "."
        }
        val state = CppWriterState(
          a,
          dir,
          options.guardPrefix,
          options.pathPrefixes,
          options.defaultStringSize,
          Some(name)
        )
        mode match {
          case CppWriter.Autocode => AutocodeCppWriter.tuList(state, tulFiles)
          case CppWriter.ImplTemplate => ImplCppWriter.tuList(state, tulFiles)
          case CppWriter.UnitTest =>
            TestCppWriter(testHelperMode).tuList(state, tulFiles)
          case CppWriter.UnitTestTemplate =>
            TestImplCppWriter(testHelperMode).tuList(state, tulFiles)
        }
      }
    } yield ()
  }

  def writeCppFileNames(cppFiles: List[String], fileName: String) = {
    val file = File.fromString(fileName)
    for { writer <- file.openWrite() 
    } yield { 
      cppFiles.sorted.map(writer.println(_))
      writer.close()
    }
  }

  def main(args: Array[String]) =
    Tool(name).mainMethod(args, oparser, Options(), command)

  val builder = OParser.builder[Options]

  val name = "fpp-to-cpp"

  val oparser = {
    import builder._
    OParser.sequence(
      programName(name),
      head(name, Version.v),
      help('h', "help").text("print this message and exit"),
      opt[Unit]('a', "auto-test-helpers")
        .action((_, c) => c.copy(autoTestHelpers = true))
        .text("enable automatic generation of test helper code"),
      opt[String]('d', "directory")
        .valueName("<dir>")
        .action((d, c) => c.copy(dir = Some(d)))
        .text("output directory"),
      opt[Seq[String]]('i', "imports")
        .valueName("<file1>,<file2>...")
        .action((i, c) => c.copy(imports = i.toList.map(File.fromString(_))))
        .text("files to import"),
      opt[String]('g', "guard-prefix")
        .valueName("<prefix>")
        .action((g, c) => c.copy(guardPrefix = Some(g)))
        .text("prefix for generated include guards"),
      opt[String]('n', "names")
        .valueName("<file>")
        .action((n, c) => c.copy(names = Some(n)))
        .text("write names of generated files to <file>"),
      opt[Seq[String]]('p', "path-prefixes")
        .valueName("<prefix1>,<prefix2>...")
        .action((p, c) => c.copy(pathPrefixes = p.toList))
        .text("prefixes to delete from generated file paths"),
      opt[Int]('s', "size")
        .valueName("<size>")
        .validate(s => if (s > 0) success else failure("size must be greater than zero"))
        .action((s, c) => c.copy(defaultStringSize = s))
        .text("default string size"),
      opt[Unit]('t', "template")
        .action((_, c) => c.copy(template = true))
        .text("emit template code"),
      opt[Unit]('u', "unit-test")
        .action((_, c) => c.copy(unitTest = true))
        .text("emit unit test code"),
      arg[String]("file ...")
        .unbounded()
        .optional()
        .action((f, c) => c.copy(files = File.fromString(f) :: c.files))
        .text("files to translate"),
    )
  }

}
