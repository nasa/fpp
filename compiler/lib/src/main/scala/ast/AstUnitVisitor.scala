package fpp.compiler.ast

/** Visit an AST with unit input */
trait AstUnitVisitor[B] extends AstVisitor[Unit, B] {

  def default: B

  def defAbsTypeNode(node: AstNode[Ast.DefAbsType]): B = default

  def defArrayNode(node: AstNode[Ast.DefArray]): B = default

  def defComponentNode(node: AstNode[Ast.DefComponent]): B = default

  def defComponentInstanceNode(node: AstNode[Ast.DefComponentInstance]): B = default

  def defConstantNode(node: AstNode[Ast.DefConstant]): B = default

  def defEnumNode(node: AstNode[Ast.DefEnum]): B = default

  def defModuleNode(node: AstNode[Ast.DefModule]): B = default

  def defPortNode(node: AstNode[Ast.DefPort]): B = default

  def defStructNode(node: AstNode[Ast.DefStruct]): B = default

  def defTopologyNode(node: AstNode[Ast.DefTopology]): B = default

  def exprArrayNode(node: AstNode[Ast.Expr], e: Ast.ExprArray): B = default

  def exprBinopNode(node: AstNode[Ast.Expr], e: Ast.ExprBinop): B = default

  def exprDotNode(node: AstNode[Ast.Expr], e: Ast.ExprDot): B = default

  def exprIdentNode(node: AstNode[Ast.Expr], e: Ast.ExprIdent): B = default

