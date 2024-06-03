package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

// Writes out C++ for state machine instantiations

object StateMachineCppWriter extends CppWriterUtils {

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

    override def defStateMachineAnnotatedNode(
      s: CppWriterState,
      aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
    ) = {
      println(s"**** defStateMachineAnnotedNode !!")
      val hppLines = List(
        line("{"),
        line(s"STATE MACHINE STUFF HERE!!!!"),
        line("};")
      )

      val hppMemberList = {
        hppLines match {
          case Nil => Nil
          case _ => {
            val ls = (Line.blank :: AnnotationCppWriter.writePreComment(aNode)) ++ hppLines
            List(linesMember(ls))
          }
        }
      }

      hppMemberList

    }


  }

}
