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
    Result.Result[Unit] = {
      // TODO
      Right(())
    }

  /** Check that component provides ports required by dictionaries */
  private def checkRequiredPorts(a: Analysis, c: Component):
    Result.Result[Unit] = {
      // TODO
      Right(())
    }

}
