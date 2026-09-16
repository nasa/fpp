package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object ResolveTopologyInstances {

  private def representedTopology(ii: InterfaceInstance): Option[Topology] =
    ii match {
      case InterfaceInstance.InterfaceTopology(top) => Some(top)
      case InterfaceInstance.InterfaceTemplateArg(_, _, bound) =>
        representedTopology(bound)
      case _ => None
    }

  private def checkNotDeployment(
    paramName: Name.Unqualified,
    ii: InterfaceInstance,
    loc: Location
  ): Result.Result[Unit] =
    representedTopology(ii) match {
      case Some(top) if top.aNode._2.data.isDeployment =>
        Left(
          SemanticError.InvalidSymbol(
            paramName,
            loc,
            "use of deployment topology is not allowed here",
            top.getLoc
          )
        )
      case _ => Right(())
    }

  /** Resolve a topology */
  def resolve(a: Analysis, t: Topology): Result.Result[Topology] =
    for {
        t <- {
            val tops = t.directTopologies.toList
            Right(tops.foldLeft (t) ((t, ti) => {
              val ii = InterfaceInstance.fromTopology(a.topologyMap(ti._1))
              t.addInstance(ii, ti._2)
            }))
        }

        t <- {
            val comps = t.directComponentInstances.toList
            Right(comps.foldLeft (t) ((t, ci) => {
              val ii = InterfaceInstance.fromComponentInstance(a.componentInstanceMap(ci._1))
              t.addInstance(ii, ci._2)
            }))
        }

        t <- {
            val instances = t.directTemplateArgs.toList
            Result.foldLeft(instances) (t) ((t, symI) => {
              val (tip, loc) = symI
              for {
                ii <- a.getInterfaceInstance(symI._1.value.id)
                _ <- checkNotDeployment(tip.paramDef.name, ii, loc)
                iface <- a.getInterface(tip.paramDef.interface.id)
              } yield t.addInstance(InterfaceInstance.fromTemplateArg(
                tip.paramDef,
                iface,
                ii
              ), loc)
            })
        }
    }
    yield t

}
