package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Match uses to their definitions */
object CheckUses extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    def visitExprNode(a: Analysis, node: AstNode[Ast.Expr]): Result = {
      def visitExprIdent(a: Analysis, node: AstNode[Ast.Expr], name: Name.Unqualified) = {
        val mapping = a.nestedScope.getOpt (NameGroup.Value) _
        for (symbol <- getSymbolForName(mapping)(node, name)) yield {
          val useDefMap = a.useDefMap + (node.getId -> symbol)
          a.copy(useDefMap = useDefMap)
        }
      }
      def visitExprDot(a: Analysis, node: AstNode[Ast.Expr], e: AstNode[Ast.Expr], id: AstNode[String]) = {
        for {
          a <- visitExprNode(a, e)
          symbol <- {
            val symbol = getDefForUse(a, e)
            val scope = getScopeForSymbol(a, symbol)
            val mapping = scope.getOpt (NameGroup.Value) _
            getSymbolForName(mapping)(id, id.getData)
          }
        } yield {
          val useDefMap = a.useDefMap + (node.getId -> symbol)
          a.copy(useDefMap = useDefMap)
        }
      }
      val data = node.getData
      data match {
        case Ast.ExprIdent(name) => visitExprIdent(a, node, name)
        case Ast.ExprDot(e, id) => visitExprDot(a, node, e, id)
        case _ => throw InternalError("constant use should be qualified identifier")
      }
    }
    visitExprNode(a, node)
  }

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    def visitQualIdentNode(a: Analysis, node: AstNode[Ast.QualIdent]): Result = {
      def visitUnqualified(a: Analysis, node: AstNode[Ast.QualIdent], name: Name.Unqualified) = {
        val mapping = a.nestedScope.getOpt (NameGroup.Type) _
        for (symbol <- getSymbolForName(mapping)(node, name)) yield {
          val useDefMap = a.useDefMap + (node.getId -> symbol)
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
          a <- visitQualIdentNode(a, qualifier)
          symbol <- {
            val symbol = getDefForUse(a, qualifier)
            val scope = getScopeForSymbol(a, symbol)
            val mapping = scope.getOpt (NameGroup.Type) _
            getSymbolForName(mapping)(name, name.getData)
          }
        } yield {
          val useDefMap = a.useDefMap + (node.getId -> symbol)
          a.copy(useDefMap = useDefMap)
        }
      }
      val data = node.getData
      data match {
        case Ast.QualIdent.Unqualified(name) => visitUnqualified(a, node, name)
        case Ast.QualIdent.Qualified(qualifier, name) => visitQualified(a, node, qualifier, name)
      }
    }
    val data = node.getData
    data match {
      case Ast.TypeNameQualIdent(qualIdentNode) => for {
        a <- visitQualIdentNode(a, qualIdentNode)
      } yield {
        val symbol = a.useDefMap(qualIdentNode.getId)
        a.copy(useDefMap = a.useDefMap + (node.getId -> symbol))
      }
      case _ => throw InternalError("type use should be qualified identifier")
    }
  }

  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- opt(typeNameNode)(a, data.typeName)
      a <- {
        val symbol = Symbol.Enum(node)
        val scope = getScopeForSymbol(a, symbol)
        val newNestedScope = a.nestedScope.push(scope)
        val a1 = a.copy(nestedScope = newNestedScope)
        visitList(a1, data.constants, defEnumConstantAnnotatedNode)
      }
    } yield a.copy(nestedScope = a.nestedScope.pop)
  }

  override def defModuleAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefModule(name, members) = node1.getData
    val oldModuleNameList = a.moduleNameList
    val newModuleNameList = name :: oldModuleNameList
    val qualifiedName = Name.Qualified.fromIdentList(newModuleNameList.reverse)
    val symbol = Symbol.Module(qualifiedName)
    val scope = getScopeForSymbol(a, symbol)
    val newNestedScope = a.nestedScope.push(scope)
    val a1 = a.copy(moduleNameList = newModuleNameList, nestedScope = newNestedScope)
    for (a <- visitList(a1, members, matchModuleMember))
    yield a.copy(
      moduleNameList = oldModuleNameList,
      nestedScope = a.nestedScope.pop
    )
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

  private def getDefForUse[T](a: Analysis, use: AstNode[T]): Symbol =
    a.useDefMap.get(use.getId) match {
      case Some(symbol) => symbol
      case None => throw InternalError(s"could not find definition for use ${use}")
    }

  private def getScopeForSymbol(a: Analysis, symbol: Symbol): Scope =
    a.symbolScopeMap.get(symbol) match {
      case Some(scope) => scope
      case None => throw InternalError(s"could not find scope for symbol ${symbol}")
    }

  private def getSymbolForName[T] 
    (mapping: Name.Unqualified => Option[Symbol]) 
    (node: AstNode[T], name: Name.Unqualified): Result.Result[Symbol] =
    mapping(name) match {
      case Some(symbol) => Right(symbol)
      case None => {
        val loc = Locations.get(node.getId)
        Left(SemanticError.UndefinedSymbol(name, loc))
      }
    }

}
