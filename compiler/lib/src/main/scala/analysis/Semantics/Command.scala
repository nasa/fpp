package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP command */
sealed trait Command {

  /** Gets the location of the command */
  def getLoc: Location

  /** Gets the name of the command */
  def getName: String

}

final object Command {

  type Opcode = Int

  /** A non-parameter command */
  final case class NonParam(
    aNode: Ast.Annotated[AstNode[Ast.SpecCommand]],
    kind: NonParam.Kind
  ) extends Command {
    def getLoc = Locations.get(aNode._2.id)
    def getName = aNode._2.data.name
  }

  final object NonParam {

    sealed trait Kind
    case class Async(
      priority: Option[Int],
      queueFull: Ast.QueueFull
    ) extends Kind
    case object Guarded extends Kind
    case object Sync extends Kind

  }

  /** A parameter command */
  final case class Param(
    aNode: Ast.Annotated[AstNode[Ast.SpecParam]],
    kind: Param.Kind,
  ) extends Command {
    def getLoc = Locations.get(aNode._2.id)
    def getName = {
      val paramName = aNode._2.data.name.toUpperCase
      kind match {
        case Param.Get => s"${paramName}_PARAM_GET"
        case Param.Set => s"${paramName}_PARAM_SET"
      }
    }
  }

  final object Param {

    sealed trait Kind
    case object Get extends Kind
    case object Set extends Kind

  }

}
