package fpp.compiler.ast

import fpp.compiler.util._

/** Transform an AST */
trait AstTransformer {

  type In

  type Out

  type Result[T] = Result.Result[(Out, T)]

  type ResultNode[T] = Result[AstNode[T]]

  type ResultAnnotatedNode[T] = Result[Ast.Annotated[AstNode[T]]]

  def default(in: In): Out

  def defAbsTypeAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefAbsType]]
  ): ResultAnnotatedNode[Ast.DefAbsType] =
    Right(default(in), node)

  def defArrayAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefArray]]): ResultAnnotatedNode[Ast.DefArray] =
    Right(default(in), node)

  def defComponentAnnotatedNode(in: In, node: Ast.Annotated[AstNode[Ast.DefComponent]]): ResultAnnotatedNode[Ast.DefComponent] =
    Right(default(in), node)

  def defComponentInstanceAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ): ResultAnnotatedNode[Ast.DefComponentInstance] = Right(default(in), node)

  def defConstantAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefConstant]]
  ): ResultAnnotatedNode[Ast.DefConstant] = Right(default(in), node)

  def defEnumAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefEnum]]
  ): ResultAnnotatedNode[Ast.DefEnum] = Right(default(in), node)

  def defModuleAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ): ResultAnnotatedNode[Ast.DefModule] = Right(default(in), node)

  def defPortAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefPort]]
  ): ResultAnnotatedNode[Ast.DefPort] = Right(default(in), node)

  def defStateMachineAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ): ResultAnnotatedNode[Ast.DefStateMachine] = Right(default(in), node)

  def defStructAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefStruct]]
  ): ResultAnnotatedNode[Ast.DefStruct] = Right(default(in), node)

  def defTopologyAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefTopology]]
  ): ResultAnnotatedNode[Ast.DefTopology] = Right(default(in), node)

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

  def specCommandAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecCommand]]
  ): ResultAnnotatedNode[Ast.SpecCommand] = Right(default(in), node)

  def specCompInstanceAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecCompInstance]]
  ): ResultAnnotatedNode[Ast.SpecCompInstance] = Right(default(in), node)

  def specConnectionGraphAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]
  ): ResultAnnotatedNode[Ast.SpecConnectionGraph] = Right(default(in), node)

  def specContainerAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecContainer]]
  ): ResultAnnotatedNode[Ast.SpecContainer] = Right(default(in), node)

  def specEventAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecEvent]]
  ): ResultAnnotatedNode[Ast.SpecEvent] = Right(default(in), node)

  def specIncludeAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecInclude]]
  ): ResultAnnotatedNode[Ast.SpecInclude] = Right(default(in), node)

  def specInitAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecInit]]
  ): ResultAnnotatedNode[Ast.SpecInit] = Right(default(in), node)

  def specInternalPortAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecInternalPort]]
  ): ResultAnnotatedNode[Ast.SpecInternalPort] = Right(default(in), node)

  def specLocAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ): ResultAnnotatedNode[Ast.SpecLoc] = Right(default(in), node)

  def specParamAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecParam]]
  ): ResultAnnotatedNode[Ast.SpecParam] = Right(default(in), node)

  def specPortInstanceAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ): ResultAnnotatedNode[Ast.SpecPortInstance] = Right(default(in), node)

  def specPortMatchingAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecPortMatching]]
  ): ResultAnnotatedNode[Ast.SpecPortMatching] = Right(default(in), node)

  def specRecordAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecRecord]]
  ): ResultAnnotatedNode[Ast.SpecRecord] = Right(default(in), node)

  def specStateMachineInstanceAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecStateMachineInstance]]
  ): ResultAnnotatedNode[Ast.SpecStateMachineInstance] = Right(default(in), node)

  def specTlmChannelAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]
  ): ResultAnnotatedNode[Ast.SpecTlmChannel] = Right(default(in), node)

  def specTopImportAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.SpecTopImport]]
  ): ResultAnnotatedNode[Ast.SpecTopImport] = Right(default(in), node)

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

  def typeNameStringNode(in: In, node: AstNode[Ast.TypeName], tn: Ast.TypeNameString): ResultNode[Ast.TypeName] =
    Right(default(in), node)

  final def matchComponentMember(in: In, member: Ast.ComponentMember): Result[Ast.ComponentMember] = {
    def transform[T](
      result: ResultAnnotatedNode[T],
      f: AstNode[T] => Ast.ComponentMember.Node
    ) = {
      for { pair <- result } yield {
        val (out, (pre, node, post)) = pair
        (out, Ast.ComponentMember(pre, f(node), post))
      }
    }
    val (pre, node, post) =  member.node
    node match {
      case Ast.ComponentMember.DefAbsType(node1) =>
        transform(defAbsTypeAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.DefAbsType(_))
      case Ast.ComponentMember.DefArray(node1) =>
        transform(defArrayAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.DefArray(_))
      case Ast.ComponentMember.DefConstant(node1) =>
        transform(defConstantAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.DefConstant(_))
      case Ast.ComponentMember.DefEnum(node1) =>
        transform(defEnumAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.DefEnum(_))
      case Ast.ComponentMember.DefStateMachine(node1) =>
        transform(defStateMachineAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.DefStateMachine(_))
      case Ast.ComponentMember.DefStruct(node1) =>
        transform(defStructAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.DefStruct(_))
      case Ast.ComponentMember.SpecCommand(node1) =>
        transform(specCommandAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecCommand(_))
      case Ast.ComponentMember.SpecContainer(node1) =>
        transform(specContainerAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecContainer(_))
      case Ast.ComponentMember.SpecEvent(node1) =>
        transform(specEventAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecEvent(_))
      case Ast.ComponentMember.SpecInclude(node1) =>
        transform(specIncludeAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecInclude(_))
      case Ast.ComponentMember.SpecInternalPort(node1) =>
        transform(specInternalPortAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecInternalPort(_))
      case Ast.ComponentMember.SpecParam(node1) =>
        transform(specParamAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecParam(_))
      case Ast.ComponentMember.SpecPortInstance(node1) =>
        transform(specPortInstanceAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecPortInstance(_))
      case Ast.ComponentMember.SpecPortMatching(node1) =>
        transform(specPortMatchingAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecPortMatching(_))
      case Ast.ComponentMember.SpecRecord(node1) =>
        transform(specRecordAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecRecord(_))
      case Ast.ComponentMember.SpecStateMachineInstance(node1) =>
        transform(specStateMachineInstanceAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecStateMachineInstance(_))
      case Ast.ComponentMember.SpecTlmChannel(node1) =>
        transform(specTlmChannelAnnotatedNode(in, (pre, node1, post)), Ast.ComponentMember.SpecTlmChannel(_))
    }
  }

  final def matchExprNode(in: In, node: AstNode[Ast.Expr]): ResultNode[Ast.Expr] =
    node.data match {
      case e : Ast.ExprArray => exprArrayNode(in, node, e)
      case e : Ast.ExprBinop => exprBinopNode(in, node, e)
      case e : Ast.ExprDot => exprDotNode(in, node, e)
      case e : Ast.ExprIdent => exprIdentNode(in, node, e)
      case e : Ast.ExprLiteralInt => exprLiteralIntNode(in, node, e)
      case e : Ast.ExprLiteralFloat => exprLiteralFloatNode(in, node, e)
      case e : Ast.ExprLiteralString => exprLiteralStringNode(in, node, e)
      case e : Ast.ExprLiteralBool => exprLiteralBoolNode(in, node, e)
      case e : Ast.ExprParen => exprParenNode(in, node, e)
      case e : Ast.ExprStruct => exprStructNode(in, node, e)
      case e : Ast.ExprUnop => exprUnopNode(in, node, e)
    }

  final def matchModuleMember(in: In, member: Ast.ModuleMember): Result[Ast.ModuleMember] = {
    def transform[T](
      result: ResultAnnotatedNode[T],
      f: AstNode[T] => Ast.ModuleMember.Node
    ) = {
      for { pair <- result } yield {
        val (out, (pre, node, post)) = pair
        (out, Ast.ModuleMember(pre, f(node), post))
      }
    }
    val (pre, node, post) =  member.node
    node match {
      case Ast.ModuleMember.DefAbsType(node1) =>
        transform(defAbsTypeAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefAbsType(_))
      case Ast.ModuleMember.DefArray(node1) =>
        transform(defArrayAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefArray(_))
      case Ast.ModuleMember.DefComponent(node1) =>
        transform(defComponentAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefComponent(_))
      case Ast.ModuleMember.DefComponentInstance(node1) =>
        transform(defComponentInstanceAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefComponentInstance(_))
      case Ast.ModuleMember.DefConstant(node1) =>
        transform(defConstantAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefConstant(_))
      case Ast.ModuleMember.DefEnum(node1) =>
        transform(defEnumAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefEnum(_))
      case Ast.ModuleMember.DefModule(node1) =>
        transform(defModuleAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefModule(_))
      case Ast.ModuleMember.DefPort(node1) =>
        transform(defPortAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefPort(_))
      case Ast.ModuleMember.DefStateMachine(node1) =>
        transform(defStateMachineAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefStateMachine(_))
      case Ast.ModuleMember.DefStruct(node1) =>
        transform(defStructAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefStruct(_))
      case Ast.ModuleMember.DefTopology(node1) =>
        transform(defTopologyAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.DefTopology(_))
      case Ast.ModuleMember.SpecInclude(node1) =>
        transform(specIncludeAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.SpecInclude(_))
      case Ast.ModuleMember.SpecLoc(node1) =>
        transform(specLocAnnotatedNode(in, (pre, node1, post)), Ast.ModuleMember.SpecLoc(_))
    }
  }

  final def matchTopologyMember(in: In, member: Ast.TopologyMember): Result[Ast.TopologyMember] = {
    def transform[T](
      result: ResultAnnotatedNode[T],
      f: AstNode[T] => Ast.TopologyMember.Node
    ) = {
      for (pair <- result) yield {
        val (out, (pre, node, post)) = pair
        (out, Ast.TopologyMember(pre, f(node), post))
      }
    }
    val (pre, node, post) =  member.node
    node match {
      case Ast.TopologyMember.SpecCompInstance(node1) =>
        transform(specCompInstanceAnnotatedNode(in, (pre, node1, post)), Ast.TopologyMember.SpecCompInstance(_))
      case Ast.TopologyMember.SpecConnectionGraph(node1) =>
        transform(specConnectionGraphAnnotatedNode(in, (pre, node1, post)), Ast.TopologyMember.SpecConnectionGraph(_))
      case Ast.TopologyMember.SpecInclude(node1) =>
        transform(specIncludeAnnotatedNode(in, (pre, node1, post)), Ast.TopologyMember.SpecInclude(_))
      case Ast.TopologyMember.SpecTopImport(node1) =>
        transform(specTopImportAnnotatedNode(in, (pre, node1, post)), Ast.TopologyMember.SpecTopImport(_))
    }
  }

  final def matchTuMember(in: In, member: Ast.TUMember): Result[Ast.TUMember] =
    matchModuleMember(in, member)

  final def matchTypeName(in: In, node: AstNode[Ast.TypeName]): ResultNode[Ast.TypeName] =
    node.data match {
      case Ast.TypeNameBool => typeNameBoolNode(in, node)
      case tn : Ast.TypeNameFloat => typeNameFloatNode(in, node, tn)
      case tn : Ast.TypeNameInt => typeNameIntNode(in, node, tn)
      case tn : Ast.TypeNameQualIdent => typeNameQualIdentNode(in, node, tn)
      case tn : Ast.TypeNameString => typeNameStringNode(in, node, tn)
    }

  private def transformNode[In,Out](rn: ResultNode[In], f: AstNode[In] => Out): Result[Out] =
    for (pair <- rn) yield {
      val (out, node) = pair
      (out, f(node))
    }

}
