package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

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
  defaultOpcode: Int = 0,
  /** The map from telemetry channel IDs to channels */
  tlmChannelMap: Map[TlmChannel.Id, TlmChannel] = Map(),
  /** The next default channel ID */
  defaultTlmChannelId: Int = 0,
  /** The map from event IDs to events */
  eventMap: Map[Event.Id, Event] = Map(),
  /** The next default event ID */
  defaultEventId: Int = 0,
  /** The map from parameter IDs to parameters */
  paramMap: Map[Param.Id, Param] = Map(),
  /** The list of port matching specifiers */
  specPortMatchingList: List[Ast.Annotated[AstNode[Ast.SpecPortMatching]]] = Nil,
  /** The list of port matching constraints */
  portMatchingList: List[Component.PortMatching] = Nil,
  /** The next default parameter ID */
  defaultParamId: Int = 0
) {

  /** Gets the max identifier */
  def getMaxId: Int = {
    def maxInMap[T](map: Map[Int, T]): Int =
      if (map.size == 0) -1 else map.keys.max
    val maxMap = Vector(
      commandMap,
      eventMap,
      paramMap,
      tlmChannelMap
    ).maxBy(maxInMap)
    maxInMap(maxMap)
  }

  /** Gets a port instance by name */
  def getPortInstance(name: AstNode[Ast.Ident]): Result.Result[PortInstance] =
    portMap.get(name.data) match {
      case Some(portInstance) => Right(portInstance)
      case None => Left(
        SemanticError.InvalidPortInstanceId(
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

  /** Add a port instance */
  def addPortInstance(instance: PortInstance): Result.Result[Component] =
    for {
      c <- updatePortMap(instance)
      c <- instance match {
        case special: PortInstance.Special => c.updateSpecialPortMap(special)
        case _ => Right(c)
      }
    }
    yield c

  /** Add a port instance to the port map */
  private def updatePortMap(instance: PortInstance):
  Result.Result[Component] = {
    val name = instance.getUnqualifiedName
    portMap.get(name) match {
      case Some(prevInstance) =>
        val loc = instance.getLoc
        val prevLoc = prevInstance.getLoc
        Left(SemanticError.DuplicatePortInstance(name, loc, prevLoc))
      case None => 
        val portMap = this.portMap + (name -> instance)
        val component = this.copy(portMap = portMap)
        Right(component)
    }
  }

  /** Add a port instance to the special port map */
  private def updateSpecialPortMap(instance: PortInstance.Special):
  Result.Result[Component] = {
    val kind = instance.specifier.kind
    specialPortMap.get(kind) match {
      case Some(prevInstance) =>
        val loc = instance.getLoc
        val prevLoc = prevInstance.getLoc
        Left(SemanticError.DuplicatePortInstance(kind.toString, loc, prevLoc))
      case None => 
        val specialPortMap = this.specialPortMap + (kind -> instance)
        val component = this.copy(specialPortMap = specialPortMap)
        Right(component)
    }
  }

  /** Add a telemetry channel */
  def addTlmChannel(
    idOpt: Option[TlmChannel.Id],
    tlmChannel: TlmChannel):
  Result.Result[Component] = {
    val id = idOpt.getOrElse(defaultTlmChannelId)
    tlmChannelMap.get(id) match {
      case Some(prevTlmChannel) =>
        val value = Analysis.displayIdValue(id)
        val loc = tlmChannel.getLoc
        val prevLoc = prevTlmChannel.getLoc
        Left(SemanticError.DuplicateIdValue(value, loc, prevLoc))
      case None =>
        val tlmChannelMap = this.tlmChannelMap + (id -> tlmChannel)
        val component = this.copy(
          tlmChannelMap = tlmChannelMap,
          defaultTlmChannelId = id + 1
        )
        Right(component)
    }
  }

  /** Add an event */
  def addEvent(
    idOpt: Option[Event.Id],
    event: Event
  ): Result.Result[Component] = {
    val id = idOpt.getOrElse(defaultEventId)
    eventMap.get(id) match {
      case Some(prevEvent) =>
        val value = Analysis.displayIdValue(id)
        val loc = event.getLoc
        val prevLoc = prevEvent.getLoc
        Left(SemanticError.DuplicateIdValue(value, loc, prevLoc))
      case None =>
        val eventMap = this.eventMap + (id -> event)
        val component = this.copy(eventMap = eventMap, defaultEventId = id + 1)
        Right(component)
    }
  }
  
  /** Add a parameter */
  def addParam(idOpt: Option[Param.Id], param: Param): 
  Result.Result[Component] = {
    val id = idOpt.getOrElse(defaultParamId)
    paramMap.get(id) match {
      case Some(prevParam) =>
        val value = Analysis.displayIdValue(id)
        val loc = param.getLoc
        val prevLoc = prevParam.getLoc
        Left(SemanticError.DuplicateIdValue(value, loc, prevLoc))
      case None =>
        val paramMap = this.paramMap + (id -> param)
        val component = this.copy(
          paramMap = paramMap,
          defaultParamId = id + 1
        )
        val name = param.aNode._2.data.name
        val setCommand = Command.Param(param.aNode, Command.Param.Get)
        val saveCommand = Command.Param(param.aNode, Command.Param.Set)
        for {
          component <- component.addCommand(Some(param.setOpcode), setCommand)
          component <- component.addCommand(Some(param.saveOpcode), saveCommand)
        }
        yield component
    }
  }

  /** Check that component provides ports required by dictionaries */
  private def checkRequiredPorts:
    Result.Result[Unit] = {
      def requirePorts(
        mapSize: Int,
        specKind: String,
        portKinds: List[Ast.SpecPortInstance.SpecialKind]
      ) = if (mapSize > 0) Result.map(
        portKinds,
        (portKind: Ast.SpecPortInstance.SpecialKind) => 
          this.specialPortMap.get(portKind) match {
            case Some(_) => Right(())
            case None =>
              val loc = Locations.get(this.aNode._2.id)
              Left(SemanticError.MissingPort(loc, specKind, portKind.toString))
          }
      ) else Right(())
      import Ast.SpecPortInstance._
      for {
        _ <- requirePorts(
          this.paramMap.size,
          "parameter",
          List(ParamGet, ParamSet, CommandRecv, CommandReg, CommandResp)
        )
        _ <- requirePorts(
          this.commandMap.size,
          "command",
          List(CommandRecv, CommandReg, CommandResp)
        )
        _ <- requirePorts(
          this.eventMap.size,
          "event",
          List(Event, TextEvent, TimeGet)
        )
        _ <- requirePorts(
          this.tlmChannelMap.size,
          "telemetry",
          List(Telemetry, TimeGet)
        )
      }
      yield ()
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
            case PortInstance.General(_, _, PortInstance.General.Kind.AsyncInput(_, _), _, _) =>
              Left(error)
            case internal: PortInstance.Internal => Left(error)
            case _ => Right(())
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
      for {
        _ <- checkPortInstances()
        _ <- checkCommands()
      }
      yield ()
    }

  /** Checks that there are no duplicate names in dictionaries */
  private def checkNoDuplicateNames:
    Result.Result[Unit] = {
      def checkDictionary[Id,Value](
        dictionary: Map[Id,Value],
        kind: String,
        getName: Value => String,
        getLoc: Value => Location
      ) = {
        val initialMap: Map[String, Location] = Map()
        Result.foldLeft (dictionary.toList) (initialMap) ((map, pair) => {
          val (_, value) = pair
          val name = getName(value)
          val loc = getLoc(value)
          map.get(name) match {
            case Some(prevLoc) =>
              Left(SemanticError.DuplicateDictionaryName(
                kind, name, loc, prevLoc
              ))
            case _ => Right(map + (name -> loc))
          }
        })
      }
      for {
        _ <- checkDictionary(
          this.paramMap,
          "parameter",
          (param: Param) => param.getName,
          (param: Param) => param.getLoc
        )
        _ <- checkDictionary(
          this.commandMap,
          "command",
          (command: Command) => command.getName,
          (command: Command) => command.getLoc
        )
        _ <- checkDictionary(
          this.eventMap,
          "event",
          (event: Event) => event.getName,
          (event: Event) => event.getLoc
        )
        _ <- checkDictionary(
          this.tlmChannelMap,
          "telemetry channel",
          (tlmChannel: TlmChannel) => tlmChannel.getName,
          (tlmChannel: TlmChannel) => tlmChannel.getLoc
        )
      }
      yield ()
    }

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
        instance <- getPortInstance(node)
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
