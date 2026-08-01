package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze template expansions members */
trait TemplateExpandAnalyzer extends Analyzer {

  /** A use of a template constant argument */
  def templateConstantArg(a: Analysis, arg: Symbol.TemplateConstantArg) = default(a)

  /** A use of a template type parameter */
  def templateTypeArg(a: Analysis, arg: Symbol.TemplateTypeArg) = default(a)

  /** A use of a template interface parameter */
  def templateInterfaceArg(a: Analysis, arg: Symbol.TemplateInterfaceArg) = default(a)

  def templateParam(a: Analysis, arg: TemplateArgSymbol) = {
    arg match {
      case param: Symbol.TemplateConstantArg => templateConstantArg(a, param)
      case param: Symbol.TemplateTypeArg => templateTypeArg(a, param)
      case param: Symbol.TemplateInterfaceArg => templateInterfaceArg(a, param)
    }
  }

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {

      // Analyze the paramters on this template expansion
      a <- {
        val expansion = a.templateExpansionMap(node.id)
        Result.foldLeft (expansion.params.values.toList) (a) (templateParam)
      }

      a <- super.specTemplateExpandAnnotatedNode(a, aNode)
    } yield a
  }

}
