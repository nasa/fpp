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

  /** Remove the longest prefix from a Java path */
  def removeLongestPrefix(path: File.JavaPath): File.JavaPath = {
    def removePrefix(s: String) = {
      val prefix = java.nio.file.Paths.get(s)
      if (path.startsWith(prefix)) prefix.relativize(path) else path
    }
    prefixes.map(removePrefix(_)) match {
      case Nil => path
      case head :: tail => {
        def min(p1: File.JavaPath, p2: File.JavaPath) = 
          if (p1.getNameCount < p2.getNameCount) p1 else p2
        tail.fold(head)(min)
      }
    }
  }

  /** Write import directives as lines */
  def writeImportDirectives(currentFile: File): List[Line] = {
    def getDirectiveForSymbol(sym: Symbol): Option[String] =
      for {
        tagFileName <- sym match {
          case Symbol.AbsType(_) => Some(
            "include_header",
            sym.getUnqualifiedName ++ ".hpp"
          )
          case Symbol.Array(aNode) => Some(
            "include_array_type",
            ComputeXmlFiles.getArrayFileName(aNode._2.getData)
          )
          case Symbol.Enum(aNode) => Some(
            "include_enum_type",
            ComputeXmlFiles.getEnumFileName(aNode._2.getData)
          )
          case Symbol.Struct(aNode) => Some(
            "import_serializable_type",
            ComputeXmlFiles.getStructFileName(aNode._2.getData)
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
