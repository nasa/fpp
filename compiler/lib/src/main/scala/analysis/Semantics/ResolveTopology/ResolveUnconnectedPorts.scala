package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve unconnected ports */
object ResolveUnconnectedPorts {

  /** Compute the unconnected ports of t */
  def resolve(t: Topology): Topology = {
    // Fold over instances and ports
    t.instanceMap.keys.foldLeft (t) ((t1, ci) =>
      ci.component.portMap.values.foldLeft (t1) ((t2, pi) => {
        val pii = PortInstanceIdentifier(ci, pi)
        if (t2.getConnectionsAt(pii).size == 0)
          t2.copy(unconnectedPortSet = t2.unconnectedPortSet + pii)
        else
          t2
      })
    )
  }

}
