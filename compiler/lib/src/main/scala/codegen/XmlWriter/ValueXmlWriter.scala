package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis._
import fpp.compiler.util._

/** Write an FPP value as XML */
object ValueXmlWriter {

  private object Visitor extends ValueVisitor {

    type In = XmlWriterState

    type Out = String

    override def absType(s: XmlWriterState, v: Value.AbsType) = {
      val aNode = v.t.node
      val cppName = s.writeSymbol(Symbol.AbsType(aNode))
      s.builtInTypes.get(cppName) match {
        case Some(v) => getValue(s, v)
        case None => TypeXmlWriter.getName(s, v.getType) ++ "()"
      }
    }

    override def array(s: XmlWriterState, v: Value.Array) = {
      val elements = v.anonArray.elements.map(getValue(s, _))
      val stringify = elements.mkString(", ")
      TypeXmlWriter.getName(s, v.getType) ++ "(" ++ stringify ++ ")"
    }

    override def boolean(s: XmlWriterState, v: Value.Boolean) = v.value.toString

    override def default(s: XmlWriterState, v: Value) =
      throw new InternalError("visitor not defined")

    override def enumConstant(s: XmlWriterState, v: Value.EnumConstant) =
      TypeXmlWriter.getName(s, v.getType) ++ "::" ++ v.value._1.toString

    override def float(s: XmlWriterState, v: Value.Float) = {
      val s = v.value.toString
      v.kind match {
        case Type.Float.F32 => s"${s}f"
        case Type.Float.F64 => s
      }
    }

    override def integer(s: XmlWriterState, v: Value.Integer) = v.value.toString

    override def primitiveInt(s: XmlWriterState, v: Value.PrimitiveInt) = v.value.toString

    override def string(s: XmlWriterState, v: Value.String) =
      "\"" ++ 
      v.value.toString.replaceAll("\\\\", "\\\\\\\\").
        replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\n") ++ 
      "\""

    override def struct(s: XmlWriterState, v: Value.Struct) = {
      val structType = v.getType
      val data = structType.node._2.data
      val namesList = data.members
      val memberNames = namesList.map(_._2.data.name)
      val membersMap = v.anonStruct.members
      val members = memberNames.map(membersMap.get(_).get)
      val memberValues = members.map(getValue(s, _))
      val aggregate = memberValues.mkString(", ")
      TypeXmlWriter.getName(s, v.getType) ++ "(" ++ aggregate ++ ")"
    }

  }

  /** Get the c++ value for a type */
  def getValue(s: XmlWriterState, v: Value): String = {
    Visitor.value(s, v)
  }

}
