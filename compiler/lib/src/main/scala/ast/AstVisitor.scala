package fpp.compiler.ast

/** Visit an AST */
trait AstVisitor[A, B] {

  def defAbsType(a: A, dat: Ast.DefAbsType): B

  def defArray(a: A, da: Ast.DefArray): B

  def defComponent(a: A, dc: Ast.DefComponent): B

  def defComponentInstance(a: A, dci: Ast.DefComponentInstance): B

  def defConstant(a: A, dc: Ast.DefConstant): B

  def defEnum(a: A, de: Ast.DefEnum): B

  def defEnumConstant(a: A, dec: Ast.DefEnumConstant): B

  def defModule(a: A, dm: Ast.DefModule): B

  def defPort(a: A, dp: Ast.DefPort): B

  def defStruct(a: A, ds: Ast.DefStruct): B

  def defTopology(a: A, dt: Ast.DefTopology): B

  def exprArray(a: A, elts: List[AstNode[Ast.Expr]]): B

  def exprBinop(a: A, e1: Ast.Expr, op: Ast.Binop, e2: Ast.Expr): B
  
  def exprDot(a: A, e: Ast.Expr, id: Ast.Ident): B
    /*
  private def exprDot(e: Ast.Expr, id: Ast.Ident) =
    lines("expr dot") ++
    (expr(e) ++ ident(id)).map(indentIn)

  private def exprLiteralBool(lb: Ast.LiteralBool) = {
    val s = lb match {
      case Ast.LiteralBool.True => "true"
      case Ast.LiteralBool.False => "false"
    }
    lines("literal bool " ++ s)
  }

  private def exprParen(e: Ast.Expr) =
    lines("expr paren") ++
    expr(e).map(indentIn)

  private def exprStruct(sml: List[Ast.StructMember]) =
    lines("expr struct") ++
    sml.map(structMember).flatten.map(indentIn)

  private def exprUnop(op: Ast.Unop, e: Ast.Expr) =
    lines("expr unop") ++
    (unop(op) ++ expr(e)).map(indentIn)
    */


  def specCommand(a: A, sc: Ast.SpecCommand): B

  def specEvent(a: A, se: Ast.SpecEvent): B

  def specInclude(a: A, si: Ast.SpecInclude): B

  def specInit(a: A, si: Ast.SpecInit): B

  def specInternalPort(a: A, sip: Ast.SpecInternalPort): B

  def specLoc(a: A, sl: Ast.SpecLoc): B

  def specParam(a: A, sp: Ast.SpecParam): B

  def specPortInstance(a: A, spi: Ast.SpecPortInstance): B

  def specTlmChannel(a: A, stc: Ast.SpecTlmChannel): B

  def transUnit(a: A, tu: Ast.TransUnit): B

  final def matchComponentMemberNode(a: A, cmn: Ast.ComponentMember.Node): B =
    cmn match {
      case Ast.ComponentMember.DefArray(node) => defArray(a, node.getData)
      case Ast.ComponentMember.DefConstant(node) => defConstant(a, node.getData)
      case Ast.ComponentMember.DefEnum(node) => defEnum(a, node.getData)
      case Ast.ComponentMember.DefStruct(node) => defStruct(a, node.getData)
      case Ast.ComponentMember.SpecCommand(node) => specCommand(a, node.getData)
      case Ast.ComponentMember.SpecEvent(node) => specEvent(a, node.getData)
      case Ast.ComponentMember.SpecInclude(node) => specInclude(a, node.getData)
      case Ast.ComponentMember.SpecInternalPort(node) => specInternalPort(a, node.getData)
      case Ast.ComponentMember.SpecParam(node) => specParam(a, node.getData)
      case Ast.ComponentMember.SpecPortInstance(node) => specPortInstance(a, node.getData)
      case Ast.ComponentMember.SpecTlmChannel(node) => specTlmChannel(a, node.getData)
    }

  final def matchModuleMemberNode(a: A, mmn: Ast.ModuleMember.Node): B =
    mmn match {
      case Ast.ModuleMember.DefAbsType(node) => defAbsType(a, node.getData)
      case Ast.ModuleMember.DefArray(node) => defArray(a, node.getData)
      case Ast.ModuleMember.DefComponent(node) => defComponent(a, node.getData)
      case Ast.ModuleMember.DefComponentInstance(node) => defComponentInstance(a, node.getData)
      case Ast.ModuleMember.DefConstant(node) => defConstant(a, node.getData)
      case Ast.ModuleMember.DefEnum(node) => defEnum(a, node.getData)
      case Ast.ModuleMember.DefModule(node) => defModule(a, node.getData)
      case Ast.ModuleMember.DefPort(node) => defPort(a, node.getData)
      case Ast.ModuleMember.DefStruct(node) => defStruct(a, node.getData)
      case Ast.ModuleMember.DefTopology(node) => defTopology(a, node.getData)
      case Ast.ModuleMember.SpecInclude(node) => specInclude(a, node.getData)
      case Ast.ModuleMember.SpecInit(node) => specInit(a, node.getData)
      case Ast.ModuleMember.SpecLoc(node) => specLoc(a, node.getData)
    }

  final def matchTuMemberNode(a: A, tumn: Ast.TUMember.Node): B  = matchModuleMemberNode(a, tumn)

}
