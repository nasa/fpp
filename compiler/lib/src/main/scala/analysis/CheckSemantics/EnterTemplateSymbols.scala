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

          // The syntax allows parameters to be any expression
          // This is to allow for constant expressions
          // Type and interface instance parameters can only actually take qualified identifiers
          // Make sure that these bounds are met
          params <- {
            Result.map[
              (AstNode[Ast.Expr], Ast.Annotated[AstNode[Ast.TemplateParam]]),
              Symbol.TemplateParam
            ](data.params zip defParams, (valueParam, defParam) => {
              defParam._2.data match {
                // Constants support all expressions
                case defParam: Ast.TemplateParam.Constant => Right(Symbol.TemplateConstantParam(
                  defParam,
                  valueParam
                ))
                case defParam: Ast.TemplateParam.Type =>
                  // TODO(tumbar) Create a ExprOrTypeName -> TypeName conversion
                  for (identList <- exprToIdentList(valueParam))
                  yield Symbol.TemplateTypeParam(
                    defParam,
                    AstNode.create(
                      Ast.TypeNameQualIdent(
                        AstNode.create(
                          Ast.QualIdent.fromNodeList(identList),
                          valueParam.id
                        ),
                      ),
                      valueParam.id
                    )
                  )
                case defParam: Ast.TemplateParam.Interface =>
                  for (identList <- exprToIdentList(valueParam))
                  yield Symbol.TemplateInterfaceParam(
                    defParam,
                    AstNode.create(
                      Ast.QualIdent.fromNodeList(identList),
                      valueParam.id
                    )
                  )
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
