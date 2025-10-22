package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.transform._

/** Compute dependencies for a list of translation units */
object ComputeDependencies {

  def checkDictionarySpecLocs(a: Analysis): Result.Result[Analysis] = {
    val dictionaryFiles: Set[File] = 
      a.locationSpecifierMap.values.filter(_.isDictionaryDef).map(
        specLoc => Locations.get(specLoc.file.id).file
      ).toSet
    Right(a.copy(dependencyFileSet = a.dependencyFileSet ++ dictionaryFiles))
  }

  def tuList(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[Analysis] = {
    for {
      pair <- ResolveSpecInclude.transformList(
        a.copy(level = a.level + 1),
        tul, 
        ResolveSpecInclude.transUnit
      )
      a <- Right(pair._1)
      tul <- Right(pair._2)
      a <- a.level match {
        case 1 => Right(a.copy(directDependencyFileSet = a.includedFileSet))
        case _ => Right(a)
      }
      a <- BuildSpecLocMap.visitList(a, tul, BuildSpecLocMap.transUnit)
      a <- ConstructImpliedUseMap.visitList(a, tul, ConstructImpliedUseMap.transUnit)
      a <- MapUsesToLocs.visitList(a, tul, MapUsesToLocs.transUnit)
      a <- checkDictionarySpecLocs(a)
    }
    yield {
      val includedFileSet = a.includedFileSet
      val dependencyFileSet = a.dependencyFileSet.diff(includedFileSet)
      a.copy(level = a.level - 1, dependencyFileSet = dependencyFileSet)
    }
  }

}
