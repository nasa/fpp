package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP system */
final case class FppSystem(
  aNode: Ast.Annotated[AstNode[Ast.DefSystem]],
  topology: Topology,
  dictionary: Dictionary
) {

  /** Gets the name of the system */
  def getName = aNode._2.data.name

  /** Gets the location of the system */
  def getLoc: Location = Locations.get(aNode._2.id)

}

