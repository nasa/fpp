package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the values of constants symbols and expressions */
object EvalConstantExprs extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = 
    visitUse(a, node, use)

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = aNode
    if (!a.valueMap.contains(node.getId)) {
      val data = node.getData
      for {
        a <- super.defConstantAnnotatedNode(a, aNode)
        v <- Right(a.valueMap(data.value.getId))
        _ <- if (v.getType.isInt) {
               val i @ Value.Integer(_) = Analysis.convertValueToType(v, Type.Integer)
               if (i.fitsInU64Width) Right(()) else {
                 val loc = Locations.get(node.data.value.getId)
                 Left(SemanticError.ConstantTooLarge(loc))
               }
             } else Right(())
      } 
      yield a.assignValue(node -> v)
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
        val v = a.valueMap(id)
        values.get(v) match {
          case None => checkForDuplicateValue(a, tail, values + (v -> id))
          case Some(prevId) => {
            val intVal = Analysis.convertValueToType(v, Type.Integer)
            val loc = Locations.get(id)
            val prevLoc = Locations.get(prevId)
            Left(SemanticError.DuplicateEnumValue(intVal.toString, loc, prevLoc))
          }
        }
      }
    }
    for {
      a <- super.defEnumAnnotatedNode(a, aNode)
      _ <- checkForDuplicateValue(a, aNode._2.getData.constants.map(_._2.getId), Map())
    } yield a
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node, _) = aNode
    if (!a.valueMap.contains(node.getId)) {
      node.data.value match {
        case Some(e) => 
          for (a <- super.defEnumConstantAnnotatedNode(a, aNode))
            yield {
              val value = Analysis.convertValueToType(a.valueMap(e.getId), Type.Integer) match {
                case Value.Integer(value) => value
                case _ => throw InternalError("conversion to Integer type should yield Integer value")
              }
              val enumType = a.typeMap(node.getId) match {
                case enumType @ Type.Enum(_, _, _) => enumType
                case _ => throw InternalError("type of enum constant definition should be enum type")
              }
              val v = Value.EnumConstant(value, enumType)
              a.assignValue(node -> v)
            }
        case None => throw InternalError("implied enum constants should already be evaluated")
      }
    }
    else Right(a)
  }

  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) =
    for (a <- super.exprArrayNode(a, node, e))
      yield {
        val eltType = a.typeMap(node.getId) match {
          case Type.AnonArray(_, eltType) => eltType
          case _ => throw InternalError("element type of array expression should be AnonArray")
        }
        def f(node: AstNode[Ast.Expr]) = {
          val v = a.valueMap(node.getId)
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
            case Ast.Binop.Add => Right(a.add(e.e1.getId, e.e2.getId))
            case Ast.Binop.Div => a.div(e.e1.getId, e.e2.getId)
            case Ast.Binop.Mul => Right(a.mul(e.e1.getId, e.e2.getId))
            case Ast.Binop.Sub => Right(a.sub(e.e1.getId, e.e2.getId))
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
    val valueIsHex = if (e.value.length > 2) {
      val prefix = e.value.substring(0, 2)
      prefix == "0x" || prefix == "0X"
    } else false
    val (value, radix) = if (valueIsHex) (e.value.substring(2, e.value.length), 16) else (e.value, 10)
    val v = Value.Integer(BigInt(value, radix))
    if (v.fitsInU64Width) 
      Right(a.assignValue(node -> v))
    else {
      val loc = Locations.get(node.getId)
      Left(SemanticError.ConstantTooLarge(loc))
    }
  }
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) = {
    val v = Value.String(e.value)
    Right(a.assignValue(node -> v))
  }

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    for (a <- super.exprParenNode(a, node, e))
      yield a.assignValue(node -> a.valueMap(e.e.getId))
  }

  override def exprStructNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    for (a <- super.exprStructNode(a, node, e))
      yield {
        def visitor(members: Value.Struct.Members, node: AstNode[Ast.StructMember]): Value.Struct.Members = {
          val data = node.data
          val v = a.valueMap(data.value.getId)
          members + (data.name -> v)
        }
        val empty: Value.Struct.Members = Map()
        val members = e.members.foldLeft(empty)(visitor)
        val v = Value.AnonStruct(members)
        a.assignValue(node -> v)
      }

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
