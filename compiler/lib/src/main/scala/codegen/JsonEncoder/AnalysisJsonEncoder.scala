package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._
import io.circe.syntax._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import scala.util.parsing.input.Position
import AstJsonEncoder._
import LocMapJsonEncoder._

object AnalysisJsonEncoder extends JsonEncoder{
  
  // JSON encoder for AST nodes
  private implicit def astNodeEncoder[T: Encoder]: Encoder[AstNode[T]] = new Encoder[AstNode[T]] {
    override def apply(astNode: AstNode[T]): Json = Json.obj("astNodeId" -> astNode.id.asJson)
  }

  // JSON encoder for annotated AST nodes
  private implicit def astNodeAnnotatedEncoder[T: Encoder]: Encoder[Ast.Annotated[AstNode[T]]] =
    new Encoder[Ast.Annotated[AstNode[T]]] {
      override def apply(aNode: Ast.Annotated[AstNode[T]]): Json = aNode._2.asJson
    }

  // JSON encoder for symbols
  private def symbolAsJson(symbol: Symbol) = addTypeName(
    symbol,
    Json.obj(
      "nodeId" -> symbol.getNodeId.asJson,
      "unqualifiedName" -> symbol.getUnqualifiedName.asJson
    )
  )

  private implicit val symbolEncoder: Encoder[Symbol] =
    Encoder.instance(symbolAsJson(_))

  private implicit val portSymbolEncoder: Encoder[Symbol.Port] =
    Encoder.instance(symbolAsJson(_))

