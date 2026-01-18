package fpp.compiler.analysis

import scala.annotation.tailrec

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

    def exprToIdentList(expr: AstNode[Ast.Expr]): Result.Result[List[AstNode[Ast.Ident]]] = {
        expr.data match {
          case Ast.ExprIdent(e) => Right(List(AstNode.create(e, expr.id)))
          case Ast.ExprDot(e, id) => for (left <- exprToIdentList(e)) yield left :+ id
          // TODO(tumbar) Make error messages specific to the parameter type
          case _ => Left(SemanticError.InvalidTemplateParameter(
                "<unknown>",
                Locations.get(expr.id),
                Locations.get(expr.id),
                "expression cannot be converted to qual ident"
              ))
        }
    }

    (data.members, a.templateExpansionMap.contains(node.id)) match {
      case (None, _) => {
        // This template has not been expanded yet, can't do much
        Right(a)
      }
      case (Some(members), true) => {
        // We already entered this expansion
        // Make sure we recursively enter all the symbols
        this.visitList(a, members, this.matchModuleMember)
      }
      case (Some(members), false) => {
        // We have not entered the symbols in this expansion
        val Right(tmpl) = a.getTemplateSymbol(data.template.id)
        val defParams = tmpl.node._2.data.params

        // Build the parameter set of the expansion
        for {
          // Make sure the lengths line up
          _ <- {
            if data.params.length == defParams.length
            then Right(())
            else Left(SemanticError.MismatchedTemplateParameters(
              Locations.get(node.id),
              Locations.get(tmpl.node._2.id),
              data.params.length,
              defParams.length
            ))
          }

          // Create the parameter symbols by mapping each argument together with the definition
          // Make sure each argument is of the proper type
          params <- {
            Result.map[
              (AstNode[Ast.TemplateParameter], Ast.Annotated[AstNode[Ast.DefTemplateParam.Node]]),
              Symbol.TemplateParam
            ](data.params zip defParams, (valueParam, defParam) => {
              (defParam._2.data, valueParam.data) match {
                case (defParam: Ast.DefTemplateParam.Constant, Ast.TemplateConstantParameter(value)) =>
                  Right(Symbol.TemplateConstantParam(defParam, value))
                case (defParam: Ast.DefTemplateParam.Type, Ast.TemplateTypeParameter(typeName)) =>
                  Right(Symbol.TemplateTypeParam(defParam, typeName))
                case (defParam: Ast.DefTemplateParam.Interface, Ast.TemplateInterfaceParameter(instance)) =>
                  Right(Symbol.TemplateInterfaceParam(defParam, instance))
                case (dp, vp) => {
                  val defKind = dp match {
                    case Ast.DefTemplateParam.Constant(_, _) => "constant"
                    case Ast.DefTemplateParam.Type(_) => "type"
                    case Ast.DefTemplateParam.Interface(_, _) => "interface"
                  }

                  val valKind = vp match {
                    case Ast.TemplateConstantParameter(_) => "constant"
                    case Ast.TemplateTypeParameter(_) => "type"
                    case Ast.TemplateInterfaceParameter(_) => "interface"
                  }

                  Left(SemanticError.InvalidTemplateParameter(
                    dp.name,
                    Locations.get(valueParam.id),
                    Locations.get(defParam._2.id),
                    s"expected ${defKind} template parameter, got ${valKind} parameter"
                  ))
                }
              }
            })
          }

          // Enter the template parameters into a new scope
          nestedScope <- {
            val scope = Scope.empty
            Result.foldLeft(params) (a.nestedScope.push(scope)) ((nestedScope, param) => {
              for {
                nestedScope <- nestedScope.put(NameGroup.Value)(param.getUnqualifiedName, param)
                nestedScope <- nestedScope.put(NameGroup.Type)(param.getUnqualifiedName, param)
                nestedScope <- nestedScope.put(NameGroup.PortInterfaceInstance)(param.getUnqualifiedName, param)
              } yield nestedScope
            })
          }

          scope <- Right(nestedScope.innerScope)
          a <- Right(a.copy(
            nestedScope = nestedScope.pop,
            templateExpansionMap = a.templateExpansionMap + (node.id -> TemplateExpansion(
              tmpl.node,
              aNode,
              Map.from(params.map(p => (p.getUnqualifiedName, p))),
              scope
            )),
          ))

          // Enter the child symbols in to the analysis
          a <- EnterSymbols.visitList(a, List(Ast.TransUnit(members)), EnterSymbols.transUnit)
        } yield a
      }
    }
  }

  private def updateMap(a: Analysis, s: Symbol): Analysis = {
    val parentSymbolMap = a.parentSymbol.fold (a.parentSymbolMap) (ps =>
      a.parentSymbolMap + (s -> ps)
    )
    a.copy(parentSymbolMap = parentSymbolMap)
  }
}
