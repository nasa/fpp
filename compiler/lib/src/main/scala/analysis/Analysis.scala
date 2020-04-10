package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** The analysis data structure */
case class Analysis(
  /** The set of files presented to the analyzer */
  inputFileSet: Set[File] = Set(),
  /** The set of files on which the analysis depends */
  dependencyFileSet: Set[File] = Set(),
  /** The set of files included when parsing input */
  includedFileSet: Set[File] = Set(),
  /** A map from pairs (spec loc kind, qualified name) to spec locs. */
  locationSpecifierMap: Map[(Ast.SpecLoc.Kind, Name.Qualified), AstNode[Ast.SpecLoc]] = Map(),
  /** A list of unqualified names representing the enclosing module scopes,
   *  with the innermost name at the head of the list. For exapmle, inside
   *  module B where B is inside A and A is at the top level, the module name
   *  list is [ B, A ]. */
  moduleNameList: List[Name.Unqualified] = List(),
  // TODO
) {


}

object Analysis {



}
