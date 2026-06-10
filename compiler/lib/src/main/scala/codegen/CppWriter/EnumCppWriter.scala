package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for enum definitions */
case class EnumCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Enum(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getEnum(name)

  private val enumType @ Type.Enum(_, _, _) = s.a.typeMap(node.id)

  private val defaultValue = ValueCppWriter.write(s, enumType.getDefaultValue.get).
    replaceAll("^.*::", "")

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val repTypeName = typeCppWriter.write(enumType.repType)

  private val numConstants = data.constants.size

  /** The set of enumerated constants, expressed as a list of
   *  closed intervals. For example, the set of enumerated constants
   *  { 0, 1, 3 } yields the list { [ 0, 1 ], [ 3, 3 ] }. */
  private val intervals = {
    val values = data.constants.map(aNode => {
      val Value.EnumConstant(value, _) = s.a.valueMap(aNode._2.id)
      value
    }).sortWith(_._2 < _._2)
    val state = values.foldLeft (EnumCppWriter.IntervalState()) ((s, v) => {
        s.lastInterval match {
          case None => s.copy(lastInterval = Some(v,v))
          case Some(lower, upper) =>
            if (v._2 == upper._2 + 1) s.copy(lastInterval = Some(lower, v))
            else s.copy(
              intervals = (lower, upper) :: s.intervals,
              lastInterval = Some(v,v)
            )
        }
    })
    (state.lastInterval.get :: state.intervals).reverse
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name enum",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = classMember(
      AnnotationCppWriter.asStringOpt(aNode),
      name,
      Some("public Fw::Serializable"),
      getClassMembers
    )
    List(
      List(hppIncludes, cppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val strings = List(
      "Fw/FPrimeBasicTypes.hpp",
      "Fw/Types/Assert.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/String.hpp"
    )
    linesMember(
      Line.blank ::
      strings.map(CppWriter.headerString).map(line)
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemStrings = List("cstring", "limits")
    val strings = List(s.getIncludePath(symbol, fileName))
    linesMember(
      List(
        List(Line.blank),
        systemStrings.map(CppWriter.systemHeaderString).map(line),
        List(Line.blank),
        strings.map(CppWriter.headerString).map(line)
      ).flatten,
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List(
      getTypeMembers,
      getConstantMembers,
      getConstructorMembers,
      getOperatorMembers,
      getMemberFunctionMembers,
      StaticFunctionMembers.get,
      getMemberVariableMembers
    ).flatten

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public") ++
        CppDocWriter.writeBannerComment("Constants") ++
        addBlankPrefix(
          wrapInEnum(
            lines(
              s"""|//! The size of the serial representation
                  |SERIALIZED_SIZE = sizeof(SerialType),
                  |//! The number of enumerated constants
                  |NUM_CONSTANTS = $numConstants,"""
            )
          )
        )
      )
    )

  private def getTypeMembers: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("public"),
          CppDocWriter.writeBannerComment("Types"),
          lines(
            s"""|
                |//! The serial representation type
                |using SerialType = $repTypeName;
                |
                |//! The raw enum type"""
          ),
          wrapInScope(
            "enum T {",
            data.constants.flatMap(aNode => {
              val node = aNode._2
              val Value.EnumConstant(value, _) = s.a.valueMap(node.id)
              val valueString = value._2.toString
              val name = node.data.name
              AnnotationCppWriter.writePreComment(aNode) ++
              lines(s"$name = $valueString,")
            }),
            "};"
          ),
          lines(
            s"""|
                |//! For backwards compatibility
                |using t = enum T;"""
          ),
        ).flatten
      )
    )
  }

  private def getConstructorMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("public"),
          CppDocWriter.writeBannerComment("Constructors"),
          lines(
            s"""|
                |//! Constructor (default value of $defaultValue)
                |$name()
                |{
                |  this->e = $defaultValue;
                |}
                |
                |//! Constructor (user-provided value)
                |$name(
                |    const enum T e1 //!< The raw enum value
                |)
                |{
                |  FW_ASSERT(isValid(e1), static_cast<FwAssertArgType>(e1));
                |  this->e = e1;
                |}
                |
                |//! Copy constructor
                |$name(
                |    const $name& obj //!< The source object
                |)
                |{
                |  this->e = obj.e;
                |#ifdef BUILD_UT
                |this->m_serializeNumericValue = obj.m_serializeNumericValue;
                |this->m_numericValue = obj.m_numericValue;
                |#endif
                |}"""

          )
        ).flatten
      ),
    )

  private def getOperatorMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Operators"),
        CppDoc.Lines.Both
      ),
      functionClassMember(
        Some(s"Copy assignment operator (object)"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $name&"),
            "obj",
            Some("The source object"),
          ),
        ),
        CppDoc.Type(s"$name&"),
        lines(
          """|this->e = obj.e;
             |#ifdef BUILD_UT
             |this->m_serializeNumericValue = obj.m_serializeNumericValue;
             |this->m_numericValue = obj.m_numericValue;
             |#endif
             |return *this;"""
        )
      ),
      functionClassMember(
        Some(s"Copy assignment operator (raw enum)"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("enum T"),
            "e1",
            Some("The enum value"),
          ),
        ),
        CppDoc.Type(s"$name&"),
        lines(
          s"""|FW_ASSERT(isValid(e1), static_cast<FwAssertArgType>(e1));
              |this->e = e1;
              |#ifdef BUILD_UT
              |this->m_serializeNumericValue = false;
              |this->m_numericValue = $defaultValue;
              |#endif
              |return *this;"""
        )
      ),
      linesClassMember(
        lines(
          """|
             |//! Conversion operator
             |operator enum T() const
             |{
             |  return this->e;
             |}
             |
             |//! Equality operator
             |bool operator==(enum T e1) const
             |{
             |  return this->e == e1;
             |}
             |
             |//! Inequality operator
             |bool operator!=(enum T e1) const
             |{
             |  return !(*this == e1);
             |}"""
        )
      ),
    ) ++ writeOstreamOperator(
      name,
      lines(
        """|Fw::String s;
           |obj.toString(s);
           |os << s;
           |return os;"""
      )
    )

  private def getMemberFunctionMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Member functions"),
        CppDoc.Lines.Both
      ),
      functionClassMember(
        Some(s"Check raw enum value for validity"),
        "isValid",
        Nil,
        CppDoc.Type("bool"),
        lines("return isValid(this->e);"),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      ),
      functionClassMember(
        Some(s"Serialize raw enum value to SerialType"),
        "serializeTo",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerialBufferBase&"),
            "buffer",
            Some("The serial buffer")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("Fw::Endianness"),
            "mode",
            Some("Endianness of serialized buffer"),
            Some("Fw::Endianness::BIG"),
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        lines(
          s"""|SerialType es = static_cast<SerialType>(this->e);
              |#ifdef BUILD_UT
              |// Unit testing only: On request, override the enum value
              |// with the numeric value, which is allowed to be invalid
              |if (this->m_serializeNumericValue) {
              |  es = this->m_numericValue;
              |}
              |#endif
              |const Fw::SerializeStatus status = buffer.serializeFrom(es, mode);
              |return status;"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      ),
      functionClassMember(
        Some(s"Deserialize raw enum value from SerialType"),
        "deserializeFrom",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerialBufferBase&"),
            "buffer",
            Some("The serial buffer")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("Fw::Endianness"),
            "mode",
            Some("Endianness of serialized buffer"),
            Some("Fw::Endianness::BIG"),
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        lines(
          s"""|SerialType es;
              |Fw::SerializeStatus status = buffer.deserializeTo(es, mode);
              |if ((status == Fw::FW_SERIALIZE_OK) && !isValid(es)) {
              |  status = Fw::FW_DESERIALIZE_FORMAT_ERROR;
              |}
              |if (status == Fw::FW_SERIALIZE_OK) {
              |  this->e = static_cast<enum T>(es);
              |}
              |return status;"""
        )
      )
    ) ++
      List.concat(
        List(
          linesClassMember(
            lines("\n#if FW_SERIALIZABLE_TO_STRING"),
            CppDoc.Lines.Cpp
          )
        ),
        wrapClassMembersInIfDirective(
          "#if FW_SERIALIZABLE_TO_STRING",
          List(
            functionClassMember(
              Some(s"Convert enum to string"),
              "toString",
              List(
                CppDoc.Function.Param(
                  CppDoc.Type("Fw::StringBase&"),
                  "sb",
                  Some("The StringBase object to hold the result")
                )
              ),
              CppDoc.Type("void"),
              List(
                lines(
                  s"""|Fw::String s;"""
                ),
                wrapInScope(
                  "switch (e) {",
                  data.constants.flatMap(aNode => {
                    val enumName = aNode._2.data.name
                    lines(
                      s"""|case $enumName:
                          |  s = "$enumName";
                          |  break;"""
                    )
                  }) ++
                    lines(
                      """|default:
                         |  s = "[invalid]";
                         |  break;"""
                    ),
                  "}"
                ),
                lines(
                  s"""|sb.format("%s ($writeFormatStr)", s.toChar(), e);"""
                )
              ).flatten,
              CppDoc.Function.NonSV,
              CppDoc.Function.Const
            )
          ),
          CppDoc.Lines.Hpp
        ),
        List(
          linesClassMember(
            lines(
              s"""|
                  |#elif FW_ENABLE_TEXT_LOGGING
                  |
                  |void $name ::
                  |  toString(Fw::StringBase& sb) const
                  |{
                  |  sb.format("$writeFormatStr", e);
                  |}
                  |
                  |#endif
                  |"""
            ),
            CppDoc.Lines.Cpp
          )
        )
      )

  private object StaticFunctionMembers {

    def get: List[CppDoc.Class.Member] =
      addAccessTagAndComment(
        "public",
        "Static functions",
        List(getIsValidFunction)
      )

    private def getIsValidFunction =
      functionClassMember(
        Some(s"Check serial type value for validity"),
        "isValid",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("SerialType"),
            "serialTypeValue",
            Some("The serial type value")
          )
        ),
        CppDoc.Type("bool"),
        Line.addPrefixAndSuffix(
          "return ",
          writeIntervals("serialTypeValue", intervals),
          ";"
        ),
        CppDoc.Function.Static
      )

  }

  private def getMemberVariableMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Member variables",
      List(
        linesClassMember(
          lines(
            s"""|
                |//! The raw enum value
                |enum T e;
                |
                |#ifdef BUILD_UT
                |
                |//! Whether to use the numeric value when serializing the enum
                |//! (unit testing only). This allows serialization of invalid values
                |//! that can't be represented as the raw enum type.
                |bool m_serializeNumericValue = false;
                |
                |//! The numeric value
                |SerialType m_numericValue = $defaultValue;
                |
                |#endif"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def writeInterval(v: String, c: EnumCppWriter.Interval) = {
    val (lower, upper) = c
    s"(($v >= ${lower._1}) && ($v <= ${upper._1}))"
  }

  private def writeIntervals(v: String, cs: List[EnumCppWriter.Interval]) =
    line(writeInterval(v, cs.head)) ::
    cs.tail.map(c => line(s"|| ${writeInterval(v, c)}")).map(indentIn)

  private def writeFormatStr = {
    val pit @ Type.PrimitiveInt(_) =
      data.typeName.map(tn => s.a.typeMap(tn.id).getUnderlyingType).
      getOrElse(Type.I32)
    FormatCppWriter.getDecimalFormat(pit)
  }

}

object EnumCppWriter {

  private type Bound = (Name.Unqualified, BigInt)

  private type Interval = (Bound, Bound)

  /** The state for computing invervals */
  private case class IntervalState(
    /** The current list of intervals */
    intervals: List[Interval] = Nil,
    /** The last interval computed */
    lastInterval: Option[Interval] = None,
  )

}
