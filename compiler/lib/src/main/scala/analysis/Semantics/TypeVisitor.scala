package fpp.compiler.analysis

/** Visit a type */
trait TypeVisitor {

  def absType(in: In, t: Type.AbsType): Out = default(in)

  def anonArray(in: In, t: Type.AnonArray): Out = default(in)

  def anonStruct(in: In, t: Type.AnonStruct): Out = default(in)

  def array(in: In, t: Type.Array): Out = default(in)

  def boolean(in: In) = default(in)

  def default(in: In): Out

  def enum(in: In, t: Type.Enum): Out = default(in)

  def float(in: In, t: Type.Float): Out = default(in)

  def integer(in: In): Out = default(in)

  def primitiveInt(in: In, t: Type.PrimitiveInt): Out = default(in)

  def string(in: In) = default(in)

  def struct(in: In, t: Type.Struct): Out = default(in)

  def ty(in: In, t: Type): Out = matchType(in, t)

  final def matchType(in: In, t: Type): Out =
    t match {
      case t @ Type.AbsType(_) => absType(in, t)
      case t @ Type.AnonArray(_, _) => anonArray(in, t)
      case t @ Type.AnonStruct(_) => anonStruct(in, t)
      case t @ Type.Array(_, _, _) => array(in, t)
      case Type.Boolean => boolean(in)
      case t @ Type.Enum(_, _, _) => enum(in, t)
      case t @ Type.Float(_) => float(in, t)
      case Type.Integer => integer(in)
      case t @ Type.PrimitiveInt(_) => primitiveInt(in, t)
      case Type.String => string(in)
      case t @ Type.Struct(_, _, _) => struct(in, t)
      case _ => default(in)
    }

  type In

  type Out

}
