package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Evaluate implied enum constants */
object EvalImpliedEnumConsts extends ModuleAnalyzer {

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val loc = Locations.get(node.getId)
    val data = node.data
    val constants = data.constants.map(_._2)
    val numExprs = {
      def count(const: Ast.DefEnumConstant) = const.value match {
        case Some(_) => 1
        case None => 0
      }
      constants.map(_.getData).map(count).fold(0)(_ + _)
    }
    if (numExprs == 0) {
      def visitConstants(a: Analysis, value: Int, constants: List[AstNode[Ast.DefEnumConstant]]): Result =
        constants match {
          case Nil => Right(a)
          case node :: tail => {
            val enumType = a.typeMap(node.getId) match {
              case enumType @ Type.Enum(_, _, _) => enumType
              case _ => throw InternalError("enum definition should have enum type")
            }
            val a1 = a.assignValue(node -> Value.EnumConstant(value, enumType))
            visitConstants(a1, value + 1, tail)
          }
        }
      visitConstants(a, 0, constants)
    }
    else if (numExprs != constants.size)
      Left(SemanticError.InvalidEnumConstants(loc))
    else
      default(a)
  }

}
