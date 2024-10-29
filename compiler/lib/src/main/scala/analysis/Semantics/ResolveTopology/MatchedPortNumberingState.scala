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

  /** Marks the specified port number as used and generates
   *  a new one */
  def usePortNumber(n: Int): MatchedPortNumberingState = {
    val s = usedPortNumbers + n
    val n1 = PortNumberingState.getNextNumber(
      nextPortNumber,
      s
    )
    MatchedPortNumberingState(s, n1, usedPorts1, usedPorts2)
  }

  /** Marks the next port number as used and generates
   *  a new one */
  def useNextPortNumber: MatchedPortNumberingState =
    usePortNumber(nextPortNumber)

  /** Gets the next port number and updates the state */
  def getPortNumber: (MatchedPortNumberingState, Int) = {
    val s = useNextPortNumber
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

  /** Adds a mapping to usedPorts1 */
  def updateUsedPorts1(n: Int, c: Connection): MatchedPortNumberingState =
    usePortNumber(n).copy(usedPorts1 = this.usedPorts1 + (n -> c))

//  {
//    val usedPortNumbers = this.usedPortNumbers + n
//    val nextPortNumber = PortNumberingState.getNextNumber(
//      this.nextPortNumber,
//      usedPortNumbers
//    )
//    val usedPorts1 = this.usedPorts1 + (n -> c)
//    this.copy(
//      usedPortNumbers = usedPortNumbers,
//      nextPortNumber = nextPortNumber,
//      usedPorts1 = usedPorts1
//    )
//  }

  /** Adds a mapping to usedPorts2 */
  def updateUsedPorts2(n: Int, c: Connection): MatchedPortNumberingState = {
    val usedPortNumbers = this.usedPortNumbers + n
    val nextPortNumber = PortNumberingState.getNextNumber(
      this.nextPortNumber,
      usedPortNumbers
    )
    val usedPorts2 = this.usedPorts2 + (n -> c)
    this.copy(
      usedPortNumbers = usedPortNumbers,
      nextPortNumber = nextPortNumber,
      usedPorts2 = usedPorts2
    )
  }

}

object MatchedPortNumberingState {

  /** A mapping from used port numbers to connections **/
  type UsedPortMap = Map[Int, Connection]

  /** Construct an initial state */
  def initial(map1: UsedPortMap, map2: UsedPortMap): MatchedPortNumberingState = {
    val usedPortNumbers = map1.keys.toSet ++ map2.keys.toSet
    val nextPortNumber = PortNumberingState.getNextNumber(0, usedPortNumbers)
    MatchedPortNumberingState(usedPortNumbers, nextPortNumber, map1, map2)
  }

}
