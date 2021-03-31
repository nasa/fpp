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
  /** The connections of this topology */
  connectionMap: Map[Name.Unqualified, List[Connection]] = Map(),
  /** The connections defined locally, not imported */
  localConnectionMap: Map[Name.Unqualified, List[Connection]] = Map(),
  /** The output connections going from each port */
  outputConnectionMap: Map[PortInstanceIdentifier, Set[Connection]] = Map(),
  /** The input connections going to each port */
  inputConnectionMap: Map[PortInstanceIdentifier, Set[Connection]] = Map(),
  /** The mapping of connections to from port numbers */
  fromPortNumberMap: Map[Connection, Int] = Map(),
  /** The mapping of connections to to port numbers */
  toPortNumberMap: Map[Connection, Int] = Map(),
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

  /** Apply general numbering */
  private def applyGeneralNumbering: Result.Result[Topology] = {
    // TODO
    Right(this)
  }

  /** Get the connetions at a port instance */
  private def getConnectionsAt(pii: PortInstanceIdentifier) = {
    import PortInstance.Direction._
    val pi = pii.portInstance
    pi.getDirection.get match {
      case Input => inputConnectionMap.getOrElse(pii, Set())
      case Output => outputConnectionMap.getOrElse(pii, Set())
    }
  }

  /** Check output ports */
  private def checkOutputPorts: Result.Result[Topology] =
    Result.foldLeft (outputConnectionMap.toList) (this) ({
      case (_, (pii, s)) => for {
        _ <- checkOutputSizeBounds(pii, s)
        _ <- checkDuplicateOutputPorts(pii, s)
      }
      yield this
    })

  /** Check that connection instances are legal */
  private def checkConnectionInstances: Result.Result[Topology] = {
    def checkConnection(c: Connection) = {
      val fromInstance = c.from.port.componentInstance
      val toInstance = c.to.port.componentInstance
      for {
        _ <- lookUpInstanceAt(fromInstance, c.from.loc)
        _ <- lookUpInstanceAt(toInstance, c.to.loc)
      }
      yield ()
    }
    for {
      _ <- Result.map(
        connectionMap.toList.map(_._2).flatten,
        checkConnection
      )
    }
    yield this
  }

  /** Check that there are no duplicate port numbers at any output
   *  ports. */
  private def checkDuplicateOutputPorts(
    pii: PortInstanceIdentifier,
    connections: Set[Connection]
  ): Result.Result[Unit] = {
    val portNumMap: Map[Int, Connection] = Map()
    for {
      _ <- Result.foldLeft (connections.toList) (portNumMap) ((m, c) =>
          c.from.portNumber match {
            case Some(portNum) => {
              m.get(portNum) match {
                case Some(prevC) => 
                  val loc = c.from.loc
                  val prevLoc = prevC.from.loc
                  Left(
                    SemanticError.DuplicateOutputPort(loc, portNum, prevLoc)
                  )
                case None => Right(m + (portNum -> c))
              }
            }
            case None => Right(m)
          }
      )
    }
    yield ()
  }

  /** Check the bounds on the number of output connections */
  private def checkOutputSizeBounds(
    pii: PortInstanceIdentifier,
    connections: Set[Connection]
  ): Result.Result[Unit] = {
    val pi = pii.portInstance
    val arraySize = pi.getArraySize
    val numPorts = connections.size
    if (numPorts <= arraySize)
      Right(())
    else {
      val loc = pi.getLoc
      val instanceLoc = pii.componentInstance.getLoc
      Left(
        SemanticError.TooManyOutputPorts(
          loc,
          numPorts,
          arraySize,
          instanceLoc
        )
      )
    }
  }

  /** Check the instances of a pattern */
  private def checkPatternInstances(pattern: ConnectionPattern) =
    for {
      // Check the source
      _ <- {
        val (instance, loc) = pattern.source
        lookUpInstanceAt(instance, loc)
      }
      // Check the targets
      _ <- Result.map(
        pattern.targets.toList,
        (pair: (ComponentInstance, Location)) => {
          val (instance, loc) = pair
          lookUpInstanceAt(instance, loc)
        }
      )
    }
    yield ()

  /** Check whether a connection exists between two ports*/
  def connectionExistsBetween(
    from: PortInstanceIdentifier,
    to: PortInstanceIdentifier
  ) = getConnectionsBetween(from, to).size > 0

  /** Compute the transitively imported topologies */
  private def computeTransitiveImports(a: Analysis) = {
    val tis = directImportMap.keys.foldLeft (Set[Symbol.Topology]()) ((tis, ts) => {
      val t = a.topologyMap(ts)
      tis.union(t.transitiveImportSet) + ts
    })
    this.copy(transitiveImportSet = tis)
  }

  /** Compute the unused ports for this topology */
  private def computeUnusedPorts: Result.Result[Topology] = {
    // TODO
    Right(this)
  }

  /** Fill in the port numbers for this topology */
  private def computePortNumbers: Result.Result[Topology] =
    Result.seq(
      Right(this),
      List(
        _.checkOutputPorts,
        Topology.Numbering.Matched.apply(_),
        _.applyGeneralNumbering
      )
    )

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

  /** Gets the unqualified name of the topology */
  def getUnqualifiedName = aNode._2.data.name

  /** Look up a component instance used at a location */
  private def lookUpInstanceAt(
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

  /** Resolve a topology definition */
  def resolve(a: Analysis): Result.Result[Topology] =
    Result.seq(
      Right(this),
      List(
        _.resolvePartiallyNumbered(a),
        _.computePortNumbers,
        _.computeUnusedPorts
      )
    )

  /** Resolve the connection patterns of this topology */
  private def resolvePatterns(a: Analysis): Result.Result[Topology] = {
    import Ast.SpecConnectionGraph._
    def getGraphName(kind: Pattern.Kind) = kind match {
      case Pattern.Command => "Commands"
      case Pattern.Event => "Events"
      case Pattern.Health => "Health"
      case Pattern.Param => "Parameters"
      case Pattern.Telemetry => "Telemetry"
      case Pattern.TextEvent => "TextEvents"
      case Pattern.Time => "Time"
    }
    Result.foldLeft (patternMap.values.toList) (this) ((t, p) => {
      val instances = instanceMap.keys
      for {
        _ <- checkPatternInstances(p)
        connections <- PatternResolver.resolve(a, p, instances)
      }
      yield {
        val name = getGraphName(p.ast.kind)
        connections.foldLeft (t) ((t, c) =>
          // Skip this connection if it already exists
          // For example, it could be imported
          if (!connectionExistsBetween(c.from.port, c.to.port))
            t.addLocalConnection(name, c)
          else t
        )
      }
    })
  }

  /** Resolve the imported connections of this topology */
  private def resolveImportedConnections(a: Analysis): Result.Result[Topology] = {
    // Check whether an endpoint is public
    def endpointIsPublic(endpoint: Connection.Endpoint) = {
      val instance = endpoint.port.componentInstance
      val (vis, _) = instanceMap(instance)
      vis == Ast.Visibility.Public
    }
    // Check whether a connection is public
    def isPublic(connection: Connection) =
      endpointIsPublic(connection.from) &&
      endpointIsPublic(connection.to)
    // Import connections from transitively imported topologies
    val result = transitiveImportSet.
      map(a.topologyMap(_).localConnectionMap).flatten.
      foldLeft (this) ({ case (t, (name, cs)) =>
        cs.foldLeft (t) ((t1, c) =>
          if (isPublic(c)) t1.addConnection(name, c) else t1
        )
      })
    Right(result)
  }

  /** Resolve the instances of this topology */
  private def resolveInstances(a: Analysis): Result.Result[Topology] = {
    def importInstance(
      t: Topology,
      mapEntry: (ComponentInstance, (Ast.Visibility, Location))
    ) = {
      val (instance, (vis, loc)) = mapEntry
      vis match {
        case Ast.Visibility.Public =>
          t.addMergedInstance(instance, vis, loc)
        case Ast.Visibility.Private => t
      }
    }
    def importInstances(into: Topology, fromSymbol: Symbol.Topology) =
      a.topologyMap(fromSymbol).instanceMap.foldLeft (into) (importInstance)
    Right(directImportMap.keys.foldLeft (this) (importInstances))
  }

  /** Resolve this topology to a partially numbered topology */
  private def resolvePartiallyNumbered(a: Analysis): Result.Result[Topology] = {
    val t = this.computeTransitiveImports(a)
    Result.seq(
      Right(t),
      List(
        _.resolveInstances(a),
        _.checkConnectionInstances,
        _.resolveImportedConnections(a),
        _.resolvePatterns(a)
      )
    )
  }

}

