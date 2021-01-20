package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._

/** XML Writer state */
case class XmlWriterState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String,
  /** The list of include prefixes */
  prefixes: List[String],
  /** The default string size */
  defaultStringSize: Int,
  /** The map from strings to locations */
  locationMap: Map[String, Location] = Map()
) {

  /** Removes the longest prefix from a Java path */
  def removeLongestPrefix(path: File.JavaPath): File.JavaPath = 
    File.removeLongestPrefix(prefixes)(path)

  /** Write import directives as lines */
  def writeImportDirectives: List[Line] = {
    def getDirectiveForSymbol(sym: Symbol): Option[String] =
      for {
        tagFileName <- sym match {
          case Symbol.AbsType(_) => Some(
            "include_header",
            sym.getUnqualifiedName ++ ".hpp"
          )
          case Symbol.Array(aNode) => Some(
            "import_array_type",
            XmlWriterState.getArrayFileName(getName(Symbol.Array(aNode)))
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
        val fullPath = loc.file match {
          case File.Path(p) => {
            val dir = p.getParent
            java.nio.file.Paths.get(dir.toString, fileName)
          }
          case _ => java.nio.file.Paths.get(fileName).toAbsolutePath
        }
        val path = removeLongestPrefix(fullPath)
        val tags = XmlTags.tags(tagName)
        XmlTags.taggedString(tags)(path.toString)
      }
    val set = a.usedSymbolSet.map(getDirectiveForSymbol(_)).filter(_.isDefined).map(_.get)
    val array = set.toArray
    scala.util.Sorting.quickSort(array)
    array.toList.map(Line(_))
  }

  /** Write an FPP symbol as XML */
  def writeSymbol(sym: Symbol): String = {
    val identList = {
      def helper(symOpt: Option[Symbol], out: List[String]): List[String] =
        symOpt match {
          case None => out
          case Some(sym) => 
            val psOpt = a.parentSymbolMap.get(sym)
            val out1 = sym match {
              // Skip component symbol names
              // Those appear in the prefixes of definition names
              case cs: Symbol.Component => out
              case _ => getName(sym) :: out
            }
            helper(psOpt, out1)
        }
      helper(Some(sym), Nil)
    }
    val qualifiedName = Name.Qualified.fromIdentList(identList)
    val shortName = a.shortName(qualifiedName)
    shortName.toString.replaceAll("\\.", "::")
  }

  /** Gets the name associated with a symbol.
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

case object XmlWriterState {

  /** Gets the generated XML file name for an array definition */
  def getArrayFileName(baseName: String) = baseName ++ "ArrayAi.xml"

  /** Gets the generated XML file name for an enum definition */
  def getEnumFileName(baseName: String) = baseName ++ "EnumAi.xml"

  /** Gets the generated XML file name for a component definition */
  def getComponentFileName(baseName: String) = baseName ++ "ComponentAi.xml"

  /** Gets the generated XML file name for a port definition */
  def getPortFileName(baseName: String) = baseName ++ "PortAi.xml"

  /** Gets the generated XML file name for a struct definition */
  def getStructFileName(baseName: String) = baseName ++ "SerializableAi.xml"

}
