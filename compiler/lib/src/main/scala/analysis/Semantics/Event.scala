package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP event */
final case class Event(
  aNode: Ast.Annotated[AstNode[Ast.SpecEvent]],
  format: Format,
  throttle: Option[Event.Throttle]
) {

  /** Gets the name of the event */
  def getName = aNode._2.data.name

  /** Gets the location of the event */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object Event {

  case class TimeInterval(
    seconds: Int,
    useconds: Int,
  )

  case class Throttle(
    count: Int,
    every: Option[TimeInterval]
  )

  type Id = BigInt

  /** Creates a event from an event specifier */
  def fromSpecEvent(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecEvent]]):
    Result.Result[Event] = {
      val node = aNode._2
      val data = node.data
      val loc = Locations.get(node.id)
      def checkRefParams(params: Ast.FormalParamList) = {
        val numRefParams = Analysis.getNumRefParams(params)
        if (numRefParams != 0) Left(
          SemanticError.InvalidEvent(loc, "event may not have ref parameters")
        )
        else Right(())
      }
      def getEveryIntervalValue(every: AstNode[Ast.Expr]) = {
        val loc = Locations.get(every.id)
        val Value.AnonStruct(intervalValue) = Analysis.convertValueToType(a.valueMap(every.id), Type.AnonStruct(
          Map(
            ("seconds", Type.U32),
            ("useconds", Type.U32),
          )
        ))
        def getNonZeroMember(member: String, maxValue: Option[Int] = None) = {
          val Value.PrimitiveInt(v, Type.PrimitiveInt.U32) = Analysis.convertValueToType(
            intervalValue.get(member).get,
            Type.U32
          )

          if (v < 0) {
            Left(SemanticError.InvalidIntValue(
              loc, v, s"$member may not be negative"
            ))
          } else {
            maxValue match {
              case Some(m) => if v >= m then
                Left(SemanticError.InvalidIntValue(
                  loc, v, s"$member must be smaller than $m"
                )) else Right(v.intValue)
              case None => Right(v.intValue)
            }
          }
        }
        for {
          seconds <- getNonZeroMember("seconds")
          useconds <- getNonZeroMember("useconds", Some(1_000_000))
          _ <- {
            if (seconds + useconds) > 0 then Right(())
            else Left(SemanticError.InvalidIntValue(
              loc, 0, s"time interval must be greater than 0"
            ))
          }
        } yield TimeInterval(seconds, useconds)
      }
      def checkEventThrottle(throttle: Ast.EventThrottle) = {
        for {
          count <- a.getNonnegativeIntValue(throttle.count.id)
          every <- Result.mapOpt(throttle.every, getEveryIntervalValue)
        } yield Throttle(count, every)
      }
      for {
        _ <- checkRefParams(data.params)
        _ <- a.checkDisplayableParams(data.params, "type of event is not displayable")
        format <- Analysis.computeFormat(
          data.format,
          data.params.map(aNode => a.typeMap(aNode._2.data.typeName.id))
        )
        throttle <- Result.mapOpt(data.throttle, checkEventThrottle)
      }
      yield Event(aNode, format, throttle)
   }

}
