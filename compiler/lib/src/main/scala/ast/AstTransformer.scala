package fpp.compiler.ast

import fpp.compiler.util._

/** Transform an AST */
trait AstTransformer[A, B] {

  type Result[T] = Result.Result[(B, T)]

  type ResultNode[T] = Result[AstNode[T]]

  def default(a: A): B

  def defAbsTypeNode(a: A, node: AstNode[Ast.DefAbsType]): ResultNode[Ast.DefAbsType] = 
    Right(default(a), node)

  def defArrayNode(a: A, node: AstNode[Ast.DefArray]): ResultNode[Ast.DefArray] =
    Right(default(a), node)

  def defComponentNode(a: A, node: AstNode[Ast.DefComponent]): ResultNode[Ast.DefComponent] =
    Right(default(a), node)

  def defComponentInstanceNode(a: A, node: AstNode[Ast.DefComponentInstance]): ResultNode[Ast.DefComponentInstance] =
    Right(default(a), node)

  def defConstantNode(a: A, node: AstNode[Ast.DefConstant]): ResultNode[Ast.DefConstant] =
    Right(default(a), node)

  def defEnumNode(a: A, node: AstNode[Ast.DefEnum]): ResultNode[Ast.DefEnum] =
    Right(default(a), node)

  def defModuleNode(a: A, node: AstNode[Ast.DefModule]): ResultNode[Ast.DefModule] =
    Right(default(a), node)

  def defPortNode(a: A, node: AstNode[Ast.DefPort]): ResultNode[Ast.DefPort] =
    Right(default(a), node)

  def defStructNode(a: A, node: AstNode[Ast.DefStruct]): ResultNode[Ast.DefStruct] =
    Right(default(a), node)

  def defTopologyNode(a: A, node: AstNode[Ast.DefTopology]): ResultNode[Ast.DefTopology] =
    Right(default(a), node)

  def exprArray(a: A, elts: List[AstNode[Ast.Expr]]): Result[List[AstNode[Ast.Expr]]] =
    Right(default(a), elts)

  def exprBinop(a: A, e: (Ast.Expr, Ast.Binop, Ast.Expr)): Result[(Ast.Expr, Ast.Binop, Ast.Expr)] =
    Right(default(a), e)
  
  def exprDot(a: A, e: (Ast.Expr, Ast.Ident)): Result[(Ast.Expr, Ast.Ident)] =
    Right(default(a), e)

  def exprIdent(a: A, id: Ast.Ident): Result[Ast.Ident] =
    Right(default(a), id)

  def exprLiteralBool(a: A, lb: Ast.LiteralBool): Result[Ast.LiteralBool] =
    Right(default(a), lb)

  def exprLiteralInt(a: A, s: String): Result[String] =
    Right(default(a), s)

  def exprLiteralString(a: A, s: String): Result[String] =
    Right(default(a), s)

  def exprLiteralFloat(a: A, s: String): Result[String] =
    Right(default(a), s)

  def exprParen(a: A, e: Ast.Expr): Result[Ast.Expr] =
    Right(default(a), e)

  def exprStruct(a: A, sml: List[Ast.StructMember]): Result[List[Ast.StructMember]] =
    Right(default(a), sml)

  def exprUnop(a: A, e: (Ast.Unop, Ast.Expr)): Result[(Ast.Unop, Ast.Expr)] =
    Right(default(a), e)

  def specCommandNode(a: A, node: AstNode[Ast.SpecCommand]): ResultNode[Ast.SpecCommand] =
    Right(default(a), node)

  def specCompInstanceNode(a: A, node: AstNode[Ast.SpecCompInstance]): ResultNode[Ast.SpecCompInstance] =
    Right(default(a), node)

  def specConnectionGraphNode(a: A, node: AstNode[Ast.SpecConnectionGraph]): ResultNode[Ast.SpecConnectionGraph] =
    Right(default(a), node)

