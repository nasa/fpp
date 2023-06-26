package fpp.compiler

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.syntax._
import fpp.compiler.transform._
import fpp.compiler.util._
import io.circe.syntax._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import scala.util.parsing.input.Position

case class JsonEncoder(
    tul: List[Ast.TransUnit] = List.empty[Ast.TransUnit],
    analysis: Analysis = Analysis()
){

  implicit val binopEncoder: Encoder[Ast.Binop] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val unopEncoder: Encoder[Ast.Unop] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val componentKindEncoder: Encoder[Ast.ComponentKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val typeIntEncoder: Encoder[Ast.TypeInt] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val typeFloatEncoder: Encoder[Ast.TypeFloat] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val queueFullEncoder: Encoder[Ast.QueueFull] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val updateEncoder: Encoder[Ast.SpecTlmChannel.Update] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val limitKindEncoder: Encoder[Ast.SpecTlmChannel.LimitKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val generalKindEncoder: Encoder[Ast.SpecPortInstance.GeneralKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val specialInputKindEncoder: Encoder[Ast.SpecPortInstance.SpecialInputKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val specialKindEncoder: Encoder[Ast.SpecPortInstance.SpecialKind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))
  implicit val visibilityEncoder: Encoder[Ast.Visibility] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))

  implicit def optionEncoder[A](implicit
      encoder: Encoder[A]
  ): Encoder[Option[A]] = {
    case Some(value) => Json.obj("Option" -> Json.obj("Some" -> encoder(value)))
    case None        => Json.obj("Option" -> Json.fromString("None"))
  }

  implicit val structTypeMemberEncoder: Encoder[Ast.Annotated[AstNode[Ast.StructTypeMember]]] =
    Encoder.instance { aNode =>
      (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
    }

  implicit val defEnumConstantEncoder: Encoder[Ast.Annotated[AstNode[Ast.DefEnumConstant]]] =
    Encoder.instance { aNode =>
      (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
    }

  implicit val specInitEncoder: Encoder[Ast.Annotated[AstNode[Ast.SpecInit]]] =
    Encoder.instance { aNode =>
      (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
    }

  implicit val formalParamEncoder: Encoder[Ast.Annotated[AstNode[Ast.FormalParam]]] = Encoder.instance {
    aNode => (aNode._1, addTypeName(aNode._2, aNode._2.asJson), aNode._3).asJson
  }

  implicit val qualIdentEncoder: Encoder[Ast.QualIdent] =
    Encoder.instance((q: Ast.QualIdent) =>
      q match {
        case ident: Ast.QualIdent.Unqualified =>
          addTypeName(ident, ident.asJson)
        case ident: Ast.QualIdent.Qualified => addTypeName(ident, ident.asJson)
      }
    )

  implicit val moduleMemberEncoder: Encoder[Ast.ModuleMember] =
    Encoder.instance((m: Ast.ModuleMember) =>
      m.node._2 match {
        case aNode: Ast.ModuleMember.DefAbsType =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefArray =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefComponent =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefComponentInstance =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefConstant =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefEnum =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefModule =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefPort =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefStruct =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.DefTopology =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.SpecInclude =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
        case aNode: Ast.ModuleMember.SpecLoc =>
          (m.node._1, addTypeName(aNode, aNode.asJson), m.node._3).asJson
      }
    )

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

  implicit val typeNameEncoder: Encoder[Ast.TypeName] =
    Encoder.instance((t: Ast.TypeName) =>
      t match {
        case t: Ast.TypeNameFloat     => addTypeName(t, t.asJson)
        case t: Ast.TypeNameInt       => addTypeName(t, t.asJson)
        case t: Ast.TypeNameQualIdent => addTypeName(t, t.asJson)
        case t: Ast.TypeNameString    => addTypeName(t, t.asJson)
        case Ast.TypeNameBool => addTypeName(t, Json.fromString("TypeNameBool"))
      }
    )

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

  def printAstJson(): Json = tul.asJson

  def addTypeName[T](x: T, json: Json): Json =
    Json.obj(getUnqualifiedClassName(x) -> json)

  def getUnqualifiedClassName[T](x: T): String =
    x.getClass.getName
      .replaceAll("\\A.*\\.", "")
      .replaceAll("\\$$", "")
      .replaceAll("\\A.*\\$", "")

  def addAnnotationJson(
      pre: List[String],
      data: Json,
      post: List[String]
    ): Json =
    Json.obj(
      "preAnnotation" -> pre.asJson,
      "data" -> data,
      "postAnnotation" -> post.asJson
    )


  implicit val fileEncoder: Encoder[File] = new Encoder[File] {
    override def apply(file: File): Json = Json.fromString(file.toString)
  }

  implicit val positionEncoder: Encoder[Position] = new Encoder[Position] {
    override def apply(position: Position): Json =
      Json.fromString(position.toString)
  }

  implicit val locationEncoder: Encoder[Location] = new Encoder[Location] {
    override def apply(location: Location): Json = Json.obj(
      "file" -> location.file.asJson,
      "pos" -> location.pos.asJson,
      "includingLoc" -> location.includingLoc.asJson
    )
  }

  def printLocationsMapJson(): Json = {
    val locationsList =
      Locations.hashMapToListOfPairs().map { case (id, location) =>
        id.toString -> location.asJson
      }
    Json.obj(locationsList: _*)
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
  // approved
  implicit val scopeMapEncoder: Encoder[Map[Symbol, Scope]] = Encoder.instance {
    symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
  }
  // approved but maybe consider creating a specifier encoder that is more concise
  implicit val componentMapEncoder: Encoder[Map[Symbol.Component, Component]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
    }

  // Approved
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

  // approved but might consider refactoring the way "kind" is encoded
  implicit val commandMapEncoder: Encoder[Map[Command.Opcode, Command]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  // No way to test but approved for now
  implicit val tlmChannelMapEncoder: Encoder[Map[TlmChannel.Id, TlmChannel]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  // No way to test but approved for now
  implicit val eventMapEncoder: Encoder[Map[Event.Id, Event]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  // No way to test but approved for now
  implicit val paramMapEncoder: Encoder[Map[Param.Id, Param]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }

  // Approved
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
          getUnqualifiedClassName(key1) -> Json.obj(
            "qualifiedName" -> key2.asJson,
            "Location" -> value.asJson
          )
        }
        .toMap
        .asJson
    }

 
  implicit val componentInstanceLocationMapEncoder
      : Encoder[Map[ComponentInstance, (Ast.Visibility, Location)]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.aNode._2.id -> value.asJson
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

  // approved
  implicit val directImportMapEncoder: Encoder[Map[Symbol.Topology, Location]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.getNodeId -> value.asJson
        }
        .toMap
        .asJson
    }

  // approved
  implicit val useDefMapEncoder: Encoder[Map[AstNode.Id, Symbol]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key -> value.getUnqualifiedName
        }
        .toMap
        .asJson
    }

  // key.toString must be changed
  implicit val ConnectionMapEncoder
      : Encoder[Map[PortInstanceIdentifier, Set[Connection]]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
        .asJson
    }


  implicit val PortNumberMapEncoder: Encoder[Map[Connection, Int]] =
    Encoder.instance { symbols =>
      symbols.toList
        .map { case (key, value) =>
          key.toString -> value.asJson
        }
        .toMap
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

  implicit val typeEncoder: Encoder[Type] =
    Encoder.encodeString.contramap(_.toString())
  implicit val valueEncoder: Encoder[Value] =
    Encoder.encodeString.contramap(_.toString)

  implicit val limitsEncoder: Encoder[TlmChannel.Limits] = Encoder.instance {
    limits =>
      limits.map { case (kind, (id, value)) =>
        kind.toString -> Json.obj(
          "id" -> id.asJson,
          "value" -> value.toString.asJson
        )
      }.asJson
  }

  implicit val paramEncoder: Encoder[Param] = Encoder.instance { param =>
    Json.obj(
      "id" -> param.aNode._2.id.asJson,
      "paramType" -> param.paramType.asJson,
      "default" -> param.default.asJson,
      "setOpcode" -> param.setOpcode.asJson,
      "saveOpcode" -> param.saveOpcode.asJson
    )
  }

  implicit val portInstanceIdentifierEncoder: Encoder[PortInstanceIdentifier] =
    Encoder.encodeString.contramap(_.toString())

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
  implicit val generalPortInstanceKindEncoder
      : Encoder[PortInstance.General.Kind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))

  implicit val generalPortInstancetEncoder: Encoder[PortInstance.General] =
    Encoder.instance { genPort =>
      Json.obj(
        "id" -> genPort.aNode._2.id.asJson,
        "specifier" -> genPort.specifier.name.asJson, // may need to expand this since name is one of many feilds in here
        "kind" -> genPort.kind.asJson,
        "size" -> genPort.size.asJson,
        "ty" -> genPort.size.asJson
      )
    }

  implicit val specialPortInstanceEncoder: Encoder[PortInstance.Special] =
    Encoder.instance { specPort =>
      Json.obj(
        "id" -> specPort.aNode._2.id.asJson,
        "specifier" -> Json.obj(
          "inputKind" -> specPort.specifier.inputKind.asJson,
          "kind" -> specPort.specifier.kind.asJson,
          "name" -> specPort.specifier.name.asJson,
          "priority" -> specPort.specifier.priority.asJson,
          "queueFull" -> specPort.specifier.queueFull.asJson
        ),
        "name" -> specPort.symbol.getUnqualifiedName.asJson,
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

  implicit val setCompInstanceLocationEncoder
      : Encoder[(ComponentInstance, Location)] = Encoder.instance { tuple =>
    (tuple._1.aNode._2.id.asJson, tuple._2.asJson).asJson
  }

  def printAnalysisJson(): Json = {
    Json.obj(
      "inputFileSet" -> analysis.inputFileSet.asJson, // good to go
      "includedFileSet" -> analysis.includedFileSet.asJson, // good to go
      "locationSpecifierMap" -> analysis.locationSpecifierMap.asJson, // should be good to go (needs more tests)
      "parentSymbolMap" -> analysis.parentSymbolMap.asJson, // good to go
      "symbolScopeMap" -> analysis.symbolScopeMap.asJson, // may need to revisit becuase there feels like too much nesting
      "useDefMap" -> analysis.useDefMap.asJson, // good to go
      "typeMap" -> analysis.typeMap.asJson, // good to go
      "valueMap" -> analysis.valueMap.asJson, // good to go
      "componentMap" -> analysis.componentMap.asJson, // mostly good to go, revisit the port matching maps
      "componentInstanceMap" -> analysis.componentInstanceMap.asJson, // good to go
      "topologyMap" -> analysis.topologyMap.asJson //
    )
  }


}
