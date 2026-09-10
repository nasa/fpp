package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP template */
case class TemplateExpansion(
  /** The AST node defining the template */
  defNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]],
  /** The AST node expanding the template */
  expansion: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]],
  /** Arguments bound to the parameters of this template during expansion */
  params: Map[TemplateExpansion.ParamKey, TemplateArgSymbol],
  /** Scope where template argument symbols are entered */
  paramScope: Scope,
  /** Scope where symbols defined in the expansion are entered */
  scope: Scope
) {

}

object TemplateExpansion {

  /** A key into the parameter map: a name group paired with a parameter name */
  type ParamKey = (NameGroup, Name.Unqualified)

  /** Get the name group in which a template argument symbol is entered */
  def nameGroup(symbol: TemplateArgSymbol): NameGroup = symbol match {
    case Symbol.TemplateConstantArg(_, _) => NameGroup.Value
    case Symbol.TemplateTypeArg(_, _) => NameGroup.Type
    case Symbol.TemplateInterfaceArg(_, _) => NameGroup.PortInterfaceInstance
  }

  /** Get the parameter map key for a template argument symbol */
  def paramKey(symbol: TemplateArgSymbol): ParamKey =
    (nameGroup(symbol), symbol.getUnqualifiedName)

}
