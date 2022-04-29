package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for enum definitions */
case class EnumCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
) extends LineUtils {

  val node = aNode._2

  val data = node.data

  val name = aNode._2.data.name

  val fileName = ComputeCppFiles.FileNames.getEnum(data.name)

  val symbol = Symbol.Enum(aNode)

  val enumType @ Type.Enum(_, _, _) = s.a.typeMap(node.id)

  val namespaceIdentList = s.getNamespaceIdentList(symbol)

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"${data.name} enum",
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
        data.name,
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
      s.getRelativePath(fileName).toString
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
      getInstanceMethodMembers,
      getInstanceVariableMembers
    ).flatten

  private def getConstantMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
          CppDocWriter.writeBannerComment("Constants")
        )
      )
    )
    

  private def getTypeMembers: List[CppDoc.Class.Member] =
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
          CppDocWriter.writeBannerComment("Types") ++
          List(
            line("enum {"),
            //indentIn(defLine),
            line("};")
          )
        )
      )
    )

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    // TODO
    Nil
  }

  private def getInstanceMethodMembers: List[CppDoc.Class.Member] = {
    // TODO
    Nil
  }

  private def getInstanceVariableMembers: List[CppDoc.Class.Member] = {
    // TODO
    Nil
  }

}
