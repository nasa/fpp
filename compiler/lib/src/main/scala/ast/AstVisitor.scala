package fpp.compiler.ast

/** Visit an AST */
trait AstVisitor[A, B] {

  def componentMember(a: A, cm: Ast.ComponentMember): B

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

  def expr(a: A, e: Ast.Expr): B

  def specCommand(a: A, sc: Ast.SpecCommand): B

  def specEvent(a: A, se: Ast.SpecEvent): B

  def specInclude(a: A, si: Ast.SpecInclude): B

  def specInit(a: A, si: Ast.SpecInit): B

  def specInternalPort(a: A, sip: Ast.SpecInternalPort): B

  def specLoc(a: A, sl: Ast.SpecLoc): B

  def specParam(a: A, sp: Ast.SpecParam): B

  def specPortInstance(a: A, spi: Ast.SpecPortInstance): B

  def specTlmChannel(a: A, stc: Ast.SpecTlmChannel): B

  def structMember(a: A, sm: Ast.StructMember): B

  def structTypeMember(a: A, stm: Ast.StructTypeMember): B

  def transUnit(a: A, tu: Ast.TransUnit): B

  def tuMember(a: A, tum: Ast.TUMember): B

  def typeName(a: A, tn: Ast.TypeName): B

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

  final def matchTuMemberNode(a: A, tumn: Ast.TUMember.Node):B  = matchModuleMemberNode(a, tumn)

}
