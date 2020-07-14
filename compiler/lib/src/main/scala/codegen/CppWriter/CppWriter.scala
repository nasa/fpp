package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ */
object CppWriter extends AstStateVisitor with LineUtils {

  type State = CppWriterState

  override def defArrayAnnotatedNode(s: CppWriterState, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    // TODO
    default(s)
  }

  override def defModuleAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val data = node.getData
    val a = s.a.copy(scopeNameList = data.name :: s.a.scopeNameList)
    val s1 = s.copy(a = a)
    visitList(s1, data.members, matchModuleMember)
    Right(s)
  }

  override def transUnit(s: CppWriterState, tu: Ast.TransUnit) = 
    visitList(s, tu.members, matchTuMember)

  def tuList(s: CppWriterState, tuList: List[Ast.TransUnit]) = {
    visitList(s, tuList, transUnit)
    writeConstants(s, tuList)
  }

  def createCppDoc(fileName: String, includeGuard: String, members: List[CppDoc.Member]) = {
    val hppFile = CppDoc.HppFile(s"$fileName.hpp", includeGuard)
    CppDoc(hppFile, s"$fileName.cpp", members)
  }

  def headerLine(s: String) = line("#include \"" ++ s ++ "\"")

  def linesMember(content: List[Line], output: CppDoc.Lines.Output = CppDoc.Lines.Hpp) = 
    CppDoc.Member.Lines(CppDoc.Lines(content, output))

  def namespaceMember(name: String, members: List[CppDoc.Namespace.Member]) =
    CppDoc.Member.Namespace(CppDoc.Namespace(name, members))

  private def writeConstants(s: CppWriterState, tuList: List[Ast.TransUnit]) = {
    val fileName = ComputeCppFiles.getConstantsName
    val constantMembers = tuList.flatMap(ConstantCppWriter.transUnit(s, _))
    val hppHeaderLines = {
      val headers = List("Fw/Types/BasicTypes.hpp")
      Line.blank :: headers.map(headerLine)
    }
    val cppHeaderLines = {
      val path = s.getRelativePath(s"$fileName.hpp")
      val headers = List(path.toString)
      Line.blank :: headers.map(headerLine)
    }
    val members = linesMember(hppHeaderLines) :: 
      linesMember(cppHeaderLines, CppDoc.Lines.Cpp) :: 
      constantMembers
    val includeGuard = s.includeGuardFromPrefix(fileName)
    val cppDoc = createCppDoc(fileName, includeGuard, members)
    writeCppDoc(s, cppDoc)
  }

  private def writeCppDoc(s: CppWriterState, cppDoc: CppDoc) = {
    writeHppFile(s, cppDoc)
    writeCppFile(s, cppDoc)
  }

  private def writeCppFile(s: CppWriterState, cppDoc: CppDoc) = {
    val lines = CppDocCppWriter.visitCppDoc(cppDoc)
    writeLinesToFile(s, cppDoc.cppFileName, lines)
  }

  private def writeHppFile(s: CppWriterState, cppDoc: CppDoc) = {
    val lines = CppDocHppWriter.visitCppDoc(cppDoc)
    writeLinesToFile(s, cppDoc.hppFile.name, lines)
  }

  private def writeLinesToFile(s: CppWriterState, fileName: String, lines: List[Line]) = {
    val path = java.nio.file.Paths.get(s.dir, fileName)
    val file = File.Path(path)
    for (writer <- file.openWrite()) yield { 
      lines.map(Line.write(writer) _)
      writer.close()
      s
    }
  }

}
