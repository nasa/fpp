package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Enter symbols into their scopes */
object EnterSymbols extends ModuleAnalyzer {

  override def defAbsTypeAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.AbsType(node1)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  override def defArrayAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Array(node1)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  override def defConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Constant(node1)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Enum(node1)
    for {
      nestedScope <- a.nestedScope.put(NameGroup.Type)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol)
      a <- {
        val scope = Scope.empty
        val nestedScope1 = nestedScope.push(scope)
        val symbolScopeMap = a.symbolScopeMap + (symbol -> scope)
        val a1 = a.copy(nestedScope = nestedScope1, symbolScopeMap = symbolScopeMap)
        visitList(a1, data.constants, defEnumConstantAnnotatedNode)
      }
    }
    yield a.copy(nestedScope = a.nestedScope.pop)
  }

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

  override def defStructAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Struct(node1)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  private def defEnumConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.EnumConstant(node1)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

}
