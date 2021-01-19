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
      def helper(sym: Symbol, head: String, tail: List[String]): List[String] = 
        a.parentSymbolMap.get(sym) match {
          case None => head :: tail
          case Some(ps: Symbol.Component) => 
            // Prefix a symbol defined inside a component with the component name
            val head1 = s"${ps.getUnqualifiedName}_$head"
            a.parentSymbolMap.get(ps) match {
              case None => head1 :: tail
              case Some(ps1) => helper(ps1, head1, tail)
            }
          case Some(ps) => helper(ps, ps.getUnqualifiedName, head :: tail)
        }
      helper(sym, sym.getUnqualifiedName, Nil)
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
