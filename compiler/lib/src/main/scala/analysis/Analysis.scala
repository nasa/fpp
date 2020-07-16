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
  /** A list of unqualified names representing the enclosing scope names,
   *  with the innermost name at the head of the list. For exapmle, inside
   *  module B where B is inside A and A is at the top level, the module name
   *  list is [ B, A ]. */
  scopeNameList: List[Name.Unqualified] = List(),
  /** The current nested scope for symbol lookup */
  nestedScope: NestedScope = NestedScope.empty,
  /** The mapping from symbols to their qualified names */
  qualifiedNameMap: Map[Symbol,Name.Qualified] = Map(),
  /** The mapping from symbols with scopes to their scopes */
  symbolScopeMap: Map[Symbol,Scope] = Map(),
  /** The mapping from uses (by node ID) to their definitions */
  useDefMap: Map[AstNode.Id, Symbol] = Map(),
  /** The set of symbols visited so far */
  visitedSymbolSet: Set[Symbol] = Set(),
  /** The set of symbols on the current use-def path.
   *  Used during cycle analysis. */
  useDefSymbolSet: Set[Symbol] = Set(),
  /** The list of use-def matchings on the current use-def path.
   *  Used during cycle analysis. */
  useDefMatchingList: List[UseDefMatching] = List(),
  /** The mapping from type and constant symbols, expressions,
   *  and type names to their types */
  typeMap: Map[AstNode.Id, Type] = Map(),
  /** THe mapping from constant symbols and expressions to their values. */
  valueMap: Map[AstNode.Id, Value] = Map(),
  /** The set of symbols used. Used during code generation. */
  usedSymbolSet: Set[Symbol] = Set(),
) {

  /** Add a mapping to the type map */
  def assignType[T](mapping: (AstNode[T], Type)): Analysis = {
    val node -> t = mapping
    this.copy(typeMap = this.typeMap + (node.getId -> t))
  }

  /** Add a value to the value map */
  def assignValue[T](mapping: (AstNode[T], Value)): Analysis = {
    val node -> v = mapping
    this.copy(valueMap = this.valueMap + (node.getId -> v))
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

  /** Add two noes */
  def add(id1: AstNode.Id, id2: AstNode.Id): Value = {
    val v1 = valueMap(id1)
    val v2 = valueMap(id2)
    v1 + v2 match {
      case Some(v) => v
      case None => throw InternalError("addition failed")
    }
  }

  /** Subtract one node from another */
  def sub(id1: AstNode.Id, id2: AstNode.Id): Value = {
    val v1 = valueMap(id1)
    val v2 = valueMap(id2)
    v1 - v2 match {
      case Some(v) => v
      case None => throw InternalError("subtraction failed")
    }
  }

  /** Multiply two nodes */
  def mul(id1: AstNode.Id, id2: AstNode.Id): Value = {
    val v1 = valueMap(id1)
    val v2 = valueMap(id2)
    v1 * v2 match {
      case Some(v) => v
      case None => throw InternalError("multiplication failed")
    }
  }

  /** Divide one node by another */
  def div(id1: AstNode.Id, id2: AstNode.Id): Result.Result[Value] = {
    val v1 = valueMap(id1)
    val v2 = valueMap(id2)
    if (v2.isZero) {
      val loc = Locations.get(id2)
      Left(SemanticError.DivisionByZero(loc))
    }
    else {
      v1 / v2 match {
        case Some(v) => Right(v)
        case None => throw InternalError("division failed")
      }
    }
  }

  /** Negate a value */
  def neg(id: AstNode.Id): Value = {
    val v = valueMap(id)
    -v match {
        case Some(v) => v
        case None => throw InternalError("negation failed")
      }
    }

  /** Computes a short qualified name
   *  Deletes the longest prefix provided by the enclosing scope */
  def shortName(name: Name.Qualified): Name.Qualified = {
    def helper(prefix: List[String], resultList: List[String]): Name.Qualified  = {
      val result = Name.Qualified.fromIdentList(resultList)
      (prefix, resultList) match {
        case (Nil, _) => result
        case (_, _ :: Nil) => result
        case (head1 :: tail1, head2 :: tail2) => 
          if (head1 == head2) helper(tail1, tail2)
          else name
        case _ => name
      }
    }
    helper(scopeNameList.reverse, name.toIdentList)
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

  /** Check for duplicate struct member */
  def checkForDuplicateStructMember[T]
    (getName: T => Name.Unqualified)
    (nodes: List[AstNode[T]]): Result.Result[Unit] = {
    def helper(
      nodes: List[AstNode[T]],
      map: Map[Name.Unqualified, AstNode.Id],
    ): Result.Result[Unit] = {
      nodes match {
        case Nil => Right(())
        case node :: tail => {
          val data = node.data
          val name = getName(data)
          map.get(name) match {
            case None => helper(tail, map + (name -> node.getId))
            case Some(id) => {
              val loc = Locations.get(node.getId)
              val prevLoc = Locations.get(id)
              Left(SemanticError.DuplicateStructMember(name, loc, prevLoc))
            }
          }
        }
      }
    }
    helper(nodes, Map())
  }

  /** Convert one type to another */
  def convertTypes(loc: Location, pair: (Type, Type)): Result.Result[Type] = {
    val (t1 -> t2) = pair
    t1.isConvertibleTo(t2) match {
      case true => Right(t2)
      case false => Left(SemanticError.TypeMismatch(loc, s"cannot convert $t1 to $t2"))
    }
  }

  /** Convert a value to a type */
  def convertValueToType(v: Value, t: Type): Value =
    v.convertToType(t) match {
      case Some(v) => v
      case None => throw InternalError(s"cannot convert value $v to type $t")
    }

}
