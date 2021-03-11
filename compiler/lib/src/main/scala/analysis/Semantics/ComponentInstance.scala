package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP component instance */
final case class ComponentInstance(
  aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]],
  component: Component,
  baseId: Int,
  file: Option[String],
  queueSize: Option[Int],
  stackSize: Option[Int],
  priority: Option[Int]
)
