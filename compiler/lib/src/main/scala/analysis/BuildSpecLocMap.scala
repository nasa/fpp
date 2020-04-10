package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Build the location specifier map 
 *  Prerequisites: Empty module name list and location specifier map */
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
        val map = a.locationSpecifierMap + (key -> specNode)
        Right(a.copy(locationSpecifierMap = map))
      }
      case Some(specNode1) => 
        for { _ <- checkPathConsistency(specNode, specNode1) } yield a
    }
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

  private def checkPathConsistency(
    specNode1: AstNode[Ast.SpecLoc],
    specNode2: AstNode[Ast.SpecLoc]
  ): Result.Result[Unit] = {
    for {
      path1 <- getPathString(specNode1)
      path2 <- getPathString(specNode2)
      _ <- if (path1 == path2) Right(()) else {
          val e = SpecLocError.Inconsistent(
          Locations.get(specNode1.getId),
          path1,
          Locations.get(specNode2.getId),
          path2
        )
        Left(e)
      }
    } yield ()
  }

  private def getPathString(node: AstNode[Ast.SpecLoc]): Result.Result[String] = {
    val loc = Locations.get(node.getId)
    for { path <- loc.relativePath(node.getData.file) } 
    yield path.toString
  }

}
