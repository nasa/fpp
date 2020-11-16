package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check events */
final object Events {

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
      for {
        _ <- checkRefParams(data.params)
        format <- Result.mapOpt(
          data.format, 
          format => Analysis.computeFormat(
            format,
            data.params.map(aNode => a.typeMap(aNode._2.data.typeName.id))
          )
        )
        throttle <- a.getIntValueOpt(data.throttle)
      }
      yield Event(aNode, format, throttle)
   }

}
