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

  /** CppWriterState for writing C++ */
  val cppWriterState = CppWriterState(a)

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
            if (CppWriterState.builtInTypes.contains(cppName)) None
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
  def writeSymbol(symbol: Symbol): String = cppWriterState.writeSymbol(symbol)

  /** Gets the unqualified name associated with a symbol. */
  def getName(symbol: Symbol): String = cppWriterState.getName(symbol)

  /** Gets the namespace and name associated with a symbol */
  def getNamespaceAndName(symbol: Symbol): List[(String, String)] = {
    val namespace = cppWriterState.getNamespace(symbol)
    val namePair = ("name", getName(symbol))
    namespace match {
      case Some(n) => List(("namespace", n), namePair)
      case None => List(namePair)
    }
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
