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

  private val structName = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getStruct(structName)

  private val structType@Type.Struct(_, _, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s, "Fw::ExternalString")

  private val astMembers = data.members

  private val typeMembers = structType.anonStruct.members

  private val sizes = structType.sizes

  private val formats = structType.formats

  private val defaultValue = structType.getDefaultValue match {
    case Some(s) => Some(s.anonStruct)
    case None => structType.anonStruct.getDefaultValue
  }

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

  private val defaultMemberNames =
    structType.anonStruct.members.filter((name, ty) => {
      ty.getUnderlyingType match {
        case _: Type.String => false
        case _ => {
          val memberDefault = defaultValueMembers(name);
          ty.getDefaultValue match {
            case Some(typeDefault) =>
              typeDefault == memberDefault
            case None => false
          }
        }
      }
    }).map((n, _) => n).toList

  private val nonInitializerListArrayMemberNames = memberNames.filter((name) =>
      (sizes.contains(name) && !defaultMemberNames.contains(name)))

  private val initializerListMemberNames = memberNames.filter((name) =>
      defaultMemberNames.contains(name) || !sizes.contains(name))

  // Writes a for loop that iterates n times
  private def iterateN(n: Int, ll: List[Line]) =
    wrapInForLoop(
      "FwSizeType i = 0",
      s"i < $n",
      "i++",
      ll
    )

  // Writes a for loop to set the value of each array member
  private def writeArraySetters(getValue: String => String) =
    arrayMemberNames.flatMap(n => this.writeArrayMemberSetter(n, getValue(n)))

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$structName struct",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def defaultValueMembers = defaultValue.get.members

  private def getAllSetterFunctionMember =
    functionClassMember(
      Some("Set all members"),
      "set",
      memberList.map(writeMemberAsParam),
      CppDoc.Type("void"),
      List.concat(
        nonArrayMemberNames.map(n => line(s"this->m_$n = $n;")),
        if arrayMemberNames.isEmpty then Nil
        else Line.blank :: writeArraySetters(n => s"$n[i]"),
      )
    )

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
    addAccessTagAndComment(
      "public",
      "Constants",
      List(getSerializedSizeEnum),
      CppDoc.Lines.Hpp
    )

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "public",
      "Constructors",
      List.concat(
        List(getDefaultValueConstructor),
        // Write the member constructor if and only if there are members
        guardedList (!memberList.isEmpty) (List(getMemberConstructor)),
        List(getCopyConstructor),
        // Write the scalar array constructor if and only if there are
        // array members
        guardedList (!sizes.isEmpty) (List(getScalarArrayConstructor))
      )
    )
  }

  private def getCopyAssignmentOperator =
    functionClassMember(
      Some("Copy assignment operator"),
      "operator=",
      List(
        CppDoc.Function.Param(
          CppDoc.Type(s"const $structName&"),
          "obj",
          Some("The source object")
        ),
      ),
      CppDoc.Type(s"$structName&"),
      if memberList.isEmpty
      then lines (
        """|(void) obj;
           |return *this;"""
      )
      else List.concat(
        wrapInIf("this == &obj", lines("return *this;")),
        Line.blank :: lines(
          s"set(${memberNames.map(n => s"obj.m_$n").mkString(", ")});"
        ),
        lines("return *this;"),
      )
    )

  private def getCopyConstructor = constructorClassMember(
    Some("Copy constructor"),
    List(
      CppDoc.Function.Param(
        CppDoc.Type(s"const $structName&"),
        "obj",
        Some("The source object")
      )
    ),
    writeInitializerList(n => s"obj.m_$n"),
    List.concat(
      guardedList (memberList.isEmpty) (lines("(void) obj;")),
      writeArraySetters(n => s"obj.m_$n[i]")
    )
  )

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      s.getIncludePath(symbol, fileName)
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(Line.blank :: userHeaders, CppDoc.Lines.Cpp)
  }

  private def getDefaultValueConstructor =
    constructorClassMember(
      Some("Constructor (default value)"),
      Nil,
      "Serializable()" :: initializerListMemberNames.map(n => {
        if defaultMemberNames.contains(n) then s"m_$n()"
        else defaultValueMembers(n) match {
          case v: Value.Struct => s"m_$n(${ValueCppWriter.writeStructMembers(s, v)})"
          case _: Value.AbsType => s"m_$n()"
          case v => writeInitializer(n, ValueCppWriter.write(s, v))
        }
      }),
      nonInitializerListArrayMemberNames.flatMap(n => writeArrayMemberSetter(
        n, ValueCppWriter.write(s, defaultValueMembers(n)
      )))
    )

  private def getDeserializeFromFunctionMember: CppDoc.Class.Member.Function =
    functionClassMember(
      Some("Deserialization"),
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
      writeSerializeFunctionBody(
        List.concat(
          lines("Fw::SerializeStatus status;"),
          Line.blank :: memberNames.flatMap(n =>
            if sizes.contains(n) then
              iterateN(sizes(n), writeDeserializeCall(s"$n[i]"))
            else
              writeDeserializeCall(n)
          ),
          Line.blank :: lines("return status;"),
        )
      )
    )

  private def getFormatStr(n: String) =
    if formats.contains(n) then formats(n)
    else Format("", List((Format.Field.Default, "")))

  private def getFunctionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Member functions",
      List.concat(
        getSerialFunctionMembers,
        wrapClassMembersInIfDirective(
          "\n#if FW_SERIALIZABLE_TO_STRING",
          List(ToStringFunctionMember.get)
        ),
        getGetterFunctionMembers,
        getSetterFunctionMembers
      )
    )

  private def getGetterFunctionMember(n: String, tn: String): CppDoc.Class.Member =
    (sizes.contains(n), typeMembers(n).getUnderlyingType) match {
      case (false, _: Type.Enum) =>
        writeEnumGetter(n, tn)
      case (false, t) if s.isPrimitive(t, tn) =>
        writePrimitiveGetter(n, tn)
      case _ =>
        writeNonPrimitiveGetters(n, tn)
    }

  private def getGetterFunctionMembers: List[CppDoc.Class.Member] =
    guardedList (!memberList.isEmpty) (
      linesClassMember(
        CppDocWriter.writeBannerComment("Getter functions")
      ) :: memberList.map(getGetterFunctionMember)
    )

  private def getGetterName(n: String) = s"get_$n"

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

  private def getInequalityOperator =
    functionClassMember(
      Some("Inequality operator"),
      "operator!=",
      List(
        CppDoc.Function.Param(
          CppDoc.Type(s"const $structName&"),
          "obj",
          Some("The other object")
        )
      ),
      CppDoc.Type("bool"),
      lines("return !(*this == obj);"),
      CppDoc.Function.NonSV,
      CppDoc.Function.Const
    )

  private def getMemberConstructor =
    constructorClassMember(
      Some("Member constructor"),
      memberList.map(writeMemberAsParam),
      writeInitializerList(n => n),
      writeArraySetters(n => s"$n[i]")
    )

  private def getMemberTypeName(n: String) = s"Type_of_$n"

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = classMember(
      AnnotationCppWriter.asStringOpt(aNode),
      structName,
      Some("public Fw::Serializable"),
      getClassMembers
    )
    hppIncludes ::
    cppIncludes ::
    wrapInNamespaces(namespaceIdentList, List(cls))
  }

  private def getOperatorMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Operators",
      getCopyAssignmentOperator ::
      EqualityOperator.get ::
      getInequalityOperator ::
      getOstreamOperator
    )

  private def getOstreamOperator =
    linesClassMember(List(Line.blank), CppDoc.Lines.Both) ::
    writeOstreamOperator(
      structName,
      lines(
        """|Fw::String s;
           |obj.toString(s);
           |os << s.toChar();
           |return os;"""
      )
    )

  private def getScalarArrayConstructor =
    constructorClassMember(
      Some("Member constructor (scalar values for arrays)"),
      memberList.map(writeMemberAsParamScalar),
      writeInitializerList(n => n),
      writeArraySetters(n => n)
    )

  private def getSerialFunctionMembers =
    List(
      getSerializeToFunctionMember,
      getDeserializeFromFunctionMember,
      SerializedSizeFunctionMember.get
    )

  private def getSerializeToFunctionMember: CppDoc.Class.Member.Function =
    functionClassMember(
      Some("Serialization"),
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
      writeSerializeFunctionBody(
        List.concat(
          lines("Fw::SerializeStatus status;"),
          Line.blank :: memberNames.flatMap(n =>
            if sizes.contains(n) then
              iterateN(sizes(n), writeSerializeCall(s"$n[i]"))
            else
              writeSerializeCall(n)
          ),
          Line.blank :: lines("return status;"),
        )
      ),
      CppDoc.Function.NonSV,
      CppDoc.Function.Const
    )

  private def getSerializedSizeEnum =
    linesClassMember(
      addBlankPrefix(
        wrapInEnum(
          List.concat(
            lines("//! The size of the serial representation"),
            lines("SERIALIZED_SIZE ="),
            lines(getSerializedSizeExpr).map(indentIn)
          )
        )
      )
    )

  private def getSerializedSizeExpr =
    if memberList.size == 0 then "0"
    else memberList.map((n, tn) =>
      writeStaticSerializedSizeExpr(s, typeMembers(n), tn) + (
        if sizes.contains(n) then s" * ${sizes(n)}"
        else ""
    )).mkString(" +\n")

  private def getSetterFunctionMembers: List[CppDoc.Class.Member] =
    guardedList (!memberList.isEmpty) (
      linesClassMember(
        CppDocWriter.writeBannerComment("Setter functions"),
        CppDoc.Lines.Both
      ) ::
      getAllSetterFunctionMember ::
      getSingleSetterFunctionMembers
    )

  private def getSingleSetterFunctionMember(n: String, tn: String): CppDoc.Class.Member.Function =
    functionClassMember(
      Some(s"Set member $n"),
      s"set_$n",
      List(writeMemberAsParam((n, tn))),
      CppDoc.Type("void"),
      if sizes.contains(n) then
        iterateN(sizes(n), lines(s"this->m_$n[i] = $n[i];"))
      else
        lines(s"this->m_$n = $n;")
    )

  private def getSingleSetterFunctionMembers =
    memberList.map(getSingleSetterFunctionMember)

  private def getTypeMembers: List[CppDoc.Class.Member] = {
  /** Provide type aliases for array member types, to work
   *  around difficult C++ array syntax. */
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

  private def getVariableMember(n: String, tn: String): List[Line] =
    lines(
      writeMemberDecl(
        s,
        tn,
        n,
        typeMembers(n),
        "m_",
        sizes.get(n).map(_.toString)
      )
    )

  private def getVariableMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "protected",
      "Member variables",
      guardedList (memberList.size > 0) (
        List(
          linesClassMember(
            addBlankPrefix(
              memberList.flatMap(getVariableMember)
            )
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def writeArrayMemberSetter(memberName: String, memberValue: String) = {
    iterateN(
        sizes(memberName),
        List.concat(
          {
            typeMembers(memberName).getUnderlyingType match {
              case _: Type.String =>
                val bufferName = getBufferName(memberName)
                lines(
                  s"""|// Initialize the external string
                      |this->m_$memberName[i].setBuffer(&m_$bufferName[i][0], sizeof m_$bufferName[i]);
                      |// Set the array value"""
                )
              case _ => Nil
            }
          },
          lines(s"this->m_$memberName[i] = $memberValue;")
        )
      )
  }

  private def writeDeserializeCall(n: String) =
    line(s"status = buffer.deserializeTo(this->m_$n, mode);") ::
    writeSerializeStatusCheck

  private def writeEnumGetter(n: String, tn: String) =
    linesClassMember(
      lines(
        s"""|
            |//! Get member $n
            |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}() const
            |{
            |  return this->m_$n.e;
            |}"""
      )
    )

  private def writeIncludeDirectives = {
    val Right(a) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
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

  private def writeMemberAsParam(member: (String, String)) = member match {
    case (n, _) =>
      if sizes.contains(n) then
        CppDoc.Function.Param(
          CppDoc.Type(s"const ${getMemberTypeName(n)}&"),
          s"$n"
        )
      else writeMemberAsParamScalar(member)
  }

  private def writeMemberAsParamScalar(member: (String, String)) = member match {
    // Writes members as function parameters using scalars for arrays
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

  private def writeNonPrimitiveGetters(n: String, tn: String) =
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
      )
    )

  private def writePrimitiveGetter(n: String, tn: String) =
    linesClassMember(
      lines(
        s"""|
            |//! Get member $n
            |${writeMemberAsReturnType((n, tn))} ${getGetterName(n)}() const
            |{
            |  return this->m_$n;
            |}"""
      )
    )

  private def writeSerializeCall(n: String) =
    line(s"status = buffer.serializeFrom(this->m_$n, mode);") :: writeSerializeStatusCheck

  private def writeSerializeFunctionBody(body: List[Line]) =
    if memberList.isEmpty
    then lines(
      """|(void) buffer;
         |(void) mode;
         |return Fw::FW_SERIALIZE_OK;"""
    )
    else body

  private def writeSerializeStatusCheck =
    wrapInIf(
      "status != Fw::FW_SERIALIZE_OK",
      lines("return status;")
    )

  /** Object for generating the equality operator */
  private object EqualityOperator {

    def get = {
      functionClassMember(
        Some("Equality operator"),
        "operator==",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $structName&"),
            "obj",
            Some("The other object")
          )
        ),
        CppDoc.Type("bool"),
        writeCode,
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    }

    private val nonArrayMemberCheck = lines(
      nonArrayMemberNames.map(n => s"(this->m_$n == obj.m_$n)"
    ).mkString(" &&\n"))

    private val addressEqualityCheck =
      lines("if (this == &obj) { return true; }")

    private def writeCodeForEmptySizes =
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

    private def writeCodeForNonEmptySizes = List.concat(
      addressEqualityCheck,
      guardedList (nonArrayMemberNames.length > 0) (
        Line.blank ::
        List.concat(
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
      ),
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

    def writeCode = 
      if astMembers.isEmpty then lines(
        """|(void) obj;
           |return true;"""
      )
      else if sizes.isEmpty then writeCodeForEmptySizes
      else writeCodeForNonEmptySizes

  }

  /** Object for generating the serialized size function member */
  private object SerializedSizeFunctionMember {

    private def sizeIterator(size: Int, ll: List[Line]): List[Line] =
      wrapInForLoop(
        "U32 index = 0",
        s"index < $size",
        "index++",
        ll,
      )

    private def writeCodeForMember(n: String, tn: String) = 
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
          if sizes.contains(n) then s" * ${sizes(n)};" else ";"
        )}"
      }

    private val writeCode = 
      if memberList.isEmpty
      then lines("return 0;")
      else List.concat(
        lines("FwSizeType size = 0;"),
        lines(memberList.map(writeCodeForMember).mkString("\n")),
        lines("return size;")
      )

    def get: CppDoc.Class.Member.Function =
      functionClassMember(
        Some("Get the dynamic serialized size of the struct"),
        "serializedSize",
        List(),
        CppDoc.Type("FwSizeType"),
        writeCode,
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )

  }


  /** Object for generating the toString function member */
  private object ToStringFunctionMember {

    // Get the toString function class member
    def get: CppDoc.Class.Member.Function =
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
        if memberList.isEmpty
        then lines("sb = \"()\";")
        else List.concat(
          guardedList (needsTmpString) (lines("Fw::String tmp;")),
          lines("sb = \"( \";"),
          writeCodeForMembers,
          lines("sb += \" )\";"),
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )

    // Does toString need a temporary string?
    private val needsTmpString = {
      def predicate(n: String) = {
        val t = typeMembers(n).getUnderlyingType
        val isArray = sizes.contains(n)
        val isString = t match {
          case _: Type.String => true
          case _ => false
        }
        // Both non-strings and arrays need a tmp string
        (!isString) || isArray
      }
      memberNames.exists(predicate)
    }

    // Write toString code for one struct member
    private def writeCodeForMember(member: Ast.StructTypeMember, idx: Int) = {
      val n = member.name
      List.concat(
        guardedList (idx > 0) (lines("sb += \", \";")),
        lines(s"""|
                  |// Format $n
                  |sb += "$n = ";"""),
        if sizes.contains(n)
        then writeCodeForArrayMember(member)
        else writeCodeForNonArrayMember(member)
      )
    }

    // write toString code for all struct members
    private val writeCodeForMembers =
      astMembers.zipWithIndex.flatMap {
        case ((_, node, _), idx) => writeCodeForMember(node.data, idx)
      }

    // Write toString code for an array member
    private def writeCodeForArrayMember(member: Ast.StructTypeMember) = {
      val n = member.name
      val t = typeMembers(n).getUnderlyingType
      val tn = typeCppWriter.write(t)
      // Iterate through each member and format it into 'tmp'
      // Append 'tmp' to the final string
      val formatStr = FormatCppWriter.write(
        s,
        getFormatStr(structName),
        member.typeName
      )
      val fillTmpString = t match {
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
          s"""|$fillTmpString
              |if (i > 0) {
              |  sb += ", ";
              |}
              |sb += tmp;"""
        )),
        lines("sb += \" ]\";"),
      )
    }

    // Write toString code for a non-array member
    private def writeCodeForNonArrayMember(member: Ast.StructTypeMember) = {
      val n = member.name
      val memberType = typeMembers(n).getUnderlyingType
      val tn = typeCppWriter.write(memberType)
      memberType match {
        case _: Type.String =>
          // Type is already a string, no need to perform any conversion
          lines(s"sb += this->m_$n;")
        case t if s.isPrimitive(t, tn) =>
          // Format the primitive into a temporary string buffer
          val formatStr = FormatCppWriter.write(
            s,
            getFormatStr(structName),
            member.typeName
          )
          lines(s"""|tmp.format("$formatStr", ${promoteF32ToF64 (t) (s"this->m_$n")});
                    |sb += tmp;""")
        case _ =>
          // A non-primitive, non-string type
          lines(s"""|this->m_$n.toString(tmp);
                    |sb += tmp;""")
      }

    }

  }

}

case object StructCppWriter {
  sealed trait ReturnMode
  case object Const extends ReturnMode
  case object NonConst extends ReturnMode
}

