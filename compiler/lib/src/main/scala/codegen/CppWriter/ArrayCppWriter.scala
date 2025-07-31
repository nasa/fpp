package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for array definitions */
case class ArrayCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefArray]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Array(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getArray(name)

  private val arrayType @ Type.Array(_, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s, "Fw::ExternalString")

  private val eltType = arrayType.anonArray.eltType

  private val eltTypeName = typeCppWriter.write(eltType)

  private val hasPrimitiveEltType = s.isPrimitive(eltType, eltTypeName)

  private val hasStringEltType= s.isStringType(eltType)

  private val constructorEltType = if hasStringEltType then "Fw::StringBase" else "ElementType"

  private val arraySize = arrayType.getArraySize.get

  private val formatStr = FormatCppWriter.write(
    s,
    arrayType.format.getOrElse(Format("", List((Format.Field.Default, "")))),
    data.eltType
  )

  private def writeIncludeDirectives(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ): List[String] = {
    val Right(a) = UsedSymbols.defArrayAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  private def getDefaultValues: List[String] = {
    val defaultValue = arrayType.getDefaultValue match {
      case Some(a) => Some(a.anonArray)
      case None => arrayType.anonArray.getDefaultValue
    }
    defaultValue.get.elements.map(ValueCppWriter.write(s, _))
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name array",
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
    val standardHeaders = List(
      "Fw/FPrimeBasicTypes.hpp",
      "Fw/Types/ExternalString.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/String.hpp"
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives(s, aNode)
    val headers = standardHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp"
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(Line.blank :: userHeaders, CppDoc.Lines.Cpp)
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List.concat(
      getTypeMembers,
      getConstantMembers,
      getConstructorMembers,
      getOperatorMembers,
      getPublicFunctionMembers,
      guardedList (hasStringEltType) (getPrivateFunctionMembers),
      getMemberVariableMembers,
    )

  private def getTypeMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("public"),
          CppDocWriter.writeBannerComment("Types"),
          lines(
            s"""|
                |//! The element type
                |using ElementType = $eltTypeName;"""
          ),
        ).flatten
      )
    )

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public") ++
        CppDocWriter.writeBannerComment("Constants") ++
        addBlankPrefix(
          wrapInEnum({
            val elementSizes = eltType.getUnderlyingType match {
              case ts: Type.String =>
                s"""|//! The string size of each element
                    |ELEMENT_STRING_SIZE = ${writeStringSize(s, ts)},
                    |//! The buffer size of each element
                    |ELEMENT_BUFFER_SIZE = Fw::StringBase::BUFFER_SIZE(ELEMENT_STRING_SIZE),
                    |//! The serialized size of each element
                    |ELEMENT_SERIALIZED_SIZE = Fw::StringBase::STATIC_SERIALIZED_SIZE(ELEMENT_STRING_SIZE),"""
              case _ =>
                s"""|//! The serialized size of each element
                    |ELEMENT_SERIALIZED_SIZE = ${writeSerializedSizeExpr(s, eltType, eltTypeName)},"""
            }
            lines(s"""|//! The size of the array
                      |SIZE = $arraySize,
                      |${elementSizes.stripMargin}
                      |//! The size of the serial representation
                      |SERIALIZED_SIZE = SIZE * ELEMENT_SERIALIZED_SIZE""")
          })
        )
      )
    )

  private val initElementsCall = guardedList (hasStringEltType) (lines("this->initElements();"))

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    val defaultValues = getDefaultValues
    // Only write this constructor if the array has more than one element
    val singleElementConstructor =
      if arraySize == 1 then Nil
      else List(
        constructorClassMember(
          Some("Constructor (single element)"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $constructorEltType&"),
              "e",
              Some("The element"),
            )
          ),
          List("Serializable()"),
          List.concat(
            initElementsCall,
            indexIterator(lines("this->elements[index] = e;"))
          )
        )
      )

    List.concat(
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
          List("Serializable()"),
          List.concat(
            initElementsCall,
            lines("// Construct using element-wise constructor"),
            wrapInScope(
              s"*this = $name(",
              lines(defaultValues.map(v => s"$v").mkString(",\n")),
              ");",
            ),
          )
        ),
        constructorClassMember(
          Some("Constructor (user-provided value)"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const ElementType"),
              "(&a)[SIZE]",
              Some("The array"),
            )
          ),
          List("Serializable()"),
          List.concat(
            initElementsCall,
            indexIterator(lines("this->elements[index] = a[index];"))
          )
        )
      ),
      singleElementConstructor,
      List(
        constructorClassMember(
          Some("Constructor (multiple elements)"),
          List.range(1, arraySize + 1).map(i => CppDoc.Function.Param(
            CppDoc.Type(s"const $constructorEltType&"),
            s"e$i",
            Some(s"Element $i"),
          )),
          List("Serializable()"),
          List.concat(
            initElementsCall,
            List.range(1, arraySize + 1).map(i => line(
              s"this->elements[${i - 1}] = e$i;"
            ))
          )
        ),
        constructorClassMember(
          Some("Copy Constructor"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "obj",
              Some("The source object"),
            )
          ),
          List("Serializable()"),
          List.concat(
            initElementsCall,
            indexIterator(lines("this->elements[index] = obj.elements[index];"))
          )
        )
      )
    )
  }

  private def getOperatorMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Operators"),
        CppDoc.Lines.Both,
      ),
      functionClassMember(
        Some("Subscript operator"),
        "operator[]",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const U32"),
            "i",
            Some("The subscript index"),
          ),
        ),
        CppDoc.Type("ElementType&", Some(s"$name::ElementType&")),
        List(
          line("FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));"),
          line("return this->elements[i];"),
        ),
      ),
      functionClassMember(
        Some("Const subscript operator"),
        "operator[]",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const U32"),
            "i",
            Some("The subscript index"),
          ),
        ),
        CppDoc.Type("const ElementType&", Some(s"const $name::ElementType&")),
        List(
          line("FW_ASSERT(i < SIZE, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(SIZE));"),
          line("return this->elements[i];"),
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const,
      ),
      functionClassMember(
        Some("Copy assignment operator (object)"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $name&"),
            "obj",
            Some("The source object"),
          ),
        ),
        CppDoc.Type(s"$name&"),
        List(
          wrapInIf("this == &obj", lines("return *this;")),
          Line.blank ::
          indexIterator(lines("this->elements[index] = obj.elements[index];")),
          lines("return *this;"),
        ).flatten,
      ),
      functionClassMember(
        Some("Copy assignment operator (raw array)"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const ElementType"),
            "(&a)[SIZE]",
            Some("The source array"),
          ),
        ),
        CppDoc.Type(s"$name&"),
        List(
          indexIterator(lines("this->elements[index] = a[index];")),
          lines("return *this;"),
        ).flatten
      ),
      functionClassMember(
        Some("Copy assignment operator (single element)"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const ElementType&"),
            "e",
            Some("The element"),
          ),
        ),
        CppDoc.Type(s"$name&"),
        List(
          indexIterator(lines("this->elements[index] = e;")),
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
            Some("The other object"),
          ),
        ),
        CppDoc.Type("bool"),
        List(
          indexIterator(wrapInIf(
            "!((*this)[index] == obj[index])",
            lines("return false;"),
          )),
          lines("return true;"),
        ).flatten,
        CppDoc.Function.NonSV,
        CppDoc.Function.Const,
      ),
      functionClassMember(
        Some("Inequality operator"),
        "operator!=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $name&"),
            "obj",
            Some("The other object"),
          ),
        ),
        CppDoc.Type("bool"),
        lines("return !(*this == obj);"),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const,
      )
    ) ++ (
      linesClassMember(
        List(Line.blank),
        CppDoc.Lines.Both
      ) :: writeOstreamOperator(
        name,
        lines(
          """|Fw::String s;
             |obj.toString(s);
             |os << s;
             |return os;"""
        )
      )
    )

  private def getPublicFunctionMembers: List[CppDoc.Class.Member] = {
    val fillTmpString =
        // Standard format string, just copy it in
        if hasStringEltType && formatStr == "%s" then s"tmp = this->elements[index];"
        // Non-standard format string, we need to copy the string in with .format()
        else if hasStringEltType then s"tmp.format(\"$formatStr\", this->elements[index].toChar());"
        // Primitive string format
        else if hasPrimitiveEltType then s"tmp.format(\"$formatStr\", ${promoteF32ToF64(eltType)("this->elements[index]")});"
        // Complex object type with default format string, convert the object to a string
        else if formatStr == "%s" then "this->elements[index].toString(tmp);"
        // Complex object type with non-default format string, convert the object to a string
        // Then re-format the string using the custom format string
        else s"""this->elements[index].toString(tmp);
                 |tmp.format(\"%s\", tmp.toChar());"""

    val formatLoop = indexIterator(lines(
      s"""|Fw::String tmp;
          |$fillTmpString
          |
          |FwSizeType size = tmp.length() + (index > 0 ? 2 : 0);
          |if ((size + sb.length()) <= sb.maxLength()) {
          |  if (index > 0) {
          |    sb += ", ";
          |  }
          |  sb += tmp;
          |} else {
          |  break;
          |}
          |"""
    ))

    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Public member functions"),
        CppDoc.Lines.Both
      ),
      functionClassMember(
        Some("Serialization"),
        "serializeTo",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerializeBufferBase&"),
            "buffer",
            Some("The serial buffer"),
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        List(
          lines("Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;"),
          indexIterator(
            line("status = buffer.serializeFrom((*this)[index]);") ::
              wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
          ),
          lines("return status;"),
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
            Some("The serial buffer"),
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        List(
          lines("Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;"),
          indexIterator(
            line("status = buffer.deserializeTo((*this)[index]);") ::
              wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
          ),
          lines("return status;"),
        ).flatten,
    )
    ) ++
      wrapClassMembersInIfDirective(
        "\n#if FW_SERIALIZABLE_TO_STRING",
        List(
          functionClassMember(
            Some("Convert array to string"),
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
              lines(
                s"""|// Clear the output string
                    |sb = "";
                    |
                    |// Array prefix
                    |if (sb.length() + 2 <= sb.maxLength()) {
                    |  sb += \"[ \";
                    |} else {
                    |  return;
                    |}"""),
              List(Line.blank),
              formatLoop,
              List(Line.blank),
              lines(
                s"""|// Array suffix
                    |if (sb.length() + 2 <= sb.maxLength()) {
                    |  sb += \" ]\";
                    |}"""),
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const,
          )
        )
      )
  }

  private def getPrivateFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("private")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Private member functions"),
        CppDoc.Lines.Both
      ),
      functionClassMember(
        Some("Initialize elements"),
        "initElements",
        Nil,
        CppDoc.Type("void"),
        indexIterator(
          lines("this->elements[index].setBuffer(&this->buffers[index][0], sizeof this->buffers[index]);")
        )
      )
    )
  }

  private def getMemberVariableMembers: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("private")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Member variables") ++
        List.concat(
          addBlankPrefix(
            eltType.getUnderlyingType match {
              case _: Type.String =>
                lines("""|//! The char buffers
                         |char buffers[SIZE][ELEMENT_BUFFER_SIZE];""".stripMargin)
              case _ => Nil
            }
          ),
          addBlankPrefix(
            lines(
              s"""|//! The array elements
                  |ElementType elements[SIZE];"""
            )
          )
        )
      )
    )

  // Writes a for loop to iterate over all indices of the array
  private def indexIterator(ll: List[Line]): List[Line] =
    wrapInForLoop(
      "U32 index = 0",
      "index < SIZE",
      "index++",
      ll,
    )
}
