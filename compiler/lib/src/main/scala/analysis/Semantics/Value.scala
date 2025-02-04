package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import java.lang

/** An FPP value */
sealed trait Value {

  /** Add two values */
  final def +(v: Value): Option[Value] = {
    def intOp(v1: BigInt, v2: BigInt) = v1 + v2
    def doubleOp(v1: Double, v2: Double) = v1 + v2
    binop(Value.Binop(intOp, doubleOp))(v)
  }

  /** Check whether a value is zero for purposes of division */
  def isZero: Boolean = false

  /** Convert this value to a distinct type */
  def convertToDistinctType(t: Type): Option[Value] = None

  /** Convert this value to a type */
  final def convertToType(t: Type): Option[Value] = {
    if (Type.areIdentical(getType, t))
      Some(this)
    else convertToDistinctType(t)
  }

  /** Divide one value by another */
  final def /(v: Value): Option[Value] = {
    def intOp(v1: BigInt, v2: BigInt) = v1 / v2
    def doubleOp(v1: Double, v2: Double) = v1 / v2
    binop(Value.Binop(intOp, doubleOp))(v)
  }

  /** Generic binary operation */
  private[analysis] def binop(op: Value.Binop)(v: Value): Option[Value] = None

  /** Get the type of the value */
  def getType: Type

  /** Multiply two values */
  final def *(v: Value): Option[Value] = {
    def intOp(v1: BigInt, v2: BigInt) = v1 * v2
    def doubleOp(v1: Double, v2: Double) = v1 * v2
    binop(Value.Binop(intOp, doubleOp))(v)
  }

  /** Negate a value */
  def unary_- : Option[Value] = None

  /** Promote this value to an array, anonymous array, struct, or anonymous struct */
  final def promoteToAggregate(t: Type): Option[Value] = {
    def promoteToAnonArray(anonArray: Type.AnonArray): Option[Value.AnonArray] = {
      if (this.getType.isPromotableToArray)
        for {
          size <- anonArray.size
          elt <- this.convertToType(anonArray.eltType)
        }
        yield Value.AnonArray(List.fill(size)(elt))
      else None
    }
    def promoteToArray(array: Type.Array): Option[Value.Array] =
      for (anonArray <- promoteToAnonArray(array.anonArray))
        yield Value.Array(anonArray, array)
    def promoteToAnonStruct(anonStruct: Type.AnonStruct): Option[Value.AnonStruct] = {
      def promote(in: List[Type.Struct.Member], out: Value.Struct.Members): Option[Value.AnonStruct] =
        in match {
          case Nil => Some(Value.AnonStruct(out))
          case (m -> t) :: tail => this.convertToType(t) match {
            case Some(v) => promote(tail, out + (m -> v))
            case None => None
          }
        }
      if (this.getType.isPromotableToStruct)
        promote(anonStruct.members.toList, Map())
      else None
    }
    def promoteToStruct(struct: Type.Struct): Option[Value.Struct] =
      for (anonStruct <- promoteToAnonStruct(struct.anonStruct))
        yield Value.Struct(anonStruct, struct)
    t match {
      case anonArray : Type.AnonArray => promoteToAnonArray(anonArray)
      case array : Type.Array => promoteToArray(array)
      case anonStruct : Type.AnonStruct => promoteToAnonStruct(anonStruct)
      case struct : Type.Struct => promoteToStruct(struct)
      case _ => None
    }
  }

  /** Subtract one value from another */
  final def -(v: Value): Option[Value] = {
    def intOp(v1: BigInt, v2: BigInt) = v1 - v2
    def doubleOp(v1: Double, v2: Double) = v1 - v2
    binop(Value.Binop(intOp, doubleOp))(v)
  }

  /** Truncate the value based on the width of its type */
  def truncate: Value = this

}

object Value {

