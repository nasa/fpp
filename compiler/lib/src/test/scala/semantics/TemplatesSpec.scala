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
        module template T(constant c: string) {
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

    // Each model in the error directory must be syntactically correct and must
    // fail analysis. Each model says in a comment which error it should report.
    "error" should {
        def error(file: File): Unit = {
            val tu = Parser.parseFile(Parser.transUnit)(None)(FppFile.fromString(
                file.toPath().toString()
            )) match {
                case Right(tu) => tu
                case Left(e) =>
                    e.print
                    fail(s"$file should parse")
            }
            analyze(tu) match {
                case Left(_) => ()
                case Right(_) => fail(s"analysis of $file should fail")
            }
        }

        val dir = new File("lib/src/test/input/templates/error")
        val files = dir.listFiles.filter(_.isFile)
            .filter(_.getName.endsWith(".fpp"))
            .foreach { file => s"not analyze $file" in error(file) }
    }

    // The content of an expansion: an expansion introduces the definitions of
    // the template body at the expansion site, once per expansion, and a use of
    // a parameter inside the body resolves to the bound parameter.
    "expansion content" should {

        val model = """
        module template T(constant c: U32, type Ty) {
            constant k = c
            type Alias = Ty
            module Inner {
                constant j = c + 1
            }
        }

        module M1 {
            expand T(constant 1, type U32)
        }

        module M2 {
            expand T(constant 2, type I16)
        }
        """

        "introduce the definitions of the body at the expansion site" in {
            val (a, tul) = analyzeString(model)
            val names = qualifiedNames(a)
            List(
                "M1.k", "M1.Alias", "M1.Inner", "M1.Inner.j",
                "M2.k", "M2.Alias", "M2.Inner", "M2.Inner.j"
            ).foreach {
                name => assert(names.contains(name), s"missing definition $name")
            }
            // The definition of the template introduces nothing by itself
            assert(!names.contains("k"), "the template body defined k at top level")
            assert(!names.contains("T.k"), "the template body defined T.k")
            // Each expansion introduces exactly the three members of the body
            assert(membersAt(tul, List("M1")).length == 3)
            assert(membersAt(tul, List("M2")).length == 3)
        }

        "give each expansion its own copy of the definitions" in {
            val (a, tul) = analyzeString(model)
            assert(intValue(a, constantAt(tul, "M1.k").id) == BigInt(1))
            assert(intValue(a, constantAt(tul, "M2.k").id) == BigInt(2))
            assert(intValue(a, constantAt(tul, "M1.Inner.j").id) == BigInt(2))
            assert(intValue(a, constantAt(tul, "M2.Inner.j").id) == BigInt(3))
        }

        "resolve a use of a constant parameter to the bound argument" in {
            val (a, tul) = analyzeString(model)
            val node = constantAt(tul, "M1.k")
            a.useDefMap.get(node.data.value.id) match {
                case Some(Symbol.TemplateConstantArg(paramDef, value)) => {
                    assert(paramDef.name == "c")
                    assert(intValue(a, value.id) == BigInt(1))
                }
                case Some(symbol) => fail(
                    s"c resolved to ${a.getQualifiedName(symbol)}, " ++
                    "not to the bound parameter"
                )
                case None => fail("the use of c is not resolved")
            }
        }

        "resolve a use of a type parameter to the bound argument" in {
            val (a, tul) = analyzeString(model)
            val node = aliasTypeAt(tul, "M2.Alias")
            a.useDefMap.get(node.data.typeName.id) match {
                case Some(Symbol.TemplateTypeArg(paramDef, value)) => {
                    assert(paramDef.name == "Ty")
                    assert(a.typeMap(value.id) == Type.I16)
                }
                case Some(symbol) => fail(
                    s"Ty resolved to ${a.getQualifiedName(symbol)}, " ++
                    "not to the bound parameter"
                )
                case None => fail("the use of Ty is not resolved")
            }
        }

    }

    // Scoping inside an expansion: a name in the template body resolves at the
    // point of the template definition, not at the point of expansion.
    "expansion scoping" should {

        "resolve a name in the body at the point of the definition" in {
            val (a, tul) = analyzeString("""
            module A {
                constant x = 1
                module template T() {
                    constant y = x
                }
            }

            module B {
                constant x = 2
                expand A.T()
            }
            """)
            val node = constantAt(tul, "B.y")
            a.useDefMap.get(node.data.value.id) match {
                case Some(symbol) => assert(
                    a.getQualifiedName(symbol).toString == "A.x",
                    s"x resolved to ${a.getQualifiedName(symbol)}, not to A.x"
                )
                case None => fail("the use of x is not resolved")
            }
            // A.x is 1 and B.x is 2, so the value of B.y shows which one won
            assert(intValue(a, node.id) == BigInt(1))
        }

        // A module defined by an expansion is merged with a same-named module
        // at the expansion site, so that both sets of members are visible from
        // outside. But a use inside the expansion must see only the members
        // that the expansion introduced: otherwise a definition at the
        // expansion site captures a use of a bound parameter, and the argument
        // is silently discarded.
        "not let a same-named module at the expansion site capture a parameter" in {
            val (a, tul) = analyzeString("""
            module template T(constant c: U32) {
                module Inner {
                    constant k = c
                }
            }

            module M {
                module Inner {
                    constant c = 7
                }

                expand T(constant 1)
            }
            """)
            val node = constantAt(tul, "M.Inner.k")
            a.useDefMap.get(node.data.value.id) match {
                case Some(Symbol.TemplateConstantArg(paramDef, value)) => {
                    assert(paramDef.name == "c")
                    assert(intValue(a, value.id) == BigInt(1))
                }
                case Some(symbol) => fail(
                    s"c resolved to ${a.getQualifiedName(symbol)}, " ++
                    "not to the bound parameter"
                )
                case None => fail("the use of c is not resolved")
            }
            // The bound parameter is 1 and M.Inner.c is 7, so the value of
            // M.Inner.k shows which one won
            assert(intValue(a, node.id) == BigInt(1))
            // Both modules named Inner are the same module, so both sets of
            // members are visible from outside the expansion
            val names = qualifiedNames(a)
            assert(names.contains("M.Inner.k"))
            assert(names.contains("M.Inner.c"))
        }

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
        val nodes = j.findAllByKey("AstNode")
        assert(nodes.nonEmpty)
        nodes.foldRight(Set())(checkNodeIsUnique)
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
            aTul <- ResolveTemplates.tuList(a, tul)
            a <- Right(aTul._1)
            tul <- Right(aTul._2)
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

    /** Resolve includes, expand templates, and check semantics */
    private def analyze(tu: Ast.TransUnit):
        Result.Result[(Analysis, List[Ast.TransUnit])] =
        for {
            includeResult <- ResolveSpecInclude.transformList(
                Analysis(),
                List(tu),
                ResolveSpecInclude.transUnit
            )
            templateResult <- ResolveTemplates.tuList(
                includeResult._1,
                includeResult._2
            )
            a <- CheckSemantics.tuList(templateResult._1, templateResult._2)
        }
        yield (a, templateResult._2)

    /** Parse and analyze a string, failing the test if either step fails */
    private def analyzeString(s: String): (Analysis, List[Ast.TransUnit]) = {
        val tu = Parser.parseString(Parser.transUnit)(s) match {
            case Right(tu) => tu
            case Left(e) => {
                e.print
                fail("parsing should succeed")
            }
        }
        analyze(tu) match {
            case Right(result) => result
            case Left(e) => {
                e.print
                fail("analysis should succeed")
            }
        }
    }

    /** Replace each template expansion specifier with the members that it
     *  introduced, so that the members of an expansion appear as members of the
     *  enclosing module */
    private def flattenMembers(members: List[Ast.ModuleMember]):
        List[Ast.ModuleMember] =
        members.flatMap {
            member => member.node match {
                case (_, Ast.ModuleMember.SpecTemplateExpand(node), _) =>
                    flattenMembers(node.data.members.getOrElse(Nil))
                case _ => List(member)
            }
        }

    /** Get the flattened members of the module at the given path.
     *  Two modules with the same name are the same module, so the members of
     *  all of them are returned. */
    private def membersAt(
        tul: List[Ast.TransUnit],
        path: List[String]
    ): List[Ast.ModuleMember] =
        path.foldLeft (flattenMembers(tul.flatMap(_.members))) (
            (members, name) => flattenMembers(
                members.flatMap {
                    member => member.node match {
                        case (_, Ast.ModuleMember.DefModule(node), _)
                            if node.data.name == name => node.data.members
                        case _ => Nil
                    }
                }
            )
        )

    /** Get the unique definition at the given dotted path, selecting the kind
     *  of definition with the function select */
    private def defAt[T](
        tul: List[Ast.TransUnit],
        path: String,
        select: (String, Ast.ModuleMember.Node) => Option[AstNode[T]]
    ): AstNode[T] = {
        val elements = path.split("\\.").toList
        val members = membersAt(tul, elements.init)
        members.flatMap(member => select(elements.last, member.node._2).toList) match {
            case node :: Nil => node
            case Nil => fail(s"there is no definition $path")
            case _ => fail(s"there is more than one definition $path")
        }
    }

    /** Get the constant definition at the given dotted path */
    private def constantAt(tul: List[Ast.TransUnit], path: String):
        AstNode[Ast.DefConstant] =
        defAt[Ast.DefConstant](tul, path, (name, memberNode) => memberNode match {
            case Ast.ModuleMember.DefConstant(defNode)
                if defNode.data.name == name => Some(defNode)
            case _ => None
        })

    /** Get the type alias definition at the given dotted path */
    private def aliasTypeAt(tul: List[Ast.TransUnit], path: String):
        AstNode[Ast.DefAliasType] =
        defAt[Ast.DefAliasType](tul, path, (name, memberNode) => memberNode match {
            case Ast.ModuleMember.DefAliasType(defNode)
                if defNode.data.name == name => Some(defNode)
            case _ => None
        })

    /** Get the integer value of the node with the given id */
    private def intValue(a: Analysis, id: AstNode.Id): BigInt =
        a.valueMap.get(id) match {
            case Some(v) => Analysis.convertValueToType(v, Type.Integer) match {
                case Value.Integer(i) => i
                case v1 => fail(s"$v1 should be an integer value")
            }
            case None => fail(s"node $id should have a value")
        }

    /** The qualified names of the definitions known to the analysis.
     *  A bound template parameter is not a global definition, so it is
     *  excluded. */
    private def qualifiedNames(a: Analysis): Set[String] = {
        val symbols: Set[Symbol] = a.parentSymbolMap.keySet ++
            a.parentSymbolMap.values.toSet ++
            a.symbolScopeMap.keySet ++
            a.useDefMap.values.toSet
        symbols.collect {
            case symbol if !symbol.isInstanceOf[TemplateArgSymbol] =>
                a.getQualifiedName(symbol).toString
        }
    }

}