private object Topology {

  object Numbering {

    // State for numbering ports
    private case class State(
      // The used port numbers
      usedPortNumbers: Set[Int],
      // The next port number
      nextPortNumber: Int
    )

    // Assign a port number to a connection at a port instance
    def assignPortNumber(
      t: Topology,
      pi: PortInstance,
      c: Connection,
      n: Int
    ): Topology = {
      import PortInstance.Direction._
      pi.getDirection.get match {
        case Input => t.copy(
          toPortNumberMap = t.toPortNumberMap + (c -> n)
        )
        case Output => t.copy(
          fromPortNumberMap = t.fromPortNumberMap + (c -> n)
        )
      }
    }

    // Get the next available port number
    def getNextNumber(from: Int, used: Set[Int]): Int = {
      def helper(n: Int): Int = 
        if (!used.contains(n))
          n
        else helper(n + 1)
      helper(from)
    }

    /** Compute matched numbering */
    object Matched {

      // A mapping from component instances to connections
      private type ConnectionMap = Map[ComponentInstance, Connection]

      // State for numbering ports
      private case class State private(
        // The topology
        t: Topology,
        // The port instance for port 1
        pi1: PortInstance,
        // The map from component instances to connections for port 1
        map1: ConnectionMap,
        // The port instance for port 2
        pi2: PortInstance,
        // The map from component instances to connections for port 2
        map2: ConnectionMap,
        // Numbering state
        numberingState: Numbering.State
      )

