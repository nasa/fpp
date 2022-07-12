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

  private val structType @ Type.Struct(_, _, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val strCppWriter = StringCppWriter(s, Some(name))

  private val members = structType.anonStruct.members

  private val typeMembers = data.members

  private val sizes = structType.sizes

  private val formats = structType.formats

  // Map from member names to C++ type names
  private val membersNamedList = members.map((n, t) => t match {
    case strType: Type.String => n -> strCppWriter.getClassName(strType)
    case otherType => n -> typeCppWriter.write(otherType)
  }).toList

  private val memberNames = members.keys.toList

  private def writeIncludeDirectives(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ): List[Line] = {
    val Right(a) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  private def getDefaultValues = {
    val defaultValue = structType.getDefaultValue match {
      case Some(s) => Some(s.anonStruct)
      case None => structType.anonStruct.getDefaultValue
    }
    defaultValue.get.members.map((n, t) =>
      n -> ValueCppWriter.write(s, t)
    )
  }

  private def getFormatStr(n: String) =
    if formats.contains(n) then formats(n)
    else Format("", List((Format.Field.Default, "")))

  private def isPrimitiveOrString(member: (String, String)) = member match {
    case (n, tn) => (sizes.contains(n), members(n)) match {
        case (false, _: Type.String) => false
        case (false, t) if s.isPrimitive(t, tn) => false
        case _ => true
    }
  }

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
        getClassMembers
      )
    )
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/BasicTypes.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/String.hpp"
    ).map(CppWriter.headerString).map(line)
    val symbolHeaders = writeIncludeDirectives(s, aNode)
    val headers = userHeaders ++ symbolHeaders
    CppWriter.linesMember(
      addBlankPrefix(headers)
    )
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
                  lines(membersNamedList.map((n, tn) =>
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

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    val defaultValues = getDefaultValues
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
          "Serializable()" ::
            membersNamedList
              .filterNot((n, _) => sizes.contains(n))
              .map((n, tn) =>
                if isPrimitiveOrString((n, tn)) then s"$n(${defaultValues(n)})"
                else s"$n(${defaultValues(n).split("\\(")(1)}"
              ),
          memberNames.filter(sizes.contains).flatMap(n =>
            iterateN(sizes(n), lines(s"this->$n[i] = ${defaultValues(n)};"))
          )
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Member constructor"),
          membersNamedList.map(writeMemberAsParam),
          Nil,
          writeSetCall("")
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
          List("Serializable()"),
          writeSetCall("obj.")
        )
      ),
    )
  }

  private def getOperatorMembers: List[CppDoc.Class.Member] =
    List(
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
            Line.blank :: writeSetCall("obj."),
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
          wrapInScope(
            "return (",
            lines(memberNames.map(n =>
              s"(this->$n == obj.$n)"
            ).mkString(" &&\n")),
            ");"
          ),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        ),
      ),
    )

  private def getMemberFunctionMembers: List[CppDoc.Class.Member] = {
    val toStringMemberNames =
      membersNamedList.filterNot(isPrimitiveOrString).map((n, _) => n)
    val initStrings = toStringMemberNames match {
      case Nil => Nil
      case names =>
        List(
          Line.blank ::
            lines("// Declare strings to hold any serializable toString() arguments"),
          names.map(n => line(s"Fw::String str_$n;")),
          Line.blank ::
            lines("// Call toString for arrays and serializable types"),
          names.map(n => line(s"this->$n.toString(str_$n);")),
        ).flatten
    }

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
                line(s"status = buffer.serialize(this->$n);") ::
                  wrapInIf(
                    "status != Fw::FW_SERIALIZE_OK",
                    lines("return status;")
                  )
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
                line(s"status = buffer.deserialize(this->$n);") ::
                  wrapInIf(
                    "status != Fw::FW_SERIALIZE_OK",
                    lines("return status;")
                  )
              ),
              Line.blank :: lines("return status;"),
            ).flatten
          )
        ),
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines("\n#if FW_SERIALIZABLE_TO_STRING || BUILD_UT"),
            CppDoc.Lines.Both
          )
        ),
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
              lines(typeMembers.map(m => FormatCppWriter.write(
                getFormatStr(m._2.data.name),
                m._2.data.typeName
              )).mkString("\"(", " \"\n\"", ")\";")).map(indentIn),
              initStrings,
              Line.blank ::
                lines("char outputString[FW_ARRAY_TO_STRING_BUFFER_SIZE];"),
              wrapInScope(
                "(void) snprintf(",
                List(
                  List(
                    line("outputString,"),
                    line("FW_ARRAY_TO_STRING_BUFFER_SIZE,"),
                    line("formatString,"),
                  ),
                  lines(membersNamedList.map((n, tn) =>
                    (sizes.contains(n), members(n)) match {
                      case (false, _: Type.String) => s"this->$n.toChar()"
                      case (false, t) if s.isPrimitive(t, tn) => s"this->$n"
                      case _ => s"str_$n.toChar()"
                  }).mkString(",\n")),
                ).flatten,
                ");"
              ),
              List(
                Line.blank,
                line("outputString[FW_ARRAY_TO_STRING_BUFFER_SIZE-1] = 0; // NULL terminate"),
                line("sb = outputString;"),
              ),
            ).flatten,
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        ),
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            lines("\n#endif"),
            CppDoc.Lines.Both
          )
        ),
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Getter functions"),
          CppDoc.Lines.Both
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

  private def getGetterFunctionMembers: List[CppDoc.Class.Member] =
    membersNamedList.map((n, tn) =>
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some(s"Get member $n"),
          s"get$n",
          Nil,
          writeMemberAsReturnType((n, tn)),
          lines(s"return this->$n;"),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      )
    )

  private def getSetterFunctionMembers: List[CppDoc.Class.Member] =
    CppDoc.Class.Member.Function(
      CppDoc.Function(
        Some("Set all values"),
        "set",
        membersNamedList.map(writeMemberAsParam),
        CppDoc.Type("void"),
        memberNames.flatMap(n =>
          if sizes.contains(n) then
            iterateN(sizes(n), lines(s"this->$n[i] = $n[i];"))
          else
            lines(s"this->$n = $n;")
        )
      )
    ) ::
      membersNamedList.map((n, tn) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Set member $n"),
            s"set$n",
            List(
              writeMemberAsParam((n, tn))
            ),
            CppDoc.Type("void"),
            lines(s"this->$n = $n;")
          )
        )
      )

  private def getMemberVariableMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(CppDocHppWriter.writeAccessTag("private"))
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Member variables") ++
            addBlankPrefix(membersNamedList.map((n, tn) =>
              if sizes.contains(n) then line(s"$tn $n[${sizes(n)}];")
              else line(s"$tn $n;")
            ))
        )
      )
    )

  private def writeMemberAsParam(member: (String, String)) = member match {
    case (n, tn) =>
      if sizes.contains(n) then
        CppDoc.Function.Param(
          CppDoc.Type(s"const $tn"),
          s"(&$n)[${sizes(n)}]",
          None
        )
      else
        CppDoc.Function.Param(
          CppDoc.Type(s"const $tn&"),
          s"$n",
          None
        )
  }

  private def writeMemberAsReturnType(member: (String, String)) = member match {
    case (n, tn) =>
      if sizes.contains(n) then
        CppDoc.Type(s"const $tn*", Some(s"const $name::$tn*"))
      else
        CppDoc.Type(s"const $tn&", Some(s"const $name::$tn&"))
  }

  private def writeSetCall(prefix: String) =
    lines(
      s"set(${memberNames.map(n => s"$prefix$n").mkString(", ")});"
    )

  // Writes a for loop that iterates n times
  private def iterateN(n: Int, ll: List[Line]) =
    wrapInForLoop(
      "NATIVE_INT_TYPE i = 0",
      s"i < $n",
      "i++",
      ll
    )

}