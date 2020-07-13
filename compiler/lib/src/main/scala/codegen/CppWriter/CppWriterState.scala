package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._

/** C++ Writer state */
case class CppWriterState(
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

}
