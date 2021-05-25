package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ */
object CppWriter extends LineUtils {

  def tuList(s: CppWriterState, tul: List[Ast.TransUnit]) =
    for {
      _ <- ConstantCppWriter.write(s, tul)
      // TODO: Write topologies
    }
    yield ()

  def createCppDoc(
    fileName: String,
    includeGuard: String,
    members: List[CppDoc.Member]
  ) = {
    val hppFile = CppDoc.HppFile(s"$fileName.hpp", includeGuard)
    CppDoc(hppFile, s"$fileName.cpp", members)
  }

  def headerLine(s: String) = {
    val q = "\""
    line(s"#include $q$s$q")
  }

  def linesMember(
    content: List[Line],
    output: CppDoc.Lines.Output = CppDoc.Lines.Hpp
  ) = CppDoc.Member.Lines(CppDoc.Lines(content, output))

  def namespaceMember(
    name: String,
    members: List[CppDoc.Namespace.Member]
  ) = CppDoc.Member.Namespace(CppDoc.Namespace(name, members))

  def writeCppDoc(s: CppWriterState, cppDoc: CppDoc) =
    for {
      _ <- writeHppFile(s, cppDoc)
      _ <- writeCppFile(s, cppDoc)
    }
    yield ()

  private def writeCppFile(s: CppWriterState, cppDoc: CppDoc) = {
    val lines = CppDocCppWriter.visitCppDoc(cppDoc)
    writeLinesToFile(s, cppDoc.cppFileName, lines)
  }

  private def writeHppFile(s: CppWriterState, cppDoc: CppDoc) = {
    val lines = CppDocHppWriter.visitCppDoc(cppDoc)
    writeLinesToFile(s, cppDoc.hppFile.name, lines)
  }

  private def writeLinesToFile(
    s: CppWriterState,
    fileName: String,
    lines: List[Line]
  ) = {
    val path = java.nio.file.Paths.get(s.dir, fileName)
    val file = File.Path(path)
    for (writer <- file.openWrite()) yield { 
      lines.map(Line.write(writer) _)
      writer.close()
    }
  }

}
