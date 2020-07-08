package fpp.compiler.analysis

/** Visit a Value */
trait ValueVisitor {

  def absValue(in: In, t: Value.AbsValue): Out = default(in, t)

  def anonArray(in: In, t: Value.AnonArray): Out = default(in, t)

  def anonStruct(in: In, t: Value.AnonStruct): Out = default(in, t)

  def array(in: In, t: Value.Array): Out = default(in, t)

  def binop(in: In, t: Value.Binop): Out = default(in, t)

  def boolean(in: In) = default(in, Value.Boolean)

  def default(in: In, t: Value): Out

  def enum(in: In, t: Value.Enum): Out = default(in, t)

  def enumConstant(in: In, t: Value.EnumConstant): Out = default(in, t)

  def float(in: In, t: Value.Float): Out = default(in, t)

  def integer(in: In): Out = default(in, Value.Integer)

  def primitiveInt(in: In, t: Value.PrimitiveInt): Out = default(in, t)

  def string(in: In, t: Value.String): Out = default(in, t)

  def struct(in: In, t: Value.Struct): Out = default(in, t)

  def ty(in: In, t: Value): Out = matchValue(in, t)

  final def matchValue(in: In, t: Value): Out =
    t match {
      case t : Value.PrimitiveInt => primitiveInt(in, t)
      case t : Value.Integer => integer(in, t)
      case t : Value.Float => float(in, t)
      case t : Value.Boolean => boolean(in, t)
      case t : Value.String => string(in, t)
      case t : Value.AnonArray => anonArray(in, t)
      case t : Value.AbsValue => absValue(in, t)
      case t : Value.Array => array(in, t)
      case t : Value.EnumConstant => enumConstant(in, t)
      case t : Value.AnonStruct => anonStruct(in, t)
      case t : Value.Struct => struct(in, t)
      case t : Value.Binop => binop(in, t)
      case _ => default(in, t)
    }

  Value In

  Value Out

}
