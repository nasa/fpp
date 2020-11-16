package fpp.compiler.analysis

import fpp.compiler.ast._

/** An FPP parameter */
final case class Param(
  aNode: Ast.Annotated[AstNode[Ast.SpecParam]],
  default: Option[Value],
  setOpcode: Int,
  saveOpcode: Int
) {

  /** Gets the location of the parameter */
  def getLoc = Locations.get(aNode._2.id)

}

final object Param {

  type Id = Int

}
