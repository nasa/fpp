package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Generic helper functions for check uses
 *  These are also used by the state machine analysis */
case class CheckUsesHelpers[A,NG,S <: SymbolInterface](
  getNestedScope: A => GenericNestedScope[NG, S],
  setNestedScope: (A, GenericNestedScope[NG, S]) => A,
  getSymbolScopeMap: A => Map[S, GenericScope[NG, S]],
  getUseDefMap: A => Map[AstNode.Id, S],
  setUseDefMap: (A, Map[AstNode.Id, S]) => A
) {

  /** Get the symbol for a name from the environment */
  def getSymbolForName
    (mapping: Name.Unqualified => Option[S])
    (id: AstNode.Id, name: Name.Unqualified): Result.Result[S] =
    mapping(name).map(Right(_)).getOrElse({
      val loc = Locations.get(id)
      Left(SemanticError.UndefinedSymbol(name, loc))
    })

  /** Visit an identifier node and check a use */
  def visitIdentNode (ng: NG) (
    a: A,
    node: AstNode[Ast.Ident]
  ) = visitUnqualifiedName (ng) (a, node.id, node.data)

  /** Visit a qualified identifier node and check a use */
  def visitQualIdentNode (ng: NG) (
    a: A,
    node: AstNode[Ast.QualIdent]
  ): Result.Result[A] =
    node.data match {
      case Ast.QualIdent.Unqualified(name) =>
        visitUnqualifiedName (ng) (a, node.id, name)
      case Ast.QualIdent.Qualified(qualifier, name) =>
        visitQualifiedName (ng) (a, node.id, qualifier, name)
    }

  private def visitUnqualifiedName (ng: NG) (
    a: A,
    id: AstNode.Id,
    name: Ast.Ident
  ) = {
    val mapping = getNestedScope(a).get (ng) _
    for (symbol <- getSymbolForName(mapping)(id, name)) yield {
      val useDefMap = getUseDefMap(a) + (id -> symbol)
      setUseDefMap(a, useDefMap)
    }
  }

  private def visitQualifiedName (ng: NG) (
    a: A,
    id: AstNode.Id,
    qualifier: AstNode[Ast.QualIdent],
    name: AstNode[Ast.Ident]
  ) =
    for {
      a <- visitQualIdentNode (ng) (a, qualifier)
      scope <- {
        val symbol = getUseDefMap(a)(qualifier.id)
        getSymbolScopeMap(a).get(symbol).map(Right(_)).getOrElse(
          Left(
            SemanticError.InvalidSymbol(
              symbol.getUnqualifiedName,
              Locations.get(id),
              "not a qualifier",
              symbol.getLoc
            )
          )
        )
      }
      symbol <- {
        val mapping = scope.get (ng) _
        getSymbolForName(mapping)(name.id, name.data)
      }
    }
    yield {
      val useDefMap = getUseDefMap(a) + (id -> symbol)
      setUseDefMap(a, useDefMap)
    }

}
