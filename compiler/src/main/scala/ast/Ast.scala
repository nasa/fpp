package fpp.compiler.ast


object Ast {

  type TODO = Int

  /** Annotated AST value */
  type Annotated[T] = (List[String], T, List[String])

  /** Identifier */
  type Ident = String

  /** Qualified identifier */
  type QualIdent = List[Ident]

  /** Translation unit */
  final case class TransUnit(members: List[Annotated[AstNode[TUMember]]])

  /** A binary operation */
  sealed trait Binop
  final object Binop {
    final case object Add extends Binop
    final case object Div extends Binop
    final case object Mul extends Binop
    final case object Sub extends Binop
  }

  /** Component kind */
  sealed trait ComponentKind
  final case object Active extends ComponentKind
  final case object Passive extends ComponentKind
  final case object Queued extends ComponentKind

  /** Component member */
  sealed trait ComponentMember
  object ComponentMember {
    final case class DefArray(member: DefArray) extends ComponentMember
    final case class DefConstant(member: DefConstant) extends ComponentMember
    final case class DefEnum(member: DefEnum) extends ComponentMember
    final case class DefPortInstance(member: DefPortInstance) extends ComponentMember
    final case class DefStruct(member: DefStruct) extends ComponentMember
    final case class SpecCommand(member: SpecCommand) extends ComponentMember
    final case class SpecEvent(member: SpecEvent) extends ComponentMember
    final case class SpecInternalPort(member: SpecInternalPort) extends ComponentMember
    final case class SpecParam(member: SpecParam) extends ComponentMember
    final case class SpecPortInstance(member: SpecPortInstance) extends ComponentMember
    final case class SpecTlmChannel(member: SpecTlmChannel) extends ComponentMember
  }

  /** Abstract type definition */
  final case class DefAbsType(id: Ident)

  /* Array definition */
  final case class DefArray(
    name: Ident,
    size: AstNode[Expr],
    eltType: AstNode[TypeName],
    default: Option[AstNode[Expr]],
    format: Option[String]
  )

  /** Component definition */
  final case class DefComponent(
    kind: ComponentKind,
    name: Ident,
    members: List[Annotated[AstNode[ComponentMember]]]
  )

  /** Component instance definition */
  final case class DefComponentInstance(
    name: Ident,
    typeName: AstNode[QualIdent],
    baseId: AstNode[Expr],
    queueSize: Option[AstNode[Expr]],
    stackSize: Option[AstNode[Expr]],
    priority: Option[AstNode[Expr]]
  )

  /** Constant definition */
  final case class DefConstant(name: Ident, value: AstNode[Expr])

  /** Enum definition */
  final case class DefEnum(
    name: Ident,
    typeName: Option[AstNode[TypeName]],
    constants: Annotated[List[DefEnumConstant]]
  )

  /** Enum constant definition */
  final case class DefEnumConstant(
    name: Ident,
    value: Option[AstNode[Expr]]
  )

  /** Module definition */
  final case class DefModule(
    name: Ident,
    members: List[Annotated[AstNode[ModuleMember]]]
  )

  /** Module member */
  sealed trait ModuleMember
  object ModuleMember {
    final case class SpecLoc(member: SpecLoc) extends ModuleMember
    final case class DefConstant(member: DefConstant) extends ModuleMember
    final case class DefModule(member: DefModule) extends ModuleMember
    final case class DefArray(member: DefArray) extends ModuleMember
    final case class DefEnum(member: DefEnum) extends ModuleMember
    final case class DefStruct(member: DefStruct) extends ModuleMember
    final case class DefAbsType(member: DefAbsType) extends ModuleMember
    final case class DefPort(member: DefPort) extends ModuleMember
    final case class DefComponent(member: DefComponent) extends ModuleMember
    final case class DefComponentInstance(member: DefComponentInstance) extends ModuleMember
    final case class DefTopology(member: DefTopology) extends ModuleMember
  }

  /** Port definition */
  final case class DefPort(
    name: Ident,
    params: List[Annotated[AstNode[FormalParam]]],
    returnType: Option[AstNode[TypeName]]
  )

  /** Port instance definition */
  sealed trait DefPortInstance
  object DefPortInstance {

    /** General port instance */
    final case class General(
      kind: GeneralKind,
      name: Ident,
      index: Option[AstNode[Expr]],
      portType: AstNode[QualIdent],
      priority: Option[AstNode[Expr]],
      queueFull: Option[QueueFull]
    ) extends DefPortInstance

    /** General port instance kind */
    sealed trait GeneralKind
    final case object AsyncInput extends GeneralKind
    final case object GuardedInput extends GeneralKind
    final case object InternalInput extends GeneralKind
    final case object Output extends GeneralKind
    final case object SyncInput extends GeneralKind

    /** Special port instance */
    final case class Special(
      kind: SpecialKind,
      name: Ident
    ) extends DefPortInstance

    /** Special port instance kind */
    sealed trait SpecialKind
    final case object Command extends SpecialKind
    final case object CommandReg extends SpecialKind
    final case object CommandResp extends SpecialKind
    final case object Event extends SpecialKind
    final case object ParamGet extends SpecialKind
    final case object ParamSet extends SpecialKind
    final case object Telemetry extends SpecialKind
    final case object Time extends SpecialKind

  }

  /** Struct definition */
  final case class DefStruct(
    name: Ident,
    members: List[Annotated[AstNode[StructTypeMember]]],
    default: Option[AstNode[Expr]]
  )

