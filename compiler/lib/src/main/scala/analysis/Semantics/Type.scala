package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP Type */
sealed trait Type {

  /** Get the array size, if it exists and is known */
  def getArraySize: Option[Type.Array.Size] = None

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

  /** Is this type convertible to type t? */
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

  val I8 = PrimitiveInt(PrimitiveInt.I8)
  val I16 = PrimitiveInt(PrimitiveInt.I16)
  val I32 = PrimitiveInt(PrimitiveInt.I32)
  val I64 = PrimitiveInt(PrimitiveInt.I64)
  val U8 = PrimitiveInt(PrimitiveInt.U8)
  val U16 = PrimitiveInt(PrimitiveInt.U16)
  val U32 = PrimitiveInt(PrimitiveInt.U32)
  val U64 = PrimitiveInt(PrimitiveInt.U64)

  /** Floating-point types */
  case class Float(kind: Float.Kind) extends Type with Primitive {
    override def isFloat = true
    override def toString = kind match {
      case Float.F32 => "F32"
      case Float.F64 => "F64"
    }
  }

  val F32 = Float(Float.F32)
  val F64 = Float(Float.F64)

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
    /** The structurally equivalent anonymous array */
    anonArray: AnonArray
  ) extends Type {
    /** Set the array size */
    def setSize(size: Array.Size) = this.copy(anonArray = anonArray.setSize(size))
    override def getArraySize = anonArray.getArraySize
    override def getDefNodeId = Some(node._2.getId)
    override def hasNumericMembers = anonArray.hasNumericMembers
    override def toString = "array type " ++ node._2.getData.name
  }

  object Array {

    type Size = BigInt

    /** Check whether two array sizes match */
    def sizesMatch(size1: Option[Size], size2: Option[Size]): Boolean = (size1, size2) match {
      case (None, _) => true
      case (_, None) => true
      case (Some(n1), Some(n2)) => n1 == n2
    }

    /** Compute a common array size */
    def commonSize(size1: Option[Size], size2: Option[Size]): Option[Size] = (size1, size2) match {
      case (Some(n1), Some(n2)) => if (n1 == n2) Some(n1) else None
      case _ => None
    }

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
    /** The structurally equivalent anonymous struct type */
    anonStruct: AnonStruct
  ) extends Type {
    override def getDefNodeId = Some(node._2.getId)
    override def hasNumericMembers = anonStruct.hasNumericMembers
    override def toString = "struct type " ++ node._2.getData.name
  }

  object Struct {

    type Member = (Name.Unqualified, Type)

    type Members = Map[Name.Unqualified, Type]

    /** Resolve a member map, generating a new member map */
    def resolveMembers (resolver: Member => Option[Member]) (members: Members): Option[Members] = {
      def helper(
        in: List[Member], 
        out: Members
      ): Option[Members] =
        in match {
          case Nil => Some(out)
          case head :: tail => resolver(head) match {
            case Some(member) => helper(tail, out + member)
            case None => None
          }
        }
      helper(members.toList, Map())
    }

  }

  /** An anonymous array type */
  case class AnonArray(
    /** The array size, if known */
    size: Option[Array.Size],
    /** The element type */
    eltType: Type
  ) extends Type {
    /** Set the array size */
    def setSize(size: Array.Size) = this.copy(size = Some(size))
    override def getArraySize = size
    override def hasNumericMembers = eltType.hasNumericMembers
    override def toString = size match {
      case Some(n) => "[" ++ n.toString ++ "] " ++ eltType.toString
      case None => "array of " ++ eltType.toString
    }
  }
  
  /** An anonymous struct type */
  case class AnonStruct(
    /** The members */
    members: Struct.Members
  ) extends Type {
    override def hasNumericMembers = members.values.forall(_.hasNumericMembers)
    override def toString = {
      def memberToString(member: Struct.Member) = member._1 ++ ": " ++ member._2.toString
      members.size match {
        case 0 => "{ }"
        case _ => "struct { " ++ members.map(memberToString).mkString(", ") ++ " }"
      }
    }
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
  
  /** Check for type convertibility */
  def mayBeConverted(pair: (Type, Type)): Boolean = {
    val t1 -> t2 = pair
    def numeric = t1.isConvertibleToNumeric && t2.isNumeric
    def array = pair match {
      case Array(_, anonArray1) -> _ => anonArray1.isConvertibleTo(t2)
      case _ -> Array(_, anonArray2) => t1.isConvertibleTo(anonArray2)
      case AnonArray(size1, eltType1) -> AnonArray(size2, eltType2) => 
        Array.sizesMatch(size1, size2) &&
        eltType1.isConvertibleTo(eltType2)
      case _ -> AnonArray(_, eltType2) =>
        t1.isPromotableToArray && t1.isConvertibleTo(eltType2)
      case _ => false
    }
    def struct = {
      def memberExistsIn (members: Struct.Members) (member: Struct.Member): Boolean =
        members.get(member._1) match {
          case Some(t) => member._2.isConvertibleTo(t)
          case None => false
        }
      pair match {
        case Struct(_, anonStruct1) -> _ => anonStruct1.isConvertibleTo(t2)
        case _ -> Struct(_, anonStruct2) => t1.isConvertibleTo(anonStruct2)
        case AnonStruct(members1) -> AnonStruct(members2) => 
          members1.forall(memberExistsIn(members2) _)
        case _ -> AnonStruct(members2) =>
            t1.isPromotableToStruct && members2.values.forall(t1.isConvertibleTo(_))
        case _ => false
      }
    }
    areIdentical(t1, t2) ||
    numeric ||
    array ||
    struct
  }

  /** Compute the common type for a pair of types */
  def commonType(t1: Type, t2: Type): Option[Type] = {
    val pair = (t1, t2)
    type Rule = () => Option[Type]
    def selectFirstMatchIn(rules: List[Rule]): Option[Type] = rules match {
      case Nil => None
      case head :: tail => head() match {
        case t @ Some(_) => t
        case _ => selectFirstMatchIn(tail)
      }
    }
    def identical() = areIdentical(t1, t2) match {
      case true => Some(t1)
      case false => None
    }
    def numeric() = 
      if (t1.isFloat && t2.isNumeric) Some(Float(Float.F64))
      else if (t1.isNumeric && t2.isFloat) Some(Float(Float.F64))
      else if (t1.isNumeric && t2.isNumeric) Some(Integer)
      else None
    def enum() = pair match {
      case (Enum(_, repType), _) => commonType(repType, t2)
      case (_, Enum(_, repType)) => commonType(t1, repType)
      case _ => None
    }
    def array() = {
      /** Handle the case of a single anonymous array in either position */
      def singleAnonArray(anonArray: AnonArray, other: Type) = {
        if (other.isPromotableToArray)
          for (eltType <- commonType(other, anonArray.eltType)) 
            yield AnonArray(anonArray.size, eltType)
        else None
      }
      pair match {
        case (_, Array(_, anonArray2)) => commonType(t1, anonArray2)
        case (Array(_, anonArray1), _) => commonType(anonArray1, t2)
        case (AnonArray(size1, eltType1), AnonArray(size2, eltType2)) =>
          if (Array.sizesMatch(size1, size2)) {
            val size = Array.commonSize(size1, size2)
            for (eltType <- commonType(eltType1, eltType2)) yield AnonArray(size, eltType)
          }
          else None
        case _ -> (anonArray @ AnonArray(_, _)) => singleAnonArray(anonArray, t1)
        case (anonArray @ AnonArray(size1, eltType1)) -> _ => singleAnonArray(anonArray, t2)
        case _ => None
      }
    }
    def struct() = {
      /** Handle the case of two anonymous structs */
      def twoAnonStructs(members1: Struct.Members, members2: Struct.Members) = {
        /** Resolve a member of t1 against the corresponding member of t2, if it exists */
        def resolveT1Member(member: Struct.Member): Option[Struct.Member] = {
          val name1 -> ty1 = member
          members2.get(name1) match {
            case Some(ty2) => for (ty <- commonType(ty1, ty2)) yield (name1 -> ty)
            case None => Some(member)
          }
        }
        /** Resolve each member of t1 against the corresponding member of t2, if it exists */
        def resolveT1Members = Struct.resolveMembers (resolveT1Member) _
        for (t1ResolvedMembers <- resolveT1Members(members1)) yield {
          def pred(member: Struct.Member) = !members1.contains(member._1)
          val t2ResolvedMembers = members2.filter(pred)
          AnonStruct(t1ResolvedMembers ++ t2ResolvedMembers)
        }
      }
      /** Handle the case of a single anonymous struct in either position */
      def singleAnonStruct(members: Struct.Members, other: Type) = {
        if (other.isPromotableToStruct) {
          /** Resolve a member of t2 against t1 */
          def resolveMember(member: Struct.Member): Option[Struct.Member] =
            for (t <- commonType(other, member._2)) yield (member._1 -> t)
          /** Resolve all members of t2 against t1 */
          def resolveMembers = Struct.resolveMembers (resolveMember) _
          for (resolvedMembers <- resolveMembers(members))
            yield AnonStruct(resolvedMembers)
        }
        else None
      }
      pair match {
        case (_, Struct(_, anonStruct2)) => commonType(t1, anonStruct2)
        case (Struct(_, anonStruct1), _) => commonType(anonStruct1, t2)
        case AnonStruct(members1) -> AnonStruct(members2) => twoAnonStructs(members1, members2)
        case _ -> AnonStruct(members) => singleAnonStruct(members, t1)
        case AnonStruct(members) -> _ => singleAnonStruct(members, t2)
        case _ => None
      }
    }
    val rules: List[Rule] = List(
      identical,
      numeric,
      enum,
      array,
      struct
    )
    selectFirstMatchIn(rules)
  }

}
