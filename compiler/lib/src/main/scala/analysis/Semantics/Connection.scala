package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP connection */
case class Connection(
  /** The from endpoint */
  from: Connection.Endpoint,
  /** The to endpoint */
  to: Connection.Endpoint,
  isUnmatched: Boolean = false
) extends Ordered[Connection] {

  override def toString = s"${from.toString} -> ${to.toString}"

  /** Checks the types of a connection */
  def checkTypes: Result.Result[Unit] = {
    val fromInstance = from.port.portInstance
    val fromType = fromInstance.getType
    val toInstance = to.port.portInstance
    val toType = toInstance.getType
    if (PortInstance.Type.areCompatible(fromType, toType))
      Right(())
    else {
      val fromTypeString = PortInstance.Type.show(fromType)
      val toTypeString = PortInstance.Type.show(toType)
      val msg = s"cannot connect port types $fromTypeString and $toTypeString"
      val fromLoc = fromInstance.getLoc
      val toLoc = toInstance.getLoc
      Left(SemanticError.InvalidConnection(getLoc, msg, fromLoc, toLoc))
    }
  }

  /** Checks the case of a serial port connected to a typed port,
   *  in either direction. The port type may not have a return type. */
  def checkSerialWithTypedInput: Result.Result[Unit] = {
    val fromInstance = from.port.portInstance
    val fromType = fromInstance.getType
    val toInstance = to.port.portInstance
    val toType = toInstance.getType
    (fromType, toType) match {
      case (
        Some(PortInstance.Type.Serial),
        Some(PortInstance.Type.DefPort(Symbol.Port(aNode)))
      ) =>
        aNode._2.data.returnType match {
          case Some(_) =>
            val toTypeString = PortInstance.Type.show(toType)
            val msg =
              s"cannot connect serial output port to input port of type $toTypeString, which returns a value"
            val fromLoc = fromInstance.getLoc
            val toLoc = toInstance.getLoc
            Left(SemanticError.InvalidConnection(
              getLoc,
              msg,
              fromLoc,
              toLoc,
              None,
              Some(Locations.get(aNode._2.id))
            ))
          case _ => Right(())
        }
      case (
        Some(PortInstance.Type.DefPort(Symbol.Port(aNode))),
        Some(PortInstance.Type.Serial)
      ) =>
        aNode._2.data.returnType match {
          case Some(_) =>
            val fromTypeString = PortInstance.Type.show(fromType)
            val msg =
              s"cannot connect output port of type $fromTypeString, which returns a value, to serial input port"
            val fromLoc = fromInstance.getLoc
            val toLoc = toInstance.getLoc
            Left(SemanticError.InvalidConnection(
              getLoc,
              msg,
              fromLoc,
              toLoc,
              Some(Locations.get(aNode._2.id)),
              None,
            ))
          case _ => Right(())
        }
      case _ => Right(())
    }
  }

  /** Checks the directions of a connection */
  def checkDirections: Result.Result[Unit] = {
    val fromInstance = from.port.portInstance
    val fromDirection = fromInstance.getDirection
    val toInstance = to.port.portInstance
    val toDirection = toInstance.getDirection
    if (PortInstance.Direction.areCompatible(fromDirection -> toDirection))
      Right(())
    else {
      val fromDirString = PortInstance.Direction.show(fromDirection)
      val toDirString = PortInstance.Direction.show(toDirection)
      val msg = s"invalid directions $fromDirString -> $toDirString (should be output -> input)"
      val fromLoc = fromInstance.getLoc
      val toLoc = toInstance.getLoc
      Left(SemanticError.InvalidConnection(getLoc, msg, fromLoc, toLoc))
    }
  }

  /** Checks whether a connection is match constrained */
  def isMatchConstrained: Boolean = {
    def portMatchingExists(pml: List[Component.PortMatching], pi: PortInstance): Boolean =
      pml.exists(pm => pi.equals(pm.instance1) || pi.equals(pm.instance2))

    from.port.interfaceInstance match {
      case InterfaceInstance.InterfaceComponentInstance(ci) => {
        val fromPi = from.port.portInstance
        val toPi = to.port.portInstance
        val fromPml = ci.component.portMatchingList
        val toPml = ci.component.portMatchingList

        portMatchingExists(fromPml, fromPi) || portMatchingExists(toPml, toPi)
      }

      case _ => false
    }
  }

  /** Compare two connections */
  override def compare(that: Connection) = {
    val fromCompare = this.from.compare(that.from)
    if (fromCompare != 0) fromCompare
    else this.to.compare(that.to)
  }

  /** Gets the location of the connection */
  def getLoc: Location = from.loc

  /** Get this endpoint of a port connection at a port instance */
  def getThisEndpoint(pi: PortInstance): Connection.Endpoint = {
    import PortInstance.Direction._
    pi.getDirection.get match {
      case Input => to
      case Output => from
    }
  }

  /** Get the other endpoint of a port connection at a port instance */
  def getOtherEndpoint(pi: PortInstance): Connection.Endpoint = {
    import PortInstance.Direction._
    pi.getDirection.get match {
      case Input => from
      case Output => to
    }
  }

}

