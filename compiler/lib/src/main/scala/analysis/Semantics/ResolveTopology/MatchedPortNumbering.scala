package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Apply matched port numbering */
object MatchedPortNumbering {

  // A mapping from component instances to connections
  private type ConnectionMap = Map[ComponentInstance, Connection]

  // State for numbering ports
  private case class State private(
    // The topology
    t: Topology,
    // The port instance for port 1
    pi1: PortInstance,
    // The map from component instances to connections for port 1
    map1: ConnectionMap,
    // The port instance for port 2
    pi2: PortInstance,
    // The map from component instances to connections for port 2
    map2: ConnectionMap,
    // Numbering state
    numberingState: PortNumberingState
  )

  private object State {

    // Number a connection pair
    def numberConnectionPair(
      state: State,
      c1: Connection,
      c2: Connection
    ) = {
      // TODO
      // * If c1 and c2 both have numbers, check that they match
      // * Otherwise if c1 has a number then assign it to c2
      // * Otherwise if c2 has a number then assign it to c1
      // * Otherwise assign n, add n to S, and increment n
      //   until it is not in S.
      Right(state)
    }

    // For each pair of connections (c1, c2), check that numbers 
    // match and/or assign numbers
    def assignNumbers(state: State): Result.Result[State] = {
      val (map1, map2) = (state.map1, state.map2)
      // Fold over map1
      for {
        result <- Result.foldLeft (map1.toList) (state) ( { 
          case (state, (ci, c1)) => {
            val c2 = map2(ci)
            numberConnectionPair(state, c1, c2)
          }
        })
      }
      yield result
    }

    // Get the port number of a connection at a port instance
    def getPortNumber(t: Topology, pi: PortInstance, c: Connection) = {
      import PortInstance.Direction._
      pi.getDirection.get match {
        case Input => t.toPortNumberMap(c)
        case Output => t.fromPortNumberMap(c)
      }
    }

    def initial(
      t: Topology,
      pi1: PortInstance,
      map1: ConnectionMap,
      pi2: PortInstance,
      map2: ConnectionMap
    ) = {
      // Compute the set of used port numbers in map1
      val usedPortNumbers = map1.values.foldLeft (Set[Int]()) ((s, c) =>
        s + getPortNumber(t, pi1, c)
      )
      // Set the next number n to the smallest number not in S
      val nextPortNumber = PortNumberingState.getNextNumber(0, usedPortNumbers)
      State(
        t,
        pi1,
        map1,
        pi2,
        map2,
        PortNumberingState(usedPortNumbers, nextPortNumber)
      )
    }

  }

  /** Apply matched numbering */
  def apply(t: Topology): Result.Result[Topology] = {
    // Fold over instances and matchings
    Result.foldLeft (t.instanceMap.keys.toList) (t) ((t, ci) =>
      Result.foldLeft (ci.component.portMatchingList) (t) ((u, pm) =>
        handlePortMatching(t, ci, pm)
      )
    )
  }

  // Check for missing connections
  private def checkForMissingConnections(
    map1: ConnectionMap,
    map2: ConnectionMap
  ): Result.Result[Unit] = {
    // Ensure that map2 contains everything in map1
    def helper(map1: ConnectionMap, map2: ConnectionMap) =
      Result.foldLeft (map1.keys.toList) (()) ((u, ci) =>
        if (map2.contains(ci))
          Right(())
        else
          ??? // Missing connection
      )
    // Ensure that the two sets of keys match
    if (map1.size >= map2.size)
      helper(map1, map2)
    else
      helper(map2, map1)
  }

  // Handle one port matching
  private def handlePortMatching(
    t: Topology,
    ci: ComponentInstance,
    portMatching: Component.PortMatching
  ) = {
    // Map remote components to connections at pi
    def constructMap(pi: PortInstance) = {
      val empty: ConnectionMap = Map()
      val pii = PortInstanceIdentifier(ci, pi)
      val cs = t.getConnectionsAt(pii).toList.sorted
      Result.foldLeft (cs) (empty) ((m, c) => {
        val piiRemote = pii.getOtherEndpoint(c).port
        val ciRemote = piiRemote.componentInstance
        m.get(ciRemote) match {
          case Some(cPrev) => ??? // Duplicate connection
          case None => Right(m + (ciRemote -> c))
        }
      })
    }
    val pi1 = portMatching.instance1
    val pi2 = portMatching.instance2
    for {
      map1 <- constructMap(pi1)
      map2 <- constructMap(pi2)
      _ <- checkForMissingConnections(map1, map2)
      state <- {
        val state = State.initial(t, pi1, map1, pi2, map2)
        State.assignNumbers(state)
      }
    }
    yield state.t
  }

}
