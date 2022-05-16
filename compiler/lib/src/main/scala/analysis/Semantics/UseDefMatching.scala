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
) {

  override def toString = {
    val useLoc = Locations.get(nodeId)
    val defLoc = symbol.getLoc
    val defName = symbol.getUnqualifiedName
    s"use ${qualifiedName} at ${useLoc.file}: ${useLoc.pos} refers to definition ${defName} at ${defLoc.file}: ${defLoc.pos}"
  }

}
