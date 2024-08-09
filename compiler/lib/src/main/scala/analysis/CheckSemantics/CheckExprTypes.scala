package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute and check expression types, except for array sizes
 *  and default values */
object CheckExprTypes extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) =
    visitUse(a, node)

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.defArrayAnnotatedNode(a, aNode)
      _ <- convertNodeToNumeric(a, data.size)
      _ <- data.default match {
        case Some(defaultNode) =>
          val arrayId = node.id
          val arrayType = a.typeMap(arrayId)
          val defaultId = defaultNode.id
          val defaultType = a.typeMap(defaultId)
          val loc = Locations.get(defaultId)
          Analysis.convertTypes(loc, defaultType -> arrayType)
        case None => Right(a)
      }
    } yield a
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = aNode
    if (!a.typeMap.contains(node.id)) {
      val data = node.data
      for (a <- super.defConstantAnnotatedNode(a, aNode))
        yield {
          val t = a.typeMap(data.value.id)
          a.assignType(node -> t)
        }
    }
    else Right(a)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.defEnumAnnotatedNode(a, aNode)
      _ <- data.default match {
        case Some(defaultNode) =>
          val enumId = node.id
          val enumType = a.typeMap(enumId)
          val defaultId = defaultNode.id
          val defaultType = a.typeMap(defaultId)
          val loc = Locations.get(defaultId)
          Analysis.convertTypes(loc, defaultType -> enumType)
        case None => Right(a)
      }
    } yield a
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.defEnumConstantAnnotatedNode(a, aNode)
      // Just check that the type of the value expression is convertible to numeric
      // The enum type of the enum constant node is already in the type map
      _ <- convertNodeToNumericOpt(a, data.value)
    }
    yield a
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.defStructAnnotatedNode(a, aNode)
      _ <- data.default match {
        case Some(defaultNode) =>
          val structId = node.id
          val structType = a.typeMap(structId)
          val defaultId = defaultNode.id
          val defaultType = a.typeMap(defaultId)
          val loc = Locations.get(defaultId)
          Analysis.convertTypes(loc, defaultType -> structType)
        case None => Right(a)
      }
    } yield a
  }

  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.id)
    val emptyListError = SemanticError.EmptyArray(loc)
    for {
      a <- super.exprArrayNode(a, node, e)
      t <- a.commonType(e.elts.map(_.id), emptyListError)
    } yield a.assignType(node -> Type.AnonArray(Some(e.elts.size), t))
  }

  override def exprBinopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprBinop) = {
    val loc = Locations.get(node.id)
    for {
      a <- super.exprBinopNode(a, node, e)
      t <- a.commonType(e.e1.id, e.e2.id, loc)
      _ <- convertToNumeric(loc, t)
    } yield a.assignType(node -> t)
  }

  override def exprLiteralBoolNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) =
    Right(a.assignType(node -> Type.Boolean))

  override def exprLiteralFloatNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    Right(a.assignType(node -> Type.F64))

  override def exprLiteralIntNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    Right(a.assignType(node -> Type.Integer))

  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    Right(a.assignType(node -> Type.String(None)))

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    for (a <- super.exprParenNode(a, node, e))
      yield a.assignType(node -> a.typeMap(e.e.id))
  }

  override def exprStructNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprStruct) = {
    def getName(member: Ast.StructMember) = member.name
    for {
      _ <- Analysis.checkForDuplicateStructMember(getName)(e.members)
      a <- super.exprStructNode(a, node, e)
    }
    yield {
      def visitor(members: Type.Struct.Members, node: AstNode[Ast.StructMember]): Type.Struct.Members = {
        val data = node.data
        val t = a.typeMap(data.value.id)
        members + (data.name -> t)
      }
      val empty: Type.Struct.Members = Map()
      val members = e.members.foldLeft(empty)(visitor)
      a.assignType(node -> Type.AnonStruct(members))
    }
  }

  override def exprUnopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprUnop) = {
    val loc = Locations.get(node.id)
    for {
      a <- super.exprUnopNode(a, node, e)
      t <- {
        val t1 = a.typeMap(e.e.id)
        convertToNumeric(loc, t1)
      }
    } yield a.assignType(node -> t)
  }

  override def specCommandAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specCommandAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.opcode)
      _ <- convertNodeToNumericOpt(a, data.priority)
    }
    yield a
  }

  override def specContainerAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecContainer]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specContainerAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.id)
      _ <- convertNodeToNumericOpt(a, data.defaultPriority)
    }
    yield a
  }

  override def specEventAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specEventAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.id)
      _ <- convertNodeToNumericOpt(a, data.throttle)
    }
    yield a
  }

  override def specInitAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecInit]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specInitAnnotatedNode(a, aNode)
      _ <- convertNodeToNumeric(a, data.phase)
    }
    yield a
  }

  override def specInternalPortAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specInternalPortAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.priority)
    }
    yield a
  }

  override def specParamAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecParam]]) = {
    val (_, node, _) = aNode
    val data = node.data
    def checkDefault (a: Analysis) (default: AstNode[Ast.Expr]) = {
      val loc = Locations.get(default.id)
      val defaultType = a.typeMap(default.id)
      val paramType = a.typeMap(data.typeName.id)
      Analysis.convertTypes(loc, defaultType -> paramType)
    }
    for {
      a <- super.specParamAnnotatedNode(a, aNode)
      _ <- Result.mapOpt(data.default, checkDefault(a))
      _ <- convertNodeToNumericOpt(a, data.id)
      _ <- convertNodeToNumericOpt(a, data.setOpcode)
      _ <- convertNodeToNumericOpt(a, data.saveOpcode)
    }
    yield a
  }

  override def specPortInstanceAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node, _) = aNode
    val data = node.data
    data match {
      case general : Ast.SpecPortInstance.General =>
        for {
          a <- super.specPortInstanceAnnotatedNode(a, aNode)
          _ <- convertNodeToNumericOpt(a, general.size)
          _ <- convertNodeToNumericOpt(a, general.priority)
        }
        yield a
      case special : Ast.SpecPortInstance.Special =>
        for {
          a <- super.specPortInstanceAnnotatedNode(a, aNode)
          _ <- convertNodeToNumericOpt(a, special.priority)
        } yield a
    }
  }

  override def specRecordAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecRecord]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specRecordAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.id)
    }
    yield a
  }

  override def specStateMachineInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specStateMachineInstanceAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.priority)
    }
    yield a
  }

  override def specTlmChannelAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]) = {
    val (_, node, _) = aNode
    val data = node.data
    def checkLimitExpr (a: Analysis) (e: AstNode[Ast.Expr]): Result.Result[Unit] = {
      val loc = Locations.get(e.id)
      val limitType = a.typeMap(e.id)
      val channelType = a.typeMap(data.typeName.id)
      for {
        _ <- convertNodeToNumeric(a, e)
        _ <- Analysis.convertTypes(loc, limitType -> channelType)
      }
      yield ()
    }
    for {
      a <- super.specTlmChannelAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.id)
      _ <- Result.map(data.low.map(_._2), checkLimitExpr(a))
      _ <- Result.map(data.high.map(_._2), checkLimitExpr(a))
    }
    yield a
  }

  override def structTypeMemberAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.structTypeMemberAnnotatedNode(a, aNode)
      _ <- convertNodeToNumericOpt(a, data.size)
    } yield a
  }

  override def typeNameStringNode(
    a: Analysis,
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameString
  ) =
    for {
      a <- super.typeNameStringNode(a, node, tn)
      _ <- convertNodeToNumericOpt(a, tn.size)
    } yield a

  private def visitUse[T](a: Analysis, node: AstNode[T]): Result = {
    val symbol = a.useDefMap(node.id)
    for {
      a <- symbol match {
        // Unqualified constant symbol: visit the constant definition
        // to ensure it has a type
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        // Unqualified enum symbol: if this is in scope, then we are in
        // the enum definition, so it already has a type
        case Symbol.EnumConstant(node) => Right(a)
        // Invalid use of a symbol in an expression
        case _ => Left(SemanticError.InvalidSymbol(
          symbol.getUnqualifiedName,
          Locations.get(node.id),
          "not a constant symbol",
          symbol.getLoc
        ))
      }
    } yield {
      val t = a.typeMap(symbol.getNodeId)
      a.assignType(node -> t)
    }
  }

  private def convertNodeToNumeric[T](a: Analysis, node: AstNode[T]) = {
    val id = node.id
    val t = a.typeMap(id)
    val loc = Locations.get(id)
    convertToNumeric(loc, t)
  }

  private def convertNodeToNumericOpt[T](a: Analysis, nodeOpt: Option[AstNode[T]]) =
    nodeOpt match {
      case Some(node) => convertNodeToNumeric(a, node)
      case None => Right(a)
    }

  private def convertToNumeric(loc: Location, t: Type): Result.Result[Type] = {
    if (t.isNumeric) Right(t)
    else if (t.isConvertibleTo(Type.Integer)) Right(Type.Integer)
    else {
      val error = SemanticError.InvalidType(loc, s"cannot convert $t to a numeric type")
      Left(error)
    }
  }

}
