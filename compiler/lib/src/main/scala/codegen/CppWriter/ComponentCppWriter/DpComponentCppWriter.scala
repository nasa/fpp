package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component data product definitions */
case class DpComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val name = s.getName(symbol)

  private val baseClassName = s"${name}ComponentBase"

  private val componentFileName = ComputeCppFiles.FileNames.getComponent(name)

  private val dpBaseClassName = s"${name}DpComponentBase"

  private val dpFileName = ComputeCppFiles.FileNames.getDpComponent(name)

  private val dpWriter = ComponentDataProducts(s, aNode)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defComponentAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, dpFileName)
    CppWriter.createCppDoc(
      s"Data product base class for $name component",
      dpFileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = classMember(
      Some(
        addSeparatedString(
          s"""|\\class $dpBaseClassName
              |\\brief Auto-generated data product base class for $name component""",
          AnnotationCppWriter.asStringOpt(aNode)
        )
      ),
      dpBaseClassName,
      Some(s"public $baseClassName"),
      getClassMembers
    )
    List(
      List(hppIncludes, cppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val standardHeaders = List(
      "FpConfig.hpp",
      "Fw/Com/ComPacket.hpp",
      "Fw/Dp/DpContainer.hpp",
      s"${s.getRelativePath(componentFileName).toString}.hpp",
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val headers = List(
      "Fw/Types/Assert.hpp",
      s"${s.getRelativePath(dpFileName).toString}.hpp",
    )
    linesMember(
      addBlankPrefix(headers.map(CppWriter.headerString).map(line)),
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = List(
    getConstructionMembers,
  ).flatten

  private def getConstructionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Construction and destruction",
      List(
        constructorClassMember(
          Some(s"Construct $dpBaseClassName object"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const char*"),
              "compName",
              Some("The component name"),
              Some("\"\"")
            )
          ),
          List(s"${baseClassName}(compName)"),
          Nil
        ),
        destructorClassMember(
          Some(s"Destroy $dpBaseClassName object"),
          Nil,
          CppDoc.Class.Destructor.Virtual
        )
      )
    )

}
