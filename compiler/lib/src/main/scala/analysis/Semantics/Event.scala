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
        every.data match {
          case Ast.ExprStruct(members) => {
            for {
              seconds <- a.getNonnegativeIntValue(members.find(_.data.name == "seconds").get.id)
              useconds <- a.getNonnegativeIntValue(members.find(_.data.name == "useconds").get.id)
            } yield TimeInterval(seconds, useconds)
          }
          case _ => throw InternalError("invalid interval value")
        }
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
