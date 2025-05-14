package fpp.compiler.analysis

import fpp.compiler.analysis.Name.Unqualified
import fpp.compiler.ast.*
import fpp.compiler.ast.Ast.SpecPortInstance
import fpp.compiler.util.*

/** An FPP component */
case class Component(
  /** The AST node defining the component */
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]],
  /** The map from port names to port instances */
  portMap: Map[Name.Unqualified, PortInstance] = Map(),
  /** The map from special port kinds to special port instances */
  specialPortMap: Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special] = Map(),
  /** The map from command opcodes to commands */
  commandMap: Map[Command.Opcode, Command] = Map(),
  /** The next default opcode */
  defaultOpcode: BigInt = 0,
  /** The map from telemetry channel IDs to channels */
  tlmChannelMap: Map[TlmChannel.Id, TlmChannel] = Map(),
  /** The map from telemetry channel names to channels */
  tlmChannelNameMap: Map[Name.Unqualified, TlmChannel] = Map(),
  /** The next default channel ID */
  defaultTlmChannelId: BigInt = 0,
  /** The map from event IDs to events */
  eventMap: Map[Event.Id, Event] = Map(),
  /** The next default event ID */
  defaultEventId: BigInt = 0,
  /** The map from parameter IDs to parameters */
  paramMap: Map[Param.Id, Param] = Map(),
  /** The list of port matching specifiers */
  specPortMatchingList: List[Ast.Annotated[AstNode[Ast.SpecPortMatching]]] = Nil,
  /** The map from state machine instance names to state machine instances */
  stateMachineInstanceMap: Map[Name.Unqualified, StateMachineInstance] = Map(),
  /** The list of port matching constraints */
  portMatchingList: List[Component.PortMatching] = Nil,
  /** The next default parameter ID */
  defaultParamId: BigInt = 0,
  /** The map from container ids to containers */
  containerMap: Map[Container.Id, Container] = Map(),
  /** The next default container ID */
  defaultContainerId: BigInt = 0,
  /** The map from record ids to records */
  recordMap: Map[Record.Id, Record] = Map(),
  /** The next default record ID */
  defaultRecordId: BigInt = 0
) extends GenericPortInterface[Component](aNode._2.data.name, portMap, specialPortMap) {
  def withPortMap(newPortMap: Map[Unqualified, PortInstance]): Component =
    this.copy(portMap = newPortMap)

  def withSpecialPortMap(
    newSpecialPortMap: Map[SpecPortInstance.SpecialKind, PortInstance.Special]
  ): Component = this.copy(specialPortMap = newSpecialPortMap)

  /** Query whether the component has parameters */
  def hasParameters = this.paramMap.nonEmpty

  /** Query whether the component has commands */
  def hasCommands = this.commandMap.nonEmpty

  /** Query whether the component has events */
  def hasEvents = this.eventMap.nonEmpty

  /** Query whether the component has telemetry */
  def hasTelemetry = this.tlmChannelMap.nonEmpty

  /** Query whether the component has data products */
  def hasDataProducts = (this.recordMap.size + this.containerMap.size) > 0

  /** Query whether the component has state machine instances */
  def hasStateMachineInstances = this.stateMachineInstanceMap.nonEmpty

  /** Query whether the component has state machine instances of
   *  the specified kind */
  def hasStateMachineInstancesOfKind(kind: StateMachine.Kind) =
    this.stateMachineInstanceMap.exists(_._2.getSmKind == kind)

  /** Gets the max identifier */
  def getMaxId: BigInt = {
    def maxInMap[T](map: Map[BigInt, T]): BigInt =
      if (map.isEmpty) -1 else map.keys.max
    val maxMap = Vector(
      commandMap,
      containerMap,
      eventMap,
      paramMap,
      tlmChannelMap
    ).maxBy(maxInMap)
    maxInMap(maxMap)
  }

  /** Gets a telemetry channel by name */
  def getTlmChannelByName(name: AstNode[Ast.Ident]): Result.Result[TlmChannel] =
    tlmChannelNameMap.get(name.data) match {
      case Some(tlmChannel) => Right(tlmChannel)
      case None => Left(
        SemanticError.InvalidTlmChannelName(
          Locations.get(name.id),
          name.data,
          aNode._2.data.name
        )
      )
    }

  /** Add a command */
  def addCommand(
    opcodeOpt: Option[Command.Opcode],
    command: Command
  ): Result.Result[Component] = {
    val opcode = opcodeOpt.getOrElse(defaultOpcode)
    commandMap.get(opcode) match {
      case Some(prevCommand) =>
        val value = Analysis.displayIdValue(opcode)
        val loc = command.getLoc
        val prevLoc = prevCommand.getLoc
        Left(SemanticError.DuplicateOpcodeValue(value, loc, prevLoc))
      case None =>
        val commandMap = this.commandMap + (opcode -> command)
        val component = this.copy(commandMap = commandMap, defaultOpcode = opcode + 1)
        Right(component)
    }
  }

  /** Add a state machine instance */
  def addStateMachineInstance(instance: StateMachineInstance):
  Result.Result[Component] = {
    val name = instance.getName
    stateMachineInstanceMap.get(name) match {
      case Some(prevInstance) =>
        val loc = instance.getLoc
        val prevLoc = prevInstance.getLoc
        Left(SemanticError.DuplicateStateMachineInstance(name, loc, prevLoc))
      case None => 
        val stateMachineInstanceMap = this.stateMachineInstanceMap + (name -> instance)
        val component = this.copy(stateMachineInstanceMap = stateMachineInstanceMap)
        Right(component)
    }
  }

  /** Add a data product container */
  def addContainer(
    idOpt: Option[TlmChannel.Id],
    container: Container
  ): Result.Result[Component] = {
    for {
      result <- Analysis.addElementToIdMap(
        containerMap,
        idOpt.getOrElse(defaultContainerId),
        container,
        _.getLoc
      )
    }
    yield this.copy(
      containerMap = result._1,
      defaultContainerId = result._2
    )
  }

  /** Add an event */
  def addEvent(
    idOpt: Option[Event.Id],
    event: Event
  ): Result.Result[Component] = {
    for {
      result <- Analysis.addElementToIdMap(
        eventMap,
        idOpt.getOrElse(defaultEventId),
        event,
        _.getLoc
      )
    }
    yield this.copy(
      eventMap = result._1,
      defaultEventId = result._2
    )
  }
  
  /** Add a parameter */
  def addParam(
    idOpt: Option[Param.Id],
    param: Param
  ): Result.Result[Component] = {
    for {
      // Update the parameter map and the default parameter ID
      result <- Analysis.addElementToIdMap(
        paramMap,
        idOpt.getOrElse(defaultParamId),
        param,
        _.getLoc
      )
      component <- Right(
        this.copy(
          paramMap = result._1,
          defaultParamId = result._2
        )
      )
      // Add the implicit set and save commands
      setCommand <- Right(Command.Param(param.aNode, Command.Param.Set))
      saveCommand <- Right(Command.Param(param.aNode, Command.Param.Save))
      component <- component.addCommand(Some(param.setOpcode), setCommand)
      component <- component.addCommand(Some(param.saveOpcode), saveCommand)
    }
    yield component
  }

  /** Add a data product record */
  def addRecord(
    idOpt: Option[Record.Id],
    record: Record
  ): Result.Result[Component] = {
    for {
      result <- Analysis.addElementToIdMap(
        recordMap,
        idOpt.getOrElse(defaultRecordId),
        record,
        _.getLoc
      )
    }
    yield this.copy(
      recordMap = result._1,
      defaultRecordId = result._2
    )
  }

  /** Add a telemetry channel */
  def addTlmChannel(
    idOpt: Option[TlmChannel.Id],
    tlmChannel: TlmChannel
  ): Result.Result[Component] = {
    for {
      result <- Analysis.addElementToIdMap(
        tlmChannelMap,
        idOpt.getOrElse(defaultTlmChannelId),
        tlmChannel,
        _.getLoc
      )
    }
    yield this.copy(
      tlmChannelMap = result._1,
      // Add the channel to the channel name map. If there is a duplicate name,
      // we will catch it later when we check all the dictionary elements.
      tlmChannelNameMap = tlmChannelNameMap + (tlmChannel.getName -> tlmChannel),
      defaultTlmChannelId = result._2
    )
  }

  /** Check that component provides ports required by dictionary
   *  and data product specifiers */
  private def checkRequiredPorts:
    Result.Result[Unit] = {
      import Ast.SpecPortInstance._
      def requirePorts(
        condition: Boolean,
        specMsg: String,
        portKinds: List[Ast.SpecPortInstance.SpecialKind]
      ) = if (condition) Result.map(
        portKinds,
        (portKind: Ast.SpecPortInstance.SpecialKind) => 
          this.specialPortMap.get(portKind) match {
            case Some(_) => Right(())
            case None =>
              val loc = Locations.get(this.aNode._2.id)
              val portMsg = s"${portKind.toString} port"
              Left(SemanticError.MissingPort(loc, specMsg, portMsg))
          }
      ) else Right(())
      def requireProductGetOrRequest =
        if (this.hasDataProducts &&
          !this.specialPortMap.contains(ProductGet) &&
          !this.specialPortMap.contains(ProductRequest)) {
            val loc = Locations.get(this.aNode._2.id)
            val specMsg = "data product specifiers"
            val portMsg = "product get port or product request port"
            Left(SemanticError.MissingPort(loc, specMsg, portMsg))
          }
         else Right(())
      for {
        _ <- requirePorts(
          this.hasParameters,
          "parameter specifiers",
          List(ParamGet, ParamSet, CommandRecv, CommandReg, CommandResp)
        )
        _ <- requirePorts(
          this.hasCommands,
          "command specifiers",
          List(CommandRecv, CommandReg, CommandResp)
        )
        _ <- requirePorts(
          this.hasEvents,
          "event specifiers",
          List(Event, TextEvent, TimeGet)
        )
        _ <- requirePorts(
          this.hasTelemetry,
          "telemetry specifiers",
          List(Telemetry, TimeGet)
        )
        _ <- if (this.hasDataProducts) requireProductGetOrRequest else Right(())
        _ <- requirePorts(
          this.hasDataProducts,
          "data product specifiers",
          List(ProductSend, TimeGet)
        )
        _ <- requirePorts (
          this.specialPortMap.contains(ProductRequest),
          "product request specifier",
          List(ProductRecv)
        )
      }
      yield ()
    }

  /** Check that if there are any data products, then there are both containers
   *  and records */
  private def checkDataProducts: Result.Result[Unit] =
    (recordMap.size, containerMap.size) match {
      case (0, 0) => Right(())
      case (_, 0) =>
        val (_, record) = recordMap.head
        val loc = Locations.get(record.aNode._2.id)
        Left(
          SemanticError.InvalidDataProducts(
            loc,
            "component that specifies records must specify at least one container"
          )
        )
      case (0, _) =>
        val (_, container) = containerMap.head
        val loc = Locations.get(container.aNode._2.id)
        Left(
          SemanticError.InvalidDataProducts(
            loc,
            "component that specifies containers must specify at least one record"
          )
        )
      case _ => Right(())
    }

  /** Checks that component has at least one async input port or async command */
  private def checkAsyncInput:
    Result.Result[Unit] = checkNoAsyncInput match {
      case Left(_) => Right(())
      case _ =>
        val node = this.aNode._2
        val kind = node.data.kind
        val loc = Locations.get(node.id)
        Left(SemanticError.MissingAsync(kind.toString, loc))
    }

  /** Checks that component has no async input ports */
  private def checkNoAsyncInput:
    Result.Result[Unit] = {
      def checkPortInstances() = Result.map(
        this.portMap.values.toList,
        (instance: PortInstance) => {
          val loc = instance.getLoc
          val error = SemanticError.PassiveAsync(loc)
          instance match {
            case general : PortInstance.General =>
              general.kind match {
                case PortInstance.General.Kind.AsyncInput(_, _) =>
                  Left(error)
                case _ => Right(())
              }
            case special : PortInstance.Special =>
              special.specifier.inputKind match {
                case Some(Ast.SpecPortInstance.Async) => Left(error)
                case _ => Right(())
              }
            case internal: PortInstance.Internal => Left(error)
          }
        }
      )
      def checkCommands() = Result.map(
        this.commandMap.values.toList,
        (command: Command) => command match {
          case Command.NonParam(_, Command.NonParam.Async(_, _)) =>
            Left(SemanticError.PassiveAsync(command.getLoc))
          case _ => Right(())
        }
      )
      def checkStateMachines() = Result.map(
        this.stateMachineInstanceMap.values.toList,
        (instance: StateMachineInstance) => {
          val loc = instance.getLoc
          val error = SemanticError.PassiveStateMachine(loc)
          Left(error)
        }
      ) 
      for {
        _ <- checkPortInstances()
        _ <- checkCommands()
        _ <- checkStateMachines()
      }
      yield ()
    }

  /** Checks that there are no duplicate names in dictionaries */
  private def checkNoDuplicateNames:
    Result.Result[Unit] =
      for {
        _ <- Analysis.checkDictionaryNames(
          this.paramMap,
          "parameter",
          _.getName,
          _.getLoc
        )
        _ <- Analysis.checkDictionaryNames(
          this.commandMap,
          "command",
          _.getName,
          _.getLoc
        )
        _ <- Analysis.checkDictionaryNames(
          this.eventMap,
          "event",
          _.getName,
          _.getLoc
        )
        _ <- Analysis.checkDictionaryNames(
          this.tlmChannelMap,
          "telemetry channel",
          _.getName,
          _.getLoc
        )
        _ <- Analysis.checkDictionaryNames(
          this.containerMap,
          "container",
          _.getName,
          _.getLoc
        )
        _ <- Analysis.checkDictionaryNames(
          this.recordMap,
          "record",
          _.getName,
          _.getLoc
        )
      }
      yield ()

  /** Checks whether a component is valid */
  private def checkValidity: Result.Result[Unit] = {
    val kind = this.aNode._2.data.kind
    for {
      _ <- checkNoDuplicateNames
      _ <- kind match {
        case Ast.ComponentKind.Passive => checkNoAsyncInput
        case _ => checkAsyncInput
      }
      _ <- checkRequiredPorts
      _ <- checkDataProducts
    }
    yield ()
  }

  /** Complete a component definition */
  def complete: Result.Result[Component] = for {
    c <- this.constructPortMatchingList
    _ <- c.checkValidity
  } yield c

  /** Construct the port matching list */
  private def constructPortMatchingList: Result.Result[Component] =
    for {
      list <- Result.map(this.specPortMatchingList, constructPortMatching)
    }
    yield this.copy(portMatchingList = list)

  /** Constructs a port matching from a specifier */
  private def constructPortMatching(aNode: Ast.Annotated[AstNode[Ast.SpecPortMatching]]):
    Result.Result[Component.PortMatching] = {
    val node = aNode._2
    val loc = Locations.get(node.id)
    def checkNames(
      name1: String,
      name2: String
    ): Result.Result[Unit] =
      if (name1 != name2) Right(())
      else Left(
        SemanticError.InvalidPortMatching(loc, s"repeated name $name1")
      )
    def getInstance(node: AstNode[String]):
    Result.Result[PortInstance.General] = {
      val name = node.data
      val loc = Locations.get(node.id)
      for {
        instance <- this.getPortInstance(node)
        general <- instance match {
          case general: PortInstance.General => Right(general)
          case _ => Left(
            SemanticError.InvalidPortMatching(
              loc,
              s"$name is not a valid port instance for matching"
            )
          )
        }
      } yield general
    }
    def checkSizes(
      instance1: PortInstance.General,
      instance2: PortInstance.General
    ) = {
      val size1 = instance1.size
      val size2 = instance2.size
      if (size1 == size2) Right(())
      else Left(
        SemanticError.InvalidPortMatching(
          loc,
          s"mismatched port sizes ($size1 vs. $size2)"
        )
      )
    }
    val port1 = node.data.port1
    val port2 = node.data.port2
    for {
      _ <- checkNames(port1.data, port2.data)
      instance1 <- getInstance(port1)
      instance2 <- getInstance(port2)
      _ <- checkSizes(instance1, instance2)
    }
    yield Component.PortMatching(aNode, instance1, instance2)
  }

}

object Component {

  /** A port matching */
  final case class PortMatching(
    aNode: Ast.Annotated[AstNode[Ast.SpecPortMatching]],
    instance1: PortInstance.General,
    instance2: PortInstance.General
  ) {

    override def toString = s"match $instance1 with $instance2"

    /** Gets the location of a port matching */
    def getLoc: Location = Locations.get(aNode._2.id)

  }

}
