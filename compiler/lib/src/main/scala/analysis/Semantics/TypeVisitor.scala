package fpp.compiler.analysis

/** Visit a type */
trait TypeVisitor {

  type In

  type Out

  def absType(in: In, t: Type.AbsType): Out = default(in, t)

  def anonArray(in: In, t: Type.AnonArray): Out = default(in, t)

  def anonStruct(in: In, t: Type.AnonStruct): Out = default(in, t)

  def array(in: In, t: Type.Array): Out = default(in, t)

  def boolean(in: In): Out = default(in, Type.Boolean)

  def default(in: In, t: Type): Out

  def enumeration(in: In, t: Type.Enum): Out = default(in, t)

  def float(in: In, t: Type.Float): Out = default(in, t)

  def integer(in: In): Out = default(in, Type.Integer)

  def primitiveInt(in: In, t: Type.PrimitiveInt): Out = default(in, t)

  def string(in: In, t: Type.String): Out = default(in, t)

  def struct(in: In, t: Type.Struct): Out = default(in, t)

  def ty(in: In, t: Type): Out = matchType(in, t)

  final def matchType(in: In, t: Type): Out =
    t match {
      case t : Type.AbsType => absType(in, t)
      case t : Type.AnonArray => anonArray(in, t)
      case t : Type.AnonStruct => anonStruct(in, t)
      case t : Type.Array => array(in, t)
      case Type.Boolean => boolean(in)
      case t : Type.Enum => enumeration(in, t)
      case t : Type.Float => float(in, t)
      case Type.Integer => integer(in)
      case t : Type.PrimitiveInt => primitiveInt(in, t)
      case t : Type.String => string(in, t)
      case t : Type.Struct => struct(in, t)
    }
}
