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
        defaultStringSize: Int = 80,
        deploymentName: String = "",
        frameworkVersion: String = "",
        projectVersion: String = "",
        libraryVersions: List[String] = Nil,
        dictionarySpecVersion: String = "1.0.0"
    )

    def writeDictionary(a: Analysis, defaultStringSize: Int, metadata: dictionary.DictionaryMetadata): Result.Result[Unit] = {
        for (((_, t), index) <- a.topologyMap.zipWithIndex) yield {
            val constructedDictionary = dictionary.Dictionary().buildDictionary(a, t)
            val jsonEncoder = dictionary.DictionaryJsonEncoder(a, constructedDictionary, metadata, defaultStringSize)
            writeJson("topology-" + index + "-dictionary.json",  jsonEncoder.dictionaryToJson)
        }
        Right(())
    }

    def writeJson (fileName: String, json: io.circe.Json): Result.Result[Unit] = {
        val path = java.nio.file.Paths.get(".", fileName)
        val file = File.Path(path)
        for (writer <- file.openWrite()) yield {
            writer.println(json)
            writer.close()
        }
    }

    // create Analysis
    // extract info we need from analysis and store in dictionary data structure (done in Dictionary.scala)
    // write json to file (maybe the fpp-to-dict tool should have a dictionary file name input?)
    def command(options: Options) = {
        fpp.compiler.util.Error.setTool(Tool(name))
         val files = options.files.reverse match {
            case Nil  => List(File.StdIn)
            case list => list
        }
        val a = Analysis(inputFileSet = options.files.toSet)
        val metadata = dictionary.DictionaryMetadata(options.deploymentName, options.projectVersion, options.frameworkVersion, options.libraryVersions, options.dictionarySpecVersion)
        for {
            tulFiles <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
            tulImports <- Result.map(options.imports, Parser.parseFile (Parser.transUnit) (None) _)
            a <- CheckSemantics.tuList(a, tulFiles ++ tulImports)
            _ <- writeDictionary(a, options.defaultStringSize, metadata)
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
            opt[Int]('s', "size")
                .valueName("<size>")
                .validate(s => if (s > 0) success else failure("size must be greater than zero"))
                .action((s, c) => c.copy(defaultStringSize = s))
                .text("default string size"),
            opt[String]('d', "deployment")
                .valueName("<deployment>")
                .action((d, c) => c.copy(deploymentName = d))
                .text("deployment name"),
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
