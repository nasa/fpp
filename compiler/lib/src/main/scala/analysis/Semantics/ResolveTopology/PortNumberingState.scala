package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Port numbering state */
case class PortNumberingState private (
  usedPorts1: Set[Int],
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

  def setUsedPorts(u1: Set[Int], u2: Set[Int]): PortNumberingState = {
    val updatedUsedPortNumbers = u1 ++ u2
    val updatedNextPortNumber = PortNumberingState.getNextNumber(
      nextPortNumber,
      updatedUsedPortNumbers
    )
    PortNumberingState(u1, u2, updatedUsedPortNumbers, nextPortNumber)
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

  def checkPortNumberInUse(n: Int, used: Set[Int]) =
    used.contains(n)

}
