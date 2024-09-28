package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Port numbering state */
case class PortNumberingState private (
  /** First set of used ports */
  usedPorts1: Set[Int],
  /** Second set of used ports */
  usedPorts2: Set[Int],
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
  def setUsedPorts(u1: Set[Int], u2: Set[Int]): PortNumberingState = {
    val updatedUsedPortNumbers = u1 ++ u2
    val updatedNextPortNumber = PortNumberingState.getNextNumber(
      nextPortNumber,
      updatedUsedPortNumbers
    )
    PortNumberingState(u1, u2, updatedUsedPortNumbers, updatedNextPortNumber)
  }

}

object PortNumberingState {

  /** Construct an initial state */
  def initial(usedPorts1: Set[Int], usedPorts2: Set[Int]): PortNumberingState = {
    val usedPortNumbers = usedPorts1 ++ usedPorts2
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

  // Checks to see if a port number is already in use
  def checkPortNumberInUse(n: Int, used: Set[Int]) =
    used.contains(n)

}
