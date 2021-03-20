package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP topology */
case class Topology(
  /** The AST node defining the topology */
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]],
  /** The imported topologies */
  importedTopologyMap: Map[Topology, Location] = Map(),
  /** The instances of this topology */
  instanceMap: Map[ComponentInstance, (Ast.Visibility, Location)] = Map(),
  /** The connection patterns of this topology */
  patternMap: Map[Ast.SpecConnectionGraph.Pattern.Kind, ConnectionPattern] = Map(),
  /** The connection graphs of this topology */
  connectionGraphMap: Map[Name.Unqualified, List[Connection]] = Map(),
  // TODO
) {

  /** Add a connection */
  def addConnection(
    graphName: Name.Unqualified,
    connection: Connection
  ): Topology = {
    val connections = connectionGraphMap.getOrElse(graphName, Nil)
    val map = connectionGraphMap + (graphName -> (connection :: connections))
    this.copy(connectionGraphMap = map)
  }

  /** Add a pattern */
  def addPattern(
    kind: Ast.SpecConnectionGraph.Pattern.Kind,
    pattern: ConnectionPattern
  ): Result.Result[Topology] = {
    // TODO
    Right(this)
  }

  /** Add an imported topology */
  def addImportedTopology(
    topology: Topology,
    loc: Location
  ): Result.Result[Topology] =
    importedTopologyMap.get(topology) match {
      case Some(prevLoc) => Left(
        SemanticError.DuplicateTopology(
          topology.aNode._2.data.name,
          loc,
          prevLoc
        )
      )
      case None =>
        val map = importedTopologyMap + (topology -> loc)
        Right(this.copy(importedTopologyMap = map))
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

  /** Complete a topology definition */
  def complete: Result.Result[Topology] =
    //TODO
    Right(this)

}
