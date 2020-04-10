package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Build the location specifier map */
object BuildSpecLocMap extends AstStateVisitor {

  type State = Analysis

  override def defModuleAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefModule(name, members) = node1.getData
    val a1 = a.copy(moduleNameList = name :: a.moduleNameList)
    for { a2 <- visitList(a1, members, matchModuleMember) }
    yield a2.copy(moduleNameList = a.moduleNameList)
  }

  override def specLocAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ) = {
    val specNode = node._2
    val spec = specNode.getData
    val symbol = spec.symbol.getData
    val qualifiedName = Name.Qualified.fromIdentList(symbol.reverse ++ a.moduleNameList)
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

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

  private def checkPathConsistency(
    spec1: Ast.SpecLoc,
    spec2: Ast.SpecLoc
  ): Result.Result[Unit] = {
    for {
      path1 <- getPathString(spec1)
      path2 <- getPathString(spec2)
      _ <- if (path1 == path2) Right(()) else {
          val e = SpecLocError.Inconsistent(
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
