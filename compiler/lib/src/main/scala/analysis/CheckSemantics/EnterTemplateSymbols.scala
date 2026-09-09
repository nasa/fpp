package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/**
 * Enter symbols from new template expansions
 * (as well as the template expansion itself)
 */
object EnterTemplateSymbols
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

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data

    (data.members, a.templateExpansionMap.get(node.id)) match {
      case (None, _) => {
        // This template has not been expanded yet, can't do much
        Right(a)
      }
      case (Some(members), Some(expansion)) => {
        // We already entered this expansion
        // Make sure we recursively enter all the symbols
        // We still need to update the scope on re-expand
        for {
          a <- Right(a.copy(nestedScope = a.nestedScope.push(expansion.scope)))
          a <- this.visitList(a, members, this.matchModuleMember)
        } yield {
          val templateScope = a.nestedScope.innerScope
          val nestedScope = a.nestedScope.pop

          a.copy(
            nestedScope = nestedScope,
            templateExpansionMap = a.templateExpansionMap + (
              node.id -> expansion.copy(scope = templateScope)
            )
          )
        }
      }
      case (Some(members), None) => {
        val tmpl = a.getTemplateSymbol(data.template.id) match {
          case Right(tmpl) => tmpl
          case Left(error) => throw InternalError(
            s"template symbol should already be resolved: $error"
          )
        }
        val defParams = tmpl.node._2.data.params

        // Build the parameter set of the expansion
        for {
          // Make sure the lengths line up
          _ <- {
            if data.args.length == defParams.length
            then Right(())
            else Left(SemanticError.WrongNumberOfTemplateParameters(
              Locations.get(node.id),
              Locations.get(tmpl.node._2.id),
              data.args.length,
              defParams.length
            ))
          }

          // Create the parameter symbols by mapping each argument together with the definition
          // Make sure each argument is of the proper type
          params <- {
            Result.map[
              (AstNode[Ast.TemplateArg], Ast.Annotated[AstNode[Ast.TemplateParam]]),
              TemplateArgSymbol
            ](data.args zip defParams, (valueParam, defParam) => {
              (defParam._2.data, valueParam.data) match {
                case (defParam: Ast.TemplateParam.Constant, Ast.TemplateArg.Constant(value)) =>
                  Right(Symbol.TemplateConstantArg(defParam, value))
                case (defParam: Ast.TemplateParam.Type, Ast.TemplateArg.Type(typeName)) =>
                  Right(Symbol.TemplateTypeArg(defParam, typeName))
                case (defParam: Ast.TemplateParam.Interface, Ast.TemplateArg.Interface(instance)) =>
                  Right(Symbol.TemplateInterfaceArg(defParam, instance))
                case (dp, vp) => {
                  val (name, defKind) = dp match {
                    case Ast.TemplateParam.Constant(name, _) => (name, "constant")
                    case Ast.TemplateParam.Type(name) => (name, "type")
                    case Ast.TemplateParam.Interface(name, _) => (name, "instance")
                  }

                  val valKind = vp match {
                    case Ast.TemplateArg.Constant(_) => "constant"
                    case Ast.TemplateArg.Type(_) => "type"
                    case Ast.TemplateArg.Interface(_) => "instance"
                  }

                  Left(SemanticError.InvalidTemplateArg(
                    name,
                    Locations.get(valueParam.id),
                    Locations.get(defParam._2.id),
                    s"expected a ${defKind} argument, got a ${valKind} argument"
                  ))
                }
              }
            })
          }

          // Enter the template parameters into a new scope
          nestedScope <- {
            val scope = Scope.empty
            Result.foldLeft(params) (a.nestedScope.push(scope)) ((nestedScope, param) =>
              nestedScope.put (TemplateExpansion.nameGroup(param)) (
                param.getUnqualifiedName,
                param
              )
            )
          }

          paramScope <- Right(nestedScope.innerScope)
          a <- Right(a.copy(nestedScope = nestedScope.pop))

          // Create an empty scope for entering template expanded symbols
          a <- {
            val scope = Scope.empty
            Right(a.copy(nestedScope = a.nestedScope.push(scope)))
          }

          // Enter the child symbols in to the analysis
          a <- EnterSymbols.visitList(a, List(Ast.TransUnit(members)), EnterSymbols.transUnit)

          templateScope <- Right(a.nestedScope.innerScope)

          // The parameter scope and the expansion scope are visible at the same
          // time, so no parameter may have the same name as a definition in the
          // expansion in the same name group
          _ <- Result.foldLeft (params zip defParams) (()) ((_, pair) => {
            val (param, defParam) = pair
            val name = param.getUnqualifiedName
            templateScope.get (TemplateExpansion.nameGroup(param)) (name) match {
              case Some(defSymbol) => Left(SemanticError.TemplateParameterConflict(
                name,
                Locations.get(defParam._2.id),
                defSymbol.getLoc
              ))
              case None => Right(())
            }
          })

          // Duplicate the symbols entries into the outer scope
          a <- {
            val nestedScope = a.nestedScope.pop

            for {
              pair <- mergeScope(a, nestedScope.innerScope, templateScope)
            } yield {
              val (a1, mergedScope) = pair
              a1.copy(
                nestedScope = nestedScope.pop.push(mergedScope),
                templateExpansionMap = a1.templateExpansionMap + (node.id -> TemplateExpansion(
                  tmpl.node,
                  aNode,
                  Map.from(params.map(p => (TemplateExpansion.paramKey(p), p))),
                  paramScope,
                  templateScope,
                ))
              )
            }
          }

          a <- this.visitList(a, members, this.matchModuleMember)
        } yield a
      }
    }
  }

  private def mergeScope(a: Analysis, dest: Scope, src: Scope):
    Result.Result[(Analysis, Scope)] =
    Result.foldLeft (src.map.toList) ((a, dest)) ((pair, ngs) => {
      val (ng, symbols) = ngs
      Result.foldLeft (symbols.map.toList) (pair) ((pair, symEntry) => {
        val (a, dest) = pair
        val (name, symbol) = symEntry
        (dest.get (ng) (name), symbol) match {
          // The name is already bound to this symbol, e.g., because we
          // merged it when visiting another name group
          case (Some(prevSymbol), _) if prevSymbol == symbol => Right((a, dest))
          // Two modules with the same name: merge their scopes and keep
          // the binding that is already there
          case (Some(prevSymbol: Symbol.Module), symbol: Symbol.Module) =>
            for (a <- mergeModuleScopes(a, prevSymbol, symbol)) yield (a, dest)
          case _ => for (dest <- dest.put (ng) (name, symbol)) yield (a, dest)
        }
      })
    })

  /** Merge the scopes of two module symbols denoting the same module */
  private def mergeModuleScopes(
    a: Analysis,
    prevSymbol: Symbol.Module,
    symbol: Symbol.Module
  ): Result.Result[Analysis] = {
    val prevScope = a.symbolScopeMap.getOrElse(prevSymbol, Scope.empty)
    val scope = a.symbolScopeMap.getOrElse(symbol, Scope.empty)
    if prevScope == scope then Right(a)
    else for (pair <- mergeScope(a, prevScope, scope))
      yield {
        val (a1, mergedScope) = pair
        // Both symbols denote the same module, so both must see all its members
        a1.copy(symbolScopeMap =
          a1.symbolScopeMap + (prevSymbol -> mergedScope) + (symbol -> mergedScope)
        )
      }
  }

  private def updateMap(a: Analysis, s: Symbol): Analysis = {
    val parentSymbolMap = a.parentSymbol.fold (a.parentSymbolMap) (ps =>
      a.parentSymbolMap + (s -> ps)
    )
    a.copy(parentSymbolMap = parentSymbolMap)
  }
}
