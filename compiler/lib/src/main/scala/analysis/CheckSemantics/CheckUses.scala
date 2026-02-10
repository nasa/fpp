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

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    helpers.visitQualIdentNode (NameGroup.ComponentInstance) (a, node)

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

  override def defStateMachineAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- {
        val symbol = Symbol.StateMachine(aNode)
        val scope = a.symbolScopeMap(symbol)
        val newNestedScope = a.nestedScope.push(scope)
        val a1 = a.copy(nestedScope = newNestedScope)
        super.defStateMachineAnnotatedNode(a1, aNode)
      }
    } yield a.copy(nestedScope = a.nestedScope.pop)
  }

  override def defTopologyAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefTopology]]) = {
    val impliedTypeUses = a.getImpliedUses(ImpliedUse.Kind.Type, node._2.id).toList
    val impliedConstantUses = a.getImpliedUses(ImpliedUse.Kind.Constant, node._2.id).toList
    for {
      _ <- Result.foldLeft (impliedConstantUses) (a) ((_, iu) => {
        val exprNode = iu.asExprNode
        for {
          a <- Result.annotateResult(
            constantUse(a, exprNode, iu.name),
            s"when constructing a dictionary, the constant ${iu.name} must be defined"
          )
          _ <- checkImpliedUseIsConstantDef(a, iu, exprNode)
        } yield a
      })
      _ <- Result.foldLeft (impliedTypeUses) (a) ((_, iu) => {
        Result.annotateResult(
          typeUse(a, iu.asTypeNameNode, iu.name),
          s"when constructing a dictionary, the type ${iu.name} must be defined"
        )
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

  // Check that an implied use is a constant def and not a member
  // of a constant def
  private def checkImpliedUseIsConstantDef(a: Analysis, iu: ImpliedUse, exprNode: AstNode[Ast.Expr]) = {
    val sym = a.useDefMap(exprNode.id)
    // Check that the name of the def matches the name of the use
    if a.getQualifiedName(sym) == iu.name
    // OK, they match
    then Right(a)
    // They don't match: the definition does not provide the required constant
    else
      val iuName = iu.name
      val symName = sym.getUnqualifiedName
      val error = Left(
        SemanticError.InvalidSymbol(
          symName,
          Locations.get(exprNode.id),
          s"it has $iuName as a member",
          sym.getLoc
        )
      )
      val notes = List(
        s"$iuName is an F Prime framework constant",
        "it must be a constant definition"
      )
      Result.annotateResult(error, notes)
  }

}
