package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Enter module symbols into their scopes */
abstract class EnterModuleSymbols
  extends Analyzer
  with ModuleAnalyzer
{

  override def defModuleAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val Ast.DefModule(name, members) = node.data
    val oldScopeNameList = a.scopeNameList
    val newScopeNameList = name :: oldScopeNameList
    val parentSymbol = a.parentSymbol
    val a1 = a.copy(scopeNameList = newScopeNameList)
    for {
      triple <- a1.nestedScope.innerScope.get (NameGroup.Value) (name) match {
        case Some(symbol: Symbol.Module) =>
          // We found a module symbol with the same name at the current level.
          // Re-open the scope.
          val scope = a1.symbolScopeMap(symbol)
          Right((a1, symbol, scope))
        case Some(symbol) =>
          // We found a non-module symbol with the same name at the current level.
          // This is an error.
          val error = SemanticError.RedefinedSymbol(
            name,
            Locations.get(node.id),
            symbol.getLoc
          )
          Left(error)
        case None =>
          // We did not find a symbol with the same name at the current level.
          // Create a new module symbol now.
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
        val (a2, symbol, scope) = triple
        val a3 = a2.copy(
          nestedScope = a2.nestedScope.push(scope),
          parentSymbol = Some(symbol)
        )
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
        parentSymbol = parentSymbol,
        symbolScopeMap = newSymbolScopeMap
      )
      updateMap(a1, symbol)
    }
  }

  protected def updateMap(a: Analysis, s: Symbol): Analysis = {
    val parentSymbolMap = a.parentSymbol.fold (a.parentSymbolMap) (ps =>
      a.parentSymbolMap + (s -> ps)
    )
    a.copy(parentSymbolMap = parentSymbolMap)
  }

}
