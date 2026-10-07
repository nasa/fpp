package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.transform._

/** Compute dependencies for a list of translation units */
object ComputeDependencies {

  /** Compute the dependencies of the input translation units */
  def tuList(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[Analysis] =
    for {
      pair <- ResolveSpecInclude.transformList(a, tul, ResolveSpecInclude.transUnit)
      a <- Right(pair._1)
      tul <- Right(pair._2)
      // The files included in the input are direct dependencies
      a <- Right(a.copy(directDependencyFileSet = a.includedFileSet))
      a <- addDependencies(a, tul)
      a <- AddDirectDependencies.tuList(a, tul)
    }
    yield a

  /** Compute the dependencies of the translation units in a dependency file */
  def dependencyTuList(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[Analysis] =
    for {
      pair <- ResolveSpecInclude.transformList(a, tul, ResolveSpecInclude.transUnit)
      a <- addDependencies(pair._1, pair._2)
    }
    yield a

  private def addDependencies(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[Analysis] =
    for {
      a <- BuildSpecLocMap.visitList(a, tul, BuildSpecLocMap.transUnit)
      a <- ConstructImpliedUseMap.visitList(a, tul, ConstructImpliedUseMap.transUnit)
      a <- AddDependencies.tuList(a, tul)
    }
    yield {
      val includedFileSet = a.includedFileSet
      val dependencyFileSet = a.dependencyFileSet.diff(includedFileSet)
      a.copy(dependencyFileSet = dependencyFileSet)
    }

}
