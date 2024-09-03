package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for constant definitions 
 *  Writes only primitive values, enum values, and strings (no structs or arrays) */
object ConstantCppWriter extends CppWriterUtils {

  /** Writes out constant hpp and cpp */
  def write(s: CppWriterState, tuList: List[Ast.TransUnit]): Result.Result[Any] =
    tuList.flatMap(Visitor.transUnit(s, _)) match {
      case Nil => Right(())
      case constantMembers => 
        val fileName = ComputeCppFiles.FileNames.getConstants
        val hppHeaderLines = {
          val headers = List("FpConfig.hpp")
          Line.blank :: headers.map(CppWriter.headerLine)
        }
        val cppHeaderLines = {
          val path = s.getRelativePath(s"$fileName.hpp")
          val headers = List(path.toString)
          Line.blank :: headers.map(CppWriter.headerLine)
        }
        val members = linesMember(hppHeaderLines) ::
          linesMember(cppHeaderLines, CppDoc.Lines.Cpp) ::
          constantMembers
        val includeGuard = s.includeGuardFromPrefix(fileName)
        val cppDoc = CppWriter.createCppDoc(
          "FPP constants",
          fileName,
          includeGuard,
          members,
          s.toolName
        )
        CppWriter.writeCppDoc(s, cppDoc)
      }

  private object Visitor extends AstVisitor with LineUtils {

    type In = CppWriterState

    type Out = List[CppDoc.Member]

    override def default(s: CppWriterState) = Nil

    override def defConstantAnnotatedNode(
      s: CppWriterState,
      aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
    ) = {
      val node = aNode._2
      val data = node.data
      val value = s.a.valueMap(node.id)
      val name = s.getName(Symbol.Constant(aNode))
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
            List(linesMember(ls))
          }
        }
      }
      val cppMemberList = {
        cppLines match {
          case Nil => Nil
          case _ => {
            val ls = Line.blank :: cppLines
            List(linesMember(ls, CppDoc.Lines.Cpp))
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
      members match {
        case Nil => Nil
        case _ =>
          val namespace = namespaceMember(data.name, members)
          List(namespace)
      }
    }

    override def defComponentAnnotatedNode(
      s: CppWriterState,
      aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
    ) = {
      val (_, node, _) = aNode
      val data = node.data
      val members = data.members.flatMap(matchComponentMember(s, _))
      members
    }

    override def transUnit(s: CppWriterState, tu: Ast.TransUnit) =
      tu.members.flatMap(matchTuMember(s, _))

    private def writeBooleanConstant(name: String, value: String) =
      (
        lines(s"extern const bool $name;"),
        lines(s"const bool $name = $value;")
      )

    private def writeIntConstant(name: String, value: String) = {
      val hppLines = {
        val defLine = line(s"$name = $value")
        List(
          line(s"enum FppConstant_$name {"),
          indentIn(defLine),
          line("};")
        )
      }
      (hppLines, Nil)
    }

    private def writeFloatConstant(name: String, value: String) =
      (
        lines(s"extern const F64 $name;"),
        lines(s"const F64 $name = $value;")
      )

    private def writeStringConstant(name: String, value: String) = {
      val s = value.replaceAll("\\\\", "\\\\\\\\").
        replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\n")
      val q = "\""
      (
        lines(s"extern const char *const $name;"),
        lines(s"const char *const $name = $q$s$q;")
      )
    }

  }

}
