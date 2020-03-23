package fpp.compiler.ast

/** Visit an AST with unit input */
trait AstUnitVisitor extends AstVisitor {

  type In = Unit

  def default: Out

  def defAbsTypeNode(node: AstNode[Ast.DefAbsType]): Out = default

  def defArrayNode(node: AstNode[Ast.DefArray]): Out = default

  def defComponentNode(node: AstNode[Ast.DefComponent]): Out = default

  def defComponentInstanceNode(node: AstNode[Ast.DefComponentInstance]): Out = default

  def defConstantNode(node: AstNode[Ast.DefConstant]): Out = default

  def defEnumNode(node: AstNode[Ast.DefEnum]): Out = default

  def defModuleNode(node: AstNode[Ast.DefModule]): Out = default

  def defPortNode(node: AstNode[Ast.DefPort]): Out = default

  def defStructNode(node: AstNode[Ast.DefStruct]): Out = default

  def defTopologyNode(node: AstNode[Ast.DefTopology]): Out = default

  def exprArrayNode(node: AstNode[Ast.Expr], e: Ast.ExprArray): Out = default

  def exprBinopNode(node: AstNode[Ast.Expr], e: Ast.ExprBinop): Out = default

  def exprDotNode(node: AstNode[Ast.Expr], e: Ast.ExprDot): Out = default

  def exprIdentNode(node: AstNode[Ast.Expr], e: Ast.ExprIdent): Out = default

