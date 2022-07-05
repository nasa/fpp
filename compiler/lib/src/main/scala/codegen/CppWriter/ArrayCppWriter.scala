package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for array definitions */
case class ArrayCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefArray]]
) extends AstVisitor with CppWriterLineUtils {

  type In = CppWriterState

  type Out = List[Line]

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Array(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getArray(name)

  private val arrayType @ Type.Array(_, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val eltType = arrayType.anonArray.eltType

  private val arraySize = arrayType.getArraySize.get

  private val maxStrSize: Option[Int] = eltType match {
    case Type.String(size) => size match {
      case Some(typeNode) => s.a.valueMap(typeNode.id) match {
        case Value.EnumConstant(value, _) => Some(value._2.toInt)
        case Value.PrimitiveInt(value, _) => Some(value.toInt)
        case Value.Integer(value) => Some(value.toInt)
        case _ => None
      }
      case None => Some(s.defaultStringSize)
    }
    case _ => None
  }

  private val eltTypeName = eltType match {
    case Type.String(_) => s"StringSize${maxStrSize.get}" // Name of generated string class
    case _ => typeCppWriter.write(eltType)
  }

  private val hasPrimitiveEltType = eltType.isPrimitive || s.isBuiltInType(eltTypeName)

  private val formatStr = FormatCppWriter.write(
    arrayType.format match {
      case Some(f) => f
      case None => Format("", List((Format.Field.Default, "")))
    },
    data.eltType
  )

  override def default(s: CppWriterState): List[Line] = Nil

  override def defArrayAnnotatedNode(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ): List[Line] = {
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
    val strings = List(
      "Fw/Types/BasicTypes.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/String.hpp"
    )
    CppWriter.linesMember(
      List(
        Line.blank ::
          strings.map(CppWriter.headerString).map(line),
        Line.blank ::
          defArrayAnnotatedNode(s, aNode),
      ).flatten
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemStrings = List("cstring", "cstdio")
    val fwStrings = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/StringUtils.hpp",
    )
    val headerString = s"${s.getRelativePath(fileName).toString}.hpp"
    CppWriter.linesMember(
      List(
        List(Line.blank),
        systemStrings.map(CppWriter.systemHeaderString).map(line),
        List(Line.blank),
        fwStrings.map(CppWriter.headerString).map(line),
        List(Line.blank),
        lines(CppWriter.headerString(headerString)),
      ).flatten,
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List(
      getStringClass,
      getTypeMembers,
      getConstantMembers,
      getConstructorMembers,
      getOperatorMembers,
      getMemberFunctionMembers,
      getMemberVariableMembers,
    ).flatten

  // Generate a nested string class of the specified size for arrays of strings
  private def getStringClass: List[CppDoc.Class.Member] = eltType match {
    case Type.String(_) => StringCppWriter(List(maxStrSize.get), Some(name)).write
    case _ => Nil
  }

  private def getTypeMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("public"),
            CppDocWriter.writeBannerComment("Types"),
            lines(
              s"""|
                  |//! The element type
                  |typedef $eltTypeName ElementType;"""
            ),
          ).flatten
        )
      )
    )

  private def getConstantMembers: List[CppDoc.Class.Member] = {
    val serializedSizeStr =
      if hasPrimitiveEltType then "sizeof(ElementType)"
      else s"$eltTypeName::SERIALIZED_SIZE"
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
          CppDocWriter.writeBannerComment("Constants") ++
          addBlankPrefix(
            wrapInEnum(
              lines(
                s"""|//! The size of the array
                    |SIZE = $arraySize,
                    |//! The size of the serial representation
                    |SERIALIZED_SIZE = SIZE * $serializedSizeStr,"""
              )
            )
          )
        )
      )
    )
  }

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
          List("Serializable()"),
          List(
            lines("// Construct using element-wise constructor"),
            wrapInScope(
              s"*this = $name(",
              defaultValues.dropRight(1).map(v => line(s"$v,")) ++
                lines(s"${defaultValues.last}"),
              ");",
            ),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Constructor (user-provided value)"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const ElementType"),
              "(&a)[SIZE]",
              Some("The array"),
            )
          ),
          List("Serializable()"),
          indexIterator(lines("this->elements[index] = a[index];")),
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Constructor (single element)"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const ElementType&"),
              "e",
              Some("The element"),
            )
          ),
          List("Serializable()"),
          indexIterator(lines("this->elements[index] = e;")),
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Constructor (multiple elements)"),
          List.range(1, arraySize + 1).map(i => CppDoc.Function.Param(
            CppDoc.Type("const ElementType"),
            s"(&e$i)",
            Some(s"Element $i"),
          )),
          List("Serializable()"),
          List.range(1, arraySize + 1).map(i => line(
            s"this->elements[${i - 1}] = e$i;"
          )),
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Copy Constructor"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"const $name&"),
              "obj",
              Some("The source object"),
            )
          ),
          List("Serializable()"),
          indexIterator(lines("this->elements[index] = obj.elements[index];")),
        )
      ),
    )
  }

  private def getOperatorMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(CppDocHppWriter.writeAccessTag("public"))
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Operators"),
          CppDoc.Lines.Both,
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
            line("FW_ASSERT(i < SIZE);"),
            line("return this->elements[i];"),
          ),
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
            line("FW_ASSERT(i < SIZE);"),
            line("return this->elements[i];"),
          ),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const,
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
              Some("The other object"),
            ),
          ),
          CppDoc.Type("bool"),
          List(
            indexIterator(wrapInIf(
              "(*this)[index] != obj[index]",
              lines("return false;"),
            )),
            lines("return true;"),
          ).flatten,
          CppDoc.Function.NonSV,
          CppDoc.Function.Const,
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          lines("\n#ifdef BUILD_UT"),
          CppDoc.Lines.Both
        )
      ),
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
            lines(
              """|Fw::String s;
                 |obj.toString(s);
                 |os << s;
                 |return os;"""
            ),
            "}"
          ),
          CppDoc.Lines.Cpp
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          lines("\n#endif"),
          CppDoc.Lines.Both
        )
      ),
    )

  private def getMemberFunctionMembers: List[CppDoc.Class.Member] = {
    // Write string initialization for serializable element types in toString()
    val initStrings =
      if hasPrimitiveEltType then Nil
      else List(
        lines("// Call toString for arrays and serializable types"),
        List.range(0, arraySize).map(i => line(s"Fw::String str$i;")),
        List(Line.blank),
        List.range(0, arraySize).map(i => line(s"this->elements[$i].toString(str$i);")),
        List(Line.blank),
      ).flatten
    // Write format arguments in toString()
    val formatArgs =
      if hasPrimitiveEltType then
        List.range(0, arraySize - 1).map(i => line(s"this->elements[$i],")) ++
          lines(s"this->elements[${arraySize - 1}]")
      else
        List.range(0, arraySize - 1).map(i => line(s"str$i.toChar(),")) ++
          lines(s"str${arraySize - 1}.toChar()")

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
              Some("The serial buffer"),
            )
          ),
          CppDoc.Type("Fw::SerializeStatus"),
          List(
            lines("Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;"),
            indexIterator(
              line("status = buffer.serialize((*this)[index]);") ::
                wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
            ),
            lines("return status;"),
          ).flatten,
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Deserialization"),
          "deserialize",
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
              line("status = buffer.deserialize((*this)[index]);") ::
                wrapInIf("status != Fw::FW_SERIALIZE_OK", lines("return status;")),
            ),
            lines("return status;"),
          ).flatten,
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          lines("\n#if FW_ARRAY_TO_STRING || BUILD_UT"),
          CppDoc.Lines.Both
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
          List(
            wrapInScope(
              "static const char *formatString = \"[ \"",
              List.fill(arraySize - 1)(line(s"\"$formatStr \"")) ++
                lines(s"\"$formatStr ]\";"),
              ""
            ),
            initStrings,
            List(
              line("// Declare strings to hold any serializable toString() arguments"),
              line("char outputString[FW_ARRAY_TO_STRING_BUFFER_SIZE];"),
            ),
            wrapInScope(
              "(void) snprintf(",
              List(
                List(
                  line("outputString,"),
                  line("FW_ARRAY_TO_STRING_BUFFER_SIZE,"),
                  line("formatString,"),
                ),
                formatArgs,
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
          CppDoc.Function.Const,
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          lines("\n#endif"),
          CppDoc.Lines.Both
        )
      ),
    )
  }

  private def getMemberVariableMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(CppDocHppWriter.writeAccessTag("private"))
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Member variables") ++
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