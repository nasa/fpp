package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check type uses */
object CheckTypeUses extends UseAnalyzer {

  override def defAbsTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
      val t = Type.AbsType(aNode)
      val node = aNode._2
      Right(a.assignType(node -> t))
    }
    visitIfNeeded(visitor)(a, aNode)
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) =
      for (a <- super.defArrayAnnotatedNode(a, aNode))
        yield {
          val (_, node, _) = aNode
          val data = node.data
          val eltType = a.typeMap(data.eltType.getId)
          val anonArray = Type.AnonArray(None, eltType)
          val t = Type.Array(aNode, anonArray)
          a.assignType(node -> t)
        }
    visitIfNeeded(visitor)(a, aNode)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
      val (_, node, _) = aNode
      val data = node.data
      val loc = Locations.get(node.getId)
      for {
        a <- super.defEnumAnnotatedNode(a, aNode)
        _ <- data.constants match {
          case Nil => Left(SemanticError.InvalidType(loc, "enum must define at least one constant"))
          case _ => Right(())
        }
        repType <- {
          data.typeName match {
            case Some(typeName) => {
              val repType = a.typeMap(typeName.getId)
              val loc = Locations.get(typeName.getId)
              repType match {
                case Type.PrimitiveInt(_) => Right(repType)
                case _ => Left(SemanticError.InvalidType(loc, "primitive integer type required"))
              }
            }
            case None => Right(Type.I32)
          }
        }
        a <- {
          val t = Type.Enum(aNode, repType)
          val a1 = a.assignType(node -> t)
          def visitor[T](a: Analysis, aNode: T) = Right(a.assignType(node -> t))
          visitList(a1, data.constants, visitor)
        }
      }
      yield a
    }
    visitIfNeeded(visitor)(a, aNode)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    def visitor(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
      val (_, node, _) = aNode
      val data = node.data
      def getName(member: Ast.StructTypeMember) = member.name
      for {
        _ <- Analysis.checkForDuplicateStructMember(getName)(data.members.map(_._2))
        a <- super.defStructAnnotatedNode(a, aNode)
      }
        yield {
          def visitor(
            members: Type.Struct.Members,
            aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
          ): Type.Struct.Members = {
            val (_, node, _) = aNode
            val data = node.data
            val t = a.typeMap(data.typeName.getId)
            members + (data.name -> t)
          }
          val empty: Type.Struct.Members = Map()
          val members = data.members.foldLeft(empty)(visitor)
          val anonStruct = Type.AnonStruct(members)
          val t = Type.Struct(aNode, anonStruct)
          a.assignType(node -> t)
        }
    }
    visitIfNeeded(visitor)(a, aNode)
  }

  override def exprNode(a: Analysis, node: AstNode[Ast.Expr]) = default(a)

  override def typeNameBoolNode(a: Analysis, node: AstNode[Ast.TypeName]) =
    Right(a.assignType(node -> Type.Boolean))

  override def typeNameFloatNode(a: Analysis, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat) = {
    val t = tn.name match {
      case Ast.F32() => Type.F32
      case Ast.F64() => Type.F64
    }
    Right(a.assignType(node -> t))
  }

  override def typeNameIntNode(a: Analysis, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt) = {
    val t = tn.name match {
      case Ast.I8() => Type.I8
      case Ast.I16() => Type.I16
      case Ast.I32() => Type.I32
      case Ast.I64() => Type.I64
      case Ast.U8() => Type.U8
      case Ast.U16() => Type.U16
      case Ast.U32() => Type.U32
      case Ast.U64() => Type.U64
    }
    Right(a.assignType(node -> t))
  }

  override def typeNameStringNode(a: Analysis, node: AstNode[Ast.TypeName]) =
    Right(a.assignType(node -> Type.String))

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) =
    visitUse(a, node, use)

  private def visitUse[T](a: Analysis, node: AstNode[T], use: Name.Qualified): Result = {
    val symbol = a.useDefMap(node.getId)
    for {
      a <- symbol match {
        case Symbol.AbsType(node) => defAbsTypeAnnotatedNode(a, node)
        case Symbol.Array(node) => defArrayAnnotatedNode(a, node)
        case Symbol.Enum(node) => defEnumAnnotatedNode(a, node)
        case Symbol.Struct(node) => defStructAnnotatedNode(a, node)
        case _ => Right(a)
      }
    } yield {
      val t = a.typeMap(symbol.getNodeId)
      a.assignType(node -> t)
    }
  }

  private def visitIfNeeded[T]
    (visitor: (Analysis, Ast.Annotated[AstNode[T]]) => Result) 
    (a: Analysis, aNode: Ast.Annotated[AstNode[T]]): Result =
  {
    val node = aNode._2
    if (!a.typeMap.contains(node.getId)) visitor(a, aNode)
    else Right(a)
  }

}
