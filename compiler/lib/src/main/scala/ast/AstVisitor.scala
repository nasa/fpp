package fpp.compiler.ast

/** Visit an AST */
trait AstVisitor {

  type In

  type Out

  def default(in: In): Out

  def defAbsTypeNode(in: In, node: AstNode[Ast.DefAbsType]): Out = default(in)

  def defArrayNode(in: In, node: AstNode[Ast.DefArray]): Out = default(in)

  def defComponentNode(in: In, node: AstNode[Ast.DefComponent]): Out = default(in)

  def defComponentInstanceNode(in: In, node: AstNode[Ast.DefComponentInstance]): Out = default(in)

  def defConstantNode(in: In, node: AstNode[Ast.DefConstant]): Out = default(in)

  def defEnumNode(in: In, node: AstNode[Ast.DefEnum]): Out = default(in)

  def defModuleNode(in: In, node: AstNode[Ast.DefModule]): Out = default(in)

  def defPortNode(in: In, node: AstNode[Ast.DefPort]): Out = default(in)

  def defStructNode(in: In, node: AstNode[Ast.DefStruct]): Out = default(in)

  def defTopologyNode(in: In, node: AstNode[Ast.DefTopology]): Out = default(in)

  def exprArrayNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprArray): Out = default(in)

  def exprBinopNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprBinop): Out = default(in)
  
  def exprDotNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprDot): Out = default(in)

  def exprIdentNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprIdent): Out = default(in)

  def exprLiteralBoolNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool): Out = default(in)

  def exprLiteralFloatNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat): Out = default(in)

  def exprLiteralIntNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt): Out = default(in)

  def exprLiteralStringNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString): Out = default(in)

  def exprParenNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprParen): Out = default(in)

  def exprStructNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprStruct): Out = default(in)

  def exprUnopNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprUnop): Out = default(in)

  def specCommandNode(in: In, node: AstNode[Ast.SpecCommand]): Out = default(in)

  def specCompInstanceNode(in: In, node: AstNode[Ast.SpecCompInstance]): Out = default(in)

  def specCompInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]): Out = default(in)

  def specConnectionGraphNode(in: In, node: AstNode[Ast.SpecConnectionGraph]): Out = default(in)

  def specConnectionGraphAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]): Out = default(in)

  def specEventNode(in: In, node: AstNode[Ast.SpecEvent]): Out = default(in)

  def specIncludeNode(in: In, node: AstNode[Ast.SpecInclude]): Out = default(in)

  def specIncludeAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecInclude]]): Out = default(in)

  def specInitNode(in: In, node: AstNode[Ast.SpecInit]): Out = default(in)

  def specInternalPortNode(in: In, node: AstNode[Ast.SpecInternalPort]): Out = default(in)

  def specLocNode(in: In, node: AstNode[Ast.SpecLoc]): Out = default(in)

  def specParamNode(in: In, node: AstNode[Ast.SpecParam]): Out = default(in)

  def specPortInstanceNode(in: In, node: AstNode[Ast.SpecPortInstance]): Out = default(in)

  def specTlmChannelNode(in: In, node: AstNode[Ast.SpecTlmChannel]): Out = default(in)

  def specTopImportNode(in: In, node: AstNode[Ast.SpecTopImport]): Out = default(in)

  def specUnusedPortsNode(in: In, node: AstNode[Ast.SpecUnusedPorts]): Out = default(in)

  def transUnit(in: In, tu: Ast.TransUnit): Out = default(in)

  def typeNameBoolNode(in: In, node: AstNode[Ast.TypeName]): Out = default(in)

  def typeNameFloatNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): Out = default(in)

  def typeNameIntNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): Out = default(in)

  def typeNameQualIdentNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): Out = default(in)

  def typeNameStringNode(in: In, node: AstNode[Ast.TypeName]): Out = default(in)

  final def matchComponentMemberNode(in: In, cmn: Ast.ComponentMember.Node): Out =
    cmn match {
      case Ast.ComponentMember.DefArray(node) => defArrayNode(in, node)
      case Ast.ComponentMember.DefConstant(node) => defConstantNode(in, node)
      case Ast.ComponentMember.DefEnum(node) => defEnumNode(in, node)
      case Ast.ComponentMember.DefStruct(node) => defStructNode(in, node)
      case Ast.ComponentMember.SpecCommand(node) => specCommandNode(in, node)
      case Ast.ComponentMember.SpecEvent(node) => specEventNode(in, node)
      case Ast.ComponentMember.SpecInclude(node) => specIncludeNode(in, node)
      case Ast.ComponentMember.SpecInternalPort(node) => specInternalPortNode(in, node)
      case Ast.ComponentMember.SpecParam(node) => specParamNode(in, node)
      case Ast.ComponentMember.SpecPortInstance(node) => specPortInstanceNode(in, node)
      case Ast.ComponentMember.SpecTlmChannel(node) => specTlmChannelNode(in, node)
    }

  final def matchExprNode(in: In, node: AstNode[Ast.Expr]): Out =
    node.getData match {
      case e @ Ast.ExprBinop(_, _, _) => exprBinopNode(in, node, e)
      case e @ Ast.ExprArray(_) => exprArrayNode(in, node, e)
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

  final def matchModuleMemberNode(in: In, mmn: Ast.ModuleMember.Node): Out =
    mmn match {
      case Ast.ModuleMember.DefAbsType(node) => defAbsTypeNode(in, node)
      case Ast.ModuleMember.DefArray(node) => defArrayNode(in, node)
      case Ast.ModuleMember.DefComponent(node) => defComponentNode(in, node)
      case Ast.ModuleMember.DefComponentInstance(node) => defComponentInstanceNode(in, node)
      case Ast.ModuleMember.DefConstant(node) => defConstantNode(in, node)
      case Ast.ModuleMember.DefEnum(node) => defEnumNode(in, node)
      case Ast.ModuleMember.DefModule(node) => defModuleNode(in, node)
      case Ast.ModuleMember.DefPort(node) => defPortNode(in, node)
      case Ast.ModuleMember.DefStruct(node) => defStructNode(in, node)
      case Ast.ModuleMember.DefTopology(node) => defTopologyNode(in, node)
      case Ast.ModuleMember.SpecInclude(node) => specIncludeNode(in, node)
      case Ast.ModuleMember.SpecInit(node) => specInitNode(in, node)
      case Ast.ModuleMember.SpecLoc(node) => specLocNode(in, node)
    }

  final def matchTopologyMemberNode(in: In, tmn: Ast.TopologyMember.Node): Out =
    tmn match {
      case Ast.TopologyMember.SpecCompInstance(node) => specCompInstanceNode(in, node)
      case Ast.TopologyMember.SpecConnectionGraph(node) => specConnectionGraphNode(in, node)
      case Ast.TopologyMember.SpecInclude(node) => specIncludeNode(in, node)
      case Ast.TopologyMember.SpecTopImport(node) => specTopImportNode(in, node)
      case Ast.TopologyMember.SpecUnusedPorts(node) => specUnusedPortsNode(in, node)
    }

  final def matchTuMemberNode(in: In, tumn: Ast.TUMember.Node): Out = 
    matchModuleMemberNode(in, tumn)

  final def matchTypeNameNode(in: In, node: AstNode[Ast.TypeName]): Out =
    node.getData match {
      case Ast.TypeNameBool => typeNameBoolNode(in, node)
      case tn @ Ast.TypeNameFloat(_) => typeNameFloatNode(in, node, tn)
      case tn @ Ast.TypeNameInt(_) => typeNameIntNode(in, node, tn)
      case tn @ Ast.TypeNameQualIdent(_) => typeNameQualIdentNode(in, node, tn)
      case Ast.TypeNameString => typeNameStringNode(in, node)
    }

}
