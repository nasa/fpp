package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve a partially numbered topology */
object ResolvePartiallyNumbered {

  /** Check that connection instances are legal */
  private def checkConnectionInstances(t: Topology): Result.Result[Unit] = {
    def checkConnection(c: Connection) = {
      val fromInstance = c.from.port.interfaceInstance
      val toInstance = c.to.port.interfaceInstance
      for {
        _ <- t.lookUpInstanceAt(fromInstance, c.from.loc)
        _ <- t.lookUpInstanceAt(toInstance, c.to.loc)
      }
      yield ()
    }
    for {
      _ <- Result.map(
        t.connectionMap.toList.map(_._2).flatten,
        checkConnection
      )
    }
    yield ()
  }

  /** Check that connection instances are legal */
  private def checkPortInstances(t: Topology): Result.Result[Unit] = {
    def checkPort(i: (PortInstanceIdentifier, Location)) = {
      val (port, loc) = i
      for {
        _ <- t.lookUpInstanceAt(port.interfaceInstance, loc)
      }
      yield ()
    }
    for {
      _ <- Result.map(
        t.portMap.toList.map(_._2),
        checkPort
      )
    }
    yield ()
  }

  /** Check the instances of a pattern */
  private def checkPatternInstances(t: Topology, pattern: ConnectionPattern) =
    for {
      // Check the source
      _ <- {
        val (instance, loc) = pattern.source
        t.lookUpInstanceAt(InterfaceInstance.fromComponentInstance(instance), loc)
      }
      // Check the targets
      _ <- Result.map(
        pattern.targets.toList,
        (pair: (ComponentInstance, Location)) => {
          val (instance, loc) = pair
          t.lookUpInstanceAt(InterfaceInstance.fromComponentInstance(instance), loc)
        }
      )
    }
    yield ()

  /** Compute the transitively imported topologies */
  private def computeTransitiveImports(a: Analysis, t: Topology) = {
    val tis = t.directTopologies.keys.foldLeft (Set[Symbol.Topology]()) ((tis, ts) => {
      val t = a.topologyMap(ts)
      tis.union(t.transitiveImportSet) + ts
    })
    t.copy(transitiveImportSet = tis)
  }

  /** Resolve the connection patterns of t  */
  private def resolvePatterns(a: Analysis, t: Topology): Result.Result[Topology] = {
    import Ast.SpecConnectionGraph._
    Result.foldLeft (t.patternMap.values.toList) (t) ((t, p) => {
      val instances = t.componentInstanceMap.keys
      for {
        _ <- checkPatternInstances(t, p)
        connections <- PatternResolver.resolve(a, p, instances)
      }
      yield {
        connections.foldLeft (t) ({ case (t, (name, c)) =>
          // Skip this connection if it already exists
          // For example, it could be imported
          if (!t.connectionExistsBetween(c.from.port, c.to.port))
            t.addLocalConnection(name, c)
          else t
        })
      }
    })
  }

  /** Resolve the imported connections of t */
  private def resolveImportedConnections(a: Analysis, t: Topology): 
  Result.Result[Topology] = {
    // Check whether an instance exists
    def endpointExists(endpoint: Connection.Endpoint) = {
      val instance = endpoint.port.interfaceInstance
      t.instanceMap.get(instance) match {
        case Some(_) => true
        case None => false
      }
    }
    // Check whether a connection exists
    def exists(connection: Connection) =
      endpointExists(connection.from) &&
      endpointExists(connection.to)
    // Import connections from transitively imported topologies
    val result = t.transitiveImportSet.
      map(a.topologyMap(_).localConnectionMap).flatten.
      foldLeft (t) ({ case (t, (name, cs)) =>
        cs.foldLeft (t) ((t1, c) =>
          if (exists(c)) t1.addConnection(name, c) else t1
        )
      })
    Right(result)
  }

  /** Resolve connections to interface instances of t to component instances */
  private def resolveInterfacesToComponentInstances(a: Analysis, t: Topology): Result.Result[Topology] = {
    def visitConnection (graphName: Name.Unqualified) (tt: Topology, c: Connection): Topology = {
      tt.addLocalConnection(graphName, c.copy(
        from = c.from.getUnderlyingEndpoint(),
        to = c.to.getUnderlyingEndpoint(),
      ))
    }

    def visitConnectionGraph(tt: Topology, graph: (Name.Unqualified, List[Connection])): Topology = {
      graph._2.foldLeft (tt) (visitConnection (graph._1))
    }

    // Clear out connections of T and reprocess them
    // Resolve all port instance identifiers to their 'true' component instance port
    Right(t.localConnectionMap.foldLeft (t.copy(
      localConnectionMap = Map(),
      connectionMap = Map(),
      outputConnectionMap = Map(),
      inputConnectionMap = Map(),
      fromPortNumberMap = Map(),
      toPortNumberMap = Map()
    )) (visitConnectionGraph))
  }

  /** Resolve the instances of t */
  private def resolveInstances(a: Analysis, t: Topology): Result.Result[Topology] = {
    def importInstance(
      t: Topology,
      mapEntry: (InterfaceInstance, Location)
    ) = {
      val (instance, loc) = mapEntry
      t.addInstance(instance, loc)
    }
    def importInstances(into: Topology, fromSymbol: Symbol.Topology) =
      a.topologyMap(fromSymbol).instanceMap.foldLeft (into) (importInstance)
    Right(t.directTopologies.keys.foldLeft (t) (importInstances))
  }

  /** Resolve this topology to a partially numbered topology */
  def resolve(a: Analysis, t: Topology): Result.Result[Topology] = {
    for {
      t <- Right(computeTransitiveImports(a, t))
      t <- resolveInstances(a, t)
      _ <- checkPortInstances(t)
      _ <- checkConnectionInstances(t)
      t <- resolveInterfacesToComponentInstances(a, t)
      t <- resolveImportedConnections(a, t)
      t <- resolvePatterns(a, t)
    }
    yield t
  }

}
