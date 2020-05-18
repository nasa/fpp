package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check location specifiers */
object CheckSpecLocs
  extends Analyzer
  with ModuleAnalyzer 
{

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    // TODO
    default(a)
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    // TODO
    default(a)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    // TODO
    default(a)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    // TODO
    default(a)
  }

}
