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

  def linesMember(ls: List[Line]) = CppDoc.Member.Lines(CppDoc.Lines(ls))

  def namespaceMember(name: String, members: List[CppDoc.Namespace.Member]) =
    CppDoc.Member.Namespace(CppDoc.Namespace(name, members))

  private def writeConstants(s: CppWriterState, tuList: List[Ast.TransUnit]) = {
    val constantMembers = tuList.flatMap(ConstantCppWriter.transUnit(s, _))
    val headers = List("Fw/Types/BasicTypes.hpp")
    val headerLines = Line.blank :: headers.map(headerLine)
    val members = linesMember(headerLines) :: constantMembers
    val includeGuard = s.includeGuardFromPrefix("FPP_CONSTANTS_HPP")
    val fileName = ComputeCppFiles.getConstantsName
    val cppDoc = createCppDoc(fileName, includeGuard, members)
    writeHppFile(s, cppDoc)
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
