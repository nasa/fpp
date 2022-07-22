package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ */
object CppWriter extends AstStateVisitor with LineUtils {

  type State = CppWriterState

  override def defArrayAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = ArrayCppWriter(s, aNode).write
    writeCppDoc(s, cppDoc)
  }

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    visitList(s, data.members, matchComponentMember)
  }

  override def defEnumAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = EnumCppWriter(s, aNode).write
    writeCppDoc(s, cppDoc)
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val node = aNode._2
    val data = node.data
    visitList(s, data.members, matchModuleMember)
  }

  override def defStructAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = {
    val cppDoc = StructCppWriter(s, aNode).write
    writeCppDoc(s, cppDoc)
  }

  override def defTopologyAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = TopologyCppWriter(s, aNode).write
    writeCppDoc(s, cppDoc)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) =
    visitList(s, tu.members, matchTuMember)

  def tuList(s: State, tul: List[Ast.TransUnit]): Result.Result[Unit] =
    for {
      _ <- ConstantCppWriter.write(s, tul)
      _ <- visitList(s, tul, transUnit)
    }
    yield ()

  def createCppDoc(
    description: String,
    fileName: String,
    includeGuard: String,
    members: List[CppDoc.Member],
    toolName: Option[String]
  ): CppDoc = {
    val hppFile = CppDoc.HppFile(s"$fileName.hpp", includeGuard)
    CppDoc(description, hppFile, s"$fileName.cpp", members, toolName)
  }

  def headerString(s: String): String = {
    val q = "\""
    s"#include $q$s$q"
  }

  def systemHeaderString(s: String): String = s"#include <$s>"

  def headerLine(s: String): Line = line(headerString(s))

  def linesMember(
    content: List[Line],
    output: CppDoc.Lines.Output = CppDoc.Lines.Hpp
  ): CppDoc.Member.Lines = CppDoc.Member.Lines(CppDoc.Lines(content, output))

  def namespaceMember(
    name: String,
    members: List[CppDoc.Member]
  ): CppDoc.Member.Namespace = CppDoc.Member.Namespace(CppDoc.Namespace(name, members))

  def wrapInNamespaces(
    namespaceNames: List[String],
    members: List[CppDoc.Member]
  ): List[CppDoc.Member] = namespaceNames match {
    case Nil => members
    case head :: tail =>
      List(namespaceMember(head, wrapInNamespaces(tail, members)))
  }

  def writeCppDoc(s: State, cppDoc: CppDoc): Result.Result[State] =
    for {
      _ <- writeHppFile(s, cppDoc)
      _ <- writeCppFile(s, cppDoc)
    }
    yield s

  private def writeCppFile(s: State, cppDoc: CppDoc) = {
    val lines = CppDocCppWriter.visitCppDoc(cppDoc)
    writeLinesToFile(s, cppDoc.cppFileName, lines)
  }

  private def writeHppFile(s: State, cppDoc: CppDoc) = {
    val lines = CppDocHppWriter.visitCppDoc(cppDoc)
    writeLinesToFile(s, cppDoc.hppFile.name, lines)
  }

  private def writeLinesToFile(
    s: State,
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
  def identFromQualifiedName(name: Name.Qualified): String =
    name.toString.replaceAll("\\.", "_")

  /** Writes a qualified name */
  def writeQualifiedName(name: Name.Qualified): String =
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

    /** Read parameters */
    val readParameters = 6

    /** Load parameters */
    val loadParameters = 7

    /** Start tasks */
    val startTasks = 8

    /** Start tasks */
    val stopTasks = 9

    /** Free threads */
    val freeThreads = 10

    /** Tear down components */
    val tearDownComponents = 11

  }

}