  /** Expression */
  sealed trait Expr
  final case class ExprArray(elts: List[AstNode[Expr]])
  final case class ExprBinop(e1: AstNode[Expr], op: Binop, e2: AstNode[Expr])
  final case class ExprDot(e: AstNode[Expr], id: Ident)
  final case class ExprIdent(value: Ident)
  final case class ExprLiteralBool(value: LiteralBool)
  final case class ExprLiteralInt(value: String)
  final case class ExprLiteralFloat(value: String)
  final case class ExprLiteralString(value: String)
  final case class ExprParen(e: AstNode[Expr])
  final case class ExprStruct(members: List[StructMember])
  final case class ExprUnop(op: Unop, e: AstNode[Expr])

  /** Topology defintion */
  final case class DefTopology(
    name: Ident,
    members: List[Annotated[AstNode[TopologyMember]]]
  )

  /** Topology member */
  sealed trait TopologyMember
  object TopologyMember {
    final case class SpecCompInstance(member: SpecCompInstance) extends TopologyMember
    final case class SpecConnectionGraph(member: SpecConnectionGraph) extends TopologyMember
    final case class SpecTopImport(member: SpecTopImport) extends TopologyMember
    final case class SpecUnusedPorts(member: SpecUnusedPorts) extends TopologyMember
  }

  /** Formal parameter */
  final case class FormalParam(
    kind: FormalParamKind,
    name: Ident,
    typeName: AstNode[TypeName]
  )

  /** Formal parameter kind */
  sealed trait FormalParamKind
  object FormalParam {
    final case object Ref extends FormalParamKind
    final case object Value extends FormalParamKind
  }

  /** Literal bool */
  sealed trait LiteralBool
  final case object True extends LiteralBool
  final case object False extends LiteralBool

  /** Queue full behavior */
  sealed trait QueueFull
  final case object Assert extends QueueFull
  final case object Block extends QueueFull
  final case object Drop extends QueueFull

  /** Command specifier */
  final case class SpecCommand(
    name: Ident,
    params: List[Annotated[AstNode[FormalParam]]],
    opcode: Option[AstNode[Expr]],
    priority: Option[AstNode[Expr]],
    queueFull: Option[AstNode[QueueFull]]
  )

  /** Component instance specifier */
  final case class SpecCompInstance(
    visibility: Visibility,
    instance: AstNode[QualIdent]
  )

  /** Connection graph specifier */
  sealed trait SpecConnectionGraph
  final object SpecConnectionGraph {

    final case class Direct(
      name: Ident,
      connections: List[Connection]
    ) extends SpecConnectionGraph

    final case class Pattern(
      source: AstNode[QualIdent],
      targets: List[AstNode[QualIdent]],
      pattern: AstNode[Expr]
    ) extends SpecConnectionGraph

    final case class Connection(
      fromInstance: AstNode[QualIdent],
      fromPort: Ident,
      fromIndex: Option[AstNode[Expr]],
      toInstance: AstNode[QualIdent],
      toPort: Ident,
      toIndex: Option[AstNode[Expr]]
    )
  }

  /** Event specifier */
  type SpecEvent = TODO

  /** Internal port specifier */
  type SpecInternalPort = TODO

  /** Location specifier */
  final case class SpecLoc(
    kind: SpecLocKind,
    symbol: AstNode[QualIdent],
    path: String
  )

  /** Location specifier kind */
  sealed trait SpecLocKind
  final case object SpecLocConstant
  final case object SpecLocType extends SpecLocKind
  final case object SpecLocPort extends SpecLocKind
  final case object SpecLocComponent extends SpecLocKind
  final case object SpecLocComponentInstance extends SpecLocKind
  final case object SpecLocTopology extends SpecLocKind

  /** Parameter specifier */
  type SpecParam = TODO

  /** Port instance specifier */
  type SpecPortInstance = TODO

  /** Telemetry channel specifier */
  type SpecTlmChannel = TODO

  /** Topology import specifier */
  type SpecTopImport = TODO

  /** Unused port specifier */
  type SpecUnusedPorts = TODO

  /** Struct member */
  final case class StructMember(name: Ident, value: AstNode[Expr])

  /** Struct type member */
  final case class StructTypeMember(name: Ident, typeName: AstNode[TypeName])

  /** Translation unit member */
  sealed trait TUMember
  object TUMember {
    final case class DefAbsType(member: DefAbsType) extends TUMember
    final case class DefArray(member: DefArray) extends TUMember
    final case class DefComponent(member: DefComponent) extends TUMember
    final case class DefConstant(member: DefConstant) extends TUMember
    final case class DefEnum(member: DefEnum) extends TUMember
    final case class DefModule(member: DefModule) extends TUMember
    final case class DefPort(member: DefPort) extends TUMember
    final case class DefStruct(member: DefStruct) extends TUMember
    final case class SpecLoc(member: SpecLoc) extends TUMember
  }

  /** Float type */
  sealed trait TypeFloat
  final case object F32 extends TypeFloat
  final case object F64 extends TypeFloat

  /** Int type */
  sealed trait TypeInt
  final case object I8 extends TypeInt
  final case object I16 extends TypeInt
  final case object I32 extends TypeInt
  final case object I64 extends TypeInt
  final case object U8 extends TypeInt
  final case object U16 extends TypeInt
  final case object U32 extends TypeInt
  final case object U64 extends TypeInt

  /** Type name */
  sealed trait TypeName
  final case class TypeNameFloat(name: TypeFloat) extends TypeName
  final case class TypeNameInt(name: TypeInt) extends TypeName
  final case class TypeNameQualIdent(name: AstNode[QualIdent]) extends TypeName
  final case object TypeNameBool extends TypeName
  final case object TypeNameString extends TypeName

  /** Unary operation */
  sealed trait Unop
  final object Unop {
    final case object Minus extends Unop
  }

  /** Visibility */
  sealed trait Visibility
  final case object Private extends Visibility
  final case object Public extends Visibility

}
