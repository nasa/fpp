package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ */
object CppWriter extends LineUtils {

  def tuList(s: CppWriterState, tul: List[Ast.TransUnit]) = {
    writeConstants(s, tul)
  }

  def createCppDoc(fileName: String, includeGuard: String, members: List[CppDoc.Member]) = {
    val hppFile = CppDoc.HppFile(s"$fileName.hpp", includeGuard)
    CppDoc(hppFile, s"$fileName.cpp", members)
  }

  def headerLine(s: String) = {
    val q = "\""
    line(s"#include $q$s$q")
  }

  def linesMember(content: List[Line], output: CppDoc.Lines.Output = CppDoc.Lines.Hpp) = 
    CppDoc.Member.Lines(CppDoc.Lines(content, output))

  def namespaceMember(name: String, members: List[CppDoc.Namespace.Member]) =
    CppDoc.Member.Namespace(CppDoc.Namespace(name, members))

  private def writeConstants(s: CppWriterState, tuList: List[Ast.TransUnit]) =
    tuList.flatMap(ConstantCppWriter.transUnit(s, _)) match {
      case Nil => Right(())
      case constantMembers => 
        val fileName = ComputeCppFiles.getConstantsName
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
    }
  }

}