  /** Primitive integer values */
  case class PrimitiveInt(value: BigInt, kind: Type.PrimitiveInt.Kind) 
    extends Value
  {

    override private[analysis] def binop(op: Binop)(v: Value) = v match {
      case PrimitiveInt(value1, kind1) => {
        val result1 = op.intOp(value, value1)
        val result2 = if (kind1 == kind) PrimitiveInt(result1, kind) else Integer(result1)
        Some(result2)
      }
      case Integer(value1) => Some(Integer(op.intOp(value, value1)))
      case Float(value1, kind1) => {
        val result = op.doubleOp(value.toFloat, value1)
        Some(Float(result.toFloat, Type.Float.F64))
      }
      case enumConstant : EnumConstant =>
        binop(op)(enumConstant.convertToRepType)
      case _ => None
    }

    override def convertToDistinctType(t: Type) =
      t match {
        case Type.PrimitiveInt(kind1) => Some(PrimitiveInt(value, kind1))
        case Type.Integer => Some(Integer(value))
        case Type.Float(kind1) => Some(Float(value.doubleValue, kind1))
        case _ => promoteToAggregate(t)
      }

    override def getType = Type.PrimitiveInt(kind)

    override def isZero = (value == 0)

    override def toString = value.toString + ": " + kind.toString

    override def truncate: PrimitiveInt = {
      def truncateUnsigned(v: BigInt, shiftAmt: Int) = {
        val modulus = BigInt(1) << shiftAmt
        val truncated = v % modulus
        if (truncated < 0) truncated + modulus else truncated
      }
      val v = kind match {
        case Type.PrimitiveInt.I8 => BigInt(value.toByte)
        case Type.PrimitiveInt.I16 => BigInt(value.toShort)
        case Type.PrimitiveInt.I32 => BigInt(value.toInt)
        case Type.PrimitiveInt.I64 => BigInt(value.toLong)
        case Type.PrimitiveInt.U8 => truncateUnsigned(value, 8)
        case Type.PrimitiveInt.U16 => truncateUnsigned(value, 16)
        case Type.PrimitiveInt.U32 => truncateUnsigned(value, 32)
        case Type.PrimitiveInt.U64 => truncateUnsigned(value, 64)
      }
      PrimitiveInt(v, kind)
    }

    override def unary_- = Some(PrimitiveInt(-value, kind))

  }

  /** Integer values */
  case class Integer(value: BigInt) extends Value {
    
    def fitsInU64Width: scala.Boolean = {
      val u64Bound = BigInt(1) << 64
      (value >= - (u64Bound / 2) && value < u64Bound)
    }

    override private[analysis] def binop(op: Binop)(v: Value) = v match {
      case PrimitiveInt(value1, kind1) => {
        val result = op.intOp(value, value1)
        Some(Integer(result))
      }
      case Integer(value1) => Some(Integer(op.intOp(value, value1)))
      case Float(value1, kind1) => {
        val result = op.doubleOp(value.toFloat, value1)
        Some(Float(result.toFloat, Type.Float.F64))
      }
      case enumConstant : EnumConstant =>
        binop(op)(enumConstant.convertToRepType)
      case _ => None
    }

    override def convertToDistinctType(t: Type) =
      t match {
        case Type.PrimitiveInt(kind1) => Some(PrimitiveInt(value, kind1))
        case Type.Integer => Some(Integer(value))
        case Type.Float(kind1) => Some(Float(value.doubleValue, kind1))
        case _ => promoteToAggregate(t)
      }

    override def getType = Type.Integer

    override def isZero = (value == 0)

    override def toString = value.toString

    override def unary_- = Some(Integer(-value))

  }

