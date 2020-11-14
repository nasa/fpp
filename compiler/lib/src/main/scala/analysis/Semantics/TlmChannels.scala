package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check commands */
final object TlmChannels {

  /** Creates a command from a telemetry channel specifier */
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
        lowLimits <- getLimits(data.low)
        highLimits <- getLimits(data.high)
      }
      yield TlmChannel(aNode, update, format, lowLimits, highLimits)
   }

   /** Gets limits */
   private def getLimits(limits: List[Ast.SpecTlmChannel.Limit]): Result.Result[TlmChannel.Limits] = {
     Right(Map())
   }

}
