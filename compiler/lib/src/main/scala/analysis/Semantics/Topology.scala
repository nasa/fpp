package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP topology */
case class Topology(
  /** The AST node defining the topology */
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]],
  /** The imported topologies */
  importedTopologyMap: Map[Symbol.Topology, Location] = Map(),
  /** The instances of this topology */
  instanceMap: Map[ComponentInstance, (Ast.Visibility, Location)] = Map(),
  /** The connection patterns of this topology */
  patternMap: Map[Ast.SpecConnectionGraph.Pattern.Kind, ConnectionPattern] = Map(),
  /** The connection graphs of this topology */
  connectionGraphMap: Map[Name.Unqualified, List[Connection]] = Map(),
  /** The set of port instances declared as unused */
  declaredUnusedPortSet: Set[PortInstanceIdentifier] = Set(),
  /** The set of port instances actually unused */
  unusedPortSet: Set[PortInstanceIdentifier] = Set()
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
  ): Result.Result[Topology] = patternMap.get(kind) match {
    case Some(prevPattern) =>
      Left(
        SemanticError.DuplicatePattern(
          kind.toString,
          Locations.get(pattern.aNode._2.id),
          Locations.get(prevPattern.aNode._2.id)
        )
      )
    case None =>
      val pm = patternMap + (kind -> pattern)
      Right(this.copy(patternMap = pm))
  }

  /** Add an imported topology */
  def addImportedTopology(
    symbol: Symbol.Topology,
    loc: Location
  ): Result.Result[Topology] =
    importedTopologyMap.get(symbol) match {
      case Some(prevLoc) => Left(
        SemanticError.DuplicateTopology(
          symbol.getUnqualifiedName,
          loc,
          prevLoc
        )
      )
      case None =>
        val map = importedTopologyMap + (symbol -> loc)
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
    vis: Ast.Visibility,
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
      case None => Right(addMergedInstance(instance, vis, loc))
    }

  /** Resolve a topology definition */
  def resolve(a: Analysis): Result.Result[Topology] =
    Result.seq(
      Right(this),
      List(
        _.resolveToPartiallyNumbered(a),
        _.computePortNumbers,
        _.computeUnusedPorts
      )
    )

  /** Compute the unused ports for this topology */
  private def computeUnusedPorts: Result.Result[Topology] = {
    // TODO
    Right(this)
  }

  /** Fill in the port numbers for this topology */
  private def computePortNumbers: Result.Result[Topology] = {
    // TODO
    Right(this)
  }

  /** Resolve the connection patterns of this topology */
  private def resolvePatterns: Result.Result[Topology] = {
    // TODO
    Right(this)
  }

  /** Resolve the direct connections of this topology */
  private def resolveDirectConnections: Result.Result[Topology] = {
    // TODO
    Right(this)
  }

  /** Resolve the instances of this topology */
  private def resolveInstances(a: Analysis): Result.Result[Topology] = {
    def importInstances(into: Topology, fromSymbol: Symbol.Topology) = {
      val from = a.topologyMap(fromSymbol)
      from.instanceMap.foldLeft (into) ((t, entry) => {
        val (instance, (vis, loc)) = entry
        t.addMergedInstance(instance, vis, loc)
      })
    }
    val t = importedTopologyMap.keys.foldLeft (this) ((into, from) => {
      importInstances(into, from)
    })
    Right(t)
  }

  /** Resolve this topology to a partially numbered topology */
  private def resolveToPartiallyNumbered(a: Analysis): Result.Result[Topology] =
    Result.seq(
      Right(this),
      List(
        _.resolveInstances(a),
        _.resolveDirectConnections,
        _.resolvePatterns
      )
    )

}
