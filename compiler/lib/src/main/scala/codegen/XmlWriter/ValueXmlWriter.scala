package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis._
import fpp.compiler.util._

/** Write an FPP value as XML */
object ValueXmlWriter {

  private object Visitor extends ValueVisitor {

    type In = XmlWriterState

    type Out = String

<<<<<<< HEAD
    override def absType(in: In, v: Value.AbsType): Out = v.getType.toString

    override def array(in: In, v: Value.Array): Out = {
      val elements = v.anonArray.elements.map(getValue(in, _))
      val stringify = elements.mkString(", ")
      TypeXmlWriter.getName(in, v.getType) ++ "(" ++ stringify ++ ")"
=======
    override def absType(in: In, v: Value.AbsType): Out = v.toString

    override def anonArray(in: In, v: Value.AnonArray): Out = {
      val elements = v.elements
      val elementValues = elements.map(getValue(in, _))
      val stringify = elementValues.mkString(", ")
      "(" ++ stringify ++ ")"
    }

    override def anonStruct(in: In, v: Value.AnonStruct): Out = {
      val members = v.members.map(_._2)
      val memberValues = members.map(getValue(in, _))
      val aggregate = memberValues.mkString(", ")
      "(" ++ aggregate ++ ")"
    }

    override def array(in: In, v: Value.Array): Out = {
      val elements = v.anonArray.elements
      val arrayType = v.getType.node._2.getData.eltType
      println(v.getType.node._2.getData.name)
      val elementValues = elements.map(listGetValue(in, arrayType, _))
      val stringify = elementValues.mkString(", ")
      "(" ++ stringify ++ ")"
>>>>>>> 352a8528cd9fbefd92361e9c76f902383a76aba8
    }

    override def boolean(in: In, v: Value.Boolean) = v.value.toString

<<<<<<< HEAD
    override def default(in: In, v: Value) = throw new InternalError("visitor not defined")

    override def enumConstant(in: In, v: Value.EnumConstant): Out = {
      TypeXmlWriter.getName(in, v.getType) ++ "::" ++ v.value._1.toString
    }
=======
    override def enumConstant(in: In, v: Value.EnumConstant): Out = v.value._1.toString
>>>>>>> 352a8528cd9fbefd92361e9c76f902383a76aba8

    override def float(in: In, v: Value.Float): Out = v.value.toString

    override def integer(in: In, v: Value.Integer): Out = v.value.toString

    override def primitiveInt(in: In, v: Value.PrimitiveInt) = v.value.toString

    override def string(in: In, v: Value.String) = "\"" ++ v.value.toString ++ "\""
    
    override def struct(in: In, v: Value.Struct): Out = {
<<<<<<< HEAD
      val members = v.anonStruct.members.map(_._2)
      val memberValues = members.map(getValue(in, _))
      val aggregate = memberValues.mkString(", ")
      TypeXmlWriter.getName(in, v.getType) ++ "(" ++ aggregate ++ ")"
    }

=======
      val memberTypes = v.getType.node._2.getData.members.map(_._2.getData.typeName)
      val members = v.anonStruct.members
      val membersZipped = members.zip(memberTypes)
      val memberValues = membersZipped.map{ case (a,b) => listGetValue(in, b, a._2) }
      val aggregate = memberValues.mkString(", ")
      "(" ++ aggregate ++ ")"
    }

    override def default(in: In, v: Value) = throw new InternalError("visitor not defined")

  }

  /** Gets the c++ value and appends the type name if is array/struct */
  def listGetValue(s: XmlWriterState, typ: AstNode[Ast.TypeName], v: Value): String = {
    val valueString = getValue(s, v)
    val taggedString = v match {
      case v: Value.Struct => TypeXmlWriter.getName(s, typ) ++ valueString
      case v: Value.Array => TypeXmlWriter.getName(s, typ) ++ valueString
      case v: Value.EnumConstant => TypeXmlWriter.getName(s, typ) ++ "::" + valueString
      case _ => valueString
    }
    taggedString
>>>>>>> 352a8528cd9fbefd92361e9c76f902383a76aba8
  }

  /** Get the c++ value for a type */
  def getValue(s: XmlWriterState, v: Value): String = {
    Visitor.value(s, v)
  }
<<<<<<< HEAD
=======

>>>>>>> 352a8528cd9fbefd92361e9c76f902383a76aba8
}