package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP Type */
sealed trait Type {

  def hasNumericMembers: Boolean = isNumeric

  def isFloat: Boolean = false

  def isInt: Boolean = false

  def isPrimitive: Boolean = false

  final def isNumeric: Boolean = isInt || isFloat

}

object Type {

  /** Integer types */
  sealed trait Int extends Type {
    override def isInt = true
  }

  /** Primitive types */
  sealed trait Primitive extends Type {
    override def isPrimitive = true
  }

  /** Primitive integer types */
  case class PrimitiveInt(kind: PrimitiveInt.Kind) 
    extends Type with Primitive with Int
  {
    override def toString = kind match {
      case PrimitiveInt.I8 => "I8"
      case PrimitiveInt.I16 => "I16"
      case PrimitiveInt.I32 => "I32"
      case PrimitiveInt.I64 => "I64"
      case PrimitiveInt.U8 => "U8"
      case PrimitiveInt.U16 => "U16"
      case PrimitiveInt.U32 => "U32"
      case PrimitiveInt.U64 => "U64"
    }
  }

  object PrimitiveInt {
    sealed trait Kind
    case object I8 extends Kind
    case object I16 extends Kind
    case object I32 extends Kind
    case object I64 extends Kind
    case object U8 extends Kind
    case object U16 extends Kind
    case object U32 extends Kind
    case object U64 extends Kind
  }

  /** Floating-point types */
  case class Float(kind: Float.Kind) extends Type with Primitive {
    override def isFloat = true
    override def toString = kind match {
      case Float.F32 => "F32"
      case Float.F64 => "F64"
    }
  }

  object Float {
    sealed trait Kind
    case object F32 extends Kind
    case object F64 extends Kind
  }

  /** The Boolean type */
  case object Boolean extends Type with Primitive {
    override def toString = "bool"
  }

  /** The string type */
  case object String extends Type {
    override def toString = "string"
  }

  /** The type of arbitrary-width integers */
  case object Integer extends Type with Int {
    override def toString = "Integer"
  }
  
  /** An abstract type */
  case class AbsType(
    /** The AST node giving the definition */
    node: Ast.Annotated[AstNode[Ast.DefAbsType]]
  ) extends Type {
    override def toString = node._2.getData.name
  }

  /** A named array type */
  case class Array(
    /** The AST node giving the definition */
    node: Ast.Annotated[AstNode[Ast.DefArray]],
    /** The corresponding anonymous array type */
    anonArray: AnonArray
  ) extends Type {
    override def hasNumericMembers = anonArray.hasNumericMembers
    override def toString = node._2.getData.name
  }

  /** An enum type */
  case class Enum(
    /** The AST node giving the definition */
    node: Ast.Annotated[AstNode[Ast.DefEnum]],
    /** The representation type */
    repType: Type
  ) extends Type {
    override def toString = node._2.getData.name
  }

  /** A named struct type */
  case class Struct(
    /** The AST node giving the definition */
    node: Ast.Annotated[AstNode[Ast.DefStruct]],
    /** The corresponding anonymous struct type */
    anonStruct: AnonStruct
  ) extends Type {
    override def hasNumericMembers = anonStruct.hasNumericMembers
    override def toString = node._2.getData.name
  }

  /** An anonymous array type */
  case class AnonArray(
    /** The size expression */
    sizeExpr: AstNode[Ast.Expr],
    /** The element type */
    eltType: Type
  ) extends Type {
    override def hasNumericMembers = eltType.hasNumericMembers
    override def toString = "array of " + eltType.toString
  }
  
  /** An anonymous struct type */
  case class AnonStruct(
    /** The members */
    members: Set[(Name.Unqualified, Type)]
  ) extends Type {
    override def hasNumericMembers = members.forall(_._2.hasNumericMembers)
    override def toString = {
      def pairToString(pair: (Name.Unqualified, Type)) = pair._1 + ": " + pair._2.toString
      members.toList match {
        case Nil => "{ }"
        case list => "{ " ++ list.map(pairToString).mkString(", ") ++ " }"
      }
    }
  }

}
