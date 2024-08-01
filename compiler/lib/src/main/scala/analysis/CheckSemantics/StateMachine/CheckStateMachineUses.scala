package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Match uses to their definitions */
object CheckStateMachineUses extends StateMachineUseAnalyzer {

  override def actionUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = visitIdentNode (StateMachineNameGroup.Action) (sma, node)

  override def guardUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = visitIdentNode (StateMachineNameGroup.Guard) (sma, node)

  override def signalUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = visitIdentNode (StateMachineNameGroup.Signal) (sma, node)

  override def stateOrJunctionUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ): Result = visitQualIdentNode (StateMachineNameGroup.State) (sma, node)

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      sma <- {
        val symbol = StateMachineSymbol.State(aNode)
        val scope = sma.symbolScopeMap(symbol)
        val newNestedScope = sma.nestedScope.push(scope)
        val sma1 = sma.copy(nestedScope = newNestedScope)
        super.defStateAnnotatedNode(sma1, aNode)
      }
    } yield sma.copy(nestedScope = sma.nestedScope.pop)
  }

  private def visitIdentNode (ng: StateMachineNameGroup) (
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident]
  ) = visitUnqualifiedName (ng) (sma, node.id, node.data)

  private def visitUnqualifiedName (ng: StateMachineNameGroup) (
    sma: StateMachineAnalysis,
    id: AstNode.Id,
    name: Ast.Ident
  ) = {
    val mapping = sma.nestedScope.get (ng) _
    for (symbol <- getSymbolForName(mapping)(id, name)) yield {
      val useDefMap = sma.useDefMap + (id -> symbol)
      sma.copy(useDefMap = useDefMap)
    }
  }

  private def getSymbolForName
    (mapping: Name.Unqualified => Option[StateMachineSymbol]) 
    (id: AstNode.Id, name: Name.Unqualified): Result.Result[StateMachineSymbol] =
    mapping(name).map(Right(_)).getOrElse({
      val loc = Locations.get(id)
      Left(SemanticError.UndefinedSymbol(name, loc))
    })

  private def visitQualIdentNode (ng: StateMachineNameGroup) (
    sma: StateMachineAnalysis,
    node: AstNode[Ast.QualIdent]
  ): Result =
    node.data match {
      case Ast.QualIdent.Unqualified(name) =>
        visitUnqualifiedName (ng) (sma, node.id, name)
      case Ast.QualIdent.Qualified(qualifier, name) =>
        visitQualifiedName (ng) (sma, node.id, qualifier, name)
    }

  private def visitQualifiedName (ng: StateMachineNameGroup) (
    sma: StateMachineAnalysis,
    id: AstNode.Id,
    qualifier: AstNode[Ast.QualIdent],
    name: AstNode[Ast.Ident]
  ) =
    for {
      sma <- visitQualIdentNode (ng) (sma, qualifier)
      scope <- {
        val symbol = sma.useDefMap(qualifier.id)
        sma.symbolScopeMap.get(symbol).map(Right(_)).getOrElse(
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
      val useDefMap = sma.useDefMap + (id -> symbol)
      sma.copy(useDefMap = useDefMap)
    }

}
