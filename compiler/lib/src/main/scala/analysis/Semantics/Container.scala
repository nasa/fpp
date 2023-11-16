package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP data product containter */
final case class Container(
  aNode: Ast.Annotated[AstNode[Ast.SpecContainer]],
  defaultPriority: Option[BigInt]
) {

  /** Gets the name of the container */
  def getName = aNode._2.data.name

  /** Gets the location of the container */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object Container {

  type Id = BigInt

  /** Creates a container from a container specifier */
  def fromSpecContainer(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecContainer]]):
    Result.Result[Container] = {
      val node = aNode._2
      val data = node.data
      for {
        defaultPriority <- a.getNonnegativeBigIntValueOpt(data.defaultPriority)
      }
      yield Container(aNode, defaultPriority)
   }

}
