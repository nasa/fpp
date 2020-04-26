package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** The analysis data structure */
case class Analysis(
  /** The set of files presented to the analyzer */
  inputFileSet: Set[File] = Set(),
  /** The set of files on which the analysis depends */
  dependencyFileSet: Set[File] = Set(),
  /** The set of dependency files that could not be opened */
  missingDependencyFileSet: Set[File] = Set(),
  /** The set of files included when parsing input */
  includedFileSet: Set[File] = Set(),
  /** A map from pairs (spec loc kind, qualified name) to spec locs. */
  locationSpecifierMap: Map[(Ast.SpecLoc.Kind, Name.Qualified), Ast.SpecLoc] = Map(),
  /** A list of unqualified names representing the enclosing module names,
   *  with the innermost name at the head of the list. For exapmle, inside
   *  module B where B is inside A and A is at the top level, the module name
   *  list is [ B, A ]. */
  moduleNameList: List[Name.Unqualified] = List(),
  /** The current nested scope for symbol lookup */
  nestedScope: NestedScope = NestedScope.empty,
  /** The mapping from symbols with scopes to their scopes */
  symbolScopeMap: Map[Symbol,Scope] = Map(),
  /** The mapping from uses (by node ID) to their definitions */
  useDefMap: Map[AstNode.Id, Symbol] = Map(),
  // TODO
) {


}

object Analysis {



}
