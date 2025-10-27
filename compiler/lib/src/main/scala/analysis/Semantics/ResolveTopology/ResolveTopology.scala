package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object ResolveTopology {

  /** Resolve a topology */
  def resolve(a: Analysis, t: Topology): Result.Result[Topology] =
    for {
      t <- ResolveTopologyInstances.resolve(a, t)
      t <- ResolveTopologyPortInterface.resolve(a, t)
      t <- ResolvePartiallyNumbered.resolve(a, t)
      t <- ResolvePortNumbers.resolve(t)
      t <- Right(ResolveUnconnectedPorts.resolve(t))

      // Check the topologies interface against the `implements` clause
      _ <- CheckTopologyInterface.check(a, t)
    }
    yield t

}
