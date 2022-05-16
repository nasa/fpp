package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Finalize type definitions. Update the types of uses (type names) that refer 
 *  to the definitions. */
object FinalizeTypeDefs 
  extends TypeExpressionAnalyzer
{

  override def exprNode(a: Analysis, node: AstNode[Ast.Expr]) = default(a)

  override def typeNameNode(a: Analysis, node: AstNode[Ast.TypeName]) = {
    val t1 = a.typeMap(node.id)
    for (t2 <- TypeVisitor.ty(a, t1))
      yield if (t1 != t2) a.assignType(node -> t2) else a
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val symbol = Symbol.Array(aNode)
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
      val node = aNode._2
      val data = node.data
      // Get the type of this node as an array type A
      val arrayType @ Type.Array(_, _, _, _) = a.typeMap(node.id)
      for {
        // Visit the element type of A, to update its members
        eltType <- TypeVisitor.ty(a, arrayType.anonArray.eltType)
        // Update the size and element type in A
        size <- a.getBoundedArraySize(data.size.id)
        arrayType <- {
          val anonArray = Type.AnonArray(Some(size.toInt), eltType)
          Right(arrayType.copy(anonArray = anonArray))
        }
        // Compute the default value
        default <- data.default match {
          case Some(defaultNode) => {
            val id = defaultNode.id
            val v = a.valueMap(id)
            val loc = Locations.get(id)
            for (_ <- Analysis.convertTypes(loc, v.getType -> arrayType))
              yield {
                val array @ Value.Array(_, _) = Analysis.convertValueToType(v, arrayType)
                array
              }
          }
          case None => {
            val Some(anonArray) = arrayType.anonArray.getDefaultValue
            Right(Value.Array(anonArray, arrayType))
          }
        }
        // Compute the format
        format <- Result.mapOpt(
          data.format,
          Analysis.computeFormat(_, List(eltType))
        )
      } 
      yield {
        // Update the default value and format in A
        val arrayType1 = arrayType.copy(default = Some(default), format = format)
        // Update A in the type map
        a.assignType(node -> arrayType1)
      }
    }
    visitIfNeeded(symbol, visitor)(a, aNode)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val symbol = Symbol.Enum(aNode)
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
      val (_, node, _) = aNode
      val data = node.data
      val enumType @ Type.Enum(_, _, _) = a.typeMap(node.id)
      val default = data.default match {
        case Some(default) => a.valueMap(default.id)
        case None => a.valueMap(data.constants.head._2.id)
      }
      val enumConstant @ Value.EnumConstant(_, _) = default
      val enumType1 = enumType.copy(default = Some(enumConstant))
      Right(a.assignType(node -> enumType1))
    }
    visitIfNeeded(symbol, visitor)(a, aNode)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val symbol = Symbol.Struct(aNode)
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
      val (_, node, _) = aNode
      val data = node.data
      // Get the type of this node as a struct type S
      val structType @ Type.Struct(_, _, _, _, _) = a.typeMap(node.id)
      for {
        // Visit the anonymous struct type of S, to update its members
        t <- TypeVisitor.ty(a, structType.anonStruct)
        // Update the anonymous struct type of S
        structType <- {
          val anonStructType @ Type.AnonStruct(_) = t
          Right(structType.copy(anonStruct = anonStructType))
        }
        // Compute the default value
        default <- data.default match {
          case Some(defaultNode) => {
            val id = defaultNode.id
            val v = a.valueMap(id)
            val loc = Locations.get(id)
            for (_ <- Analysis.convertTypes(loc, v.getType -> structType))
              yield {
                val struct @ Value.Struct(_, _) = Analysis.convertValueToType(v, structType)
                struct
              }
          }
          case None => {
            val Some(anonStruct) = structType.anonStruct.getDefaultValue
            Right(Value.Struct(anonStruct, structType))
          }
        }
        members <- Right(data.members.map(_._2.data))
        // Compute the sizes
        sizes <- {
          def mapping(member: Ast.StructTypeMember) = for {
            intOpt <- a.getUnboundedArraySizeOpt(member.size)
          } yield (intOpt.map(n => (member.name, n)))
          for (pairs <- Result.map(members, mapping)) yield {
            pairs.filter(_.isDefined).map(_.get).toMap
          }
        }
        // Compute the formats
        formats <- {
          def mapping(member: Ast.StructTypeMember) = {
            val name = member.name
            val t = structType.anonStruct.members(name)
            for {
              formatOpt <- Result.mapOpt(
                member.format,
                node => Analysis.computeFormat(node, List(t))
              )
            } yield formatOpt.map(format => (name, format))
          }
          for (pairs <- Result.map(members, mapping)) yield {
            pairs.filter(_.isDefined).map(_.get).toMap
          }
        }
      } 
      yield {
        // Update the default value, sizes, and formats in S
        val structType1 = structType.copy(
          default = Some(default),
          sizes = sizes,
          formats = formats
        )
        // Update S in the type map
        a.assignType(node -> structType1)
      }
    }
    visitIfNeeded(symbol, visitor)(a, aNode)
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    super.transUnit(a.copy(visitedSymbolSet = Set()), tu)

  private def visitIfNeeded[T]
    (symbol: Symbol, visitor: (Analysis, Ast.Annotated[AstNode[T]]) => Result) 
    (a: Analysis, aNode: Ast.Annotated[AstNode[T]]): Result =
  {
    if (!a.visitedSymbolSet.contains(symbol))
      for (a <- visitor(a, aNode))
        yield a.copy(visitedSymbolSet = a.visitedSymbolSet + symbol)
    else Right(a)
  }

  object TypeVisitor extends TypeVisitor {

    type In = Analysis

    type Out = Result.Result[Type]

    override def default(a: Analysis, t: Type) = Right(t)

    override def array(a: Analysis, t: Type.Array) =
      for (a <- defArrayAnnotatedNode(a, t.node))
        yield a.typeMap(t.node._2.id)

    override def anonArray(a: Analysis, t: Type.AnonArray) =
      for (eltType <- ty(a, t.eltType))
        yield Type.AnonArray(t.size, eltType)

    override def enumeration(a: Analysis, t: Type.Enum) =
      for (a <- defEnumAnnotatedNode(a, t.node))
        yield a.typeMap(t.node._2.id)

    override def string(a: Analysis, t: Type.String) =
      t.size match {
        case Some(e) => {
          val id = e.id
          val Value.Integer(size) = Analysis.convertValueToType(
            a.valueMap(id),
            Type.Integer
          )
          if (size > 0) Right(t)
          else {
            val loc = Locations.get(id)
            Left(SemanticError.InvalidStringSize(loc, size))
          }
        }
        case None => Right(t)
      }

    override def struct(a: Analysis, t: Type.Struct) =
      for (a <- defStructAnnotatedNode(a, t.node))
        yield a.typeMap(t.node._2.id)

    override def anonStruct(a: Analysis, t: Type.AnonStruct) = {
      def visitor(member: Type.Struct.Member): Result.Result[Type.Struct.Member] = 
        for (memberType <- ty(a, member._2)) yield member._1 -> memberType
      for (members <- Result.map(t.members.toList, visitor))
        yield Type.AnonStruct(members.toMap)
    }

  }

}
