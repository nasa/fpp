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

object Command {

  type Opcode = Int

  /** A non-parameter command */
  final case class NonParam(
    aNode: Ast.Annotated[AstNode[Ast.SpecCommand]],
    kind: NonParam.Kind
  ) extends Command {
    override def getLoc = Locations.get(aNode._2.id)
    override def getName = aNode._2.data.name
  }

  object NonParam {

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
    override def getLoc = Locations.get(aNode._2.id)
    override def getName = {
      val paramName = aNode._2.data.name.toUpperCase
      kind match {
        case Param.Get => s"${paramName}_PARAM_GET"
        case Param.Set => s"${paramName}_PARAM_SET"
      }
    }
  }

  object Param {

    sealed trait Kind
    case object Get extends Kind
    case object Set extends Kind

  }

  /** Creates a command from a command specifier */
  def fromSpecCommand(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]):
    Result.Result[Command] = {
      val node = aNode._2
      val data = node.data
      val loc = Locations.get(node.id)
      def checkRefParams(params: Ast.FormalParamList) = {
        val numRefParams = Analysis.getNumRefParams(params)
        if (numRefParams != 0) Left(
          SemanticError.InvalidCommand(
            loc,
            "command may not have ref parameters",
          )
        )
        else Right(())
      }
      for {
        _ <- (data.kind, data.priority) match {
          case (Ast.SpecCommand.Async, _) => Right(())
          case (_, Some(priority)) =>
            val loc = Locations.get(priority.id)
            Left(SemanticError.InvalidPriority(loc))
          case (_, None) => Right(())
        }
        _ <- (data.kind, data.queueFull) match {
          case (Ast.SpecCommand.Async, _) => Right(())
          case (_, Some(queueFull)) =>
            val loc = Locations.get(queueFull.id)
            Left(SemanticError.InvalidQueueFull(loc))
          case (_, None) => Right(())
        }
        priority <- a.getIntValueOpt(data.priority)
        _ <- Analysis.checkForDuplicateParameter(data.params)
        _ <- checkRefParams(data.params)
      }
      yield {
        val kind = data.kind match {
          case Ast.SpecCommand.Async =>
            val queueFull = Analysis.getQueueFull(data.queueFull.map(_.data))
            Command.NonParam.Async(priority, queueFull)
          case Ast.SpecCommand.Guarded => Command.NonParam.Guarded
          case Ast.SpecCommand.Sync => Command.NonParam.Sync
        }
        Command.NonParam(aNode, kind)
      }
   }

}
