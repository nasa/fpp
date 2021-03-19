package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP connection pattern */
case class ConnectionPattern(
  /** The AST node specifying the pattern */
  aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]],
  /** The AST pattern */
  pattern: Ast.SpecConnectionGraph.Pattern,
  /** The source instance */
  source: ComponentInstance,
  /** The target instances */
  targets: Set[ComponentInstance]
)

object ConnectionPattern {

  /** Constructs a connection pattern from an AST node */
  def fromSpecConnectionGraph(
    a: Analysis, 
    aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]],
    pattern: Ast.SpecConnectionGraph.Pattern
  ): Result.Result[ConnectionPattern] = {
    def getInstance(node: AstNode[Ast.QualIdent]) =
      a.getComponentInstance(node.id)
    for {
      source <- getInstance(pattern.source)
      targetList <- Result.map(pattern.targets, getInstance)
    } yield ConnectionPattern(aNode, pattern, source, targetList.toSet)
  }

}