  def exprLiteralBoolNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool): Out = default

  def exprLiteralFloatNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat): Out = default

  def exprLiteralIntNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt): Out = default

  def exprLiteralStringNode(node: AstNode[Ast.Expr], e: Ast.ExprLiteralString): Out = default

  def exprParenNode(node: AstNode[Ast.Expr], e: Ast.ExprParen): Out = default

  def exprStructNode(node: AstNode[Ast.Expr], e: Ast.ExprStruct): Out = default

  def exprUnopNode(node: AstNode[Ast.Expr], e: Ast.ExprUnop): Out = default

  def specCommandNode(node: AstNode[Ast.SpecCommand]): Out = default

  def specCompInstanceNode(node: AstNode[Ast.SpecCompInstance]): Out = default

  def specConnectionGraphNode(node: AstNode[Ast.SpecConnectionGraph]): Out = default

  def specEventNode(node: AstNode[Ast.SpecEvent]): Out = default

  def specIncludeNode(node: AstNode[Ast.SpecInclude]): Out = default

  def specInitNode(node: AstNode[Ast.SpecInit]): Out = default

  def specInternalPortNode(node: AstNode[Ast.SpecInternalPort]): Out = default

  def specLocNode(node: AstNode[Ast.SpecLoc]): Out = default

  def specParamNode(node: AstNode[Ast.SpecParam]): Out = default
  
  def specPortInstanceNode(node: AstNode[Ast.SpecPortInstance]): Out = default

  def specTlmChannelNode(node: AstNode[Ast.SpecTlmChannel]): Out = default

  def specTopImportNode(node: AstNode[Ast.SpecTopImport]): Out = default

  def specUnusedPortsNode(node: AstNode[Ast.SpecUnusedPorts]): Out = default
  
  def transUnit(tu: Ast.TransUnit): Out = default

  def typeNameBoolNode(node: AstNode[Ast.TypeName]): Out = default

  def typeNameFloatNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): Out = default

  def typeNameIntNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): Out = default

  def typeNameQualIdentNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): Out = default

  def typeNameStringNode(node: AstNode[Ast.TypeName]): Out = default

  final def matchComponentMemberNode(cmn: Ast.ComponentMember.Node): Out =
    matchComponentMemberNode((), cmn)

  final def matchExprNode(node: AstNode[Ast.Expr]): Out = matchExprNode((), node)

  final def matchModuleMemberNode(mmn: Ast.ModuleMember.Node): Out =
    matchModuleMemberNode((), mmn)

  final def matchTopologyMemberNode(tmn: Ast.TopologyMember.Node): Out =
    matchTopologyMemberNode((), tmn)

  final def matchTuMemberNode(tumn: Ast.TUMember.Node): Out =
    matchTuMemberNode((), tumn)

  final def matchTypeNameNode(node: AstNode[Ast.TypeName]): Out = matchTypeNameNode((), node)

  final override def default(u: Unit): Out = default

  final override def defAbsTypeNode(a: Unit, node: AstNode[Ast.DefAbsType]): Out = 
    defAbsTypeNode(node)

  final override def defArrayNode(a: Unit, node: AstNode[Ast.DefArray]): Out = 
    defArrayNode(node)

  final override def defComponentNode(a: Unit, node: AstNode[Ast.DefComponent]): Out = 
    defComponentNode(node)

  final override def defComponentInstanceNode(a: Unit, node: AstNode[Ast.DefComponentInstance]): Out = 
    defComponentInstanceNode(node)

  final override def defConstantNode(a: Unit, node: AstNode[Ast.DefConstant]): Out = 
    defConstantNode(node)

  final override def defEnumNode(a: Unit, node: AstNode[Ast.DefEnum]): Out = 
    defEnumNode(node)

  final override def defModuleNode(a: Unit, node: AstNode[Ast.DefModule]): Out = 
    defModuleNode(node)

  final override def defPortNode(a: Unit, node: AstNode[Ast.DefPort]): Out = 
    defPortNode(node)

  final override def defStructNode(a: Unit, node: AstNode[Ast.DefStruct]): Out = 
    defStructNode(node)

  final override def defTopologyNode(a: Unit, node: AstNode[Ast.DefTopology]): Out = 
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

  final override def specCommandNode(a: Unit, node: AstNode[Ast.SpecCommand]): Out = 
    specCommandNode(node)

  final override def specCompInstanceNode(a: Unit, node: AstNode[Ast.SpecCompInstance]): Out = 
    specCompInstanceNode(node)

  final override def specConnectionGraphNode(a: Unit, node: AstNode[Ast.SpecConnectionGraph]): Out = 
    specConnectionGraphNode(node)

  final override def specEventNode(a: Unit, node: AstNode[Ast.SpecEvent]): Out = 
    specEventNode(node)

  final override def specIncludeNode(a: Unit, node: AstNode[Ast.SpecInclude]): Out = 
    specIncludeNode(node)

  final override def specInitNode(a: Unit, node: AstNode[Ast.SpecInit]): Out = 
    specInitNode(node)

  final override def specInternalPortNode(a: Unit, node: AstNode[Ast.SpecInternalPort]): Out = 
    specInternalPortNode(node)

  final override def specLocNode(a: Unit, node: AstNode[Ast.SpecLoc]): Out = 
    specLocNode(node)

  final override def specParamNode(a: Unit, node: AstNode[Ast.SpecParam]): Out = 
    specParamNode(node)

  final override def specPortInstanceNode(a: Unit, node: AstNode[Ast.SpecPortInstance]): Out = 
    specPortInstanceNode(node)

  final override def specTlmChannelNode(a: Unit, node: AstNode[Ast.SpecTlmChannel]): Out = 
    specTlmChannelNode(node)

  final override def specTopImportNode(a: Unit, node: AstNode[Ast.SpecTopImport]): Out = 
    specTopImportNode(node)

  final override def specUnusedPortsNode(a: Unit, node: AstNode[Ast.SpecUnusedPorts]): Out = 
    specUnusedPortsNode(node)

  final override def transUnit(a: Unit, tu: Ast.TransUnit): Out = 
    transUnit(tu)

  final override def typeNameBoolNode(a: Unit, node: AstNode[Ast.TypeName]): Out = typeNameBoolNode(node)

  final override def typeNameFloatNode(a: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): Out = 
    typeNameFloatNode(node, tn)

  final override def typeNameIntNode(a: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): Out = 
    typeNameIntNode(node, tn)

  final override def typeNameQualIdentNode(a: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): Out = 
    typeNameQualIdentNode(node, tn)

  final override def typeNameStringNode(a: Unit, node: AstNode[Ast.TypeName]): Out = 
    typeNameStringNode(node)

}
