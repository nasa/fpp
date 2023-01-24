package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Utilities for writing C++ lines */
trait CppWriterLineUtils extends LineUtils {

  def wrapInScope(
    s1: String,
    ll: List[Line],
    s2: String
  ): List[Line] = ll match {
    case Nil => Nil
    case _ => List(lines(s1), ll.map(indentIn), lines(s2)).flatten
  }

  def wrapInAnonymousNamespace(ll: List[Line]): List[Line] =
    wrapInScope("namespace {", ll, "}")

  def wrapInNamespace(namespace: String, ll: List[Line]): List[Line] =
    wrapInScope(s"namespace $namespace {", ll, "}")

  def wrapInEnum(ll: List[Line]): List[Line] =
    wrapInScope("enum {", ll, "};")

  def wrapInForLoop(init: String, condition: String, step: String, body: List[Line]): List[Line] =
    wrapInScope(s"for ($init; $condition; $step) {", body, "}")

  def wrapInIf(condition: String, body: List[Line]): List[Line] =
    wrapInScope(s"if ($condition) {", body, "}")

  def wrapMembersInIfDirective(
    directive: String,
    body: List[CppDoc.Member],
    linesOutput: CppDoc.Lines.Output = CppDoc.Lines.Both
  ): List[CppDoc.Member] =
    wrapInIfDirective(
      directive,
      body,
      CppDoc.Member.Lines.apply,
      linesOutput
    )

  def wrapClassMembersInIfDirective(
    directive: String,
    body: List[CppDoc.Class.Member],
    linesOutput: CppDoc.Lines.Output = CppDoc.Lines.Both
  ): List[CppDoc.Class.Member] =
    wrapInIfDirective(
      directive,
      body,
      CppDoc.Class.Member.Lines.apply,
      linesOutput
    )

  def wrapInIfDirective[T](
    directive: String,
    body: List[T],
    constructMemberLines: CppDoc.Lines => T,
    linesOutput: CppDoc.Lines.Output = CppDoc.Lines.Both
  ): List[T] =
    List(
      List(
        constructMemberLines(
          CppDoc.Lines(
            lines(directive),
            linesOutput
          )
        )
      ),
      body,
      List(
        constructMemberLines(
          CppDoc.Lines(
            Line.blank :: lines("#endif"),
            linesOutput
          )
        )
      ),
    ).flatten

  def writeOstreamOperator(
    name: String,
    body: List[Line]
  ): List[CppDoc.Class.Member] =
    wrapClassMembersInIfDirective(
      "#ifdef BUILD_UT",
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines(
              s"""|
                  |//! Ostream operator
                  |friend std::ostream& operator<<(
                  |    std::ostream& os, //!< The ostream
                  |    const $name& obj //!< The object
                  |);"""
            )
          )
        ),
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            wrapInScope(
              s"\nstd::ostream& operator<<(std::ostream& os, const $name& obj) {",
              body,
              "}"
            ),
            CppDoc.Lines.Cpp
          )
        ),
      )
    )

  /** Writes a type as a C++ type */
  def writeCppTypeName(
    t: Type,
    s: CppWriterState,
    namespaceNames: List[String] = Nil,
    strName: Option[String] = None
  ): String =
    t match {
      case t: Type.String => strName match {
        case Some(name) => name
        case None => StringCppWriter(s).getQualifiedClassName(t, namespaceNames)
      }
      case t =>
        TypeCppWriter(s).write(t)
    }

  /** Writes a formal parameter as a C++ parameter */
  def writeFormalParam(
    param: Ast.FormalParam,
    s: CppWriterState,
    namespaceNames: List[String] = Nil,
    strName: Option[String] = None
  ): String = {
    val t = s.a.typeMap(param.typeName.id)
    val typeName = writeCppTypeName(t, s, namespaceNames, strName)

    (t, param.kind) match {
      // Reference formal parameters become non-constant C++ reference parameters
      case (_, Ast.FormalParam.Ref) => s"$typeName&"
      // Primitive, non-reference formal parameters become C++ value parameters
      case (t, Ast.FormalParam.Value) if s.isPrimitive(t, typeName) => typeName
      // Other non-reference formal parameters become constant C++ reference parameters
      case (_, Ast.FormalParam.Value) => s"const $typeName&"
    }
  }

  /** Writes a list of formal parameters as a list of CppDoc Function Params */
  def writeFormalParamList(
    params: Ast.FormalParamList,
    s: CppWriterState,
    namespaceNames: List[String] = Nil,
    strName: Option[String] = None
  ): List[CppDoc.Function.Param] =
    params.map(aNode => {
      CppDoc.Function.Param(
        CppDoc.Type(writeFormalParam(aNode._2.data, s, namespaceNames, strName)),
        aNode._2.data.name,
        AnnotationCppWriter.asStringOpt(aNode)
      )
    })

}
