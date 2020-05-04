package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP value */
sealed trait Value {

  /** Convert this value to a distinct type */
  def convertToDistinctType(t: Type): Option[Value] = None

  /** Convert this value to a type */
  final def convertToType(t: Type): Option[Value] = {
    if (Type.areIdentical(getType, t))
      Some(this)
    else convertToDistinctType(t)
  }

  /** Get the type of the value */
  def getType: Type

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
      case anonArray @ Type.AnonArray(_, _) => promoteToAnonArray(anonArray)
      case array @ Type.Array(_, _, _) => promoteToArray(array)
      case anonStruct @ Type.AnonStruct(_) => promoteToAnonStruct(anonStruct)
      case struct @ Type.Struct(_, _, _) => promoteToStruct(struct)
      case _ => None
    }
  }

  def *(v: Value): Option[Value] = None

  def +(v: Value): Option[Value] = None

  def -(v: Value): Option[Value] = None

  def /(v: Value): Option[Result.Result[Value]] = None

  def unary_-(v: Value): Option[Value] = None

}

object Value {

  /** Primitive integer values */
  case class PrimitiveInt(value: BigInt, kind: Type.PrimitiveInt.Kind) 
    extends Value
  {

    override def convertToDistinctType(t: Type) =
      t match {
        case Type.PrimitiveInt(kind1) => Some(PrimitiveInt(value, kind1))
        case Type.Integer => Some(Integer(value))
        case Type.Float(kind1) => Some(Float(value.doubleValue, kind1))
        case _ => promoteToAggregate(t)
      }

    override def getType = Type.PrimitiveInt(kind)

    override def toString = value.toString + ": " + kind.toString

  }

  /** Integer values */
  case class Integer(value: BigInt) extends Value {

    override def convertToDistinctType(t: Type) =
      t match {
        case Type.PrimitiveInt(kind1) => Some(PrimitiveInt(value, kind1))
        case Type.Integer => Some(Integer(value))
        case Type.Float(kind1) => Some(Float(value.doubleValue, kind1))
        case _ => promoteToAggregate(t)
      }

    override def getType = Type.Integer

    override def toString = value.toString

  }

  /** Floating-point values */
  case class Float(value: Double, kind: Type.Float.Kind) extends Value {

    override def convertToDistinctType(t: Type) =
      t match {
        case Type.PrimitiveInt(kind1) => Some(PrimitiveInt(value.intValue, kind1))
        case Type.Integer => Some(Integer(value.intValue))
        case Type.Float(kind1) => Some(Float(value, kind1))
        case _ => promoteToAggregate(t)
      }
    override def getType = Type.Float(kind)

    override def toString = value.toString + ": " + kind.toString

  }

  /** Boolean values */
  case class Boolean(value: scala.Boolean) extends Value {

    override def convertToDistinctType(t: Type) = promoteToAggregate(t)

    override def getType = Type.Boolean

    override def toString = value.toString

  }

  /** String values */
  case class String(value: java.lang.String) extends Value {

    override def convertToDistinctType(t: Type) = promoteToAggregate(t)

    override def getType = Type.String

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
      val Type.Array(_, anonArrayType, _) = arrayType
      for (anonArray <- convertToAnonArray(anonArrayType))
        yield Array(anonArray, arrayType)
    }

    override def convertToDistinctType(t: Type) =
      t match {
        case anonArrayType @ Type.AnonArray(_, _) => convertToAnonArray(anonArrayType)
        case arrayType @ Type.Array(_, _, _) => convertToArray(arrayType)
        case _ => None
      }

    override def getType = Type.AnonArray(Some(elements.size), elements.head.getType)

    override def toString = "[ " ++ elements.mkString(", ") ++ " ]"

  }

  /** Array values */
  case class Array(anonArray: AnonArray, t: Type.Array) extends Value {

    def convertToAnonArray(anonArrayType: Type.AnonArray): Option[Value.AnonArray] =
      anonArray.convertToAnonArray(anonArrayType)

    def convertToArray(arrayType: Type.Array): Option[Value.Array] =
      anonArray.convertToArray(arrayType)

    override def convertToDistinctType(t: Type) =
      t match {
        case anonArrayType @ Type.AnonArray(_, _) => convertToAnonArray(anonArrayType)
        case arrayType @ Type.Array(_, _, _) => convertToArray(arrayType)
        case _ => None
      }

    override def getType = t

    override def toString = anonArray.toString ++ ": " ++ t.node._2.getData.name

  }

  /** Enum constant values */
  case class EnumConstant(value: BigInt, t: Type.Enum) extends Value {

    override def convertToDistinctType(t: Type) = t match {
      case Type.PrimitiveInt(kind1) => Some(PrimitiveInt(value, kind1))
      case Type.Integer => Some(Integer(value))
      case Type.Float(kind1) => Some(Float(value.doubleValue, kind1))
      case _ => promoteToAggregate(t)
    }

    override def getType = t

    override def toString = value.toString ++ ": " ++ t.node._2.getData.name

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
              case None => Some(t.getDefaultValue)
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
      val Type.Struct(_, anonStructType, _) = structType
      for (anonStruct <- convertToAnonStruct(anonStructType))
        yield Struct(anonStruct, structType)
    }

    override def convertToDistinctType(t: Type) =
      t match {
        case anonStructType @ Type.AnonStruct(_) => convertToAnonStruct(anonStructType)
        case structType @ Type.Struct(_, _, _) => convertToStruct(structType)
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

  }

  /** Struct values */
  case class Struct(anonStruct: AnonStruct, t: Type.Struct) extends Value {

    def convertToAnonStruct(anonStructType: Type.AnonStruct): Option[Value.AnonStruct] =
      anonStruct.convertToAnonStruct(anonStructType)
    def convertToStruct(structType: Type.Struct): Option[Value.Struct] =
      anonStruct.convertToStruct(structType)
    override def convertToDistinctType(t: Type) =
      t match {
        case anonStructType @ Type.AnonStruct(_) => convertToAnonStruct(anonStructType)
        case structType @ Type.Struct(_, _, _) => convertToStruct(structType)
        case _ => None
      }

    override def getType = t

    override def toString = anonStruct.toString ++ ": " ++ t.node._2.getData.name

  }

  object Struct {

    type Member = (Name.Unqualified, Value)

    type Members = Map[Name.Unqualified, Value]

  }

}
