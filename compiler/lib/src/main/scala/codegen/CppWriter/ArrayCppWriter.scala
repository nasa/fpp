package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for array definitions */
case class ArrayCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefArray]]
) extends CppWriterLineUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Array(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getArray(name)

  private val arrayType @ Type.Array(_, _, _, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val eltTypeName = typeCppWriter.write(arrayType.anonArray.eltType)

  private val arraySize = arrayType.getArraySize.get

  private def getDefaultValues: List[String] = {
    val defaultValue = arrayType.getDefaultValue match {
      case Some(a) => Some(a.anonArray)
      case None => arrayType.anonArray.getDefaultValue
    }
    defaultValue match {
      case Some(a) => a.elements.map(ValueCppWriter.write(s, _))
      case None => Nil // TODO: Get unspecified default value
    }
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name array",
      fileName,
      includeGuard,
      getMembers
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
      Line.blank ::
        strings.map(CppWriter.headerString).map(line)
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemStrings = List("cstring", "cstdio")
    val strings = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/StringUtils.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp"
    )
    CppWriter.linesMember(
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
      getMemberVariableMembers
    ).flatten

  private def indexIterator(ll: List[Line]): List[Line] =
    wrapInForLoop(
      "U32 index = 0",
      "index < SIZE",
      "index++",
      ll,
    )

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

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
          CppDocWriter.writeBannerComment("Types") ++
          addBlankPrefix(
            wrapInEnum(
              lines(
                s"""|//! The size of the array
                    |SIZE = $arraySize;
                    |//! The size of the serial representation
                    |SERIALIZED_SIZE = SIZE * sizeof(ElementType),"""
              )
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
          List("Serializable()"),
          wrapInScope(
            s"*this = $name(",
            defaultValues.dropRight(1).map(v => line(s"$v,")) ++
            lines(s"${defaultValues.last}"),
            ");",
          ),
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
          CppDoc.Type("ElementType&"),
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
          CppDoc.Type("const ElementType&"),
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
          CppDoc.Type("A&"),
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
          CppDoc.Type("A&"),
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
          CppDoc.Type("A&"),
          List(
            indexIterator(lines("this->elements[index] = e")),
            lines("return *this;"),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Equality operatord"),
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
              "(*this)[index] != other[index]",
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
          Some("Equality operatord"),
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
}