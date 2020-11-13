package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check commands */
final object Commands {

  /** Creates a command from a command specifier */
  def fromSpecCommand(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecCommand]]):
    Result.Result[Command] = {
      val node = aNode._2
      val data = node.data
      val loc = Locations.get(node.id)
      def checkRefParams(params: Ast.FormalParamList) = {
        val numRefParams = Analysis.getNumRefParams(params)
        if (numRefParams != 0) Left(
          SemanticError.InvalidInternalPort(
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
        val queueFull = Analysis.getQueueFull(data.queueFull.map(_.data))
        Command(aNode, priority, queueFull)
      }
   }

}
