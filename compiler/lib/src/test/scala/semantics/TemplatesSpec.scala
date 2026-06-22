package fpp.compiler.test

import fpp.compiler.util.{File => FppFile}
import java.io.File

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.transform._
import fpp.compiler.codegen._

import fpp.compiler.syntax.{Lexer,Parser,TokenId}

import org.scalatest.wordspec.AnyWordSpec

class TemplatesSpec extends AnyWordSpec {

    "simple" should {
        expandUniqueString("""
        template T(constant c: string) {
            constant f = c
        }

        module M1 {
            expand T(constant "a")
        }

        module M2 {
            expand T(constant "b")
        }

        module M3 {
            expand T(constant "c")
        }
        """)
    }

    "OK" should {
        def ok(file: File): Unit = {
            val Right(tul) = Parser.parseFile(Parser.transUnit)(None)(FppFile.fromString(
                file.toPath().toString()
            ))

            checkExpandUnique(tul)
        }

        val dir = new File("lib/src/test/input/templates/ok")
        val files = dir.listFiles.filter(_.isFile)
            .filter(_.getName.endsWith(".fpp"))
            .foreach { file => s"expand $file" in ok(file) }
    }

    private def checkNodeIsUnique(j: io.circe.Json, visited: Set[AstNode.Id]): Set[AstNode.Id] = {
        val node = j.asObject.get.toMap
        val data = node("data")
        val id = node("id")

        val idNumber = id.asNumber.get.toInt.get
        if visited.contains(idNumber) then {
            Console.err.println(s"duplicate ast node id ${idNumber}")
            Console.err.println(Locations.get(idNumber))
            Console.err.println(j)
            Console.err.flush()
            assert(false)
        }

        visited + idNumber
    }

    private def checkAllNodesUnique(tul: List[Ast.TransUnit]): Unit = {
        val j = AstJsonEncoder.astToJson(tul)
        // Console.err.println(j)
        j.findAllByKey("AstNode").foldRight(Set())(checkNodeIsUnique)
    }

    def expandUniqueString(s: String): Unit = {
        val Right(tul) = Parser.parseString(Parser.transUnit)(s)
        "expand" in {
            checkExpandUnique(tul)
        }
    }

    def checkExpandUnique(tul: Ast.TransUnit): Unit = {
        val a = Analysis()
        val tul1 = for {
            a_tul <- ResolveSpecInclude.transformList(a, List(tul), ResolveSpecInclude.transUnit)
            a <- Right(a_tul._1)
            tul <- Right(a_tul._2)

            a <- EnterSymbols.visitList(a, tul, EnterSymbols.transUnit)
            a_tul <- ResolveTemplates.transUnit(a, tul)

            a <- Right(a_tul._1)
            tul <- Right(a_tul._2)
        } yield tul

        tul1 match {
            case Right(tul) => checkAllNodesUnique(tul)
            case Left(l) => {
                Console.err.println(s"failed with error")
                l.print
                Console.err.flush()
                assert(false)
            }
        }
    }

}
