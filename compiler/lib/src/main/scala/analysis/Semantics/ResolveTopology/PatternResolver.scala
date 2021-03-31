package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A class for resolving connection patterns */
private sealed trait PatternResolver {

  /** The type of a source */
  type Source

  /** The type of the target */
  type Target

  /** The enclosing analysis */
  val a: Analysis

  /** The connection pattern to resolve */
  val pattern: ConnectionPattern

  /** The component instances in scope */
  val instances: Iterable[ComponentInstance]

  /** Resolve the source */
  def resolveSource: Result.Result[Source]

  /** Resolve a target */
  def resolveTarget(targetUse: (ComponentInstance, Location)): Result.Result[Target]

  /** Generate the connections for a source and target */
  def getConnectionsForTarget(source: Source, target: Target): Iterable[Connection]

  /** Resolve the pattern to a list of connections */
  final def resolve: Result.Result[Iterable[Connection]] =
    for {
      source <- resolveSource
      targets <- resolveTargets
    }
    yield targets.flatMap(getConnectionsForTarget(source, _))

  /** Gets the specified general port from a component instance */
  def getGeneralPort(
    ciUse: (ComponentInstance, Location),
    kind: String,
    direction: PortInstance.Direction,
    portTypeName: String,
  ): Result.Result[PortInstanceIdentifier] = {
    def hasConnectorPort(pi: PortInstance): Boolean = {
      (pi.getType, pi.getDirection) match {
        case (
          Some(PortInstance.Type.DefPort(s)),
          Some(d)
        ) => a.getQualifiedName(s).toString == portTypeName &&
             d == direction
        case _ => false
      }
    }
    val (ci, loc) = ciUse
    for {
      pii <- PatternResolver.resolveToSinglePort(
        PatternResolver.getPortsForInstance(ci).filter(hasConnectorPort),
        s"$kind $direction",
        loc,
        ci.getUnqualifiedName
      )
    } yield PortInstanceIdentifier(ci, pii)
  }

  private def resolveTargets: Result.Result[Iterable[Target]] =
    pattern.targets.size match {
      case 0 => resolveImplicitTargets
      case _ => resolveExplicitTargets
    }

  private def resolveImplicitTargets: Result.Result[Iterable[Target]] = {
    val loc = pattern.getLoc
    val targets = instances.map(i => resolveTarget((i, loc))).
      filter(_.isRight).map(Result.expectRight)
    Right(targets)
  }

  private def resolveExplicitTargets: Result.Result[List[Target]] =
    Result.map(pattern.targets.toList, resolveTarget)

}

object PatternResolver {

