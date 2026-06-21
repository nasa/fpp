package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object ResolveTopologyInstances {

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
                iface <- a.getInterface(tip.paramDef.interface.id)
                // Make sure that we can bind ii to iface
                _ <- ii.getInterface.implements(iface.portInterface)
              } yield t.addInstance(InterfaceInstance.fromTemplateArg(
                tip.paramDef,
                iface.portInterface,
                ii
              ), loc)
            })
        }
    }
    yield t

}
