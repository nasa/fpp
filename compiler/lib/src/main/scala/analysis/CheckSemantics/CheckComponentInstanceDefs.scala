package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component instance definitions */
object CheckComponentInstanceDefs
  extends Analyzer 
  with ModuleAnalyzer
{

  override def defComponentInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = {
    // TODO
    default(a)
  }

  /** Ensure that ID ranges do not overlap */
  def checkIdRanges(a: Analysis): Result.Result[Unit] = {
    // TODO
    Right(())
  }

}
