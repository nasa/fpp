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

  /** Removes the longest prefix from a Java path */
  def removeLongestPathPrefix(path: File.JavaPath): File.JavaPath =
    File.removeLongestPrefix(pathPrefixes)(path)

  /** Constructs an include guard from the prefix and a name */
  def includeGuardFromPrefix(name: String) = guardPrefix match {
    case Some(s) => s ++ "_" ++ name
    case None => name
  }

  /** Constructs an include guard from the enclosing namespace and a name */
  def includeGuardFromNamespace(name: String) = {
    a.scopeNameList.reverse.mkString("_") match {
      case "" => name
      case prefix => prefix ++ "_" ++ name
    }
  }

}
