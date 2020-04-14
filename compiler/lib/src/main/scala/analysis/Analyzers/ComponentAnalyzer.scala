package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze component members */
trait ComponentAnalyzer extends Analyzer {

  override def defComponentAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefComponent(kind, name, members) = node1.getData
    val a1 = a.copy(moduleNameList = name :: a.moduleNameList)
    for { a2 <- visitList(a1, members, matchComponentMember) }
    yield a2.copy(moduleNameList = a.moduleNameList)
  }

}
