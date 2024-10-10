package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Port numbering state */
case class PortNumberingState private (
  /** First set of used ports */
  usedPorts1: Map[Int, Connection],
  /** Second set of used ports */
  usedPorts2: Map[Int, Connection],
  /** The used port numbers */
  usedPortNumbers: Set[Int],
  /** The next port number */
  nextPortNumber: Int
) {

  /** Marks the next port number as used and generates
   *  a new one */
  def usePortNumber: PortNumberingState = {
    val s = usedPortNumbers + nextPortNumber
    val n = PortNumberingState.getNextNumber(
      nextPortNumber,
      s
    )
    PortNumberingState(usedPorts1, usedPorts2, s, n)
  }

  /** Gets the next port number and updates the state */
  def getPortNumber: (PortNumberingState, Int) = {
    val s = usePortNumber
    (s, nextPortNumber)
  }

  // Takes in the updated sets, updated the usedPortNumbers set 
  // (ie: union of usedPorts1 and usedPorts2) and figure out the new next port number 
  def setUsedPorts(u1: Map[Int, Connection], u2: Map[Int, Connection]): PortNumberingState = {
    val updatedUsedPortNumbers = Set(u1.keys.toList:_*) ++ Set(u2.keys.toList:_*)
    val updatedNextPortNumber = PortNumberingState.getNextNumber(
      nextPortNumber,
      updatedUsedPortNumbers
    )
    PortNumberingState(u1, u2, updatedUsedPortNumbers, updatedNextPortNumber)
  }

}

object PortNumberingState {

  /** Construct an initial state */
  def initial(usedPorts1: Map[Int, Connection], usedPorts2: Map[Int, Connection]): PortNumberingState = {
    val usedPortNumbers = Set(usedPorts1.keys.toList:_*) ++ Set(usedPorts2.keys.toList:_*)
    val nextPortNumber = getNextNumber(0, usedPortNumbers)
    PortNumberingState(usedPorts1, usedPorts2, usedPortNumbers, nextPortNumber)
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
