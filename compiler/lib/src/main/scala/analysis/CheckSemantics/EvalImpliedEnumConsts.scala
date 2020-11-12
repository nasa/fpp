package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Evaluate implied enum constants */
object EvalImpliedEnumConsts
  extends Analyzer
  with ModuleAnalyzer
  with ComponentAnalyzer
{

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val loc = Locations.get(node.id)
    val data = node.data
    val constants = data.constants.map(_._2)
    val numExprs = {
      def count(const: Ast.DefEnumConstant) = const.value match {
        case Some(_) => 1
        case None => 0
      }
      constants.map(_.data).map(count).fold(0)(_ + _)
    }
    if (numExprs == 0) {
      val (a1, _) = constants.foldLeft((a, 0))( (pair, node) => {
        val (a, intValue) = pair
        val enumType = a.typeMap(node.id) match {
          case enumType : Type.Enum => enumType
          case _ => throw InternalError("type of enum definition should be enum type")
        }
        val value = (node.data.name, BigInt(intValue))
        val a1 = a.assignValue(node -> Value.EnumConstant(value, enumType))
        (a1, intValue + 1)
      } )
      Right(a1)
    }
    else if (numExprs != constants.size)
      Left(SemanticError.InvalidEnumConstants(loc))
    else
      default(a)
  }

}
