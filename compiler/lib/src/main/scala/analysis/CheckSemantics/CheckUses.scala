package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.ast.Ast.Expr
import fpp.compiler.ast.Ast.ExprIdent

/** Match uses to their definitions */
object CheckUses extends BasicUseAnalyzer {

  val helpers = CheckUsesHelpers(
    (a: Analysis) => a.nestedScope,
    (a: Analysis, ns: NestedScope) => a.copy(nestedScope = ns),
    (a: Analysis) => a.symbolScopeMap,
    (a: Analysis) => a.useDefMap,
    (a: Analysis, udm: Map[AstNode.Id, Symbol]) => a.copy(useDefMap = udm)
  )

  override def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.Component) (a, node)

  override def stateMachineUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.StateMachine) (a, node)

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    def visitExprNode(a: Analysis, node: AstNode[Ast.Expr]): Result = {
      def visitExprIdent(a: Analysis, node: AstNode[Ast.Expr], name: Name.Unqualified) = {
        val mapping = a.nestedScope.get (NameGroup.Value) _
        for (symbol <- helpers.getSymbolForName(NameGroup.Value, mapping)(node.id, name)) yield {
          val useDefMap = a.useDefMap + (node.id -> symbol)
          a.copy(useDefMap = useDefMap)
        }
      }
      def visitExprDot(a: Analysis, node: AstNode[Ast.Expr], e: AstNode[Ast.Expr], id: AstNode[String]) = {
        for {
          // Visit the left side of the dot recursively
          a <- exprNode(a, e)

          // Find the symbol referred to by the left side (if-any)
          symbol <- {
            a.useDefMap.get(e.id) match {
              // Left side is a constant, we are selecting a member of this constant
              // we are not creating another use.
              case Some(Symbol.Constant(_)) => Right(None)

              // The left side is some symbol other than a constant (a qualifier),
              // look up this symbol and add it to the use-def entries
              case Some(qual) =>
                val scope = a.symbolScopeMap(qual)
                val mapping = scope.get (NameGroup.Value) _
                helpers.getSymbolForName(NameGroup.Value, mapping)(id.id, id.data) match {
                  case Right(value) => Right(Some(value))
                  case Left(err) => Left(err)
                }

              // Left-hand side is not a symbol
              // There is no resolution on the dot expression
              case None => Right(None)
            }
          }
        } yield {
          symbol match {
            case Some(sym) => {
              val useDefMap = a.useDefMap + (node.id -> sym)
              a.copy(useDefMap = useDefMap)
            }
            case None => a
          }
        }
      }
      val data = node.data
      data match {
        case Ast.ExprIdent(name) => visitExprIdent(a, node, name)
        case Ast.ExprDot(e, id) => visitExprDot(a, node, e, id)
        case _ => throw InternalError("constant use should be qualified identifier")
      }
    }
    visitExprNode(a, node)
  }

  override def defComponentAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefComponent]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- {
        val symbol = Symbol.Component(aNode)
        val scope = a.symbolScopeMap(symbol)
        val newNestedScope = a.nestedScope.push(scope)
        val a1 = a.copy(nestedScope = newNestedScope)
        super.defComponentAnnotatedNode(a1, aNode)
      }
    } yield a.copy(nestedScope = a.nestedScope.pop)
  }

  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.data
    for {
      a <- opt(typeNameNode)(a, data.typeName)
      a <- {
        val symbol = Symbol.Enum(node)
        val scope = a.symbolScopeMap(symbol)
        val newNestedScope = a.nestedScope.push(scope)
        val a1 = a.copy(nestedScope = newNestedScope)
        visitList(a1, data.constants, defEnumConstantAnnotatedNode)
      }
      a <- opt(exprNode)(a, data.default)
    } yield a.copy(nestedScope = a.nestedScope.pop)
  }

  override def defModuleAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val node = aNode._2
    val Ast.DefModule(name, members) = node.data
    for {
      symbol <- {
        val mapping = a.nestedScope.get (NameGroup.Value) _
        helpers.getSymbolForName(NameGroup.Value, mapping)(node.id, name)
      }
      a <- {
        val scope = a.symbolScopeMap(symbol)
        val newNestedScope = a.nestedScope.push(scope)
        val a1 = a.copy(nestedScope = newNestedScope)
        visitList(a1, members, matchModuleMember)
      }
    }
    yield a.copy(nestedScope = a.nestedScope.pop)
  }

  override def defTopologyAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefTopology]]) = {
    val impliedTypeUses = a.getImpliedUses(ImpliedUse.Kind.Type, node._2.id).toList
    val impliedConstantUses = a.getImpliedUses(ImpliedUse.Kind.Constant, node._2.id).toList
    for {
      _ <- Result.foldLeft (impliedConstantUses) (()) ((_, itu) => {
        val impliedUse = itu.asUniqueExprNode

        for {
          a <- {
            Result.annotateResult(
              constantUse(a, impliedUse, itu.name),
              s"when constructing a dictionary, the constant ${itu.name} must be defined"
            )
          }

          // Check to make sure this implied use is actually a constant
          // ...rather than a member of a constant.
          _ <- {
            a.useDefMap.get(impliedUse.id) match {
              case Some(Symbol.Constant(_) | Symbol.EnumConstant(_)) => Right(a)
              case Some(_) => throw new InternalError("not a constant use or member")
              case x =>
                // Get the parent symbol to make the error reporting better
                def getSymbolOfExpr(e: AstNode[Expr]): Symbol = {
                  (a.useDefMap.get(e.id), e.data) match {
                    case (Some(sym), _) => sym
                    case (None, Ast.ExprDot(ee, eid)) => getSymbolOfExpr(ee)
                    case _ => throw new InternalError("expected a constant use")
                  }
                }

                val sym = getSymbolOfExpr(impliedUse)
                Left(SemanticError.InvalidSymbol(
                  sym.getUnqualifiedName,
                  Locations.get(impliedUse.id),
                  s"${itu.name} must be a constant symbol",
                  sym.getLoc
                ))
            }
          }
        } yield ()
      })

      _ <- Result.foldLeft (impliedTypeUses) (()) ((_, itu) => {
        for {
          _ <- Result.annotateResult(
            typeUse(a, itu.asTypeNameNode, itu.name),
            s"when constructing a dictionary, the type ${itu.name} must be defined"
          )
        } yield ()
      })

      a <- super.defTopologyAnnotatedNode(a, node)
    } yield a
  }

  override def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.Port) (a, node)

  override def interfaceInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.PortInterfaceInstance) (a, node)

  override def interfaceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.PortInterface) (a, node)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    val data = node.data
    data match {
      case Ast.TypeNameQualIdent(qualIdentNode) => for {
        a <- helpers.visitQualIdentNode (NameGroup.Type) (a, qualIdentNode)
      } yield {
        val symbol = a.useDefMap(qualIdentNode.id)
        a.copy(useDefMap = a.useDefMap + (node.id -> symbol))
      }
      case _ => throw InternalError("type use should be qualified identifier")
    }
  }

}
