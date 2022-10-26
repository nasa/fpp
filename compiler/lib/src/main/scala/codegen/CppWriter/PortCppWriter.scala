package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

case class PortCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends CppWriterLineUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Port(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getPort(name)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val strCppWriter = StringCppWriter(s, Some(name))

  private val params = data.params

  private val paramTypeMap = params.map((_, node, _) => {
    (node.data.name, s.a.typeMap(node.data.typeName.id))
  }).toMap

  private val paramList = params.map((_, node, _) => {
    val n = node.data.name
    val k = node.data.kind
    paramTypeMap(n) match {
      case t: Type.String => (n, strCppWriter.getClassName(t), k)
      case t => (n, typeCppWriter.write(t), k)
    }
  })

  private val cppParams = paramList.map((n, tn, k) => {
    (paramTypeMap(n), k)  match {
      case (_, Ast.FormalParam.Ref) => s"$tn& $n"
      case (t, Ast.FormalParam.Value) if s.isPrimitive(t, tn) => s"$tn $n"
      case (_, Ast.FormalParam.Value) => s"const $tn& $n"
    }
  }).mkString(", ")

  private val cppReturnType = data.returnType match {
    case Some(value) => s.a.typeMap(value.id) match {
      case t: Type.String => strCppWriter.getClassName(t)
      case t => typeCppWriter.write(t)
    }
    case None => "void"
  }

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defPortAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name port",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val annotation = AnnotationCppWriter.asStringOpt(aNode) match {
      case Some(value) => s"\n$value"
      case None => ""
    }
    val classes = List(
      getStringClasses,
      List(
        CppDoc.Member.Class(
          CppDoc.Class(
            Some(s"Input $name port" + annotation),
            s"Input${name}Port",
            Some("public Fw::InputPortBase"),
            getInputPortClassMembers
          )
        ),
        CppDoc.Member.Class(
          CppDoc.Class(
            Some(s"Output $name port" + annotation),
            s"Output${name}Port",
            Some("public Fw::OutputPortBase"),
            getOutputPortClassMembers
          )
        ),
      )
    ).flatten
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, classes)
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val standardHeaders = List(
      "FpConfig.hpp",
      "Fw/Cmd/CmdArgBuffer.hpp",
      "Fw/Comp/PassiveComponentBase.hpp",
      "Fw/Port/InputPortBase.hpp",
      "Fw/Port/OutputPortBase.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/StringType.hpp",
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    CppWriter.linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemHeaders = List(
      "cstdio",
      "cstring",
    ).map(CppWriter.systemHeaderString).map(line)
    val userHeaders = List(
      "Fw/Types/StringUtils.hpp",
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

  private def getStringClasses: List[CppDoc.Class.Member] = {
    val strTypes = paramTypeMap.map((_, t) => t match {
      case t: Type.String => Some(t)
      case _ => None
    }).filter(_.isDefined).map(_.get).toList
    strTypes match {
      case Nil => Nil
      case l => strCppWriter.write(l)
    }
  }

  private def getInputPortClassMembers: List[CppDoc.Class.Member] = {
    List(
      getInputPortConstantMembers,
      getInputPortTypeMembers
    ).flatten
  }

  private def getInputPortConstantMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
            CppDocWriter.writeBannerComment("Constants") ++
            addBlankPrefix(
              wrapInEnum(
                List(
                  lines("//! The size of the serial representations of the port arguments"),
                  lines("SERIALIZED_SIZE ="),
                  lines(paramList.map((n, tn, _) =>
                    s.getSerializedSizeExpr(paramTypeMap(n), tn)
                  ).mkString(" +\n")).map(indentIn)
                ).flatten
              )
            )
        )
      )
    )
  }

  private def getInputPortTypeMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("public"),
            CppDocWriter.writeBannerComment("Types"),
            Line.blank :: lines("//! The port callback function type"),
            lines(s"typedef $cppReturnType (*CompFuncPtr)(Fw::PassiveComponentBase* callComp, $cppParams);")
          ).flatten
        )
      )
    )
  }

  private def getOutputPortClassMembers: List[CppDoc.Class.Member] = {
    List(

    )
  }

}
