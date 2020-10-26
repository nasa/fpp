package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Enter symbols into their scopes */
object EnterSymbols 
  extends Analyzer 
  with EnumAnalyzer
  with ComponentAnalyzer
  with ModuleAnalyzer
  with TopologyAnalyzer
{

  override def defAbsTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.AbsType(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.Array(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defComponentAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.Component(aNode)
    for {
      nestedScope <- a.nestedScope.put(NameGroup.Component)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol)
      a <- {
        val scope = Scope.empty
        val nestedScope1 = nestedScope.push(scope)
        val a1 = a.copy(nestedScope = nestedScope1)
        super.defComponentAnnotatedNode(a1, aNode)
      }
    }
    yield {
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      val a1 = a.copy(
        nestedScope = a.nestedScope.pop,
        symbolScopeMap = newSymbolScopeMap
      )
      updateMap(a1, symbol)
    }
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.Constant(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.Enum(aNode)
    for {
      nestedScope <- a.nestedScope.put(NameGroup.Type)(name, symbol)
      nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol)
      a <- {
        val scope = Scope.empty
        val nestedScope1 = nestedScope.push(scope)
        val a1 = a.copy(nestedScope = nestedScope1)
        super.defEnumAnnotatedNode(a1, aNode)
      }
    }
    yield {
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      val a1 = a.copy(
        nestedScope = a.nestedScope.pop,
        symbolScopeMap = newSymbolScopeMap
      )
      updateMap(a1, symbol)
    }
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.EnumConstant(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Value)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defModuleAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val Ast.DefModule(name, members) = node.getData
    val oldScopeNameList = a.scopeNameList
    val newScopeNameList = name :: oldScopeNameList
    val a1 = a.copy(scopeNameList = newScopeNameList)
    for {
      triple <- a1.nestedScope.get (NameGroup.Value) (name) match {
        case Some(symbol: Symbol.Module) => 
          val scope = a1.symbolScopeMap(symbol)
          Right((a1, symbol, scope))
        case Some(symbol) => 
          val error = SemanticError.RedefinedSymbol(
            name,
            Locations.get(node.getId),
            symbol.getLoc
          )
          Left(error)
        case None => 
          val symbol = Symbol.Module(aNode)
          val scope = Scope.empty
          for {
            nestedScope <- Result.foldLeft (NameGroup.groups) (a1.nestedScope) (
              (ns, ng) => ns.put (ng) (name, symbol)
            )
          }
          yield {
            val a = a1.copy(nestedScope = nestedScope)
            (a, symbol, scope)
          }
      }
      a <- {
        val (a2, _, scope) = triple
        val a3 = a2.copy(nestedScope = a2.nestedScope.push(scope))
        visitList(a3, members, matchModuleMember)
      }
    }
    yield {
      val symbol = triple._2
      val scope = a.nestedScope.innerScope
      val newSymbolScopeMap = a.symbolScopeMap + (symbol -> scope)
      val a1 = a.copy(
        scopeNameList = oldScopeNameList,
        nestedScope = a.nestedScope.pop,
        symbolScopeMap = newSymbolScopeMap
      )
      updateMap(a1, symbol)
    }
  }

  override def defPortAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.Port(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Port)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val name = data.name
    val symbol = Symbol.Struct(aNode)
    val nestedScope = a.nestedScope
    for (nestedScope <- nestedScope.put(NameGroup.Type)(name, symbol))
      yield updateMap(a, symbol).copy(nestedScope = nestedScope)
  }

  private def updateMap(a: Analysis, s: Symbol): Analysis = {
    val identList = (s.getUnqualifiedName :: a.scopeNameList).reverse
    val name = Name.Qualified.fromIdentList(identList)
    a.copy(qualifiedNameMap = a.qualifiedNameMap + (s -> name))
  }

}
