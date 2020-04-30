package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute and check types, except for array sizes */
object CheckTypesPhase1 extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) =
    visitUse(a, node, use)

  override def defConstantAnnotatedNode(a: Analysis, anode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = anode
    if (!a.typeMap.contains(node.getId)) {
      val data = node.getData
      for (a <- super.defConstantAnnotatedNode(a, anode))
        yield {
          val t = a.typeMap.get(data.value.getId)
          addTypeMapping(a, node -> t)
        }
    }
    else Right(a)
  }

  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.getId)
    val emptyListError = SemanticError.MalformedExpression(loc, "array expression may not have zero elements")
    for {
      a <- super.exprArrayNode(a, node, e)
      t <- a.computeCommonType(e.elts.map(_.getId), emptyListError)
    } yield a.addTypeMapping(node -> Type.AnonArray(None, t))
  }

  override def exprBinopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprBinop) = {
    // TODO
    Right(a.addTypeMapping(node -> Type.Integer))
  }

  override def exprLiteralBoolNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) =
    Right(a.addTypeMapping(node -> Type.Boolean))

  override def exprLiteralFloatNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    Right(a.addTypeMapping(node -> Type.Float(Type.Float.F64)))

  override def exprLiteralIntNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    Right(a.addTypeMapping(node -> Type.Integer))
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    Right(a.addTypeMapping(node -> Type.String))

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    // TODO
    Right(a.addTypeMapping(node -> Type.Integer))
  }

  override def exprStructNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprStruct) = {
    // TODO
    Right(a.addTypeMapping(node -> Type.AnonStruct(Map())))
  }

  override def exprUnopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprUnop) = {
    // TODO
    Right(a.addTypeMapping(node -> Type.Integer))
  }

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    // TODO
    Right(a.addTypeMapping(node -> Type.Integer))
  }

  private def visitUse[T](a: Analysis, node: AstNode[T], use: Name.Qualified): Result = {
    val symbol = a.useDefMap(node.getId)
    for {
      a <- symbol match {
        case Symbol.Array(node) => defArrayAnnotatedNode(a, node)
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        case Symbol.Enum(node) => defEnumAnnotatedNode(a, node)
        case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a, node)
        case Symbol.Struct(node) => defStructAnnotatedNode(a, node)
        case _ => Right(a)
      }
    } yield {
      val t = a.typeMap.get(symbol.getNodeId)
      addTypeMapping(a, node -> t)
    }
  }

  private def addTypeMapping[T](a: Analysis, mapping: (AstNode[T], Option[Type])): Analysis = {
    // TODO: Make this abort on failure to find the mapping
    val node -> tOpt = mapping
    tOpt match {
      case Some(t) => a.addTypeMapping(node -> t)
      case None => a
    }
  }

}
