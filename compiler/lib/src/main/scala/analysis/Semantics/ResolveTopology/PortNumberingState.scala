package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Port numbering state */
case class PortNumberingState(
  /** The used port numbers */
  usedPortNumbers: Set[Int],
  /** The next port number */
  nextPortNumber: Int
) 

object PortNumberingState {

  /** Assigns a port number to a connection at a port instance */
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

  /** Gets the next available port number */
  def getNextNumber(from: Int, used: Set[Int]): Int = {
    def helper(n: Int): Int = 
      if (!used.contains(n))
        n
      else helper(n + 1)
    helper(from)
  }

}
