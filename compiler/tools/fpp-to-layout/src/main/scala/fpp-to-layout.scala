package fpp.compiler

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
            tulFiles <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
            aTulFiles <- ResolveSpecInclude.transformList(
                a,
                tulFiles, 
                ResolveSpecInclude.transUnit
            )
            tulFiles <- Right(aTulFiles._2)
            tulImports <- Result.map(options.imports, Parser.parseFile (Parser.transUnit) (None) _)
            a <- CheckSemantics.tuList(a, tulFiles ++ tulImports)
            state <- {
                val dir = options.dir match {
                    case Some(dir1) => dir1
                    case None => "."
                }
                ComputeLayoutFiles.visitList(
                    LayoutWriterState(a=a, dir=dir),
                    tulFiles, 
                    ComputeLayoutFiles.transUnit
                )
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

    def main(args: Array[String]) =
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
