package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object CheckTemplateInterfaceArgs
{

  /** Check all interface parameters template expansions implement the proper interfaces */
  def check(a: Analysis): Result.Result[Unit] =
    Result.foldLeft(a.templateExpansionMap.toList) (()) ((_, expansion) => {
      val (expansionNodeId, t) = expansion
      Result.foldLeft(t.params.values.collect {
        case arg @ Symbol.TemplateInterfaceArg(_, _) => arg
      })(())((_, arg) => {
        for {
          // Resolve the concrete instance supplied as the argument
          instance <- a.getInterfaceInstance(arg.value.id)
          // Resolve the interface declared for this parameter
          iface <- a.getInterface(arg.paramDef.interface.id)
          // Make sure the concrete instance provides everything the declared
          // interface requires (the declared interface must be a sub-interface
          // of the concrete instance's port interface)
          _ <- instance.getInterface.implements(iface.portInterface) match {
            case Right(_) => Right(())
            case Left(err) => Left(SemanticError.InterfaceImplements(
              Locations.get(arg.value.id),
              err
            ))
          }
        } yield (())
      })
    })

}
