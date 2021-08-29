package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP init specifier */
final case class InitSpecifier(
  aNode: Ast.Annotated[AstNode[Ast.SpecInit]],
  instance: ComponentInstance,
  phase: Int
) {

  /** Gets the location for this init specifier */
  def getLoc = Locations.get(aNode._2.id)

}

object InitSpecifier {

  /** Creates an init specifier from an AST node */
  def fromNode(
    a: Analysis,
    instance: ComponentInstance,
    aNode: Ast.Annotated[AstNode[Ast.SpecInit]]
  ):
    Result.Result[InitSpecifier] = {
      val node = aNode._2
      val data = node.data
      for { phase <- a.getIntValue(data.phase.id) }
      yield InitSpecifier(aNode, instance, phase)
    }

  /** Creates an init specifier from an old-style AST node */
  def fromOldNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecInitOld]]):
    Result.Result[InitSpecifier] = {
      val node = aNode._2
      val data = node.data
      val specInit = Ast.SpecInit(data.phase, data.code)
      val specInitNode = AstNode.create(specInit, node.id)
      val specInitANode = (aNode._1, specInitNode, aNode._3)
      for {
        instance <- a.getComponentInstance(data.instance.id)
        phase <- a.getIntValue(data.phase.id)
      }
      yield InitSpecifier(specInitANode, instance, phase)
    }

}
