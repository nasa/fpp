package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check for use of unimplemented features */
object CheckUnimplemented extends ModuleAnalyzer {

  override def defComponentAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefComponent]]) =
    unimplemented(a, node._2)

  override def defComponentInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) =
    unimplemented(a, node._2)

  override def defPortAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefPort]]) =
    unimplemented(a, node._2)

  override def defTopologyAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefTopology]]) =
    unimplemented(a, node._2)

  override def specCompInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]) =
    unimplemented(a, node._2)

  override def specInitAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecInit]]) =
    unimplemented(a, node._2)

  private def unimplemented[T](a: Analysis, node: AstNode[T]): Result = {
    val loc = Locations.get(node.getId)
    Left(SemanticError.NotImplemented(loc))
  }

}
