package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object CheckTemplateInterfaceParams
{

  /** Check all interface parameters template expansions implement the proper interfaces */
  def check(a: Analysis): Result.Result[Unit] =
    Result.foldLeft(a.templateExpansionMap.toList) (()) ((_, expansion) => {
      val (expansionNodeId, t) = expansion
      Result.foldLeft(t.params.values.collect {
        case param @ Symbol.TemplateInterfaceParam(_, _) => param
      })(())((_, param) => {
        for {
          // Make sure this concrete value meets the specified port interface constraints
          // These parameters are already checked if they are used in a topology inside the
          _ <- a.getInterfaceInstance(param.value.id)
        } yield (())
      })
    })

}
