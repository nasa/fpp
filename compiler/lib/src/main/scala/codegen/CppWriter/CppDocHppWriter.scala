package fpp.compiler.codegen

import java.time.Year

/** Write a CppDoc to an hpp file */
object CppDocHppWriter extends CppDocWriter {

  def addParamComment(s: String, commentOpt: Option[String]): List[Line] = commentOpt match {
    case Some(comment) =>
      val ls = CppDocWriter.writeDoxygenPostComment(comment)
      Line.joinLists (Line.Indent) (lines(s)) (" ") (ls)
    case None => lines(s)
  }

  def addParamDefault(s: String, defaultOpt: Option[String]): String = defaultOpt match {
    case Some(default) => s"$s = $default"
    case None => s
  }

  def closeIncludeGuard: List[Line] = lines(
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

  def paramString(p: CppDoc.Function.Param): String = {
    val s1 = CppDocCppWriter.paramString(p)
    addParamDefault(s1, p.default)
  }

  def paramLines(p: CppDoc.Function.Param): List[Line] =
    addParamComment(paramString(p), p.comment)

  def paramLinesComma(p: CppDoc.Function.Param): List[Line] =
    addParamComment(s"${paramString(p)},", p.comment)

  def writeAccessTag(tag: String): List[Line] = List(
    Line.blank,
    line(s"$tag:").indentOut(2)
  )

  def writeParams(prefix: String, params: List[CppDoc.Function.Param]): List[Line] = {
    if (params.length == 0) lines(s"$prefix()")
    else if (params.length == 1 && params.head.comment.isEmpty)
      lines(s"$prefix(" ++ paramString(params.head) ++ ")")
    else {
      val head :: tail = params.reverse
      val paramsLines = (paramLines(head) :: tail.map(paramLinesComma(_))).reverse.flatten
      line(s"$prefix(") :: (paramsLines.map(_.indentIn(2 * indentIncrement)) :+ line(")"))
    }
  }

  override def visitClass(in: Input, c: CppDoc.Class) = {
    val name = c.name
    val commentLines = CppDocWriter.writeDoxygenCommentOpt(c.comment)
    val className = c.qualifier match {
      case CppDoc.Class.Final => s"class $name final"
      case CppDoc.Class.NonFinal => s"class $name"
    }
    val openLines = c.superclassDecls match {
      case Some(d) => List(
        line(s"$className :"),
        indentIn(line(d)),
        line("{")
      )
      case None => lines(s"$className {")
    }
    val bodyLines = {
      val newClassNameList = name :: in.classNameList
      val in1 = in.copy(classNameList = newClassNameList)
      val bodyLines = c.members.map(visitClassMember(in1, _)).flatten
      bodyLines.map(_.indentIn(2 * indentIncrement))
    }
    val closeLines = List(Line.blank, line("};"))
    commentLines ++ openLines ++ bodyLines ++ closeLines
  }

  override def visitConstructor(in: Input, constructor: CppDoc.Class.Constructor) = {
    val unqualifiedClassName = in.getEnclosingClassUnqualified
    val outputLines = {
      val lines1 = CppDocWriter.writeDoxygenCommentOpt(constructor.comment)
      val lines2 = {
        import CppDoc.Class.Constructor._
        val params = writeParams(unqualifiedClassName, constructor.params)
        val nameAndParams = Line.addSuffix(params, ";")
        constructor.explicitQualifier match {
          case Explicit => Line.addPrefix("explicit ", nameAndParams)
          case NotExplicit => nameAndParams
        }
      }
      lines1 ++ lines2
    }
    outputLines
  }

  override def visitCppDoc(cppDoc: CppDoc, cppFileNameBaseOpt: Option[String] = None) = {
    val hppFile = cppDoc.hppFile
    val cppFileName = cppDoc.cppFileName
    val in = Input(hppFile, cppFileName)
    val ext = hppFile.name.toString.split("\\.").last
    List(
      CppDocWriter.writeBanner(
        cppDoc,
        in.hppFile.name,
        s"$ext file for ${cppDoc.description}"
      ),
      openIncludeGuard(hppFile.includeGuard),
      cppDoc.members.map(visitMember(in, _)).flatten,
      closeIncludeGuard
    ).flatten.map(CppDocWriter.leftAlignDirective)
  }

  override def visitDestructor(in: Input, destructor: CppDoc.Class.Destructor) = {
    val unqualifiedClassName = in.getEnclosingClassUnqualified
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
            case PureVirtual => "virtual "
            case Static => "static "
            case Virtual => "virtual "
            case _ => ""
          }
          val retType = function.retType.hppType match {
            case "" => ""
            case t => s"$t "
          }
          prefix1 ++ s"${retType}${function.name}"
        }
        val lines1 = {
          val lines11 = writeParams(prefix, function.params)
          val lines12 = function.constQualifier match {
            case Const => Line.addSuffix(lines11, " const")
            case _ => lines11
          }
          function.svQualifier match {
            case Final => Line.addSuffix(lines12, " final")
            case Override => Line.addSuffix(lines12, " override")
            case _ => lines12
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

}
