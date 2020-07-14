package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for constant definitions 
 *  Writes only primitive values, enum values, and strings (no structs or arrays) */
object ConstantCppWriter extends AstVisitor with LineUtils {

  override def default(s: CppWriterState) = Nil

  override def defConstantAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) = {
    val node = aNode._2
    val loc = Locations.get(node.getId)
    val data = node.getData
    val value = s.a.valueMap(node.getId)
    val (hppLines, cppLines) = value match {
      case Value.Boolean(b) => writeBooleanConstant(data.name, b.toString)
      case Value.EnumConstant(e, _) => writeIntConstant(data.name, e._2.toString)
      case Value.Integer(i) => writeIntConstant(data.name, i.toString)
      case Value.Float(f, _) => writeFloatConstant(data.name, f.toString)
      case Value.String(s) => writeStringConstant(data.name, s)
      case Value.PrimitiveInt(i, _) => writeIntConstant(data.name, i.toString)
      case _ => (Nil, Nil)
    }
    val hppMemberList = {
      hppLines match {
        case Nil => Nil
        case _ => {
          val ls = (Line.blank :: AnnotationCppWriter.writePreComment(aNode)) ++ hppLines
          List(CppWriter.linesMember(ls))
        }
      }
    }
    val cppMemberList = {
      cppLines match {
        case Nil => Nil
        case _ => {
          val ls = Line.blank :: cppLines
          List(CppWriter.linesMember(ls, CppDoc.Lines.Cpp))
        }
      }
    }
    hppMemberList ++ cppMemberList
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
    (
      lines("extern const bool " ++ name ++ ";"),
      lines("const bool " ++ name ++ " = " ++ value ++ ";")
    )

  private def writeIntConstant(name: String, value: String) = {
    val hppLines = {
      val defLine = line(name ++ " = " ++ value)
      List(
        line("enum FppConstant_" ++ name ++ " {"),
        indentIn(defLine),
        line("};")
      )
    }
    (hppLines, Nil)
  }

  private def writeFloatConstant(name: String, value: String) =
    (
      lines("extern const F64 " ++ name ++ ";"),
      lines("const F64 " ++ name ++ " = " ++ value ++ ";")
    )

  private def writeStringConstant(name: String, value: String) = {
    val s = value.replaceAll("\"", "\\\"").replaceAll("\n", "\\n")
    (
      lines("extern const char *const " ++ name ++ ";"),
      lines("const char *const " ++ name ++ " = \"" ++ s ++ "\";")
    )
  }

  type In = CppWriterState

  type Out = List[CppDoc.Member]

}