object Connection {

  /** Constructs a connection from an AST connection */
  def fromAst(a: Analysis, connection: Ast.SpecConnectionGraph.Connection):
    Result.Result[Connection] =
      for {
        from <- Endpoint.fromAst(a, connection.fromPort, connection.fromIndex)
        to <- Endpoint.fromAst(a, connection.toPort, connection.toIndex)
        connection <- Right(Connection(from, to, connection.isUnmatched))
        _ <- connection.checkDirections
        _ <- connection.checkTypes
        _ <- connection.checkSerialWithTypedInput
        _ <- 
          if !connection.isMatchConstrained && connection.isUnmatched 
          then Left(SemanticError.MissingPortMatching(connection.getLoc))
          else Right(())
      }
      yield connection

  /** A connection endpoint */
  case class Endpoint(
    /** The location where the endpoint is defined */
    loc: Location,
    /** The port instance identifier */
    port: PortInstanceIdentifier,
    /** The port number */
    portNumber: Option[Int] = None,
    /** Topology port this mapped to */
    topologyPort: Option[Endpoint] = None
  ) extends Ordered[Endpoint] {

    override def toString = portNumber match {
      case Some(n) => s"${port.toString}[${n.toString}]"
      case None => port.toString
    }

    override def compare(that: Endpoint) = {
      val name1 = this.port.getQualifiedName.toString
      val name2 = that.port.getQualifiedName.toString
      val nameCompare = name1.compare(name2)
      if (nameCompare != 0) nameCompare
      else (this.portNumber, that.portNumber) match {
        case (Some (n1), Some (n2)) => n1 - n2
        case _ => 0
      }
    }

    /** Check that port number is in bounds for the size */
    def checkPortNumber(loc: Location): Result.Result[Unit] = portNumber match {
      case Some(n) =>
        val size = port.portInstance.getArraySize
        val specLoc = port.portInstance.getLoc
        val name = port.getQualifiedName.toString
        if (n < size) Right(())
        else Left(SemanticError.InvalidPortNumber(loc, n, name, size, specLoc))
      case None => Right(())
    }

    /** Get underlying endpoint by stripping off topology port aliases */
    def getUnderlyingEndpoint(): Endpoint = {
      port.interfaceInstance match {
        // This endpoint is already a component instance
        case InterfaceInstance.InterfaceComponentInstance(ci) => this
        case InterfaceInstance.InterfaceTopology(top) => this.copy(
          // Look up the mapping for this port instance
          port = top.portMap(port.portInstance.getUnqualifiedName),
          topologyPort = Some(this)
          // Recursively resolve the endpoint to a component instance
        ).getUnderlyingEndpoint()
      }
    }

  }

  object Endpoint {

    /** Constructs a connection endpoint from AST info */
    def fromAst(
      a: Analysis,
      port: AstNode[Ast.PortInstanceIdentifier],
      portNumber: Option[AstNode[Ast.Expr]]
    ): Result.Result[Endpoint] = for {
      pid <- PortInstanceIdentifier.fromNode(a, port)
      _ <- pid.portInstance.requireConnectionAt(Locations.get(port.id))
      pn <- a.getNonnegativeIntValueOpt(portNumber)
      endpoint <- Right(Endpoint(Locations.get(port.id), pid, pn))
      _ <- portNumber match {
        case Some(pn) => endpoint.checkPortNumber(Locations.get(pn.id))
        case None => Right(())
      }
    } yield endpoint

  }

}
