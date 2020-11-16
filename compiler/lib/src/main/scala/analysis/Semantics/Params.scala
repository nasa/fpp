package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check parameters */
final object Params {

  /** Creates a parameter from a parameter specifier */
  def fromSpecParam(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecParam]]):
    Result.Result[Param] = {
      val node = aNode._2
      val data = node.data
      def computeDefaultValue(default: AstNode[Ast.Expr]) = {
        val paramType = a.typeMap(data.typeName.id)
        val defaultValue = a.valueMap(default.id)
        val defaultType = a.typeMap(default.id)
        val loc = Locations.get(default.id)
        for (_ <- Analysis.convertTypes(loc, defaultType -> paramType))
          yield defaultValue
      }
      for {
        default <- Result.mapOpt(data.default, computeDefaultValue)
        setOpcode <- a.getIntValueOpt(data.setOpcode)
        saveOpcode <- a.getIntValueOpt(data.saveOpcode)
      }
      yield Param(aNode, default, setOpcode, saveOpcode)
   }

}
