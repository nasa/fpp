package fpp.compiler

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import scopt.OParser


object FPPToDict {
    case class Options(
        files: List[File] = Nil,
        imports: List[File] = Nil,
        dir: Option[String] = None,
        defaultStringSize: Int = DictionaryJsonEncoderState.defaultDefaultStringSize,
        frameworkVersion: String = "",
        projectVersion: String = "",
        libraryVersions: List[String] = Nil,
        dictionarySpecVersion: String = "1.0.0"
    )

    def command(options: Options) = {
        fpp.compiler.util.Error.setTool(Tool(name))
         val files = options.files.reverse match {
            case Nil  => List(File.StdIn)
            case list => list
        }
        val a = Analysis(inputFileSet = options.files.toSet)
        val metadata = DictionaryMetadata(
            projectVersion=options.projectVersion, 
            frameworkVersion=options.frameworkVersion, 
            libraryVersions=options.libraryVersions, 
            dictionarySpecVersion=options.dictionarySpecVersion
        )
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
                ComputeDictionaryFiles.visitList(
                    DictionaryJsonEncoderState(a=a, dir=dir, defaultStringSize=options.defaultStringSize, metadata=metadata),
                    tulFiles, 
                    ComputeDictionaryFiles.transUnit
                )
            }
            _ <- DictionaryJsonWriter.visitList(state, tulFiles, DictionaryJsonWriter.transUnit)
        } yield ()
    }

    def main(args: Array[String]) =
        Tool(name).mainMethod(args, oparser, Options(), command)


    val builder = OParser.builder[Options]
    val name = "fpp-to-dict"

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
                .text("output directory"),
            opt[Int]('s', "size")
                .valueName("<size>")
                .validate(s => if (s > 0) success else failure("size must be greater than zero"))
                .action((s, c) => c.copy(defaultStringSize = s))
                .text("default string size"),
            opt[String]('f', "frameworkVersion")
                .valueName("<frameworkVersion>")
                .action((f, c) => c.copy(frameworkVersion = f))
                .text("framework version"),
            opt[String]('p', "projectVersion")
                .valueName("<projectVersion>")
                .action((p, c) => c.copy(projectVersion = p))
                .text("project version"),
            opt[Seq[String]]('l', "libraryVersions")
                .valueName("<lib1ver>,<lib2ver>,...")
                .action((l, c) => {
                    c.copy(libraryVersions = l.toList)
                })
                .text("library versions")
        )
    }
}
