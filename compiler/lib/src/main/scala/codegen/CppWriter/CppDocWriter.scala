package fpp.compiler.codegen

/** A C++ doc writer */
object CppDocWriter extends CppDocVisitor with LineUtils {

  case class Input(
    /** The current hpp file */
    hppFile: String,
    /** The list of name qualifiers, backwards */
    qualifierList: List[String] = Nil,
  )


  type Output = Map[String,List[Line]]

  def default(in: Input) = Map()

  /*
  def visitClass(in: Input, c: CppDoc.Class): Output = default(in)
 
  def visitConstructor(in: Input, constructor: CppDoc.Class.Constructor): Output = default(in)

  def visitFunction(in: Input, function: CppDoc.Function): Output = default(in)
  */

  override def visitLines(in: Input, lines: CppDoc.Lines) = lines.output match {
    case CppDoc.Lines.Hpp => Map(in.hppFile -> lines.content)
    case CppDoc.Lines.Cpp(cppFile) => Map(cppFile -> lines.content)
    case CppDoc.Lines.Both(cppFile) => Map(in.hppFile -> lines.content, cppFile -> lines.content)
  }

  override def visitNamespace(in: Input, namespace: CppDoc.Namespace): Output = {
    val name = namespace.name
    val newQualifierList = name :: in.qualifierList
    val output = namespace.members.map(visitNamespaceMember(in, _)).fold(Map())(mergeOutput(_, _))
    val lines1 = List(
      Line.blank,
      line(s"namespace $name {"),
      Line.blank
    )
    val lines2 = List(
      Line.blank,
      line("}")
    )
    output + (in.hppFile -> (lines1 ++ output(in.hppFile).map(indentIn(_)) ++ lines2))
  }

  def visitCppDoc(cppDoc: CppDoc): Output = {
    val hppFile = cppDoc.hppFile
    val in = Input(hppFile)
    // TODO: Write out banner
    val output = Map(hppFile -> openIncludeGuard(cppDoc.includeGuard))
    val output1 = cppDoc.members.map(visitMember(in, _)).fold(output)(mergeOutput(_, _))
    val output2 = output1 + (hppFile -> (output1(hppFile) ++ closeIncludeGuard))
    // TODO: Move # directives back to left-hand side
    output2
  }

  private def openIncludeGuard(guard: String): List[Line] = {
    lines(
      s"""|
          |#ifndef $guard
          |#define $guard"""
    )
  }

  private def closeIncludeGuard = lines(
    """|
       |#endif"""
  )

  private def mergeOutput(output1: Output, output2: Output) =
    output2.foldRight(output1)({ case (k -> v, map) => map + (k -> (map.getOrElse(k, Nil) ++ v)) })

}
