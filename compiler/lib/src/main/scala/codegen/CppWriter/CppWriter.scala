package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ */
object CppWriter extends AstStateVisitor with LineUtils {

  type State = CppWriterState

  override def defModuleAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val node = aNode._2
    val data = node.data
    visitList(s, data.members, matchModuleMember)
  }

  override def defTopologyAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = TopologyCppWriter(s, aNode).write
    writeCppDoc(s, cppDoc)
  }

  override def transUnit(s: CppWriterState, tu: Ast.TransUnit) = 
    visitList(s, tu.members, matchTuMember)

  def tuList(s: CppWriterState, tul: List[Ast.TransUnit]) =
    for {
      _ <- ConstantCppWriter.write(s, tul)
      _ <- visitList(s, tul, transUnit)
    }
    yield ()

  def createCppDoc(
    description: String,
    fileName: String,
    includeGuard: String,
    members: List[CppDoc.Member]
  ) = {
    val hppFile = CppDoc.HppFile(s"$fileName.hpp", includeGuard)
    CppDoc(description, hppFile, s"$fileName.cpp", members)
  }

  def headerString(s: String) = {
    val q = "\""
    s"#include $q$s$q"
  }

  def headerLine(s: String) = line(headerString(s))

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
    yield s

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

  /** Constructs a C++ identifier from a qualified name */
  def identFromQualifiedName(name: Name.Qualified) =
    name.toString.replaceAll("\\.", "_")

  /** Translates a qualified name to C++ */
  def translateQualifiedName(name: Name.Qualified) =
    name.toString.replaceAll("\\.", "::")

  /** Writes an identifier */
  def writeId(id: Int): String = s"0x${Integer.toString(id, 16).toUpperCase}"

  /** The phases of code generation */
  object Phases {

    /** Configuration constants */
    val configConstants = 0

    /** Configuration objects */
    val configObjects = 1

    /** Component instances */
    val instances = 2

    /** Initialize components */
    val initComponents = 3

    /** Configure components */
    val configComponents = 4

    /** Register commands */
    val regCommands = 5

    /** Load parameters */
    val loadParameters = 6

    // TODO

  }

}
