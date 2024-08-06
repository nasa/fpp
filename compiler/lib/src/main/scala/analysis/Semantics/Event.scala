package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP event */
final case class Event(
  aNode: Ast.Annotated[AstNode[Ast.SpecEvent]],
  format: Format,
  throttle: Option[Int]
) {

  /** Gets the name of the event */
  def getName = aNode._2.data.name

  /** Gets the location of the event */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object Event {

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
      def checkDisplayableParams(params: Ast.FormalParamList) = {
        if(!params.forall(aNode => {
          val id = aNode._2.data.typeName.id
          val paramType = a.typeMap(id)
          paramType.isDisplayable
        })) {
          Left(
            SemanticError.InvalidEvent(
              loc, 
              "event parameters need to be displayable type")
          )
        }
        else Right(())
      }
      for {
        _ <- checkRefParams(data.params)
        _ <- checkDisplayableParams(data.params)
        format <- Analysis.computeFormat(
          data.format,
          data.params.map(aNode => a.typeMap(aNode._2.data.typeName.id))
        )
        throttle <- a.getNonnegativeIntValueOpt(data.throttle)
      }
      yield Event(aNode, format, throttle)
   }

}
