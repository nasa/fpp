package fpp.compiler.codegen

import fpp.compiler.analysis._

/** Utilities for writing C++ */
trait CppWriterUtils extends LineUtils {

  /** Standard system hpp headers */
  val standardSystemHppHeaders = List(
    "FpConfig.hpp"
  ).map(CppWriter.systemHeaderString)

  /** Standard user hpp headers */
  val standardUserHppHeaders = List(
    "Fw/Types/ExternalString.hpp",
    "Fw/Types/Serializable.hpp",
    "Fw/Types/String.hpp"
  ).map(CppWriter.headerString)

  /** Standard system cpp headers */
  val standardSystemCppHeaders = Nil

  /** Standard user cpp headers */
  val standardUserCppHeaders = List(
    "Fw/Types/Assert.hpp"
  ).map(CppWriter.headerString)

  /** Guards a value with a Boolean condition */
  def guardedValue[T] (default: T) (cond: Boolean) (value: => T) =
    if cond then value else default

  /** Guards a list with a Boolean condition */
  def guardedList[T] = guardedValue (Nil: List[T]) _

  /** Guards an option type with a Boolean condition */
  def guardedOption[T] = guardedValue (None: Option[T]) _

  /** Add an access tag and comment to a nonempty list of class members */
  def addAccessTagAndComment(
    accessTag: String,
    comment: String,
    members: List[CppDoc.Class.Member],
    output: CppDoc.Lines.Output = CppDoc.Lines.Both,
    cppFileNameBaseOpt: Option[String] = None
  ): List[CppDoc.Class.Member] = members match {
    case Nil => Nil
    case _ =>
      linesClassMember(CppDocHppWriter.writeAccessTag(accessTag)) ::
      linesClassMember(CppDocWriter.writeBannerComment(comment), output, cppFileNameBaseOpt) ::
      members
  }

  /** Add an optional string separated by two newlines */
  def addSeparatedString(str: String, strOpt: Option[String]): String = {
    strOpt match {
      case Some(s) => s"$str\n\n$s"
      case None => str
    }
  }

  /** Add an optional pre comment separated by two newlines */
  def addSeparatedPreComment(str: String, commentOpt: Option[String]): List[Line] = {
    commentOpt match {
      case Some(s) => List.concat(
        CppDocWriter.writeDoxygenComment(str + "\n\n" + s)
      )
      case None => CppDocWriter.writeDoxygenComment(str)
    }
  }

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

  def wrapInNamespaceLines(
    namespaceNames: List[String],
    ll: List[Line]
  ): List[Line] = namespaceNames match {
    case Nil => ll
    case head :: tail =>
      wrapInNamespace(head, wrapInNamespaceLines(tail, ll))
  }

  def wrapInNamedEnum(name: String, ll: List[Line]): List[Line] =
    wrapInScope(s"enum $name {", ll, "};")

  def wrapInNamedStruct(name: String, ll: List[Line]): List[Line] =
    wrapInScope(s"struct $name {", ll, "};")

  def wrapInEnum(ll: List[Line]): List[Line] =
    wrapInScope("enum {", ll, "};")

  def wrapInEnumClass(
    name: String,
    ll: List[Line],
    tyOpt: Option[String]
  ): List[Line] = {
    val prefix = tyOpt match {
      case Some(ty) => s"enum class $name : $ty {"
      case None => s"enum class $name {"
    }
    wrapInScope(prefix, ll, "};")
  }

  def wrapInSwitch(condition: String, body: List[Line]) =
    wrapInScope(s"switch ($condition) {", body, "}")

  def wrapInForLoop(init: String, condition: String, step: String, body: List[Line]): List[Line] =
    wrapInScope(s"for ($init; $condition; $step) {", body, "}")

  def wrapInForLoopStaggered(init: String, condition: String, step: String, body: List[Line]): List[Line] =
    wrapInScope(
      s"""|for (
          |  $init;
          |  $condition;
          |  $step
          |) {
          |""",
      body,
      "}"
    )

