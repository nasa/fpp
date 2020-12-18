package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check telemetry channels */
final object TlmChannels {

  /** Creates a telemetry channel from a telemetry channel specifier */
  def fromSpecTlmChannel(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecTlmChannel]]):
    Result.Result[TlmChannel] = {
      val node = aNode._2
      val data = node.data
      val channelType = a.typeMap(data.typeName.id)
      val update = data.update.getOrElse(Ast.SpecTlmChannel.Always)
      for {
        format <- Result.mapOpt(
          data.format, 
          format => Analysis.computeFormat(format, List(channelType))
        )
        lowLimits <- computeLimits(a, data.low)
        highLimits <- computeLimits(a, data.high)
      }
      yield TlmChannel(aNode, channelType, update, format, lowLimits, highLimits)
   }

   /** Computes limits from AST limits */
   private def computeLimits(
     a: Analysis,
     astLimits: List[Ast.SpecTlmChannel.Limit]
   ): Result.Result[TlmChannel.Limits] = {
     val limits0: TlmChannel.Limits = Map()
     Result.foldLeft (astLimits) (limits0) ((limits, limit) => {
       val kind = limit._1
       val e = limit._2
       limits.get(kind.data) match {
         case Some((id, _)) =>
           val loc = Locations.get(kind.id)
           val prevLoc = Locations.get(id)
           Left(SemanticError.DuplicateLimit(loc, prevLoc))
         case None =>
           val v = a.valueMap(e.id)
           Right(limits + (kind.data -> (kind.id, v)))
       }
     })
   }

}
