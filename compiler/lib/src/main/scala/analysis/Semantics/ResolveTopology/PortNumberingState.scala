package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Port numbering state */
case class PortNumberingState private (
  /** The used port numbers */
  usedPortNumbers: Set[Int],
  /** The next port number */
  nextPortNumber: Int,
  /** First map of used ports for port instance 1 */
  usedPorts1: Map[Int, Connection] = Map(),
  /** Second map of used ports for port instance 2 */
  usedPorts2: Map[Int, Connection] = Map()
) {

  /** Marks the next port number as used and generates
   *  a new one */
  def usePortNumber: PortNumberingState = {
    val s = usedPortNumbers + nextPortNumber
    val n = PortNumberingState.getNextNumber(
      nextPortNumber,
      s
    )
    PortNumberingState(s, n, usedPorts1, usedPorts2)
  }

  /** Gets the next port number and updates the state */
  def getPortNumber: (PortNumberingState, Int) = {
    val s = usePortNumber
    (s, nextPortNumber)
  }

  // Takes in the updated sets, updated the usedPortNumbers set
  // (ie: union of usedPorts1 and usedPorts2) and figure out the new next port number
  def setUsedPorts(u1: Map[Int, Connection], u2: Map[Int, Connection]): PortNumberingState = {
    val updatedUsedPortNumbers = u1.keys.toSet ++ u2.keys.toSet
    val updatedNextPortNumber = PortNumberingState.getNextNumber(
      nextPortNumber,
      updatedUsedPortNumbers
    )
    PortNumberingState(updatedUsedPortNumbers, updatedNextPortNumber, u1, u2)
  }

}

object PortNumberingState {

  /** Construct an initial state */
  def initial(
    usedPortNumbers: Set[Int],
    usedPorts1: Map[Int, Connection] = Map(),
    usedPorts2: Map[Int, Connection] = Map()
  ): PortNumberingState = {
    val nextPortNumber = getNextNumber(0, usedPortNumbers)
    PortNumberingState(usedPortNumbers, nextPortNumber, usedPorts1, usedPorts2)
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
