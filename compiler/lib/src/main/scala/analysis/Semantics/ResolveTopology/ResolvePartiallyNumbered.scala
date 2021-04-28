package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve a partially numbered topology */
object ResolvePartiallyNumbered {

  /** Check that connection instances are legal */
  private def checkConnectionInstances(t: Topology): Result.Result[Unit] = {
    def checkConnection(c: Connection) = {
      val fromInstance = c.from.port.componentInstance
      val toInstance = c.to.port.componentInstance
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

  /** Check the instances of a pattern */
  private def checkPatternInstances(t: Topology, pattern: ConnectionPattern) =
    for {
      // Check the source
      _ <- {
        val (instance, loc) = pattern.source
        t.lookUpInstanceAt(instance, loc)
      }
      // Check the targets
      _ <- Result.map(
        pattern.targets.toList,
        (pair: (ComponentInstance, Location)) => {
          val (instance, loc) = pair
          t.lookUpInstanceAt(instance, loc)
        }
      )
    }
    yield ()

  /** Compute the transitively imported topologies */
  private def computeTransitiveImports(a: Analysis, t: Topology) = {
    val tis = t.directImportMap.keys.foldLeft (Set[Symbol.Topology]()) ((tis, ts) => {
      val t = a.topologyMap(ts)
      tis.union(t.transitiveImportSet) + ts
    })
    t.copy(transitiveImportSet = tis)
  }

  /** Resolve the connection patterns of t  */
  private def resolvePatterns(a: Analysis, t: Topology): Result.Result[Topology] = {
    import Ast.SpecConnectionGraph._
    Result.foldLeft (t.patternMap.values.toList) (t) ((t, p) => {
      val instances = t.instanceMap.keys
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
      val instance = endpoint.port.componentInstance
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

  /** Resolve the instances of t */
  private def resolveInstances(a: Analysis, t: Topology): Result.Result[Topology] = {
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
    Right(t.directImportMap.keys.foldLeft (t) (importInstances))
  }

  /** Resolve this topology to a partially numbered topology */
  def resolve(a: Analysis, t: Topology): Result.Result[Topology] = {
    for {
      t <- Right(computeTransitiveImports(a, t))
      t <- resolveInstances(a, t)
      _ <- checkConnectionInstances(t)
      t <- resolveImportedConnections(a, t)
      t <- resolvePatterns(a, t)
    }
    yield t
  }

}
