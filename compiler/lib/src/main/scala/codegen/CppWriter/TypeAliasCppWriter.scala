package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for array definitions */
case class TypeAliasCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.AliasType(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getAliasType(name)

  private val aliasType @ Type.AliasType(_, _) = s.a.typeMap(node.id)

  private val typeCppWriter = TypeCppWriter(s, "Fw::ExternalString")

  private val aliasedTypeName = typeCppWriter.write(aliasType.aliasType)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private def writeIncludeDirectives(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
  ): List[String] = {
    val Right(a) = UsedSymbols.defAliasTypeAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name type alias",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    List(
      List(getHppIncludes),
      wrapInNamespaces(namespaceIdentList, List(getTypeDefMember))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    // FIXME(tumbar) Should we only include the headers we need if this is a builtin type?
    val standardHeaders = List(
      "FpConfig.hpp",
      "Fw/Types/String.hpp"
    ).map(CppWriter.headerString)

    val symbolHeaders = writeIncludeDirectives(s, aNode)
    val headers = standardHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getTypeDefMember: CppDoc.Member = {
    linesMember(addBlankPrefix(List(
      line(s"using $name = $aliasedTypeName;")
    )))
  }
}
