package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._

/** Add the transitive dependencies */
object AddDependencies extends LocatedUseAnalyzer {

  def tuList(a: Analysis, tul: List[Ast.TransUnit]): Result =
    visitList(a, tul, transUnit)

  override def locatedUse(a: Analysis, specLoc: Ast.SpecLoc) =
    addDependencies (a) (specLoc)

  override def specLocAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ) = {
    val specLoc = node._2.data
    if a.includeDictionaryDeps && specLoc.isDictionaryDef
    then
      // We are visiting a dictionary specifier after visiting
      // the first topology. Add the depdendencies for the specifier.
      addDependencies (a) (specLoc)
    else
      // This is not a dictionary specifier, or we haven't seen a topology.
      // Nothing to do.
      Right(a)
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    for {
      // Add dependencies based on explicit and implicit uses in the topology
      a <- super.defTopologyAnnotatedNode(a, node)
      // Add dependencies based on dictionary specifiers
      a <- if node._2.data.isDeployment && !a.includeDictionaryDeps
           then
             // This is the first deployment topology we have visited.
             // Set includeDictionaryDeps = true and add all dictionary dependencies
             // discovered so far.
             val a1 = a.copy(includeDictionaryDeps = true)
             val dictionarySpecLocs =
               a1.locationSpecifierMap.values.map(_.data).filter(_.isDictionaryDef)
             Result.foldLeft (dictionarySpecLocs.toList) (a1) {
               case (a, s) => addDependencies (a) (s)
             }
           else
             // This is the second or later deployment topology, or a
             // non-deployment topology; nothing to do
             Right(a)
    } yield a
  }

  private def addDependenciesHelper(a: Analysis, specLoc: Ast.SpecLoc, file: File): Result = {
    val a1 = a.copy(dependencyFileSet = a.dependencyFileSet + file)
    val result = for {
      tu <- Parser.parseFile (Parser.transUnit) (None) (file)
      pair <- ResolveSpecInclude.transUnit(a1, tu)
      a2 <- ComputeDependencies.dependencyTuList(
        pair._1.copy(scopeNameList = Nil),
        List(pair._2)
      )
    } yield a2.copy(scopeNameList = a1.scopeNameList)
    result match {
      case Left(FileError.CannotOpen(_, _)) => {
        val a = a1.copy(missingDependencyFileSet = a1.missingDependencyFileSet + file)
        Right(a)
      }
      case _ => result
    }
  }

  private def addDependencies (a: Analysis) (specLoc: Ast.SpecLoc): Result = {
    val file = getFile(specLoc)
    if !a.inputFileSet.contains(file) && !a.dependencyFileSet.contains(file)
    then addDependenciesHelper(a, specLoc, file)
    else Right(a)
  }

}
