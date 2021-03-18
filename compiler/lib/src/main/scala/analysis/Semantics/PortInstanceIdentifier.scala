package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance identifier */
case class PortInstanceIdentifier(
  /** The AST node defining the identifier */
  node: AstNode[Ast.PortInstanceIdentifier],
  /** The component instance */
  componentInstance: ComponentInstance,
  /** The port instance */
  portInstance: PortInstance
)
