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
    val spec = specNode.getData
    val symbol = spec.symbol
    val qualifiedName = Name.Qualified.fromIdentList(a.moduleNameList.reverse ++ Ast.QualIdentNode.toList(symbol).map(_.getData))
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
    for {
      path1 <- getPathString(spec1)
      path2 <- getPathString(spec2)
      _ <- if (path1 == path2) Right(()) else {
          val e = SemanticError.InconsistentSpecLoc(
          Locations.get(spec1.file.getId),
          path1,
          Locations.get(spec2.file.getId),
          path2
        )
        Left(e)
      }
    } yield ()
  }

  private def getPathString(spec: Ast.SpecLoc): Result.Result[String] = {
    val loc = Locations.get(spec.file.getId)
    for { path <- loc.relativePath(spec.file.getData) } 
    yield path.toString
  }

}
