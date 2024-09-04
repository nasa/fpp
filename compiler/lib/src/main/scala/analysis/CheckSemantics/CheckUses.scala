package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Match uses to their definitions */
object CheckUses extends UseAnalyzer {

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    visitQualIdentNode (NameGroup.ComponentInstance) (a, node)

  override def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    visitQualIdentNode (NameGroup.Component) (a, node)

  override def stateMachineUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    visitQualIdentNode (NameGroup.StateMachine) (a, node)

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    def visitExprNode(a: Analysis, node: AstNode[Ast.Expr]): Result = {
      def visitExprIdent(a: Analysis, node: AstNode[Ast.Expr], name: Name.Unqualified) = {
        val mapping = a.nestedScope.get (NameGroup.Value) _
        for (symbol <- getSymbolForName(mapping)(node, name)) yield {
          val useDefMap = a.useDefMap + (node.id -> symbol)
          a.copy(useDefMap = useDefMap)
        }
      }
      def visitExprDot(a: Analysis, node: AstNode[Ast.Expr], e: AstNode[Ast.Expr], id: AstNode[String]) = {
        for {
          a <- visitExprNode(a, e)
          scope <- {
            val symbol = a.useDefMap(e.id)
            a.symbolScopeMap.get(symbol) match {
              case Some(scope) => Right(scope)
              case None => Left(SemanticError.InvalidSymbol(
                symbol.getUnqualifiedName,
                Locations.get(node.id),
                "not a qualifier",
                symbol.getLoc
              ))
            }
          }
          symbol <- {
            val mapping = scope.get (NameGroup.Value) _
            getSymbolForName(mapping)(id, id.data)
          }
        } yield {
          val useDefMap = a.useDefMap + (node.id -> symbol)
          a.copy(useDefMap = useDefMap)
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
        getSymbolForName(mapping)(node, name)
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

  override def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    visitQualIdentNode (NameGroup.Port) (a, node)

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    visitQualIdentNode (NameGroup.Topology) (a, node)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    val data = node.data
    data match {
      case Ast.TypeNameQualIdent(qualIdentNode) => for {
        a <- visitQualIdentNode (NameGroup.Type) (a, qualIdentNode)
      } yield {
        val symbol = a.useDefMap(qualIdentNode.id)
        a.copy(useDefMap = a.useDefMap + (node.id -> symbol))
      }
      case _ => throw InternalError("type use should be qualified identifier")
    }
  }

  private def getSymbolForName[T] 
    (mapping: Name.Unqualified => Option[Symbol]) 
    (node: AstNode[T], name: Name.Unqualified): Result.Result[Symbol] =
    mapping(name) match {
      case Some(symbol) => Right(symbol)
      case None =>
        val loc = Locations.get(node.id)
        Left(SemanticError.UndefinedSymbol(name, loc))
    }

  private def visitQualIdentNode (ng: NameGroup) (a: Analysis, node: AstNode[Ast.QualIdent]): Result = {
    def visitUnqualified(a: Analysis, node: AstNode[Ast.QualIdent], name: Name.Unqualified) = {
      val mapping = a.nestedScope.get (ng) _
      for (symbol <- getSymbolForName(mapping)(node, name)) yield {
        val useDefMap = a.useDefMap + (node.id -> symbol)
        a.copy(useDefMap = useDefMap)
      }
    }
    def visitQualified(
      a: Analysis,
      node: AstNode[Ast.QualIdent],
      qualifier: AstNode[Ast.QualIdent],
      name: AstNode[Ast.Ident]
    ) = {
      for {
        a <- visitQualIdentNode (ng) (a, qualifier)
        scope <- {
          val symbol = a.useDefMap(qualifier.id)
          a.symbolScopeMap.get(symbol) match {
            case Some(symbol) => Right(symbol)
            case None => Left(
              SemanticError.InvalidSymbol(
                symbol.getUnqualifiedName,
                Locations.get(node.id),
                "not a qualifier",
                symbol.getLoc
              )
            )
          }
        }
        symbol <- {
          val mapping = scope.get (ng) _
          getSymbolForName(mapping)(name, name.data)
        }
      } yield {
        val useDefMap = a.useDefMap + (node.id -> symbol)
        a.copy(useDefMap = useDefMap)
      }
    }
    val data = node.data
    data match {
      case Ast.QualIdent.Unqualified(name) => visitUnqualified(a, node, name)
      case Ast.QualIdent.Qualified(qualifier, name) => visitQualified(a, node, qualifier, name)
    }
  }

}
