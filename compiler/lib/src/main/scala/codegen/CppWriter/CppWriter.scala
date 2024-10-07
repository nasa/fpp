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

  /** A file banner for the CppWriter */
  case class FileBanner(toolNameOpt: Option[String]) extends CppDoc.FileBanner {
    val defaultFileBanner = CppDoc.DefaultFileBanner(toolNameOpt)
    override def getTitle(fileName: String): String =
      // If the file is a template, the title is the actual file name
      ComputeCppFiles.FileNames.convertTemplateToActual(fileName)
    override def getAuthor(fileName: String): String =
      if ComputeCppFiles.FileNames.isTemplate(fileName)
      // If the file is a template, then write in the user name as the author.
      // Ownership of the generated code passes to the user when the
      // template file is copied to the actual file.
      then System.getProperty("user.name")
      // Otherwise say that the file is auto-generated
      else defaultFileBanner.getAuthor(fileName)
    override def getDescription(fileName: String, genericDescription: String): String = {
      // The description for the TesterHelpers file is specialized
      val defaultDescription =
        defaultFileBanner.getDescription(fileName, genericDescription)
      if ComputeCppFiles.FileNames.isTesterHelpers(fileName)
      then defaultDescription.replaceAll("implementation class", "helper functions")
      else defaultDescription
    }
  }

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
    val fileBanner = Some(FileBanner(toolName))
    CppDoc(description, hppFile, s"$fileNameBase.$cppFileExtension", members, toolName, fileBanner)
  }

  /** Construct a header string */
  def headerString: String => String = CppWriterState.headerString

  def systemHeaderString(s: String): String = s"#include <$s>"

  def headerLine(s: String): Line = line(headerString(s))

  /** Write the CppDoc.
   *  Always write the hpp file and cppDoc.cppFileName.
   *  If cppFileNameBases is non-Nil, then write those files as well. */
  def writeCppDoc(
    s: CppWriterState,
    cppDoc: CppDoc,
    cppFileNameBases: List[String] = Nil
  ): Result.Result[CppWriterState] = {
    for {
      // Write the hpp file
      s <- writeHppFile(s, cppDoc)
      // Write the default cpp file
      s <- writeCppFile(s, cppDoc)
      // Write the supplemental cpp files
      s <- Result.foldLeft (cppFileNameBases) (s) (
        (s, fileName) => writeCppFile(s, cppDoc, Some(fileName))
      )
    }
    yield s
  }

  /** Writes a cpp file for the CppDoc. By default, the cpp file written is
   *  cppDoc.cppFileName.
   *  You can specify a different cpp file by setting cppFileNameBaseOpt to
   *  a value other than None. */
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
    for (_ <- writeLinesToFile(s, cppFileName, lines)) yield s
  }

  /** Writes the hpp file for the CppDoc. */
  def writeHppFile(s: CppWriterState, cppDoc: CppDoc) = {
    val lines = CppDocHppWriter.visitCppDoc(cppDoc)
    for (_ <- writeLinesToFile(s, cppDoc.hppFile.name, lines)) yield s
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
  def identFromQualifiedName: Name.Qualified => String =
    CppWriterState.identFromQualifiedName

  /** Writes a qualified name */
  val writeQualifiedName: Name.Qualified => String =
    CppWriterState.writeQualifiedName

  /** Writes an identifier */
  def writeId(id: BigInt): String = s"0x${id.toString(16).toUpperCase}"

  def getMode(template: Boolean, unitTest: Boolean): Mode =
    (template, unitTest) match {
      case (false, false) => Autocode
      case (true, false) => ImplTemplate
      case (false, true) => UnitTest
      case (true, true) => UnitTestTemplate
    }

  def getTestHelperMode(auto: Boolean): TestHelperMode =
    auto match {
      case true => TestHelperMode.Auto
      case false => TestHelperMode.Manual
    }

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
  case object UnitTest extends Mode
  case object UnitTestTemplate extends Mode

  sealed trait TestHelperMode
  object TestHelperMode {
    case object Auto extends TestHelperMode
    case object Manual extends TestHelperMode
  }

}
