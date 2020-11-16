package fpp.compiler.analysis

import fpp.compiler.ast._

/** An FPP event */
final case class Event(
  aNode: Ast.Annotated[AstNode[Ast.SpecEvent]],
  format: Option[Format],
  throttle: Option[Int]
) {

  /** Gets the location of the event */
  def getLoc = Locations.get(aNode._2.id)

}

final object Event {

  type Id = Int

}
