package fpp.compiler.codegen

import java.time.Year

/** A C++ doc hpp writer */
object CppDocHppWriter extends CppDocWriter {

  def writeAccessTag(tag: String) = List(
    Line.blank,
    line(s"$tag:").indentOut(2)
  )

  def addParamComment(s: String, commentOpt: Option[String]) = commentOpt match {
    case Some(comment) => s"$s //!< ${"\n".r.replaceAllIn(comment, " ")}"
    case None => s
  }

  def closeIncludeGuard = lines(
    """|
       |#endif"""
  )

  def openIncludeGuard(guard: String): List[Line] = {
    lines(
      s"""|
          |#ifndef $guard
          |#define $guard"""
    )
  }

  def paramString(p: CppDoc.Function.Param) = {
    val s1 = CppDocCppWriter.paramString(p)
    val s2 = addParamComment(s1, p.comment)
    s2
  }

  def paramStringComma(p: CppDoc.Function.Param) = {
    val s1 = CppDocCppWriter.paramStringComma(p)
    val s2 = addParamComment(s1, p.comment)
    s2
  }

  def writeParams(prefix: String, params: List[CppDoc.Function.Param]) = {
    if (params.length == 0) lines(s"$prefix()")
    else if (params.length == 1 && params.head.comment.isEmpty)
      lines(s"$prefix(" ++ CppDocCppWriter.paramString(params.head) ++ ")")
    else {
      val head :: tail = params.reverse
      val paramLines = (writeParam(head) :: tail.map(writeParamComma(_))).reverse
      line(s"$prefix(") :: (paramLines.map(_.indentIn(2 * indentIncrement)) :+ line(")"))
    }
  }

  override def visitClass(in: Input, c: CppDoc.Class): Output = {
    val name = c.name
    val newClassNameList = name :: in.classNameList
    val in1 = in.copy(classNameList = newClassNameList)
    val startLines = c.superclassDecls match {
      case Some(d) => List(
        Line.blank,
        line(s"class $name :"), 
        indentIn(line(d)),
        line("{")
      )
      case None => List(Line.blank, line(s"class $name {"))
    }
    val outputLines = {
      val outputLines = c.members.map(visitClassMember(in1, _)).flatten
      outputLines.map(_.indentIn(2 * indentIncrement))
    }
    val endLines = List(Line.blank, line("};"))
    startLines ++ outputLines ++ endLines
  }

  override def visitConstructor(in: Input, constructor: CppDoc.Class.Constructor) = {
    val unqualifiedClassName = in.getEnclosingClassUnqualified
    val qualifiedClassName = in.getEnclosingClassQualified
    val outputLines = {
      val lines1 = CppDocWriter.writeDoxygenCommentOpt(constructor.comment)
      val lines2 = {
        val params = writeParams(unqualifiedClassName, constructor.params)
        Line.addSuffix(params, ";")
      }
      lines1 ++ lines2
    }
    outputLines
  }

  override def visitCppDoc(cppDoc: CppDoc): Output = {
    val hppFile = cppDoc.hppFile
    val cppFileName = cppDoc.cppFileName
    val in = Input(hppFile, cppFileName)
    List(
      CppDocWriter.writeBanner(in.hppFile.name),
      openIncludeGuard(hppFile.includeGuard),
      cppDoc.members.map(visitMember(in, _)).flatten,
      closeIncludeGuard
    ).flatten
  }

  override def visitDestructor(in: Input, destructor: CppDoc.Class.Destructor) = {
    val unqualifiedClassName = in.getEnclosingClassUnqualified
    val qualifiedClassName = in.getEnclosingClassQualified
    val outputLines = {
      val lines1 = CppDocWriter.writeDoxygenCommentOpt(destructor.comment)
      val lines2 = destructor.virtualQualifier match {
        case CppDoc.Class.Destructor.Virtual => lines(s"virtual ~$unqualifiedClassName();")
        case _ => lines(s"~$unqualifiedClassName();")
      }
      lines1 ++ lines2
    }
    outputLines
  }

  override def visitFunction(in: Input, function: CppDoc.Function) = {
    import CppDoc.Function._
    val outputLines = {
      val lines1 = CppDocWriter.writeDoxygenCommentOpt(function.comment)
      val lines2 = {
        val prefix = {
          val prefix1 = function.svQualifier match {
            case Virtual => "virtual "
            case PureVirtual => "virtual "
            case Static => "static "
            case _ => ""
          }
          prefix1 ++ s"${function.retType.hppType} ${function.name}"
        }
        val lines1 = {
          val lines1 = writeParams(prefix, function.params)
          function.constQualifier match {
            case Const => Line.addSuffix(lines1, " const")
            case _ => lines1
          }
        }
        val lines2 = function.svQualifier match {
          case PureVirtual => lines(" = 0;")
          case _ => lines(";")
        }
        Line.joinLists(Line.NoIndent)(lines1)("")(lines2)
      }
      lines1 ++ lines2
    }
    outputLines
  }

  override def visitLines(in: Input, lines: CppDoc.Lines) = {
    val content = lines.content
    lines.output match {
      case CppDoc.Lines.Hpp => content
      case CppDoc.Lines.Cpp => Nil
      case CppDoc.Lines.Both => content
    }
  }

  override def visitNamespace(in: Input, namespace: CppDoc.Namespace): Output = {
    val name = namespace.name
    val startLines = List(Line.blank, line(s"namespace $name {"))
    val outputLines = namespace.members.map(visitNamespaceMember(in, _)).flatten
    val endLines = List(Line.blank, line("}"))
    startLines ++ outputLines.map(indentIn(_)) ++ endLines
  }

}
