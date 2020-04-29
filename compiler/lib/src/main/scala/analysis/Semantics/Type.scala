package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP Type */
sealed trait Type {

  /** Convert this type to another type t. Yield t or an error if the conversion is illegal. */
  def convertTo(t: Type, loc: Location): Result.Result[Type] =
    this.isConvertibleTo(t) match {
      case true => Right(t)
      case false => Left(SemanticError.TypeMismatch(loc, s"cannot convert ${this} to ${t}"))
    }

  /** Get the definition node identifier, if any */
  def getDefNodeId: Option[AstNode.Id] = None

  /** Does this type have numeric members? */
  def hasNumericMembers: Boolean = isNumeric

  /** Is this type convertible to a numeric type? */
  def isConvertibleToNumeric: Boolean = isNumeric

  /** Is this type promotable to an array type? */
  def isPromotableToArray: Boolean = isNumeric

  /** Is this type a float type? */
  def isFloat: Boolean = false

  /** Is this type an int type? */
  def isInt: Boolean = false

  /** Is this type a primitive type? */
  def isPrimitive: Boolean = false

  /** Is this type promotable to a struct type? */
  final def isPromotableToStruct = isPromotableToArray

  /** Is this type numeric? */
  final def isNumeric: Boolean = isInt || isFloat

  /** Is this type convertible to type t? 
   *  Checking here is done without regard to array size */
  final def isConvertibleTo(t: Type) = Type.mayBeConverted(this -> t)

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
    override def isPromotableToArray = true
  }

  /** The string type */
  case object String extends Type {
    override def toString = "string"
    override def isPromotableToArray = true
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
    override def getDefNodeId = Some(node._2.getId)
    override def toString = "abstract type " ++ node._2.getData.name
  }

  /** A named array type */
  case class Array(
    /** The AST node giving the definition */
    node: Ast.Annotated[AstNode[Ast.DefArray]],
    /** The size expression */
    size: AstNode[Ast.Expr],
    /** The element type */
    eltType: Type
  ) extends Type {
    override def getDefNodeId = Some(node._2.getId)
    override def hasNumericMembers = eltType.hasNumericMembers
    override def toString = "array type " ++ node._2.getData.name
  }

  /** An enum type */
  case class Enum(
    /** The AST node giving the definition */
    node: Ast.Annotated[AstNode[Ast.DefEnum]],
    /** The representation type */
    repType: Type
  ) extends Type {
    override def getDefNodeId = Some(node._2.getId)
    override def isConvertibleToNumeric = true
    override def isPromotableToArray = true
    override def toString = "enum type " ++ node._2.getData.name
  }

  /** A named struct type */
  case class Struct(
    /** The AST node giving the definition */
    node: Ast.Annotated[AstNode[Ast.DefStruct]],
    /** The corresponding anonymous struct type */
    anonStruct: AnonStruct
  ) extends Type {
    override def getDefNodeId = Some(node._2.getId)
    override def hasNumericMembers = anonStruct.hasNumericMembers
    override def toString = "struct type " ++ node._2.getData.name
  }

  /** An anonymous array type */
  case class AnonArray(
    /** The array size */
    size: Int,
    /** The element type */
    eltType: Type
  ) extends Type {
    override def hasNumericMembers = eltType.hasNumericMembers
    override def toString = "array of " + eltType.toString
  }
  
  /** An anonymous struct type */
  case class AnonStruct(
    /** The members */
    members: AnonStruct.Members
  ) extends Type {
    override def hasNumericMembers = members.values.forall(_.hasNumericMembers)
    override def toString = {
      def memberToString(member: AnonStruct.Member) = member._1 ++ ": " ++ member._2.toString
      members.size match {
        case 0 => "{ }"
        case _ => "struct type { " ++ members.map(memberToString).mkString(", ") ++ " }"
      }
    }
  }

  object AnonStruct {

    type Member = (Name.Unqualified, Type)

    type Members = Map[Name.Unqualified, Type]

  }

  /** Check for type identity */
  def areIdentical(t1: Type, t2: Type): Boolean = {
    val pair = (t1, t2)
    def numeric = pair match {
      case (PrimitiveInt(kind1), PrimitiveInt(kind2)) => kind1 == kind2
      case (Float(kind1), Float(kind2)) => kind1 == kind2
      case (Integer, Integer) => true
      case _ => false
    }
    def boolean = pair match {
      case (Boolean -> Boolean) => true
      case _ => false
    }
    def string = pair match {
      case (String -> String) => true
      case _ => false
    }
    def sameDef = (t1.getDefNodeId, t2.getDefNodeId) match {
      case (Some(id1), Some(id2)) => id1 == id2
      case _ => false
    }
    numeric ||
    boolean ||
    string ||
    sameDef
  }
  
  /** Check for convertibility, ignoring array sizes */
  def mayBeConverted(pair: (Type, Type)): Boolean = {
    val t1 -> t2 = pair
    def toNumeric = t1.isConvertibleToNumeric && t2.isNumeric
    def toArray = pair match {
      case AnonArray(_, eltType1) -> AnonArray(_, eltType2) => 
        eltType1.isConvertibleTo(eltType2)
      case AnonArray(_, eltType1) -> Array(_, _, eltType2) =>
        eltType1.isConvertibleTo(eltType2)
      case _ -> Array(_, _, eltType) =>
        t1.isPromotableToArray && t1.isConvertibleTo(eltType)
      case _ => false
    }
    def toStruct = {
      def memberExistsIn (members: AnonStruct.Members) (member: AnonStruct.Member): Boolean =
        members.get(member._1) match {
          case Some(t) => member._2.isConvertibleTo(t)
          case None => false
        }
      pair match {
        case AnonStruct(members1) -> AnonStruct(members2) => 
          members1.forall(memberExistsIn(members2) _)
        case (anonStruct1 @ AnonStruct(_)) -> Struct(_, anonStruct2) =>
          anonStruct1.isConvertibleTo(anonStruct2)
        case _ -> Struct(_, AnonStruct(members)) =>
            t1.isPromotableToStruct && members.values.forall(t1.isConvertibleTo(_))
        case _ => false
      }
    }
    areIdentical(t1, t2) ||
    toNumeric ||
    toArray ||
    toStruct
  }

}
