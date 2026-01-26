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
              t.addInstance(ii, ii.getInterface, ii.getUnqualifiedName, ti._2)
            }))
        }

        t <- {
            val comps = t.directComponentInstances.toList
            Right(comps.foldLeft (t) ((t, ci) => {
              val ii = InterfaceInstance.fromComponentInstance(a.componentInstanceMap(ci._1))
              t.addInstance(ii, ii.getInterface, ii.getUnqualifiedName, ci._2)
            }))
        }

        t <- {
            val instances = t.directTemplateParameters.toList
            Result.foldLeft(instances) (t) ((t, symI) => {
              val (tip, loc) = symI
              for {
                ii <- a.getInterfaceInstance(symI._1.value.id)
                iface <- a.getInterface(tip.paramDef.interface.id)
                // Make sure that we can bind ii to iface
                _ <- ii.getInterface.implements(iface.portInterface)
              } yield t.addInstance(ii, iface.portInterface, iface.aNode._2.data.name, loc)
            })
        }
    }
    yield t

}
