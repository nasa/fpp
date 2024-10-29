package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Matched port numbering state */
case class MatchedPortNumberingState private (
  /** The port numbering state */
  usedPortNumbers: Set[Int],
  /** The next port number */
  nextPortNumber: Int,
  /** Map from port numbers to connections for port instance 1 */
  usedPorts1: MatchedPortNumberingState.UsedPortMap = Map(),
  /** Map from port numbers to connections for port instance 2 */
  usedPorts2: MatchedPortNumberingState.UsedPortMap = Map()
) {

  /** Marks the next port number as used and generates
   *  a new one */
  def usePortNumber: MatchedPortNumberingState = {
    val s = usedPortNumbers + nextPortNumber
    val n = PortNumberingState.getNextNumber(
      nextPortNumber,
      s
    )
    MatchedPortNumberingState(s, n, usedPorts1, usedPorts2)
  }

  /** Gets the next port number and updates the state */
  def getPortNumber: (MatchedPortNumberingState, Int) = {
    val s = usePortNumber
    (s, nextPortNumber)
  }

  // Takes in the updated sets, updated the usedPortNumbers set
  // (ie: union of usedPorts1 and usedPorts2) and figure out the new next port number
  def setUsedPorts(
    u1: MatchedPortNumberingState.UsedPortMap,
    u2: MatchedPortNumberingState.UsedPortMap
  ): MatchedPortNumberingState = {
    val updatedUsedPortNumbers = u1.keys.toSet ++ u2.keys.toSet
    val updatedNextPortNumber = PortNumberingState.getNextNumber(
      nextPortNumber,
      updatedUsedPortNumbers
    )
    MatchedPortNumberingState(updatedUsedPortNumbers, updatedNextPortNumber, u1, u2)
  }

}

object MatchedPortNumberingState {

  /** A mapping from used port numbers to connections **/
  type UsedPortMap = Map[Int, Connection]

  /** Construct an initial state */
  def initial(upm1: UsedPortMap, upm2: UsedPortMap): MatchedPortNumberingState = {
    val usedPortNumbers = upm1.keys.toSet ++ upm2.keys.toSet
    val nextPortNumber = PortNumberingState.getNextNumber(0, usedPortNumbers)
    MatchedPortNumberingState(usedPortNumbers, nextPortNumber, upm1, upm2)
  }

}
