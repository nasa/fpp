package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze uses */
trait UseAnalyzer extends TypeExpressionAnalyzer {

  /** A use of a component definition */
  def componentUse(a: Analysis, qualIdent: Ast.QualIdent.NodeList, use: Name.Qualified): Result = default(a)
 
  /** A use of a component instance definition */
  def componentInstanceUse(a: Analysis, node: Ast.QualIdent.NodeList, use: Name.Qualified): Result = default(a)

  /** A use of a constant definition or enumerated constant definition */
  def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified): Result = default(a)

  /** A use of a port definition */
  def portUse(a: Analysis, qualIdent: Ast.QualIdent.NodeList, use: Name.Qualified): Result = default(a)

  /** A use of a topology definition */
  def topologyUse(a: Analysis, qualIdent: Ast.QualIdent.NodeList, use: Name.Qualified): Result = default(a)

  /** A use of a type definition */
  def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified): Result = default(a)

  override def defComponentInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- qualIdent (componentUse) (a, Ast.QualIdent.Node.toList(data.component))
      a <- exprNode(a, data.baseId)
      a <- opt(exprNode)(a, data.queueSize)
      a <- opt(exprNode)(a, data.stackSize)
      a <- opt(exprNode)(a, data.priority)
    } yield a
  }

  override def exprDotNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprDot) = {
    def nameOpt(e: Ast.Expr, qualifier: List[Name.Unqualified]): Option[Name.Qualified] = {
      e match {
        case Ast.ExprIdent(id) => {
          val list = id :: qualifier
          val use = Name.Qualified.fromIdentList(list)
          Some(use)
        }
        case Ast.ExprDot(e1, id) => nameOpt(e1.getData, id.getData :: qualifier)
        case _ => None
      }
    }
    nameOpt(e, Nil) match {
      case Some(use) => constantUse(a, node, use)
      case None => Right(a)
    }
  }

  override def exprIdentNode(a: Analysis, node: AstNode[Ast.Expr], e: Ast.ExprIdent) = {
    val use = Name.Qualified(Nil, e.value)
    constantUse(a, node, use)
  }

  override def exprNode(a: Analysis, node: AstNode[Ast.Expr]): Result = matchExprNode(a, node)

  override def specCompInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    qualIdent (componentInstanceUse) (a, Ast.QualIdent.Node.toList(data.instance))
  }

  override def specConnectionGraphAnnotatedNode(
    a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]) = {
    def connection(a: Analysis, connection: Ast.SpecConnectionGraph.Connection): Result = {
      for {
        a <- portInstanceIdentifierNode(a, connection.fromPort)
        a <- opt(exprNode)(a, connection.fromIndex)
        a <- portInstanceIdentifierNode(a, connection.toPort)
        a <- opt(exprNode)(a, connection.toIndex)
      } yield a
    }
    val (_, node1, _) = node
    val data = node1.getData
    data match {
      case direct @ Ast.SpecConnectionGraph.Direct(_, _) => visitList(a, direct.connections, connection)
      case pattern @ Ast.SpecConnectionGraph.Pattern(_, _, _) => for {
        a <- qualIdent (componentInstanceUse) (a, Ast.QualIdent.Node.toList(pattern.source))
        a <- visitList(a, pattern.targets.map(Ast.QualIdent.Node.toList), qualIdent (componentInstanceUse) _)
        a <- exprNode(a, pattern.pattern)
      } yield a
    }
  }

  override def specInitAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecInit]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    for {
      a <- qualIdent (componentInstanceUse) (a, Ast.QualIdent.Node.toList(data.instance))
      a <- exprNode(a, data.phase)
    } yield a
  }

  override def specPortInstanceAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    data match {
      case general @ Ast.SpecPortInstance.General(_, _, _, _, _, _) =>
        for {
          a <- opt(exprNode)(a, general.size)
          a <- opt(qualIdent(portUse))(a, for (port <- general.port) yield Ast.QualIdent.Node.toList(port))
          a <- opt(exprNode)(a, general.priority)
        } yield a
      case _ => Right(a)
    }
  }

  override def specTopImportAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecTopImport]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    qualIdent(topologyUse)(a, Ast.QualIdent.Node.toList(data.top))
  }

  override def specUnusedPortsAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.SpecUnusedPorts]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    visitList(a, data.ports, portInstanceIdentifierNode)
  }

  override def typeNameNode(a: Analysis, node: AstNode[Ast.TypeName]) = matchTypeNameNode(a, node)

  override def typeNameQualIdentNode(a: Analysis, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent) = {
    val use = Name.Qualified.fromIdentList(Ast.QualIdent.Node.toList(tn.name).map(_.getData))
    typeUse(a, node, use)
  }

  private def portInstanceIdentifierNode(a: Analysis, node: AstNode[Ast.PortInstanceIdentifier]): Result =
    qualIdent (componentInstanceUse) (a, Ast.QualIdent.Node.toList(node.getData.componentInstance))

  private def qualIdent
    (f: (Analysis, Ast.QualIdent.NodeList, Name.Qualified) => Result) 
    (a: Analysis, qualIdent: Ast.QualIdent.NodeList): Result = {
    val use = Name.Qualified.fromIdentList(qualIdent.map(_.getData))
    f(a, qualIdent, use)
  }

}
