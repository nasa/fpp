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
    val Ast.DefModule(name, members) = node1.data
    val a1 = a.copy(scopeNameList = name :: a.scopeNameList)
    for { a2 <- visitList(a1, members, matchModuleMember) }
    yield a2.copy(scopeNameList = a.scopeNameList)
  }

}
