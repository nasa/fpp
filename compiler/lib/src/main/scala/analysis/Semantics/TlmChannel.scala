package fpp.compiler.analysis

import fpp.compiler.ast._

/** An FPP telemetry channel */
final case class TlmChannel(
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]],
  update: Ast.SpecTlmChannel.Update,
  format: Option[Format],
  lowLimits: TlmChannel.Limits,
  highLimits: TlmChannel.Limits
) {

  /** Gets the location of the command */
  def getLoc = Locations.get(aNode._2.id)

}

final object TlmChannel {

  type Id = Int

  type Limits = Map[Ast.SpecTlmChannel.LimitKind, Value]

}
