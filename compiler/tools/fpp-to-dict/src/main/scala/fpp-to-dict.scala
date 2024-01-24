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
        files: List[File] = Nil
    )
    // TODO: need to add arg for including dependency FPP files

    def constructDictionary(a: Analysis): Iterable[dictionary.Dictionary] = {
        val dictionaryList = for (((_, t), index) <- a.topologyMap.zipWithIndex) yield {
            val constructedDictionary = dictionary.Dictionary().buildDictionary(a, t)
            val jsonEncoder = dictionary.DictionaryJsonEncoder(a, constructedDictionary)
            writeJson("justine-test-" + index + ".json",  jsonEncoder.dictionaryToJson)
            constructedDictionary
        }
        return dictionaryList
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
    def command(options: Options): Result.Result[Unit] = {
        fpp.compiler.util.Error.setTool(Tool(name))
         val files = options.files.reverse match {
            case Nil  => List(File.StdIn)
            case list => list
        }
        val a = Analysis(inputFileSet = options.files.toSet)
        for {
            tul <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
            a <- CheckSemantics.tuList(a, tul)
            dictionaryList <- constructDictionary(a).asInstanceOf[Result.Result[dictionary.Dictionary]]
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
        )
    }
}
