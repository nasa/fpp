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

  // ----------------------------------------------------------------------
  // Special cases
  // ----------------------------------------------------------------------

  // JSON encoder for AST nodes
  // Summarize the entire node with the ID, to avoid repeating info.
  // We can look up the node in the AST JSON.
  private implicit def astNodeEncoder[T: Encoder]: Encoder[AstNode[T]] =
    new Encoder[AstNode[T]] {
      override def apply(astNode: AstNode[T]): Json =
        Json.obj("astNodeId" -> astNode.id.asJson)
    }

  // JSON encoder for annotated AST nodes
  // Omit the annotations when translating analysis.
  // We can look them up in the AST JSON.
  private implicit def astNodeAnnotatedEncoder[T: Encoder]:
    Encoder[Ast.Annotated[AstNode[T]]] =
      new Encoder[Ast.Annotated[AstNode[T]]] {
        override def apply(aNode: Ast.Annotated[AstNode[T]]): Json = aNode._2.asJson
      }

  // JSON encoder for symbols
  // Report the symbol kind and the info in the SymbolInterface trait
  implicit val symbolInterfaceEncoder: Encoder[SymbolInterface] =
    Encoder.instance { symbol =>
      addTypeNameKey(
        symbol,
        Json.obj(
          "nodeId" -> symbol.getNodeId.asJson,
          "unqualifiedName" -> symbol.getUnqualifiedName.asJson
        )
      )
    }

  // JSON encoder for interface instances
  // Replace component instance/topology with its AST node
  private implicit val interfaceInstanceEncoder: Encoder[InterfaceInstance] =
    Encoder.instance { instance =>
      val nodeJson = instance match {
        case InterfaceInstance.InterfaceComponentInstance(ci) => ci.aNode.asJson
        case InterfaceInstance.InterfaceTopology(t) => t.aNode.asJson
      }
      addTypeNameKey(instance, nodeJson)
  }

  // JSON encoder for component instances
  // Use the default Circe encoding, but replace the component instance
  // with its AST node. We can use the ID to look up the component
  // in the component map.
  private implicit val componentInstanceEncoder: Encoder[ComponentInstance] =
    Encoder.instance {
      compInstance => io.circe.generic.semiauto.deriveEncoder[ComponentInstance].
        apply(compInstance).asObject.get.
        add("component", compInstance.component.aNode.asJson).asJson
    }

  // Dictionary encoder that omits the reverseTlmChannelEntryMap
  implicit val dictionaryEncoder: Encoder[Dictionary] = 
      Encoder.instance { d =>
        io.circe.Json.obj(
          "usedSymbolSet" -> d.usedSymbolSet.asJson,
          "commandEntryMap" -> d.commandEntryMap.asJson,
          "tlmChannelEntryMap" -> d.tlmChannelEntryMap.asJson,
          "eventEntryMap" -> d.eventEntryMap.asJson,
          "paramEntryMap" -> d.paramEntryMap.asJson,
          "recordEntryMap" -> d.recordEntryMap.asJson,
          "containerEntryMap" -> d.containerEntryMap.asJson,
          "tlmPacketSetMap" -> d.tlmPacketSetMap.asJson
        )
      }

  // Encoder for converting node ID keys to strings
  implicit val astNodeIdKeyEncoder: KeyEncoder[AstNode.Id] =
    KeyEncoder.instance(_.toString)

  // Encoder for converting BigInt keys to strings
  implicit val bigIntKeyEncoder: KeyEncoder[BigInt] = 
    KeyEncoder.instance(_.toString)

  // Encoder for converting node ID keys to strings
  implicit def stateMachineSymbolKeyEncoder[A <: StateMachineSymbol]: KeyEncoder[A]=
    KeyEncoder.instance(_.getNodeId.toString)

  // Encoder for converting symbol keys to node ID strings
  implicit def symbolKeyEncoder[A <: Symbol]: KeyEncoder[A] =
    KeyEncoder.instance(_.getNodeId.toString)

  // ----------------------------------------------------------------------
  // Encoders for helping Circe with recursive types
  // ----------------------------------------------------------------------

  private implicit val enumConstantEncoder: Encoder[Value.EnumConstant] =
    io.circe.generic.semiauto.deriveEncoder[Value.EnumConstant]

  private implicit val generalPortInstanceKindEncoder: Encoder[PortInstance.General.Kind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))

  private implicit val endpointEncoder: Encoder[Connection.Endpoint] =
    io.circe.generic.semiauto.deriveEncoder[Connection.Endpoint]

  private implicit val portInstanceEncoder: Encoder[PortInstance] =
    io.circe.generic.semiauto.deriveEncoder[PortInstance]

  private implicit val portInstanceIdentifierEncoder: Encoder[PortInstanceIdentifier] =
    io.circe.generic.semiauto.deriveEncoder[PortInstanceIdentifier]

  private implicit val portSymbolEncoder: Encoder[Symbol.Port] =
    Encoder.instance(symbolInterfaceEncoder.apply)

  private implicit val stateMachineSymbolEncoder: Encoder[StateMachineSymbol] =
    Encoder.instance(symbolInterfaceEncoder.apply)

  private implicit val symbolEncoder: Encoder[Symbol] =
    Encoder.instance(symbolInterfaceEncoder.apply)

  private implicit val typeEncoder: Encoder[Type] =
    io.circe.generic.semiauto.deriveEncoder[Type]

  private implicit val valueAnonArrayEncoder: Encoder[Value.AnonArray] =
    io.circe.generic.semiauto.deriveEncoder[Value.AnonArray]

  private implicit val valueAnonStructEncoder: Encoder[Value.AnonStruct] =
    io.circe.generic.semiauto.deriveEncoder[Value.AnonStruct]

  private implicit val valueArrayEncoder: Encoder[Value.Array] =
    io.circe.generic.semiauto.deriveEncoder[Value.Array]

  private implicit val valueStructEncoder: Encoder[Value.Struct] =
    io.circe.generic.semiauto.deriveEncoder[Value.Struct]

  // ----------------------------------------------------------------------
  // Methods for converting Scala maps to JSON maps
  // We use this conversion when the keys can be converted to strings
  // ----------------------------------------------------------------------

  private def mapAsJsonMap[A,B] (f1: A => String) (f2: B => Json) (map: Map[A,B]): Json =
    (map.map { case (key, value) => (f1(key), f2(value)) }).asJson

  private implicit val limitsEncoder: Encoder[TlmChannel.Limits] = {
    def f1(kind: Ast.SpecTlmChannel.LimitKind) = kind.toString
    def f2(tlmPoint: (AstNode.Id, Value)) = tlmPoint.asJson
    Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val nameGroupSymbolMapEncoder:
    Encoder[Map[NameGroup, NameSymbolMap]] =
  {
    def f1(nameGroup: NameGroup) = getUnqualifiedClassName(nameGroup)
    def f2(map: NameSymbolMap) = map.asJson
    Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val patternMapEncoder:
    Encoder[Map[Ast.SpecConnectionGraph.Pattern.Kind, ConnectionPattern]] =
  {
    def f1(kind: Ast.SpecConnectionGraph.Pattern.Kind) =
      getUnqualifiedClassName(kind)
    def f2(pattern: ConnectionPattern) = pattern.asJson
    Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val specialKindMapEncoder:
    Encoder[Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special]] =
  {
    def f1(kind: Ast.SpecPortInstance.SpecialKind) = kind.toString
    def f2(pi: PortInstance.Special) = pi.asJson
    Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val stateMachineNameGroupMapEncoder:
    Encoder[Map[StateMachineNameGroup, SmNameSymbolMap]] =
  {
    def f1(nameGroup: StateMachineNameGroup) = getUnqualifiedClassName(nameGroup)
    def f2(map: SmNameSymbolMap) = map.asJson
    Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val transitionExprMapEncoder: Encoder[StateMachineAnalysis.TransitionExprMap] = {
      def f1(n: AstNode[Ast.TransitionExpr]) = n.id.toString
      def f2(t: Transition) = t.asJson
      Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val transitionGraphArcMapEncoder: Encoder[TransitionGraph.ArcMap] = {
    def f1(n: TransitionGraph.Node) = n.soc.getSymbol.getNodeId.toString
    def f2(as: Set[TransitionGraph.Arc]) = (as.map(elem => elem.asJson)).toList.asJson
    Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val typeOptionMap: Encoder[Map[StateMachineTypedElement, Option[Type]]] = {
      def f1(e: StateMachineTypedElement) = e.getNodeId.toString
      def f2(t: Option[Type]) = t.asJson
      Encoder.instance (mapAsJsonMap(f1)(f2))
  }

  private implicit val portInterfaceEncoder: Encoder[PortInterface] =
    io.circe.generic.semiauto.deriveEncoder[PortInterface]

  // ----------------------------------------------------------------------
  // Methods for converting Scala maps to JSON lists
  // We use this conversion when the keys cannot be converted to strings
  // ----------------------------------------------------------------------

  implicit val locationComparator: java.util.Comparator[Location] =
    new java.util.Comparator[Location] {
      override def compare(o1: Location, o2: Location): Int =
        o1.compare(o2)
    }

  private implicit val interfaceInstanceLocationMapEncoder:
    Encoder[Map[InterfaceInstance, Location]] =
    Encoder.instance(_.toList.sortBy(_._2).asJson)

  private implicit val connectionMapEncoder:
    Encoder[Map[PortInstanceIdentifier, Set[Connection]]] =
    Encoder.instance(_.toList.asJson)

  private implicit val locationSpecifierMapEncoder:
    Encoder[Map[(Ast.SpecLoc.Kind, Name.Qualified), AstNode[Ast.SpecLoc]]] =
    Encoder.instance(_.toList.asJson)

  private implicit val portNumberMapEncoder: Encoder[Map[Connection, Int]] =
    Encoder.instance(_.toList.asJson)

  private implicit val stateMachineAnalysisEncoder: Encoder[StateMachineAnalysis] = {
    def stateMachineAnalysisToJson(sma: StateMachineAnalysis): Json =
      Json.obj(
        "symbol" -> sma.symbol.asJson,
        "symbolScopeMap" -> sma.symbolScopeMap.asJson,
        "useDefMap" -> sma.useDefMap.asJson,
        "transitionGraph" -> sma.transitionGraph.asJson,
        "reverseTransitionGraph" -> sma.reverseTransitionGraph.asJson,
        "typeOptionMap" -> sma.typeOptionMap.asJson,
        "flattenedStateTransitionMap" -> sma.flattenedStateTransitionMap.asJson,
        "flattenedChoiceTransitionMap" -> sma.flattenedChoiceTransitionMap.asJson
      )
    Encoder.instance(stateMachineAnalysisToJson)
  }

  // ----------------------------------------------------------------------
  // The public encoder interface
  // ----------------------------------------------------------------------

  /** Converts the Analysis data structure to JSON */
  def analysisToJson(a: Analysis): Json = {
    Json.obj(
      "fppVersion" -> Version.v.asJson,
      "analysis" -> Json.obj(
        "componentInstanceMap" -> a.componentInstanceMap.asJson,
        "componentMap" -> a.componentMap.asJson,
        "frameworkDefinitions" -> a.frameworkDefinitions.asJson,
        "includedFileSet" -> a.includedFileSet.asJson,
        "inputFileSet" -> a.inputFileSet.asJson,
        "locationSpecifierMap" -> a.locationSpecifierMap.asJson,
        "parentSymbolMap" -> a.parentSymbolMap.asJson,
        "symbolScopeMap" -> a.symbolScopeMap.asJson,
        "topologyMap" -> a.topologyMap.asJson,
        "typeMap" -> a.typeMap.asJson,
        "useDefMap" -> a.useDefMap.asJson,
        "valueMap" -> a.valueMap.asJson,
        "stateMachineMap" -> a.stateMachineMap.asJson,
        "dictionarySymbolSet" -> a.dictionarySymbolSet.asJson,
        "interfaceMap" -> a.interfaceMap.asJson,
        "dictionaryMap" -> a.dictionaryMap.asJson
      )
    )
  }

}
