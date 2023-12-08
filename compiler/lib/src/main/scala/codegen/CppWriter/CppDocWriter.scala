package fpp.compiler.codegen

import java.time.Year

/** Write a Cpp doc as hpp or cpp */
trait CppDocWriter extends CppDocVisitor with LineUtils {

  case class Input(
    /** The hpp file */
    hppFile: CppDoc.HppFile,
    /** The default cpp file name */
    defaultCppFileName: String,
    /** The name of the cpp file to write output to, if different from the default */
    outputCppFileNameOpt: Option[String] = None,
    /** The list of enclosing class names, backwards. A class name may include :: */
    classNameList: List[String] = Nil,
  ) {

    /** Get the enclosing class name, including any qualifier */
    def getEnclosingClassQualified: String = classNameList.reverse.mkString("::")

    /** Get the enclosing class name with no qualifier */
    def getEnclosingClassUnqualified: String = classNameList.head.split("::").reverse.head

    /** Get the output cpp file name */
    def getOutputCppFileName: String = outputCppFileNameOpt.getOrElse(defaultCppFileName)

  }

  def default(in: Input) = Nil

  /** Visit a CppDoc */
  def visitCppDoc(cppDoc: CppDoc, cppFileNameBaseOpt: Option[String]): Output

  type Output = List[Line]

}

object CppDocWriter extends LineUtils {

  /** Write a banner comment */
  def writeBannerComment(comment: String): List[Line] = {
    def banner =
      line("// ----------------------------------------------------------------------")
    (Line.blank :: banner :: writeCommentBody(comment)) :+ banner
  }

  /** Write a comment */
  def writeComment(comment: String): List[Line] = Line.blank :: writeCommentBody(comment)

  /** Write an optional Doxygen comment */
  def writeDoxygenCommentOpt(commentOpt: Option[String]): List[Line] =
    commentOpt match {
      case Some(comment) => writeDoxygenComment(comment)
      case None => Line.blank :: Nil
    }

  /** Write an optional Doxygen post comment */
  def writeDoxygenPostCommentOpt(commentOpt: Option[String]): List[Line] =
    commentOpt match {
      case Some(comment) => writeDoxygenPostComment(comment)
      case None => Line.blank :: Nil
    }

  /** Add a a prefix to a comment line */
  def addCommentPrefix (prefix: String) (l: Line): Line = l.string match {
    case "" => line(prefix)
    case _ => Line.join(" ")(line(prefix))(l)
  }

  /** Write a Doxygen comment */
  def writeDoxygenComment(comment: String): List[Line] =
    Line.blank :: lines(comment).map(addCommentPrefix("//!")_)

  /** Write a Doxygen post comment */
  def writeDoxygenPostComment(comment: String): List[Line] =
    lines(comment).map(addCommentPrefix("//!<")_)

  /** Write a comment body */
  def writeCommentBody(comment: String): List[Line] =
    lines(comment).map(addCommentPrefix("//")_)

  /** Left align a compiler directive */
  def leftAlignDirective(line: Line): Line =
    if (line.string.startsWith("#")) Line(line.string) else line

  // FIXME: Get this class out of the CppDoc layer and into the CppWriter layer
  case class FileBanner(cppDoc: CppDoc) extends CppDoc.FileBanner {
    // Map template patterns to final code patterns
    val templateMap = Map(
      "TestMain\\.cpp$" -> "TestMain.cpp",
      "Tester\\.cpp$" -> "Tester.cpp",
      "Tester\\.hpp$" -> "Tester.hpp",
      "\\.template\\.cpp$" -> ".cpp",
      "\\.template\\.hpp$" -> ".hpp",
    )
    // Check whether the file name matches a template pattern
    def fileIsTemplate(fileName: String): Boolean =
      templateMap.keys.foldLeft (false) (
        (s, pattern) => s || fileName.matches(s".*$pattern.*")
      )
    // Check whether the file name is a tester helpers file
    def fileIsTesterHelpers(fileName: String): Boolean =
      fileName.matches(".*TesterHelpers\\.cpp")
    override def getTitle(fileName: String): String =
      templateMap.foldLeft (fileName) {
        case (s, (key, value)) => s.replaceAll(key, value)
      }
    override def getAuthor(fileName: String): String =
      if fileIsTemplate(fileName)
      then System.getProperty("user.name")
      else cppDoc.DefaultFileBanner.getAuthor(fileName)
    override def getDescription(fileName: String, genericDescription: String): String =
      // The description for the TesterHelpers file is specialized
      if fileIsTesterHelpers(fileName)
      then genericDescription.replaceAll("implementation class", "helper functions")
      else genericDescription
  }

  /** Write a header banner */
  def writeBanner(cppDoc: CppDoc, fileName: String, genericDescription: String): List[Line] = {
    // Return the banner
    val fileBanner = FileBanner(cppDoc)
    lines(
    s"""|// ======================================================================
        |// \\title  ${fileBanner.getTitle(fileName)}
        |// \\author ${fileBanner.getAuthor(fileName)}
        |// \\brief  ${fileBanner.getDescription(fileName, genericDescription)}
        |// ======================================================================"""
    )
  }

  /** Write a function body */
  def writeFunctionBody(body: List[Line]): List[Line] = {
    val bodyLines = body.length match {
      case 0 => Line.blank :: Nil
      case _ => body.map(indentIn(_))
    }
    line("{") :: (bodyLines :+ line("}"))
  }

}
