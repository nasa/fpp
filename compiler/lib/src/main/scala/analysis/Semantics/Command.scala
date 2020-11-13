package fpp.compiler.analysis

import fpp.compiler.ast._

/** An FPP command */
final case class Command(
  aNode: Ast.Annotated[AstNode[Ast.SpecCommand]],
  priority: Option[Int],
  queueFull: Ast.QueueFull
) {

  /** Gets the location of the command */
  def getLoc = Locations.get(aNode._2.id)

}

final object Command {

  type Opcode = Int

}
