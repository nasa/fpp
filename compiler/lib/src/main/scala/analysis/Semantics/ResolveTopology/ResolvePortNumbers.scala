package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve port numbers */
object ResolvePortNumbers {

  /** Check output ports for t */
  private def checkOutputPorts(t: Topology): Result.Result[Unit] =
    Result.foldLeft (t.outputConnectionMap.toList) (()) ({
      case (_, (pii, s)) => for {
        _ <- checkOutputSizeBounds(pii, s)
        _ <- checkDuplicateOutputPorts(s)
      }
      yield ()
    })

  /** Check that there are no duplicate port numbers at any output
   *  ports. */
  private def checkDuplicateOutputPorts(
    connections: Set[Connection]
  ): Result.Result[Unit] = {
    val portNumMap: Map[Int, Connection] = Map()
    for {
      _ <- Result.foldLeft (connections.toList) (portNumMap) ((m, c) =>
          c.from.portNumber match {
            case Some(portNum) => {
              m.get(portNum) match {
                case Some(prevC) => 
                  val loc = c.from.loc
                  val prevLoc = prevC.from.loc
                  Left(
                    SemanticError.DuplicateOutputPort(loc, portNum, prevLoc)
                  )
                case None => Right(m + (portNum -> c))
              }
            }
            case None => Right(m)
          }
      )
    }
    yield ()
  }

  /** Check the bounds on the number of output connections */
  private def checkOutputSizeBounds(
    pii: PortInstanceIdentifier,
    connections: Set[Connection]
  ): Result.Result[Unit] = {
    val pi = pii.portInstance
    val arraySize = pi.getArraySize
    val numPorts = connections.size
    if (numPorts <= arraySize)
      Right(())
    else {
      val loc = pi.getLoc
      val instanceLoc = pii.componentInstance.getLoc
      Left(
        SemanticError.TooManyOutputPorts(
          loc,
          numPorts,
          arraySize,
          instanceLoc
        )
      )
    }
  }

  /** Fill in the port numbers for this topology */
  def resolve(t: Topology): Result.Result[Topology] =
    for {
      _ <- checkOutputPorts(t)
      t <- MatchedPortNumbering.apply(t)
      t <- Right(GeneralPortNumbering.apply(t))
    }
    yield t

}
