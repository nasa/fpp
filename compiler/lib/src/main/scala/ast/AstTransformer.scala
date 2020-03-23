package fpp.compiler.ast

import fpp.compiler.util._

/** Transform an AST */
trait AstTransformer {

  type In

  type Out

  type Result[T] = Result.Result[(Out, T)]

  type ResultNode[T] = Result[AstNode[T]]

  def default(in: In): Out

  def defAbsTypeNode(in: In, node: AstNode[Ast.DefAbsType]): ResultNode[Ast.DefAbsType] = 
    Right(default(in), node)

  def defArrayNode(in: In, node: AstNode[Ast.DefArray]): ResultNode[Ast.DefArray] =
    Right(default(in), node)

  def defComponentNode(in: In, node: AstNode[Ast.DefComponent]): ResultNode[Ast.DefComponent] =
    Right(default(in), node)

  def defComponentInstanceNode(in: In, node: AstNode[Ast.DefComponentInstance]): ResultNode[Ast.DefComponentInstance] =
    Right(default(in), node)

  def defConstantNode(in: In, node: AstNode[Ast.DefConstant]): ResultNode[Ast.DefConstant] =
    Right(default(in), node)

  def defEnumNode(in: In, node: AstNode[Ast.DefEnum]): ResultNode[Ast.DefEnum] =
    Right(default(in), node)

  def defModuleNode(in: In, node: AstNode[Ast.DefModule]): ResultNode[Ast.DefModule] =
    Right(default(in), node)

  def defPortNode(in: In, node: AstNode[Ast.DefPort]): ResultNode[Ast.DefPort] =
    Right(default(in), node)

  def defStructNode(in: In, node: AstNode[Ast.DefStruct]): ResultNode[Ast.DefStruct] =
    Right(default(in), node)

  def defTopologyNode(in: In, node: AstNode[Ast.DefTopology]): ResultNode[Ast.DefTopology] =
    Right(default(in), node)

