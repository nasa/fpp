package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._


/** Dictionary JSON Encoder state */
case class DictionaryJsonEncoderState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String = ".",
  /** The default bool size */
  boolSize: Int = DictionaryJsonEncoderState.boolSize,
  /** The Dictionary metadata */
  metadata: DictionaryMetadata = DictionaryMetadata(),
  /** The map from strings to locations */
  locationMap: Map[String, Location] = Map()
) {

  /** Gets the unqualified name associated with a symbol. */
  def getName(symbol: Symbol): String = symbol.getUnqualifiedName

  def getFwDefaultStringSize: BigInt = {
    val s = a.frameworkDefinitions.constantMap("FW_FIXED_LENGTH_STRING_SIZE")
    a.valueMap(s.getNodeId) match {
      case Value.Integer(value) => value
      case _ => throw InternalError("expected integer value")
    }
  }

  /** Writes the name of a symbol */
  def writeSymbolName(symbol: Symbol, separator: String = "_") =
    a.getQualifiedName(symbol).toString.replaceAll("\\.", separator)

}

case object DictionaryJsonEncoderState {

  /** The default bool size */
  val boolSize = 8

  /** Gets the generated JSON file name for a topology definition */
  def getTopologyFileName(baseName: String): String = s"${baseName}TopologyDictionary.json"

  /** Gets the generated JSON file name for a system definition */
  def getSystemFileName(baseName: String): String = s"${baseName}SystemDictionary.json"

}
