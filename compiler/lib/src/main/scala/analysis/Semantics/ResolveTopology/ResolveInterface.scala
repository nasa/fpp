package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object ResolveInterface {

  /** Resolve an interface */
  def resolve(a: Analysis, i: Interface): Result.Result[Interface] = {
    def resolveImport(i: Interface, ii: (Symbol.Interface, (AstNode.Id, Location))) =
      i.addImportedInterface(a.interfaceMap(ii._1), ii._2._1)

    Result.foldLeft(List.from(i.directImportMap.iterator)) (i) (resolveImport)
  }

}
