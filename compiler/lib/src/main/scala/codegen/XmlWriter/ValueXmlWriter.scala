<<<<<<< HEAD
<<<<<<< HEAD
package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis._
=======
ackage fpp.compiler.codegen

import fpp.compiler.ast._
>>>>>>> FPP to XML tool array feature minus default values
=======
package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis._
>>>>>>> Pre-merge valuevisitor changes
import fpp.compiler.util._

/** Write an FPP value as XML */
object ValueXmlWriter {

  private object Visitor extends ValueVisitor {

    type In = XmlWriterState

    type Out = String
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> Pre-merge valuevisitor changes

    override def absType(in: In, v: Value.AbsType): Out = v.getType.toString

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
      val members = v.anonStruct.members.map(_._2)
      val memberValues = members.map(getValue(in, _))
      val aggregate = memberValues.mkString(", ")
      TypeXmlWriter.getName(in, v.getType) ++ "(" ++ aggregate ++ ")"
    }

  }

  /** Gets the c++ value and appends the type name if is array/struct */
  /**def listGetValue(s: XmlWriterState, typ: AstNode[Ast.TypeName], v: Value): String = {
    val valueString = getValue(s, v)
    val taggedString = v match {
      case v: Value.Struct => TypeXmlWriter.getName(s, typ) ++ valueString
      case v: Value.Array => TypeXmlWriter.getName(s, typ) ++ valueString
      case v: Value.EnumConstant => TypeXmlWriter.getName(s, typ) ++ "::" + valueString
      case _ => valueString
    }
    taggedString
  }*/

  /** Get the c++ value for a type */
  def getValue(s: XmlWriterState, v: Value): String = {
    Visitor.value(s, v)
<<<<<<< HEAD
  }

=======
  }
>>>>>>> FPP to XML tool array feature minus default values
=======
  }

>>>>>>> Pre-merge valuevisitor changes
}