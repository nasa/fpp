package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis._
import fpp.compiler.util._

/** Write an FPP value as C++ */
object ValueCppWriter {

  private object Visitor extends ValueVisitor {

    type In = CppWriterState

    type Out = String

    override def absType(s: CppWriterState, v: Value.AbsType) = {
      val aNode = v.t.node
      TypeCppWriter.getName(s, v.getType) ++ "()"
    }

    override def array(s: CppWriterState, v: Value.Array) = {
      def useSingleElement(elements: List[Value]) =
        elements.tail.forall(_ == elements.head)
      val name = TypeCppWriter.getName(s, v.getType)
      val args = {
        val elements = v.anonArray.elements
        if useSingleElement(elements)
          then write(s, elements.head)
          else s"{${elements.map(write(s, _)).mkString(", ")}}"
      }
      s"$name($args)"
    }

    override def boolean(s: CppWriterState, v: Value.Boolean) = v.value.toString

    override def default(s: CppWriterState, v: Value) =
      throw new InternalError("visitor not defined")

    override def enumConstant(s: CppWriterState, v: Value.EnumConstant) =
      TypeCppWriter.getName(s, v.getType) ++ "::" ++ v.value._1.toString

    override def float(s: CppWriterState, v: Value.Float) = {
      val s = v.value.toString
      v.kind match {
        case Type.Float.F32 => s"${s}f"
        case Type.Float.F64 => s
      }
    }

    override def integer(s: CppWriterState, v: Value.Integer) = v.value.toString

    override def primitiveInt(s: CppWriterState, v: Value.PrimitiveInt) = v.value.toString

    override def string(s: CppWriterState, v: Value.String) =
      "Fw::String(\"" ++
      v.value.toString.replaceAll("\\\\", "\\\\\\\\").
        replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\n") ++
      "\")"

    override def struct(s: CppWriterState, v: Value.Struct) = {
      val aggregate = writeStructMembers(s, v)
      TypeCppWriter.getName(s, v.getType) ++ "(" ++ aggregate ++ ")"
    }

  }

  /** Write struct members as a comma-separated list of C++ values */
  def writeStructMembers(s: CppWriterState, v: Value.Struct) = {
    val structType = v.getType
    val data = structType.node._2.data
    val namesList = data.members
    val memberNames = namesList.map(_._2.data.name)
    val membersMap = v.anonStruct.members
    val members = memberNames.map(membersMap(_))
    val memberValues = members.map(write(s, _))
    memberValues.mkString(", ")
  }

  /** Write a C++ value */
  def write(s: CppWriterState, v: Value): String = {
    Visitor.value(s, v)
  }

}
