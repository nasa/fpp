package fpp.compiler.analysis

import fpp.compiler.ast._

/** An FPP command */
final case class Command(
  aNode: Ast.Annotated[AstNode[Ast.SpecCommand]],
  priority: Option[Int],
  queueFull: Ast.QueueFull
)

final object Command {

  type Opcode = Int

}
