package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component definitions */
object CheckComponentDefs
  extends Analyzer 
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  override def defComponentAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val a1 = a.copy(component = Some(Component(aNode)))
    for {
      a <- super.defComponentAnnotatedNode(a1, aNode)
    }
    yield {
      val symbol = Symbol.Component(aNode)
      a.copy(componentMap = a.componentMap + (symbol -> a.component.get))
    }
  }

  override def specPortInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) = {
    // TODO
    // Add the instance to the component
    for {
      _ <- PortInstances.fromSpecPortInstance(a, aNode)
    }
    yield a
  }

}
