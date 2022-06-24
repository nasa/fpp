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

  private val defaultValue = ValueCppWriter.write(s, arrayType.getDefaultValue.get)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val eltTypeName = typeCppWriter.write(arrayType.anonArray.eltType)

  private val arraySize = arrayType.getArraySize.get

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
//      getConstructorMembers,
//      getOperatorMembers,
//      getMemberFunctionMembers,
//      getMemberVariableMembers
    ).flatten

  private def getTypeMembers: List[CppDoc.Class.Member] = {
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
  }

  private def getConstantMembers: List[CppDoc.Class.Member] = {
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
  }
}