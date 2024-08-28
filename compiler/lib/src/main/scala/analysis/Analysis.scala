package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** The analysis data structure */
case class Analysis(
  /** The set of files presented to the analyzer */
  inputFileSet: Set[File] = Set(),
  /** The recursive level of the analysis */
  level: Int = 0,
  /** The set of files on which the analysis transitively depends.
   *  Does not contain included files. */
  dependencyFileSet: Set[File] = Set(),
  /** The set of files on which the analysis directly depends.
   *  Does contain included files. */
  directDependencyFileSet: Set[File] = Set(),
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
  /** The current parent symbol */
  parentSymbol: Option[Symbol] = None,
  /** The mapping from symbols to their parent symbols */
  parentSymbolMap: Map[Symbol,Symbol] = Map(),
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
  /** The map from component symbols to components */
  componentMap: Map[Symbol.Component, Component] = Map(),
  /** The component under construction */
  component: Option[Component] = None,
  /** The map from component instance symbols to component instances */
  componentInstanceMap: Map[Symbol.ComponentInstance, ComponentInstance] = Map(),
  /** The component instance under construction */
  componentInstance: Option[ComponentInstance] = None,
  /** The map from topology symbols to topologies */
  topologyMap: Map[Symbol.Topology, Topology] = Map(),
  /** The topology under construction */
  topology: Option[Topology] = None,
  /** The map from state machine symbols to state machines */
  stateMachineMap: Map[Symbol.StateMachine, StateMachine] = Map(),
) {

  /** Gets the qualified name of a symbol */
  val getQualifiedName = Analysis.getQualifiedNameFromMap (parentSymbolMap)

  /** Gets the list of enclosing identifiers for a symbol */
  def getEnclosingNames(s: Symbol): List[Ast.Ident] =
    parentSymbolMap.get(s) match {
      case Some(s1) => getQualifiedName(s1).toIdentList
      case None => Nil
    }

  /** Add a mapping to the type map */
  def assignType[T](mapping: (AstNode[T], Type)): Analysis = {
    val node -> t = mapping
    this.copy(typeMap = this.typeMap + (node.id -> t))
  }

  /** Add a value to the value map */
  def assignValue[T](mapping: (AstNode[T], Value)): Analysis = {
    val node -> v = mapping
    this.copy(valueMap = this.valueMap + (node.id -> v))
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

  /** Gets a component from the component map */
  def getComponent(id: AstNode.Id): Result.Result[Component] =
    this.useDefMap(id) match {
      case cs: Symbol.Component => Right(this.componentMap(cs))
      case s => Left(
        SemanticError.InvalidSymbol(
          s.getUnqualifiedName,
          Locations.get(id),
          "not a component symbol",
          s.getLoc
        )
      )
    }

  /** Gets a component instance symbol from the use-def map */
  def getComponentInstanceSymbol(id: AstNode.Id):
  Result.Result[Symbol.ComponentInstance] =
    this.useDefMap(id) match {
      case cis: Symbol.ComponentInstance => Right(cis)
      case s => Left(
        SemanticError.InvalidSymbol(
          s.getUnqualifiedName,
          Locations.get(id),
          "not a component instance symbol",
          s.getLoc
        )
      )
    }

  /** Gets a component instance from the component instance map */
  def getComponentInstance(id: AstNode.Id): Result.Result[ComponentInstance] =
    for (cis <- getComponentInstanceSymbol(id))
      yield this.componentInstanceMap(cis)

  /** Gets a topology symbol from use-def map */
  def getTopologySymbol(id: AstNode.Id): Result.Result[Symbol.Topology] =
    this.useDefMap(id) match {
      case ts: Symbol.Topology => Right(ts)
      case s => Left(
        SemanticError.InvalidSymbol(
          s.getUnqualifiedName,
          Locations.get(id),
          "not a topology symbol",
          s.getLoc
        )
      )
    }

  /** Gets a topology from the topology map */
  def getTopology(id: AstNode.Id): Result.Result[Topology] =
    for (ts <- getTopologySymbol(id))
      yield this.topologyMap(ts)

  /** Gets a BigInt value from an AST node */
  def getBigIntValue(id: AstNode.Id): BigInt = {
    val Value.Integer(v) = Analysis.convertValueToType(
      valueMap(id),
      Type.Integer
    )
    v
  }

  /** Gets an int value from an AST node */
  def getIntValue(id: AstNode.Id): Result.Result[Int] = {
    val v = getBigIntValue(id)
    if (v >= Int.MinValue && v <= Int.MaxValue) {
      Right(v.intValue)
    }
    else {
      val loc = Locations.get(id)
      Left(SemanticError.InvalidIntValue(loc, v, "value out of range"))
    }
  }

  /** Gets a nonnegative BigInt value from an AST node */
  def getNonnegativeBigIntValue(id: AstNode.Id): Result.Result[BigInt] = {
    val v = getBigIntValue(id)
    if (v >= 0) Right(v)
      else Left(SemanticError.InvalidIntValue(
        Locations.get(id),
        v,
        "value may not be negative"
      ))
  }

  /** Gets a nonnegative int value from an AST node */
  def getNonnegativeIntValue(id: AstNode.Id): Result.Result[Int] =
    for {
      v <- getIntValue(id)
      v <- if (v >= 0) Right(v)
           else Left(SemanticError.InvalidIntValue(
             Locations.get(id),
             v,
             "value may not be negative"
           ))
    }
    yield v

  /** Gets an optional BigInt value from an AST node */
  def getBigIntValueOpt[T](nodeOpt: Option[AstNode[T]]): Option[BigInt] =
    nodeOpt.map((node: AstNode[T]) => getBigIntValue(node.id))

  /** Gets an optional int value from an AST node */
  def getIntValueOpt[T](nodeOpt: Option[AstNode[T]]): Result.Result[Option[Int]] =
    Result.mapOpt(nodeOpt, (node: AstNode[T]) => getIntValue(node.id))

  /** Gets an optional nonnegative BigInt value from an AST ndoe */
  def getNonnegativeBigIntValueOpt[T](nodeOpt: Option[AstNode[T]]):
    Result.Result[Option[BigInt]] =
    Result.mapOpt(nodeOpt, (node: AstNode[T]) => getNonnegativeBigIntValue(node.id))

  /** Gets an optional nonnegative int value from an AST ndoe */
  def getNonnegativeIntValueOpt[T](nodeOpt: Option[AstNode[T]]): Result.Result[Option[Int]] =
    Result.mapOpt(nodeOpt, (node: AstNode[T]) => getNonnegativeIntValue(node.id))

  /** Gets a bounded array size from an AST node */
  def getBoundedArraySize(id: AstNode.Id): Result.Result[Int] =
    for {
      v <- getIntValue(id)
      size <- if (v >= 1 && v <= Error.maxArraySize)
        Right(v.intValue) else {
          val loc = Locations.get(id)
          Left(SemanticError.InvalidArraySize(loc, v))
        }
    }
    yield size

  /** Gets an optional unbounded array size */
  def getUnboundedArraySizeOpt[T](nodeOpt: Option[AstNode[T]]): Result.Result[Option[Int]] =
    Result.mapOpt(nodeOpt, (node: AstNode[T]) => getUnboundedArraySize(node.id))

  /** Gets an unbounded array size from an AST node */
  def getUnboundedArraySize(id: AstNode.Id): Result.Result[Int] =
    for {
      v <- getIntValue(id)
      size <- if (v >= 1)
        Right(v.intValue) else {
          val loc = Locations.get(id)
          Left(SemanticError.InvalidArraySize(loc, v))
        }
    }
    yield size

  /** Checks whether a port instance is the specified general port */
  def isGeneralPort(
    pi: PortInstance,
    direction: PortInstance.Direction,
    portTypeName: String
  ): Boolean = {
    (pi.getType, pi.getDirection) match {
      case (
        Some(PortInstance.Type.DefPort(s)),
        Some(d)
      ) => getQualifiedName(s).toString == portTypeName &&
           d == direction
      case _ => false
    }
  }

  /** Gets the reason for a non-displayable type at an AST node id
   *
   *  The id must identify an AST node with a non-displayable type,
   *  or the function will intentionally crash. */
  def getReasonForNonDisplayableTypeAt(id: AstNode.Id): String = {
    def getElementReason(id: AstNode.Id): String = {
      val reason = getReasonForNonDisplayableTypeAt(id)
      s"\n\n${Locations.get(id)}\nbecause this type is not displayable$reason"
    }
    this.typeMap(id) match {
      case a: Type.Array =>
        val id = a.node._2.data.eltType.id
        getElementReason(id)
      case s: Type.Struct =>
        val idOpt = s.node._2.data.members.map(_._2.data.typeName.id).find(
          id => !this.typeMap(id).isDisplayable
        )
        idOpt.map(getElementReason).getOrElse(
          throw new InternalError(
            "a non-displayable struct type must have a non-displayable member type"
          )
        )
      case t =>
        t.getDefNodeId.map(id => s"\n\n${Locations.get(id)}\nType is defined here").getOrElse(
          throw new InternalError(
            "a non-displayable type at an AST node ID must have a definition"
          )
        )
    }
  }

  /** Checks for a displayable type */
  def checkDisplayableType(id: AstNode.Id, errorMsg: String): Result.Result[Unit] = {
    val loc = Locations.get(id)
    val t = this.typeMap(id)
    if (t.isDisplayable) Right(())
    else {
      val reason = getReasonForNonDisplayableTypeAt(id)
      Left(SemanticError.InvalidType(loc, s"$errorMsg$reason"))
    }
  }

  /** Checks that all parameters in a formal param list are displayable */
  def checkDisplayableParams(nodes: Ast.FormalParamList, errorMsg: String): Result.Result[Unit] = {
    Result.foldLeft (nodes) (()) ((result, aNode) =>
      checkDisplayableType(aNode._2.data.typeName.id, errorMsg)
    )
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

  /** Check for duplicate node */
  def checkForDuplicateNode[T]
    (getName: T => Name.Unqualified)
    (error: (String, Location, Location) => Error)
    (nodes: List[AstNode[T]]): Result.Result[Unit] = {
    val result: Result.Result[Map[Name.Unqualified, AstNode.Id]] = Right(Map())
    for {
      _ <- nodes.foldLeft(result)( (result, node) => {
        val Right(map) = result
        val name = getName(node.data)
        map.get(name) match {
          case None => Right(map + (name -> node.id))
          case Some(id) => {
            val loc = Locations.get(node.id)
            val prevLoc = Locations.get(id)
            Left(error(name, loc, prevLoc))
          }
        }
      } )
    } yield ()
  }

  /** Check for duplicate struct member */
  def checkForDuplicateStructMember[T]
    (getName: T => Name.Unqualified)
    (nodes: List[AstNode[T]]): Result.Result[Unit] =
    checkForDuplicateNode (getName) (SemanticError.DuplicateStructMember.apply) (nodes)

  /** Check for duplicate parameter */
  def checkForDuplicateParameter(nodes: Ast.FormalParamList): Result.Result[Unit] = {
    def getName(param: Ast.FormalParam) = param.name
    checkForDuplicateNode (getName) (SemanticError.DuplicateParameter.apply) (nodes.map(_._2))
  }

  /** Check that int value is nonnegative */
  def checkForNegativeValue(id: AstNode.Id, v: Int): Result.Result[Int] = {
    val loc = Locations.get(id)
    if (v >= 0) Right(v) else
    Left(SemanticError.InvalidIntValue(loc, v, "value may not be negative"))
  }

  /** Compute a format from a format string and a list of types */
  def computeFormat(node: AstNode[String], ts: List[Type]): Result.Result[Format] = {
    val loc = Locations.get(node.id)
    def checkSize(format: Format) =
      if (format.fields.size < ts.size)
        Left(SemanticError.InvalidFormatString(loc, "missing replacement field"))
      else if (format.fields.size > ts.size)
        Left(SemanticError.InvalidFormatString(loc, "too many replacement fields"))
      else Right(())
    def checkNumericField(pair: (Type, Format.Field)) = {
      val (t, field) = pair
      val loc = Locations.get(node.id)
      for {
        _ <- if (field.isInteger && !t.isInt)
               Left(SemanticError.InvalidFormatString(loc, s"$t is not an integer type"))
             else Right(())
        _ <- field match {
               case Format.Field.Rational(Some(precision), _) =>
                 if (precision > Format.Field.Rational.maxPrecision)
                   Left(
                     SemanticError.InvalidFormatString(
                       loc,
                       s"precision value $precision is out of range"
                     )
                   )
                 else Right(())
               case _ => Right(())
             }
        _ <- if (field.isRational && !t.isFloat)
               Left(SemanticError.InvalidFormatString(loc, s"$t is not a floating-point type"))
             else Right(())
      }
      yield ()
    }
    for {
      format <- Format.Parser.parseNode(node)
      _ <- checkSize(format)
      _ <- Result.map(ts zip format.fields.map(_._1), checkNumericField)
    } yield format
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

  /** Gets the number of ref params in a formal param list */
  def getNumRefParams(params: Ast.FormalParamList): Int =
    params.filter(aNode => {
      val param = aNode._2.data
      param.kind == Ast.FormalParam.Ref
    }).size

  /** Gets a queue full behavior from an AST node */
  def getQueueFull(queueFullOpt: Option[Ast.QueueFull]): Ast.QueueFull =
    queueFullOpt.getOrElse(Ast.QueueFull.Assert)

  /** Displays an ID value */
  def displayIdValue(value: BigInt): String = {
    val dec = value.toString
    val hex = value.toString(16).toUpperCase
    s"($dec dec, $hex hex)"
  }

  /** Gets the qualified name of a symbol from a parent-symbol map */
  def getQualifiedNameFromMap[S <: SymbolInterface]
    (parentSymbolMap: Map[S,S]) (s: S):
  Name.Qualified = {
    def getIdentList(so: Option[S], out: List[Ast.Ident]): List[Ast.Ident] =
      so match {
        case Some(s) =>
          val so1 = parentSymbolMap.get(s)
          getIdentList(so1, s.getUnqualifiedName :: out)
        case None => out
      }
    Name.Qualified.fromIdentList(getIdentList(Some(s), Nil))
  }
}
