package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A class for resolving connection patterns */
sealed trait PatternResolver {

  /** The type of a source */
  type Source

  /** The type of the target */
  type Target

  /** The connection pattern to resolve */
  val pattern: ConnectionPattern

  /** The component instances in scope */
  val instances: List[ComponentInstance]

  /** Resolve the source */
  def resolveSource: Result.Result[Source]

  /** Resolve a target */
  def resolveTarget(
    loc: Location, /** The location where the target was declared */
    target: ComponentInstance /** The target */
  ): Result.Result[Target]

  /** Generate the connections for a source and target */
  def getConnectionsForTarget(source: Source, target: Target): List[Connection]

  /** Resolve the pattern to a list of connections */
  final def resolve: Result.Result[List[Connection]] =
    for {
      source <- resolveSource
      targets <- resolveTargets
    }
    yield targets.flatMap(getConnectionsForTarget(source, _))

  private def resolveTargets: Result.Result[List[Target]] =
    pattern.targets.size match {
      case 0 => resolveImplicitTargets
      case _ => resolveExplicitTargets
    }

  private def resolveImplicitTargets: Result.Result[List[Target]] = {
    val loc = Locations.get(pattern.aNode._2.id)
    val targets = instances.map(target => resolveTarget(loc, target)).
      filter(_.isRight).map(Result.expectRight)
    Right(targets)
  }

  private def resolveExplicitTargets: Result.Result[List[Target]] =
    Result.map(
      pattern.targets.toList,
      target => resolveTarget(Locations.get(target.aNode._2.id), target)
    )

}
