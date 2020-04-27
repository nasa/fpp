package fpp.compiler.ast

import fpp.compiler.util._

object Ast {

  /** Annotated AST value */
  type Annotated[T] = (List[String], T, List[String])

  /** Formal parameter list */
  type FormalParamList = List[Annotated[AstNode[FormalParam]]]

  /** Identifier */
  type Ident = String

  /** A qualified identifier represented as a list of identifier nodes */
  type IdentNodeList = List[AstNode[Ident]]

  object IdentNodeList {

    /** Split a qualified identifier list into qualifier and name */
    def split(list: IdentNodeList) = list.reverse match {
      case head :: tail => (tail.reverse, head)
      case Nil => throw InternalError("qualified identifier should not be empty")
    }

    /** Get the qualifier */
    def qualifier(list: IdentNodeList) = split(list)._1

    /** Get the unqualified name*/
    def name(list: IdentNodeList) = split(list)._2

  }

  sealed trait QidNew
  object QidNew {
    case class Unqualified(name: Ident) extends QidNew
    case class Qualified(qualifier: AstNode[QidNew], name: AstNode[Ident]) extends QidNew

    def fromList(list: IdentNodeList): QidNew =
      IdentNodeList.split(list) match {
        case (Nil, name) => QidNew.Unqualified(name.getData)
        case (qualifier, name) => {
          val qualifier1 = fromList(qualifier)
          val node = AstNode.create(qualifier1, IdentNodeList.name(qualifier).getId)
          QidNew.Qualified(node, name)
        }
      }
  }
  object QidNewNode {
    def fromList(list: IdentNodeList): AstNode[QidNew] =
      IdentNodeList.split(list) match {
        case (Nil, name) => AstNode.create(QidNew.Unqualified(name.getData), name.getId)
        case (qualifier, name) => {
          val qualifier1 = fromList(qualifier)
          val qidNew = QidNew.Qualified(qualifier1, name)
          val node = AstNode.create(qidNew)
          val loc = Locations.get(qualifier1.getId)
          Locations.put(node.getId, loc)
          node
        }
      }
    def toQid(node: AstNode[QidNew]): IdentNodeList = {
      node.data match {
        case QidNew.Unqualified(name) => {
          val node1 = AstNode.create(name, node.getId)
          List(node1)
        }
        case QidNew.Qualified(qualifier, name) => toQid(qualifier) ++ List(name)
      }
    }
  }

  /** Translation unit */
  final case class TransUnit(members: List[TUMember])

  /** Binary operation */
  sealed trait Binop
  final object Binop {
    final case object Add extends Binop
    final case object Div extends Binop
    final case object Mul extends Binop
    final case object Sub extends Binop
  }

  /** Component kind */
  sealed trait ComponentKind
  final object ComponentKind {
    final case object Active extends ComponentKind
    final case object Passive extends ComponentKind
    final case object Queued extends ComponentKind
  }

  /** Component member */
  final case class ComponentMember(node: Annotated[ComponentMember.Node])
  object ComponentMember {
    sealed trait Node
    final case class DefArray(node: AstNode[Ast.DefArray]) extends Node
    final case class DefConstant(node: AstNode[Ast.DefConstant]) extends Node
    final case class DefEnum(node: AstNode[Ast.DefEnum]) extends Node
    final case class DefStruct(node: AstNode[Ast.DefStruct]) extends Node
    final case class SpecCommand(node: AstNode[Ast.SpecCommand]) extends Node
    final case class SpecEvent(node: AstNode[Ast.SpecEvent]) extends Node
    final case class SpecInclude(node: AstNode[Ast.SpecInclude]) extends Node
    final case class SpecInternalPort(node: AstNode[Ast.SpecInternalPort]) extends Node
    final case class SpecParam(node: AstNode[Ast.SpecParam]) extends Node
    final case class SpecPortInstance(node: AstNode[Ast.SpecPortInstance]) extends Node
    final case class SpecTlmChannel(node: AstNode[Ast.SpecTlmChannel]) extends Node
  }

