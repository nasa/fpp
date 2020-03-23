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

  def exprArray(elts: List[AstNode[Ast.Expr]]): B = default

  def exprBinop(e1: Ast.Expr, op: Ast.Binop, e2: Ast.Expr): B = default

  def exprDot(e: Ast.Expr, id: Ast.Ident): B = default

  def exprIdent(id: Ast.Ident): B = default

  def exprLiteralBool(lb: Ast.LiteralBool): B = default

  def exprLiteralFloat(s: String): B = default

  def exprLiteralInt(s: String): B = default

  def exprLiteralString(s: String): B = default

  def exprParen(e: Ast.Expr): B = default

  def exprStruct(sml: List[Ast.StructMember]): B = default

  def exprUnop(op: Ast.Unop, e: Ast.Expr): B = default

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

  final def matchExpr(e: Ast.Expr): B = matchExpr((), e)

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

  final override def exprArray(a: Unit, elts: List[AstNode[Ast.Expr]]): B = 
    exprArray(elts)

  final override def exprBinop(a: Unit, e1: Ast.Expr, op: Ast.Binop, e2: Ast.Expr): B = 
    exprBinop(e1, op, e2)

  final override def exprDot(a: Unit, e: Ast.Expr, id: Ast.Ident): B = exprDot(e, id)

  final override def exprIdent(a: Unit, id: Ast.Ident): B = exprIdent(id)

  final override def exprLiteralBool(a: Unit, lb: Ast.LiteralBool): B = exprLiteralBool(lb)

  final override def exprLiteralInt(a: Unit, s: String): B = exprLiteralInt(s)

  final override def exprLiteralFloat(a: Unit, s: String): B = exprLiteralFloat(s)

  final override def exprLiteralString(a: Unit, s: String): B = exprLiteralString(s)

  final override def exprParen(a: Unit, e: Ast.Expr): B = exprParen(e)

  final override def exprStruct(a: Unit, sml: List[Ast.StructMember]): B = exprStruct(sml)

  final override def exprUnop(a: Unit, op: Ast.Unop, e: Ast.Expr): B = exprUnop(op, e)

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
