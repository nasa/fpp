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
    def getIncludeFiles(sym: Symbol): Option[String] = {
      val name = s.getName(sym)
      for {
        fileName <- sym match {
          case Symbol.AliasType(_) => Some(ComputeCppFiles.FileNames.getAliasType(name))
          case Symbol.AbsType(node) =>
             if s.isBuiltInType(name) then Some("FppConfig") else None
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

    val standardHeaders = List(
      "Fw/Types/BasicTypes.h",
    ).map(CppWriter.headerString)
    val symbolHeaders = writeHIncludeDirectives(s, aNode)
    val headers = standardHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.distinct.sorted.map(line)))
  }

  private def getHppIncludes: CppDoc.Member.Lines = {
    val standardHeaders = List(
      aliasType.aliasType match {
        case Type.String(_) => "Fw/Types/String.hpp"
        case Type.AbsType(node) =>
           s.isBuiltInType(node._2._1.name) match {
            case true => "FppConfig.hpp"
            case false => "Fw/Types/BasicTypes.h"
          }
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
    def getTypePRI(ty: Type): String = {
      ty match {
        case Type.Float(f) => aliasType.aliasType.toString().toLowerCase()
        case Type.PrimitiveInt(i) => aliasType.aliasType.toString().toLowerCase()
        case _ => typeCppWriter.write(ty)
      }
    }

    val fmtSpec = getTypePRI(aliasType.aliasType)

    linesMember(addBlankPrefix(
      AnnotationCppWriter.writePreComment(aNode) ++ lines(
        s"""|typedef ${typeCppWriter.write(aliasType.aliasType)} $name;
            |#define PRI_$name PRI_${fmtSpec}""")
    ))
  }
}
