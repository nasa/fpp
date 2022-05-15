package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP connection pattern */
case class ConnectionPattern(
  /** The AST node specifying the pattern */
  aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]],
  /** The AST pattern */
  ast: Ast.SpecConnectionGraph.Pattern,
  /** The source instance */
  source: (ComponentInstance, Location),
  /** The target instances */
  targets: Set[(ComponentInstance, Location)]
) {

  def getLoc: Location = Locations.get(aNode._2.id)

}

object ConnectionPattern {

  /** Constructs a connection pattern from an AST node */
  def fromSpecConnectionGraph(
    a: Analysis, 
    aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]],
    pattern: Ast.SpecConnectionGraph.Pattern
  ): Result.Result[ConnectionPattern] = {
    def getInstance(node: AstNode[Ast.QualIdent]) =
      for (instance <- a.getComponentInstance(node.id))
        yield (instance, Locations.get(node.id))
    for {
      source <- getInstance(pattern.source)
      targetList <- Result.map(pattern.targets, getInstance)
    } yield ConnectionPattern(aNode, pattern, source, targetList.toSet)
  }

}
