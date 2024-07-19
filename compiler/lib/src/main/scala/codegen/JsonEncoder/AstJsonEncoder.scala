package fpp.compiler.codegen

import fpp.compiler.ast._
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._

/** JSON encoder for Ast objects */
object AstJsonEncoder extends JsonEncoder {

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
  implicit val exprEncoder: Encoder[Ast.Expr] =
    io.circe.generic.semiauto.deriveEncoder[Ast.Expr]

  // JSON encoder for module member nodes
  implicit val moduleMemberNodeEncoder: Encoder[Ast.ModuleMember.Node] =
    io.circe.generic.semiauto.deriveEncoder[Ast.ModuleMember.Node]

  // JSON encoder for qualified identifiers
  implicit val qualIdentEncoder: Encoder[Ast.QualIdent] =
    io.circe.generic.semiauto.deriveEncoder[Ast.QualIdent]

  // JSON encoder for state machine member nodes
  implicit val stateMachineMemberNodeEncoder: Encoder[Ast.StateMachineMember.Node] =
    io.circe.generic.semiauto.deriveEncoder[Ast.StateMachineMember.Node]

  // JSON encoder for type names
  implicit val typeNameEncoder: Encoder[Ast.TypeName] =
    io.circe.generic.semiauto.deriveEncoder[Ast.TypeName]

  // ----------------------------------------------------------------------
  // Encoders for skipping the node field in member lists
  // This reduces clutter in the output
  // ----------------------------------------------------------------------

  // JSON encoder for component members
  private implicit val componentMemberEncoder: Encoder[Ast.ComponentMember] =
    Encoder.instance((m: Ast.ComponentMember) => m.node.asJson)

  // JSON encoder for module members
  private implicit val moduleMemberEncoder: Encoder[Ast.ModuleMember] =
    Encoder.instance((m: Ast.ModuleMember) => m.node.asJson)

  // JSON encoder for state machine members
  private implicit val stateMachineMemberEncoder: Encoder[Ast.StateMachineMember] =
    Encoder.instance((m: Ast.StateMachineMember) => m.node.asJson)

  // JSON encoder for state members
  private implicit val stateMemberEncoder: Encoder[Ast.StateMember] =
    Encoder.instance((m: Ast.StateMember) => m.node.asJson)

  // JSON encoder for topology members
  private implicit val topologyMemberEncoder: Encoder[Ast.TopologyMember] =
    Encoder.instance((m: Ast.TopologyMember) => m.node.asJson)

  // ----------------------------------------------------------------------
  // The public encoder interface
  // ----------------------------------------------------------------------

  /** Converts Ast to JSON */
  def astToJson(tul: List[Ast.TransUnit]): Json = tul.asJson

}
