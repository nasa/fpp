package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ */
trait CppWriter extends AstStateVisitor with LineUtils {

  type State = CppWriterState

  override def transUnit(s: State, tu: Ast.TransUnit) =
    visitList(s, tu.members, matchTuMember)

  def tuList(s: State, tul: List[Ast.TransUnit]): Result.Result[Unit] =
    for {
      _ <- ConstantCppWriter.write(s, tul)
      _ <- visitList(s, tul, transUnit)
    }
    yield ()
}

object CppWriter extends LineUtils{

  def createCppDoc(
    description: String,
    fileNameBase: String,
    includeGuard: String,
    members: List[CppDoc.Member],
    toolName: Option[String],
    hppFileExtension: String = "hpp",
    cppFileExtension: String = "cpp",
  ): CppDoc = {
    val hppFile = CppDoc.HppFile(s"$fileNameBase.$hppFileExtension", includeGuard)
    CppDoc(description, hppFile, s"$fileNameBase.$cppFileExtension", members, toolName)
  }

  def headerString(s: String): String = {
    val q = "\""
    s"#include $q$s$q"
  }

  def systemHeaderString(s: String): String = s"#include <$s>"

  def headerLine(s: String): Line = line(headerString(s))

  def writeCppDoc(
    s: CppWriterState,
    cppDoc: CppDoc,
    cppFileNameBaseOpt: Option[String] = None
  ): Result.Result[CppWriterState] =
    for {
      _ <- writeHppFile(s, cppDoc)
      _ <- writeCppFile(s, cppDoc, cppFileNameBaseOpt)
    }
    yield s

  def writeCppFile(
    s: CppWriterState,
    cppDoc: CppDoc,
    cppFileNameBaseOpt: Option[String] = None
  ) = {
    val lines = CppDocCppWriter.visitCppDoc(cppDoc, cppFileNameBaseOpt)
    val cppFileName = cppFileNameBaseOpt match {
      case Some(base) => s"$base.cpp"
      case None => cppDoc.cppFileName
    }
    writeLinesToFile(s, cppFileName, lines)
  }

  def writeHppFile(s: CppWriterState, cppDoc: CppDoc) = {
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
  def identFromQualifiedName(name: Name.Qualified): String =
    name.toString.replaceAll("\\.", "_")

  /** Writes a qualified name */
  def writeQualifiedName(name: Name.Qualified): String =
    name.toString.replaceAll("\\.", "::")

  /** Writes an identifier */
  def writeId(id: BigInt): String = s"0x${id.toString(16).toUpperCase}"

  def getMode(template: Boolean): Mode =
    if template then ImplTemplate
    else Autocode

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

  sealed trait Mode
  case object Autocode extends Mode
  case object ImplTemplate extends Mode

}