  def exprLiteralBoolNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool): B = default

  def exprLiteralFloatNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat): B = default

  def exprLiteralIntNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt): B = default

  def exprLiteralStringNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralString): B = default

  def exprParenNode(node: AstNode[Ast.Expr], e: Ast.ExprParen): B = default

  def exprStructNode(node: AstNode[Ast.Expr], e: Ast.ExprStruct): B = default

  def exprUnopNode(node: AstNode[Ast.Expr], e: Ast.ExprUnop): B = default

  def specCommandNode(node: AstNode[Ast.SpecCommand]): B = default

  def specCompInstanceNode(node: AstNode[Ast.SpecCompInstance]): B = default

  def specConnectionGraphNode(node: AstNode[Ast.SpecConnectionGraph]): B = default

  def specEventNode(node: AstNode[Ast.SpecEvent]): B = default

  def specIncludeNode(node: AstNode[Ast.SpecInclude]): B = default

  def specInitNode(node: AstNode[Ast.SpecInit]): B = default

  def specInternalPortNode(node: AstNode[Ast.SpecInternalPort]): B = default

  def specLocNode(node: AstNode[Ast.SpecLoc]): B = default

  def specParamNode(node: AstNode[Ast.SpecParam]): B = default
  
  def specPortInstanceNode(node: AstNode[Ast.SpecPortInstance]): B = default

  def specTlmChannelNode(node: AstNode[Ast.SpecTlmChannel]): B = default

  def specTopImportNode(node: AstNode[Ast.SpecTopImport]): B = default

  def specUnusedPortsNode(node: AstNode[Ast.SpecUnusedPorts]): B = default
  
  def transUnit(tu: Ast.TransUnit): B = default

  def typeNameBoolNode(node: AstNode[Ast.TypeName]): B = default

  def typeNameFloatNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): B = default

  def typeNameIntNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): B = default

  def typeNameQualIdentNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): B = default

  def typeNameStringNode(node: AstNode[Ast.TypeName]): B = default

  final def matchComponentMemberNode(cmn: Ast.ComponentMember.Node): B =
    matchComponentMemberNode((), cmn)

  final def matchExprNode(node: AstNode[Ast.Expr]): B = matchExprNode((), node)

  final def matchModuleMemberNode(mmn: Ast.ModuleMember.Node): B =
    matchModuleMemberNode((), mmn)

  final def matchTopologyMemberNode(tmn: Ast.TopologyMember.Node): B =
    matchTopologyMemberNode((), tmn)

  final def matchTuMemberNode(tumn: Ast.TUMember.Node): B =
    matchTuMemberNode((), tumn)

  final def matchTypeNameNode(node: AstNode[Ast.TypeName]): B = matchTypeNameNode((), node)

  final override def default(u: Unit): B = default

  final override def defAbsTypeNode(a: Unit, node: AstNode[Ast.DefAbsType]): B = 
    defAbsTypeNode(node)

  final override def defArrayNode(a: Unit, node: AstNode[Ast.DefArray]): B = 
    defArrayNode(node)

  final override def defComponentNode(a: Unit, node: AstNode[Ast.DefComponent]): B = 
    defComponentNode(node)

  final override def defComponentInstanceNode(a: Unit, node: AstNode[Ast.DefComponentInstance]): B = 
    defComponentInstanceNode(node)

  final override def defConstantNode(a: Unit, node: AstNode[Ast.DefConstant]): B = 
    defConstantNode(node)

  final override def defEnumNode(a: Unit, node: AstNode[Ast.DefEnum]): B = 
    defEnumNode(node)

  final override def defModuleNode(a: Unit, node: AstNode[Ast.DefModule]): B = 
    defModuleNode(node)

  final override def defPortNode(a: Unit, node: AstNode[Ast.DefPort]): B = 
    defPortNode(node)

  final override def defStructNode(a: Unit, node: AstNode[Ast.DefStruct]): B = 
    defStructNode(node)

  final override def defTopologyNode(a: Unit, node: AstNode[Ast.DefTopology]): B = 
    defTopologyNode(node)

  final override def exprArrayNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprArray) =
    exprArrayNode(node, e)

  final override def exprBinopNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprBinop) =
    exprBinopNode(node, e)

  final override def exprDotNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    exprDotNode(node, e)

  final override def exprIdentNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprIdent) =
    exprIdentNode(node, e)

  final override def exprLiteralBoolNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) =
    exprLiteralBoolNode(node, e)

  final override def exprLiteralIntNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    exprLiteralIntNode(node, e)

  final override def exprLiteralFloatNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    exprLiteralFloatNode(node, e)

  final override def exprLiteralStringNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    exprLiteralStringNode(node, e)

  final override def exprParenNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprParen) =
    exprParenNode(node, e)

  final override def exprStructNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    exprStructNode(node, e)

  final override def exprUnopNode(a: Unit, node: AstNode[Ast.Expr], e: Ast.ExprUnop) =
    exprUnopNode(node, e)

  final override def specCommandNode(a: Unit, node: AstNode[Ast.SpecCommand]): B = 
    specCommandNode(node)

  final override def specCompInstanceNode(a: Unit, node: AstNode[Ast.SpecCompInstance]): B = 
    specCompInstanceNode(node)

  final override def specConnectionGraphNode(a: Unit, node: AstNode[Ast.SpecConnectionGraph]): B = 
    specConnectionGraphNode(node)

  final override def specEventNode(a: Unit, node: AstNode[Ast.SpecEvent]): B = 
    specEventNode(node)

  final override def specIncludeNode(a: Unit, node: AstNode[Ast.SpecInclude]): B = 
    specIncludeNode(node)

  final override def specInitNode(a: Unit, node: AstNode[Ast.SpecInit]): B = 
    specInitNode(node)

  final override def specInternalPortNode(a: Unit, node: AstNode[Ast.SpecInternalPort]): B = 
    specInternalPortNode(node)

  final override def specLocNode(a: Unit, node: AstNode[Ast.SpecLoc]): B = 
    specLocNode(node)

  final override def specParamNode(a: Unit, node: AstNode[Ast.SpecParam]): B = 
    specParamNode(node)

  final override def specPortInstanceNode(a: Unit, node: AstNode[Ast.SpecPortInstance]): B = 
    specPortInstanceNode(node)

  final override def specTlmChannelNode(a: Unit, node: AstNode[Ast.SpecTlmChannel]): B = 
    specTlmChannelNode(node)

  final override def specTopImportNode(a: Unit, node: AstNode[Ast.SpecTopImport]): B = 
    specTopImportNode(node)

  final override def specUnusedPortsNode(a: Unit, node: AstNode[Ast.SpecUnusedPorts]): B = 
    specUnusedPortsNode(node)

  final override def transUnit(a: Unit, tu: Ast.TransUnit): B = 
    transUnit(tu)

  final override def typeNameBoolNode(a: Unit, node: AstNode[Ast.TypeName]): B = typeNameBoolNode(node)

  final override def typeNameFloatNode(a: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): B = 
    typeNameFloatNode(node, tn)

  final override def typeNameIntNode(a: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): B = 
    typeNameIntNode(node, tn)

  final override def typeNameQualIdentNode(a: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): B = 
    typeNameQualIdentNode(node, tn)

  final override def typeNameStringNode(a: Unit, node: AstNode[Ast.TypeName]): B = 
    typeNameStringNode(node)

}
