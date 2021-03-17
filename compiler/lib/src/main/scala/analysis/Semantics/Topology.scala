package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP topology */
case class Topology(
  /** The AST node defining the topology */
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]],
  /** The imported topologies */
  importedTopologySet: Set[Topology] = Set(),
  /** The instances of this topology */
  instanceMap: Map[ComponentInstance, (Ast.Visibility, Location)] = Map(),
  // TODO
) {

  /** Add an instance that must be unique */
  def addUniqueInstance(
    instance: ComponentInstance,
    visibility: Ast.Visibility,
    loc: Location
  ): Result.Result[Topology] =
    instanceMap.get(instance) match {
      case Some((_, prevLoc)) => Left(
        SemanticError.DuplicateInstance(
          instance.aNode._2.data.name,
          loc,
          prevLoc
        )
      )
      case None => Right(addMergedInstance(instance, visibility, loc))
    }

  /** Add an instance that may already be there */
  def addMergedInstance(
    instance: ComponentInstance,
    vis: Ast.Visibility,
    loc: Location
  ): Topology = {
    def mergeVisibility(v1: Ast.Visibility, v2: Ast.Visibility) =
      (v1, v2) match {
        case (Ast.Visibility.Private, Ast.Visibility.Private) =>
          Ast.Visibility.Private
        case _ => Ast.Visibility.Public
      }
    val (mergedVis, mergedLoc) = instanceMap.get(instance) match {
      case Some((prevVis, prevLoc)) => 
        // Merge the visibility and use the previous location
        (mergeVisibility(prevVis, vis), prevLoc)
      case None => (vis, loc)
    }
    val map = instanceMap + (instance -> (mergedVis, mergedLoc))
    this.copy(instanceMap = map)
  }

  /** Complete a topology definition */
  def complete: Result.Result[Topology] =
    //TODO
    Right(this)

}
