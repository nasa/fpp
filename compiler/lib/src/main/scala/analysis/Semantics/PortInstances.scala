package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check port instances */
object PortInstances {

  /** Creates a port instance from a port instance specifier */
  def fromSpecPortInstance(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): 
    Result.Result[PortInstance] = {
    val node = aNode._2
    val data = node.getData
    /** Creates a general port instance */
    def general(a: Analysis, specifier: Ast.SpecPortInstance.General) = {
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
        for {
          instance <- general(a, specifier)
          _ <- checkAsyncInput(instance)
        } yield instance
      case specifier : Ast.SpecPortInstance.Special =>
        Right(PortInstance.Special(aNode, specifier))
    }
      
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

  /** Checks async input uses of general ports */
  private def checkAsyncInput(instance: PortInstance.General) = {
    val loc = Locations.get(instance.aNode._2.getId)
    def checkRefParams(defPort: Ast.DefPort, defLoc: Location) = {
      val numRefParams = defPort.params.filter(aNode => {
        val param = aNode._2.getData
        param.kind == Ast.FormalParam.Ref
      }).size
      if (numRefParams > 0) Left(
        SemanticError.InvalidPortInstance(
          loc,
          "async input port may not have ref parameters",
          defLoc
        )
      )
      else Right(())
    }
    def checkReturnType(defPort: Ast.DefPort, defLoc: Location) = {
      defPort.returnType match {
        case Some(_) => Left(
          SemanticError.InvalidPortInstance(
            loc,
            "async input port may not return a value",
            defLoc
          )
        )
        case None => Right(())
      }
    }
    (instance.kind, instance.ty) match {
      case (
        kind: PortInstance.General.Kind.AsyncInput,
        PortInstance.General.Type.DefPort(symbol)
      ) => {
        val node = symbol.node._2
        val defPort = node.getData
        val defLoc = Locations.get(node.getId)
        for {
          _ <- checkRefParams(defPort, defLoc)
          _ <- checkReturnType(defPort, defLoc)
        } yield ()
      }
      case _ => Right(())
    }
  }

}
