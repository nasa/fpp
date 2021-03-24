package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A class for resolving connection patterns */
sealed trait PatternResolver {

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
      case Pattern.Event => toDo
      case Pattern.Health => toDo
      case Pattern.Param => toDo
      case Pattern.Telemetry => toDo
      case Pattern.Time => Some(PatternResolver.Time(a, pattern, instances))
    }
    resolverOpt match {
      case Some(resolver) => resolver.resolve
      case None => Right(Nil)
    }
  }

  private def resolveToSinglePort(
    kind: String,
    loc: Location,
    instanceName: String,
    ports: Iterable[PortInstance]
  ): Result.Result[PortInstance] =
    ports.size match {
      case 1 => Right(ports.head)
      case 0 => Left(
        SemanticError.InvalidPattern(
          loc,
          s"could not find $kind port for instance $instanceName"
        )
      )
      case _ =>
        val portNames = ports.map(_.getUnqualifiedName).mkString(", ")
        Left(
          SemanticError.InvalidPattern(
            loc,
            s"ambiguous pattern: instance $instanceName has $kind ports $portNames"
          )
        )
    }

  final case class Time(
    a: Analysis,
    pattern: ConnectionPattern,
    instances: Iterable[ComponentInstance]
  ) extends PatternResolver {

    type Source = PortInstanceIdentifier

    type Target = PortInstanceIdentifier

    override def resolveSource: Result.Result[Source] = {
      def isTimeGetIn(pi: PortInstance): Boolean = 
        (pi.getType, pi.getDirection) match {
          case (
            Some(PortInstance.Type.DefPort(s)),
            Some(PortInstance.Direction.Input)
          ) => a.getQualifiedName(s).toString == "Fw.Time"
          case _ => false
        }
      val source = pattern.source._1
      val allPorts = source.component.portMap.values
      val timeGetInPorts = allPorts.filter(isTimeGetIn)
      val loc = Locations.get(pattern.ast.source.id)
      val instanceName = source.getUnqualifiedName
      for {
        pi <- resolveToSinglePort(
          "time get in",
          loc,
          instanceName,
          timeGetInPorts
        )
      } yield PortInstanceIdentifier(source, pi)
    }

    override def resolveTarget(
      target: (ComponentInstance, Location)
    ): Result.Result[Target] = {
      val (ci, loc) = target
      ci.component.specialPortMap.get(Ast.SpecPortInstance.TimeGet) match { 
        case Some(pi) => Right(PortInstanceIdentifier(ci, pi))
        case None =>
          val instanceName = ci.getUnqualifiedName
          Left(
            SemanticError.InvalidPattern(
              loc,
              s"could not find time get port for instance $instanceName"
            )
          )
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

}