  /** Resolve a pattern */
  def resolve(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance]
  ): Result.Result[Iterable[Connection]] = {
    import Ast.SpecConnectionGraph._
    val resolver = pattern.ast.kind match {
      case Pattern.Command => PatternResolver.Command(a, pattern, instances)
      case Pattern.Event => PatternResolver.event(a, pattern, instances)
      case Pattern.Health => PatternResolver.Health(a, pattern, instances)
      case Pattern.Param => PatternResolver.Param(a, pattern, instances)
      case Pattern.Telemetry => PatternResolver.telemetry(a, pattern, instances)
      case Pattern.TextEvent => PatternResolver.textEvent(a, pattern, instances)
      case Pattern.Time => PatternResolver.time(a, pattern, instances)
    }
    resolver.resolve
  }

  private def connect(
    loc: Location,
    fromPii: PortInstanceIdentifier,
    toPii: PortInstanceIdentifier
  ) = {
    val from = Connection.Endpoint(loc, fromPii)
    val to = Connection.Endpoint(loc, fromPii)
    Connection(from, to)
  }

  private def getPortsForInstance(instance: ComponentInstance) =
    instance.component.portMap.values

  private def missingPort[T](
    loc: Location,
    kind: String,
    instanceName: Name.Unqualified
  ): Result.Result[T] =
    Left(
      SemanticError.InvalidPattern(
        loc,
        s"instance $instanceName has no $kind port"
      )
    )

  private def getSpecialPort(
    ciUse: (ComponentInstance, Location),
    kind: Ast.SpecPortInstance.SpecialKind
  ): Result.Result[PortInstanceIdentifier] = {
    val (ci, loc) = ciUse
    ci.component.specialPortMap.get(kind) match { 
      case Some(pi) => Right(PortInstanceIdentifier(ci, pi))
      case None => missingPort(loc, kind.toString, ci.getUnqualifiedName)
    }
  }

  private def resolveToSinglePort(
    ports: Iterable[PortInstance],
    kind: String,
    loc: Location,
    instanceName: String
  ): Result.Result[PortInstance] =
    ports.size match {
      case 1 => Right(ports.head)
      case 0 => missingPort(loc, kind, instanceName)
      case _ =>
        val portNames = ports.map(_.getUnqualifiedName).mkString(", ")
        Left(
          SemanticError.InvalidPattern(
            loc,
            s"ambiguous pattern: instance $instanceName has $kind ports $portNames"
          )
        )
    }

  /** Resolve a command pattern */
  private final case class Command(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance],
  ) extends PatternResolver {

    case class Source(
      cmdRegIn: PortInstanceIdentifier,
      cmdOut: PortInstanceIdentifier,
      cmdResponseIn: PortInstanceIdentifier
    )

    case class Target(
      cmdRegOut: PortInstanceIdentifier,
      cmdIn: PortInstanceIdentifier,
      cmdResponseOut: PortInstanceIdentifier
    )

    def getCmdRegIn = getGeneralPort(
      pattern.source,
      "command reg",
      PortInstance.Direction.Input,
      "Fw.CmdReg"
    )

    def getCmdOut = getGeneralPort(
      pattern.source,
      "command send",
      PortInstance.Direction.Output,
      "Fw.Cmd"
    )

    def getCmdResponseIn = getGeneralPort(
      pattern.source,
      "command resp",
      PortInstance.Direction.Input,
      "Fw.CmdResponse"
    )

    def getCmdRegOut(targetUse: (ComponentInstance, Location)) =
      getSpecialPort(targetUse, Ast.SpecPortInstance.CommandReg)

    def getCmdIn(targetUse: (ComponentInstance, Location)) =
      getSpecialPort(targetUse, Ast.SpecPortInstance.CommandRecv)

    def getCmdResponseOut(targetUse: (ComponentInstance, Location)) =
      getSpecialPort(targetUse, Ast.SpecPortInstance.CommandResp)

    override def resolveSource =
      for {
        cmdRegIn <- getCmdRegIn
        cmdOut <- getCmdOut
        cmdResponseIn <- getCmdResponseIn
      } yield Source(cmdRegIn, cmdOut, cmdResponseIn)

    override def resolveTarget(targetUse: (ComponentInstance, Location)) =
      for {
        cmdRegOut <- getCmdRegOut(targetUse)
        cmdIn <- getCmdIn(targetUse)
        cmdResponseOut <- getCmdResponseOut(targetUse)
      } yield Target(cmdRegOut, cmdIn, cmdResponseOut)

    override def getConnectionsForTarget(
      source: Source,
      target: Target
    ): List[Connection] = {
      val loc = pattern.getLoc
      List(
        connect(loc, target.cmdRegOut, source.cmdRegIn),
        connect(loc, source.cmdOut, target.cmdIn),
        connect(loc, target.cmdResponseOut, source.cmdResponseIn)
      )
    }

  }

  /** Resolve a pattern involving connections from a single special 
   *  target port */
  private final case class FromSpecialTargetPort(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance],
    kind: Ast.SpecPortInstance.SpecialKind,
    portTypeName: String
  ) extends PatternResolver {

    type Source = PortInstanceIdentifier

    type Target = PortInstanceIdentifier

    override def resolveSource = getGeneralPort(
      pattern.source,
      kind.toString,
      PortInstance.Direction.Input,
      portTypeName
    )

    override def resolveTarget(targetUse: (ComponentInstance, Location)) =
      getSpecialPort(targetUse, kind)

    override def getConnectionsForTarget(
      source: Source,
      target: Target
    ): List[Connection] = List(connect(pattern.getLoc, target, source))

  }

  private def event(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance]
  ) = FromSpecialTargetPort(
    a, pattern, instances,
    Ast.SpecPortInstance.Event,
    "Fw.Log"
  )

  private def telemetry(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance]
  ) = FromSpecialTargetPort(
    a, pattern, instances,
    Ast.SpecPortInstance.Telemetry,
    "Fw.Tlm"
  )

  private def textEvent(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance]
  ) = FromSpecialTargetPort(
    a, pattern, instances,
    Ast.SpecPortInstance.TextEvent,
    "Fw.LogText"
  )

  private def time(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance]
  ) = FromSpecialTargetPort(
    a, pattern, instances,
    Ast.SpecPortInstance.TimeGet,
    "Fw.Time"
  )

  /** Resolve a health pattern */
  private final case class Health(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance],
  ) extends PatternResolver {

    type Source = Health.PingPorts

    type Target = Health.PingPorts

    private def getPingPort(
      ciUse: (ComponentInstance, Location),
      direction: PortInstance.Direction
    ) = getGeneralPort(ciUse, "ping", direction, "Svc.Ping")

    private def getPingPorts(ciUse: (ComponentInstance, Location)) =
      for {
        pingIn <- getPingPort(ciUse, PortInstance.Direction.Input)
        pingOut <- getPingPort(ciUse, PortInstance.Direction.Output)
      }
      yield Health.PingPorts(pingIn, pingOut)

    override def resolveSource = getPingPorts(pattern.source)

    override def resolveTarget(targetUse: (ComponentInstance, Location)) =
      getPingPorts(targetUse)

    override def getConnectionsForTarget(
      source: Source,
      target: Target
    ): List[Connection] = {
      val loc = pattern.getLoc
      List(
        connect(loc, source.pingOut, target.pingIn),
        connect(loc, target.pingOut, source.pingIn)
      )
    }

  }

  private final object Health {

    final case class PingPorts(
      pingIn: PortInstanceIdentifier,
      pingOut: PortInstanceIdentifier
    )

  }

  /** Resolve a param pattern */
  private final case class Param(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance],
  ) extends PatternResolver {

    case class Source(
      prmGetIn: PortInstanceIdentifier,
      prmSetIn: PortInstanceIdentifier
    )

    case class Target(
      prmGetOut: PortInstanceIdentifier,
      prmSetOut: PortInstanceIdentifier
    )

    def getPrmGetIn = getGeneralPort(
      pattern.source,
      "param get",
      PortInstance.Direction.Input,
      "Fw.PrmGet"
    )

    def getPrmSetIn = getGeneralPort(
      pattern.source,
      "param set",
      PortInstance.Direction.Input,
      "Fw.PrmSet"
    )

    def getPrmGetOut(targetUse: (ComponentInstance, Location)) =
      getSpecialPort(targetUse, Ast.SpecPortInstance.ParamGet)

    def getPrmSetOut(targetUse: (ComponentInstance, Location)) =
      getSpecialPort(targetUse, Ast.SpecPortInstance.ParamSet)

    override def resolveSource = for {
      prmGetIn <- getPrmGetIn
      prmSetIn <- getPrmSetIn
    } yield Source(prmGetIn, prmSetIn)

    override def resolveTarget(targetUse: (ComponentInstance, Location)) =
      for {
        prmGetOut <- getPrmGetOut(targetUse)
        prmSetOut <- getPrmSetOut(targetUse)
      } yield Target(prmGetOut, prmSetOut)

    override def getConnectionsForTarget(
      source: Source,
      target: Target
    ): List[Connection] = {
      val loc = pattern.getLoc
      List(
        connect(loc, target.prmGetOut, source.prmGetIn),
        connect(loc, target.prmSetOut, source.prmSetIn)
      )
    }

  }

}
