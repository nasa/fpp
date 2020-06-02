package fpp.compiler.codegen

import java.time.Year

/** A C++ doc hpp writer */
object CppDocHppWriter extends CppDocWriter {

  override def visitCppDoc(cppDoc: CppDoc): Output = {
    val hppFile = cppDoc.hppFile
    val cppFileName = cppDoc.cppFileName
    val in = Input(hppFile, cppFileName)
    val body = cppDoc.members.foldRight(Output())(
      { case (member, output) => visitMember(in, member) ++ output }
    )
    List(
      Output(CppDocWriter.writeBanner(in.hppFile.name), CppDocWriter.writeBanner(in.cppFileName)),
      Output.hpp(CppDocWriter.openIncludeGuard(hppFile.includeGuard)),
      body,
      Output.hpp(CppDocWriter.closeIncludeGuard)
    ).fold(Output())(_ ++ _)
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
    val output = {
      val Output(outputLines, _) = c.members.foldRight(Output())(
        { case (member, output) => visitClassMember(in1, member) ++ output }
      )
      Output(outputLines.map(_.indentIn(2 * indentIncrement)), Nil)
    }
    val endLines = List(
      Line.blank,
      line("};")
    )
    Output.hpp(startLines) ++ output ++ Output.hpp(endLines)
  }

  override def visitConstructor(in: Input, constructor: CppDoc.Class.Constructor) = {
    val unqualifiedClassName = in.getEnclosingClassUnqualified
    val qualifiedClassName = in.getEnclosingClassQualified
    val outputLines = {
      val lines1 = CppDocWriter.doxygenCommentOpt(constructor.comment)
      val lines2 = {
        val params = CppDocWriter.writeHppParams(unqualifiedClassName, constructor.params)
        Line.addSuffix(params, ";")
      }
      lines1 ++ lines2
    }
    Output(outputLines, Nil)
  }

  override def visitDestructor(in: Input, destructor: CppDoc.Class.Destructor) = {
    val unqualifiedClassName = in.getEnclosingClassUnqualified
    val qualifiedClassName = in.getEnclosingClassQualified
    val outputLines = {
      val lines1 = CppDocWriter.doxygenCommentOpt(destructor.comment)
      val lines2 = destructor.virtualQualifier match {
        case CppDoc.Class.Destructor.Virtual => lines(s"virtual ~$unqualifiedClassName();")
        case _ => lines(s"~$unqualifiedClassName();")
      }
      lines1 ++ lines2
    }
    Output(outputLines, Nil)
  }

  override def visitFunction(in: Input, function: CppDoc.Function) = {
    import CppDoc.Function._
    val outputLines = {
      val lines1 = CppDocWriter.doxygenCommentOpt(function.comment)
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
          val lines1 = CppDocWriter.writeHppParams(prefix, function.params)
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
    Output(outputLines, Nil)
  }

  override def visitLines(in: Input, lines: CppDoc.Lines) = {
    val content = lines.content
    lines.output match {
      case CppDoc.Lines.Hpp => Output.hpp(content)
      case CppDoc.Lines.Cpp => Output()
      case CppDoc.Lines.Both => Output.hpp(content)
    }
  }

  override def visitNamespace(in: Input, namespace: CppDoc.Namespace): Output = {
    val name = namespace.name
    val output = namespace.members.foldRight(Output())(
      { case (member, output) => visitNamespaceMember(in, member) ++ output }
    )
    val startLines = List(Line.blank, line(s"namespace $name {"))
    val endLines = List(Line.blank, line("}"))
    val startOutput = Output.hpp(startLines)
    val endOutput = Output.hpp(endLines)
    startOutput ++ output.indentIn() ++ endOutput
  }

}
