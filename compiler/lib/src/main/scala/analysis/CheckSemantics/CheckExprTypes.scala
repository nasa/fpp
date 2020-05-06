package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute and check expression types, except for array sizes
 *  and default values */
object CheckExprTypes extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = 
    visitUse(a, node, use)

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.defArrayAnnotatedNode(a, aNode)
      _ <- {
        val id = data.size.getId
        val t = a.typeMap(id)
        val loc = Locations.get(id)
        convertToNumeric(loc, t)
      }
      _ <- data.default match {
        case Some(defaultNode) => {
          val arrayId = node.getId
          val arrayType = a.typeMap(arrayId)
          val defaultId = defaultNode.id
          val defaultType = a.typeMap(defaultId)
          val loc = Locations.get(defaultId)
          Analysis.convertTypes(loc, defaultType -> arrayType)
        }
        case None => Right(a)
      }
    } yield a
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = aNode
    if (!a.typeMap.contains(node.getId)) {
      val data = node.getData
      for (a <- super.defConstantAnnotatedNode(a, aNode))
        yield {
          val t = a.typeMap(data.value.getId)
          a.assignType(node -> t)
        }
    }
    else Right(a)
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node, _) = aNode
    val data = node.data
    data.value match {
      case Some(e) => {
        for {
          a <- super.defEnumConstantAnnotatedNode(a, aNode)
          _ <- {
            val exprType = a.typeMap(e.getId)
            val loc = Locations.get(e.getId)
            // Just check that the type of the value expression is convertible to numeric
            // The enum type of the enum constant node is already in the type map
            convertToNumeric(loc, exprType)
          }
        } yield a
      }
      case None => Right(a)
    }
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.defStructAnnotatedNode(a, aNode)
      _ <- data.default match {
        case Some(defaultNode) => {
          val structId = node.getId
          val structType = a.typeMap(structId)
          val defaultId = defaultNode.id
          val defaultType = a.typeMap(defaultId)
          val loc = Locations.get(defaultId)
          Analysis.convertTypes(loc, defaultType -> structType)
        }
        case None => Right(a)
      }
    } yield a
  }

  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.getId)
    val emptyListError = SemanticError.EmptyArray(loc)
    for {
      a <- super.exprArrayNode(a, node, e)
      t <- a.commonType(e.elts.map(_.getId), emptyListError)
    } yield a.assignType(node -> Type.AnonArray(Some(e.elts.size), t))
  }

  override def exprBinopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprBinop) = {
    val loc = Locations.get(node.getId)
    for {
      a <- super.exprBinopNode(a, node, e)
      t <- a.commonType(e.e1.getId, e.e2.getId, loc)
      _ <- convertToNumeric(loc, t)
    } yield a.assignType(node -> t)
  }

  override def exprLiteralBoolNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) =
    Right(a.assignType(node -> Type.Boolean))

  override def exprLiteralFloatNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    Right(a.assignType(node -> Type.F64))

  override def exprLiteralIntNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    Right(a.assignType(node -> Type.Integer))
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    Right(a.assignType(node -> Type.String))

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    for (a <- super.exprParenNode(a, node, e))
      yield a.assignType(node -> a.typeMap(e.e.getId))
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
        val t = a.typeMap(data.value.getId)
        members + (data.name -> t)
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
        convertToNumeric(loc, t1)
      }
    } yield a.assignType(node -> t)
  }

  override def typeNameNode(a: Analysis, node: AstNode[Ast.TypeName]) = default(a)

  private def visitUse[T](a: Analysis, node: AstNode[T], use: Name.Qualified): Result = {
    val symbol = a.useDefMap(node.getId)
    for {
      a <- symbol match {
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        case _ => Right(a)
      }
    } yield {
      val t = a.typeMap(symbol.getNodeId)
      a.assignType(node -> t)
    }
  }

  private def convertToNumeric(loc: Location, t: Type): Result.Result[Type] = {
    if (t.isNumeric) Right(t) 
    else if (t.isConvertibleTo(Type.Integer)) Right(Type.Integer)
    else {
      val error = SemanticError.InvalidType(loc, s"cannot convert $t to a numeric type")
      Left(error)
    }
  }

}
