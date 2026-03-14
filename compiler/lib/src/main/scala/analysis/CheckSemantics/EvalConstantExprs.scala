package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the values of constants symbols and expressions */
object EvalConstantExprs extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    val symbol = a.useDefMap(node.id)
    for {
      a <- symbol match {
        case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a, node)
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        case _ => throw InternalError(s"invalid constant use symbol ${symbol} (${symbol.getClass.getName()})")
      }
    } yield {
      val v = a.valueMap(symbol.getNodeId)
      a.assignValue(node -> v)
    }
  }

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

  override def exprArraySubscriptNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArraySubscript) = {
    for {
      a <- super.exprNode(a, e.e1)
      a <- super.exprNode(a, e.e2)

      elements <- {
        a.valueMap(e.e1.id) match {
          case Value.AnonArray(elements) => Right(elements)
          case Value.Array(Value.AnonArray(elements), _) => Right(elements)
          case _ => throw InternalError("expected array value")
        }
      }

      index <- {
        a.valueMap(e.e2.id) match {
          case Value.PrimitiveInt(value, _) => Right(value)
          case Value.Integer(value) => Right(value)
          case _ => throw InternalError("type of index should be an integer type")
        }
      }

      // Check if the index is in bounds
      _ <- {
        if index < 0
        then Left(SemanticError.InvalidIntValue(
          Locations.get(e.e2.id),
          index,
          "value may not be negative"
        ))
        else if index >= elements.length
        then Left(SemanticError.InvalidIntValue(
          Locations.get(e.e2.id),
          index,
          s"index value is not in the range [0, ${elements.length-1}]"
        ))
        else Right(None)
      }
    } yield a.assignValue(node -> elements(index.toInt))
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
    val bigInt = if e.value.length > 2 then e.value.substring(0, 2) match {
      case "0x" | "0X" => BigInt(e.value.substring(2, e.value.length), 16)
      case _ => BigInt(e.value)
    } else BigInt(e.value)

    Right(a.assignValue(node -> Value.Integer(bigInt)))
  }
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) = {
    val v = Value.String(e.value)
    Right(a.assignValue(node -> v))
  }

  override def exprNode(a: Analysis, node: AstNode[Ast.Expr]) =
    // If we've already visited the expression, don't analyze it again
    if !a.valueMap.contains(node.id) then super.exprNode(a, node) else Right(a)

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    for (a <- super.exprParenNode(a, node, e))
      yield a.assignValue(node -> a.valueMap(e.e.id))
  }

  override def exprSizeOfNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprSizeOf) =
    for {
      // First run the TypeExpressionAnalyzer on the type name
      a <- super.typeNameNode(a, e.typeName)
      // Then compute the type size
      a <- {
        def assignSize(a: Analysis, t: Type) = {
          val size = Type.SerializedSize.ty(a, t).get
          val v = Value.Integer(size)
          a.assignValue(node -> v)
        }
        // Get the type from the type map
        val t = a.typeMap(e.typeName.id)
        t.getDefNodeId match {
          case Some(id) =>
            // If the type has a definition, then finalize it
            // and use the size of the finalized type
            for (a <- FinalizeType.finalizeIfNeeded(a, t))
              yield assignSize(a, a.typeMap(id))
          case _ =>
            // Otherwise use size of the original type
            Right(assignSize(a, t))
        }
      }
    } yield a

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

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) = {
    for {
      // Ensure that the parent selector has a type
      a <- super.exprDotNode(a, node, e)

      // Get the value of the selected member
      v <- {
        if (a.valueMap.contains(node.id)) {
          // If the entire dot expression was already resolved by
          // a constantUse, the value will already be in this map
          Right(a.valueMap(node.id))
        } else {
          // The value is not already resolved, this is some sort of
          // member selection.
          a.valueMap(e.e.id) match {
            case Value.AnonStruct(members) =>
              Right(members(e.id.data))
            case Value.Struct(v, ty) =>
              Right(v.members(e.id.data))
            case x => Left(SemanticError.InvalidTypeForMemberSelection(
              e.id.data,
              Locations.get(e.id.id),
              x.getType.toString(),
            ))
          }
        }
      }
    } yield {
      a.assignValue(node -> v)
    }
  }

  // Visit nodes and member types, and finalize type defs
  // The FinalizeTypeDefs methods update the visited symbol set
  private object FinalizeType extends TypeVisitor {

    type In = Analysis

    type Out = Result.Result[Analysis]

    override def default(a: Analysis, t: Type) = Right(a)

    override def aliasType(a: Analysis, t: Type.AliasType) =
      for {
        a <- defAliasTypeAnnotatedNode(a, t.node)
        a <- FinalizeTypeDefs.defAliasTypeAnnotatedNode(a, t.node)
      } yield a

    override def array(a: Analysis, t: Type.Array) =
      for {
        a <- defArrayAnnotatedNode(a, t.node)
        a <- finalizeIfNeeded(a, t.anonArray.eltType)
        a <- FinalizeTypeDefs.defArrayAnnotatedNode(a, t.node)
      } yield a

    override def enumeration(a: Analysis, t: Type.Enum) =
      for {
        a <- defEnumAnnotatedNode(a, t.node)
        a <- FinalizeTypeDefs.defEnumAnnotatedNode(a, t.node)
      } yield a

    override def struct(a: Analysis, t: Type.Struct) =
      for {
        a <- defStructAnnotatedNode(a, t.node)
        a <- Result.foldLeft (t.anonStruct.members.toList) (a) {
          case (a1, (_ -> t1)) => finalizeIfNeeded(a1, t1)
        }
        a <- FinalizeTypeDefs.defStructAnnotatedNode(a, t.node)
      } yield a

    // Query whether a type is finalized
    // A type is finalized if (1) it has a definition symbol S
    // and S is in the visited symbol set; or (2)
    // it has no definition symbol
    private def typeIsFinalized(a: Analysis, t: Type) =
      t.getDefSymbol match {
        case Some(sym) => a.visitedSymbolSet.contains(sym)
        case _ => true
      }

    // Finalize a type if not already finalized
    def finalizeIfNeeded(a: Analysis, t: Type) =
      if !typeIsFinalized(a, t) then ty(a, t) else Right(a)

  }

}
