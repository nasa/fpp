package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP telemetry packet */
final case class TlmPacket(
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]],
  level: Int,
  members: Set[TlmChannel.Id]
) {

  /** Gets the name of the packet */
  def getName = aNode._2.data.name

  /** Gets the location of the packet specifier */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object TlmPacket {

  type Id = BigInt

  // TODO

}
