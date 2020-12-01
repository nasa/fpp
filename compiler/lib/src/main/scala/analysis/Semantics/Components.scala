package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check components */
object Components {

  /** Checks whether a component is valid */
  def checkValidity(a: Analysis, c: Component): Result.Result[Unit] = {
    val kind = c.aNode._2.data.kind
    for {
      _ <- checkNoDuplicateNames(a, c)
      _ <- kind match {
        case Ast.ComponentKind.Passive => checkNoAsyncInput(a, c)
        case _ => checkAsyncInput(a, c)
      }
      _ <- checkRequiredPorts(a, c)
    }
    yield ()
  }

  /** Checks that there are no duplicate names in dictionaries */
  private def checkNoDuplicateNames(a: Analysis, c: Component):
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
          c.paramMap,
          "parameter",
          (param: Param) => param.getName,
          (param: Param) => param.getLoc
        )
        _ <- checkDictionary(
          c.commandMap,
          "command",
          (command: Command) => command.getName,
          (command: Command) => command.getLoc
        )
        _ <- checkDictionary(
          c.eventMap,
          "event",
          (event: Event) => event.getName,
          (event: Event) => event.getLoc
        )
        _ <- checkDictionary(
          c.tlmChannelMap,
          "telemetry channel",
          (tlmChannel: TlmChannel) => tlmChannel.getName,
          (tlmChannel: TlmChannel) => tlmChannel.getLoc
        )
      }
      yield ()
    }

  /** Checks that component has no async input ports */
  private def checkNoAsyncInput(a: Analysis, c: Component):
    Result.Result[Unit] = {
      def checkPortInstances() = Result.map(
        c.portMap.values.toList,
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
        c.commandMap.values.toList,
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

  /** Checks that component has at least one async input port or async command */
  private def checkAsyncInput(a: Analysis, c: Component):
    Result.Result[Unit] = checkNoAsyncInput(a, c) match {
      case Left(_) => Right(())
      case _ =>
        val node = c.aNode._2
        val kind = node.data.kind
        val loc = Locations.get(node.id)
        Left(SemanticError.MissingAsync(kind.toString, loc))
    }

  /** Check that component provides ports required by dictionaries */
  private def checkRequiredPorts(a: Analysis, c: Component):
    Result.Result[Unit] = {
      def requirePorts(
        mapSize: Int,
        specKind: String,
        portKinds: List[Ast.SpecPortInstance.SpecialKind]
      ) = if (mapSize > 0) Result.map(
        portKinds,
        (portKind: Ast.SpecPortInstance.SpecialKind) => 
          c.specialPortMap.get(portKind) match {
            case Some(_) => Right(())
            case None =>
              val loc = Locations.get(c.aNode._2.id)
              Left(SemanticError.MissingPort(loc, specKind, portKind.toString))
          }
      ) else Right(())
      import Ast.SpecPortInstance._
      for {
        _ <- requirePorts(
          c.paramMap.size,
          "parameter",
          List(ParamGet, ParamSet, CommandRecv, CommandReg, CommandResp)
        )
        _ <- requirePorts(
          c.commandMap.size,
          "command",
          List(CommandRecv, CommandReg, CommandResp)
        )
        _ <- requirePorts(
          c.eventMap.size,
          "event",
          List(Event, TextEvent, TimeGet)
        )
        _ <- requirePorts(
          c.tlmChannelMap.size,
          "telemetry",
          List(Telemetry, TimeGet)
        )
      }
      yield ()
    }

}
