package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Match uses to their definitions */
object CheckUses extends UseAnalyzer {

  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- opt(typeNameNode)(a, data.typeName)
      a <- {
        val symbol = Symbol.Enum(node1)
        val scope = getScopeForSymbol(a, symbol)
        val newNestedScope = a.nestedScope.push(scope)
        val a1 = a.copy(nestedScope = newNestedScope)
        visitList(a1, data.constants, defEnumConstantAnnotatedNode)
      }
    } yield a.copy(nestedScope = a.nestedScope.pop)
  }

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
    val scope = getScopeForSymbol(a, symbol)
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

  private def getScopeForSymbol(a: Analysis, symbol: Symbol): Scope =
    a.symbolScopeMap.get(symbol) match {
      case Some(scope) => scope
      case None => throw InternalError(s"could not find scope for symbol ${symbol}")
    }

}
