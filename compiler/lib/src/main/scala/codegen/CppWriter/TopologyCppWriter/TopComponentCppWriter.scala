package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out topology C++ for component definitions */
case class TopComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]],
  componentInstanceMap: TopComponents.ComponentInstanceMap
) extends ComponentCppWriterUtils(s, aNode) {

  def writeIsConnectedFns = {
    val qualifiedName = s.a.getQualifiedName(Symbol.Component(aNode))
    lines(
      s"""|
          |// TODO: isConnected functions for component $qualifiedName"""
    )
  }

  def writeInvocationFns = {
    val qualifiedName = s.a.getQualifiedName(Symbol.Component(aNode))
    lines(
      s"""|
          |// TODO: Invocation functions for component $qualifiedName"""
    )
  }

}
