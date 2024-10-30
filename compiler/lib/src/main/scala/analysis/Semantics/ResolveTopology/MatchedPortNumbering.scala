package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Apply matched port numbering */
object MatchedPortNumbering {

  // A map from component instances to connections for tracking
  // matching pairs of connections
  private type InstanceConnectionMap = Map[ComponentInstance, Connection]

  // A map from port numbers to connections for tracking port
  // assignments
  private type PortConnectionMap = Map[Int, Connection]

  // State for matched port numbering
  private case class State private(
    // The topology
    t: Topology,
    // The port instance for port 1
    pi1: PortInstance,
    // The map from component instances to connections for port 1
    icm1: InstanceConnectionMap,
    // The map from port numbers to connections for port 1
    pcm1: PortConnectionMap,
    // The port instance for port 2
    pi2: PortInstance,
    // The map from component instances to connections for port 2
    icm2: InstanceConnectionMap,
    // The map from port numbers to connections for port 2
    pcm2: PortConnectionMap,
    // Port numbering state
    numbering: PortNumberingState
  ) {

    // Marks the specified port number as used and generates a new one
    def usePortNumber(n: Int): State =
      this.copy(numbering = numbering.usePortNumber(n))

    // Marks the next port number as used and generates a new one
    def useNextPortNumber: State =
      this.copy(numbering = numbering.useNextPortNumber)

    // Gets the next port number and updates the port numbring state
    def getPortNumber: (State, Int) = {
      val (s, n) = numbering.getPortNumber
      (this.copy(numbering = s), n)
    }

    // Adds a mapping to pcm1 and updates the port numbering state
    def updatePortConnectionMap1(n: Int, c: Connection): State =
      usePortNumber(n).copy(pcm1 = this.pcm1 + (n -> c))

    // Adds a mapping to pcm2 and updates the port numbering state
    def updatePortConnectionMap2(n: Int, c: Connection): State =
      usePortNumber(n).copy(pcm2 = this.pcm2 + (n -> c))

  }

  private object State {

    // Number a connection pair
    private def numberConnectionPair(
      matchingLoc: Location,
      state: State,
      c1: Connection,
      c2: Connection
    ): Result.Result[State] = {
      val t = state.t
      val pi1 = state.pi1
      val n1Opt = t.getPortNumber(pi1, c1)
      val pi2 = state.pi2
      val n2Opt = t.getPortNumber(pi2, c2)
      (n1Opt, n2Opt) match {
        case (Some(n1), Some(n2)) =>
          // Both ports have a number
          if (n1 == n2)
            // Numbers match: OK, nothing to do
            Right(state)
          else {
            // Numbers don't match: error
            val p1Loc = c1.getThisEndpoint(pi1).loc
            val p2Loc = c2.getThisEndpoint(pi2).loc
            Left(
              SemanticError.MismatchedPortNumbers(
                p1Loc,
                n1,
                p2Loc,
                n2,
                matchingLoc
              )
            )
          }
        case (Some(n), None) =>
          // Only pi1 has a number
          state.pcm2.get(n) match {
            case Some(prevC) =>
              // Number is already assigned at pi2: error
              Left(
                SemanticError.ImplicitDuplicateConnectionAtMatchedPort(
                  c2.getLoc,
                  pi2.toString,
                  n,
                  c1.getLoc,
                  matchingLoc,
                  prevC.getLoc,
                )
              )
            case None =>
              // Assign the number to c2 at pi2 and update the state
              val t1 = t.assignPortNumber(pi2, c2, n)
              val state1 = state.updatePortConnectionMap2(n, c2)
              Right(state1.copy(t = t1))
          }
        case (None, Some(n)) =>
          // Only pi2 has a number
          state.pcm1.get(n) match {
            case Some(prevC) =>
              // Number is already assigned at pi1: error
              Left(
                SemanticError.ImplicitDuplicateConnectionAtMatchedPort(
                  c1.getLoc,
                  pi1.toString,
                  n,
                  c2.getLoc,
                  matchingLoc,
                  prevC.getLoc
                )
              )
            case None =>
              // Assign the number to c1 at pi1 and update the state
              val t1 = t.assignPortNumber(pi1, c1, n)
              val state1 = state.updatePortConnectionMap1(n, c1)
              Right(state1.copy(t = t1))
          }
        case (None, None) =>
          // Neither port has a number; get a new one
          val (state1, n) = state.getPortNumber
          if(n >= pi1.getArraySize)
            // Port number is out of range: error
            Left(
              SemanticError.NoPortAvailableForMatchedNumbering(
                c1.getLoc,
                c2.getLoc,
                matchingLoc
              )
            )
          else {
            // Assign the number to both sides and update the state
            val t1 = t.assignPortNumber(pi1, c1, n).assignPortNumber(pi2, c2, n)
            val state2 = state1.updatePortConnectionMap1(n, c1).
              updatePortConnectionMap2(n, c2)
            Right(state2.copy(t = t1))
          }
      }
    }

