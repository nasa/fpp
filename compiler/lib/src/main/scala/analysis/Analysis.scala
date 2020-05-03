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
  /** The set of symbols visited so far */
  visitedSymbolSet: Set[Symbol] = Set(),
  /** The set of symbols on the current use-def path.
   *  Used during cycle analysis. */
  useDefSymbolSet: Set[Symbol] = Set(),
  /* The list of use-def matchings on the current use-def path.
   * Used during cycle analysis. */
  useDefMatchingList: List[UseDefMatching] = List(),
  /* The mapping from type and constant symbols, expressions,
   * and type names to their types */
  typeMap: Map[AstNode.Id, Type] = Map(),
  // TODO
) {

  /** Add a mapping to the type map */
  def assignType[T](mapping: (AstNode[T], Type)): Analysis = {
    val node -> t = mapping
    this.copy(typeMap = this.typeMap + (node.getId -> t))
  }

  /** Compute the common type for a list of node Ids */
  def commonType(nodes: List[AstNode.Id], emptyListError: Error): Result.Result[Type] = {
    def helper(prevNodeId: AstNode.Id, prevType: Type, nextNodes: List[AstNode.Id]): Result.Result[Type] = {
      nextNodes match {
        case Nil => Right(prevType)
        case head :: tail => {
          val currentType = this.typeMap(head)
          val loc = Locations.get(prevNodeId)
          Analysis.commonType(prevType, currentType, loc) match {
            case error @ Left(_) => error
            case Right(t) => helper(head, t, tail)
          }
        }
      }
    }
    nodes match {
      case Nil => Left(emptyListError)
      case firstNodeId :: rest => {
        val firstType = this.typeMap(firstNodeId)
        helper(firstNodeId, firstType, rest)
      }
    }
  }

  /** Compute the common type for two node Ids */
  def commonType(id1: AstNode.Id, id2: AstNode.Id, errorLoc: Location): Result.Result[Type] = {
    val t1 = this.typeMap(id1)
    val t2 = this.typeMap(id2)
    Analysis.commonType(t1, t2, errorLoc)
  }

}

object Analysis {

  /** Compute the common type for two types */
  def commonType(t1: Type, t2: Type, errorLoc: Location): Result.Result[Type] =
    Type.commonType(t1, t2) match {
      case None => {
        val msg = s"cannot compute common type of $t1 and $t2"
        Left(SemanticError.TypeMismatch(errorLoc, msg))
      }
      case Some(t) => Right(t)
    }

}
