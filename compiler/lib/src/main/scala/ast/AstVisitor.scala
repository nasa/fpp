package fpp.compiler.ast

/** Visit an AST */
trait AstVisitor {

  type In

  type Out

  def default(a: In): Out

  def defAbsTypeNode(a: In, node: AstNode[Ast.DefAbsType]): Out = default(a)

  def defArrayNode(a: In, node: AstNode[Ast.DefArray]): Out = default(a)

  def defComponentNode(a: In, node: AstNode[Ast.DefComponent]): Out = default(a)

  def defComponentInstanceNode(a: In, node: AstNode[Ast.DefComponentInstance]): Out = default(a)

  def defConstantNode(a: In, node: AstNode[Ast.DefConstant]): Out = default(a)

  def defEnumNode(a: In, node: AstNode[Ast.DefEnum]): Out = default(a)

  def defModuleNode(a: In, node: AstNode[Ast.DefModule]): Out = default(a)

  def defPortNode(a: In, node: AstNode[Ast.DefPort]): Out = default(a)

  def defStructNode(a: In, node: AstNode[Ast.DefStruct]): Out = default(a)

  def defTopologyNode(a: In, node: AstNode[Ast.DefTopology]): Out = default(a)

  def exprArrayNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprArray): Out = default(a)

  def exprBinopNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprBinop): Out = default(a)
  
  def exprDotNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprDot): Out = default(a)

  def exprIdentNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprIdent): Out = default(a)

  def exprLiteralBoolNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool): Out = default(a)

  def exprLiteralFloatNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat): Out = default(a)

  def exprLiteralIntNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt): Out = default(a)

  def exprLiteralStringNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString): Out = default(a)

  def exprParenNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprParen): Out = default(a)

  def exprStructNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprStruct): Out = default(a)

  def exprUnopNode(a: In, node: AstNode[Ast.Expr], e: Ast.ExprUnop): Out = default(a)

  def specCommandNode(a: In, node: AstNode[Ast.SpecCommand]): Out = default(a)

  def specCompInstanceNode(a: In, node: AstNode[Ast.SpecCompInstance]): Out = default(a)

  def specConnectionGraphNode(a: In, node: AstNode[Ast.SpecConnectionGraph]): Out = default(a)

  def specEventNode(a: In, node: AstNode[Ast.SpecEvent]): Out = default(a)

  def specIncludeNode(a: In, node: AstNode[Ast.SpecInclude]): Out = default(a)

  def specInitNode(a: In, node: AstNode[Ast.SpecInit]): Out = default(a)

  def specInternalPortNode(a: In, node: AstNode[Ast.SpecInternalPort]): Out = default(a)

  def specLocNode(a: In, node: AstNode[Ast.SpecLoc]): Out = default(a)

  def specParamNode(a: In, node: AstNode[Ast.SpecParam]): Out = default(a)

  def specPortInstanceNode(a: In, node: AstNode[Ast.SpecPortInstance]): Out = default(a)

  def specTlmChannelNode(a: In, node: AstNode[Ast.SpecTlmChannel]): Out = default(a)

  def specTopImportNode(a: In, node: AstNode[Ast.SpecTopImport]): Out = default(a)

  def specUnusedPortsNode(a: In, node: AstNode[Ast.SpecUnusedPorts]): Out = default(a)

  def transUnit(a: In, tu: Ast.TransUnit): Out = default(a)

  def typeNameBoolNode(a: In, node: AstNode[Ast.TypeName]): Out = default(a)

  def typeNameFloatNode(a: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): Out = default(a)

  def typeNameIntNode(a: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): Out = default(a)

  def typeNameQualIdentNode(a: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): Out = default(a)

  def typeNameStringNode(a: In, node: AstNode[Ast.TypeName]): Out = default(a)

  final def matchComponentMemberNode(a: In, cmn: Ast.ComponentMember.Node): Out =
    cmn match {
      case Ast.ComponentMember.DefArray(node) => defArrayNode(a, node)
      case Ast.ComponentMember.DefConstant(node) => defConstantNode(a, node)
      case Ast.ComponentMember.DefEnum(node) => defEnumNode(a, node)
      case Ast.ComponentMember.DefStruct(node) => defStructNode(a, node)
      case Ast.ComponentMember.SpecCommand(node) => specCommandNode(a, node)
      case Ast.ComponentMember.SpecEvent(node) => specEventNode(a, node)
      case Ast.ComponentMember.SpecInclude(node) => specIncludeNode(a, node)
      case Ast.ComponentMember.SpecInternalPort(node) => specInternalPortNode(a, node)
      case Ast.ComponentMember.SpecParam(node) => specParamNode(a, node)
      case Ast.ComponentMember.SpecPortInstance(node) => specPortInstanceNode(a, node)
      case Ast.ComponentMember.SpecTlmChannel(node) => specTlmChannelNode(a, node)
    }

  final def matchExprNode(a: In, node: AstNode[Ast.Expr]): Out =
    node.getData match {
      case e @ Ast.ExprBinop(_, _, _) => exprBinopNode(a, node, e)
      case e @ Ast.ExprArray(_) => exprArrayNode(a, node, e)
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

  final def matchModuleMemberNode(a: In, mmn: Ast.ModuleMember.Node): Out =
    mmn match {
      case Ast.ModuleMember.DefAbsType(node) => defAbsTypeNode(a, node)
      case Ast.ModuleMember.DefArray(node) => defArrayNode(a, node)
      case Ast.ModuleMember.DefComponent(node) => defComponentNode(a, node)
      case Ast.ModuleMember.DefComponentInstance(node) => defComponentInstanceNode(a, node)
      case Ast.ModuleMember.DefConstant(node) => defConstantNode(a, node)
      case Ast.ModuleMember.DefEnum(node) => defEnumNode(a, node)
      case Ast.ModuleMember.DefModule(node) => defModuleNode(a, node)
      case Ast.ModuleMember.DefPort(node) => defPortNode(a, node)
      case Ast.ModuleMember.DefStruct(node) => defStructNode(a, node)
      case Ast.ModuleMember.DefTopology(node) => defTopologyNode(a, node)
      case Ast.ModuleMember.SpecInclude(node) => specIncludeNode(a, node)
      case Ast.ModuleMember.SpecInit(node) => specInitNode(a, node)
      case Ast.ModuleMember.SpecLoc(node) => specLocNode(a, node)
    }

  final def matchTopologyMemberNode(a: In, tmn: Ast.TopologyMember.Node): Out =
    tmn match {
      case Ast.TopologyMember.SpecCompInstance(node) => specCompInstanceNode(a, node)
      case Ast.TopologyMember.SpecConnectionGraph(node) => specConnectionGraphNode(a, node)
      case Ast.TopologyMember.SpecInclude(node) => specIncludeNode(a, node)
      case Ast.TopologyMember.SpecTopImport(node) => specTopImportNode(a, node)
      case Ast.TopologyMember.SpecUnusedPorts(node) => specUnusedPortsNode(a, node)
    }

  final def matchTuMemberNode(a: In, tumn: Ast.TUMember.Node): Out = 
    matchModuleMemberNode(a, tumn)

  final def matchTypeNameNode(a: In, node: AstNode[Ast.TypeName]): Out =
    node.getData match {
      case Ast.TypeNameBool => typeNameBoolNode(a, node)
      case tn @ Ast.TypeNameFloat(_) => typeNameFloatNode(a, node, tn)
      case tn @ Ast.TypeNameInt(_) => typeNameIntNode(a, node, tn)
      case tn @ Ast.TypeNameQualIdent(_) => typeNameQualIdentNode(a, node, tn)
      case Ast.TypeNameString => typeNameStringNode(a, node)
    }

}
