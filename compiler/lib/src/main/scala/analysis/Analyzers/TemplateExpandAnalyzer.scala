package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze template expansions members */
trait TemplateExpandAnalyzer extends Analyzer {

  /** A use of a template constant parameter */
  def templateConstantParam(a: Analysis, param: Symbol.TemplateConstantParam) = default(a)

  /** A use of a template type parameter */
  def templateTypeParam(a: Analysis, param: Symbol.TemplateTypeParam) = default(a)

  /** A use of a template interface parameter */
  def templateInterfaceParam(a: Analysis, param: Symbol.TemplateInterfaceParam) = default(a)

  def templateParam(a: Analysis, param: Symbol.TemplateParam) = {
    param match {
      case param: Symbol.TemplateConstantParam => templateConstantParam(a, param)
      case param: Symbol.TemplateTypeParam => templateTypeParam(a, param)
      case param: Symbol.TemplateInterfaceParam => templateInterfaceParam(a, param)
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
