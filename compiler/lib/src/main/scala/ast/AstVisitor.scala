package fpp.compiler.ast

/** Visit an AST */
trait AstVisitor {

  type In

  type Out

  def default(in: In): Out

  def defAbsTypeAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefAbsType]]): Out = default(in)

  def defArrayAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefArray]]): Out = default(in)

  def defComponentAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefComponent]]): Out = default(in)

  def defComponentInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]): Out = default(in)

  def defConstantAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefConstant]]): Out = default(in)

  def defEnumAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefEnum]]): Out = default(in)

  def defModuleAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefModule]]): Out = default(in)

  def defPortAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefPort]]): Out = default(in)

  def defStateMachineAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefStateMachine]]): Out = default(in)

  def defStructAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefStruct]]): Out = default(in)

  def defTopologyAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefTopology]]): Out = default(in)

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

  def specCommandAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecCommand]]): Out = default(in)

  def specCompInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]): Out = default(in)

  def specConnectionGraphAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]): Out = default(in)

  def specContainerAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecContainer]]): Out = default(in)

  def specEventAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecEvent]]): Out = default(in)

  def specIncludeAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecInclude]]): Out = default(in)

  def specInitAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecInit]]): Out = default(in)

  def specInternalPortAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecInternalPort]]): Out = default(in)

  def specLocAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecLoc]]): Out = default(in)

  def specParamAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecParam]]): Out = default(in)

  def specPortInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): Out = default(in)

  def specPortMatchingAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecPortMatching]]): Out = default(in)

  def specRecordAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecRecord]]): Out = default(in)

  def specStateMachineInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]): Out = default(in)

  def specTlmChannelAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]): Out = default(in)

  def specTopImportAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecTopImport]]): Out = default(in)

  def transUnit(in: In, tu: Ast.TransUnit): Out = default(in)

  def typeNameBoolNode(in: In, node: AstNode[Ast.TypeName]): Out = default(in)

  def typeNameFloatNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat): Out = default(in)

  def typeNameIntNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt): Out = default(in)

  def typeNameQualIdentNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent): Out = default(in)

  def typeNameStringNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameString): Out = default(in)

  final def matchComponentMember(in: In, member: Ast.ComponentMember): Out = {
    val (pre, node, post) =  member.node
    node match {
      case Ast.ComponentMember.DefAbsType(node1) => defAbsTypeAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.DefArray(node1) => defArrayAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.DefConstant(node1) => defConstantAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.DefEnum(node1) => defEnumAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.DefStateMachine(node1) => defStateMachineAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.DefStruct(node1) => defStructAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecCommand(node1) => specCommandAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecContainer(node1) => specContainerAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecEvent(node1) => specEventAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecInclude(node1) => specIncludeAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecInternalPort(node1) => specInternalPortAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecParam(node1) => specParamAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecPortInstance(node1) => specPortInstanceAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecPortMatching(node1) => specPortMatchingAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecRecord(node1) => specRecordAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecStateMachineInstance(node1) => specStateMachineInstanceAnnotatedNode(in, (pre, node1, post))
      case Ast.ComponentMember.SpecTlmChannel(node1) => specTlmChannelAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchExprNode(in: In, node: AstNode[Ast.Expr]): Out =
    node.data match {
      case e : Ast.ExprArray => exprArrayNode(in, node, e)
      case e : Ast.ExprBinop => exprBinopNode(in, node, e)
      case e : Ast.ExprDot => exprDotNode(in, node, e)
      case e : Ast.ExprIdent => exprIdentNode(in, node, e)
      case e : Ast.ExprLiteralBool => exprLiteralBoolNode(in, node, e)
      case e : Ast.ExprLiteralFloat => exprLiteralFloatNode(in, node, e)
      case e : Ast.ExprLiteralInt => exprLiteralIntNode(in, node, e)
      case e : Ast.ExprLiteralString => exprLiteralStringNode(in, node, e)
      case e : Ast.ExprParen => exprParenNode(in, node, e)
      case e : Ast.ExprStruct => exprStructNode(in, node, e)
      case e : Ast.ExprUnop => exprUnopNode(in, node, e)
    }

  final def matchModuleMember(in: In, member: Ast.ModuleMember): Out = {
    val (pre, node, post) = member.node
    node match {
      case Ast.ModuleMember.DefAbsType(node1) => defAbsTypeAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefArray(node1) => defArrayAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefComponent(node1) => defComponentAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefComponentInstance(node1) => defComponentInstanceAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefConstant(node1) => defConstantAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefEnum(node1) => defEnumAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefModule(node1) => defModuleAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefPort(node1) => defPortAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefStateMachine(node1) => defStateMachineAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefStruct(node1) => defStructAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefTopology(node1) => defTopologyAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.SpecInclude(node1) => specIncludeAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.SpecLoc(node1) => specLocAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchTopologyMember(in: In, member: Ast.TopologyMember): Out = {
    val (pre, node, post) =  member.node
    node match {
      case Ast.TopologyMember.SpecCompInstance(node1) => specCompInstanceAnnotatedNode(in, (pre, node1, post))
      case Ast.TopologyMember.SpecConnectionGraph(node1) => specConnectionGraphAnnotatedNode(in, (pre, node1, post))
      case Ast.TopologyMember.SpecInclude(node1) => specIncludeAnnotatedNode(in, (pre, node1, post))
      case Ast.TopologyMember.SpecTopImport(node1) => specTopImportAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchTuMember(in: In, member: Ast.TUMember): Out =
    matchModuleMember(in, member)

  final def matchTypeNameNode(in: In, node: AstNode[Ast.TypeName]): Out =
    node.data match {
      case Ast.TypeNameBool => typeNameBoolNode(in, node)
      case tn : Ast.TypeNameFloat => typeNameFloatNode(in, node, tn)
      case tn : Ast.TypeNameInt => typeNameIntNode(in, node, tn)
      case tn : Ast.TypeNameQualIdent => typeNameQualIdentNode(in, node, tn)
      case tn : Ast.TypeNameString => typeNameStringNode(in, node, tn)
    }

}