  implicit val generalPortInstanceKindEncoder: Encoder[PortInstance.General.Kind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))

  implicit val typeEncoder: Encoder[Type] =
    Encoder.instance((t: Type) =>
      t match {
        case t : Type.PrimitiveInt => Json.obj(getUnqualifiedClassName(t) -> Json.obj("kind" -> t.toString().asJson))
        case t : Type.Float => Json.obj(getUnqualifiedClassName(t) -> Json.obj("kind" -> t.toString().asJson))
        case t : Type.Boolean.type => getUnqualifiedClassName(t).asJson
        case t : Type.String => Json.obj(getUnqualifiedClassName(t) -> Json.obj("size" -> t.size.asJson))
        case t : Type.Integer.type => getUnqualifiedClassName(t).asJson
        case t : Type.AbsType => Json.obj(getUnqualifiedClassName(t) -> Json.obj(
          "defaultValue" -> t.getDefaultValue.asJson,
          "nodeId" -> t.getDefNodeId.asJson, 
          "name" -> t.toString.asJson))
        case t : Type.Array => Json.obj(getUnqualifiedClassName(t) -> Json.obj(
          "defaultValue" -> t.default.asJson,
          "format" -> t.format.asJson,
          "anonArray" -> t.anonArray.asJson,
          "size" -> t.getArraySize.asJson,
          "nodeId" -> t.node._2.id.asJson,
          "name" -> t.node._2.data.name.asJson))
        case t : Type.Enum => Json.obj(getUnqualifiedClassName(t) -> Json.obj(
          "defaultValue" -> t.default.asJson,
          "repType" -> t.repType.asJson,
          "nodeId" -> t.node._2.id.asJson,
          "name" -> t.node._2.data.name.asJson))
        case t : Type.Struct => Json.obj(getUnqualifiedClassName(t) -> Json.obj(
          "defaultValue" -> t.default.asJson,
          "anonStruct" -> t.anonStruct.asJson,
          "sizes" -> t.sizes.asJson,
          "formats" -> t.formats.asJson,
          "nodeId" -> t.node._2.id.asJson,
          "name" -> t.node._2.data.name.asJson))
        case t : Type.AnonArray => Json.obj(getUnqualifiedClassName(t) -> Json.obj(
          "size" -> t.size.asJson, 
          "eltType" -> t.eltType.asJson 
          ))
        case t : Type.AnonStruct => Json.obj(getUnqualifiedClassName(t) -> Json.obj(
          "members" -> t.members.asJson
          ))
      }
    )

  implicit val enumConstantEncoder: Encoder[Value.EnumConstant] = new Encoder[Value.EnumConstant] {
    override def apply(enumConstant: Value.EnumConstant): Json = Json.obj(
      "name" -> enumConstant.value._1.asJson,
      "value" -> enumConstant.value._2.asJson,
      "type" -> enumConstant.t.asJson
    ) 
  }

  implicit val valueArrayEncoder: Encoder[Value.Array] = new Encoder[Value.Array] {
    override def apply(array: Value.Array): Json = Json.obj(
      "anonArray" -> array.anonArray.asJson,
    )
  }
  implicit val valueAnonArrayEncoder: Encoder[Value.AnonArray] = new Encoder[Value.AnonArray] {
    override def apply(array: Value.AnonArray): Json = Json.obj(
      "elements" -> array.elements.asJson,
    )
  }

  implicit val valueStructEncoder: Encoder[Value.Struct] = new Encoder[Value.Struct] {
    override def apply(struct: Value.Struct): Json = Json.obj(
      "anonStruct" -> struct.anonStruct.asJson,
    )
  }
  implicit val valueAnonStructEncoder: Encoder[Value.AnonStruct] = new Encoder[Value.AnonStruct] {
    override def apply(struct: Value.AnonStruct): Json = Json.obj(
      "members" -> struct.members.asJson
    )
  }

  implicit val portInstanceIdentifierEncoder: Encoder[PortInstanceIdentifier] = new Encoder[PortInstanceIdentifier] {
    override def apply(port: PortInstanceIdentifier): Json = Json.obj(
      "name" -> port.toString.asJson,
      "componentInstance" -> port.componentInstance.asJson,
      "portInstance" -> port.portInstance.asJson
    )
  }

  implicit val symbolMapEncoder: Encoder[Map[Symbol, Symbol]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.getNodeId
        }
        .toMap
        .asJson
    }
  
  implicit val scopeMapEncoder: Encoder[Map[Symbol, Scope]] = Encoder.instance {
    symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
  }
  
  implicit val componentMapEncoder: Encoder[Map[Symbol.Component, Component]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
    }

  
  implicit val specialKindMapEncoder
      : Encoder[Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  
  implicit val commandMapEncoder: Encoder[Map[Command.Opcode, Command]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  
  implicit val tlmChannelMapEncoder: Encoder[Map[TlmChannel.Id, TlmChannel]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  
  implicit val eventMapEncoder: Encoder[Map[Event.Id, Event]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  
  implicit val paramMapEncoder: Encoder[Map[Param.Id, Param]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  
  implicit val componentInstanceMapEncoder
      : Encoder[Map[Symbol.ComponentInstance, ComponentInstance]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
    }

 
  implicit val topologyMapEncoder: Encoder[Map[Symbol.Topology, Topology]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
    }

  implicit val typeMapEncoder: Encoder[Map[AstNode.Id, Type]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  implicit val valueMapEncoder: Encoder[Map[AstNode.Id, Value]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }


  implicit val nameGroupSymbolMapEncoder
      : Encoder[Map[NameGroup, NameSymbolMap]] = Encoder.instance { symbols =>
    symbols.toList
      .map { case (key, value) =>
        getUnqualifiedClassName(key) -> value.asJson
      }
      .toMap
      .asJson
  }

  
  implicit val nameSymbolMapEncoder: Encoder[Map[Name.Unqualified, Symbol]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.getNodeId
        }
        .toMap
        .asJson
    }


  implicit val directImportMapEncoder: Encoder[Map[Symbol.Topology, Location]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
    }


  implicit val useDefMapEncoder: Encoder[Map[AstNode.Id, Symbol]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key -> value.getUnqualifiedName
        }
        .toMap
        .asJson
    }

  
  implicit val ConnectionMapEncoder
      : Encoder[Map[PortInstanceIdentifier, Set[Connection]]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.asJson -> value.asJson
        }
        .asJson
    }


  implicit val PortNumberMapEncoder: Encoder[Map[Connection, Int]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.asJson -> value.asJson
        }
        .asJson
    }


  implicit val patternMapEncoder
      : Encoder[Map[Ast.SpecConnectionGraph.Pattern.Kind, ConnectionPattern]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          getUnqualifiedClassName(key) -> value.asJson
        }
        .toMap
        .asJson
    }

  implicit val locationSpecifierMapEncoder
      : Encoder[Map[(Ast.SpecLoc.Kind, Name.Qualified), Ast.SpecLoc]] =
    Encoder.instance { map => map.toList.asJson }
 
  implicit val componentInstanceLocationMapEncoder: Encoder[Map[ComponentInstance, (Ast.Visibility, Location)]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.asJson -> value.asJson
        }
        .asJson
    }

  implicit val limitsEncoder: Encoder[TlmChannel.Limits] = Encoder.instance {
    limits =>
      limits.map { case (kind, (id, value)) =>
        kind.toString -> Json.obj(
          "id" -> id.asJson,
          "value" -> value.toString.asJson
        )
      }.asJson
  }

  implicit val componentInstanceEncoder: Encoder[ComponentInstance] =
    Encoder.instance {
      compInstance => io.circe.generic.semiauto.deriveEncoder[ComponentInstance].
        apply(compInstance).asObject.get.
        add("component", compInstance.component.aNode.asJson).asJson
    }

  /** Converts the Analysis data structure to JSON */
  def analysisToJson(a: Analysis): Json = Json.obj(
    "inputFileSet" -> a.inputFileSet.asJson, 
    "includedFileSet" -> a.includedFileSet.asJson, 
    "locationSpecifierMap" -> a.locationSpecifierMap.asJson, 
    "parentSymbolMap" -> a.parentSymbolMap.asJson, 
    "symbolScopeMap" -> a.symbolScopeMap.asJson, 
    "useDefMap" -> a.useDefMap.asJson,
    "typeMap" -> a.typeMap.asJson, 
    "valueMap" -> a.valueMap.asJson, 
    "componentMap" -> a.componentMap.asJson, 
    "componentInstanceMap" -> a.componentInstanceMap.asJson, 
    "topologyMap" -> a.topologyMap.asJson 
  )
    
}
