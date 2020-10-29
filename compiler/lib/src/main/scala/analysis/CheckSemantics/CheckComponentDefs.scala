package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component definitions */
object CheckComponentDefs
  extends Analyzer 
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  override def defComponentAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val a1 = a.copy(component = Some(Component(aNode)))
    for {
      a <- super.defComponentAnnotatedNode(a1, aNode)
    }
    yield {
      val symbol = Symbol.Component(aNode)
      a.copy(componentMap = a.componentMap + (symbol -> a.component.get))
    }
  }

  override def specPortInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) = {
    // TODO
    // Check that if the instance is async, the port does not return values
    // Add the instance to the component
    for {
      _ <- createPortInstance(a, aNode)
    }
    yield a
  }


  /** Gets a priority from an AST node */
  private def getPriority(a: Analysis, priorityOpt: Option[AstNode[Ast.Expr]]):
    Result.Result[Option[Int]] = 
    priorityOpt match {
      case Some(priority) => a.getIntValue(priority.getId) match {
        case Right(v) => Right(Some(v))
        case Left(e) => Left(e)
      }
      case None => Right(None)
    }

  /** Gets a size from an AST node */
  private def getSize(a: Analysis, sizeOpt: Option[AstNode[Ast.Expr]]):
    Result.Result[Int] = 
    sizeOpt match {
      case Some(size) => a.getArraySize(size.getId)
      case None => Right(1)
    }

  /** Gets a queue full behavior from an AST node */
  private def getQueueFull(queueFullOpt: Option[AstNode[Ast.QueueFull]]):
    Ast.QueueFull =
    queueFullOpt match {
      case Some(queueFull) => queueFull.data
      case None => Ast.QueueFull.Assert
    }

  /** Creates a port instance from a port instance specifier */
  private def createPortInstance(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): 
    Result.Result[PortInstance] = {
    val node = aNode._2
    val data = node.getData
    /** Creates a general port instance */
    def createPortInstanceGeneral(a: Analysis, specifier: Ast.SpecPortInstance.General) = {
      for {
        _ <- (specifier.kind, specifier.priority) match {
          case (Ast.SpecPortInstance.AsyncInput, _) => Right(())
          case (_, Some(priority)) =>  {
            val loc = Locations.get(priority.getId)
            Left(SemanticError.InvalidPriority(loc))
          }
          case (_, None) => Right(())
        }
        _ <- (specifier.kind, specifier.queueFull) match {
          case (Ast.SpecPortInstance.AsyncInput, _) => Right(())
          case (_, Some(queueFull)) => {
            val loc = Locations.get(queueFull.getId)
            Left(SemanticError.InvalidQueueFull(loc))
          }
          case (_, None) => Right(())
        }
        size <- getSize(a, specifier.size)
        priority <- getPriority(a, specifier.priority)
      }
      yield {
        val kind = specifier.kind match {
          case Ast.SpecPortInstance.AsyncInput =>
            val queueFull = getQueueFull(specifier.queueFull)
            PortInstance.General.Kind.AsyncInput(priority, queueFull)
          case Ast.SpecPortInstance.GuardedInput =>
            PortInstance.General.Kind.GuardedInput
          case Ast.SpecPortInstance.Output =>
            PortInstance.General.Kind.Output
          case Ast.SpecPortInstance.SyncInput =>
            PortInstance.General.Kind.SyncInput
        }
        val ty = specifier.port match {
          case Some(qid) => {
            val symbol @ Symbol.Port(_) = a.useDefMap(qid.getId)
            PortInstance.General.Type.DefPort(symbol)
          }
          case None => PortInstance.General.Type.Serial
        }
        PortInstance.General(aNode, specifier, kind, size, ty)
      }
    }
    data match {
      case specifier : Ast.SpecPortInstance.General =>
        createPortInstanceGeneral(a, specifier)
      case specifier : Ast.SpecPortInstance.Special =>
        Right(PortInstance.Special(aNode, specifier))
    }
      
  }

}
