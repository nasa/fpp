package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check components */
object Components {

  /** Checks whether a component is valid */
  def checkValidity(a: Analysis, c: Component): Result.Result[Unit] =
    for {
      _ <- checkNoDuplicateNames(a, c)
      _ <- checkPassiveNoAsync(a, c)
      _ <- checkQueuedAsync(a, c)
      _ <- checkRequiredPorts(a, c)
    }
    yield ()

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
          c.paramMap,
          "parameter",
          (param: Param) => param.getName,
          (param: Param) => param.getLoc
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

  /** Checks that if component is passive, then no async ports */
  private def checkPassiveNoAsync(a: Analysis, c: Component):
    Result.Result[Unit] = {
      // TODO
      Right(())
    }

  /** Checks that if component is active or queued, then it has at least
   *  one async input port or async command */
  private def checkQueuedAsync(a: Analysis, c: Component):
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
