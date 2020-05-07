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
        // Visit the anonymous array type A' inside A, to update the members of A'
        t <- TypeVisitor.anonArray(a, arrayType.anonArray)
        // Update the size in A'
        arrayType <- {
          val sizeId = data.size.getId
          val Type.AnonArray(_, eltType) = t
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
        // Compute the default value for the node
        default <- data.default match {
          case Some(defaultNode) => {
            val id = defaultNode.getId
            val v = a.valueMap(id)
            val loc = Locations.get(id)
            for (_ <- Analysis.convertTypes(loc, v.getType -> arrayType))
              yield Analysis.convertValueToType(v, arrayType)
          }
          case None => Right(arrayType.getDefaultValue)
        }
      } 
      yield {
        // Update the default value in A
        val array @ Value.Array(_, _) = default
        val t = arrayType.copy(default = Some(array))
        // Update the type map
        a.assignType(node -> t)
      }
    }
    visitIfNeeded(symbol, visitor)(a, aNode)
  }

//  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
//    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
//      /*
//      val (_, node, _) = aNode
//      val data = node.data
//      val loc = Locations.get(node.getId)
//      for {
//        a <- super.defEnumAnnotatedNode(a, aNode)
//        _ <- data.constants match {
//          case Nil => Left(SemanticError.InvalidType(loc, "enum must define at least one constant"))
//          case _ => Right(())
//        }
//        repType <- {
//          data.typeName match {
//            case Some(typeName) => {
//              val repType = a.typeMap(typeName.getId)
//              val loc = Locations.get(typeName.getId)
//              repType match {
//                case t @ Type.PrimitiveInt(_) => Right(t)
//                case _ => Left(SemanticError.InvalidType(loc, "primitive integer type required"))
//              }
//            }
//            case None => Right(Type.I32)
//          }
//        }
//        a <- {
//          val t = Type.Enum(aNode, repType)
//          val a1 = a.assignType(node -> t)
//          def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]): Result =
//            Right(a.assignType(aNode._2 -> t))
//          visitList(a1, data.constants, visitor)
//        }
//      }
//      yield a
//      */
//      Right(a)
//    }
//    visitIfNeeded(visitor)(a, aNode)
//  }
//
//  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
//    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
//      /*
//      val (_, node, _) = aNode
//      val data = node.data
//      def getName(member: Ast.StructTypeMember) = member.name
//      for {
//        _ <- Analysis.checkForDuplicateStructMember(getName)(data.members.map(_._2))
//        a <- super.defStructAnnotatedNode(a, aNode)
//      }
//        yield {
//          def visitor(
//            members: Type.Struct.Members,
//            aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
//          ): Type.Struct.Members = {
//            val (_, node, _) = aNode
//            val data = node.data
//            val t = a.typeMap(data.typeName.getId)
//            members + (data.name -> t)
//          }
//          val empty: Type.Struct.Members = Map()
//          val members = data.members.foldLeft(empty)(visitor)
//          val anonStruct = Type.AnonStruct(members)
//          val t = Type.Struct(aNode, anonStruct)
//          a.assignType(node -> t)
//        }
//        */
//      Right(a)
//    }
//    visitIfNeeded(visitor)(a, aNode)
//  }

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

  }

}