  def wrapInIf(condition: String, body: List[Line]): List[Line] =
    wrapInScope(s"if ($condition) {", body, "}")

  def wrapInIfElse(condition: String, ifBody: List[Line], elseBody: List[Line]): List[Line] =
    wrapInIf(condition, ifBody) ++ wrapInScope("else {", elseBody, "}")

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
  ): List[T] = body match {
    case Nil => Nil
    case _ => List(
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
  }

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

  /** Write an enumerated constant */
  def writeEnumConstant(
    name: String,
    value: BigInt,
    comment: Option[String] = None,
    radix: CppWriterUtils.Radix = CppWriterUtils.Decimal
  ): List[Line] = {
    val valueStr = radix match {
      case CppWriterUtils.Decimal => value.toString
      case CppWriterUtils.Hex => s"0x${value.toString(16)}"
    }

    CppDocHppWriter.addParamComment(s"$name = $valueStr,", comment)
  }

  /** Write a function call with fixed and variable arguments */
  def writeFunctionCall(
    name: String,
    args: List[String],
    variableArgs: List[String] = Nil
  ): List[Line] = variableArgs match {
    case Nil => lines(
      s"$name(${args.mkString(", ")});"
    )
    case _ => wrapInScope(
      s"$name(",
      lines(
        (args ++ variableArgs).mkString(",\n")
      ),
      ");"
    )
  }

  /** Write a variable declaration */
  def writeVarDecl(s: CppWriterState, typeName: String, name: String, t: Type): String =
    t match {
      case st: Type.String =>
        val bufferName = getBufferName(name)
        val size = writeStringSize(s, st)
        s"""|char ${bufferName}[Fw::StringBase::BUFFER_SIZE($size)];
            |Fw::ExternalString $name($bufferName, sizeof $bufferName);""".stripMargin
      case _ => s"$typeName $name;"
    }

  /** Write a member declaration */
  def writeMemberDecl(
    s: CppWriterState,
    typeName: String,
    name: String,
    t: Type,
    prefix: String = "",
    arraySize: Option[String] = None
  ): String = {
    val arrayBrackets = arraySize.map(s => s"[$s]").getOrElse("")
    t match {
      case st: Type.String =>
        val bufferName = getBufferName(name)
        val size = writeStringSize(s, st)
        s"""|char ${prefix}${bufferName}${arrayBrackets}[Fw::StringBase::BUFFER_SIZE($size)];
            |Fw::ExternalString ${prefix}${name}${arrayBrackets};""".stripMargin
      case _ => s"$typeName ${prefix}${name}${arrayBrackets};"
    }
  }

  /** Get a buffer name */
  def getBufferName(name: String) = s"__fprime_ac_${name}_buffer"

  /** Gets the size of a string type */
  def getStringSize(s: CppWriterState, t: Type.String): BigInt =
    t.size.map(
      node => {
        val _ @ Value.Integer(value) = s.a.valueMap(node.id).convertToType(Type.Integer).get
        value
      }
    ).getOrElse(BigInt(s.defaultStringSize))

  /** Write the size of a string type */
  def writeStringSize(s: CppWriterState, t: Type.String): String =
    t.size.map(node => ValueCppWriter.write(s, s.a.valueMap(node.id))).
      getOrElse(s.defaultStringSize.toString)

  /** Write a C++ expression for static serialized size */
  def writeSerializedSizeExpr(s: CppWriterState, t: Type, typeName: String): String =
    (t.getUnderlyingType, s.isPrimitive(t, typeName))  match {
      // sizeof(bool) is not defined in C++
      // F Prime serializes bool as U8
      case (Type.Boolean, _)=> "sizeof(U8)"
      case (ts: Type.String, _) =>
        lazy val stringSizeExpr = {
          val serialSize = writeStringSize(s, ts)
          s"Fw::StringBase::STATIC_SERIALIZED_SIZE($serialSize)"
        }
        typeName match {
          case "Fw::StringBase" => stringSizeExpr
          case "Fw::ExternalString" => stringSizeExpr
          case _ => s"$typeName::SERIALIZED_SIZE"
        }
      case (_, true) => s"sizeof($typeName)"
      case _ => s"$typeName::SERIALIZED_SIZE"
    }

  /** Explicitly promotes an F32 value to an F64 value */
  def promoteF32ToF64 (t: Type) (v: String) =
    if t == Type.F32
    then s"static_cast<F64>($v)"
    else v

  def classMember(
    comment: Option[String],
    name: String,
    superclassDecls: Option[String],
    members: List[CppDoc.Class.Member],
    qualifier: CppDoc.Class.FinalQualifier = CppDoc.Class.NonFinal
  ): CppDoc.Member.Class =
    CppDoc.Member.Class(
      CppDoc.Class(
        comment,
        name,
        superclassDecls,
        members,
        qualifier
      )
    )

  def classClassMember(
    comment: Option[String],
    name: String,
    superclassDecls: Option[String],
    members: List[CppDoc.Class.Member],
  ): CppDoc.Class.Member.Class =
    CppDoc.Class.Member.Class(
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
    cppFileNameBaseOpt: Option[String] = None
  ): CppDoc.Member.Function =
    CppDoc.Member.Function(
      CppDoc.Function(
        comment,
        name,
        params,
        retType,
        body,
        svQualifier,
        constQualifier,
        cppFileNameBaseOpt
      )
    )

  def linesMember(
    content: List[Line],
    output: CppDoc.Lines.Output = CppDoc.Lines.Hpp,
    cppFileNameBaseOpt: Option[String] = None
  ): CppDoc.Member.Lines = CppDoc.Member.Lines(CppDoc.Lines(content, output, cppFileNameBaseOpt))

  def namespaceMember(
    name: String,
    members: List[CppDoc.Member]
  ): CppDoc.Member.Namespace = CppDoc.Member.Namespace(CppDoc.Namespace(name, members))

  def constructorClassMember(
    comment: Option[String],
    params: List[CppDoc.Function.Param],
    initializers: List[String],
    body: List[Line],
    cppFileNameBaseOpt: Option[String] = None
  ): CppDoc.Class.Member.Constructor =
    CppDoc.Class.Member.Constructor(
      CppDoc.Class.Constructor(
        comment,
        params,
        initializers,
        body,
        cppFileNameBaseOpt
      )
    )

  def destructorClassMember(
    comment: Option[String],
    body: List[Line],
    virtualQualifier: CppDoc.Class.Destructor.VirtualQualifier = CppDoc.Class.Destructor.NonVirtual,
    cppFileNameBaseOpt: Option[String] = None
  ): CppDoc.Class.Member.Destructor =
    CppDoc.Class.Member.Destructor(
      CppDoc.Class.Destructor(
        comment,
        body,
        virtualQualifier,
        cppFileNameBaseOpt
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
    cppFileNameBaseOpt: Option[String] = None
  ): CppDoc.Class.Member.Function =
    CppDoc.Class.Member.Function(
      CppDoc.Function(
        comment,
        name,
        params,
        retType,
        body,
        svQualifier,
        constQualifier,
        cppFileNameBaseOpt
      )
    )

  def linesClassMember(
    content: List[Line],
    output: CppDoc.Lines.Output = CppDoc.Lines.Hpp,
    cppFileNameBaseOpt: Option[String] = None
  ): CppDoc.Class.Member.Lines =
    CppDoc.Class.Member.Lines(
      CppDoc.Lines(
        content,
        output,
        cppFileNameBaseOpt
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

object CppWriterUtils {

  sealed trait Radix
  case object Decimal extends Radix
  case object Hex extends Radix

}
