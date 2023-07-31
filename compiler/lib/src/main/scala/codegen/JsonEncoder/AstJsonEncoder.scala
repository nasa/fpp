package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._
import scala.util.parsing.input.Position

object AstJsonEncoder extends JsonEncoder {

  // JSON encoder for AST nodes
  implicit def astNodeEncoder[T: Encoder]: Encoder[AstNode[T]] = new Encoder[AstNode[T]] {
    override def apply(astNode: AstNode[T]): Json = Json.obj( "AstNode" -> Json.obj(
      "data" -> astNode.data.asJson,
      "id" -> astNode.id.asJson
    ))
  }

  // JSON encoder for qualified identifiers
  implicit val qualIdentEncoder: Encoder[Ast.QualIdent] =
    Encoder.instance((q: Ast.QualIdent) =>
        q match {
            case ident: Ast.QualIdent.Unqualified =>
            addTypeName(ident, ident.asJson)
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
  // We explicitly handle each case to avoid an infinite recursion.
  // This appears to be a weakness in Circe when dealing with
  // recursive variant types.
  implicit val exprEncoder: Encoder[Ast.Expr] =
    Encoder.instance((e: Ast.Expr) =>
      e match {
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
  // We explicitly handle each case to avoid an infinite recursion.
  // See above.
  implicit val typeNameEncoder: Encoder[Ast.TypeName] =
    Encoder.instance((t: Ast.TypeName) =>
      t match {
        case t: Ast.TypeNameFloat     => addTypeName(t, t.asJson)
        case t: Ast.TypeNameInt       => addTypeName(t, t.asJson)
        case t: Ast.TypeNameQualIdent => addTypeName(t, t.asJson)
        case t: Ast.TypeNameString    => addTypeName(t, t.asJson)
        case Ast.TypeNameBool         => addTypeName(t, Json.fromString("TypeNameBool"))
      }
    )
  
  // JSON encoder for module member nodes
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
  implicit val moduleMemberEncoder: Encoder[Ast.ModuleMember] =
    Encoder.instance((m: Ast.ModuleMember) => m.node.asJson)
  
  // JSON encoder for component members
  implicit val componentMemberEncoder: Encoder[Ast.ComponentMember] =
    Encoder.instance((c: Ast.ComponentMember) =>
      c.node._2 match {
        case aNode: Ast.ComponentMember.DefAbsType =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.DefArray =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.DefConstant =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.DefEnum =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.DefStruct =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecCommand =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecEvent =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecInclude =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecInternalPort =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecParam =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecPortInstance =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecPortMatching =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
        case aNode: Ast.ComponentMember.SpecTlmChannel =>
          (c.node._1, addTypeName(aNode, aNode.asJson), c.node._3).asJson
      }
    )

  // JSON encoder for topology members
  implicit val topologyMemberEncoder: Encoder[Ast.TopologyMember] =
    Encoder.instance((t: Ast.TopologyMember) =>
      t.node._2 match {
        case aNode: Ast.TopologyMember.SpecCompInstance =>
          (t.node._1, addTypeName(aNode, aNode.asJson), t.node._3).asJson
        case aNode: Ast.TopologyMember.SpecConnectionGraph =>
          (t.node._1, addTypeName(aNode, aNode.asJson), t.node._3).asJson
        case aNode: Ast.TopologyMember.SpecInclude =>
          (t.node._1, addTypeName(aNode, aNode.asJson), t.node._3).asJson
        case aNode: Ast.TopologyMember.SpecTopImport =>
          (t.node._1, addTypeName(aNode, aNode.asJson), t.node._3).asJson
      }
    )

  // JSON encoders for annontatable elements that are not members of
  // translation units,  modules, or components

  /*
  implicit val defEnumConstantEncoder: Encoder[Ast.Annotated[AstNode[Ast.DefEnumConstant]]] =
    Encoder.instance { aNode =>
      (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
    }
    */

  implicit val formalParamEncoder: Encoder[Ast.Annotated[AstNode[Ast.FormalParam]]] = Encoder.instance {
    aNode => (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
  }

  implicit val specInitEncoder: Encoder[Ast.Annotated[AstNode[Ast.SpecInit]]] =
    Encoder.instance { aNode =>
      (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
    }

  implicit val structTypeMemberEncoder: Encoder[Ast.Annotated[AstNode[Ast.StructTypeMember]]] =
    Encoder.instance { aNode =>
      (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
    }

  /** Converts Ast to JSON */
  def astToJson(tul: List[Ast.TransUnit]): Json = tul.asJson

}
