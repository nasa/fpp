package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP init specifier */
final case class InitSpecifier(
  aNode: Ast.Annotated[AstNode[Ast.SpecInit]],
  phase: Int
) {

  /** Gets the location for this init specifier */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object InitSpecifier {

  /** Creates an init specifier from an AST node */
  def fromNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInit]]
  ):
    Result.Result[InitSpecifier] = {
      val node = aNode._2
      val data = node.data
      for { phase <- a.getIntValue(data.phase.id) }
      yield InitSpecifier(aNode, phase)
    }

}
