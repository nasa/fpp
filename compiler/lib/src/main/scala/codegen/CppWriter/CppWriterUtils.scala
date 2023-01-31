package fpp.compiler.codegen

/** Utilities for writing C++ */
trait CppWriterUtils extends LineUtils {

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

  def classMember(
    comment: Option[String],
    name: String,
    superclassDecls: Option[String],
    members: List[CppDoc.Class.Member],
  ): CppDoc.Member.Class =
    CppDoc.Member.Class(
      CppDoc.Class(
        comment,
        name,
        superclassDecls,
        members
      )
    )

  def functionMember(
    comment: Option[String],
    name: String,
    params: List[CppDoc.Function.Param],
    retType: CppDoc.Type,
    body: List[Line],
    svQualifier: CppDoc.Function.SVQualifier = CppDoc.Function.NonSV,
    constQualifier: CppDoc.Function.ConstQualifier = CppDoc.Function.NonConst,
  ): CppDoc.Member.Function =
    CppDoc.Member.Function(
      CppDoc.Function(
        comment,
        name,
        params,
        retType,
        body,
        svQualifier,
        constQualifier
      )
    )

  def linesMember(
    content: List[Line],
    output: CppDoc.Lines.Output = CppDoc.Lines.Hpp
  ): CppDoc.Member.Lines = CppDoc.Member.Lines(CppDoc.Lines(content, output))

  def namespaceMember(
    name: String,
    members: List[CppDoc.Member]
  ): CppDoc.Member.Namespace = CppDoc.Member.Namespace(CppDoc.Namespace(name, members))

  def constructorClassMember(
    comment: Option[String],
    params: List[CppDoc.Function.Param],
    initializers: List[String],
    body: List[Line]
  ): CppDoc.Class.Member.Constructor =
    CppDoc.Class.Member.Constructor(
      CppDoc.Class.Constructor(
        comment,
        params,
        initializers,
        body
      )
    )

  def destructorClassMember(
    comment: Option[String],
    body: List[Line],
    virtualQualifier: CppDoc.Class.Destructor.VirtualQualifier = CppDoc.Class.Destructor.NonVirtual
  ): CppDoc.Class.Member.Destructor =
    CppDoc.Class.Member.Destructor(
      CppDoc.Class.Destructor(
        comment,
        body,
        virtualQualifier
      )
    )

  def functionClassMember(
    comment: Option[String],
    name: String,
    params: List[CppDoc.Function.Param],
    retType: CppDoc.Type,
    body: List[Line],
    svQualifier: CppDoc.Function.SVQualifier = CppDoc.Function.NonSV,
    constQualifier: CppDoc.Function.ConstQualifier = CppDoc.Function.NonConst,
  ): CppDoc.Class.Member.Function =
    CppDoc.Class.Member.Function(
      CppDoc.Function(
        comment,
        name,
        params,
        retType,
        body,
        svQualifier,
        constQualifier
      )
    )

  def linesClassMember(
    content: List[Line],
    output: CppDoc.Lines.Output = CppDoc.Lines.Hpp
  ): CppDoc.Class.Member.Lines =
    CppDoc.Class.Member.Lines(
      CppDoc.Lines(
        content,
        output
      )
    )

  def wrapInNamespaces(
    namespaceNames: List[String],
    members: List[CppDoc.Member]
  ): List[CppDoc.Member] = namespaceNames match {
    case Nil => members
    case head :: tail =>
      List(namespaceMember(head, wrapInNamespaces(tail, members)))
  }

}
