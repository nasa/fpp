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
    val oldModuleNameList = a.moduleNameList
    val newModuleNameList = name :: oldModuleNameList
    val a1 = a.copy(moduleNameList = newModuleNameList)
    val qualifiedName = Name.Qualified.fromIdentList(newModuleNameList.reverse)
    val symbol = Symbol.Module(qualifiedName)
    val (a2, scope) = a1.symbolScopeMap.get(symbol) match {
      case Some(scope) => (a1, scope)
      case None => {
        val scope = Scope.empty
        val result = a1.nestedScope.put(NameGroup.Value)(name, symbol)
        val nestedScope = Result.expectRight(result)
        val a = a1.copy(
          nestedScope = nestedScope,
          symbolScopeMap = a1.symbolScopeMap + (symbol -> scope)
        )
        (a, scope)
      }
    }
    val a3 = a2.copy(nestedScope = a2.nestedScope.push(scope))
    for (a <- visitList(a3, members, matchModuleMember))
    yield a.copy(
      moduleNameList = oldModuleNameList,
      nestedScope = a.nestedScope.pop
    )
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

}
