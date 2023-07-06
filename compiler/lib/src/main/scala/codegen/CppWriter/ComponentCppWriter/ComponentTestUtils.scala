package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Utilities for writing C++ component test harness classes */
abstract class ComponentTestUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  val testerBaseClassName: String = s"${name}TesterBase"

  val gTestClassName: String = s"${name}GTestBase"

  val testImplClassName: String = s"${name}Tester"

  /** Get the name for the corresponding input port for an output port */
  def inputPortName(name: String) =
    s"from_$name"

  /** Get the name for the corresponding output port for an input port */
  def outputPortName(name: String) =
    s"to_$name"

  /** Get the name for an from port handler function */
  def fromPortHandlerName(name: String): String =
    inputPortHandlerName(inputPortName(name))

}