  /** Floating-point values */
  case class Float(value: Double, kind: Type.Float.Kind) extends Value {

    override private[analysis] def binop(op: Binop)(v: Value) = v match {
      case PrimitiveInt(value1, kind1) => {
        val result = op.doubleOp(value, value1.toDouble)
        Some(Float(result.toFloat, Type.Float.F64))
      }
      case Integer(value1) => {
        val result = op.doubleOp(value, value1.toDouble)
        Some(Float(result.toDouble, Type.Float.F64))
      }
      case Float(value1, kind1) => {
        val result1 = op.doubleOp(value, value1)
        val result2 = 
          if (kind1 == kind) Float(result1, kind) 
          else Float(result1, Type.Float.F64)
        Some(result2)
      }
      case enumConstant : EnumConstant =>
        binop(op)(enumConstant.convertToRepType)
      case _ => None
    }

    override def isZero = (Math.abs(value) < Float.EPSILON)

    override def convertToDistinctType(t: Type) =
      t match {
        case Type.PrimitiveInt(kind1) => Some(PrimitiveInt(value.intValue, kind1))
        case Type.Integer => Some(Integer(value.intValue))
        case Type.Float(kind1) => Some(Float(value, kind1))
        case _ => promoteToAggregate(t)
      }
    override def getType = Type.Float(kind)

    override def toString = value.toString + ": " + kind.toString

    override def truncate = kind match {
      case Type.Float.F32 => Float(value.toFloat, kind)
      case Type.Float.F64 => this
    }

    override def unary_- = Some(Float(-value, kind))

  }

  object Float {

    /** Epsilon for nearness to zero */
    val EPSILON = 0.0000001

  }

  /** Boolean values */
  case class Boolean(value: scala.Boolean) extends Value {

    override def convertToDistinctType(t: Type) = promoteToAggregate(t)

    override def getType = Type.Boolean

    override def toString = value.toString

  }

  /** String values */
  case class String(value: java.lang.String) extends Value {

    override def convertToDistinctType(t: Type) =
      t match {
        case Type.String(_) => Some(this)
        case _ => promoteToAggregate(t)
      }

    override def getType = Type.String(None)

    override def toString = "\"" + value.toString + "\""

  }

  /** Anonymous array values */
  case class AnonArray(elements: List[Value]) extends Value {

    def convertToAnonArray(anonArrayType: Type.AnonArray): Option[Value.AnonArray] = {
      def convertElements(in: List[Value], t: Type, out: List[Value]): Option[List[Value]] =
        in match {
          case Nil => Some(out.reverse)
          case head :: tail => head.convertToType(t) match {
            case Some(v) => convertElements(tail, t, v :: out)
            case None => None
          }
        }
      val Type.AnonArray(size, eltType) = anonArrayType
      if (Type.Array.sizesMatch(Some(elements.size), size))
        for (elements <- convertElements(elements, eltType, Nil))
          yield AnonArray(elements)
      else None
    }

    def convertToArray(arrayType: Type.Array): Option[Value.Array] = {
      val Type.Array(_, anonArrayType, _, _) = arrayType
      for (anonArray <- convertToAnonArray(anonArrayType))
        yield Array(anonArray, arrayType)
    }

    override def convertToDistinctType(t: Type) =
      t match {
        case anonArrayType : Type.AnonArray => convertToAnonArray(anonArrayType)
        case arrayType : Type.Array => convertToArray(arrayType)
        case _ => None
      }

    override def getType = Type.AnonArray(Some(elements.size), elements.head.getType)

    override def toString = "[ " ++ elements.mkString(", ") ++ " ]"

    override def truncate: AnonArray = AnonArray(elements.map(_.truncate))

  }

  /** An abstract type */
  case class AbsType(t: Type.AbsType) extends Value {
    
    override def getType = t

    override def toString = s"value of type $t"

  }

  /** Array values */
  case class Array(anonArray: AnonArray, t: Type.Array) extends Value {

    def convertToAnonArray(anonArrayType: Type.AnonArray): Option[Value.AnonArray] =
      anonArray.convertToAnonArray(anonArrayType)

    def convertToArray(arrayType: Type.Array): Option[Value.Array] =
      anonArray.convertToArray(arrayType)

    override def convertToDistinctType(t: Type) =
      t match {
        case anonArrayType : Type.AnonArray => convertToAnonArray(anonArrayType)
        case arrayType : Type.Array => convertToArray(arrayType)
        case _ => None
      }

    override def getType = t

    override def toString = anonArray.toString ++ ": " ++ t.node._2.data.name

    override def truncate: Array = Array(anonArray.truncate, t)

  }