  /** Abstract type definition */
  final case class DefAbsType(name: Ident)

  /* Array definition */
  final case class DefArray(
    name: Ident,
    size: AstNode[Expr],
    eltType: AstNode[TypeName],
    default: Option[AstNode[Expr]],
    format: Option[AstNode[String]]
  )

  /** Component definition */
  final case class DefComponent(
    kind: ComponentKind,
    name: Ident,
    members: List[ComponentMember]
  )

  /** Component instance definition */
  final case class DefComponentInstance(
    name: Ident,
    component: AstNode[QidNew],
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
    constants: List[Annotated[AstNode[DefEnumConstant]]]
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
  final case class ModuleMember(node: Annotated[ModuleMember.Node])
  object ModuleMember {
    sealed trait Node
    final case class DefAbsType(node: AstNode[Ast.DefAbsType]) extends Node
    final case class DefArray(node: AstNode[Ast.DefArray]) extends Node
    final case class DefComponent(node: AstNode[Ast.DefComponent]) extends Node
    final case class DefComponentInstance(node: AstNode[Ast.DefComponentInstance]) extends Node
    final case class DefConstant(node: AstNode[Ast.DefConstant]) extends Node
    final case class DefEnum(node: AstNode[Ast.DefEnum]) extends Node
    final case class DefModule(node: AstNode[Ast.DefModule]) extends Node
    final case class DefPort(node: AstNode[Ast.DefPort]) extends Node
    final case class DefStruct(node: AstNode[Ast.DefStruct]) extends Node
    final case class DefTopology(node: AstNode[Ast.DefTopology]) extends Node
    final case class SpecInclude(node: AstNode[Ast.SpecInclude]) extends Node
    final case class SpecInit(node: AstNode[Ast.SpecInit]) extends Node
    final case class SpecLoc(node: AstNode[Ast.SpecLoc]) extends Node
  }

  /** Port definition */
  final case class DefPort(
    name: Ident,
    params: FormalParamList,
    returnType: Option[AstNode[TypeName]]
  )

  /** Struct definition */
  final case class DefStruct(
    name: Ident,
    members: List[Annotated[AstNode[StructTypeMember]]],
    default: Option[AstNode[Expr]]
  )

  /** Expression */
  sealed trait Expr
  final case class ExprArray(elts: List[AstNode[Expr]]) extends Expr
  final case class ExprBinop(e1: AstNode[Expr], op: Binop, e2: AstNode[Expr]) extends Expr
  final case class ExprDot(e: AstNode[Expr], id: AstNode[Ident]) extends Expr
  final case class ExprIdent(value: Ident) extends Expr
  final case class ExprLiteralBool(value: LiteralBool) extends Expr
  final case class ExprLiteralInt(value: String) extends Expr
  final case class ExprLiteralFloat(value: String) extends Expr
  final case class ExprLiteralString(value: String) extends Expr
  final case class ExprParen(e: AstNode[Expr]) extends Expr
  final case class ExprStruct(members: List[StructMember]) extends Expr
  final case class ExprUnop(op: Unop, e: AstNode[Expr]) extends Expr

  /** Topology defintion */
  final case class DefTopology(
    name: Ident,
    members: List[TopologyMember]
  )

  /** Topology member */
  final case class TopologyMember(node: Annotated[TopologyMember.Node])
  object TopologyMember {
    sealed trait Node
    final case class SpecCompInstance(node: AstNode[Ast.SpecCompInstance]) extends Node
    final case class SpecConnectionGraph(node: AstNode[Ast.SpecConnectionGraph]) extends Node
    final case class SpecInclude(node: AstNode[Ast.SpecInclude]) extends Node
    final case class SpecTopImport(node: AstNode[Ast.SpecTopImport]) extends Node
    final case class SpecUnusedPorts(node: AstNode[Ast.SpecUnusedPorts]) extends Node
  }

  /** Formal parameter */
  final case class FormalParam(
    kind: FormalParam.Kind,
    name: Ident,
    typeName: AstNode[TypeName],
    size: Option[AstNode[Expr]]
  )
  final object FormalParam {
    /** Formal parameter kind */
    sealed trait Kind
    final case object Ref extends Kind
    final case object Value extends Kind
  }

  /** Literal bool */
  sealed trait LiteralBool
  object LiteralBool {
    final case object True extends LiteralBool
    final case object False extends LiteralBool
  }

  /** Port instance identifier */
  final case class PortInstanceIdentifier(
    componentInstance: AstNode[QidNew],
    portName: AstNode[Ident]
  )

  /** Queue full behavior */
  sealed trait QueueFull
  final object QueueFull {
    final case object Assert extends QueueFull
    final case object Block extends QueueFull
    final case object Drop extends QueueFull
  }

  /** Command specifier */
  final case class SpecCommand(
    kind: SpecCommand.Kind,
    name: Ident,
    params: FormalParamList,
    opcode: Option[AstNode[Expr]],
    priority: Option[AstNode[Expr]],
    queueFull: Option[AstNode[QueueFull]]
  )
  object SpecCommand {
    sealed trait Kind
    final case object Async extends Kind
    final case object Guarded extends Kind
    final case object Sync extends Kind
  }

  /** Component instance specifier */
  final case class SpecCompInstance(
    visibility: Visibility,
    instance: AstNode[QidNew]
  )

  /** Connection graph specifier */
  sealed trait SpecConnectionGraph
  final object SpecConnectionGraph {

    final case class Direct(
      name: Ident,
      connections: List[Connection]
    ) extends SpecConnectionGraph

    final case class Pattern(
      source: AstNode[QidNew],
      targets: List[AstNode[QidNew]],
      pattern: AstNode[Expr]
    ) extends SpecConnectionGraph

    /** Connection */
    final case class Connection(
      fromPort: AstNode[PortInstanceIdentifier],
      fromIndex: Option[AstNode[Expr]],
      toPort: AstNode[PortInstanceIdentifier],
      toIndex: Option[AstNode[Expr]]
    )
  }

  /** Event specifier */
  final case class SpecEvent(
    name: Ident,
    params: FormalParamList,
    severity: SpecEvent.Severity,
    id: Option[AstNode[Expr]],
    format: Option[AstNode[String]],
    throttle: Option[AstNode[Expr]]
  )
  final object SpecEvent {
    /** Event severity */
    sealed trait Severity
    final case object ActivityHigh extends Severity
    final case object ActivityLow extends Severity
    final case object Command extends Severity
    final case object Diagnostic extends Severity
    final case object Fatal extends Severity
    final case object WarningHigh extends Severity
    final case object WarningLow extends Severity
  }

  /** Include specifier */
  final case class SpecInclude(file: AstNode[String])

  /** Init specifier */
  final case class SpecInit(
    instance: AstNode[QidNew],
    phase: AstNode[Expr],
    code: String
  )

  /** Internal port specifier */
  final case class SpecInternalPort(
    name: Ident,
    params: FormalParamList,
    priority: Option[AstNode[Expr]],
    queueFull: Option[QueueFull]
  )

  /** Location specifier */
  final case class SpecLoc(
    kind: SpecLoc.Kind,
    symbol: AstNode[QidNew],
    file: AstNode[String]
  )
  object SpecLoc {
    /** Location specifier kind */
    sealed trait Kind
    final case object Component extends Kind
    final case object ComponentInstance extends Kind
    final case object Constant extends Kind
    final case object Port extends Kind
    final case object Topology extends Kind
    final case object Type extends Kind
  }

  /** Parameter specifier */
  final case class SpecParam(
    name: Ident,
    typeName: AstNode[TypeName],
    default: Option[AstNode[Expr]],
    id: Option[AstNode[Expr]],
    setOpcode:  Option[AstNode[Expr]],
    saveOpcode:  Option[AstNode[Expr]]
  )

  /** Port instance specifier */
  sealed trait SpecPortInstance
  object SpecPortInstance {

    /** General port instance */
    final case class General(
      kind: GeneralKind,
      name: Ident,
      size: Option[AstNode[Expr]],
      port: Option[AstNode[QidNew]],
      priority: Option[AstNode[Expr]],
      queueFull: Option[QueueFull]
    ) extends SpecPortInstance

    /** General port instance kind */
    sealed trait GeneralKind
    final case object AsyncInput extends GeneralKind
    final case object GuardedInput extends GeneralKind
    final case object Output extends GeneralKind
    final case object SyncInput extends GeneralKind

    /** Special port instance */
    final case class Special (
      kind: SpecialKind,
      name: Ident
    ) extends SpecPortInstance

    /** Special port instance kind */
    sealed trait SpecialKind
    final case object CommandRecv extends SpecialKind
    final case object CommandReg extends SpecialKind
    final case object CommandResp extends SpecialKind
    final case object Event extends SpecialKind
    final case object ParamGet extends SpecialKind
    final case object ParamSet extends SpecialKind
    final case object Telemetry extends SpecialKind
    final case object TextEvent extends SpecialKind
    final case object TimeGet extends SpecialKind

  }

  /** Telemetry channel specifier */
  final case class SpecTlmChannel(
    name: Ident,
    typeName: AstNode[TypeName],
    id: Option[AstNode[Expr]],
    update: Option[SpecTlmChannel.Update],
    format: Option[AstNode[String]],
    low: List[SpecTlmChannel.Limit],
    high: List[SpecTlmChannel.Limit]
  )
  final object SpecTlmChannel {

    /** Telemetry update */
    sealed trait Update
    final case object Always extends Update
    final case object OnChange extends Update

    /** Telemetry limit */
    type Limit = (LimitKind, AstNode[Expr])

    /** Telemetry limit kind */
    sealed trait LimitKind
    final case object Red extends LimitKind
    final case object Orange extends LimitKind
    final case object Yellow extends LimitKind

  }

  /** Topology import specifier */
  final case class SpecTopImport(top: AstNode[QidNew])

  /** Unused port specifier */
  final case class SpecUnusedPorts(ports: List[AstNode[PortInstanceIdentifier]])

  /** Struct member */
  final case class StructMember(name: Ident, value: AstNode[Expr])

  /** Struct type member */
  final case class StructTypeMember(
    name: Ident,
    typeName: AstNode[TypeName],
    format: Option[AstNode[String]]
  )

  /** Translation unit member */
  type TUMember = ModuleMember
  val TUMember = ModuleMember

  /** Float type */
  sealed trait TypeFloat
  final case class F32() extends TypeFloat
  final case class F64() extends TypeFloat

  /** Int type */
  sealed trait TypeInt
  final case class I8() extends TypeInt
  final case class I16() extends TypeInt
  final case class I32() extends TypeInt
  final case class I64() extends TypeInt
  final case class U8() extends TypeInt
  final case class U16() extends TypeInt
  final case class U32() extends TypeInt
  final case class U64() extends TypeInt

  /** Type name */
  sealed trait TypeName
  final case class TypeNameFloat(name: TypeFloat) extends TypeName
  final case class TypeNameInt(name: TypeInt) extends TypeName
  final case class TypeNameQualIdent(name: AstNode[QidNew]) extends TypeName
  final case object TypeNameBool extends TypeName
  final case object TypeNameString extends TypeName

  /** Unary operation */
  sealed trait Unop
  final object Unop {
    final case object Minus extends Unop
  }

  /** Visibility */
  sealed trait Visibility
  final object Visibility {
    final case object Private extends Visibility
    final case object Public extends Visibility
  }

}