    // For each pair of connections (c1, c2), check that numbers
    // match and/or assign numbers
    def assignNumbers(
      matchingLoc: Location,
      state: State
    ): Result.Result[State] = {
      val (icm1, icm2) = (state.icm1, state.icm2)
      val list1 = icm1.toList.sortWith(_._2 < _._2)
      for {
        result <- Result.foldLeft (list1) (state) ({
          case (s, (ci, c1)) => {
            val c2 = icm2(ci)
            numberConnectionPair(matchingLoc, s, c1, c2)
          }
        })
      }
      yield result
    }

    def initial(
      t: Topology,
      pi1: PortInstance,
      icm1: InstanceConnectionMap,
      pcm1: PortConnectionMap,
      pi2: PortInstance,
      icm2: InstanceConnectionMap,
      pcm2: PortConnectionMap
    ): State = {
      // Compute the used port numbers
      val usedPortNumbers = pcm1.keys.toSet ++ pcm2.keys.toSet
      State(
        t,
        pi1,
        icm1,
        pcm1,
        pi2,
        icm2,
        pcm2,
        PortNumberingState.initial(usedPortNumbers)
      )
    }

  }

  /** Apply matched numbering */
  def apply(t: Topology): Result.Result[Topology] = {
    // Fold over instances and matchings
    Result.foldLeft (t.instanceMap.keys.toList) (t) ((t, ci) =>
      Result.foldLeft (ci.component.portMatchingList) (t) ((t, pm) =>
        handlePortMatching(t, ci, pm)
      )
    )
  }

  // Check for missing connections
  private def checkForMissingConnections(
    matchingLoc: Location,
    icm1: InstanceConnectionMap,
    icm2: InstanceConnectionMap
  ): Result.Result[Unit] = {
    // Ensure that icm2 contains everything in icm1
    def helper(icm1: InstanceConnectionMap, icm2: InstanceConnectionMap) =
      Result.foldLeft (icm1.toList) (()) ({ case (u, (ci, c)) =>
        if (icm2.contains(ci))
          Right(())
        else {
          val loc = c.getLoc
          Left(SemanticError.MissingConnection(loc, matchingLoc))
        }
      })
    // Ensure that the two sets of keys match
    if (icm1.size >= icm2.size)
      helper(icm1, icm2)
    else
      helper(icm2, icm1)
  }

  // Handle one port matching
  private def handlePortMatching(
    t: Topology,
    ci: ComponentInstance,
    portMatching: Component.PortMatching
  ) = {
    // Map remote component instances to connections at pi
    def computeInstanceConnectionMap(pi: PortInstance): Result.Result[InstanceConnectionMap] = {
      val empty: InstanceConnectionMap = Map()
      val pii = PortInstanceIdentifier(ci, pi)
      val cs = t.getConnectionsAt(pii).toList.sorted
      Result.foldLeft (cs) (empty) ((m, c) => {
        if(c.isUnmatched)
          Right(m)
        else {
          val piiRemote = c.getOtherEndpoint(pi).port
          val ciRemote = piiRemote.componentInstance
          m.get(ciRemote) match {
            case Some(cPrev) => Left(
              SemanticError.DuplicateMatchedConnection(
                c.getLoc,
                cPrev.getLoc,
                portMatching.getLoc
              )
            )
            case None => Right(m + (ciRemote -> c))
          }
        }
      })
    }

    // Map port numbers to connections at pi
    // While computing the map, enforce the rule against duplicate connections
    def computePortConnectionMap(pi: PortInstance): Result.Result[PortConnectionMap] = {
      val pii = PortInstanceIdentifier(ci, pi)
      val cs = t.getConnectionsAt(pii).toList.sorted
      val empty: PortConnectionMap = Map()
      Result.foldLeft (cs) (empty) ((m, c) => {
        val piiRemote = c.getOtherEndpoint(pi).port
        t.getPortNumber(pi, c) match {
            case Some(n) =>
              m.get(n) match {
                case Some(prevC) =>
                  val loc = c.getLoc
                  val prevLoc = prevC.getLoc
                  Left(
                    SemanticError.DuplicateConnectionAtMatchedPort(
                      loc,
                      pi.toString,
                      n,
                      prevLoc,
                      portMatching.getLoc
                    )
                  )
                case None => Right(m + (n -> c))
              }
            case None => Right(m)
          }
      })
    }

    val pi1 = portMatching.instance1
    val pi2 = portMatching.instance2
    val loc = portMatching.getLoc
    for {
      pcm1 <- computePortConnectionMap(pi1)
      pcm2 <- computePortConnectionMap(pi2)
      icm1 <- computeInstanceConnectionMap(pi1)
      icm2 <- computeInstanceConnectionMap(pi2)
      _ <- checkForMissingConnections(loc, icm1, icm2)
      state <- {
        val state = State.initial(t, pi1, icm1, pcm1, pi2, icm2, pcm2)
        State.assignNumbers(loc, state)
      }
    }
    yield state.t
  }

}
