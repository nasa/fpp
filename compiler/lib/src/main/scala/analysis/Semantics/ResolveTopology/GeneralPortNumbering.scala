package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Apply general port numbering */
object GeneralPortNumbering {

  // Number an input port array
  private def numberInputPortArray(
    t: Topology,
    pii: PortInstanceIdentifier
  ): Topology = {
    val pi = pii.portInstance
    t.getConnectionsTo(pii).toArray.sorted.foldLeft (t) ((t, c) =>
      t.getPortNumber(pi, c) match {
        case Some(n) => t
        case None => t.assignPortNumber(pi, c, 0)
      }
    )
  }

  // Number an output port array
  private def numberOutputPortArray(
    t: Topology,
    pii: PortInstanceIdentifier
  ): Topology = {
    val pi = pii.portInstance
    val cs = t.getConnectionsFrom(pii).toList.sorted
    val usedPortNumbers = 
      cs.foldLeft (Map[Int, Connection]()) ((m, c) =>
        t.getPortNumber(pi, c) match {
          case Some(n) => m + (n -> c)
          case None => m
        }
      )
    // Initialize the PortNumberingState with an empty map as one 
    // of its args since in GeneralPortNumbering we are only 
    // working with one map of port numbers to connections at a time
    val state = PortNumberingState.initial(Map(), usedPortNumbers)
    val (_, t1) = cs.foldLeft ((state, t)) ({ case ((s,t), c) =>
      t.getPortNumber(pi, c) match {
        case Some(n) => (s, t)
        case None => {
          val (s1, n) = s.getPortNumber
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
    t.instanceMap.keys.foldLeft (t) ((t1, ci) =>
      ci.component.portMap.values.foldLeft (t1) ((t2, pi) => {
        import PortInstance.Direction._
        val pii = PortInstanceIdentifier(ci, pi)
        pi.getDirection match {
          case Some(Input) => numberInputPortArray(t2, pii)
          case Some(Output) => numberOutputPortArray(t2, pii)
          case None => t2
        }
      })
    )
  }

}
