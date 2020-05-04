package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute and check expression types, except for array sizes */
object CheckExprTypesPhase1 extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) =
    visitUse(a, node, use)

  override def defConstantAnnotatedNode(a: Analysis, anode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = anode
    if (!a.typeMap.contains(node.getId)) {
      val data = node.getData
      for (a <- super.defConstantAnnotatedNode(a, anode))
        yield {
          val t = a.typeMap.get(data.value.getId)
          assignType(a, node -> t)
        }
    }
    else Right(a)
  }

  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.getId)
    val emptyListError = SemanticError.EmptyArray(loc)
    for {
      a <- super.exprArrayNode(a, node, e)
      t <- a.commonType(e.elts.map(_.getId), emptyListError)
    } yield a.assignType(node -> Type.AnonArray(None, t))
  }

  override def exprBinopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprBinop) = {
    val loc = Locations.get(node.getId)
    for {
      a <- super.exprBinopNode(a, node, e)
      t <- a.commonType(e.e1.getId, e.e2.getId, loc)
      t <- if (t.isNumeric) Right(t) 
           else if (t.isConvertibleTo(Type.Integer)) Right(Type.Integer)
           else Left(nonNumericType(loc, t))
    } yield a.assignType(node -> t)
  }

  override def exprLiteralBoolNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) =
    Right(a.assignType(node -> Type.Boolean))

  override def exprLiteralFloatNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    Right(a.assignType(node -> Type.Float(Type.Float.F64)))

  override def exprLiteralIntNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    Right(a.assignType(node -> Type.Integer))
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    Right(a.assignType(node -> Type.String))

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    for (a <- super.exprParenNode(a, node, e))
      yield assignType(a, node -> a.typeMap.get(e.e.getId))
  }

  override def exprStructNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprStruct) = {
    def getName(member: Ast.StructMember) = member.name
    for {
      _ <- Analysis.checkForDuplicateStructMember(getName)(e.members)
      a <- super.exprStructNode(a, node, e)
    } 
    yield {
      def visitor(members: Type.Struct.Members, node: AstNode[Ast.StructMember]): Type.Struct.Members = {
        val data = node.data
        a.typeMap.get(data.value.getId) match {
          case Some(t) => members + (data.name -> t)
          case None => members
        }
      }
      val empty: Type.Struct.Members = Map()
      val members = e.members.foldLeft(empty)(visitor)
      a.assignType(node -> Type.AnonStruct(members))
    }
  }

  override def exprUnopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprUnop) = {
    val loc = Locations.get(node.getId)
    for {
      a <- super.exprUnopNode(a, node, e)
      t <- {
        val t1 = a.typeMap(e.e.getId)
        if (t1.isNumeric) Right(t1) 
        else if (t1.isConvertibleTo(Type.Integer)) Right(Type.Integer)
        else Left(nonNumericType(loc, t1))
      }
    } yield a.assignType(node -> t)
  }

  override def typeNameNode(a: Analysis, node: AstNode[Ast.TypeName]) = default(a)

  private def visitUse[T](a: Analysis, node: AstNode[T], use: Name.Qualified): Result = {
    val symbol = a.useDefMap(node.getId)
    for {
      a <- symbol match {
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a, node)
        case _ => Right(a)
      }
    } yield {
      val t = a.typeMap.get(symbol.getNodeId)
      assignType(a, node -> t)
    }
  }

  private def assignType[T](a: Analysis, mapping: (AstNode[T], Option[Type])): Analysis = {
    // TODO: Make this abort on failure to find the mapping
    val node -> tOpt = mapping
    tOpt match {
      case Some(t) => a.assignType(node -> t)
      case None => a
    }
  }

  private def nonNumericType(loc: Location, t: Type): Error = 
    SemanticError.InvalidType(loc, s"cannot convert $t to a numeric type")

}
