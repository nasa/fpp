package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for enum definitions */
case class EnumCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
) extends CppWriterLineUtils {

  val node = aNode._2

  val data = node.data

  val symbol = Symbol.Enum(aNode)

  val name = s.getName(symbol)

  val fileName = ComputeCppFiles.FileNames.getEnum(name)

  val enumType @ Type.Enum(_, _, _) = s.a.typeMap(node.id)

  val namespaceIdentList = s.getNamespaceIdentList(symbol)

  val typeCppWriter = TypeCppWriter(s)

  val repTypeName = typeCppWriter.write(enumType.repType)

  val numConstants = data.constants.size

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name enum",
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
    val systemStrings = List("cstring", "limits")
    val strings = List(
      "Fw/Types/Assert.hpp",
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
      getConstantMembers,
      getTypeMembers,
      getConstructorMembers,
      getMemberFunctionMembers,
      getMemberVariableMembers
    ).flatten

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
          CppDocWriter.writeBannerComment("Constants") ++
          addBlankPrefix(
            wrapInEnum(
              List(
                "//! The serialized size of each enumerated constant",
                s"SERIALIZED_SIZE = sizeof($repTypeName),",
                "//! The number of enumerated constants",
                s"NUM_CONSTANTS = $numConstants,"
              ).map(line)
            )
          )
        )
      )
    )

  private def getTypeMembers: List[CppDoc.Class.Member] = {
    val enumMembers = data.constants.flatMap(aNode => {
      val node = aNode._2
      val Value.EnumConstant(value, _) = s.a.valueMap(node.id)
      val valueString = value._2.toString
      val name = node.data.name
      AnnotationCppWriter.writePreComment(aNode) ++
      lines(s"$name = $valueString,")
    })
    val body = wrapInScope("typedef enum {", enumMembers, s"} t;")
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
          CppDocWriter.writeBannerComment("Types") ++
          addBlankPrefix(body)
        )
      )
    )
  }

  private def getConstructorMembers: List[CppDoc.Class.Member] =
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
          Some(s"Construct $name object with default initialization"),
          Nil,
          Nil,
          lines("this->e = ...;")
        )
      )
    )

  private def getMemberFunctionMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(CppDocHppWriter.writeAccessTag("public"))
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Member functions") ++
          addBlankPrefix(lines("// TODO")),
          CppDoc.Lines.Both
        )
      )
    )

  private def getMemberVariableMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(CppDocHppWriter.writeAccessTag("public"))
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Member variables") ++
          addBlankPrefix(
            List(
              "//! The enumeration value",
              "t e;"
            ).map(line)
          )
        )
      )
    )

}
