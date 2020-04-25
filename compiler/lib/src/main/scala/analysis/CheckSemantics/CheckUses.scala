package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Match uses to their definitions */
object CheckUses extends UseAnalyzer {

  override def defModuleAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefModule(name, members) = node1.getData
    val oldModuleNameList = a.moduleNameList
    val newModuleNameList = name :: oldModuleNameList
    val qualifiedName = Name.Qualified.fromIdentList(newModuleNameList.reverse)
    val symbol = Symbol.Module(qualifiedName)
    val scope = a.symbolScopeMap.get(symbol) match {
      case Some(scope) => scope
      case None => throw InternalError(s"could not find symbol ${symbol} in symbol-scope map")
    }
    val newNestedScope = a.nestedScope.push(scope)
    val a1 = a.copy(moduleNameList = newModuleNameList, nestedScope = newNestedScope)
    for (a <- visitList(a1, members, matchModuleMember))
    yield a.copy(
      moduleNameList = oldModuleNameList,
      nestedScope = a.nestedScope.pop
    )
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

}
