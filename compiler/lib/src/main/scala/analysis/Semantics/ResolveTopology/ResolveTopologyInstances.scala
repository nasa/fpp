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
              t.addInstance(InterfaceInstance.fromTopology(a.topologyMap(ti._1)), ti._2)
            }))
        }

        t <- {
            val comps = t.directComponentInstances.toList
            Right(comps.foldLeft (t) ((t, ci) => {
              t.addInstance(InterfaceInstance.fromComponentInstance(a.componentInstanceMap(ci._1)), ci._2)
            }))
        }

        t <- {
            val instances = t.directTemplateParameters.toList
            Result.foldLeft(instances) (t) ((t, symI) => {
              for (ii <- a.getInterfaceInstance(symI._1.value.id))
                yield t.addInstance(InterfaceInstance.fromInterfaceTemplateParam(ii), symI._2)
            })
        }
    }
    yield t

}
