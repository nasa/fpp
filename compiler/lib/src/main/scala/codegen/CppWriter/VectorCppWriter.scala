package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for vector definitions */
case class VectorCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefVector]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Vector(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getVector(name)

  private val vectorType @ Type.Vector(_, _, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s, "Fw::ExternalString")

  private val eltType = vectorType.anonVector.eltType

  private val eltTypeName = typeCppWriter.write(eltType)

  private val hasPrimitiveEltType = s.isPrimitive(eltType, eltTypeName)

  private val hasStringEltType= s.isStringType(eltType)

  private val constructorEltType = if hasStringEltType then "Fw::StringBase" else "ElementType"

  private val initializerListEltType = if hasStringEltType then "Fw::String" else constructorEltType

  private val capacity = vectorType.anonVector.maxSize.get

  private val sizePrefixType = vectorType.sizePrefixType.getOrElse {
    val fwSizeStoreSymbol = s.a.frameworkDefinitions.typeMap("FwSizeStoreType")
    s.a.typeMap(fwSizeStoreSymbol.getNodeId)
  }

  private val sizePrefixTypeName = typeCppWriter.write(sizePrefixType)

  private val formatStr = FormatCppWriter.write(
    s,
    vectorType.format.getOrElse(Format("", List((Format.Field.Default, "")))),
    data.eltType
  )

  private def writeIncludeDirectives(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefVector]]
  ): List[String] = {
    val Right(a) = UsedSymbols.defVectorAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name vector",
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
    List.concat(
      List(hppIncludes, cppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    )
  }

  private def getHppIncludes: CppDoc.Member = {
    val systemHeaders = List(
      "initializer_list"
    ).map(CppWriter.systemHeaderString).map(line)
    val userHeaders = {
      val standardHeaders = List(
        "Fw/FPrimeBasicTypes.hpp",
        "Fw/Types/ExternalString.hpp",
        "Fw/Types/Serializable.hpp",
        "Fw/Types/String.hpp",
        "Fw/Types/SuccessEnumAc.hpp"
      ).map(CppWriter.headerString)
      val symbolHeaders = writeIncludeDirectives(s, aNode)
      (standardHeaders ++ symbolHeaders).sorted.map(line)
    }
    linesMember(addBlankPrefix(systemHeaders) ++ addBlankPrefix(userHeaders))
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      s.getIncludePath(symbol, fileName)
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
        List.concat(
          CppDocHppWriter.writeAccessTag("public"),
          CppDocWriter.writeBannerComment("Types"),
          lines(
            s"""|
                |//! The element type
                |using ElementType = $eltTypeName;"""
          ),
        )
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
                    |ELEMENT_SERIALIZED_SIZE = ${writeStaticSerializedSizeExpr(s, eltType, eltTypeName)},"""
            }
            lines(s"""|//! The maximum size of the vector
                      |CAPACITY = $capacity,
                      |${elementSizes.stripMargin}
                      |//! The serialized size of the length prefix
                      |SIZE_PREFIX_SERIALIZED_SIZE = ${writeStaticSerializedSizeExpr(s, sizePrefixType, sizePrefixTypeName)},
                      |//! The maximum size of the serial representation
                      |SERIALIZED_SIZE = SIZE_PREFIX_SERIALIZED_SIZE + CAPACITY * ELEMENT_SERIALIZED_SIZE""")
          })
        )
      )
    )

  private val initElementsCall = guardedList (hasStringEltType) (lines("this->initElements();"))

  private val defaultIsEmpty: Boolean =
    vectorType.getDefaultValue.get.anonArray.elements.isEmpty

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    val defaultValueConstructor = constructorClassMember(
      Some("Constructor (default value)"),
      Nil,
      List(
        "Serializable()",
        "elements()",
        "length(0)"
      ),
      List.concat(
        initElementsCall,
        guardedList (!defaultIsEmpty) ({
          val valueString = ValueCppWriter.write(s, vectorType.getDefaultValue.get)
          lines(s"*this = $valueString;")
        })
      )
    )
    val initializerListConstructor = constructorClassMember(
      Some("Constructor (initializer list)"),
      List(
        CppDoc.Function.Param(
          CppDoc.Type(s"const std::initializer_list<$initializerListEltType>&"),
          "il",
          Some("The initializer list"),
        ),
      ),
      List("Serializable()", "elements()", "length(0)"),
      List.concat(
        initElementsCall,
        lines("*this = il;")
      )
    )
    val copyConstructor = constructorClassMember(
      Some("Copy constructor"),
      List(
        CppDoc.Function.Param(
          CppDoc.Type(s"const $name&"),
          "obj",
          Some("The source object"),
        )
      ),
      List("Serializable()", "elements()", "length(0)"),
      List.concat(
        initElementsCall,
        lines("*this = obj;")
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
        defaultValueConstructor,
        initializerListConstructor,
        copyConstructor
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
            CppDoc.Type("const FwSizeType"),
            "i",
            Some("The subscript index"),
          ),
        ),
        CppDoc.Type("ElementType&", Some(s"$name::ElementType&")),
        List(
          line("FW_ASSERT(i < this->length, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(this->length));"),
          line("return this->elements[i];"),
        ),
      ),
      functionClassMember(
        Some("Const subscript operator"),
        "operator[]",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const FwSizeType"),
            "i",
            Some("The subscript index"),
          ),
        ),
        CppDoc.Type("const ElementType&", Some(s"const $name::ElementType&")),
        List(
          line("FW_ASSERT(i < this->length, static_cast<FwAssertArgType>(i), static_cast<FwAssertArgType>(this->length));"),
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
        List.concat(
          wrapInIf(
            "this != &obj",
            List.concat(
              lines("this->length = obj.length;"),
              lengthIterator(lines("this->elements[index] = obj.elements[index];")),
            ),
          ),
          lines("return *this;"),
        ),
      ),
      functionClassMember(
        Some("Copy assignment operator (initializer list)"),
        "operator=",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const std::initializer_list<$initializerListEltType>&"),
            "il",
            Some("The initializer list"),
          ),
        ),
        CppDoc.Type(s"$name&"),
        lines("""|// Check that the initializer does not exceed the capacity
                 |FW_ASSERT(il.size() <= CAPACITY, static_cast<FwAssertArgType>(il.size()), static_cast<FwAssertArgType>(CAPACITY));
                 |this->length = static_cast<FwSizeType>(il.size());
                 |FwSizeType i = 0;
                 |for (const auto& e : il) {
                 |  this->elements[i] = e;
                 |  i++;
                 |}
                 |return *this;""")
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
        List.concat(
          wrapInIf(
            "this->length != obj.length",
            lines("return false;"),
          ),
          lengthIterator(wrapInIf(
            "!((*this)[index] == obj[index])",
            lines("return false;"),
          )),
          lines("return true;"),
        ),
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
    ) ++ writeOstreamOperator(
      name,
      lines(
        """|Fw::String s;
           |obj.toString(s);
           |os << s;
           |return os;"""
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

    val formatLoop = lengthIterator(lines(
      s"""|// Vector data
          |Fw::String tmp;
          |$fillTmpString
          |
          |if (index > 0) {
          |  sb += ", ";
          |}
          |sb += tmp;
          |"""
    ))
    val serializedSize = eltType.getUnderlyingType match {
      case ts: (Type.String | Type.Array | Type.Struct | Type.Vector) => {
        List.concat(
          lines("FwSizeType size = SIZE_PREFIX_SERIALIZED_SIZE;"),
          lengthIterator(lines(
            "size += this->elements[index].serializedSize();"
          )),
          lines("return size;")
        )
      }
      case _ => lines("return SIZE_PREFIX_SERIALIZED_SIZE + this->length * ELEMENT_SERIALIZED_SIZE;")
    }

    List(
      linesClassMember(
        CppDocHppWriter.writeAccessTag("public")
      ),
      linesClassMember(
        CppDocWriter.writeBannerComment("Public member functions"),
        CppDoc.Lines.Both
      ),
      functionClassMember(
        Some("Get the current length of the vector"),
        "getLength",
        List(),
        CppDoc.Type("FwSizeType"),
        lines("return this->length;"),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      ),
      functionClassMember(
        Some("Set the current length of the vector"),
        "setLength",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("const FwSizeType"),
            "len",
            Some("The length"),
          ),
        ),
        CppDoc.Type("void"),
        lines(
          """|FW_ASSERT(len <= CAPACITY, static_cast<FwAssertArgType>(len), static_cast<FwAssertArgType>(CAPACITY));
             |this->length = len;"""
        )
      ),
      functionClassMember(
        Some("Push an element onto the end of the vector"),
        "push",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"const $constructorEltType&"),
            "e",
            Some("The element to push"),
          ),
        ),
        CppDoc.Type("Fw::Success"),
        List.concat(
          wrapInIf("this->length >= CAPACITY", lines("return Fw::Success::FAILURE;")),
          lines(
            """|this->elements[this->length] = e;
               |this->length++;
               |return Fw::Success::SUCCESS;"""
          ),
        )
      ),
      functionClassMember(
        Some("Pop the last element off the vector"),
        "pop",
        List(
          CppDoc.Function.Param(
            CppDoc.Type(s"$constructorEltType&"),
            "e",
            Some("The popped element"),
          ),
        ),
        CppDoc.Type("Fw::Success"),
        List.concat(
          wrapInIf("this->length == 0", lines("return Fw::Success::FAILURE;")),
          lines(
            """|this->length--;
               |e = this->elements[this->length];
               |return Fw::Success::SUCCESS;"""
          ),
        )
      ),
      functionClassMember(
        Some("Serialization"),
        "serializeTo",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerialBufferBase&"),
            "buffer",
            Some("The serial buffer"),
          ),
          CppDoc.Function.Param(
            CppDoc.Type("Fw::Endianness"),
            "mode",
            Some("Endianness of serialized buffer"),
            Some("Fw::Endianness::BIG"),
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        List.concat(
          lines("Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;"),
          lines(s"// Serialize the length as a prefix"),
          line(s"status = buffer.serializeFrom(static_cast<$sizePrefixTypeName>(this->length), mode);") ::
            wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
          lengthIterator(
            line("status = buffer.serializeFrom((*this)[index], mode);") ::
              wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
          ),
          lines("return status;"),
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      ),
      functionClassMember(
        Some("Deserialization"),
        "deserializeFrom",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerialBufferBase&"),
            "buffer",
            Some("The serial buffer"),
          ),
          CppDoc.Function.Param(
            CppDoc.Type("Fw::Endianness"),
            "mode",
            Some("Endianness of serialized buffer"),
            Some("Fw::Endianness::BIG"),
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        List.concat(
          lines("Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;"),
          lines(s"// Deserialize the length prefix"),
          line(s"$sizePrefixTypeName storedLength = 0;") ::
            line("status = buffer.deserializeTo(storedLength, mode);") ::
            wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
          wrapInIf(
            "static_cast<FwSizeType>(storedLength) > static_cast<FwSizeType>(CAPACITY)",
            lines("return Fw::FW_DESERIALIZE_SIZE_MISMATCH;"),
          ),
          lines("this->length = static_cast<FwSizeType>(storedLength);"),
          lengthIterator(
            line("status = buffer.deserializeTo((*this)[index], mode);") ::
              wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
          ),
          lines("return status;"),
        ),
      ),
      functionClassMember(
        Some("Get the dynamic serialized size of the vector"),
        "serializedSize",
        List(),
        CppDoc.Type("FwSizeType"),
        serializedSize,
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    ) ++
      wrapClassMembersInIfDirective(
        "#if FW_SERIALIZABLE_TO_STRING",
        List(
          functionClassMember(
            Some("Convert vector to string"),
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
                    |// Vector prefix
                    |sb += \"[ \";"""),
              List(Line.blank),
              formatLoop,
              List(Line.blank),
              lines(
                s"""|// Vector suffix
                    |sb += \" ]\";""")
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
        capacityIterator(
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
                         |char buffers[CAPACITY][ELEMENT_BUFFER_SIZE];""".stripMargin)
              case _ => Nil
            }
          ),
          addBlankPrefix(
            lines(
              s"""|//! The vector elements
                  |ElementType elements[CAPACITY];"""
            )
          ),
          addBlankPrefix(
            lines(
              s"""|//! The current length of the vector
                  |FwSizeType length;"""
            )
          )
        )
      )
    )

  // Writes a for loop to iterate over the present elements of the vector
  private def lengthIterator(ll: List[Line]): List[Line] =
    wrapInForLoop(
      "FwSizeType index = 0",
      "index < this->length",
      "index++",
      ll,
    )

  // Writes a for loop to iterate over all indices up to the capacity of the vector
  private def capacityIterator(ll: List[Line]): List[Line] =
    wrapInForLoop(
      "FwSizeType index = 0",
      "index < CAPACITY",
      "index++",
      ll,
    )
}
