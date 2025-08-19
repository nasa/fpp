package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.ast.Ast.Expr
import fpp.compiler.ast.Ast.ExprIdent

/** Match uses to their definitions */
object CheckUses extends UseAnalyzer {

  val helpers = CheckUsesHelpers(
    (a: Analysis) => a.nestedScope,
    (a: Analysis, ns: NestedScope) => a.copy(nestedScope = ns),
    (a: Analysis) => a.symbolScopeMap,
    (a: Analysis) => a.useDefMap,
    (a: Analysis, udm: Map[AstNode.Id, Symbol]) => a.copy(useDefMap = udm)
  )

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.ComponentInstance) (a, node)

  override def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.Component) (a, node)

  override def stateMachineUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.StateMachine) (a, node)

  override def exprIdentNode(a: Analysis, node: AstNode[Expr], e: ExprIdent): Out = {
    val mapping = a.nestedScope.get (NameGroup.Value) _
    for (symbol <- helpers.getSymbolForName(mapping)(node.id, e.value)) yield {
      val useDefMap = a.useDefMap + (node.id -> symbol)
      a.copy(useDefMap = useDefMap)
    }
  }

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot): Out = {
    for {
      // Visit the left side of the dot recursively
      a <- exprNode(a, e.e)

      // Find the symbol referred to by the left side (if-any)
      symbol <- {
        a.useDefMap.get(e.e.id) match {
          // Left side is a constant, we are selecting a member of this constant
          // we are not creating another use.
          case Some(Symbol.Constant(_)) => Right(None)

          // The left side is some symbol other than a constant, create a new use-def entry
          case Some(qual) =>
            a.symbolScopeMap.get(qual) match {
              case Some(scope) =>
                val mapping = scope.get (NameGroup.Value) _
                helpers.getSymbolForName(mapping)(e.id.id, e.id.data) match {
                  case Right(value) => Right(Some(value))
                  case Left(err) => Left(err)
                }
              case None => Left(SemanticError.InvalidSymbol(
                qual.getUnqualifiedName,
                Locations.get(node.id),
                "not a qualifier",
                qual.getLoc
              ))
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
        helpers.getSymbolForName(mapping)(node.id, name)
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
      _ <- Result.foldLeft (impliedTypeUses) (()) ((_, itu) => {
        for {
          _ <- Result.annotateResult(
            typeUse(a, itu.asTypeNameNode, itu.name),
            s"when constructing a dictionary, the type ${itu.name} must be defined"
          )
        } yield ()
      })
      _ <- Result.foldLeft (impliedConstantUses) (()) ((_, itu) => {
        for {
          _ <- {
            Result.annotateResult(
              constantUse(a, itu.asExprNode, itu.name),
              s"when constructing a dictionary, the constant ${itu.name} must be defined"
            )
          }
        } yield ()
      })
      a <- super.defTopologyAnnotatedNode(a, node)
    } yield a
  }

  override def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.Port) (a, node)

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.Topology) (a, node)

  override def interfaceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.Interface) (a, node)

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
