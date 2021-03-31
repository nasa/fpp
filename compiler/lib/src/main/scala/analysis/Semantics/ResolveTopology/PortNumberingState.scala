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

  /** Gets the next available port number */
  def getNextNumber(from: Int, used: Set[Int]): Int = {
    def helper(n: Int): Int = 
      if (!used.contains(n))
        n
      else helper(n + 1)
    helper(from)
  }

}
