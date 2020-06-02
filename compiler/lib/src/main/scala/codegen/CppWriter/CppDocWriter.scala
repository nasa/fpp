package fpp.compiler.codegen

import java.time.Year

/** A C++ doc writer */
object CppDocWriter extends CppDocVisitor with LineUtils {

  case class Input(
    /** The current hpp file */
    hppFile: String,
    /** The list of name qualifiers, backwards. A name qualifier may include :: */
    qualifierList: List[String] = Nil,
  )

  def accessTag(tag: String) = List(
    Line.blank,
    line(s"$tag:").indentOut(2)
  )

  def bannerComment(comment: String) = {
    def banner =
      line("// ----------------------------------------------------------------------")
    (Line.blank :: banner :: commentBody(comment)) :+ banner
  }

  def comment(comment: String) = Line.blank :: commentBody(comment)

  def default(in: Input) = Map()

  def doxygenCommentOpt(commentOpt: Option[String]) = commentOpt match {
    case Some(comment) => doxygenComment(comment)
    case None => Line.blank :: Nil
  }
    
  def doxygenComment(comment: String) = 
    Line.blank ::lines(comment).map(Line.join(" ")(line("//!"))_)
    
  private def hppParamString(p: CppDoc.Function.Param) = {
    val s1 = cppParamString(p)
    val s2 = addParamComment(s1, p.comment)
    s2
  }

  def visitCppDoc(cppDoc: CppDoc): Output = {
    val hppFile = cppDoc.hppFile
    val in = Input(hppFile)
    val output = Map(hppFile -> openIncludeGuard(cppDoc.includeGuard))
    val output1 = cppDoc.members.map(visitMember(in, _)).fold(output)(mergeOutput(_, _))
    val output2 = output1 + (hppFile -> (output1(hppFile) ++ closeIncludeGuard))
    val output3 = output2.map({ case (k -> v) => (k -> (writeBanner(k) ++ v)) })
    val output4 = output3.map({ case (k -> v) => (k -> v.map(leftAlignDirective(_))) })
    output4
  }

  override def visitClass(in: Input, c: CppDoc.Class): Output = {
    val name = c.name
    val newQualifierList = name :: in.qualifierList
    val in1 = in.copy(qualifierList = newQualifierList)
    val output = c.members.map(visitClassMember(in1, _)).fold(Map())(mergeOutput(_, _))
    val lines1 = c.superclassDecls match {
      case Some(d) => List(
        Line.blank,
        line(s"class $name :"), 
        indentIn(line(d)),
        line("{")
      )
      case None => List(Line.blank, line(s"class $name {"))
    }
    val lines2 = List(
      Line.blank,
      line("};")
    )
    output + (
      in.hppFile -> 
      (lines1 ++ output.getOrElse(in.hppFile, Nil).
        map(_.indentIn(2 * indentIncrement)) ++ lines2)
    )
  }

  override def visitFunction(in: Input, function: CppDoc.Function) = {
    val lines = List(Line.blank, line(s"// Function $function.name"))
    Map(in.hppFile -> lines)
  }

  override def visitLines(in: Input, lines: CppDoc.Lines) = {
    val content = lines.content
    lines.output match {
      case CppDoc.Lines.Hpp => Map(in.hppFile -> content)
      case CppDoc.Lines.Cpp(cppFile) => Map(cppFile -> content)
      case CppDoc.Lines.Both(cppFile) => Map(in.hppFile -> content, cppFile -> content)
    }
  }

  override def visitNamespace(in: Input, namespace: CppDoc.Namespace): Output = {
    val name = namespace.name
    val newQualifierList = name :: in.qualifierList
    val in1 = in.copy(qualifierList = newQualifierList)
    val output = namespace.members.map(visitNamespaceMember(in1, _)).fold(Map())(mergeOutput(_, _))
    val lines1 = List(
      Line.blank,
      line(s"namespace $name {")
    )
    val lines2 = List(
      Line.blank,
      line("}")
    )
    output + (in.hppFile -> (lines1 ++ output.getOrElse(in.hppFile, Nil).map(indentIn(_)) ++ lines2))
  }

  private def addParamComment(s: String, commentOpt: Option[String]) = commentOpt match {
    case Some(comment) => s"$s //!< ${"\n".r.replaceAllIn(comment, " ")}"
    case None => s
  }

  private def addParamConstQualifier(q: CppDoc.Function.ConstQualifier, s: String) = {
    import CppDoc.Function._
    q match {
      case Const => s"const $s"
      case NonConst => s
    }
  }

  private def closeIncludeGuard = lines(
    """|
       |#endif"""
  )

  private def commentBody(comment: String) = lines(comment).map(Line.join(" ")(line("//"))_)

  private def cppParamString(p: CppDoc.Function.Param) = {
    val s1 = s"${p.t.hppType} ${p.name}"
    val s2 = addParamConstQualifier(p.constQualifier, s1)
    s2
  }

  private def getEnclosingName(in: Input) = in.qualifierList.head.split("::").reverse.head
 
  override def visitConstructor(in: Input, constructor: CppDoc.Class.Constructor) = {
    val className = getEnclosingName(in)
    val hppLines = {
      val lines1 = doxygenCommentOpt(constructor.comment)
      val lines2 = lines1 ++ lines(s"$className")
      val lines3 = Line.joinLists(Line.NoIndent)(lines2)("")(writeHppParams(constructor.params))
      val lines4 = Line.joinLists(Line.NoIndent)(lines3)("")(lines(";"))
      lines4
    }
    Map(in.hppFile -> hppLines)
  }

  private def leftAlignDirective(line: Line) =
    if (line.string.startsWith("#")) Line(line.string) else line

  private def mergeOutput(output1: Output, output2: Output) =
    output2.foldRight(output1)({ case (k -> v, map) => map + (k -> (map.getOrElse(k, Nil) ++ v)) })

  private def openIncludeGuard(guard: String): List[Line] = {
    lines(
      s"""|
          |#ifndef $guard
          |#define $guard"""
    )
  }

  private def writeBanner(title: String) = lines(
    s"""|// ====================================================================== 
        |// \\title  $title
        |// \\author Generated by fpp-to-cpp
        |//
        |// \\copyright
        |// Copyright (C) ${Year.now.getValue} California Institute of Technology.
        |// ALL RIGHTS RESERVED.  United States Government Sponsorship
        |// acknowledged. Any commercial use must be negotiated with the Office
        |// of Technology Transfer at the California Institute of Technology.
        |// 
        |// This software may be subject to U.S. export control laws and
        |// regulations.  By accepting this document, the user agrees to comply
        |// with all U.S. export laws and regulations.  User has the
        |// responsibility to obtain export licenses, or other export authority
        |// as may be required before exporting such information to foreign
        |// countries or providing access to foreign persons.
        |// ======================================================================"""
  )

  private def writeCppParam(p: CppDoc.Function.Param) = line(cppParamString(p))

  private def writeHppParam(p: CppDoc.Function.Param) = line(hppParamString(p))

  private def writeHppParams(params: List[CppDoc.Function.Param]) = {
    if (params.length == 0) lines("()")
    else if (params.length == 1 && params.head.comment.isEmpty)
      lines("(" ++ hppParamString(params.head) ++ ")")
    else (line("(") :: params.map(writeHppParam(_))).map(indentIn(_)) :+ line(")")
  }

  type Output = Map[String,List[Line]]

}
