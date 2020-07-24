package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis._
import fpp.compiler.util._

/** Write an FPP value as XML */
object ValueXmlWriter {

  private object Visitor extends ValueVisitor {

    type In = XmlWriterState

    type Out = String

    override def absType(in: In, v: Value.AbsType): Out = TypeXmlWriter.getName(in, v.getType) ++ "()"

    override def array(in: In, v: Value.Array): Out = {
      val elements = v.anonArray.elements.map(getValue(in, _))
      val stringify = elements.mkString(", ")
      TypeXmlWriter.getName(in, v.getType) ++ "(" ++ stringify ++ ")"
    }

    override def boolean(in: In, v: Value.Boolean) = v.value.toString

    override def default(in: In, v: Value) = throw new InternalError("visitor not defined")

    override def enumConstant(in: In, v: Value.EnumConstant): Out = {
      TypeXmlWriter.getName(in, v.getType) ++ "::" ++ v.value._1.toString
    }

    override def float(in: In, v: Value.Float): Out = v.value.toString

    override def integer(in: In, v: Value.Integer): Out = v.value.toString

    override def primitiveInt(in: In, v: Value.PrimitiveInt) = v.value.toString

    override def string(in: In, v: Value.String) = "\"" ++ v.value.toString ++ "\""
    
    override def struct(in: In, v: Value.Struct): Out = {
      val structType = v.getType
      val data = structType.node._2.getData
      val namesList = data.members
      val memberNames = namesList.map(_._2.getData.name)
      val membersMap = v.anonStruct.members
      val members = memberNames.map(membersMap.get(_).get)
      val memberValues = members.map(getValue(in, _))
      val aggregate = memberValues.mkString(", ")
      TypeXmlWriter.getName(in, v.getType) ++ "(" ++ aggregate ++ ")"
    }

  }
 
  /** Get the c++ value for a type */
  def getValue(s: XmlWriterState, v: Value): String = {
    Visitor.value(s, v)
  }
}