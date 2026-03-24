package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP topology port */
case class TopologyPort(
  aNode: Ast.Annotated[AstNode[Ast.SpecTopPort]],
  pii: PortInstanceIdentifier
) {

  def getLoc: Location = Locations.get(aNode._2.id)

  def getUnderlyingPortLoc = Locations.get(aNode._2.data.underlyingPort.id)

}