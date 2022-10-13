package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Writes out C++ for struct definitions */
case class StructCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
) extends CppWriterLineUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Struct(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getStruct(name)

  private val structType@Type.Struct(_, _, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val strCppWriter = StringCppWriter(s, Some(name))

  private val members = structType.anonStruct.members

  private val typeMembers = data.members

  private val sizes = structType.sizes

  private val formats = structType.formats

  // List of tuples (<memberName>, <memberTypeName>)
  // Preserves ordering of struct members
  private val memberList = typeMembers.map((_, node, _) => {
    val n = node.data.name
    members(n) match {
      case t: Type.String => (n, strCppWriter.getClassName(t))
      case t => (n, typeCppWriter.write(t))
    }
  })

  private val memberNames = memberList.map((n, _) => n)

  private val arrayMemberNames = memberNames.filter(sizes.contains)

  private val nonArrayMemberNames = memberNames.filterNot(sizes.contains)

  // Returns map from member name to its default value
  private def getDefaultValues = {
    val defaultValue = structType.getDefaultValue match {
      case Some(s) => Some(s.anonStruct)
      case None => structType.anonStruct.getDefaultValue
    }
    defaultValue.get.members
  }

  private def getFormatStr(n: String) =
    if formats.contains(n) then formats(n)
    else Format("", List((Format.Field.Default, "")))

  private def getArrayTypeName(n: String) = s"Type_of_$n"

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name struct",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = CppDoc.Member.Class(
      CppDoc.Class(
        AnnotationCppWriter.asStringOpt(aNode),
        name,
        Some("public Fw::Serializable"),
        // If struct is empty, write an empty class
        if memberList.isEmpty then Nil
        else getClassMembers
      )
    )
    List(
      // If struct is empty, write an empty .cpp file
      if memberList.isEmpty then List(hppIncludes)
      else List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def writeIncludeDirectives = {
    val Right(a) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  private def getHppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "FpConfig.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/String.hpp"
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = userHeaders ++ symbolHeaders
    CppWriter.linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemheaders = List(
      "cstdio",
      "cstring",
    ).map(CppWriter.headerString).map(line)
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/StringUtils.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp",
    ).sorted.map(CppWriter.headerString).map(line)
    CppWriter.linesMember(
      List(
        Line.blank :: systemheaders,
        Line.blank :: userHeaders,
      ).flatten,
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List(
      getStringClasses,
      getTypeMembers,
      getConstantMembers,
      getConstructorMembers,
      getOperatorMembers,
      getMemberFunctionMembers,
      getMemberVariableMembers,
    ).flatten

  private def getStringClasses: List[CppDoc.Class.Member] = {
    val strTypes = members.map((_, t) => t match {
      case t: Type.String => Some(t)
      case _ => None
    }).filter(_.isDefined).map(_.get).toList
    strTypes match {
      case Nil => Nil
      case l => strCppWriter.write(l)
    }
  }

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
            CppDocWriter.writeBannerComment("Constants") ++
            addBlankPrefix(
              wrapInEnum(
                List(
                  lines("//! The size of the serial representation"),
                  lines("SERIALIZED_SIZE ="),
                  lines(memberList.map((n, tn) =>
                    s.getSerializedSizeExpr(members(n), tn) + (
                      if sizes.contains(n) then s" * ${sizes(n)}"
                      else ""
                      )).mkString(" +\n")).map(indentIn),
                ).flatten
              )
            )
        )
      )
    )

  private def getTypeMembers: List[CppDoc.Class.Member] =
    if sizes.isEmpty then Nil
    else
      // Write types for array members to simplify C++ array reference syntax
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment("Types"),
              Line.blank :: lines("//! The array member types"),
              memberList.filter((n, _) => sizes.contains(n)).map((n, tn) =>
                line(s"typedef $tn ${getArrayTypeName(n)}[${sizes(n)}];")
              )
            ).flatten
          )
        )
      )

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    val defaultValues = getDefaultValues
    // Only write this constructor if the struct contains an array
    val scalarConstructor =
      if sizes.isEmpty then None
      else Some(
        CppDoc.Class.Member.Constructor(
          CppDoc.Class.Constructor(
            Some("Member constructor (scalar values for arrays)"),
            memberList.map(writeMemberAsParamScalar),
            writeInitializerList(n => n),
            writeArraySetters(n => n)
          )
        )
      )

    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public")
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Constructors"),
          CppDoc.Lines.Both
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Constructor (default value)"),
          Nil,
          "Serializable()" :: nonArrayMemberNames.map(n => {
            defaultValues(n) match {
              case v: Value.Struct => s"$n(${ValueCppWriter.writeStructMembers(s, v)})"
              case _: Value.AbsType => s"$n()"
              case v => s"$n(${ValueCppWriter.write(s, v)})"
            }
          }),
          writeArraySetters(n => ValueCppWriter.write(s, defaultValues(n)))
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Member constructor"),
          memberList.map(writeMemberAsParam),
          writeInitializerList(n => n),
          writeArraySetters(n => s"$n[i]")
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Copy constructor"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "obj",
              Some("The source object")
            )
          ),
          writeInitializerList(n => s"obj.$n"),
          writeArraySetters(n => s"obj.$n[i]")
        )
      ),
    ) ++
      scalarConstructor
  }

  private def getOperatorMembers: List[CppDoc.Class.Member] = {
    val nonArrayMemberCheck = lines(
      nonArrayMemberNames.map(n => s"(this->$n == obj.$n)"
      ).mkString(" &&\n"))
    val equalityOpBody =
      // Simplify syntax if there are no array members
      if sizes.isEmpty then
        if nonArrayMemberNames.length == 1 then
          lines(s"return ${nonArrayMemberCheck.head};")
        else wrapInScope(
          "return (",
          nonArrayMemberCheck,
          ");"
        )
      else List(
        lines("// Compare non-array members"),
        if nonArrayMemberNames.length == 1 then
          wrapInIf(
            s"!${nonArrayMemberCheck.head}",
            lines("return false;")
          )
        else List(
          lines("if (!("),
          nonArrayMemberCheck.map(indentIn),
          lines(
            """|)) {
               |  return false;
               |}"""
          ),
        ).flatten,
        Line.blank :: lines("// Compare array members"),
        arrayMemberNames.flatMap(n =>
          wrapInIf(
            s"!(this->$n == obj.$n)",
            iterateN(
              sizes(n),
              wrapInIf(
                s"!(this->$n[i] == obj.$n[i])",
                lines("return false;")
              )
            )
          )
        ),
        Line.blank :: lines("return true;"),
      ).flatten

    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public")
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Operators"),
          CppDoc.Lines.Both
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Copy assignment operator"),
          "operator=",
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "obj",
              Some("The source object")
            ),
          ),
          CppDoc.Type(s"$name&"),
          List(
            wrapInIf("this == &obj", lines("return *this;")),
            Line.blank :: lines(
              s"set(${memberNames.map(n => s"obj.$n").mkString(", ")});"
            ),
            lines("return *this;"),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Equality operator"),
          "operator==",
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "obj",
              Some("The other object")
            )
          ),
          CppDoc.Type("bool"),
          equalityOpBody,
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        ),
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Inequality operator"),
          "operator!=",
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "obj",
              Some("The other object")
            )
          ),
          CppDoc.Type("bool"),
          lines("return !(*this == obj);"),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        ),
      ),
    ) ++ (
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(Line.blank),
          CppDoc.Lines.Both
        )
      ) :: writeOstreamOperator(
        name,
        lines(
          """|Fw::String s;
             |obj.toString(s);
             |os << s.toChar();
             |return os;"""
        )
      )
    )
  }

  private def getMemberFunctionMembers: List[CppDoc.Class.Member] = {
    // Members on which to call toString()
    val toStringMemberNames =
      memberList.filter((n, tn) => members(n) match {
        case _: Type.String => false
        case t if s.isPrimitive(t, tn) => false
        case _ => true
      }).map((n, _) => n)
    // String initialization for arrays and serializable member types in toString()
    val initStrings = toStringMemberNames match {
      case Nil => Nil
      case names =>
        List(
          Line.blank ::
            lines("// Declare strings to hold any serializable toString() arguments"),
          names.flatMap(n =>
            if sizes.contains(n) then
              lines(s"Fw::String ${n}Str[${sizes(n)}];")
            else
              lines(s"Fw::String ${n}Str;")
          ),
          Line.blank ::
            lines("// Call toString for arrays and serializable types"),
          names.flatMap(n =>
            if sizes.contains(n) then
              iterateN(sizes(n), lines(s"this->$n[i].toString(${n}Str[i]);"))
            else
              lines(s"this->$n.toString(${n}Str);")
          ),
        ).flatten
    }

    def writeSerializeStatusCheck =
      wrapInIf(
        "status != Fw::FW_SERIALIZE_OK",
        lines("return status;")
      )
    def writeSerializeCall(n: String) =
      line(s"status = buffer.serialize(this->$n);") :: writeSerializeStatusCheck
    def writeDeserializeCall(n: String) =
      line(s"status = buffer.deserialize(this->$n);") :: writeSerializeStatusCheck

    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(CppDocHppWriter.writeAccessTag("public"))
        ),
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            CppDocWriter.writeBannerComment("Member functions"),
            CppDoc.Lines.Both
          )
        ),
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some("Serialization"),
            "serialize",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("Fw::SerializeBufferBase&"),
                "buffer",
                Some("The serial buffer")
              )
            ),
            CppDoc.Type("Fw::SerializeStatus"),
            List(
              lines("Fw::SerializeStatus status;"),
              Line.blank :: memberNames.flatMap(n =>
                if sizes.contains(n) then
                  iterateN(sizes(n), writeSerializeCall(s"$n[i]"))
                else
                  writeSerializeCall(n)
              ),
              Line.blank :: lines("return status;"),
            ).flatten,
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          ),
        ),
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some("Deserialization"),
            "deserialize",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("Fw::SerializeBufferBase&"),
                "buffer",
                Some("The serial buffer")
              )
            ),
            CppDoc.Type("Fw::SerializeStatus"),
            List(
              lines("Fw::SerializeStatus status;"),
              Line.blank :: memberNames.flatMap(n =>
                if sizes.contains(n) then
                  iterateN(sizes(n), writeDeserializeCall(s"$n[i]"))
                else
                  writeDeserializeCall(n)
              ),
              Line.blank :: lines("return status;"),
            ).flatten
          )
        ),
      ),
      wrapClassMembersInIfDirective(
        "\n#if FW_SERIALIZABLE_TO_STRING || BUILD_UT",
        List(
          CppDoc.Class.Member.Function(
            CppDoc.Function(
              Some("Convert struct to string"),
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
                lines("static const char* formatString ="),
                lines(typeMembers.flatMap((_, node, _) => {
                  val n = node.data.name
                  val formatStr = FormatCppWriter.write(
                    getFormatStr(n),
                    node.data.typeName
                  )
                  if sizes.contains(n) then {
                    if sizes(n) == 1 then
                      List(s"$n = [ $formatStr ]")
                    else
                      s"$n = [ $formatStr" ::
                        List.fill(sizes(n) - 2)(formatStr) ++
                          List(s"$formatStr ]")
                  } else
                    List(s"$n = $formatStr")
                }).mkString("\"( \"\n\"", ", \"\n\"", "\"\n\" )\";")).map(indentIn),
                initStrings,
                Line.blank ::
                  lines("char outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE];"),
                wrapInScope(
                  "(void) snprintf(",
                  List(
                    List(
                      line("outputString,"),
                      line("FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE,"),
                      line("formatString,"),
                    ),
                    // Write format arguments
                    lines(memberList.flatMap((n, tn) =>
                      (sizes.contains(n), members(n)) match {
                        case (false, _: Type.String) =>
                          List(s"this->$n.toChar()")
                        case (false, t) if s.isPrimitive(t, tn) =>
                          List(s"this->$n")
                        case (false, _) =>
                          List(s"${n}Str.toChar()")
                        case (true, _: Type.String) =>
                          List.range(0, sizes(n)).map(i => s"this->$n[$i].toChar()")
                        case (true, t) if s.isPrimitive(t, tn) =>
                          List.range(0, sizes(n)).map(i => s"this->$n[$i]")
                        case _ =>
                          List.range(0, sizes(n)).map(i => s"${n}Str[$i].toChar()")
                      }).mkString(",\n")),
                  ).flatten,
                  ");"
                ),
                List(
                  Line.blank,
                  line("outputString[FW_SERIALIZABLE_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate"),
                  line("sb = outputString;"),
                ),
              ).flatten,
              CppDoc.Function.NonSV,
              CppDoc.Function.Const
            )
          )
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Getter functions"),
        )
      ) :: getGetterFunctionMembers,
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Setter functions"),
          CppDoc.Lines.Both
        )
      ) :: getSetterFunctionMembers,
    ).flatten
  }

  private def getGetterFunctionMembers: List[CppDoc.Class.Member] = {
    def getGetterName(n: String) = s"get$n"

    memberList.flatMap((n, tn) => (sizes.contains(n), members(n)) match {
      case (false, _: Type.Enum) => List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines(
              s"""|
                  |//! Get member $n
                  |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}() const
                  |{
                  |  return this->$n.e;
                  |}"""
            ),
          )
        )
      )
      case (false, t) if s.isPrimitive(t, tn) => List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines(
              s"""|
                  |//! Get member $n
                  |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}() const
                  |{
                  |  return this->$n;
                  |}"""
            ),
          )
        )
      )
      case _ => List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines(
              s"""|
                  |//! Get member $n
                  |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}()
                  |{
                  |  return this->$n;
                  |}"""
            ),
          )
        ),
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines(
              s"""|
                  |//! Get member $n (const)
                  |${writeMemberAsReturnType((n, tn), true)} ${getGetterName(n)}() const
                  |{
                  |  return this->$n;
                  |}"""
            ),
          )
        ),
      )
    })
  }

  private def getSetterFunctionMembers: List[CppDoc.Class.Member] =
    CppDoc.Class.Member.Function(
      CppDoc.Function(
        Some("Set all members"),
        "set",
        memberList.map(writeMemberAsParam),
        CppDoc.Type("void"),
        List(
          nonArrayMemberNames.map(n => line(s"this->$n = $n;")),
          if arrayMemberNames.isEmpty then Nil
          else Line.blank :: writeArraySetters(n => s"$n[i]"),
        ).flatten
      )
    ) ::
      memberList.map((n, tn) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Set member $n"),
            s"set$n",
            List(
              writeMemberAsParam((n, tn))
            ),
            CppDoc.Type("void"),
            if sizes.contains(n) then
              iterateN(sizes(n), lines(s"this->$n[i] = $n[i];"))
            else
              lines(s"this->$n = $n;")
          )
        )
      )

  private def getMemberVariableMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(CppDocHppWriter.writeAccessTag("protected"))
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Member variables") ++
            addBlankPrefix(memberList.map((n, tn) =>
              if sizes.contains(n) then line(s"$tn $n[${sizes(n)}];")
              else line(s"$tn $n;")
            ))
        )
      )
    )

  private def writeMemberAsParam(member: (String, String)) = member match {
    case (n, _) =>
      if sizes.contains(n) then
        CppDoc.Function.Param(
          CppDoc.Type(s"const ${getArrayTypeName(n)}&"),
          s"$n"
        )
      else writeMemberAsParamScalar(member)
  }

  // Writes members as function parameters using scalars for arrays
  private def writeMemberAsParamScalar(member: (String, String)) = member match {
    case (n, tn) => CppDoc.Function.Param(
      CppDoc.Type(
        members(n) match {
          case _: Type.Enum => s"$tn::T"
          case t if s.isPrimitive(t, tn) => s"$tn"
          case _ => s"const $tn&"
        }
      ),
      s"$n"
    )
  }

  private def writeMemberAsReturnType(
    member: (String, String),
    isConst: Boolean = false
  ) = member match {
    case (n, tn) =>
      val constStr = if isConst then "const " else ""
      (sizes.contains(n), members(n)) match {
        case (false, _: Type.Enum) => s"$tn::T"
        case (false, t) if s.isPrimitive(t, tn) => s"$tn"
        case (false, _) => s"$constStr$tn&"
        case _ => s"$constStr${getArrayTypeName(n)}&"
      }
  }

  private def writeInitializerList(getValue: String => String) =
    "Serializable()" :: nonArrayMemberNames.map(n => s"$n(${getValue(n)})")

  // Writes a for loop to set the value of each array member
  private def writeArraySetters(getValue: String => String) =
    arrayMemberNames.flatMap(n =>
      iterateN(sizes(n), lines(s"this->$n[i] = ${getValue(n)};"))
    )

  // Writes a for loop that iterates n times
  private def iterateN(n: Int, ll: List[Line]) =
    wrapInForLoop(
      "NATIVE_UINT_TYPE i = 0",
      s"i < $n",
      "i++",
      ll
    )

}
