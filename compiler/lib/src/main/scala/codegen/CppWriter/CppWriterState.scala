package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._

/** C++ Writer state */
case class CppWriterState(
  /** The result of semantic analysis */
  a: Analysis,
  /** The output directory */
  dir: String,
  /** The include guard prefix */
  guardPrefix: Option[String],
  /** The list of include path prefixes */
  pathPrefixes: List[String],
  /** The default string size */
  defaultStringSize: Int,
) {

  /** Adds the component name prefix to a name.
   *  This is to work around the fact that we can't declare
   *  constants inside component classes, because we are using
   *  F Prime XML to generate the component classes. */
  def addComponentNamePrefix(symbol: Symbol): String = {
    val name = symbol.getUnqualifiedName
    a.parentSymbolMap.get(symbol) match {
      case Some(componentSymbol: Symbol.Component) =>
        val componentName = componentSymbol.getUnqualifiedName
        s"${componentName}_$name"
      case _ => name
    }
  }

  /** Removes the longest prefix from a Java path */
  def removeLongestPathPrefix(path: File.JavaPath): File.JavaPath =
    File.removeLongestPrefix(pathPrefixes)(path)

  /** Gets the relative path for a file */
  def getRelativePath(fileName: String): File.JavaPath = {
    val path = java.nio.file.Paths.get(fileName).toAbsolutePath.normalize
    removeLongestPathPrefix(path)
  }

  /** Constructs an include guard from the prefix and a name */
  def includeGuardFromPrefix(name: String) = {
    val guard = guardPrefix match {
      case Some(s) => s"${s}_$name"
      case None => name
    }
    s"${guard}_HPP"
  }

  /** Constructs a C++ identifier from a qualified symbol name */
  def identFromQualifiedSymbolName(s: Symbol) =
    CppWriter.identFromQualifiedName(a.getQualifiedName(s))

  /** Constructs an include guard from a qualified name and a kind */
  def includeGuardFromQualifiedName(s: Symbol, name: String) = {
    val guard = a.getEnclosingNames(s) match {
      case Nil => name
      case names => 
        val prefix = CppWriter.identFromQualifiedName(
          Name.Qualified.fromIdentList(names)
        )
        s"${prefix}_$name"
    }
    s"${guard}_HPP"
  }

  /** Gets the C++ namespace associated with a symbol */
  def getNamespace(symbol: Symbol): Option[String] =
    a.parentSymbolMap.get(symbol).map(
      s => CppWriter.translateQualifiedName(a.getQualifiedName(s))
    )

}
