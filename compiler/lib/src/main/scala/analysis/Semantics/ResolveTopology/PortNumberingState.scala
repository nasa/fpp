package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Port numbering state */
case class PortNumberingState private (
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
    PortNumberingState(s, n)
  }

  /** Gets the next port number and updates the state */
  def getPortNumber: (PortNumberingState, Int) = {
    val s = usePortNumber
    (s, nextPortNumber)
  }

}

object PortNumberingState {

  /** Construct an initial state */
  def initial(usedPortNumbers: Set[Int]): PortNumberingState = {
    val nextPortNumber = getNextNumber(0, usedPortNumbers)
    PortNumberingState(usedPortNumbers, nextPortNumber)
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
