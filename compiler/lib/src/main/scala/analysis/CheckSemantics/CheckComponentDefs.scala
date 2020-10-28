package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component definitions */
object CheckComponentDefs
  extends Analyzer 
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  /** Creates a port instance from an AST node */
  private def createPortInstance(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): 
    Result.Result[PortInstance] = {
    val (a1, node, a2) = aNode
    val data = node.getData
    data match {
      case general : Ast.SpecPortInstance.General =>
        throw new InternalError("TODO")
      case special : Ast.SpecPortInstance.Special =>
        Right(PortInstance.Special(aNode, special))
    }
      
  }

}
