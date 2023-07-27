package fpp.compiler.codegen

import java.time.Year

/** Write a CppDoc to a cpp file */
object CppDocCppWriter extends CppDocWriter {

  def paramString(p: CppDoc.Function.Param): String = s"${p.t.hppType} ${p.name}"

  def paramStringComma(p: CppDoc.Function.Param): String = s"${paramString(p)},"

  def paramLine(p: CppDoc.Function.Param): Line = line(paramString(p))

  def paramLineComma(p: CppDoc.Function.Param): Line = line(paramStringComma(p))

  def writeParams(prefix: String, params: List[CppDoc.Function.Param]): List[Line] = {
    if (params.length == 0) lines(s"$prefix()")
    else if (params.length == 1)
      lines(s"$prefix(" ++ paramString(params.head) ++ ")")
    else {
      val head :: tail = params.reverse
      val paramLines = (paramLine(head) :: tail.map(paramLineComma(_))).reverse
      line(s"$prefix(") :: (paramLines.map(_.indentIn(2 * indentIncrement)) :+ line(")"))
    }
  }

  /** Write lines for the selected C++ file */
  def writeSelectedLines(
    in: Input,
    selectedCppFileOpt: Option[String],
    lines: => List[Line]
  ): List[Line] = {
    // Resolve the selected cpp file for the lines
    val selectedCppFile = selectedCppFileOpt.getOrElse(in.defaultCppFileName)
    // Resolve the output cpp file
    val outputCppFile = in.getOutputCppFileName
    // Write the lines if the two cpp files match
    if (selectedCppFile == outputCppFile) lines else Nil
  }

  override def visitClass(in: Input, c: CppDoc.Class) = {
    val name = c.name
    val newClassNameList = name :: in.classNameList
    val in1 = in.copy(classNameList = newClassNameList)
    c.members.map(visitClassMember(in1, _)).flatten
  }

  override def visitConstructor(in: Input, constructor: CppDoc.Class.Constructor) =
    writeSelectedLines(in, constructor.cppFileNameBaseOpt,
      {
        val unqualifiedClassName = in.getEnclosingClassUnqualified
        val qualifiedClassName = in.getEnclosingClassQualified
        val outputLines = {
          val nameLines = lines(s"$qualifiedClassName ::")
          val paramLines = {
            val lines1 = writeParams(unqualifiedClassName, constructor.params)
            val lines2 = constructor.initializers match {
              case Nil => lines1
              case _ => Line.addSuffix(lines1, " :")
            }
            lines2.map(indentIn(_))
          }
          val initializerLines = constructor.initializers.reverse match {
            case Nil => Nil
            case head :: tail => {
              val list = head :: tail.map(_ ++ ",")
              list.reverse.map(line(_)).map(_.indentIn(2 * indentIncrement))
            }
          }
          val bodyLines = CppDocWriter.writeFunctionBody(constructor.body)
          Line.blank :: List(
            nameLines,
            paramLines,
            initializerLines,
            bodyLines
          ).flatten
        }
        outputLines
      }
    )

  override def visitCppDoc(cppDoc: CppDoc, cppFileNameBaseOpt: Option[String] = None) = {
    val in = Input(cppDoc.hppFile, cppDoc.cppFileName, cppFileNameBaseOpt)
    List(
      CppDocWriter.writeBanner(
        in.getOutputCppFileName,
        cppDoc.toolName,
        s"cpp file for ${cppDoc.description}"
      ),
      cppDoc.members.map(visitMember(in, _)).flatten,
    ).flatten.map(CppDocWriter.leftAlignDirective)
  }

  override def visitDestructor(in: Input, destructor: CppDoc.Class.Destructor) =
    writeSelectedLines(in, destructor.cppFileNameBaseOpt,
      {
        val unqualifiedClassName = in.getEnclosingClassUnqualified
        val qualifiedClassName = in.getEnclosingClassQualified
        val outputLines = {
          val startLine1 = line(s"$qualifiedClassName ::")
          val startLine2 = indentIn(line(s"~$unqualifiedClassName()"))
          val bodyLines = CppDocWriter.writeFunctionBody(destructor.body)
          Line.blank :: startLine1 :: startLine2 :: bodyLines
        }
        outputLines
      }
    )

  override def visitFunction(in: Input, function: CppDoc.Function) =
    writeSelectedLines(in, function.cppFileNameBaseOpt,
      (function.svQualifier, function.body) match {
        // If the function is pure virtual, and the function body is empty,
        // then there is no implementation, so don't write one out.
        case (CppDoc.Function.PureVirtual, Nil) => Nil
        // Otherwise write out the implementation.
        // For a pure virtual function, this is a default implementation.
        case _ => {
          val contentLines = {
            val startLines = {
              val prototypeLines = {
                import CppDoc.Function._
                val lines1 = writeParams(function.name, function.params)
                function.constQualifier match {
                  case Const => Line.addSuffix(lines1, " const")
                  case NonConst => lines1
                }
              }
              val retType = function.retType.getCppType match {
                case "" => ""
                case t => s"$t "
              }
              in.classNameList match {
                case _ :: _ => {
                  val line1 = line(s"${retType}${in.getEnclosingClassQualified} ::")
                  line1 :: prototypeLines.map(indentIn(_))
                }
                case Nil =>
                  Line.addPrefix(retType, prototypeLines)
              }
            }
            val bodyLines = CppDocWriter.writeFunctionBody(function.body)
            in.classNameList match {
              case _ :: _ => startLines ++ bodyLines
              case Nil => Line.joinLists(Line.NoIndent)(startLines)(" ")(bodyLines)
            }
          }
          Line.blank :: contentLines
        }
      }
    )

  override def visitLines(in: Input, lines: CppDoc.Lines) =
    lines.output match {
      case CppDoc.Lines.Hpp => Nil
      case _ => writeSelectedLines(in, lines.cppFileNameBaseOpt, lines.content)
    }

  override def visitNamespace(in: Input, namespace: CppDoc.Namespace) =
    namespace.members.flatMap(visitNamespaceMember(in, _)) match {
      case Nil => Nil
      case outputLines =>
        val name = namespace.name
        List(Line.blank, line(s"namespace $name {")) ++
        outputLines.map(indentIn(_)) ++
        List(Line.blank, line("}"))
    }

}
