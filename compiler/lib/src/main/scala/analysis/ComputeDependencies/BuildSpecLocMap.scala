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
        val map = a.locationSpecifierMap + (key -> specNode)
        Right(a.copy(locationSpecifierMap = map))
      }
      case Some(specNode1) =>
        for {
          _ <- checkPathConsistency(spec, specNode1.data)
          _ <- checkDictionarySpecifierConsistency(specNode, specNode1)
        } yield a
    }
  }

  private def checkDictionarySpecifierConsistency(
    specNode1: AstNode[Ast.SpecLoc],
    specNode2: AstNode[Ast.SpecLoc]
  ): Result.Result[Unit] = {
    val spec1 = specNode1.data
    val spec2 = specNode2.data
    if(spec1.isDictionaryDef == spec2.isDictionaryDef) then
      Right(())
    else
      Left(
        SemanticError.InconsistentDictionarySpecifier(
          Locations.get(specNode1.id),
          Locations.get(specNode2.id),
        )
      )
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
      SemanticError.InconsistentLocationPath(
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
