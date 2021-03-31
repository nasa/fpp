package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve unused ports */
object ResolveUnusedPorts {

  /** Compute the ports of t that are actually unused */
  def resolve(t: Topology): Result.Result[Topology] =
    // TODO
    Right(t)

}
