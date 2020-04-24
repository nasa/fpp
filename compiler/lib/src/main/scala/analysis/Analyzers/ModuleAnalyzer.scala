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
    for { 
      a_scope <- a1.symbolScopeMap.get(symbol) match {
        case Some(scope) => Right((a1, scope))
        case None => {
          val scope = Scope.empty
          for (nestedScope <- a1.nestedScope.put(NameGroup.Value)(name, symbol))
            yield {
              val a = a1.copy(
                nestedScope = nestedScope,
                symbolScopeMap = a1.symbolScopeMap + (symbol -> scope)
              )
              (a, scope)
            }
        }
      }
      a <- Right(a_scope._1)
      scope <- Right(a_scope._2)
      a <- {
        val a1 = a.copy(nestedScope = a.nestedScope.push(scope))
        visitList(a1, members, matchModuleMember)
      }
    }
    yield a.copy(
      moduleNameList = oldModuleNameList,
      nestedScope = a.nestedScope.pop
    )
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

}
