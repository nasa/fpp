package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the values of constants symbols and expressions */
object EvalConstantExprs extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = 
    visitUse(a, node)

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = aNode
    if (!a.valueMap.contains(node.id)) {
      val data = node.data
      for (a <- super.defConstantAnnotatedNode(a, aNode))
        yield {
          val v = a.valueMap(data.value.id)
          a.assignValue(node -> v)
        }
    }
    else Right(a)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    def checkForDuplicateValue(
      a: Analysis,
      ids: List[AstNode.Id],
      values: Map[Value,AstNode.Id]
    ): Result.Result[Unit] = ids match {
      case Nil => Right(())
      case id :: tail => {
        val v = Analysis.convertValueToType(a.valueMap(id), Type.Integer)
        values.get(v) match {
          case None => checkForDuplicateValue(a, tail, values + (v -> id))
          case Some(prevId) => {
            val loc = Locations.get(id)
            val prevLoc = Locations.get(prevId)
            Left(SemanticError.DuplicateEnumValue(v.toString, loc, prevLoc))
          }
        }
      }
    }
    for {
      a <- super.defEnumAnnotatedNode(a, aNode)
      _ <- checkForDuplicateValue(a, aNode._2.data.constants.map(_._2.id), Map())
    } yield a
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node, _) = aNode
    if (!a.valueMap.contains(node.id)) {
      node.data.value match {
        case Some(e) => 
          for (a <- super.defEnumConstantAnnotatedNode(a, aNode))
            yield {
              val intValue = Analysis.convertValueToType(a.valueMap(e.id), Type.Integer) match {
                case Value.Integer(intValue) => intValue
                case _ => throw InternalError("conversion to Integer type should yield Integer value")
              }
              val enumType = a.typeMap(node.id) match {
                case enumType : Type.Enum => enumType
                case _ => throw InternalError("type of enum constant definition should be enum type")
              }
              val value = (node.data.name, intValue)
              val enumValue = Value.EnumConstant(value, enumType)
              a.assignValue(node -> enumValue)
            }
        case None => throw InternalError("implied enum constants should already be evaluated")
      }
    }
    else Right(a)
  }

  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) =
    for (a <- super.exprArrayNode(a, node, e))
      yield {
        val eltType = a.typeMap(node.id) match {
          case Type.AnonArray(_, eltType) => eltType
          case _ => throw InternalError("element type of array expression should be AnonArray")
        }
        def f(node: AstNode[Ast.Expr]) = {
          val v = a.valueMap(node.id)
          Analysis.convertValueToType(v, eltType)
        }
        val elts = e.elts.map(f)
        val v = Value.AnonArray(elts)
        a.assignValue(node -> v)
      }

  override def exprBinopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprBinop) =
    for {
      a <- super.exprBinopNode(a, node, e)
      v <- e.op match {
            case Ast.Binop.Add => Right(a.add(e.e1.id, e.e2.id))
            case Ast.Binop.Div => a.div(e.e1.id, e.e2.id)
            case Ast.Binop.Mul => Right(a.mul(e.e1.id, e.e2.id))
            case Ast.Binop.Sub => Right(a.sub(e.e1.id, e.e2.id))
          }
    } yield a.assignValue(node -> v)

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
    val bigInt = if (e.value.startsWith("0x") || e.value.startsWith("0X"))
      BigInt(e.value.substring(2, e.value.length), 16) else BigInt(e.value)
    val v = Value.Integer(bigInt)
    Right(a.assignValue(node -> v))
  }
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) = {
    val v = Value.String(e.value)
    Right(a.assignValue(node -> v))
  }

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    for (a <- super.exprParenNode(a, node, e))
      yield a.assignValue(node -> a.valueMap(e.e.id))
  }

  override def exprStructNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    for (a <- super.exprStructNode(a, node, e))
      yield {
        def visitor(members: Value.Struct.Members, node: AstNode[Ast.StructMember]): Value.Struct.Members = {
          val data = node.data
          val v = a.valueMap(data.value.id)
          members + (data.name -> v)
        }
        val empty: Value.Struct.Members = Map()
        val members = e.members.foldLeft(empty)(visitor)
        val v = Value.AnonStruct(members)
        a.assignValue(node -> v)
      }

  override def exprUnopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprUnop) = {
    for (a <- super.exprUnopNode(a, node, e))
      yield {
        val v = a.neg(e.e.id)
        a.assignValue(node -> v)
      }
  }

  private def visitUse[T](a: Analysis, node: AstNode[T]): Result = {
    val symbol = a.useDefMap(node.id)
    for {
      a <- symbol match {
        case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a, node)
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        case _ => Right(a)
      }
    } yield {
      val v = a.valueMap(symbol.getNodeId)
      a.assignValue(node -> v)
    }
  }

}