  def exprArrayNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprArray): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprBinopNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprBinop): ResultNode[Ast.Expr] =
    Right(default(in), node)
  
  def exprDotNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprDot): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprIdentNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprIdent): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprLiteralBoolNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprLiteralFloatNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprLiteralIntNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprLiteralStringNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprParenNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprParen): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprStructNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprStruct): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def exprUnopNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprUnop): ResultNode[Ast.Expr] =
    Right(default(in), node)

  def specCommandNode(in: In, node: AstNode[Ast.SpecCommand]): ResultNode[Ast.SpecCommand] =
    Right(default(in), node)

  def specCompInstanceNode(in: In, node: AstNode[Ast.SpecCompInstance]): ResultNode[Ast.SpecCompInstance] =
    Right(default(in), node)

  def specConnectionGraphNode(in: In, node: AstNode[Ast.SpecConnectionGraph]): ResultNode[Ast.SpecConnectionGraph] =
    Right(default(in), node)

  def specEventNode(in: In, node: AstNode[Ast.SpecEvent]): ResultNode[Ast.SpecEvent] =
    Right(default(in), node)

  def specIncludeNode(in: In, node: AstNode[Ast.SpecInclude]): ResultNode[Ast.SpecInclude] =
    Right(default(in), node)

  def specInitNode(in: In, node: AstNode[Ast.SpecInit]): ResultNode[Ast.SpecInit] =
    Right(default(in), node)

  def specInternalPortNode(in: In, node: AstNode[Ast.SpecInternalPort]): ResultNode[Ast.SpecInternalPort] =
    Right(default(in), node)

  def specLocNode(in: In, node: AstNode[Ast.SpecLoc]): ResultNode[Ast.SpecLoc] =
    Right(default(in), node)

  def specParamNode(in: In, node: AstNode[Ast.SpecParam]): ResultNode[Ast.SpecParam] =
    Right(default(in), node)

  def specPortInstanceNode(in: In, node: AstNode[Ast.SpecPortInstance]): ResultNode[Ast.SpecPortInstance] =
    Right(default(in), node)

  def specTlmChannelNode(in: In, node: AstNode[Ast.SpecTlmChannel]): ResultNode[Ast.SpecTlmChannel] =
    Right(default(in), node)

  def specTopImportNode(in: In, node: AstNode[Ast.SpecTopImport]): ResultNode[Ast.SpecTopImport] =
    Right(default(in), node)

  def specUnusedPortsNode(in: In, node: AstNode[Ast.SpecUnusedPorts]): ResultNode[Ast.SpecUnusedPorts] =
    Right(default(in), node)

  def transUnit(in: In, tu: Ast.TransUnit): Result[Ast.TransUnit] =
    Right(default(in), tu)

  def typeNameBoolNode(in: In, node: AstNode[Ast.TypeName]): ResultNode[Ast.TypeName] =
    Right(default(in), node)

  def typeNameFloatNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): ResultNode[Ast.TypeName] =
    Right(default(in), node)

  def typeNameIntNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): ResultNode[Ast.TypeName] = 
    Right(default(in), node)

  def typeNameQualIdentNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): ResultNode[Ast.TypeName] =
    Right(default(in), node)

  def typeNameStringNode(in: In, node: AstNode[Ast.TypeName]): ResultNode[Ast.TypeName] =
    Right(default(in), node)

  final def matchComponentMemberNode(in: In, cmn: Ast.ComponentMember.Node): Result[Ast.ComponentMember.Node] = {
    cmn match {
      case Ast.ComponentMember.DefArray(node) => 
        transformNode(defArrayNode(in, node), Ast.ComponentMember.DefArray(_))
      case Ast.ComponentMember.DefConstant(node) => 
        transformNode(defConstantNode(in, node), Ast.ComponentMember.DefConstant(_))
      case Ast.ComponentMember.DefEnum(node) => 
        transformNode(defEnumNode(in, node), Ast.ComponentMember.DefEnum(_))
      case Ast.ComponentMember.DefStruct(node) => 
        transformNode(defStructNode(in, node), Ast.ComponentMember.DefStruct(_))
      case Ast.ComponentMember.SpecCommand(node) => 
        transformNode(specCommandNode(in, node), Ast.ComponentMember.SpecCommand(_))
      case Ast.ComponentMember.SpecEvent(node) => 
        transformNode(specEventNode(in, node), Ast.ComponentMember.SpecEvent(_))
      case Ast.ComponentMember.SpecInclude(node) => 
        transformNode(specIncludeNode(in, node), Ast.ComponentMember.SpecInclude(_))
      case Ast.ComponentMember.SpecInternalPort(node) => 
        transformNode(specInternalPortNode(in, node), Ast.ComponentMember.SpecInternalPort(_))
      case Ast.ComponentMember.SpecParam(node) => 
        transformNode(specParamNode(in, node), Ast.ComponentMember.SpecParam(_))
      case Ast.ComponentMember.SpecPortInstance(node) => 
        transformNode(specPortInstanceNode(in, node), Ast.ComponentMember.SpecPortInstance(_))
      case Ast.ComponentMember.SpecTlmChannel(node) => 
        transformNode(specTlmChannelNode(in, node), Ast.ComponentMember.SpecTlmChannel(_))
    }
  }

  final def matchExprNode(in: In, node: AstNode[Ast.Expr]): ResultNode[Ast.Expr] =
    node.getData match {
      case e @ Ast.ExprArray(_) => exprArrayNode(in, node, e)
      case e @ Ast.ExprBinop(_, _, _) => exprBinopNode(in, node, e)
      case e @ Ast.ExprDot(_, _) => exprDotNode(in, node, e)
      case e @ Ast.ExprIdent(_) => exprIdentNode(in, node, e)
      case e @ Ast.ExprLiteralInt(_) => exprLiteralIntNode(in, node, e)
      case e @ Ast.ExprLiteralFloat(_) => exprLiteralFloatNode(in, node, e)
      case e @ Ast.ExprLiteralString(_) => exprLiteralStringNode(in, node, e)
      case e @ Ast.ExprLiteralBool(_) => exprLiteralBoolNode(in, node, e)
      case e @ Ast.ExprParen(_) => exprParenNode(in, node, e)
      case e @ Ast.ExprStruct(_) => exprStructNode(in, node, e)
      case e @ Ast.ExprUnop(_, _) => exprUnopNode(in, node, e)
    }

  final def matchModuleMemberNode(in: In, mmn: Ast.ModuleMember.Node): Result[Ast.ModuleMember.Node] =
    mmn match {
      case Ast.ModuleMember.DefAbsType(node) => 
        transformNode(defAbsTypeNode(in, node), Ast.ModuleMember.DefAbsType(_))
      case Ast.ModuleMember.DefArray(node) => 
        transformNode(defArrayNode(in, node), Ast.ModuleMember.DefArray(_))
      case Ast.ModuleMember.DefComponent(node) => 
        transformNode(defComponentNode(in, node), Ast.ModuleMember.DefComponent(_))
      case Ast.ModuleMember.DefComponentInstance(node) => 
        transformNode(defComponentInstanceNode(in, node), Ast.ModuleMember.DefComponentInstance(_))
      case Ast.ModuleMember.DefConstant(node) => 
        transformNode(defConstantNode(in, node), Ast.ModuleMember.DefConstant(_))
      case Ast.ModuleMember.DefEnum(node) => 
        transformNode(defEnumNode(in, node), Ast.ModuleMember.DefEnum(_))
      case Ast.ModuleMember.DefModule(node) => 
        transformNode(defModuleNode(in, node), Ast.ModuleMember.DefModule(_))
      case Ast.ModuleMember.DefPort(node) => 
        transformNode(defPortNode(in, node), Ast.ModuleMember.DefPort(_))
      case Ast.ModuleMember.DefStruct(node) => 
        transformNode(defStructNode(in, node), Ast.ModuleMember.DefStruct(_))
      case Ast.ModuleMember.DefTopology(node) => 
        transformNode(defTopologyNode(in, node), Ast.ModuleMember.DefTopology(_))
      case Ast.ModuleMember.SpecInclude(node) => 
        transformNode(specIncludeNode(in, node), Ast.ModuleMember.SpecInclude(_))
      case Ast.ModuleMember.SpecInit(node) => 
        transformNode(specInitNode(in, node), Ast.ModuleMember.SpecInit(_))
      case Ast.ModuleMember.SpecLoc(node) => 
        transformNode(specLocNode(in, node), Ast.ModuleMember.SpecLoc(_))
    }

  final def matchTopologyMemberNode(in: In, tmn: Ast.TopologyMember.Node): Result[Ast.TopologyMember.Node] =
    tmn match {
      case Ast.TopologyMember.SpecCompInstance(node) => 
        transformNode(specCompInstanceNode(in, node), Ast.TopologyMember.SpecCompInstance(_))
      case Ast.TopologyMember.SpecConnectionGraph(node) => 
        transformNode(specConnectionGraphNode(in, node), Ast.TopologyMember.SpecConnectionGraph(_))
      case Ast.TopologyMember.SpecInclude(node) => 
        transformNode(specIncludeNode(in, node), Ast.TopologyMember.SpecInclude(_))
      case Ast.TopologyMember.SpecTopImport(node) => 
        transformNode(specTopImportNode(in, node), Ast.TopologyMember.SpecTopImport(_))
      case Ast.TopologyMember.SpecUnusedPorts(node) => 
        transformNode(specUnusedPortsNode(in, node), Ast.TopologyMember.SpecUnusedPorts(_))
    }

  final def matchTuMemberNode(in: In, tumn: Ast.TUMember.Node): Result[Ast.TUMember.Node] = 
    matchModuleMemberNode(in, tumn)

  final def matchTypeName(in: In, node: AstNode[Ast.TypeName]): ResultNode[Ast.TypeName] =
    node.getData match {
      case Ast.TypeNameBool => typeNameBoolNode(in, node)
      case tn @ Ast.TypeNameFloat(_) => typeNameFloatNode(in, node, tn)
      case tn @ Ast.TypeNameInt(_) => typeNameIntNode(in, node, tn)
      case tn @ Ast.TypeNameQualIdent(_) => typeNameQualIdentNode(in, node, tn)
      case Ast.TypeNameString => typeNameStringNode(in, node)
    }

  private def transformNode[In,Out](rn: ResultNode[In], f: AstNode[In] => Out): Result[Out] =
    rn match {
      case Right((b, node)) => Right((b, f(node)))
      case Left(e) => Left(e)
    }

}