      private object State {

        // Number a connection pair
        def numberConnectionPair(
          state: State,
          c1: Connection,
          c2: Connection
        ) = {
          // TODO
          // * If c1 and c2 both have numbers, check that they match
          // * Otherwise if c1 has a number then assign it to c2
          // * Otherwise if c2 has a number then assign it to c1
          // * Otherwise assign n, add n to S, and increment n
          //   until it is not in S.
          Right(state)
        }

        // For each pair of connections (c1, c2), check that numbers 
        // match and/or assign numbers
        def assignNumbers(state: State): Result.Result[State] = {
          val (map1, map2) = (state.map1, state.map2)
          // Fold over map1
          for {
            result <- Result.foldLeft (map1.toList) (state) ( { 
              case (state, (ci, c1)) => {
                val c2 = map2(ci)
                numberConnectionPair(state, c1, c2)
              }
            })
          }
          yield result
        }

        // Get the port number of a connection at a port instance
        def getPortNumber(t: Topology, pi: PortInstance, c: Connection) = {
          import PortInstance.Direction._
          pi.getDirection.get match {
            case Input => t.toPortNumberMap(c)
            case Output => t.fromPortNumberMap(c)
          }
        }

        def initial(
          t: Topology,
          pi1: PortInstance,
          map1: ConnectionMap,
          pi2: PortInstance,
          map2: ConnectionMap
        ) = {
          // Compute the set of used port numbers in map1
          val usedPortNumbers = map1.values.foldLeft (Set[Int]()) ((s, c) =>
            s + getPortNumber(t, pi1, c)
          )
          // Set the next number n to the smallest number not in S
          val nextPortNumber = getNextNumber(0, usedPortNumbers)
          State(
            t,
            pi1,
            map1,
            pi2,
            map2,
            Numbering.State(usedPortNumbers, nextPortNumber)
          )
        }

      }

      /** Apply matched numbering */
      def apply(t: Topology): Result.Result[Topology] = {
        // Fold over instances and matchings
        Result.foldLeft (t.instanceMap.keys.toList) (t) ((t, ci) =>
          Result.foldLeft (ci.component.portMatchingList) (t) ((u, pm) =>
            handlePortMatching(t, ci, pm)
          )
        )
      }

      // Check for missing connections
      private def checkForMissingConnections(
        map1: ConnectionMap,
        map2: ConnectionMap
      ): Result.Result[Unit] = {
        // Ensure that map2 contains everything in map1
        def helper(map1: ConnectionMap, map2: ConnectionMap) =
          Result.foldLeft (map1.keys.toList) (()) ((u, ci) =>
            if (map2.contains(ci))
              Right(())
            else
              ??? // Missing connection
          )
        // Ensure that the two sets of keys match
        if (map1.size >= map2.size)
          helper(map1, map2)
        else
          helper(map2, map1)
      }

      // Handle one port matching
      private def handlePortMatching(
        t: Topology,
        ci: ComponentInstance,
        portMatching: Component.PortMatching
      ) = {
        // Map remote components to connections at pi
        def constructMap(pi: PortInstance) = {
          val empty: ConnectionMap = Map()
          val pii = PortInstanceIdentifier(ci, pi)
          val cs = t.getConnectionsAt(pii).toList.sorted
          Result.foldLeft (cs) (empty) ((m, c) => {
            val piiRemote = pii.getOtherEndpoint(c).port
            val ciRemote = piiRemote.componentInstance
            m.get(ciRemote) match {
              case Some(cPrev) => ??? // Duplicate connection
              case None => Right(m + (ciRemote -> c))
            }
          })
        }
        val pi1 = portMatching.instance1
        val pi2 = portMatching.instance2
        for {
          map1 <- constructMap(pi1)
          map2 <- constructMap(pi2)
          _ <- checkForMissingConnections(map1, map2)
          state <- {
            val state = State.initial(t, pi1, map1, pi2, map2)
            State.assignNumbers(state)
          }
        }
        yield state.t
      }

    }

  }

}
