package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Matched port numbering state */
case class MatchedPortNumberingState private (
  /** The port numbering state */
  portNumberingState: PortNumberingState,
  /** The used port numbers */
  usedPortNumbers: Set[Int],
  /** The next port number */
  nextPortNumber: Int,
  /** Map from port numbers to connections for port instance 1 */
  usedPorts1: MatchedPortNumberingState.UsedPortMap,
  /** Map from port numbers to connections for port instance 2 */
  usedPorts2: MatchedPortNumberingState.UsedPortMap
) {

  /** Marks the specified port number as used and generates
   *  a new one */
  def usePortNumber(n: Int): MatchedPortNumberingState = {
    MatchedPortNumberingState(
      portNumberingState.usePortNumber(n),
      Set(), 0, usedPorts1, usedPorts2
    )
  }

  /** Marks the next port number as used and generates
   *  a new one */
  def useNextPortNumber: MatchedPortNumberingState =
    this.copy(portNumberingState = portNumberingState.useNextPortNumber)

  /** Gets the next port number and updates the state */
  def getPortNumber: (MatchedPortNumberingState, Int) = {
    val (pns, pn) = portNumberingState.getPortNumber
    (this.copy(portNumberingState = pns), pn)
  }

  /** Adds a mapping to usedPorts1 */
  def updateUsedPorts1(n: Int, c: Connection): MatchedPortNumberingState =
    usePortNumber(n).copy(usedPorts1 = this.usedPorts1 + (n -> c))

  /** Adds a mapping to usedPorts2 */
  def updateUsedPorts2(n: Int, c: Connection): MatchedPortNumberingState =
    usePortNumber(n).copy(usedPorts2 = this.usedPorts2 + (n -> c))

}

object MatchedPortNumberingState {

  /** A mapping from used port numbers to connections **/
  type UsedPortMap = Map[Int, Connection]

  /** Construct an initial state */
  def initial(map1: UsedPortMap, map2: UsedPortMap): MatchedPortNumberingState = {
    val usedPortNumbers = map1.keys.toSet ++ map2.keys.toSet
    val portNumberingState = PortNumberingState.initial(usedPortNumbers)
    //val nextPortNumber = PortNumberingState.getNextNumber(0, usedPortNumbers)
    MatchedPortNumberingState(portNumberingState, Set(), 0, map1, map2)
  }

}
