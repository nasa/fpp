package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the values of constants symbols and expressions */
object EvalConstantExprs extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = 
    visitUse(a, node, use)

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    /*
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
    */
    default(a)
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = aNode
    if (!a.valueMap.contains(node.getId)) {
      val data = node.getData
      for (a <- super.defConstantAnnotatedNode(a, aNode))
        yield {
          val v = a.valueMap(data.value.getId)
          a.assignValue(node -> v)
        }
    }
    else Right(a)
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    /*
    val (_, node, _) = aNode
    val data = node.data
    data.value match {
      case Some(e) => {
        for {
          a <- super.defEnumConstantAnnotatedNode(a, aNode)
          _ <- {
            val exprType = a.typeMap(e.getId)
            val loc = Locations.get(e.getId)
            convertToNumeric(loc, exprType)
          }
        } yield a
      }
      case None => Right(a)
    }
    */
    default(a)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    /*
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
    */
    default(a)
  }

  /*
  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.getId)
    val emptyListError = SemanticError.EmptyArray(loc)
    for {
      a <- super.exprArrayNode(a, node, e)
      t <- a.commonType(e.elts.map(_.getId), emptyListError)
    } yield a.assignType(node -> Type.AnonArray(Some(e.elts.size), t))
  }
  */

  override def exprBinopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprBinop) = {
    for {
      a <- super.exprBinopNode(a, node, e)
      v <- e.op match {
            case Ast.Binop.Add => Right(a.add(e.e1.getId, e.e2.getId))
            case Ast.Binop.Div => a.div(e.e1.getId, e.e2.getId)
            case Ast.Binop.Mul => Right(a.mul(e.e1.getId, e.e2.getId))
            case Ast.Binop.Sub => Right(a.sub(e.e1.getId, e.e2.getId))
          }
    } yield a.assignValue(node -> v)
  }

  override def exprLiteralBoolNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) = {
    val b = e.value match {
      case Ast.LiteralBool.True => true
      case Ast.LiteralBool.False => false
    }
    val v = Value.Boolean(b)
    Right(a.assignValue(node -> v))
  }


  override def exprLiteralFloatNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) = {
    val v = Value.Float(e.value.toDouble, Type.Float.F64)
    Right(a.assignValue(node -> v))
  }

  override def exprLiteralIntNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) = {
    val v = Value.Integer(BigInt(e.value))
    Right(a.assignValue(node -> v))
  }
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) = {
    val v = Value.String(e.value)
    Right(a.assignValue(node -> v))
  }

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    for (a <- super.exprParenNode(a, node, e))
      yield a.assignValue(node -> a.valueMap(e.e.getId))
  }

  /*
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
  */

  override def exprUnopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprUnop) = {
    val loc = Locations.get(node.getId)
    for (a <- super.exprUnopNode(a, node, e))
      yield {
        val v = a.neg(e.e.getId)
        a.assignValue(node -> v)
      }
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
      val v = a.valueMap(symbol.getNodeId)
      a.assignValue(node -> v)
    }
  }

}
