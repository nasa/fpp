package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++/C headers for type alias definitions */
case class AliasCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.AliasType(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getAliasType(name)

  private val aliasType @ Type.AliasType(_, _) = s.a.typeMap(node.id)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s, "Fw::String")

  private val supportedCHeader = s.isTypeSupportedInC(aliasType)

  private def writeHppIncludeDirectives(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
  ): List[String] = {
    val Right(a) = UsedSymbols.defAliasTypeAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  private def writeHIncludeDirectives(
    s: CppWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
  ): List[String] = {
    val Right(a) = UsedSymbols.defAliasTypeAnnotatedNode(s.a, aNode)
    // Here we can assume that all symbols referenced with be numeric primitives (U/I[8-64] or F32/F64)
    // ..or an alias to one of those types.
    // We already are including `FpConfig.h` as part of the system headers so we only have to handle the
    // alias case.
    def getIncludeFiles(sym: Symbol): Option[String] = {
      val name = s.getName(sym)
      for {
        fileName <- sym match {
          case Symbol.AliasType(_) => Some(ComputeCppFiles.FileNames.getAliasType(name))
          case _ => None
        }
      }
      yield s.getIncludePath(sym, fileName, "h")
    }

    a.usedSymbolSet.map(getIncludeFiles).filter(_.isDefined).map(_.get).map(CppWriterState.headerString).toList
  }

  def writeHpp: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName, "HPP")
    CppWriter.createCppDoc(
      s"$name alias",
      fileName,
      includeGuard,
      getHppMembers,
      s.toolName,
      "hpp"
    )
  }

  def writeH: Option[CppDoc] = {
    if (!supportedCHeader) {
      return None
    }

    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName, "H")
    Some(CppWriter.createCppDoc(
      s"$name alias",
      fileName,
      includeGuard,
      getHMembers,
      s.toolName,
      "h"
    ))
  }

  private def getHppMembers: List[CppDoc.Member] = {
    List.concat(
        List(getHppIncludes),
        wrapInNamespaces(namespaceIdentList, List(getHppDefinition))
    )
  }

  private def getHMembers: List[CppDoc.Member] = {
    List(
      getHIncludes,
      getHDefinition,
    )
  }

  private def getHPPCPPGuard: CppDoc.Member = {
    linesMember(addBlankPrefix(lines(
        """extern "C" {""")
      ))
  }

  private def getHPPCloseCPPGuard: CppDoc.Member = {
    linesMember(addBlankPrefix(lines(
        """}""")
      ))
  }

  private def getHIncludes: CppDoc.Member = {
    if (!supportedCHeader) {
      // C header is not supported, we will generate the definition
      // in the C++ header
      return linesMember(List())
    }

    // Include Fw/Types/BasicTypes.h here
    // To avoid an include cycle, don't depend on Fw/FPrimeBasicTypes.hpp
    // If the alias refers to one of those types, the header will appear
    // in symbolHeaders
    val standardHeaders = List(
      "Fw/Types/BasicTypes.h",
    ).map(CppWriter.headerString)

    val symbolHeaders = writeHIncludeDirectives(s, aNode)
    val headers = (standardHeaders ++ symbolHeaders).distinct.sorted.map(line)
    linesMember(addBlankPrefix(headers))
  }

  private def getHppIncludes: CppDoc.Member.Lines = {
    val standardHeaders = List(
      // Include BasicTypes.h or Fw/Types/String.hpp here
      // Fw/Types/String.hpp includes Fw/Types/BasicTypes.h
      // To avoid an include cycle, don't depend on Fw/FPrimeBasicTypes.hpp
      // If the alias refers to one of those types, the header will appear
      // in symbolHeaders
      aliasType.aliasType match {
        case Type.String(_) => "Fw/Types/String.hpp"
        case _ => "Fw/Types/BasicTypes.h"
      },
    ).map(CppWriter.headerString)
    val symbolHeaders = writeHppIncludeDirectives(s, aNode)
    val headers = standardHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.distinct.sorted.map(line)))
  }

  private def getHppDefinition: CppDoc.Member.Lines = {
    val name = s.getName(symbol)

    supportedCHeader match {
      case true => linesMember(
          // Include the C definition of the type alias
          // This is using a `typedef`
          addBlankPrefix(lines(
            s"""|extern "C" {
                |${CppWriterState.headerString(s.getIncludePath(symbol, ComputeCppFiles.FileNames.getAliasType(name), "h"))}
                |}""")
          ))
      case false => linesMember(addBlankPrefix(
        // Define a C++ only
        AnnotationCppWriter.writePreComment(aNode) ++ lines(
          s"using $name = ${typeCppWriter.write(aliasType.aliasType)};"
        )
      ))
    }
  }

  private def getHDefinition: CppDoc.Member.Lines = {
    val name = s.getName(symbol)

    val fmtSpecList = aliasType.aliasType match {
        // C-style floating-poing format strings (f, g) are platform-independent
        // In F Prime code, we just use them
        case Type.Float(f) => Nil
        // C-style integer format strings (d, u, ld, lu, etc.) are platform-specific
        // In F Prime code we don't use them. Instead we use a platform-independent macro.
        // Write out the macro here
        case _ => List("_" + typeCppWriter.write(aliasType.aliasType))
      }

    linesMember(
      addBlankPrefix(
        AnnotationCppWriter.writePreComment(aNode) ++ (
          s"typedef ${typeCppWriter.write(aliasType.aliasType)} $name;" ::
          fmtSpecList.map(s => s"#define PRI_$name PRI$s")
        ).map(line)
      )
    )
  }
}
