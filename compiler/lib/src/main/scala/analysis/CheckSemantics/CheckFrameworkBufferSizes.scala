package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check that the commands, events, telemetry channels, and parameters
 *  of a component fit in the F Prime framework buffers that carry them */
object CheckFrameworkBufferSizes {

  /** A framework buffer, given by the framework constant that specifies
   *  its size */
  case class Buffer(sizeName: String)

  /** The buffer that carries the arguments of a command */
  val cmdArgBuffer = Buffer("FW_CMD_ARG_BUFFER_MAX_SIZE")

  /** The buffer that carries the arguments of an event */
  val logBuffer = Buffer("FW_LOG_BUFFER_MAX_SIZE")

  /** The buffer that carries the value of a parameter */
  val paramBuffer = Buffer("FW_PARAM_BUFFER_MAX_SIZE")

  /** The buffer that carries the value of a telemetry channel */
  val tlmBuffer = Buffer("FW_TLM_BUFFER_MAX_SIZE")

  /** Check a component */
  def component(a: Analysis, c: Component): Result.Result[Unit] =
    for {
      _ <- Result.foldLeft (sortById(c.commandMap)) (()) ((_, command) =>
        command match {
          case command: Command.NonParam =>
            val data = command.aNode._2.data
            checkSize(
              a,
              command.getLoc,
              s"arguments of command ${command.getName}",
              data.params.map(aNode => a.typeMap(aNode._2.data.typeName.id)),
              cmdArgBuffer
            )
          case _: Command.Param => Right(())
        }
      )
      _ <- Result.foldLeft (sortById(c.eventMap)) (()) ((_, event) =>
        checkSize(
          a,
          event.getLoc,
          s"arguments of event ${event.getName}",
          event.aNode._2.data.params.map(
            aNode => a.typeMap(aNode._2.data.typeName.id)
          ),
          logBuffer
        )
      )
      _ <- Result.foldLeft (sortById(c.tlmChannelMap)) (()) ((_, channel) =>
        checkSize(
          a,
          channel.getLoc,
          s"value of telemetry channel ${channel.getName}",
          List(channel.channelType),
          tlmBuffer
        )
      )
      _ <- Result.foldLeft (sortById(c.paramMap)) (()) ((_, param) =>
        checkSize(
          a,
          param.getLoc,
          s"value of parameter ${param.getName}",
          List(param.paramType),
          paramBuffer
        )
      )
    } yield ()

  /** Check that values of the types ts, serialized in sequence,
   *  can fit in a framework buffer. If the buffer size constant is not
   *  defined, there is nothing to check. The check uses the minimum
   *  serialized size, in which every string is empty: it reports an
   *  error only when no value of the types can fit in the buffer. */
  private def checkSize(
    a: Analysis,
    loc: Location,
    msg: String,
    ts: List[Type],
    buffer: Buffer
  ): Result.Result[Unit] =
    getFrameworkConstant(a, buffer.sizeName) match {
      case None => Right(())
      case Some((bufferSymbol, bufferSize)) =>
        val sizeOpt = ts.foldLeft (Some(BigInt(0)): Option[BigInt]) ((sizeOpt, t) =>
          for {
            s1 <- sizeOpt
            s2 <- getMinSerializedSize(a, t)
          } yield s1 + s2
        )
        sizeOpt match {
          case Some(size) if size > bufferSize =>
            Left(
              SemanticError.SerializedSizeTooLarge(
                loc,
                msg,
                size,
                buffer.sizeName,
                bufferSize,
                Locations.get(bufferSymbol.getNodeId)
              )
            )
          case _ => Right(())
        }
    }

  /** Get the minimum serialized size of a value of type t.
   *  A string value is serialized as its length followed by its
   *  characters, so its minimum size is the size of its length. */
  private def getMinSerializedSize(a: Analysis, t: Type): Option[BigInt] =
    t.getUnderlyingType match {
      case _: Type.String => Some(getStringLengthSize(a))
      case t: Type.Array =>
        val n = BigInt(t.getArraySize.get)
        getMinSerializedSize(a, t.anonArray.eltType).map(n * _)
      case t: Type.Struct =>
        t.anonStruct.members.foldLeft (Some(BigInt(0)): Option[BigInt]) {
          case (sizeOpt, (m, t1)) =>
            for {
              s1 <- sizeOpt
              s2 <- getMinSerializedSize(a, t1)
            } yield s1 + s2 * t.sizes.getOrElse(m, 1)
        }
      case t => Type.SerializedSize.ty(a, t)
    }

  /** Get the serialized size of the length of a string */
  private def getStringLengthSize(a: Analysis): BigInt = {
    val symbol = a.frameworkDefinitions.typeMap("FwSizeStoreType")
    Type.SerializedSize.ty(a, a.typeMap(symbol.getNodeId)).get
  }

  /** Get a framework constant and its value, if it is defined */
  private def getFrameworkConstant(a: Analysis, name: String):
    Option[(Symbol.Constant, BigInt)] =
    a.frameworkDefinitions.constantMap.get(name).map(
      symbol => (symbol, a.getBigIntValue(symbol.getNodeId))
    )

  /** Get the values of a map in order of their identifiers */
  private def sortById[T](map: Map[BigInt, T]): List[T] =
    map.toList.sortBy(_._1).map(_._2)

}
