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

  implicit val locationSpecifierMapEncoder
      : Encoder[Map[(Ast.SpecLoc.Kind, Name.Qualified), Ast.SpecLoc]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case ((key1, key2), value) =>
          (getUnqualifiedClassName(key1), key2.asJson).asJson -> value.asJson
        }
        .asJson
    }

 
  implicit val componentInstanceLocationMapEncoder: Encoder[Map[ComponentInstance, (Ast.Visibility, Location)]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.asJson -> value.asJson
        }
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

  implicit val tlmChannelEncoder: Encoder[TlmChannel] = Encoder.instance {
    tlmChannel =>
      Json.obj(
        "id" -> tlmChannel.aNode._2.id.asJson,
        "channelType" -> tlmChannel.channelType.asJson,
        "update" -> tlmChannel.update.asJson,
        "format" -> tlmChannel.format.asJson,
        "lowLimits" -> tlmChannel.lowLimits.asJson,
        "highLimits" -> tlmChannel.highLimits.asJson
      )
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

  /*
  implicit val paramEncoder: Encoder[Param] = Encoder.instance { param =>
    Json.obj(
      "id" -> param.aNode._2.id.asJson,
      "paramType" -> param.paramType.asJson,
      "default" -> param.default.asJson,
      "setOpcode" -> param.setOpcode.asJson,
      "saveOpcode" -> param.saveOpcode.asJson
    )
  }
  */

  implicit val componentEncoder: Encoder[Component] = Encoder.instance {
    component =>
      Json.obj(
        "id" -> component.aNode._2.id.asJson,
        "portMap" -> component.portMap.asJson,
        "specialPortMap" -> component.specialPortMap.asJson,
        "commandMap" -> component.commandMap.asJson,
        "defaultOpcode" -> component.defaultOpcode.asJson,
        "tlmChannelMap" -> component.tlmChannelMap.asJson,
        "defaultTlmChannelId" -> component.defaultTlmChannelId.asJson,
        "eventMap" -> component.eventMap.asJson,
        "defaultEventId" -> component.defaultEventId.asJson,
        "paramMap" -> component.paramMap.asJson,
        "specPortMatchingList" -> component.specPortMatchingList.asJson,
        "portMatchingList" -> component.portMatchingList.asJson,
        "defaultParamId" -> component.defaultParamId.asJson
      )
  }


  implicit val generalPortInstancetEncoder: Encoder[PortInstance.General] =
    Encoder.instance { genPort =>
      Json.obj(
        "id" -> genPort.aNode._2.id.asJson,
        "specifier" -> genPort.specifier.asJson,
        "kind" -> genPort.kind.asJson,
        "size" -> genPort.size.asJson,
        "ty" -> genPort.size.asJson
      )
    }

  implicit val specialPortInstanceEncoder: Encoder[PortInstance.Special] =
    Encoder.instance { specPort =>
      Json.obj(
        "id" -> specPort.aNode._2.id.asJson,
        "specifier" -> specPort.specifier.asJson,
        "name" -> specPort.symbol.getUnqualifiedName.asJson,
        "symbolId" -> specPort.symbol.getNodeId.asJson,
        "priority" -> specPort.priority.asJson,
        "queueFull" -> specPort.queueFull.asJson
      )
    }

  implicit val internallPortInstancetEncoder: Encoder[PortInstance.Internal] =
    Encoder.instance { intPort =>
      Json.obj(
        "id" -> intPort.aNode._2.id.asJson,
        "priority" -> intPort.priority.asJson,
        "queueFull" -> intPort.queueFull.asJson
      )
    }

  implicit val nonParamCommandEncoder: Encoder[Command.NonParam] =
    Encoder.instance { command =>
      Json.obj(
        "id" -> command.aNode._2.id.asJson,
        "kind" -> command.kind.asJson
      )
    }

  implicit val paramCommandEncoder: Encoder[Command.Param] = Encoder.instance {
    param =>
      Json.obj(
        "id" -> param.aNode._2.id.asJson,
        "kind" -> param.kind.asJson
      )
  }

  implicit val componentInstanceEncoder: Encoder[ComponentInstance] =
    Encoder.instance { compInstance =>
      Json.obj(
        "id" -> compInstance.aNode._2.id.asJson,
        "Qualified Name" -> compInstance.qualifiedName.asJson,
        "Component" -> compInstance.component.aNode._2.id.asJson,
        "baseId" -> compInstance.baseId.asJson,
        "maxId" -> compInstance.maxId.asJson,
        "file" -> compInstance.file.asJson,
        "queueSize" -> compInstance.queueSize.asJson,
        "stackSize" -> compInstance.stackSize.asJson,
        "priority" -> compInstance.priority.asJson,
        "cpu" -> compInstance.cpu.asJson,
        "initSpecifierMap" -> compInstance.initSpecifierMap.asJson
      )
    }

  implicit val InitSpecifierEncoder: Encoder[InitSpecifier] = Encoder.instance {
    initSpec =>
      Json.obj(
        "id" -> initSpec.aNode._2.id.asJson,
        "phase" -> initSpec.phase.asJson
      )
  }

  implicit val eventEncoder: Encoder[Event] = Encoder.instance { event =>
    Json.obj(
      "id" -> event.aNode._2.id.asJson,
      "format" -> event.format.asJson,
      "throttle" -> event.throttle.asJson
    )
  }

  implicit val topologyEncoder: Encoder[Topology] = Encoder.instance {
    topology =>
      Json.obj(
        "id" -> topology.aNode._2.id.asJson,
        "directImportMap" -> topology.directImportMap.asJson,
        "transitiveImportSet" -> Json.arr(
          topology.transitiveImportSet.map(_.getNodeId).map(_.asJson).toSeq: _*
        ),
        "instanceMap" -> topology.instanceMap.asJson,
        "patternMap" -> topology.patternMap.asJson,
        "connectionMap" -> topology.connectionMap.asJson,
        "localConnectionMap" -> topology.localConnectionMap.asJson,
        "outputConnectionMap" -> topology.outputConnectionMap.asJson,
        "inputConnectionMap" -> topology.inputConnectionMap.asJson,
        "fromPortNumberMap" -> topology.fromPortNumberMap.asJson,
        "toPortNumberMap" -> topology.toPortNumberMap.asJson,
        "unconnectedPortSet" -> topology.unconnectedPortSet.asJson
      )
  }

  implicit val connectionPatternEncoder: Encoder[ConnectionPattern] =
    Encoder.instance { connectionPattern =>
      Json.obj(
        "id" -> connectionPattern.aNode._2.id.asJson,
        "pattern" -> connectionPattern.ast.asJson,
        "source" -> connectionPattern.source.asJson,
        "targets" -> connectionPattern.targets.asJson
      )
    }

  implicit val setCompInstanceLocationEncoder: Encoder[(ComponentInstance, Location)] = Encoder.instance { tuple =>
    (tuple._1.aNode._2.id.asJson, tuple._2.asJson).asJson
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
