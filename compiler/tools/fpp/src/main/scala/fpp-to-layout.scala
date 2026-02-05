package fpp.compiler.tools

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser


object FPPToLayout {
    case class Options(
        files: List[File] = Nil,
        imports: List[File] = Nil,
        dir: Option[String] = None
    )

    def command(options: Options) = {
        fpp.compiler.util.Error.setTool(Tool(name))
         val files = options.files.reverse match {
            case Nil  => List(File.StdIn)
            case list => list
        }
        val a = Analysis(inputFileSet = options.files.toSet)
        for {
            aTulTul <- ToolUtils.parseFilesAndResolveAsts(a, files, options.imports)
            a <- Right(aTulTul._1)
            tulFiles <- Right(aTulTul._2)
            tulImports <- Right(aTulTul._3)
            a <- CheckSemantics.tuList(a, tulFiles ++ tulImports)
            state <- {
                val dir = options.dir match {
                    case Some(dir1) => dir1
                    case None => "."
                }
                Right(LayoutWriterState(a, dir))
            }
            _ <- {
                LayoutWriter.visitList(
                    state, 
                    tulFiles, 
                    LayoutWriter.transUnit
                )
            }
        } yield ()
    }

    def toolMain(args: Array[String]) =
        Tool(name).mainMethod(args, oparser, Options(), command)


    val builder = OParser.builder[Options]
    val name = "fpp-to-layout"

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
                .text(".fpp file(s)"),
            opt[Seq[String]]('i', "imports")
                .valueName("<file1>,<file2>...")
                .action((i, c) => c.copy(imports = i.toList.map(File.fromString(_))))
                .text("files to import"),
            opt[String]('d', "directory")
                .valueName("<dir>")
                .action((d, c) => c.copy(dir = Some(d)))
                .text("output directory")
        )
    }
}
