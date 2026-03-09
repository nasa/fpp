package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object ResolveTopologyPortInterface {

  /** Resolve a topology's port interface */
  def resolve(a: Analysis, t: Topology): Result.Result[Topology] =
    Result.foldLeft (t.ports) (t) ((t, aNode) => {
      val (_, node, _) = aNode
      val topPort = node.data

      for {
        instance <- PortInstanceIdentifier.fromNode(a, topPort.underlyingPort)
        t <- t.addPort(
          aNode,
          instance,
          Locations.get(node.id)
        )
      } yield t
    })

}
