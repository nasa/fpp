package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check system definitions */
object CheckSystemDefs
  extends Analyzer
  with ModuleAnalyzer
{

  private def checkNoDuplicateDef(
    a: Analysis,
    symbol: Symbol.System
  ) =
    a.systemMap.keys.toList match
      case Nil => Right(a)
      case prevSymbol :: _ =>
        val loc = Locations.get(symbol.node._2.id)
        val prevLoc = Locations.get(prevSymbol.node._2.id)
        Left(SemanticError.DuplicateSystemDefinition(loc, prevLoc))

  private def checkDeploymentTopology(ss: Symbol.System, t: Topology) = {
    val loc = Locations.get(ss.getNodeId)
    if t.aNode._2.data.isDeployment then Right(())
    else 
      val defLoc = Locations.get(t.aNode._2.id)
      Left(
        SemanticError.InvalidSymbol(
          t.getName,
          loc,
          "topology used here must be a deployment topology",
          defLoc
        )
      )
  }

  override def defSystemAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefSystem]]
  ) = {
    val node = aNode._2
    val id = node.data.topology.id
    val ss = Symbol.System(aNode)
    for {
      _ <- checkNoDuplicateDef(a, ss)
      t <- a.getTopology(id)
      _ <- checkDeploymentTopology(ss, t)
      d <- a.getDictionary(id)
    } yield a.copy(systemMap = Map(ss -> FppSystem(aNode, t, d)))
  }

}
