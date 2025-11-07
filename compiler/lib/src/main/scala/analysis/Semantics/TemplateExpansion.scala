package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP template */
case class TemplateExpansion(
  /** The AST node defining the template */
  defNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]],
  /** The AST node expanding the template */
  expansion: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]],
  /** Concrete parameters given to this template during expansion */
  params: Map[String, Symbol.TemplateParam],
  /** Scope where parameter symbols are entered */
  scope: Scope
) {

}
