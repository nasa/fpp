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

    def createDictionary(dict: dictionary.Dictionary, componentMap: Map[Symbol.Component, Component]): dictionary.Dictionary = {
        var newDict = dict
        for((componentSymbol, component) <- componentMap) {
            // call func to gather commmands
            for((commandOpcode, commandEntry) <- component.commandMap) {
                newDict = newDict.addCommand(commandOpcode, commandEntry)
            }
            // call func to gather events
            // call func to gather channels
            // call func to gather params
            // call func to gather commmands
            for((paramIdentifier, paramEntry) <- component.paramMap) {
                newDict.addParameter(paramIdentifier, paramEntry)
            }
            // create Dictionary object with above properties
            // add dicitionary to component to dictionary mapping
        }
        // return component to dictionary mapping
        return newDict
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
        val d = dictionary.Dictionary()
        for {
            tul <- Result.map(files, Parser.parseFile (Parser.transUnit) (None) _)
            a <- CheckSemantics.tuList(a, tul)
            d <- createDictionary(d, a.componentMap).asInstanceOf[Result.Result[Unit]]
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
