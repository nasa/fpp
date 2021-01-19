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
) {

  /** Get the name of a symbol.
   *  If a symbol is defined in a component, then prefix its name
   *  with the component name. */
  def getSymbolName(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(cs: Symbol.Component) => s"${cs.getUnqualifiedName}_$name"
      case _ => name
    }
  }

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
            ComputeXmlFiles.getArrayFileName(aNode._2.data.name)
          )
          case Symbol.Enum(aNode) => Some(
            "import_enum_type",
            ComputeXmlFiles.getEnumFileName(aNode._2.data.name)
          )
          case Symbol.Port(aNode) => Some(
            "import_port_type",
            ComputeXmlFiles.getPortFileName(aNode._2.data.name)
          )
          case Symbol.Struct(aNode) => Some(
            "import_serializable_type",
            ComputeXmlFiles.getStructFileName(aNode._2.data.name)
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
              case _ => getSymbolName(sym) :: out
            }
            helper(psOpt, out1)
        }
      helper(Some(sym), Nil)
    }
    val qualifiedName = Name.Qualified.fromIdentList(identList)
    val shortName = a.shortName(qualifiedName)
    shortName.toString.replaceAll("\\.", "::")
  }

  /** Get the enclosing namespace */
  def getNamespace: String = a.scopeNameList.reverse match {
    case Nil => ""
    case head :: Nil => head
    case head :: tail => tail.foldLeft(head)({ case (s, name) => s ++ "::" ++ name })
  }

  /** Get the enclosing namespace and the name */
  def getNamespaceAndName(name: String): List[(String, String)] = {
    val namespace = this.getNamespace
    val namePair = ("name", name)
    val namespacePair = ("namespace", namespace)
    if (namespace != "") List(namespacePair, namePair) else List(namePair)
  }

}
