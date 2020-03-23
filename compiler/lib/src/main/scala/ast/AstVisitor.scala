package fpp.compiler.ast

/** Visit an AST */
trait AstVisitor[A, B] {

  def default(a: A): B

  def defAbsTypeNode(a: A, node: AstNode[Ast.DefAbsType]): B = default(a)

  def defArrayNode(a: A, node: AstNode[Ast.DefArray]): B = default(a)

  def defComponentNode(a: A, node: AstNode[Ast.DefComponent]): B = default(a)

  def defComponentInstanceNode(a: A, node: AstNode[Ast.DefComponentInstance]): B = default(a)

  def defConstantNode(a: A, node: AstNode[Ast.DefConstant]): B = default(a)

  def defEnumNode(a: A, node: AstNode[Ast.DefEnum]): B = default(a)

  def defModuleNode(a: A, node: AstNode[Ast.DefModule]): B = default(a)

  def defPortNode(a: A, node: AstNode[Ast.DefPort]): B = default(a)

  def defStructNode(a: A, node: AstNode[Ast.DefStruct]): B = default(a)

  def defTopologyNode(a: A, node: AstNode[Ast.DefTopology]): B = default(a)

  def exprArrayNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprArray): B = default(a)

  def exprBinopNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprBinop): B = default(a)
  
  def exprDotNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprDot): B = default(a)

  def exprIdentNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprIdent): B = default(a)

  def exprLiteralBoolNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool): B = default(a)

  def exprLiteralFloatNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat): B = default(a)

  def exprLiteralIntNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt): B = default(a)

  def exprLiteralStringNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString): B = default(a)

  def exprParenNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprParen): B = default(a)

  def exprStructNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprStruct): B = default(a)

  def exprUnopNode(a: A, node: AstNode[Ast.Expr], e: Ast.ExprUnop): B = default(a)

  def specCommandNode(a: A, node: AstNode[Ast.SpecCommand]): B = default(a)

  def specCompInstanceNode(a: A, node: AstNode[Ast.SpecCompInstance]): B = default(a)

  def specConnectionGraphNode(a: A, node: AstNode[Ast.SpecConnectionGraph]): B = default(a)

  def specEventNode(a: A, node: AstNode[Ast.SpecEvent]): B = default(a)

  def specIncludeNode(a: A, node: AstNode[Ast.SpecInclude]): B = default(a)

  def specInitNode(a: A, node: AstNode[Ast.SpecInit]): B = default(a)

  def specInternalPortNode(a: A, node: AstNode[Ast.SpecInternalPort]): B = default(a)

  def specLocNode(a: A, node: AstNode[Ast.SpecLoc]): B = default(a)

  def specParamNode(a: A, node: AstNode[Ast.SpecParam]): B = default(a)

  def specPortInstanceNode(a: A, node: AstNode[Ast.SpecPortInstance]): B = default(a)

  def specTlmChannelNode(a: A, node: AstNode[Ast.SpecTlmChannel]): B = default(a)

  def specTopImportNode(a: A, node: AstNode[Ast.SpecTopImport]): B = default(a)

  def specUnusedPortsNode(a: A, node: AstNode[Ast.SpecUnusedPorts]): B = default(a)

  def transUnit(a: A, tu: Ast.TransUnit): B = default(a)

  def typeNameBoolNode(a: A, node: AstNode[Ast.TypeName]): B = default(a)

  def typeNameFloatNode(a: A, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): B = default(a)

  def typeNameIntNode(a: A, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): B = default(a)

  def typeNameQualIdentNode(a: A, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): B = default(a)

  def typeNameStringNode(a: A, node: AstNode[Ast.TypeName]): B = default(a)

  final def matchComponentMemberNode(a: A, cmn: Ast.ComponentMember.Node): B =
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

  final def matchExprNode(a: A, node: AstNode[Ast.Expr]): B =
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

  final def matchModuleMemberNode(a: A, mmn: Ast.ModuleMember.Node): B =
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

  final def matchTypeNameNode(a: A, node: AstNode[Ast.TypeName]): B =
    node.getData match {
      case Ast.TypeNameBool => typeNameBoolNode(a, node)
      case tn @ Ast.TypeNameFloat(_) => typeNameFloatNode(a, node, tn)
      case tn @ Ast.TypeNameInt(_) => typeNameIntNode(a, node, tn)
      case tn @ Ast.TypeNameQualIdent(_) => typeNameQualIdentNode(a, node, tn)
      case Ast.TypeNameString => typeNameStringNode(a, node)
    }

}
