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
