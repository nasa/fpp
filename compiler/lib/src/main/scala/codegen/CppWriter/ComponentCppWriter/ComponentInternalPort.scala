package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component internal ports */
case class ComponentInternalPort (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    if !hasInternalPorts then Nil
    else List(
      getHandlers,
      getHandlerBases
    ).flatten
  }

  private def getHandlers: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "PROTECTED",
        "Internal interface handlers"
      ),
      internalPorts.map(p =>
        functionClassMember(
          Some(
            s"Internal interface handler for ${p.getUnqualifiedName}"
          ),
          internalInterfaceHandlerName(p.getUnqualifiedName),
          getPortFunctionParams(p),
          CppDoc.Type("void"),
          Nil,
          CppDoc.Function.PureVirtual
        )
      )
    ).flatten
  }

  private def getHandlerBases: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "PROTECTED",
        "Internal interface base-class functions"
      ),
      internalPorts.map(p =>
        functionClassMember(
          Some(
            s"Internal interface base-class function for ${p.getUnqualifiedName}"
          ),
          internalInterfaceHandlerBaseName(p.getUnqualifiedName),
          getPortFunctionParams(p),
          CppDoc.Type("void"),
          Nil
        )
      )
    ).flatten
  }

  // Get the name for an internal interface handler
  private def internalInterfaceHandlerName(name: String) =
    s"${name}_internalInterfaceHandler"

  // Get the name for an internal interface base-class function
  private def internalInterfaceHandlerBaseName(name: String) =
    s"${name}_internalInterfaceInvoke"

}