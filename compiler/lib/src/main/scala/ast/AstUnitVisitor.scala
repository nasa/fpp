package fpp.compiler.ast

/** Visit an AST with unit input */
trait AstUnitVisitor extends AstVisitor {

  type In = Unit

  def default: Out

  def defAbsTypeAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefAbsType]]): Out = default

  def defArrayAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefArray]]): Out = default

  def defComponentAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefComponent]]): Out = default

  def defComponentInstanceAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]): Out = default

  def defConstantAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefConstant]]): Out = default

  def defEnumAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefEnum]]): Out = default

  def defModuleAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefModule]]): Out = default

  def defPortAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefPort]]): Out = default

  def defStructAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefStruct]]): Out = default

  def defTopologyAnnotatedNode(node: Ast.Annotated[AstNode[Ast.DefTopology]]): Out = default

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

  def specCommandAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecCommand]]): Out = default

  def specCompInstanceAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]): Out = default

  def specConnectionGraphAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]): Out = default

  def specEventAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecEvent]]): Out = default

  def specIncludeAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecInclude]]): Out = default

  def specInitAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecInit]]): Out = default

  def specInternalPortAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecInternalPort]]): Out = default

  def specLocAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecLoc]]): Out = default

  def specParamAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecParam]]): Out = default
  
  def specPortInstanceAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): Out = default

  def specTlmChannelAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]): Out = default

  def specTopImportAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecTopImport]]): Out = default

  def specUnusedPortsAnnotatedNode(node: Ast.Annotated[AstNode[Ast.SpecUnusedPorts]]): Out = default

  def transUnit(tu: Ast.TransUnit): Out = default

  def typeNameBoolNode(node: AstNode[Ast.TypeName]): Out = default

  def typeNameFloatNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): Out = default

  def typeNameIntNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): Out = default

  def typeNameQualIdentNode(node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): Out = default

  def typeNameStringNode(node: AstNode[Ast.TypeName]): Out = default

  final def matchComponentMember(member: Ast.ComponentMember): Out =
    matchComponentMember((), member)

  final def matchExprNode(node: AstNode[Ast.Expr]): Out = matchExprNode((), node)

  final def matchModuleMember(member: Ast.ModuleMember): Out =
    matchModuleMember((), member)

  final def matchTopologyMember(tm: Ast.TopologyMember): Out =
    matchTopologyMember((), tm)

  final def matchTuMember(member: Ast.TUMember): Out =
    matchTuMember((), member)

  final def matchTypeNameNode(node: AstNode[Ast.TypeName]): Out = matchTypeNameNode((), node)

  final override def default(in: Unit): Out = default

  final override def defAbsTypeAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefAbsType]]): Out = 
    defAbsTypeAnnotatedNode(node)

  final override def defArrayAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefArray]]): Out = 
    defArrayAnnotatedNode(node)

  final override def defComponentAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefComponent]]): Out = 
    defComponentAnnotatedNode(node)

  final override def defComponentInstanceAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]): Out = 
    defComponentInstanceAnnotatedNode(node)

  final override def defConstantAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefConstant]]): Out = 
    defConstantAnnotatedNode(node)

  final override def defEnumAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefEnum]]): Out = 
    defEnumAnnotatedNode(node)

  final override def defModuleAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefModule]]): Out = 
    defModuleAnnotatedNode(node)

  final override def defPortAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefPort]]): Out = 
    defPortAnnotatedNode(node)

  final override def defStructAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefStruct]]): Out = 
    defStructAnnotatedNode(node)

  final override def defTopologyAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.DefTopology]]): Out = 
    defTopologyAnnotatedNode(node)

  final override def exprArrayNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprArray) =
    exprArrayNode(node, e)

  final override def exprBinopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprBinop) =
    exprBinopNode(node, e)

  final override def exprDotNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    exprDotNode(node, e)

  final override def exprIdentNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprIdent) =
    exprIdentNode(node, e)

  final override def exprLiteralBoolNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) =
    exprLiteralBoolNode(node, e)

  final override def exprLiteralIntNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    exprLiteralIntNode(node, e)

  final override def exprLiteralFloatNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    exprLiteralFloatNode(node, e)

  final override def exprLiteralStringNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    exprLiteralStringNode(node, e)

  final override def exprParenNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprParen) =
    exprParenNode(node, e)

  final override def exprStructNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    exprStructNode(node, e)

  final override def exprUnopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprUnop) =
    exprUnopNode(node, e)

  final override def specCommandAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecCommand]]): Out = 
    specCommandAnnotatedNode(node)

  final override def specCompInstanceAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]): Out = 
    specCompInstanceAnnotatedNode(node)

  final override def specConnectionGraphAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]): Out = 
    specConnectionGraphAnnotatedNode(node)

  final override def specEventAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecEvent]]): Out = 
    specEventAnnotatedNode(node)

  final override def specIncludeAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecInclude]]): Out = 
    specIncludeAnnotatedNode(node)

  final override def specInitAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecInit]]): Out = 
    specInitAnnotatedNode(node)

  final override def specInternalPortAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecInternalPort]]): Out = 
    specInternalPortAnnotatedNode(node)

  final override def specLocAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecLoc]]): Out = 
    specLocAnnotatedNode(node)

  final override def specParamAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecParam]]): Out = 
    specParamAnnotatedNode(node)

  final override def specPortInstanceAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): Out = 
    specPortInstanceAnnotatedNode(node)

  final override def specTlmChannelAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]): Out = 
    specTlmChannelAnnotatedNode(node)

  final override def specTopImportAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecTopImport]]): Out = 
    specTopImportAnnotatedNode(node)

  final override def specUnusedPortsAnnotatedNode(in: Unit, node: Ast.Annotated[AstNode[Ast.SpecUnusedPorts]]): Out = 
    specUnusedPortsAnnotatedNode(node)

  final override def transUnit(in: Unit, tu: Ast.TransUnit): Out = 
    transUnit(tu)

  final override def typeNameBoolNode(in: Unit, node: AstNode[Ast.TypeName]): Out = typeNameBoolNode(node)

  final override def typeNameFloatNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): Out = 
    typeNameFloatNode(node, tn)

  final override def typeNameIntNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): Out = 
    typeNameIntNode(node, tn)

  final override def typeNameQualIdentNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): Out = 
    typeNameQualIdentNode(node, tn)

  final override def typeNameStringNode(in: Unit, node: AstNode[Ast.TypeName]): Out = 
    typeNameStringNode(node)

}
