package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._


/** Layout Writer State */
case class LayoutWriterState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String = ".",
  /** The map from strings to locations */
  locationMap: Map[String, Location] = Map()
) {

  /** Gets the unqualified name associated with a symbol. */
  def getName(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(cs: Symbol.Component) => s"${cs.getUnqualifiedName}_$name"
      case _ => name
    }
  }

}

case object LayoutWriterState {

  /** Gets the layout directory name for a topology definition */
  def getTopologyDirectoryName(baseName: String): String = s"${baseName}Layout"

  /** Gets the text filename for a connection graph */
  def getConnectionGraphFileName(baseName: String): String = s"${baseName}.txt"

}