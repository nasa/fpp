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
  /** The next default parameter ID */
  defaultParamId: Int = 0
) {

  /** Add a port instance */
  def addPortInstance(instance: PortInstance): Result.Result[Component] =
    for {
      c <- updatePortMap(instance)
      c <- instance match {
        case special : PortInstance.Special => updateSpecialPortMap(special)
        case _ => Right(c)
      }
    }
    yield c

  /** Add a command */
  def addCommand(opcodeOpt: Option[Command.Opcode], command: Command): Result.Result[Component] = {
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

  /** Add an event */
  def addEvent(idOpt: Option[Event.Id], event: Event): Result.Result[Component] = {
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
    eventMap.get(id) match {
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

  /** Add a telemetry channel */
  def addTlmChannel(idOpt: Option[TlmChannel.Id], tlmChannel: TlmChannel): Result.Result[Component] = {
    val id = idOpt.getOrElse(defaultTlmChannelId)
    tlmChannelMap.get(id) match {
      case Some(prevTlmChannel) =>
        val value = Analysis.displayIdValue(id)
        val loc = tlmChannel.getLoc
        val prevLoc = prevTlmChannel.getLoc
        Left(SemanticError.DuplicateIdValue(value, loc, prevLoc))
      case None =>
        val tlmChannelMap = this.tlmChannelMap + (id -> tlmChannel)
        val component = this.copy(tlmChannelMap = tlmChannelMap, defaultTlmChannelId = id + 1)
        Right(component)
    }
  }

  /** Add a port instance to the port map */
  private def updatePortMap(instance: PortInstance): Result.Result[Component] = {
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
  private def updateSpecialPortMap(instance: PortInstance.Special): Result.Result[Component] = {
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

}
