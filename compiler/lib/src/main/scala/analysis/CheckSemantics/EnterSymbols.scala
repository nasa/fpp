package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Enter symbols into their scopes */
object EnterSymbols 
  extends Analyzer 
  with ComponentAnalyzer
  with ModuleAnalyzer
  with TopologyAnalyzer
{

  override def defAbsTypeAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.AbsType(node)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  override def defArrayAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Array(node)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  override def defConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Constant(node)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Enum(node)
    for {
      nestedScope <- a.nestedScope.put(NameGroup.Type)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol)
      a <- {
        val scope = Scope.empty
        val nestedScope1 = nestedScope.push(scope)
        val a1 = a.copy(nestedScope = nestedScope1)
        visitList(a1, data.constants, defEnumConstantAnnotatedNode)
      }
    }
    yield {
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      a.copy(
        nestedScope = a.nestedScope.pop,
        symbolScopeMap = newSymbolScopeMap
      )
    }
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
        val nestedScope = Result.expectRight(a1.nestedScope.put(NameGroup.Value)(name, symbol))
        val nestedScope1 = Result.expectRight(nestedScope.put(NameGroup.Type)(name, symbol))
        val a = a1.copy(nestedScope = nestedScope1)
        (a, scope)
      }
    }
    val a3 = a2.copy(nestedScope = a2.nestedScope.push(scope))
    for (a <- visitList(a3, members, matchModuleMember))
    yield {
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      a.copy(
        moduleNameList = oldModuleNameList,
        nestedScope = a.nestedScope.pop,
        symbolScopeMap = newSymbolScopeMap
      )
    }
  }

  override def defStructAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.Struct(node)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

  private def defEnumConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val name = data.name
    val symbol = Symbol.EnumConstant(node)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield a.copy(nestedScope = nestedScope)
  }

}
