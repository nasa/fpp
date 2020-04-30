package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute and check types, except for array sizes */
object CheckTypesPhase1 extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) =
    visitUse(a, node, use)

  override def defConstantAnnotatedNode(a: Analysis, anode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node,_) = anode
    if (!a.typeMap.contains(node.getId)) {
      val data = node.getData
      for (a <- super.defConstantAnnotatedNode(a, anode))
        yield {
          val t = a.typeMap.get(data.value.getId)
          assignType(a, node -> t)
        }
    }
    else Right(a)
  }

  override def exprArrayNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprArray) = {
    val loc = Locations.get(node.getId)
    val emptyListError = SemanticError.EmptyArray(loc)
    for {
      a <- super.exprArrayNode(a, node, e)
      t <- a.computeCommonType(e.elts.map(_.getId), emptyListError)
    } yield a.assignType(node -> Type.AnonArray(None, t))
  }

  override def exprBinopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprBinop) = {
    // TODO
    Right(a.assignType(node -> Type.Integer))
  }

  override def exprLiteralBoolNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) =
    Right(a.assignType(node -> Type.Boolean))

  override def exprLiteralFloatNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    Right(a.assignType(node -> Type.Float(Type.Float.F64)))

  override def exprLiteralIntNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    Right(a.assignType(node -> Type.Integer))
  
  override def exprLiteralStringNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    Right(a.assignType(node -> Type.String))

  override def exprParenNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprParen) = {
    // TODO
    Right(a.assignType(node -> Type.Integer))
  }

  override def exprStructNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprStruct) = {
    def checkForDuplicateMember(nodes: List[AstNode[Ast.StructMember]], map: Map[Name.Unqualified, AstNode.Id]): Result.Result[Unit] =
      nodes match {
        case Nil => Right(())
        case node :: tail => {
          val data = node.data
          map.get(data.name) match {
            case None => checkForDuplicateMember(tail, map + (data.name -> node.getId))
            case Some(id) => {
              val loc = Locations.get(node.getId)
              val prevLoc = Locations.get(id)
              Left(SemanticError.DuplicateStructMember(data.name, loc, prevLoc))
            }
          }
        }
      }
    for {
      _ <- checkForDuplicateMember(e.members, Map())
      a <- super.exprStructNode(a, node, e)
    } 
    yield {
      def f(members: Type.Struct.Members, node: AstNode[Ast.StructMember]): Type.Struct.Members = {
        val data = node.data
        a.typeMap.get(data.value.getId) match {
          case Some(t) => members + (data.name -> t)
          case None => members
        }
      }
      val empty: Type.Struct.Members = Map()
      val members = e.members.foldLeft(empty)(f)
      a.assignType(node -> Type.AnonStruct(members))
    }
  }

  override def exprUnopNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprUnop) = {
    // TODO
    Right(a.assignType(node -> Type.Integer))
  }

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    // TODO
    Right(a.assignType(node -> Type.Integer))
  }

  private def visitUse[T](a: Analysis, node: AstNode[T], use: Name.Qualified): Result = {
    val symbol = a.useDefMap(node.getId)
    for {
      a <- symbol match {
        case Symbol.Array(node) => defArrayAnnotatedNode(a, node)
        case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
        case Symbol.Enum(node) => defEnumAnnotatedNode(a, node)
        case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a, node)
        case Symbol.Struct(node) => defStructAnnotatedNode(a, node)
        case _ => Right(a)
      }
    } yield {
      val t = a.typeMap.get(symbol.getNodeId)
      assignType(a, node -> t)
    }
  }

  private def assignType[T](a: Analysis, mapping: (AstNode[T], Option[Type])): Analysis = {
    // TODO: Make this abort on failure to find the mapping
    val node -> tOpt = mapping
    tOpt match {
      case Some(t) => a.assignType(node -> t)
      case None => a
    }
  }

}
