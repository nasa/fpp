package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A data structure that represents a template parameter */
sealed trait TemplateParameter(
  val paramDefNode: Ast.Annotated[AstNode[Ast.TemplateParam]]
)

object TemplateParameter {
  final case class Constant(
    /** Node pointing to definition */
    defNode: Ast.Annotated[AstNode[Ast.TemplateParam.Constant]],
    /** Node holding the concrete value */
    valueNode: AstNode[Ast.Expr]
  ) extends TemplateParameter(defNode)

  final case class Type(
    defNode: Ast.Annotated[AstNode[Ast.TemplateParam.Type]]
  ) extends TemplateParameter(defNode)

  final case class Interface(
    defNode: Ast.Annotated[AstNode[Ast.TemplateParam.Interface]]
  ) extends TemplateParameter(defNode)
}

/** An FPP template */
case class Template(
  /** The AST node defining the template */
  aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]],
  /** Fully qualified name of the template */
  qualifiedName: Name.Qualified
) { }
