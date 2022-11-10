package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

case class ComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends CppWriterLineUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Component(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getComponent(name)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val kindStr = data.kind match {
    case Ast.ComponentKind.Active => "Active"
    case Ast.ComponentKind.Passive => "Passive"
    case Ast.ComponentKind.Queued => "Queued"
  }

  private val members = data.members
  
  private val generalPorts = members.map(member => {
    member match {
      case Ast.ComponentMember.SpecPortInstance(node) => node match {
        case p: Ast.SpecPortInstance.General => Some(p)
        case _ => None
      }
      case _ => None
    }
  }).filter(_.isDefined).map(_.get)

  private val inputPorts = generalPorts.map(p => {
    p.kind match {
      case Ast.SpecPortInstance.Output => None
      case _ => Some(p)
    }
  }).filter(_.isDefined).map(_.get)

  private val specialPorts = members.map(member => {
    member match {
      case Ast.ComponentMember.SpecPortInstance(node) => node match {
        case p: Ast.SpecPortInstance.Special => Some(p)
        case _ => None
      }
      case _ => None
    }
  }).filter(_.isDefined).map(_.get)

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defComponentAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component base class",
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
        s"${name}ComponentBase",
        Some(s"public Fw::${kindStr}ComponentBase"),
        getClassMembers
      )
    )
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val standardHeaders = List(
      "FpConfig.hpp",
      s"Fw/Comp/${kindStr}ComponnetBase.hpp"
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    CppWriter.linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemHeaders = List(
      "cstdio",
    ).map(CppWriter.systemHeaderString).map(line)
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/String.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp"
    ).sorted.map(CppWriter.headerString).map(line)
    CppWriter.linesMember(
      List(
        Line.blank :: systemHeaders,
        Line.blank :: userHeaders
      ).flatten,
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = {
    List(
      getInputPortGetters,
    ).flatten
  }

  private def getInputPortGetters: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment("Getters for typed input ports"),
            ).flatten
          )
        )
      ),
      inputPorts.map(p => {
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some("Get input port at index"),
            s"get_${p.name}_InputPort",
            List(
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "portNum"
              )
            ),
            CppDoc.Type(
              "a"
            ),
            Nil,
          )
        )
      })
    ).flatten
  }

}
