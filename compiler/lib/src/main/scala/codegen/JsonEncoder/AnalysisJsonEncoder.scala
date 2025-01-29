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
  private def symbolAsJson(symbol: SymbolInterface) = addTypeNameKey(
    symbol,
    Json.obj(
      "nodeId" -> symbol.getNodeId.asJson,
      "unqualifiedName" -> symbol.getUnqualifiedName.asJson
    )
  )

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

  // ----------------------------------------------------------------------
  // Encoders for helping Circe with recursive types
  // ----------------------------------------------------------------------

  private implicit val enumConstantEncoder: Encoder[Value.EnumConstant] =
    io.circe.generic.semiauto.deriveEncoder[Value.EnumConstant]

  private implicit val generalPortInstanceKindEncoder: Encoder[PortInstance.General.Kind] =
    Encoder.encodeString.contramap(getUnqualifiedClassName(_))

  private implicit val portInstanceIdentifierEncoder: Encoder[PortInstanceIdentifier] =
    io.circe.generic.semiauto.deriveEncoder[PortInstanceIdentifier]

  private implicit val portSymbolEncoder: Encoder[Symbol.Port] =
    Encoder.instance(symbolAsJson(_))

  private implicit val stateMachineSymbolEncoder: Encoder[StateMachineSymbol] =
    Encoder.instance(symbolAsJson(_))

  private implicit val symbolEncoder: Encoder[Symbol] =
    Encoder.instance(symbolAsJson(_))

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

  private def astNodeIdToString(id: AstNode.Id) = id.toString

  private def mapAsJsonMap[A,B] (f1: A => String) (f2: B => Json) (map: Map[A,B]): Json =
    (map.map { case (key, value) => (f1(key), f2(value)) }).asJson

  private def stateMachineSymbolToIdString(s: StateMachineSymbol) = s.getNodeId.toString

  private def symbolToIdString(s: Symbol) = s.getNodeId.toString

  private implicit val commandMapEncoder: Encoder[Map[Command.Opcode, Command]] = {
    def f1(opcode: Command.Opcode) = opcode.toString
    def f2(command: Command) = command.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val componentInstanceMapEncoder:
    Encoder[Map[Symbol.ComponentInstance, ComponentInstance]] =
  {
    def f2(ci: ComponentInstance) = ci.asJson
    Encoder.instance (mapAsJsonMap (symbolToIdString) (f2) _)
  }

  private implicit val componentMapEncoder:
    Encoder[Map[Symbol.Component, Component]] =
  {
    def f2(c: Component) = c.asJson
    Encoder.instance (mapAsJsonMap (symbolToIdString) (f2) _)
  }

  private implicit val containerMapEncoder:
    Encoder[Map[Container.Id, Container]] =
  {
    def f1(id: Container.Id) = id.toString
    def f2(c: Container) = c.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val directImportMapEncoder:
    Encoder[Map[Symbol.Topology, Location]] =
  {
    def f2(loc: Location) = loc.asJson
    Encoder.instance (mapAsJsonMap (symbolToIdString) (f2) _)
  }

  private implicit val eventMapEncoder: Encoder[Map[Event.Id, Event]] = {
    def f1(id: Event.Id) = id.toString
    def f2(event: Event) = event.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val limitsEncoder: Encoder[TlmChannel.Limits] = {
    def f1(kind: Ast.SpecTlmChannel.LimitKind) = kind.toString
    def f2(tlmPoint: (AstNode.Id, Value)) = tlmPoint.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val nameGroupSymbolMapEncoder:
    Encoder[Map[NameGroup, NameSymbolMap]] =
  {
    def f1(nameGroup: NameGroup) = getUnqualifiedClassName(nameGroup)
    def f2(map: NameSymbolMap) = map.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val nameSymbolMapEncoder:
    Encoder[Map[Name.Unqualified, Symbol]] =
  {
    def f1(name: Name.Unqualified) = name.toString
    def f2(symbol: Symbol) = symbol.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val paramMapEncoder: Encoder[Map[Param.Id, Param]] = {
    def f1(id: Param.Id) = id.toString
    def f2(param: Param) = param.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val patternMapEncoder:
    Encoder[Map[Ast.SpecConnectionGraph.Pattern.Kind, ConnectionPattern]] =
  {
    def f1(kind: Ast.SpecConnectionGraph.Pattern.Kind) =
      getUnqualifiedClassName(kind)
    def f2(pattern: ConnectionPattern) = pattern.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val recordMapEncoder:
    Encoder[Map[Record.Id, Record]] =
  {
    def f1(id: Record.Id) = id.toString
    def f2(c: Record) = c.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val signalStateTransitionMapEncoder: Encoder[StateMachineAnalysis.SignalStateTransitionMap] = {
    def f1(s: StateMachineSymbol.Signal) = s.getNodeId.toString
    def f2(s: StateMachineSymbol.State) = s.getUnqualifiedName
    def f3(t: Transition.Guarded) = t.asJson
    Encoder.instance (mapAsJsonMap (f1) (mapAsJsonMap (f2) (f3) _) _)
  }

  private implicit val specialKindMapEncoder:
    Encoder[Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special]] =
  {
    def f1(kind: Ast.SpecPortInstance.SpecialKind) = kind.toString
    def f2(pi: PortInstance.Special) = pi.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val stateMachineMapEncoder:
    Encoder[Map[Symbol.StateMachine, StateMachine]] =
  {
    def f2(sm: StateMachine): Json = sm.asJson
    Encoder.instance (mapAsJsonMap (symbolToIdString) (f2) _)
  }

  private implicit val stateMachineNameGroupMapEncoder:
    Encoder[Map[StateMachineNameGroup, SmNameSymbolMap]] =
  {
    def f1(nameGroup: StateMachineNameGroup) = getUnqualifiedClassName(nameGroup)
    def f2(map: SmNameSymbolMap) = map.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val stateMachineNameSymbolMapEncoder:
    Encoder[Map[Name.Unqualified, StateMachineSymbol]] =
  {
    def f1(name: Name.Unqualified) = name.toString
    def f2(symbol: StateMachineSymbol) = symbol.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val stateMachineSymbolScopeMapEncoder: Encoder[Map[StateMachineSymbol, StateMachineScope]] = {
    def f2(s: StateMachineScope) = s.asJson
    Encoder.instance (mapAsJsonMap (stateMachineSymbolToIdString) (f2) _)
  }

  private implicit val stateMachineUseDefMapEncoder: Encoder[Map[AstNode.Id, StateMachineSymbol]] = {
      def f2(s: StateMachineSymbol) = s.asJson
      Encoder.instance (mapAsJsonMap (astNodeIdToString) (f2) _)
  }

  private implicit val symbolMapEncoder: Encoder[Map[Symbol, Symbol]] = {
    def f2(s: Symbol) = symbolAsJson(s)
    Encoder.instance (mapAsJsonMap (symbolToIdString) (f2) _)
  }

  private implicit val symbolScopeMapEncoder: Encoder[Map[Symbol, Scope]] = {
    def f2(s: Scope) = s.asJson
    Encoder.instance (mapAsJsonMap (symbolToIdString) (f2) _)
  }

  private implicit val tlmChannelMapEncoder:
    Encoder[Map[TlmChannel.Id, TlmChannel]] =
  {
    def f1(id: TlmChannel.Id) = id.toString
    def f2(channel: TlmChannel) = channel.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val topologyMapEncoder:
    Encoder[Map[Symbol.Topology, Topology]] =
  {
    def f2(t: Topology) = t.asJson
    Encoder.instance (mapAsJsonMap (symbolToIdString) (f2) _)
  }

  private implicit val transitionExprMapEncoder: Encoder[StateMachineAnalysis.TransitionExprMap] = {
      def f1(n: AstNode[Ast.TransitionExpr]) = n.id.toString
      def f2(t: Transition) = t.asJson
      Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val transitionGraphArcMapEncoder: Encoder[TransitionGraph.ArcMap] = {
    def f1(n: TransitionGraph.Node) = n.soc.getName
    def f2(as: Set[TransitionGraph.Arc]) = (as.map(elem => elem.asJson)).toList.asJson
    Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val typeMapEncoder: Encoder[Map[AstNode.Id, Type]] = {
    def f2(t: Type) = t.asJson
    Encoder.instance (mapAsJsonMap (astNodeIdToString) (f2) _)
  }

  private implicit val typeOptionMap: Encoder[Map[StateMachineTypedElement, Option[Type]]] = {
      def f1(e: StateMachineTypedElement) = e.getNodeId.toString
      def f2(t: Option[Type]) = t.asJson
      Encoder.instance (mapAsJsonMap (f1) (f2) _)
  }

  private implicit val useDefMapEncoder: Encoder[Map[AstNode.Id, Symbol]] = {
    def f2(s: Symbol) = s.asJson
    Encoder.instance (mapAsJsonMap (astNodeIdToString) (f2) _)
  }

  private implicit val valueMapEncoder: Encoder[Map[AstNode.Id, Value]] = {
    def f2(value: Value) = value.asJson
    Encoder.instance (mapAsJsonMap (astNodeIdToString) (f2) _)
  }

  // ----------------------------------------------------------------------
  // Methods for converting Scala maps to JSON lists
  // We use this conversion when the keys cannot be converted to strings
  // ----------------------------------------------------------------------

  private implicit val componentInstanceLocationMapEncoder:
    Encoder[Map[ComponentInstance, (Ast.Visibility, Location)]] =
    Encoder.instance(_.toList.asJson)

  private implicit val connectionMapEncoder:
    Encoder[Map[PortInstanceIdentifier, Set[Connection]]] =
    Encoder.instance(_.toList.asJson)

  private implicit val locationSpecifierMapEncoder:
    Encoder[Map[(Ast.SpecLoc.Kind, Name.Qualified), Ast.SpecLoc]] =
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
    Encoder.instance(stateMachineAnalysisToJson(_))
  }

  // ----------------------------------------------------------------------
  // The public encoder interface
  // ----------------------------------------------------------------------

  /** Converts the Analysis data structure to JSON */
  def analysisToJson(a: Analysis): Json = {
    Json.obj(
      "componentInstanceMap" -> a.componentInstanceMap.asJson,
      "componentMap" -> a.componentMap.asJson,
      "includedFileSet" -> a.includedFileSet.asJson,
      "inputFileSet" -> a.inputFileSet.asJson,
      "locationSpecifierMap" -> a.locationSpecifierMap.asJson,
      "parentSymbolMap" -> a.parentSymbolMap.asJson,
      "symbolScopeMap" -> a.symbolScopeMap.asJson,
      "topologyMap" -> a.topologyMap.asJson,
      "typeMap" -> a.typeMap.asJson,
      "useDefMap" -> a.useDefMap.asJson,
      "valueMap" -> a.valueMap.asJson,
      "stateMachineMap" -> a.stateMachineMap.asJson
    )
  }

}
