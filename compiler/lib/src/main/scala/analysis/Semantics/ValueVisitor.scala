package fpp.compiler.analysis

/** Visit a Value */
trait ValueVisitor {

  type In

  type Out

  def absType(in: In, v: Value.AbsType): Out = default(in, v)

  def anonArray(in: In, v: Value.AnonArray): Out = default(in, v)

  def anonStruct(in: In, v: Value.AnonStruct): Out = default(in, v)

  def array(in: In, v: Value.Array): Out = default(in, v)

  def boolean(in: In, v: Value.Boolean): Out = default(in, v)

  def default(in: In, v: Value): Out

  def enumConstant(in: In, v: Value.EnumConstant): Out = default(in, v)

  def float(in: In, v: Value.Float): Out = default(in, v)

  def integer(in: In, v: Value.Integer): Out = default(in, v)

  def primitiveInt(in: In, v: Value.PrimitiveInt): Out = default(in, v)

  def string(in: In, v: Value.String): Out = default(in, v)

  def struct(in: In, v: Value.Struct): Out = default(in, v)

  def value(in: In, v: Value): Out = matchValue(in, v)

  final def matchValue(in: In, v: Value): Out = {
    v match {
      case v : Value.PrimitiveInt => primitiveInt(in, v)
      case v : Value.Integer => integer(in, v)
      case v : Value.Float => float(in, v)
      case v : Value.Boolean => boolean(in, v)
      case v : Value.String => string(in, v)
      case v : Value.AnonArray => anonArray(in, v)
      case v : Value.AbsType => absType(in, v)
      case v : Value.Array => array(in, v)
      case v : Value.EnumConstant => enumConstant(in, v)
      case v : Value.AnonStruct => anonStruct(in, v)
      case v : Value.Struct => struct(in, v)
    }
  }

}
