package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes C++ port definitions */
case class PortCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends PortCppWriterUtils(s, aNode) {

  private val portBufferClassWriter = PortBufferClassWriter(s, aNode)

  private val inputPortClassWriter = InputPortClassWriter(s, aNode)

  private val outputPortClassWriter = OutputPortClassWriter(s, aNode)

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(portSymbol, portFileName)
    CppWriter.createCppDoc(
      s"$portName port",
      portFileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getClasses =
    List.concat(
      guardedList (!hasReturnValue) (List(portBufferClassWriter.write)),
      wrapMembersInIfDirective(
        "#if !FW_DIRECT_PORT_CALLS",
        List(
          inputPortClassWriter.write,
          outputPortClassWriter.write
        ),
        CppDoc.Lines.Both
      )
    )

  private def getMembers: List[CppDoc.Member] =
    List.concat(
      List(
        getHppIncludes,
        getCppIncludes
      ),
      wrapInNamespaces(
        namespaceIdentList,
        getClasses
      )
    )

  private def getHppIncludes: CppDoc.Member = {
    val unconditional = List.concat(
      List(
        "Fw/FPrimeBasicTypes.hpp",
        "Fw/Types/String.hpp"
      ).map(CppWriter.headerString),
      writeIncludeDirectives
    ).sorted.map(line)
    val conditional = List.concat(
      guardedList (!hasReturnValue) (List("Fw/Types/Serializable.hpp")),
      List(
        "Fw/Comp/PassiveComponentBase.hpp",
        "Fw/Port/InputPortBase.hpp",
        "Fw/Port/OutputPortBase.hpp",
      )
    ).map(CppWriter.headerString).sorted.map(line)
    linesMember(
      Line.blank ::
      List.concat(
        unconditional,
        lines("#if !FW_DIRECT_PORT_CALLS"),
        conditional,
        lines("#endif")
      )
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/ExternalString.hpp",
      s.getIncludePath(portSymbol, portFileName)
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(
      Line.blank :: userHeaders,
      CppDoc.Lines.Cpp
    )
  }

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defPortAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

}
