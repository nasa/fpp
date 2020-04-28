package fpp.compiler.analysis

import fpp.compiler.ast._

/** A matching between a use and its definition */
case class UseDefMatching(
  /** The node Identifier corresponding to the use */
  nodeId: AstNode.Id,
  /** The qualified name appearing in the use */
  qualifiedName: Name.Qualified,
  /** The symbol corresponding to the definition */
  symbol: Symbol
)
