package fpp.compiler.codegen

import fpp.compiler.ast._
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._

/** Encoder for Ast case classes
 *  Note: For recursive type variants, we explicitly handle each case
 *  to avoid an infinite recursion. This appears to be a weakness in Circe when
 *  dealing with recursive types. */
object AstJsonEncoder extends JsonEncoder {

  // JSON encoder for AST nodes
  implicit def astNodeEncoder[T: Encoder]: Encoder[AstNode[T]] = new Encoder[AstNode[T]] {
    override def apply(astNode: AstNode[T]): Json = Json.obj(
      "AstNode" -> Json.obj(
        "data" -> astNode.data.asJson,
        "id" -> astNode.id.asJson
      )
    )
  }

  // JSON encoder for qualified identifiers
  // We explicitly handle each case to avoid infinite recursion. See note above.
  implicit val qualIdentEncoder: Encoder[Ast.QualIdent] =
    Encoder.instance((q: Ast.QualIdent) =>
        q match {
            case ident: Ast.QualIdent.Unqualified => addTypeName(ident, ident.asJson)
            case ident: Ast.QualIdent.Qualified => addTypeName(ident, ident.asJson)
        }
    )

  // JSON encoders for Scala type variants, each of which has one value
  // with no arguments. We use the unqualified class name of the
  // type to represent the value.
  implicit val binopEncoder: Encoder[Ast.Binop] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val componentKindEncoder: Encoder[Ast.ComponentKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val formalParamKindEncoder: Encoder[Ast.FormalParam.Kind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val generalKindEncoder: Encoder[Ast.SpecPortInstance.GeneralKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val limitKindEncoder: Encoder[Ast.SpecTlmChannel.LimitKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val patternEncoder: Encoder[Ast.SpecConnectionGraph.Pattern.Kind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val queueFullEncoder: Encoder[Ast.QueueFull] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val severityEncoder: Encoder[Ast.SpecEvent.Severity] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val specialInputKindEncoder: Encoder[Ast.SpecPortInstance.SpecialInputKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val specialKindEncoder: Encoder[Ast.SpecPortInstance.SpecialKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val typeFloatEncoder: Encoder[Ast.TypeFloat] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val typeIntEncoder: Encoder[Ast.TypeInt] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val unopEncoder: Encoder[Ast.Unop] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val updateEncoder: Encoder[Ast.SpecTlmChannel.Update] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val visibilityEncoder: Encoder[Ast.Visibility] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))


  // JSON encoder for expressions
  // We explicitly handle each case to avoid infinite recursion. See note above.
  implicit val exprEncoder: Encoder[Ast.Expr] =
    Encoder.instance(
      (e: Ast.Expr) => e match {
        case expr: Ast.ExprArray         => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprBinop         => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprDot           => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprParen         => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprIdent         => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprStruct        => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprUnop          => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralInt    => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralBool   => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralFloat  => addTypeName(expr, expr.asJson)
        case expr: Ast.ExprLiteralString => addTypeName(expr, expr.asJson)
      }
    )

  // JSON encoder for type names
  // We explicitly handle each case to avoid infinite recursion. See note above.
  implicit val typeNameEncoder: Encoder[Ast.TypeName] =
    Encoder.instance(
      (t: Ast.TypeName) => t match {
        case t: Ast.TypeNameFloat     => addTypeName(t, t.asJson)
        case t: Ast.TypeNameInt       => addTypeName(t, t.asJson)
        case t: Ast.TypeNameQualIdent => addTypeName(t, t.asJson)
        case t: Ast.TypeNameString    => addTypeName(t, t.asJson)
        case Ast.TypeNameBool         => addTypeName(t, Json.fromString("TypeNameBool"))
      }
    )
  
  // JSON encoder for module member nodes
  // We explicitly handle each case to avoid infinite recursion. See note above.
  implicit val moduleMemberNodeEncoder: Encoder[Ast.ModuleMember.Node] =
    Encoder.instance(
      (node: Ast.ModuleMember.Node) => node match {
        case node: Ast.ModuleMember.DefAbsType => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefArray => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefComponent => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefComponentInstance => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefConstant => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefEnum => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefModule => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefPort => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefStruct => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.DefTopology => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.SpecInclude => addTypeName(node, node.asJson)
        case node: Ast.ModuleMember.SpecLoc => addTypeName(node, node.asJson)
      }
    )

  // JSON encoder for module members
  // Skip the "node" key to reduce clutter
  implicit val moduleMemberEncoder: Encoder[Ast.ModuleMember] =
    Encoder.instance((m: Ast.ModuleMember) => m.node.asJson)
  
  // JSON encoder for component member nodes
  // We explicitly handle each case to avoid infinite recursion. See note above.
  implicit val componentMemberNodeEncoder: Encoder[Ast.ComponentMember.Node] =
    Encoder.instance(
      (node: Ast.ComponentMember.Node) => node match {
        case node: Ast.ComponentMember.DefAbsType => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.DefArray => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.DefConstant => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.DefEnum => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.DefStruct => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecCommand => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecEvent => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecInclude => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecInternalPort => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecParam => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecPortInstance => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecPortMatching => addTypeName(node, node.asJson)
        case node: Ast.ComponentMember.SpecTlmChannel => addTypeName(node, node.asJson)
      }
    )

  // JSON encoder for component members
  // Skip the "node" key to reduce clutter
  implicit val componentMemberEncoder: Encoder[Ast.ComponentMember] =
    Encoder.instance((m: Ast.ComponentMember) => m.node.asJson)

  // JSON encoder for topology member nodes
  // We explicitly handle each case to avoid infinite recursion. See note above.
  implicit val topologyMemberNodeEncoder: Encoder[Ast.TopologyMember.Node] =
    Encoder.instance(
      (node: Ast.TopologyMember.Node) => node match {
        case node: Ast.TopologyMember.SpecCompInstance => addTypeName(node, node.asJson)
        case node: Ast.TopologyMember.SpecConnectionGraph => addTypeName(node, node.asJson)
        case node: Ast.TopologyMember.SpecInclude => addTypeName(node, node.asJson)
        case node: Ast.TopologyMember.SpecTopImport => addTypeName(node, node.asJson)
      }
    )
  
  // JSON encoder for topology members
  // Skip the "node" key to reduce clutter
  implicit val topologyMemberEncoder: Encoder[Ast.TopologyMember] =
    Encoder.instance((m: Ast.TopologyMember) => m.node.asJson)

  /** Converts Ast to JSON */
  def astToJson(tul: List[Ast.TransUnit]): Json = tul.asJson

}
