package fpp.compiler.analysis

import fpp.compiler.ast._

/** An FPP telemetry channel */
final case class TlmChannel(
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]],
  channelType: Type,
  update: Ast.SpecTlmChannel.Update,
  format: Option[Format],
  lowLimits: TlmChannel.Limits,
  highLimits: TlmChannel.Limits
) {

  /** Gets the name of the channel */
  def getName = aNode._2.data.name

  /** Gets the location of the channel */
  def getLoc = Locations.get(aNode._2.id)

}

final object TlmChannel {

  type Id = Int

  type Limits = Map[Ast.SpecTlmChannel.LimitKind, (AstNode.Id, Value)]

}
