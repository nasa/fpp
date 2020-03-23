package fpp.compiler.ast

import fpp.compiler.util._

/** Transform an AST */
trait AstTransformer[A, B] {

  type Result[T] = Result.Result[(B, T)]

  type ResultNode[T] = Result[AstNode[T]]

  def default(a: A): B

  def defAbsTypeNode(a: A, node: AstNode[Ast.DefAbsType]): ResultNode[Ast.DefAbsType] = 
    Right(default(a), node)

  def defArrayNode(a: A, node: AstNode[Ast.DefArray]): ResultNode[Ast.DefArray] =
    Right(default(a), node)

  def defComponentNode(a: A, node: AstNode[Ast.DefComponent]): ResultNode[Ast.DefComponent] =
    Right(default(a), node)

  def defComponentInstanceNode(a: A, node: AstNode[Ast.DefComponentInstance]): ResultNode[Ast.DefComponentInstance] =
    Right(default(a), node)

  def defConstantNode(a: A, node: AstNode[Ast.DefConstant]): ResultNode[Ast.DefConstant] =
    Right(default(a), node)

  def defEnumNode(a: A, node: AstNode[Ast.DefEnum]): ResultNode[Ast.DefEnum] =
    Right(default(a), node)

  def defModuleNode(a: A, node: AstNode[Ast.DefModule]): ResultNode[Ast.DefModule] =
    Right(default(a), node)

  def defPortNode(a: A, node: AstNode[Ast.DefPort]): ResultNode[Ast.DefPort] =
    Right(default(a), node)

  def defStructNode(a: A, node: AstNode[Ast.DefStruct]): ResultNode[Ast.DefStruct] =
    Right(default(a), node)

  def defTopologyNode(a: A, node: AstNode[Ast.DefTopology]): ResultNode[Ast.DefTopology] =
    Right(default(a), node)

