package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object CheckTopologyInterface {

  /** Check a topology implements all the interfaces that are listed in the AST */
  def check(a: Analysis, t: Topology): Result.Result[Unit] =
    Result.foldLeft (t.aNode._2.data.implements) (()) ((_, impl) => {
      for {
        iface <- a.getInterface(impl.id)
        _ <- {
          t.portInterface.implements(iface.portInterface) match {
            case Right(_) => Right(())
            case Left(err) => Left(SemanticError.InterfaceImplements(
              Locations.get(impl.id),
              err
            ))
          }
        }
      } yield (())
    })

}
