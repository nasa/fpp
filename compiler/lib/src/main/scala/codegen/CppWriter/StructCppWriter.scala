package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Writes out C++ for struct definitions */
case class StructCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Struct(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getStruct(name)

  private val structType@Type.Struct(_, _, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s, "Fw::ExternalString")

  private val astMembers = data.members

  private val typeMembers = structType.anonStruct.members

  private val sizes = structType.sizes

  private val formats = structType.formats

  // List of tuples (<memberName>, <memberTypeName>)
  // Preserves ordering of struct members
  private val memberList = astMembers.map((_, node, _) => {
    val n = node.data.name
    val t = typeMembers(n)

    (n, typeCppWriter.write(t))
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

  private def getMemberTypeName(n: String) = s"Type_of_$n"

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
    val cls = classMember(
      AnnotationCppWriter.asStringOpt(aNode),
      name,
      Some("public Fw::Serializable"),
      // If struct is empty, write an empty class
      if memberList.isEmpty then Nil
      else getClassMembers
    )
    List(
      // If struct is empty, write an empty .cpp file
      if memberList.isEmpty then List(hppIncludes)
      else List(hppIncludes, cppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def writeIncludeDirectives = {
    val Right(a) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  private def getHppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/FPrimeBasicTypes.hpp",
      "Fw/Types/ExternalString.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/String.hpp"
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = userHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp",
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(Line.blank :: userHeaders, CppDoc.Lines.Cpp)
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List(
      getTypeMembers,
      getConstantMembers,
      getConstructorMembers,
      getOperatorMembers,
      getFunctionMembers,
      getVariableMembers,
    ).flatten

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public") ++
          CppDocWriter.writeBannerComment("Constants") ++
          addBlankPrefix(
            wrapInEnum(
              List(
                lines("//! The size of the serial representation"),
                lines("SERIALIZED_SIZE ="),
                lines(memberList.map((n, tn) =>
                  writeStaticSerializedSizeExpr(s, typeMembers(n), tn) + (
                    if sizes.contains(n) then s" * ${sizes(n)}"
                    else ""
                    )).mkString(" +\n")).map(indentIn),
              ).flatten
            )
          )
      )
    )

  /** Provide type aliases for array member types, to work
   *  around difficult C++ array syntax. */
  private def getTypeMembers: List[CppDoc.Class.Member] = {
    val typeAliases = memberList.flatMap((n, tn) => {
      val mtn = getMemberTypeName(n)
      sizes.get(n) match {
        case Some(size) =>
          Line.blank ::
          line(s"//! The type of $n") ::
          lines(s"using $mtn = $tn[$size];")
        case None => Nil
      }
    })
    val members = if typeAliases.isEmpty
      then Nil
      else List(linesClassMember(typeAliases))
    addAccessTagAndComment("public", "Types", members, CppDoc.Lines.Hpp)
  }

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    val defaultValues = getDefaultValues
    // Write this constructor only if the struct has an array member
    // In this case, the constructor provides scalar initialization
    // of the array members.
    val scalarConstructor =
      if sizes.isEmpty then None
      else Some(
        constructorClassMember(
          Some("Member constructor (scalar values for arrays)"),
          memberList.map(writeMemberAsParamScalar),
          writeInitializerList(n => n),
          writeArraySetters(n => n)
        )
      )

    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Constructors"),
        CppDoc.Lines.Both
      ),
      constructorClassMember(
        Some("Constructor (default value)"),
        Nil,
        "Serializable()" :: nonArrayMemberNames.map(n => {
          defaultValues(n) match {
            case v: Value.Struct => s"m_$n(${ValueCppWriter.writeStructMembers(s, v)})"
            case _: Value.AbsType => s"m_$n()"
            case v => writeInitializer(n, ValueCppWriter.write(s, v))
          }
        }),
        writeArraySetters(n => ValueCppWriter.write(s, defaultValues(n)))
      ),
      constructorClassMember(
        Some("Member constructor"),
        memberList.map(writeMemberAsParam),
        writeInitializerList(n => n),
        writeArraySetters(n => s"$n[i]")
      ),
      constructorClassMember(
        Some("Copy constructor"),
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $name&"),
            "obj",
            Some("The source object")
          )
        ),
        writeInitializerList(n => s"obj.m_$n"),
        writeArraySetters(n => s"obj.m_$n[i]")
      ),
    ) ++
      scalarConstructor
  }

  private def getOperatorMembers: List[CppDoc.Class.Member] = {
    val nonArrayMemberCheck = lines(
      nonArrayMemberNames.map(n => s"(this->m_$n == obj.m_$n)"
      ).mkString(" &&\n"))
    val addressEqualityCheck = lines("if (this == &obj) { return true; }")
    lazy val emptySizes =
      if nonArrayMemberNames.length == 1 then
        lines(s"return ${nonArrayMemberCheck.head};")
      else List.concat(
        addressEqualityCheck,
        wrapInScope(
          "return (",
          nonArrayMemberCheck,
          ");"
        )
      )
    lazy val nonEmptySizes = List.concat(
      addBlankPostfix(addressEqualityCheck),
      if nonArrayMemberNames.length > 0
      then List.concat(
        lines("// Compare non-array members"),
        if nonArrayMemberNames.length == 1 then
          wrapInIf(
            s"!${nonArrayMemberCheck.head}",
            lines("return false;")
          )
        else List.concat(
          lines("if (!("),
          nonArrayMemberCheck.map(indentIn),
          lines(
            """|)) {
               |  return false;
               |}"""
          )
        )
      )
      else lines(s""),
      Line.blank :: lines("// Compare array members"),
      arrayMemberNames.flatMap(n =>
        iterateN(
          sizes(n),
          wrapInIf(
            s"!(this->m_$n[i] == obj.m_$n[i])",
            lines("return false;")
          )
        )
      ),
      Line.blank :: lines("return true;"),
    )
    val equalityOpBody = 
      // Simplify syntax if there are no array members
      if sizes.isEmpty then emptySizes else nonEmptySizes

    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Operators"),
        CppDoc.Lines.Both
      ),
      functionClassMember(
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
            s"set(${memberNames.map(n => s"obj.m_$n").mkString(", ")});"
          ),
          lines("return *this;"),
        ).flatten
      ),
      functionClassMember(
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
      functionClassMember(
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
    ) ++ (
      linesClassMember(
        List(Line.blank),
        CppDoc.Lines.Both
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

  private def getFunctionMembers: List[CppDoc.Class.Member] = {
    // Members on which to call toString()
    val needsTmpString =
      astMembers.map((_, node, _) => {
        val t = typeMembers(node.data.name)
        (node, node.data.name, typeCppWriter.write(t))
      }).find((n, _, tn) => (sizes.contains(n.data.name), typeMembers(n.data.name).getUnderlyingType) match {
        // Only plain string types don't need a tmp string form appending to the struct
        case (false, _: Type.String) => false
        case _ => true
      }).isDefined

    val getMemberToString = (node: AstNode[Ast.StructTypeMember], n: String, tn: String) => {
      (sizes.contains(node.data.name), typeMembers(node.data.name).getUnderlyingType) match {
        case (false, _: Type.String) =>
          // Type is already a string, no need to perform any conversion
          lines(s"sb += this->m_$n;")
        case (false, t) if s.isPrimitive(t, tn) =>
          // Format the primitive into a temporary string buffer
          val formatStr = FormatCppWriter.write(
            s,
            getFormatStr(name),
            node.data.typeName
          )
          lines(s"""|tmp.format("$formatStr", ${promoteF32ToF64 (t) (s"this->m_$n")});
                    |sb += tmp;""")
        case (true, _) =>
          // An array member
          // Iterate through each member and format it into 'tmp'
          // Append 'tmp' to the final string
          val formatStr = FormatCppWriter.write(
            s,
            getFormatStr(name),
            node.data.typeName
          )

          val fillTmpString = (typeMembers(n).getUnderlyingType) match {
            case (_: Type.String) =>
              s"tmp = this->m_$n[i];"
            case t if s.isPrimitive(t, tn) =>
              s"""tmp.format("$formatStr", ${promoteF32ToF64 (t) (s"this->m_$n[i]")});"""
            case _ =>
              s"this->m_$n[i].toString(tmp);"
          }

          List.concat(
            lines("sb += \"[ \";"),
            iterateN(sizes(n), lines(
              s"""|${fillTmpString}
                  |if (i > 0) {
                  |  sb += ", ";
                  |}
                  |sb += tmp;"""
            )),
            lines("sb += \" ]\";"),
          )
        case (false, _) =>
          // A complex (non-array) type
          lines(s"""|this->m_$n.toString(tmp);
                    |sb += tmp;""")
      }
    }

    // toString() append formatted member to string
    val memberToString =
      astMembers.map((_, node, _) => {
        val t = typeMembers(node.data.name)
        (node, node.data.name, typeCppWriter.write(t))
      }).map((node, n, tn) => List.concat(
          lines(s"""|
                    |// Format $n
                    |sb += "$n = ";"""),
          getMemberToString(node, n, tn),
      ))

    def sizeIterator(size: Int, ll: List[Line]): List[Line] =
      wrapInForLoop(
        "U32 index = 0",
        s"index < $size",
        "index++",
        ll,
      )

    def getSerializedSize(n: String, tn: String) = 
      typeMembers(n).getUnderlyingType match {
        case ts: (Type.Array | Type.Struct | Type.String) => {
          if sizes.contains(n) then
            sizeIterator(
              sizes(n),
              lines(s"size += this->m_$n[index].serializedSize();")
            ).mkString("\n")
          else s"size += this->m_$n.serializedSize();"
        }
        case ts => s"size += ${writeStaticSerializedSizeExpr(s, ts, tn) + (
            if sizes.contains(n) then s" * ${sizes(n)};"
            else ";"
        )}"
      }

    val serializedSize = 
      List.concat(
        lines("FwSizeType size = 0;"),
        lines(memberList.map((n, tn) => 
          s"${getSerializedSize(n, tn)}"
        ).mkString("\n")),
        lines("return size;")
      )

    def writeSerializeStatusCheck =
      wrapInIf(
        "status != Fw::FW_SERIALIZE_OK",
        lines("return status;")
      )
    def writeSerializeCall(n: String) =
      line(s"status = buffer.serializeFrom(this->m_$n);") :: writeSerializeStatusCheck
    def writeDeserializeCall(n: String) =
      line(s"status = buffer.deserializeTo(this->m_$n);") :: writeSerializeStatusCheck

    List(
      List(
        linesClassMember(
          CppDocHppWriter.writeAccessTag("public")
        ),
        linesClassMember(
          CppDocWriter.writeBannerComment("Member functions"),
          CppDoc.Lines.Both
        ),
        functionClassMember(
          Some("Serialization"),
          "serializeTo",
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
        functionClassMember(
          Some("Deserialization"),
          "deserializeFrom",
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
        ),
        functionClassMember(
          Some("Get the dynamic serialized size of the struct"),
          "serializedSize",
          List(),
          CppDoc.Type("FwSizeType"),
          serializedSize,
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      ),
      wrapClassMembersInIfDirective(
        "\n#if FW_SERIALIZABLE_TO_STRING",
        List(
          functionClassMember(
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
            List.concat(
              if needsTmpString then lines("Fw::String tmp;") else List(),
              lines("sb = \"( \";"),
              memberToString.zipWithIndex.flatMap((memberToStringLines, idx) => {
                if idx > 0 then List.concat(
                  lines("sb += \", \";"),
                  memberToStringLines,
                ) else memberToStringLines
              }),
              lines("sb += \" )\";"),
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        )
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Getter functions"),
      ) :: getGetterFunctionMembers,
      linesClassMember(
        CppDocWriter.writeBannerComment("Setter functions"),
        CppDoc.Lines.Both
      ) :: getSetterFunctionMembers,
    ).flatten
  }

  private def getGetterFunctionMembers: List[CppDoc.Class.Member] = {
    def getGetterName(n: String) = s"get_$n"

    memberList.flatMap((n, tn) => (sizes.contains(n), typeMembers(n).getUnderlyingType) match {
      case (false, _: Type.Enum) => List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines(
              s"""|
                  |//! Get member $n
                  |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}() const
                  |{
                  |  return this->m_$n.e;
                  |}"""
            ),
          )
        )
      )
      case (false, t) if s.isPrimitive(t, tn) => List(
        linesClassMember(
          lines(
            s"""|
                |//! Get member $n
                |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}() const
                |{
                |  return this->m_$n;
                |}"""
          ),
        )
      )
      case _ => List(
        linesClassMember(
          lines(
            s"""|
                |//! Get member $n
                |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}()
                |{
                |  return this->m_$n;
                |}
                |
                |//! Get member $n (const)
                |${writeMemberAsReturnType((n, tn), StructCppWriter.Const)} ${getGetterName(n)}() const
                |{
                |  return this->m_$n;
                |}"""
          ),
        )
      )
    })
  }

  private def getSetterFunctionMembers: List[CppDoc.Class.Member] =
    functionClassMember(
      Some("Set all members"),
      "set",
      memberList.map(writeMemberAsParam),
      CppDoc.Type("void"),
      List(
        nonArrayMemberNames.map(n => line(s"this->m_$n = $n;")),
        if arrayMemberNames.isEmpty then Nil
        else Line.blank :: writeArraySetters(n => s"$n[i]"),
      ).flatten
    ) ::
      memberList.map((n, tn) =>
        functionClassMember(
          Some(s"Set member $n"),
          s"set_$n",
          List(
            writeMemberAsParam((n, tn))
          ),
          CppDoc.Type("void"),
          if sizes.contains(n) then
            iterateN(sizes(n), lines(s"this->m_$n[i] = $n[i];"))
          else
            lines(s"this->m_$n = $n;")
        )
      )

  private def getVariableMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("protected")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Member variables") ++
          addBlankPrefix(memberList.flatMap((n, tn) => lines(
            writeMemberDecl(s, tn, n, typeMembers(n), "m_", sizes.get(n).map(_.toString))
          )))
      )
    )

  private def writeMemberAsParam(member: (String, String)) = member match {
    case (n, _) =>
      if sizes.contains(n) then
        CppDoc.Function.Param(
          CppDoc.Type(s"const ${getMemberTypeName(n)}&"),
          s"$n"
        )
      else writeMemberAsParamScalar(member)
  }

  // Writes members as function parameters using scalars for arrays
  private def writeMemberAsParamScalar(member: (String, String)) = member match {
    case (n, tn) => CppDoc.Function.Param(
      CppDoc.Type(
        typeMembers(n).getUnderlyingType match {
          case _: Type.Enum => s"$tn::T"
          case _: Type.String => "const Fw::StringBase&"
          case t => if s.isPrimitive(t, tn) then tn else s"const $tn&"
        }
      ),
      n
    )
  }

  private def writeMemberAsReturnType(
    member: (String, String),
    returnMode: StructCppWriter.ReturnMode = StructCppWriter.NonConst
  ) = member match {
    case (n, tn) =>
      val maybeConstStr = returnMode match {
        case StructCppWriter.Const => "const "
        case StructCppWriter.NonConst => ""
      }
      (sizes.contains(n), typeMembers(n).getUnderlyingType) match {
        case (true, _) => s"$maybeConstStr${getMemberTypeName(n)}&"
        case (_, _: Type.Enum) => s"$tn::T"
        case (_, _: Type.String) => s"${maybeConstStr}Fw::ExternalString&"
        case (_, t) =>
          if s.isPrimitive(t, tn) then tn
          else s"$maybeConstStr$tn&"
      }
  }

  private def writeInitializer(name: String, value: String) = {
    val bufferName = getBufferName(name)
    typeMembers(name).getUnderlyingType match {
      case _: Type.String => s"m_$name(m_$bufferName, sizeof m_$bufferName, $value)"
      case _ => s"m_$name($value)"
    }
  }

  private def writeInitializerList(getValue: String => String) =
    "Serializable()" ::
    nonArrayMemberNames.map(name => writeInitializer(name, getValue(name)))

  // Writes a for loop to set the value of each array member
  private def writeArraySetters(getValue: String => String) =
    arrayMemberNames.flatMap(n =>
      iterateN(
        sizes(n),
        List.concat(
          {
            typeMembers(n).getUnderlyingType match {
              case _: Type.String =>
                val bufferName = getBufferName(n)
                lines(s"""|// Initialize the external string
                          |this->m_$n[i].setBuffer(&m_$bufferName[i][0], sizeof m_$bufferName[i]);
                          |// Set the array value""".stripMargin)
              case _ => Nil
            }
          },
          lines(s"this->m_$n[i] = ${getValue(n)};")
        )
      )
    )

  // Writes a for loop that iterates n times
  private def iterateN(n: Int, ll: List[Line]) =
    wrapInForLoop(
      "FwSizeType i = 0",
      s"i < $n",
      "i++",
      ll
    )

}

case object StructCppWriter {
  sealed trait ReturnMode
  case object Const extends ReturnMode
  case object NonConst extends ReturnMode
}

