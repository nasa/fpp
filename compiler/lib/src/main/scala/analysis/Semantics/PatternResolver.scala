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
  def resolveTarget(target: (ComponentInstance, Location)): Result.Result[Target]

  /** Generate the connections for a source and target */
  def getConnectionsForTarget(source: Source, target: Target): Iterable[Connection]

  /** Resolve the pattern to a list of connections */
  final def resolve: Result.Result[Iterable[Connection]] =
    for {
      source <- resolveSource
      targets <- resolveTargets
    }
    yield targets.flatMap(getConnectionsForTarget(source, _))

  private def resolveTargets: Result.Result[Iterable[Target]] =
    pattern.targets.size match {
      case 0 => resolveImplicitTargets
      case _ => resolveExplicitTargets
    }

  private def resolveImplicitTargets: Result.Result[Iterable[Target]] = {
    val loc = pattern.getLoc
    val targets = instances.map(ti => resolveTarget((ti, loc))).
      filter(_.isRight).map(Result.expectRight)
    Right(targets)
  }

  private def resolveExplicitTargets: Result.Result[List[Target]] =
    Result.map(
      pattern.targets.toList,
      resolveTarget
    )

}

object PatternResolver {

  /** Resolve a pattern */
  def resolve(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance]
  ): Result.Result[Iterable[Connection]] = {
    import Ast.SpecConnectionGraph._
    val toDo = None
    val resolverOpt: Option[PatternResolver] = pattern.ast.kind match {
      case Pattern.Command => toDo
      case Pattern.Event => Some(PatternResolver.event(a, pattern, instances))
      case Pattern.Health => toDo
      case Pattern.Param => toDo
      case Pattern.Telemetry => Some(PatternResolver.telemetry(a, pattern, instances))
      case Pattern.TextEvent => Some(PatternResolver.textEvent(a, pattern, instances))
      case Pattern.Time => Some(PatternResolver.time(a, pattern, instances))
    }
    resolverOpt match {
      case Some(resolver) => resolver.resolve
      case None => Right(Nil)
    }
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

  /** Resolve a pattern involving connections from a special 
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

    override def resolveSource = {
      def hasInputPort(pi: PortInstance): Boolean = 
        (pi.getType, pi.getDirection) match {
          case (
            Some(PortInstance.Type.DefPort(s)),
            Some(PortInstance.Direction.Input)
          ) => a.getQualifiedName(s).toString == portTypeName
          case _ => false
        }
      val (ci, loc) = pattern.source
      for {
        pii <- resolveToSinglePort(
          getPortsForInstance(ci).filter(hasInputPort),
          s"$kind in",
          loc,
          ci.getUnqualifiedName
        )
      } yield PortInstanceIdentifier(ci, pii)
    }

    override def resolveTarget(target: (ComponentInstance, Location)) = {
      val (ci, loc) = target
      ci.component.specialPortMap.get(kind) match { 
        case Some(pi) => Right(PortInstanceIdentifier(ci, pi))
        case None => missingPort(loc, kind.toString, ci.getUnqualifiedName)
      }
    }

    override def getConnectionsForTarget(
      source: Source,
      target: Target
    ): List[Connection] = {
      val loc = pattern.getLoc
      val from = Connection.Endpoint(loc, target)
      val to = Connection.Endpoint(loc, source)
      List(Connection(from, to))
    }

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
    Ast.SpecPortInstance.Event,
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

}
