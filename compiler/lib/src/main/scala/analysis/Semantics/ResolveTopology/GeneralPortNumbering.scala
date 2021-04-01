package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Apply general port numbering */
object GeneralPortNumbering {

  // Number an input port array
  def numberInputPortArray(
    t: Topology,
    pii: PortInstanceIdentifier
  ): Topology = {
    val pi = pii.portInstance
    t.getConnectionsTo(pii).foldLeft (t) ((t, c) =>
      t.getPortNumber(pi, c) match {
        case Some(n) => t
        case None => t.assignPortNumber(pi, c, 0)
      }
    )
  }

  // Number an output port array
  def numberOutputPortArray(
    t: Topology,
    pii: PortInstanceIdentifier
  ): Topology = {
    val pi = pii.portInstance
    val cs = t.getConnectionsFrom(pii)
    val usedPortNumbers = t.getUsedPortNumbers(pi, cs)
    val state = PortNumberingState.initial(usedPortNumbers)
    val (_, t1) = cs.foldLeft ((state, t)) ({ case ((s,t), c) =>
      t.getPortNumber(pi, c) match {
        case Some(n) => (s, t)
        case None => {
          val (s1, n) = state.getPortNumber
          val t1 = t.assignPortNumber(pi, c, n)
          (s1, t1)
        }
      }
    })
    t1
  }

  /** Apply general numbering */
  def apply(t: Topology): Topology = {
    // Fold over instances and ports
    t.instanceMap.keys.foldLeft (t) ((t, ci) =>
      ci.component.portMap.values.foldLeft (t) ((u, pi) => {
        import PortInstance.Direction._
        val pii = PortInstanceIdentifier(ci, pi)
        pi.getDirection match {
          case Some(Input) => numberInputPortArray(t, pii)
          case Some(Output) => numberOutputPortArray(t, pii)
          case None => t
        }
      })
    )
  }

}
