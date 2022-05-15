package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP topology */
case class Topology(
  /** The AST node defining the topology */
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]],
  /** The directly imported topologies */
  directImportMap: Map[Symbol.Topology, Location] = Map(),
  /** The transitively imported topologies */
  transitiveImportSet: Set[Symbol.Topology] = Set(),
  /** The instances of this topology */
  instanceMap: Map[ComponentInstance, (Ast.Visibility, Location)] = Map(),
  /** The connection patterns of this topology */
  patternMap: Map[Ast.SpecConnectionGraph.Pattern.Kind, ConnectionPattern] = Map(),
  /** The connections of this topology, indexed by graph name */
  connectionMap: Map[Name.Unqualified, List[Connection]] = Map(),
  /** The connections defined locally, not imported */
  localConnectionMap: Map[Name.Unqualified, List[Connection]] = Map(),
  /** The output connections going from each port */
  outputConnectionMap: Map[PortInstanceIdentifier, Set[Connection]] = Map(),
  /** The input connections going to each port */
  inputConnectionMap: Map[PortInstanceIdentifier, Set[Connection]] = Map(),
  /** The mapping between connections and from port numbers */
  fromPortNumberMap: Map[Connection, Int] = Map(),
  /** The mapping between connections and to port numbers */
  toPortNumberMap: Map[Connection, Int] = Map(),
  /** The unconnected port instances */
  unconnectedPortSet: Set[PortInstanceIdentifier] = Set()
) {

  /** Add a connection */
  def addConnection(
    graphName: Name.Unqualified,
    connection: Connection
  ): Topology = {
    val cgMap = {
      val connections = connectionMap.getOrElse(graphName, Nil)
      connectionMap + (graphName -> (connection :: connections))
    }
    val ocMap = {
      val from = connection.from.port
      val connections = outputConnectionMap.getOrElse(from, Set())
      outputConnectionMap + (from -> (connections + connection))
    }
    val icMap = {
      val to = connection.to.port
      val connections = inputConnectionMap.getOrElse(to, Set())
      inputConnectionMap + (to -> (connections + connection))
    }
    val fpnMap = connection.from.portNumber match {
      case Some(n) => fromPortNumberMap + (connection -> n)
      case None => fromPortNumberMap
    }
    val tpnMap = connection.to.portNumber match {
      case Some(n) => toPortNumberMap + (connection -> n)
      case None => toPortNumberMap
    }
    this.copy(
      connectionMap = cgMap,
      outputConnectionMap = ocMap,
      inputConnectionMap = icMap,
      fromPortNumberMap = fpnMap,
      toPortNumberMap = tpnMap
    )
  }

  /** Add a local connection */
  def addLocalConnection (
    graphName: Name.Unqualified,
    connection: Connection
  ): Topology = {
    val lcMap = {
      val connections = localConnectionMap.getOrElse(graphName, Nil)
      localConnectionMap + (graphName -> (connection :: connections))
    }
    addConnection(graphName, connection).copy(localConnectionMap = lcMap)
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
          pattern.getLoc,
          prevPattern.getLoc
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
    directImportMap.get(symbol) match {
      case Some(prevLoc) => Left(
        SemanticError.DuplicateTopology(
          symbol.getUnqualifiedName,
          loc,
          prevLoc
        )
      )
      case None =>
        val map = directImportMap + (symbol -> loc)
        Right(this.copy(directImportMap = map))
    }

  /** Add an instance that may already be there */
  def addMergedInstance(
    instance: ComponentInstance,
    vis: Ast.Visibility,
    loc: Location
  ): Topology = {
    import Ast.Visibility._
    // Private overrides public
    val pairOpt = instanceMap.get(instance)
    val mergedVis = (vis, pairOpt) match {
      case (Private, _) => Private
      case (_, Some((Private, _))) => Private
      case _ => Public
    }
    // Use the previous location, if it exists
    val mergedLoc = pairOpt.map(_._2).getOrElse(loc)
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
          instance.getUnqualifiedName,
          loc,
          prevLoc
        )
      )
      case None => Right(addMergedInstance(instance, vis, loc))
    }

  /** Assigns a port number to a connection at a port instance */
  def assignPortNumber(
    pi: PortInstance,
    c: Connection,
    n: Int
  ): Topology = {
    import PortInstance.Direction._
    pi.getDirection.get match {
      case Input => this.copy(
        toPortNumberMap = this.toPortNumberMap + (c -> n)
      )
      case Output => this.copy(
        fromPortNumberMap = this.fromPortNumberMap + (c -> n)
      )
    }
  }

  /**  Get the port number of a connection at a port instance */
  def getPortNumber(pi: PortInstance, c: Connection): Option[Int] = {
    import PortInstance.Direction._
    pi.getDirection.get match {
      case Input => toPortNumberMap.get(c)
      case Output => fromPortNumberMap.get(c)
    }
  }

  /** Check whether a connection exists between two ports*/
  def connectionExistsBetween(
    from: PortInstanceIdentifier,
    to: PortInstanceIdentifier
  ): Boolean = getConnectionsBetween(from, to).size > 0

  /** Get the connections between two ports */
  def getConnectionsBetween(
    from: PortInstanceIdentifier,
    to: PortInstanceIdentifier
    ): Set[Connection] = getConnectionsFrom(from).filter(c => c.to.port == to)

  /** Get the connections from a port */
  def getConnectionsFrom(from: PortInstanceIdentifier): Set[Connection] =
    outputConnectionMap.getOrElse(from, Set())

  /** Get the connections to a port */
  def getConnectionsTo(to: PortInstanceIdentifier): Set[Connection] =
    inputConnectionMap.getOrElse(to, Set())

  /** Get the connections at a port instance */
  def getConnectionsAt(pii: PortInstanceIdentifier): Set[_ <: Connection] = {
    import PortInstance.Direction._
    val pi = pii.portInstance
    pi.getDirection match {
      case Some(Input) => inputConnectionMap.getOrElse(pii, Set())
      case Some(Output) => outputConnectionMap.getOrElse(pii, Set())
      case None => Set()
    }
  }

  /** Gets the unqualified name of the topology */
  def getUnqualifiedName = aNode._2.data.name

  /** Gets the set of used port numbers */
  def getUsedPortNumbers(pi: PortInstance, cs: Iterable[Connection]): Set[Int] = 
    cs.foldLeft (Set[Int]()) ((s, c) =>
      getPortNumber(pi, c) match {
        case Some(n) => s + n
        case None => s
      }
    )

  /** Look up a component instance used at a location */
  def lookUpInstanceAt(
    instance: ComponentInstance,
    loc: Location
  ): Result.Result[(Ast.Visibility, Location)] =
    instanceMap.get(instance) match {
      case Some(result) => Right(result)
      case None => Left(
        SemanticError.InvalidComponentInstance(
          loc,
          instance.getUnqualifiedName,
          this.getUnqualifiedName
        )
      )
    }

  /** Resolve the numbers in a connection */
  def resolveNumbers(c: Connection): Connection = {
    val fromNumber = getPortNumber(c.from.port.portInstance, c)
    val toNumber = getPortNumber(c.to.port.portInstance, c)
    val from = c.from.copy(portNumber = fromNumber)
    val to = c.to.copy(portNumber = toNumber)
    c.copy(from = from, to = to)
  }

  /** Sort connections */
  def sortConnections(connections: Seq[Connection]): Seq[Connection] =
    connections.map(c => (c, resolveNumbers(c))).sortWith(_._2 < _._2).map(_._1)

}