  /** Enum constant values */
  case class EnumConstant(value: (Name.Unqualified, BigInt), t: Type.Enum) extends Value {

    override private[analysis] def binop(op: Binop)(v: Value) = convertToRepType.binop(op)(v)

    /** Convert the enum to the representation type */
    def convertToRepType: PrimitiveInt = PrimitiveInt(value._2, t.repType.kind)

    override def convertToDistinctType(t: Type) =
      convertToRepType.convertToDistinctType(t) match {
        case Some(v) => Some(v)
        case None => promoteToAggregate(t)
      }

    override def getType = t

    override def isZero = convertToRepType.isZero

    override def toString = value.toString ++ ": " ++ t.node._2.data.name

    override def unary_- = - convertToRepType

  }

  /** Anonymous struct values */
  case class AnonStruct(members: Struct.Members) extends Value {

    def convertToAnonStruct(anonStructType: Type.AnonStruct): Option[Value.AnonStruct] = {
      def convertMembers(in: List[Type.Struct.Member], out: Struct.Members): Option[Struct.Members] =
        in match {
          case Nil => Some(out)
          case (m -> t) :: tail => {
            val vOpt = members.get(m) match {
              case Some(v) => v.convertToType(t)
              case None => t.getDefaultValue
            }
            vOpt match {
              case Some(v) => convertMembers(tail, out + (m -> v))
              case None => None
            }
          }
        }
      for (members <- convertMembers(anonStructType.members.toList, Map()))
        yield Value.AnonStruct(members)
    }

    def convertToStruct(structType: Type.Struct): Option[Value.Struct] = {
      val Type.Struct(_, anonStructType, _, _, _) = structType
      for (anonStruct <- convertToAnonStruct(anonStructType))
        yield Struct(anonStruct, structType)
    }

    override def convertToDistinctType(t: Type) =
      t match {
        case anonStructType : Type.AnonStruct => convertToAnonStruct(anonStructType)
        case structType : Type.Struct => convertToStruct(structType)
        case _ => None
      }

    override def getType = {
      def f(member: Struct.Member): Type.Struct.Member = (member._1, member._2.getType)
      val typeMembers = members.map(f)
      Type.AnonStruct(typeMembers)
    }

    override def toString = {
      def memberToString(member: Struct.Member) = member._1 ++ " = " ++ member._2.toString
      members.size match {
        case 0 => "{ }"
        case _ => "{ " ++ members.map(memberToString).mkString(", ") ++ " }"
      }
    }

    override def truncate: AnonStruct = {
      def f(member: Value.Struct.Member) = {
        val m -> v = member
        m -> v.truncate
      }
      AnonStruct(members.map(f))
    }

  }

  /** Struct values */
  case class Struct(anonStruct: AnonStruct, t: Type.Struct) extends Value {

    def convertToAnonStruct(anonStructType: Type.AnonStruct): Option[Value.AnonStruct] =
      anonStruct.convertToAnonStruct(anonStructType)
    def convertToStruct(structType: Type.Struct): Option[Value.Struct] =
      anonStruct.convertToStruct(structType)
    override def convertToDistinctType(t: Type) =
      t match {
        case anonStructType : Type.AnonStruct => convertToAnonStruct(anonStructType)
        case structType : Type.Struct => convertToStruct(structType)
        case _ => None
      }

    override def getType: Type.Struct = t

    override def toString = anonStruct.toString ++ ": " ++ t.node._2.data.name

    override def truncate = Struct(anonStruct.truncate, t)

  }

  object Struct {

    type Member = (Name.Unqualified, Value)

    type Members = Map[Name.Unqualified, Value]

  }

  /** Binary operations */
  private[analysis] case class Binop(
    /** The integer operation */
    intOp: Binop.Op[BigInt], 
    /** The double-precision floating point operation */
    doubleOp: Binop.Op[Double]
  )

  private[analysis] object Binop {

    /** A binary operation */
    type Op[T] = (T, T) => T

  }

}
