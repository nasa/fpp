package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Build the location specifier map */
object BuildSpecLocMap extends ModuleAnalyzer {

  override def specLocAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ) = {
    val specNode = node._2
    val spec = specNode.data
    val symbol = spec.symbol
    val qualifiedName = Name.Qualified.fromIdentList(a.scopeNameList.reverse ++ symbol.data.toIdentList)
    val key = (spec.kind, qualifiedName)
    a.locationSpecifierMap.get(key) match {
      case None => {
        val map = a.locationSpecifierMap + (key -> spec)
        Right(a.copy(locationSpecifierMap = map))
      }
      case Some(spec1) => 
        for { _ <- checkPathConsistency(spec, spec1) } yield a
    }
  }

  private def checkPathConsistency(
    spec1: Ast.SpecLoc,
    spec2: Ast.SpecLoc
  ): Result.Result[Unit] = {
    val path1 = getPathString(spec1)
    val path2 = getPathString(spec2)
    if (path1 == path2)
      Right(())
    else Left(
      SemanticError.InconsistentSpecLoc(
        Locations.get(spec1.file.id),
        path1,
        Locations.get(spec2.file.id),
        path2
      )
    )
  }

  private def getPathString(spec: Ast.SpecLoc): String = {
    val loc = Locations.get(spec.file.id)
    val path = loc.getRelativePath(spec.file.data)
    path.toString
  }

}
