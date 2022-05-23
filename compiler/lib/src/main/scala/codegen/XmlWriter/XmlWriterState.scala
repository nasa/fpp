package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._

/** XML Writer state */
case class XmlWriterState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String = ".",
  /** The list of include prefixes */
  prefixes: List[String] = Nil,
  /** The default string size */
  defaultStringSize: Int = XmlWriterState.defaultDefaultStringSize,
  /** The map from strings to locations */
  locationMap: Map[String, Location] = Map()
) {

  /** A mapping from special built-in types to their
   *  default values */
  val zero: Value.Integer = Value.Integer(0)
  val builtInTypes: Map[String,Value.Integer] = Map(
    "FwBuffSizeType" -> zero,
    "FwChanIdType" -> zero,
    "FwEnumStoreType" -> zero,
    "FwEventIdType" -> zero,
    "FwOpcodeType" -> zero,
    "FwPacketDescriptorType" -> zero,
    "FwPrmIdType" -> zero,
    "FwTimeBaseStoreType" -> zero,
    "FwTimeContextStoreType" -> zero,
    "NATIVE_INT_TYPE" -> zero,
    "NATIVE_UINT_TYPE" -> zero,
    "POINTER_CAST" -> zero,
  )

  /** Removes the longest prefix from a Java path */
  def removeLongestPrefix(path: File.JavaPath): File.JavaPath = 
    File.removeLongestPrefix(prefixes)(path)

  /** Write import directives as lines */
  def writeImportDirectives(usedSymbols: Iterable[Symbol]): List[Line] = {
    def getDirectiveForSymbol(sym: Symbol): Option[String] =
      for {
        tagFileName <- sym match {
          case Symbol.AbsType(aNode) => 
            val symbol = Symbol.AbsType(aNode)
            // Don't import headers for built-in types
            val cppName = writeSymbol(symbol)
            if (builtInTypes.contains(cppName)) None
            else {
              val name = getName(symbol)
              Some("include_header", s"${name}.hpp")
            }
          case Symbol.Array(aNode) => Some(
            "import_array_type",
            XmlWriterState.getArrayFileName(getName(Symbol.Array(aNode)))
          )
          case Symbol.Component(aNode) => Some(
            "import_component_type",
            XmlWriterState.getComponentFileName(getName(Symbol.Component(aNode)))
          )
          case Symbol.Enum(aNode) => Some(
            "import_enum_type",
            XmlWriterState.getEnumFileName(getName(Symbol.Enum(aNode)))
          )
          case Symbol.Port(aNode) => Some(
            "import_port_type",
            XmlWriterState.getPortFileName(getName(Symbol.Port(aNode)))
          )
          case Symbol.Struct(aNode) => Some(
            "import_serializable_type",
            XmlWriterState.getStructFileName(getName(Symbol.Struct(aNode)))
          )
          case _ => None
        }
      }
      yield {
        val loc = sym.getLoc
        val (tagName, fileName) = tagFileName
        val fullPath = loc.getNeighborPath(fileName)
        val path = removeLongestPrefix(fullPath)
        val tags = XmlTags.tags(tagName)
        XmlTags.taggedString(tags)(path.toString)
      }
    val array = usedSymbols.map(getDirectiveForSymbol(_)).
      filter(_.isDefined).map(_.get).toArray
    scala.util.Sorting.quickSort(array)
    array.toList.map(Line(_))
  }

  /** Write an FPP symbol as XML */
  def writeSymbol(sym: Symbol): String = {
    // Skip component names in qualifiers
    // Those appear in the prefixes of definition names
    def removeComponentQualifiers(
      symOpt: Option[Symbol],
      out: List[String]
    ): List[String] = symOpt match {
      case None => out
      case Some(sym) => 
        val psOpt = a.parentSymbolMap.get(sym)
        val out1 = sym match {
          case cs: Symbol.Component => out
          case _ => getName(sym) :: out
        }
        removeComponentQualifiers(psOpt, out1)
    }
    val qualifiedName = sym match {
      // For component symbols, use the qualified name
      case cs: Symbol.Component => a.getQualifiedName(cs)
      // For other symbols, remove component qualifiers
      case _ => {
        val identList = removeComponentQualifiers(Some(sym), Nil)
        Name.Qualified.fromIdentList(identList)
      }
    }
    writeQualifiedName(qualifiedName)
  }

  /** Writes an FPP qualified name as XML */
  def writeQualifiedName(qualifiedName: Name.Qualified): String = {
    qualifiedName.toString.replaceAll("\\.", "::")
  }

  /** Gets the unqualified name associated with a symbol.
   *  If a symbol is defined in a component, then we prefix its name
   *  with the component name. This is to work around the fact that
   *  we cannot define classes inside components in the F Prime XML. */
  def getName(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(cs: Symbol.Component) => s"${cs.getUnqualifiedName}_$name"
      case _ => name
    }
  }

  /** Gets the namespace associated with a symbol */
  def getNamespace(symbol: Symbol): String = {
    def helper(symbolOpt: Option[Symbol], out: String): String = {
      symbolOpt match {
        case None => out
        case Some(symbol) =>
          val ps = a.parentSymbolMap.get(symbol)
          (symbol, out) match {
            // Don't add the enclosing component to the namespace
            case (_: Symbol.Component, _) => helper(ps, out)
            case (_, "") => helper(ps, symbol.getUnqualifiedName)
            case (_, _) => helper(ps, s"${symbol.getUnqualifiedName}::$out")
          }
      }
    }
    helper(a.parentSymbolMap.get(symbol), "")
  }

  /** Gets the namespace and name associated with a symbol */
  def getNamespaceAndName(symbol: Symbol): List[(String, String)] = {
    val namespace = getNamespace(symbol)
    val namePair = ("name", getName(symbol))
    val namespacePair = ("namespace", namespace)
    if (namespace != "") List(namespacePair, namePair) else List(namePair)
  }

}

case object XmlWriterState extends LineUtils {

  /** The default default string size */
  val defaultDefaultStringSize = 80

  /** Gets the generated XML file name for an array definition */
  def getArrayFileName(baseName: String): String = s"${baseName}ArrayAi.xml"

  /** Gets the generated XML file name for an enum definition */
  def getEnumFileName(baseName: String): String = s"${baseName}EnumAi.xml"

  /** Gets the generated XML file name for a component definition */
  def getComponentFileName(baseName: String): String = s"${baseName}ComponentAi.xml"

  /** Gets the generated XML file name for a port definition */
  def getPortFileName(baseName: String): String = s"${baseName}PortAi.xml"

  /** Gets the generated XML file name for a struct definition */
  def getStructFileName(baseName: String): String = s"${baseName}SerializableAi.xml"

  /** Gets the generated XML file name for a topology definition */
  def getTopologyFileName(baseName: String): String = s"${baseName}TopologyAppAi.xml"

  /** Write an XML comment */
  def writeComment(comment: String): List[Line] = 
    Line.addPrefixAndSuffix("<!-- ", lines(comment), " -->")

  /** Write an identifier */
  def writeId(id: Int): String = s"0x${Integer.toString(id, 16).toUpperCase}"

}
