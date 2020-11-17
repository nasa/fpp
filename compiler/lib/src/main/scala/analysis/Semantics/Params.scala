package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check parameters */
final object Params {

  /** Creates a parameter from a parameter specifier
   *  Returns the new default opcode */
  def fromSpecParam(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecParam]], defaultOpcode: Int):
    Result.Result[(Param, Int)] = {
      val node = aNode._2
      val data = node.data
      val paramType = a.typeMap(data.typeName.id)
      def computeDefaultValue(default: AstNode[Ast.Expr]) = {
        val defaultValue = a.valueMap(default.id)
        val defaultType = a.typeMap(default.id)
        val loc = Locations.get(default.id)
        for (_ <- Analysis.convertTypes(loc, defaultType -> paramType))
          yield Analysis.convertValueToType(defaultValue, paramType)
      }
      def computeOpcode(intOpt: Option[Int], defaultOpcode: Int) =
        intOpt match {
          case Some(i) => (i, defaultOpcode)
          case None => (defaultOpcode, defaultOpcode + 1)
        }
      for {
        default <- Result.mapOpt(data.default, computeDefaultValue)
        setOpcodeOpt <- a.getIntValueOpt(data.setOpcode)
        saveOpcodeOpt <- a.getIntValueOpt(data.saveOpcode)
      }
      yield {
        val (setOpcode, defaultOpcode1) = computeOpcode(setOpcodeOpt, defaultOpcode)
        val (saveOpcode, defaultOpcode2) = computeOpcode(saveOpcodeOpt, defaultOpcode1)
        (Param(aNode, default, setOpcode, saveOpcode), defaultOpcode2)
      }
   }

}
