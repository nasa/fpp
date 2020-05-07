package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Finalize type definitions. Update the types of uses (type names) that refer 
 *  to the definitions. */
object FinalizeTypeDefs extends ModuleAnalyzer {

  val maxArraySize = 1000

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val symbol = Symbol.Array(aNode)
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
      val node = aNode._2
      val data = node.getData
      // Get the type of this node as an array type A
      val arrayType @ Type.Array(_, _, _) = a.typeMap(node.getId)
      for {
        // Visit the element type of A, to update its members
        eltType <- TypeVisitor.ty(a, arrayType.anonArray.eltType)
        // Update the size and element type in A
        arrayType <- {
          val sizeId = data.size.getId
          val Value.Integer(size) = Analysis.convertValueToType(
            a.valueMap(sizeId),
            Type.Integer
          )
          if (size >= 0 && size <= maxArraySize) {
            val anonArray = Type.AnonArray(Some(size.toInt), eltType)
            Right(arrayType.copy(anonArray = anonArray))
          }
          else {
            val loc = Locations.get(sizeId)
            Left(SemanticError.InvalidArraySize(loc, size))
          }
        }
        // Compute the default value
        default <- data.default match {
          case Some(defaultNode) => {
            val id = defaultNode.getId
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
      } 
      yield {
        // Update the default value in A
        val arrayType1 = arrayType.copy(default = Some(default))
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
      val enumType @ Type.Enum(_, _, _) = a.typeMap(node.getId)
      val default = a.valueMap(data.constants.head._2.getId)
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
      // TODO: Compute the real default value and check that the type matches
      val anonStruct = Value.AnonStruct(Map())
      val structType @ Type.Struct(_, _, _) = a.typeMap(node.getId)
      val default = Value.Struct(anonStruct, structType)
      val structType1 = structType.copy(default = Some(default))
      Right(a.assignType(node -> structType1))
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
        yield a.typeMap(t.node._2.getId)

    override def anonArray(a: Analysis, t: Type.AnonArray) =
      for (eltType <- ty(a, t.eltType))
        yield Type.AnonArray(t.size, eltType)

    override def enum(a: Analysis, t: Type.Enum) = 
      for (a <- defEnumAnnotatedNode(a, t.node))
        yield a.typeMap(t.node._2.getId)

    override def struct(a: Analysis, t: Type.Struct) =
      for (a <- defStructAnnotatedNode(a, t.node))
        yield a.typeMap(t.node._2.getId)

    override def anonStruct(a: Analysis, t: Type.AnonStruct) = {
      def visitor(member: Type.Struct.Member): Result.Result[Type.Struct.Member] = 
        for (t <- ty(a, member._2)) yield member._1 -> t
      for (members <- Result.map(t.members.toList, visitor))
        yield Type.AnonStruct(members.toMap)
    }

  }

}
