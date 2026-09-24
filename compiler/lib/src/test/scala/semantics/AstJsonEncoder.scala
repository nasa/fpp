package fpp.compiler.test

import fpp.compiler.ast._
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._

/** JSON encoder for Ast objects
 *
 *  This exists only to support testing. Encoding the AST to JSON gives a
 *  generic way to walk every node of a translation unit, which lets a test
 *  check a whole-AST invariant (for example, that every AstNode id is unique
 *  after template expansion) without writing a visitor for each node type.
 *  Nothing in the compiler proper encodes the AST as JSON.
 */
object AstJsonEncoder {

  /** Encodes a value of Option type
   *
   *  Without this, Circe erases None to null and Some(x) to the encoding of x,
   *  which loses the distinction between an absent node and a present one.
   */
  private implicit def optionEncoder[A](implicit encoder: Encoder[A]): Encoder[Option[A]] = {
    case Some(value) => Json.obj("Some" -> encoder(value))
    case None => Json.fromString("None")
  }

  // JSON encoder for AST nodes
  private implicit def astNodeEncoder[T: Encoder]: Encoder[AstNode[T]] =
    new Encoder[AstNode[T]] {
      override def apply(astNode: AstNode[T]): Json = Json.obj(
        "AstNode" -> Json.obj(
          "data" -> astNode.data.asJson,
          "id" -> astNode.id.asJson
        )
      )
    }

  // ----------------------------------------------------------------------
  // Encoders for helping Circe with recursive types
  // ----------------------------------------------------------------------

  // JSON encoder for expressions
  implicit lazy val exprEncoder: Encoder[Ast.Expr] =
    io.circe.generic.semiauto.deriveEncoder[Ast.Expr]

  // JSON encoder for event throttle
  implicit val eventThrottleEncoder: Encoder[Ast.EventThrottle] =
    io.circe.generic.semiauto.deriveEncoder[Ast.EventThrottle]

  // JSON encoder for module member nodes
  implicit lazy val moduleMemberNodeEncoder: Encoder[Ast.ModuleMember.Node] =
    io.circe.generic.semiauto.deriveEncoder[Ast.ModuleMember.Node]

  // JSON encoder for qualified identifiers
  implicit lazy val qualIdentEncoder: Encoder[Ast.QualIdent] =
    io.circe.generic.semiauto.deriveEncoder[Ast.QualIdent]

  // JSON encoder for state machine member nodes
  implicit lazy val stateMachineMemberNodeEncoder: Encoder[Ast.StateMachineMember.Node] =
    io.circe.generic.semiauto.deriveEncoder[Ast.StateMachineMember.Node]

  // JSON encoder for state member nodes
  implicit lazy val stateMemberNodeEncoder: Encoder[Ast.StateMember.Node] =
    io.circe.generic.semiauto.deriveEncoder[Ast.StateMember.Node]

  // JSON encoder for type names
  implicit lazy val typeNameEncoder: Encoder[Ast.TypeName] =
    io.circe.generic.semiauto.deriveEncoder[Ast.TypeName]

  // ----------------------------------------------------------------------
  // Encoders for skipping the node field in member lists
  // This reduces clutter in the output
  // ----------------------------------------------------------------------

  // JSON encoder for component members
  private lazy implicit val componentMemberEncoder: Encoder[Ast.ComponentMember] =
    Encoder.instance((m: Ast.ComponentMember) => m.node.asJson)

  // JSON encoder for module members
  private lazy implicit val moduleMemberEncoder: Encoder[Ast.ModuleMember] =
    Encoder.instance((m: Ast.ModuleMember) => m.node.asJson)

  // JSON encoder for interface members
  private implicit val interfaceMemberEncoder: Encoder[Ast.InterfaceMember] =
    Encoder.instance((m: Ast.InterfaceMember) => m.node.asJson)

  // JSON encoder for state machine members
  private lazy implicit val stateMachineMemberEncoder: Encoder[Ast.StateMachineMember] =
    Encoder.instance((m: Ast.StateMachineMember) => m.node.asJson)

  // JSON encoder for state members
  private lazy implicit val stateMemberEncoder: Encoder[Ast.StateMember] =
    Encoder.instance((m: Ast.StateMember) => m.node.asJson)

  // JSON encoder for tlm packet set members
  private implicit val tlmPacketSetMember: Encoder[Ast.TlmPacketSetMember] =
    Encoder.instance((m: Ast.TlmPacketSetMember) => m.node.asJson)

  // JSON encoder for topology members
  private lazy implicit val topologyMemberEncoder: Encoder[Ast.TopologyMember] =
    Encoder.instance((m: Ast.TopologyMember) => m.node.asJson)

  // ----------------------------------------------------------------------
  // The public encoder interface
  // ----------------------------------------------------------------------

  /** Converts Ast to JSON */
  def astToJson(tul: List[Ast.TransUnit]): Json = tul.asJson

}
