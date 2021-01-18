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
    val loc = Locations.get(node.id)
    val data = node.data
    val value = s.a.valueMap(node.id)
    val name = s.addNamePrefix(data.name)
    val (hppLines, cppLines) = value match {
      case Value.Boolean(b) => writeBooleanConstant(name, b.toString)
      case Value.EnumConstant(e, _) => writeIntConstant(name, e._2.toString)
      case Value.Integer(i) => writeIntConstant(name, i.toString)
      case Value.Float(f, _) => writeFloatConstant(name, f.toString)
      case Value.String(s) => writeStringConstant(name, s)
      case Value.PrimitiveInt(i, _) => writeIntConstant(name, i.toString)
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
    val data = node.data
    val members = data.members.flatMap(matchModuleMember(s, _))
    val namespace = CppWriter.namespaceMember(data.name, members)
    List(CppWriter.linesMember(List(Line.blank)), namespace)
  }

  override def defComponentAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    // Prefix names in a component with the component name.
    // This is to work around the fact that the F Prime XML
    // provides no way to define constants inside classes.
    val s1 = s.copy(namePrefix = s.addNamePrefix(s"${data.name}_"))
    val members = data.members.flatMap(matchComponentMember(s1, _))
    members
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
    val s = value.replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\n")
    (
      lines("extern const char *const " ++ name ++ ";"),
      lines("const char *const " ++ name ++ " = \"" ++ s ++ "\";")
    )
  }

  type In = CppWriterState

  type Out = List[CppDoc.Member]

}
