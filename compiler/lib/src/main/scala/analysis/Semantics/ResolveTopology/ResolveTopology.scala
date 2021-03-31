package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object ResolveTopology {

  /** Resolve a topology */
  def resolve(a: Analysis, t: Topology): Result.Result[Topology] =
    for {
      t <- ResolvePartiallyNumbered.resolve(a, t)
      // TODO
    }
    yield t
//    Result.seq(
//      Right(this),
//      List(
//        _.computePortNumbers,
//        _.computeUnusedPorts
//      )
//    )

}
