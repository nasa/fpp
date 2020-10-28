package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component definitions */
object CheckComponentDefs
  extends Analyzer 
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  /** Creates a port instance from an AST node */
  private def createPortInstance(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): 
    Result.Result[PortInstance] = {
    val node = aNode._2
    val data = node.getData
    /** Creates a general port instance */
    def createPortInstanceGeneral(a: Analysis, general: Ast.SpecPortInstance.General) = {
      for {
        _ <- (general.kind, general.priority) match {
          case (Ast.SpecPortInstance.AsyncInput, _) => Right(())
          case (_, Some(priority)) =>  {
            val loc = Locations.get(priority.getId)
            Left(SemanticError.InvalidPriority(loc))
          }
          case (_, None) => Right(())
        }
        _ <- (general.kind, general.queueFull) match {
          case (Ast.SpecPortInstance.AsyncInput, _) => Right(())
          case (_, Some(queueFull)) => {
            val loc = Locations.get(queueFull.getId)
            Left(SemanticError.InvalidQueueFull(loc))
          }
          case (_, None) => Right(())
        }
        size <- general.size match {
          case Some(size) => a.getArraySize(size.getId)
          case None => Right(1)
        }
        priority <- general.priority match {
          case Some(priority) => a.getIntValue(priority.getId) match {
            case Right(v) => Right(Some(v))
            case Left(e) => Left(e)
          }
          case None => Right(None)
        }
      }
      yield {
        val kind = general.kind match {
          case Ast.SpecPortInstance.AsyncInput =>
            val queueFull = general.queueFull match {
              case Some(qf) => qf.data
              case None => Ast.QueueFull.Assert
            }
            PortInstance.General.Kind.AsyncInput(priority, queueFull)
          case Ast.SpecPortInstance.GuardedInput =>
            PortInstance.General.Kind.GuardedInput
          case Ast.SpecPortInstance.Output =>
            PortInstance.General.Kind.Output
          case Ast.SpecPortInstance.SyncInput =>
            PortInstance.General.Kind.SyncInput
        }
        val ty = general.port match {
          case Some(qid) => {
            val symbol @ Symbol.Port(_) = a.useDefMap(qid.getId)
            PortInstance.General.Type.DefPort(symbol)
          }
          case None => PortInstance.General.Type.Serial
        }
        PortInstance.General(kind, size, ty, aNode, general)
      }
    }
    data match {
      case general : Ast.SpecPortInstance.General =>
        createPortInstanceGeneral(a, general)
      case special : Ast.SpecPortInstance.Special =>
        Right(PortInstance.Special(aNode, special))
    }
      
  }

}