  def exprArrayNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprArray): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprBinopNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprBinop): ResultNode[Ast.Expr] =
    Right(default(a), node)
  
  def exprDotNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprDot): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprIdentNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprIdent): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprLiteralBoolNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprLiteralFloatNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprLiteralIntNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprLiteralStringNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprParenNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprParen): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprStructNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprStruct): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def exprUnopNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprUnop): ResultNode[Ast.Expr] =
    Right(default(a), node)

  def specCommandNode(a: A, node: AstNode[Ast.SpecCommand]): ResultNode[Ast.SpecCommand] =
    Right(default(a), node)

  def specCompInstanceNode(a: A, node: AstNode[Ast.SpecCompInstance]): ResultNode[Ast.SpecCompInstance] =
    Right(default(a), node)

  def specConnectionGraphNode(a: A, node: AstNode[Ast.SpecConnectionGraph]): ResultNode[Ast.SpecConnectionGraph] =
    Right(default(a), node)

  def specEventNode(a: A, node: AstNode[Ast.SpecEvent]): ResultNode[Ast.SpecEvent] =
    Right(default(a), node)

  def specIncludeNode(a: A, node: AstNode[Ast.SpecInclude]): ResultNode[Ast.SpecInclude] =
    Right(default(a), node)

  def specInitNode(a: A, node: AstNode[Ast.SpecInit]): ResultNode[Ast.SpecInit] =
    Right(default(a), node)

  def specInternalPortNode(a: A, node: AstNode[Ast.SpecInternalPort]): ResultNode[Ast.SpecInternalPort] =
    Right(default(a), node)

  def specLocNode(a: A, node: AstNode[Ast.SpecLoc]): ResultNode[Ast.SpecLoc] =
    Right(default(a), node)

  def specParamNode(a: A, node: AstNode[Ast.SpecParam]): ResultNode[Ast.SpecParam] =
    Right(default(a), node)

  def specPortInstanceNode(a: A, node: AstNode[Ast.SpecPortInstance]): ResultNode[Ast.SpecPortInstance] =
    Right(default(a), node)

  def specTlmChannelNode(a: A, node: AstNode[Ast.SpecTlmChannel]): ResultNode[Ast.SpecTlmChannel] =
    Right(default(a), node)

  def specTopImportNode(a: A, node: AstNode[Ast.SpecTopImport]): ResultNode[Ast.SpecTopImport] =
    Right(default(a), node)

  def specUnusedPortsNode(a: A, node: AstNode[Ast.SpecUnusedPorts]): ResultNode[Ast.SpecUnusedPorts] =
    Right(default(a), node)

  def transUnit(a: A, tu: Ast.TransUnit): Result[Ast.TransUnit] =
    Right(default(a), tu)

  def typeNameBool(a: A): Result.Result[B] = Right(default(a))

  def typeNameFloat(a: A, tnf: Ast.TypeNameFloat): Result[Ast.TypeNameFloat] =
    Right(default(a), tnf)

  def typeNameInt(a: A, tni: Ast.TypeNameInt): Result[Ast.TypeNameInt] = 
    Right(default(a), tni)

  def typeNameQualIdent(a: A, tnqid: Ast.TypeNameQualIdent): Result[Ast.TypeNameQualIdent] =
    Right(default(a), tnqid)

  def typeNameString(a: A): Result.Result[B]= Right(default(a))

  final def matchComponentMemberNode(a: A, cmn: Ast.ComponentMember.Node): Result[Ast.ComponentMember.Node] = {
    cmn match {
      case Ast.ComponentMember.DefArray(node) => 
        transformNode(defArrayNode(a, node), Ast.ComponentMember.DefArray(_))
      case Ast.ComponentMember.DefConstant(node) => 
        transformNode(defConstantNode(a, node), Ast.ComponentMember.DefConstant(_))
      case Ast.ComponentMember.DefEnum(node) => 
        transformNode(defEnumNode(a, node), Ast.ComponentMember.DefEnum(_))
      case Ast.ComponentMember.DefStruct(node) => 
        transformNode(defStructNode(a, node), Ast.ComponentMember.DefStruct(_))
      case Ast.ComponentMember.SpecCommand(node) => 
        transformNode(specCommandNode(a, node), Ast.ComponentMember.SpecCommand(_))
      case Ast.ComponentMember.SpecEvent(node) => 
        transformNode(specEventNode(a, node), Ast.ComponentMember.SpecEvent(_))
      case Ast.ComponentMember.SpecInclude(node) => 
        transformNode(specIncludeNode(a, node), Ast.ComponentMember.SpecInclude(_))
      case Ast.ComponentMember.SpecInternalPort(node) => 
        transformNode(specInternalPortNode(a, node), Ast.ComponentMember.SpecInternalPort(_))
      case Ast.ComponentMember.SpecParam(node) => 
        transformNode(specParamNode(a, node), Ast.ComponentMember.SpecParam(_))
      case Ast.ComponentMember.SpecPortInstance(node) => 
        transformNode(specPortInstanceNode(a, node), Ast.ComponentMember.SpecPortInstance(_))
      case Ast.ComponentMember.SpecTlmChannel(node) => 
        transformNode(specTlmChannelNode(a, node), Ast.ComponentMember.SpecTlmChannel(_))
    }
  }

  final def matchExprNode(a: A, node: AstNode[Ast.Expr]): ResultNode[Ast.Expr] = {
    node.getData match {
      case e @ Ast.ExprArray(_) => exprArrayNode(a, node, e)
      case e @ Ast.ExprBinop(_, _, _) => exprBinopNode(a, node, e)
      case e @ Ast.ExprDot(_, _) => exprDotNode(a, node, e)
      case e @ Ast.ExprIdent(_) => exprIdentNode(a, node, e)
      case e @ Ast.ExprLiteralInt(_) => exprLiteralIntNode(a, node, e)
      case e @ Ast.ExprLiteralFloat(_) => exprLiteralFloatNode(a, node, e)
      case e @ Ast.ExprLiteralString(_) => exprLiteralStringNode(a, node, e)
      case e @ Ast.ExprLiteralBool(_) => exprLiteralBoolNode(a, node, e)
      case e @ Ast.ExprParen(_) => exprParenNode(a, node, e)
      case e @ Ast.ExprStruct(_) => exprStructNode(a, node, e)
      case e @ Ast.ExprUnop(_, _) => exprUnopNode(a, node, e)
    }
  }

  final def matchModuleMemberNode(a: A, mmn: Ast.ModuleMember.Node): Result[Ast.ModuleMember.Node] = {
    mmn match {
      case Ast.ModuleMember.DefAbsType(node) => 
        transformNode(defAbsTypeNode(a, node), Ast.ModuleMember.DefAbsType(_))
      case Ast.ModuleMember.DefArray(node) => 
        transformNode(defArrayNode(a, node), Ast.ModuleMember.DefArray(_))
      case Ast.ModuleMember.DefComponent(node) => 
        transformNode(defComponentNode(a, node), Ast.ModuleMember.DefComponent(_))
      case Ast.ModuleMember.DefComponentInstance(node) => 
        transformNode(defComponentInstanceNode(a, node), Ast.ModuleMember.DefComponentInstance(_))
      case Ast.ModuleMember.DefConstant(node) => 
        transformNode(defConstantNode(a, node), Ast.ModuleMember.DefConstant(_))
      case Ast.ModuleMember.DefEnum(node) => 
        transformNode(defEnumNode(a, node), Ast.ModuleMember.DefEnum(_))
      case Ast.ModuleMember.DefModule(node) => 
        transformNode(defModuleNode(a, node), Ast.ModuleMember.DefModule(_))
      case Ast.ModuleMember.DefPort(node) => 
        transformNode(defPortNode(a, node), Ast.ModuleMember.DefPort(_))
      case Ast.ModuleMember.DefStruct(node) => 
        transformNode(defStructNode(a, node), Ast.ModuleMember.DefStruct(_))
      case Ast.ModuleMember.DefTopology(node) => 
        transformNode(defTopologyNode(a, node), Ast.ModuleMember.DefTopology(_))
      case Ast.ModuleMember.SpecInclude(node) => 
        transformNode(specIncludeNode(a, node), Ast.ModuleMember.SpecInclude(_))
      case Ast.ModuleMember.SpecInit(node) => 
        transformNode(specInitNode(a, node), Ast.ModuleMember.SpecInit(_))
      case Ast.ModuleMember.SpecLoc(node) => 
        transformNode(specLocNode(a, node), Ast.ModuleMember.SpecLoc(_))
    }
  }

  /*
  final def matchTopologyMemberNode(a: A, tmn: Ast.TopologyMember.Node): B =
    tmn match {
      case Ast.TopologyMember.SpecCompInstance(node) => specCompInstanceNode(a, node)
      case Ast.TopologyMember.SpecConnectionGraph(node) => specConnectionGraphNode(a, node)
      case Ast.TopologyMember.SpecInclude(node) => specIncludeNode(a, node)
      case Ast.TopologyMember.SpecTopImport(node) => specTopImportNode(a, node)
      case Ast.TopologyMember.SpecUnusedPorts(node) => specUnusedPortsNode(a, node)
    }

  final def matchTuMemberNode(a: A, tumn: Ast.TUMember.Node): B = 
    matchModuleMemberNode(a, tumn)

  final def matchTypeName(a: A, tn: Ast.TypeName): B =
    tn match {
      case Ast.TypeNameBool => typeNameBool(a)
      case tnf @ Ast.TypeNameFloat(_) => typeNameFloat(a, tnf)
      case tni @ Ast.TypeNameInt(_) => typeNameInt(a, tni)
      case tnqid @ Ast.TypeNameQualIdent(_) => typeNameQualIdent(a, tnqid)
      case Ast.TypeNameString => typeNameString(a)
    }
    */

  private def transformNode[A,B](rn: ResultNode[A], f: AstNode[A] => B): Result[B] =
    rn match {
      case Right((b, node1)) => Right((b, f(node1)))
      case Left(e) => Left(e)
    }

}