  def specEventNode(a: A, node: AstNode[Ast.SpecEvent]): ResultNode[Ast.SpecEvent] =
    Right(default(a), node)

  def specIncludeNode(a: A, node: AstNode[Ast.SpecInclude]): ResultNode[Ast.SpecInclude] =
    Right(default(a), node)

  def specInitNode(a: A, node: AstNode[Ast.SpecInit]): ResultNode[Ast.SpecInit] =
    Right(default(a), node)

  def specInternalPortNode(a: A, node: AstNode[Ast.SpecInternalPort]): ResultNode[Ast.SpecInternalPort] =
    Right(default(a), node)

  def specLocNode(a: A, node: AstNode[Ast.SpecLoc]): ResultNode[Ast.SpecLoc] =
    Right(default(a), node)

  def specParamNode(a: A, node: AstNode[Ast.SpecParam]): ResultNode[Ast.SpecParam] =
    Right(default(a), node)

  def specPortInstanceNode(a: A, node: AstNode[Ast.SpecPortInstance]): ResultNode[Ast.SpecPortInstance] =
    Right(default(a), node)

  def specTlmChannelNode(a: A, node: AstNode[Ast.SpecTlmChannel]): ResultNode[Ast.SpecTlmChannel] =
    Right(default(a), node)

  def specTopImportNode(a: A, node: AstNode[Ast.SpecTopImport]): ResultNode[Ast.SpecTopImport] =
    Right(default(a), node)

  def specUnusedPortsNode(a: A, node: AstNode[Ast.SpecUnusedPorts]): ResultNode[Ast.SpecUnusedPorts] =
    Right(default(a), node)

  def transUnit(a: A, tu: Ast.TransUnit): Result[Ast.TransUnit] =
    Right(default(a), tu)

  def typeNameBool(a: A): Result.Result[B] = Right(default(a))

  def typeNameFloat(a: A, tnf: Ast.TypeNameFloat): Result[Ast.TypeNameFloat] =
    Right(default(a), tnf)

  def typeNameInt(a: A, tni: Ast.TypeNameInt): Result[Ast.TypeNameInt] = 
    Right(default(a), tni)

  def typeNameQualIdent(a: A, tnqid: Ast.TypeNameQualIdent): Result[Ast.TypeNameQualIdent] =
    Right(default(a), tnqid)

  def typeNameString(a: A): Result.Result[B]= Right(default(a))

  /*
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

  final def matchExpr(a: A, e: Ast.Expr): B =
    e match {
      case Ast.ExprBinop(e1, op, e2) => exprBinop(a, e1.getData, op, e2.getData)
      case Ast.ExprArray(enl) => exprArray(a, enl)
      case Ast.ExprDot(en, id) => exprDot(a, en.getData, id)
      case Ast.ExprIdent(id) => exprIdent(a, id)
      case Ast.ExprLiteralInt(s) => exprLiteralInt(a, s)
      case Ast.ExprLiteralFloat(s) => exprLiteralFloat(a, s)
      case Ast.ExprLiteralString(s) => exprLiteralString(a, s)
      case Ast.ExprLiteralBool(lb) => exprLiteralBool(a, lb)
      case Ast.ExprParen(en) => exprParen(a, en.getData)
      case Ast.ExprStruct(sml) => exprStruct(a, sml)
      case Ast.ExprUnop(op, en) => exprUnop(a, op, en.getData)
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

  final def matchTypeName(a: A, tn: Ast.TypeName): B =
    tn match {
      case Ast.TypeNameBool => typeNameBool(a)
      case tnf @ Ast.TypeNameFloat(_) => typeNameFloat(a, tnf)
      case tni @ Ast.TypeNameInt(_) => typeNameInt(a, tni)
      case tnqid @ Ast.TypeNameQualIdent(_) => typeNameQualIdent(a, tnqid)
      case Ast.TypeNameString => typeNameString(a)
    }
    */

}
