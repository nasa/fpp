package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._


/** Dictionary JSON Encoder state */
case class DictionaryJsonEncoderState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String = ".",
  /** The list of include prefixes */
  prefixes: List[String] = Nil,
  /** The default string size */
  defaultStringSize: Int = DictionaryJsonEncoderState.defaultDefaultStringSize,
  /** The default bool size */
  boolSize: Int = DictionaryJsonEncoderState.boolSize,
  /** The Dictionary metadata */
  metadata: DictionaryMetadata,
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

case object DictionaryJsonEncoderState {

  /** The default default string size */
  // move out since its shared between cpp and json
  val defaultDefaultStringSize = 80

  /** The default default bool size */
  val boolSize = 8

  /** Gets the generated XML file name for a topology definition */
  def getTopologyFileName(baseName: String): String = s"${baseName}TopologyDictionary.json"

}