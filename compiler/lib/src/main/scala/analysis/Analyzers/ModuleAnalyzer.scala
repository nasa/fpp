package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Visit translation unit members and module members */
trait ModuleAnalyzer extends Analyzer {

  override def defModuleAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefModule(name, members) = node1.getData
    val a1 = a.copy(moduleNameList = name :: a.moduleNameList)
    for { a2 <- visitList(a1, members, matchModuleMember) }
    yield a2.copy(moduleNameList = a.moduleNameList)
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

}
