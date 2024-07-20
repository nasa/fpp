package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** The interface for an FPP symbol */
trait SymbolInterface {

  /** Gets the location of the symbol */
  final def getLoc: Location = Locations.get(getNodeId)

  /** Gets the AST node ID of the symbol */
  def getNodeId: AstNode.Id

  /** Gets the unqualified name of the symbol */
  def getUnqualifiedName: Name.Unqualified

}
