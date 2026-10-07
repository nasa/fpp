package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Add the direct dependencies of the input translation units.
 *  A direct dependency is a file that contains a definition used in the
 *  input. Run this analysis after the transitive dependency analysis,
 *  so that the location specifier map is complete. */
object AddDirectDependencies extends LocatedUseAnalyzer {

  def tuList(a: Analysis, tul: List[Ast.TransUnit]): Result =
    for (a <- visitList(a, tul, transUnit))
      yield {
        // The dictionary of a deployment includes all dictionary definitions
        val dictionaryFiles =
          if a.includeDictionaryDeps
          then a.locationSpecifierMap.values.map(_.data).
            filter(_.isDictionaryDef).map(getFile)
          else Nil
        dictionaryFiles.foldLeft(a)(addDirectDependency)
      }

  override def locatedUse(a: Analysis, specLoc: Ast.SpecLoc) =
    Right(addDirectDependency(a, getFile(specLoc)))

  private def addDirectDependency(a: Analysis, file: File): Analysis =
    if a.inputFileSet.contains(file) then a
    else a.copy(directDependencyFileSet = a.directDependencyFileSet + file)

}
