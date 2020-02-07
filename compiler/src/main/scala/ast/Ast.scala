package fpp.compiler.ast

object Ast {

  /** Annotated AST value */
  type Annotated[T] = (List[String], T, List[String])

  /** Identifier */
  type Ident = String

  /** Translation unit */
  final case class TransUnit(members: List[TUMember])

  /** A binary operation */
  sealed trait Binop
  final object Binop {
    final case object Add extends Binop
    final case object Div extends Binop
    final case object Mul extends Binop
    final case object Sub extends Binop
  }

  final object Component {

    /** Component kind */
    sealed trait Kind
    final object Kind {
      final case object Active
      final case object Passive
      final case object Queued
    }

    /** Component member */
    final case class Member(node: Annotated[MemberNode])

    /** Component member node */
    sealed trait MemberNode
    final case class DefArray(node: AstNode[DefArray]) extends MemberNode
    final case class DefConstant(node: AstNode[DefConstant]) extends MemberNode
    final case class DefEnum(node: AstNode[DefEnum]) extends MemberNode
    final case class DefPortInstance(node: AstNode[DefPortInstance]) extends MemberNode
    final case class DefStruct(node: AstNode[DefStruct]) extends MemberNode

  }

  /** Abstract type definition */
  final case class DefAbsType(id: Ident)

  /* Array definition */
  final case class DefArray(
    name: Ident,
    size: AstNode[Expr],
    eltType: AstNode[TypeName],
    default: Option[AstNode[Expr]]
  )

  /** Component definition */
  final case class DefComponent(
    kind: Component.Kind,
    name: Ident,
    members: List[Component.Member]
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
    members: List[ModuleMember]
  )

  /** Module member */
  type ModuleMember = TUMember

  /** Port definition */
  final case class DefPort(
    name: Ident,
    params: List[Annotated[AstNode[FormalParam]]],
    typeName: Option[AstNode[TypeName]]
  )

  /** Port instance definition */
  sealed trait DefPortInstance
  object DefPortInstance {

    /** General port instance */
    final case class General(
      kind: GeneralKind,
      name: Ident,
      index: Option[AstNode[Expr]],
      portType: List[Ident],
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
  final case class ExprBinop(
    e1: AstNode[Expr],
    op: Binop,
    e2: AstNode[Expr]
  )
  final case class ExprDot(e: AstNode[Expr], id: Ident)
  final case class ExprIdent(value: Ident)
  final case class ExprLiteralBool(value: LiteralBool)
  final case class ExprLiteralInt(value: String)
  final case class ExprLiteralFloat(value: String)
  final case class ExprLiteralString(value: String)
  final case class ExprParent(e: AstNode[Expr])
  final case class ExprStruct(members: List[StructMember])
  final case class ExprUnop(op: Unop, e: AstNode[Expr])

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

  /** Location specifier */
  final case class SpecLoc(
    kind: SpecLocKind,
    symbol: List[Ident],
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

  /** Struct member */
  final case class StructMember(name: Ident, value: AstNode[Expr])

  /** Struct type member */
  final case class StructTypeMember(name: Ident, typeName: AstNode[TypeName])

  /** Translation unit member */
  final case class TUMember(node: Annotated[TUMemberNode])

  /** Translation unit member node */
  sealed trait TUMemberNode
  final case class TUDefAbsType(node: AstNode[DefAbsType]) extends TUMemberNode
  final case class TUDefArray(node: AstNode[DefArray]) extends TUMemberNode
  final case class TUDefComponent(node: AstNode[DefComponent]) extends TUMemberNode
  final case class TUDefConstant(node: AstNode[DefConstant]) extends TUMemberNode
  final case class TUDefEnum(node: AstNode[DefEnum]) extends TUMemberNode
  final case class TUDefModule(node: AstNode[DefModule]) extends TUMemberNode
  final case class TUDefPort(node: AstNode[DefPort]) extends TUMemberNode
  final case class TUDefStruct(node: AstNode[DefStruct]) extends TUMemberNode
  final case class TUSpecLoc(node: AstNode[SpecLoc]) extends TUMemberNode

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
  final case object TypeNameBool extends TypeName
  final case class TypeNameFloat(value: TypeFloat) extends TypeName
  final case class TypeNameInt(value: Int) extends TypeName
  final case class TypeNameQualIdent(value: List[Ident]) extends TypeName
  final case object TypeNameString extends TypeName

  /** Unary operation */
  sealed trait Unop
  final object Unop {
    final case object Minus extends Unop
  }

}
