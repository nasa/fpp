package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check system definitions */
object CheckSystemDefs
  extends Analyzer
  with ModuleAnalyzer
{

  private def checkDuplicateDef(
    a: Analysis,
    node: AstNode[Ast.DefSystem]
  ) =
    a.systemOpt match {
      case None => Right(a)
      case Some(s) =>
        val loc = Locations.get(node.id)
        val prevLoc = Locations.get(s.aNode._2.id)
        Left(SemanticError.DuplicateSystemDefinition(loc, prevLoc))
    }

  override def defSystemAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefSystem]]
  ) = {
    val node = aNode._2
    val id = node.data.topology.id
    for {
      _ <- checkDuplicateDef(a, node)
      t <- a.getTopology(id)
      d <- a.getDictionary(id)
    } yield a.copy(systemOpt = Some(FppSystem(aNode, t, d)))
  }

}
