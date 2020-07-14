package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for constant definitions */
object ConstantCppWriter extends AstVisitor with LineUtils {

  override def default(s: CppWriterState) = Nil

  override def defConstantAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) = {
    val node = aNode._2
    val loc = Locations.get(node.getId)
    val data = node.getData
    val ty = s.a.typeMap(node.getId)
    val value = s.a.valueMap(node.getId)
    val constantLines = {
      if (ty.isInt) writeIntConstant(data.name, value.toString)
      else if (ty.isFloat) writeFloatConstant(data.name, value.toString)
      else ty match {
        case Type.Boolean => writeBooleanConstant(data.name, value.toString)
        case _: Type.Enum => {
          val Value.EnumConstant(enumValue, _) = value
          val valueString = enumValue._2.toString
          writeIntConstant(data.name, valueString)
        }
        case _: Type.String => {
          val Value.String(s) = value
          writeStringConstant(data.name, s)
        }
        case _ => Nil
      }
    }
    constantLines match {
      case Nil => Nil
      case _ => {
        val ls = (Line.blank :: AnnotationCppWriter.writePreComment(aNode)) ++ constantLines
        List(CppWriter.linesMember(ls))
      }
    }
  }

  override def defModuleAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val data = node.getData
    val members = data.members.flatMap(matchModuleMember(s, _))
    val namespace = CppWriter.namespaceMember(data.name, members)
    List(CppWriter.linesMember(List(Line.blank)), namespace)
  }

  override def transUnit(s: CppWriterState, tu: Ast.TransUnit) =
    tu.members.flatMap(matchTuMember(s, _))

  private def writeBooleanConstant(name: String, value: String) =
    lines("const bool " ++ name ++ " = " ++ value ++ ";")

  private def writeIntConstant(name: String, value: String) = {
    val defLine = line(name ++ " = " ++ value)
    List(line("enum " ++ name ++ " {"), indentIn(defLine), line("};"))
  }

  private def writeFloatConstant(name: String, value: String) =
    lines("const F64 " ++ name ++ " = " ++ value ++ ";")

  private def writeStringConstant(name: String, value: String) = {
    val s = value.replaceAll("\"", "\\\"").replaceAll("\n", "\\n")
    lines("const char *const " ++ name ++ " = \"" ++ s ++ "\";")
  }

  type In = CppWriterState

  type Out = List[CppDoc.Member]

}
