package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Finalize constant expressions. Recompute the types and values of constant
 *  expressions, using the finalized type definitions. Doing this fills in any
 *  missing anonymous array sizes and updates any type or value that depends
 *  on a finalized type definition. */
object FinalizeConstantExprs extends UseAnalyzer
{
  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.id)
    val emptyListError = SemanticError.EmptyArray(loc)
    for {
      a <- super.exprArrayNode(a, node, e)
      eltType <- a.commonType(e.elts.map(_.id), emptyListError)
      _ <- {
        val typeNames = e.elts.map(elt => a.typeMap(elt.id).toString)
        val msg = "cannot compute common type of array value with elements: " + typeNames.mkString(", ")
        eltType match {
          case Type.AnonArray(None, _) => Left(
            SemanticError.TypeMismatch(loc, msg)
          )
          case _ => Right(())
        }
      }
    } yield {
      def f(node: AstNode[Ast.Expr]) = {
        val v = a.valueMap(node.id)
        Analysis.convertValueToType(v, eltType)
      }
      val elts = e.elts.map(f)
      val v = Value.AnonArray(elts)
      a.assignType(node -> Type.AnonArray(Some(e.elts.size), eltType))
        .assignValue(node -> v)
    }
  }

  override def exprArraySubscriptNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArraySubscript) = {
    for {
      a <- super.exprArraySubscriptNode(a, node, e)

      elements <- {
        a.valueMap(e.e1.id) match {
          case Value.AnonArray(elements, _) => Right(elements)
          case Value.Array(Value.AnonArray(elements, _), _) => Right(elements)
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
      // For a scalar value promoted to an array, the size is not known until
      // the array type is finalized, so this is the first place where we can
      // check the upper bound
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
    } yield {
      // Update the element type, in case the type of the array was finalized
      val eltType = a.typeMap(e.e1.id).getUnderlyingType match {
        case Type.AnonArray(_, t) => t
        case Type.Array(_, anonArray, _, _) => anonArray.eltType
        case _ => a.typeMap(node.id)
      }
      a.assignType(node -> eltType).assignValue(node -> elements(index.toInt))
    }
  }

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    for (a <- super.exprDotNode(a, node, e))
      yield {
        // If this node is a use of a symbol, then visiting it has already
        // updated its type and value. Otherwise it selects a member of a
        // struct value, so update the type and value of the selection.
        if (a.useDefMap.contains(node.id)) a
        else {
          val name = e.id.data
          val tOpt = a.typeMap(e.e.id).getUnderlyingType match {
            case Type.Struct(_, anonStruct, _, _, _) => anonStruct.members.get(name)
            case Type.AnonStruct(members) => members.get(name)
            case _ => None
          }
          val vOpt = a.valueMap(e.e.id) match {
            case Value.Struct(anonStruct, _) => anonStruct.members.get(name)
            case Value.AnonStruct(members) => members.get(name)
            case _ => None
          }
          val t = tOpt.getOrElse(a.typeMap(node.id))
          val v = vOpt.getOrElse(a.valueMap(node.id))
          a.assignType(node -> t).assignValue(node -> v)
        }
      }

  override def exprStructNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    for (a <- super.exprStructNode(a, node, e))
      yield {
        def typeVisitor(members: Type.Struct.Members, node: AstNode[Ast.StructMember]): Type.Struct.Members = {
          val data = node.data
          members + (data.name -> a.typeMap(data.value.id))
        }
        def valueVisitor(members: Value.Struct.Members, node: AstNode[Ast.StructMember]): Value.Struct.Members = {
          val data = node.data
          members + (data.name -> a.valueMap(data.value.id))
        }
        val emptyTypeMembers: Type.Struct.Members = Map()
        val emptyValueMembers: Value.Struct.Members = Map()
        val t = Type.AnonStruct(e.members.foldLeft(emptyTypeMembers)(typeVisitor))
        val v = Value.AnonStruct(e.members.foldLeft(emptyValueMembers)(valueVisitor))
        a.assignType(node -> t).assignValue(node -> v)
      }

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    val symbol = a.useDefMap(node.id)
    for {
      a <- symbol match {
        // Constant symbol: visit the constant definition
        // to ensure it has a type
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        // Enum symbol: if this is in scope, then we are in
        // the enum definition, so it already has a type
        case Symbol.EnumConstant(node) => Right(a)
        case arg @ Symbol.TemplateConstantArg(_, _) => templateConstantArg(a, arg)
        // Invalid use of a symbol in an expression
        case _ =>
          Left(SemanticError.InvalidSymbol(
            symbol.getUnqualifiedName,
            Locations.get(node.id),
            "not a constant symbol",
            symbol.getLoc
          ))
      }
    } yield {
      val id = symbol.getNodeId
      a.assignType(node -> a.typeMap(id)).assignValue(node -> a.valueMap(id))
    }
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for (a <- super.defConstantAnnotatedNode(a, aNode))
      yield {
        val id = data.value.id
        a.assignType(node -> a.typeMap(id)).assignValue(node -> a.valueMap(id))
      }
  }

  override def templateConstantArg(
    a: Analysis,
    arg: Symbol.TemplateConstantArg
  ) = {
    val Symbol.TemplateConstantArg(paramDef, value) = arg
    val loc = Locations.get(value.id)
    for {
      a <- super.templateConstantArg(a, arg)
      ty <- Right(a.typeMap(paramDef.typeName.id))
      v <- Right(a.valueMap(value.id))
      newVal <- {
        v.convertToType(ty) match {
          case Some(v) => Right(v)
          case None => Left(SemanticError.TypeMismatch(loc, s"cannot convert value $v to type $ty"))
        }
      }
    } yield a.assignType(value -> ty).assignValue(value -> newVal)
  }
}
