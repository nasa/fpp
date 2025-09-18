package fpp.compiler.ast

/** Visit an AST */
trait AstVisitor {

  type In

  type Out

  def default(in: In): Out

  def defAbsTypeAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefAbsType]]): Out = default(in)

  def defAliasTypeAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefAliasType]]): Out = default(in)

  def defActionAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefAction]]): Out = default(in)

  def defArrayAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefArray]]): Out = default(in)

  def defChoiceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefChoice]]): Out = default(in)

  def defComponentAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefComponent]]): Out = default(in)

  def defInterfaceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefInterface]]): Out = default(in)

  def defComponentInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]): Out = default(in)

  def defConstantAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefConstant]]): Out = default(in)

  def defEnumAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefEnum]]): Out = default(in)

  def defGuardAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefGuard]]): Out = default(in)

  def defModuleAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefModule]]): Out = default(in)

  def defPortAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefPort]]): Out = default(in)

  def defSignalAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefSignal]]): Out = default(in)

  def defStateAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefState]]): Out = default(in)

  def defStateMachineAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefStateMachine]]): Out =
    node._2.data.members match {
      case Some(members) => defStateMachineAnnotatedNodeInternal(in, node, members)
      case None => defStateMachineAnnotatedNodeExternal(in, node)
    }

  def defStateMachineAnnotatedNodeExternal(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ): Out = default(in)

  def defStateMachineAnnotatedNodeInternal(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ): Out = default(in)

  def defStructAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefStruct]]): Out = default(in)

  def defTopologyAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefTopology]]): Out = default(in)

  def exprArrayNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprArray): Out = default(in)

  def exprArraySubscriptNode(in: In, node: AstNode[Ast.Expr], e: Ast.ExprArraySubscript): Out = default(in)

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

  def specInitialTransitionAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]): Out = default(in)

  def specInternalPortAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecInternalPort]]): Out = default(in)

  def specLocAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecLoc]]): Out = default(in)

  def specParamAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecParam]]): Out = default(in)

  def specPortInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): Out = default(in)

  def specPortMatchingAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecPortMatching]]): Out = default(in)

  def specRecordAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecRecord]]): Out = default(in)

  def specStateEntryAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecStateEntry]]): Out = default(in)

  def specStateExitAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecStateExit]]): Out = default(in)

  def specStateMachineInstanceAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]): Out = default(in)

  def specStateTransitionAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecStateTransition]]): Out = default(in)

  def specTlmChannelAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]): Out = default(in)

  def specTlmPacketAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecTlmPacket]]): Out = default(in)

  def specTlmPacketSetAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]): Out = default(in)

  def specTopImportAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecImport]]): Out = default(in)

  def specInterfaceImportAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.SpecImport]]): Out = default(in)

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
      case Ast.ComponentMember.DefAliasType(node1) => defAliasTypeAnnotatedNode(in, (pre, node1, post))
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
      case Ast.ComponentMember.SpecImportInterface(node1) => specInterfaceImportAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchInterfaceMember(in: In, member: Ast.InterfaceMember): Out = {
    val (pre, node, post) =  member.node
    node match {
      case Ast.InterfaceMember.SpecPortInstance(node1) => specPortInstanceAnnotatedNode(in, (pre, node1, post))
      case Ast.InterfaceMember.SpecImportInterface(node1) => specInterfaceImportAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchExprNode(in: In, node: AstNode[Ast.Expr]): Out =
    node.data match {
      case e : Ast.ExprArray => exprArrayNode(in, node, e)
      case e : Ast.ExprArraySubscript => exprArraySubscriptNode(in, node, e)
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
      case Ast.ModuleMember.DefAliasType(node1) => defAliasTypeAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefArray(node1) => defArrayAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefComponent(node1) => defComponentAnnotatedNode(in, (pre, node1, post))
      case Ast.ModuleMember.DefInterface(node1) => defInterfaceAnnotatedNode(in, (pre, node1, post))
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

  final def matchStateMachineMember(in: In, member: Ast.StateMachineMember): Out = {
    val (pre, node, post) =  member.node
    node match {
      case Ast.StateMachineMember.DefAction(node1) => defActionAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMachineMember.DefGuard(node1) => defGuardAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMachineMember.DefChoice(node1) => defChoiceAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMachineMember.DefSignal(node1) => defSignalAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMachineMember.DefState(node1) => defStateAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMachineMember.SpecInitialTransition(node1) => specInitialTransitionAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchStateMember(in: In, member: Ast.StateMember): Out = {
    val (pre, node, post) =  member.node
    node match {
      case Ast.StateMember.DefChoice(node1) => defChoiceAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMember.DefState(node1) => defStateAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMember.SpecInitialTransition(node1) => specInitialTransitionAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMember.SpecStateEntry(node1) => specStateEntryAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMember.SpecStateExit(node1) => specStateExitAnnotatedNode(in, (pre, node1, post))
      case Ast.StateMember.SpecStateTransition(node1) => specStateTransitionAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchTlmPacketSetMember(in: In, member: Ast.TlmPacketSetMember): Out = {
    val (pre, node, post) =  member.node
    node match {
      case Ast.TlmPacketSetMember.SpecInclude(node1) => specIncludeAnnotatedNode(in, (pre, node1, post))
      case Ast.TlmPacketSetMember.SpecTlmPacket(node1) => specTlmPacketAnnotatedNode(in, (pre, node1, post))
    }
  }

  final def matchTopologyMember(in: In, member: Ast.TopologyMember): Out = {
    val (pre, node, post) =  member.node
    node match {
      case Ast.TopologyMember.SpecCompInstance(node1) => specCompInstanceAnnotatedNode(in, (pre, node1, post))
      case Ast.TopologyMember.SpecConnectionGraph(node1) => specConnectionGraphAnnotatedNode(in, (pre, node1, post))
      case Ast.TopologyMember.SpecInclude(node1) => specIncludeAnnotatedNode(in, (pre, node1, post))
      case Ast.TopologyMember.SpecTlmPacketSet(node1) => specTlmPacketSetAnnotatedNode(in, (pre, node1, post))
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
