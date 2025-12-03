package fpp.compiler.analysis

import scala.annotation.tailrec

import fpp.compiler.ast._
import fpp.compiler.util._

/** Enter symbols from new template expansions (as well as the template expansion itself) */
object EnterTemplateSymbols
  extends Analyzer
  with ModuleAnalyzer
{
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
          a <- {
            val scope = Scope.empty
            Result.foldLeft(params) (a.copy(nestedScope = a.nestedScope.push(scope))) ((a, param) => {
              for {
                nestedScope <- a.nestedScope.put(NameGroup.Value)(param.getUnqualifiedName, param)
                nestedScope <- nestedScope.put(NameGroup.Type)(param.getUnqualifiedName, param)
                nestedScope <- nestedScope.put(NameGroup.PortInterfaceInstance)(param.getUnqualifiedName, param)
              } yield a.copy(nestedScope = nestedScope)
            })
          }

          // We should be entering symbols into the parent scope
          a_scope <- {
            val scope = a.nestedScope.innerScope
            Right((a.copy( nestedScope = a.nestedScope.pop ), scope))
          }

          a <- {
            val (a, scope) = a_scope
            Right(a.copy(
              templateExpansionMap = a.templateExpansionMap + (node.id -> TemplateExpansion(
                tmpl.node,
                aNode,
                Map.from(params.map(p => (p.getUnqualifiedName, p))),
                scope
              )),
            ))
          }

          // Enter the child symbols in to the analysis
          a <- EnterSymbols.visitList(a, List(Ast.TransUnit(members)), EnterSymbols.transUnit)
        } yield a
      }
    }
  }
}
