package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve unconnected ports */
object ResolveUnconnectedPorts {

  /** Compute the unconnected ports of t */
  def resolve(t: Topology): Topology = {
    // Fold over instances and ports
    t.instanceMap.keys.foldLeft (t) ((t, ci) =>
      ci.component.portMap.values.foldLeft (t) ((u, pi) => {
        val pii = PortInstanceIdentifier(ci, pi)
        if (t.getConnectionsAt(pii).size == 0)
          t.copy(unconnectedPortSet = t.unconnectedPortSet + pii)
        else
          t
      })
    )
  }

}
