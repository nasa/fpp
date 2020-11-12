package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check port instances */
object PortInstances {

  /** Creates a port instance from an internal port specifier */
  def fromSpecInternalPort(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]]):
    Result.Result[PortInstance.Internal] = {
    val node = aNode._2
    val data = node.data
    val loc = Locations.get(node.id)
    def checkRefParams(params: Ast.FormalParamList) = {
      val numRefParams = getNumRefParams(params)
      if (numRefParams != 0) Left(
        SemanticError.InvalidInternalPort(
          loc,
          "internal port may not have ref parameters",
        )
      )
      else Right(())
    }
    for {
      priority <- getPriority(a, data.priority)
      _ <- checkRefParams(data.params)
    }
    yield {
      val queueFull = getQueueFull(data.queueFull)
      PortInstance.Internal(
        aNode,
        priority,
        queueFull
      )
    }
  }

  /** Creates a port instance from a port instance specifier */
  def fromSpecPortInstance(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]): 
    Result.Result[PortInstance] = {
    val node = aNode._2
    val data = node.data
    /** Creates a general port instance */
    def general(a: Analysis, specifier: Ast.SpecPortInstance.General) = {
      for {
        _ <- (specifier.kind, specifier.priority) match {
          case (Ast.SpecPortInstance.AsyncInput, _) => Right(())
          case (_, Some(priority)) =>  {
            val loc = Locations.get(priority.id)
            Left(SemanticError.InvalidPriority(loc))
          }
          case (_, None) => Right(())
        }
        _ <- (specifier.kind, specifier.queueFull) match {
          case (Ast.SpecPortInstance.AsyncInput, _) => Right(())
          case (_, Some(queueFull)) => {
            val loc = Locations.get(queueFull.id)
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
            val queueFull = getQueueFull(specifier.queueFull.map(_.data))
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
            val symbol @ Symbol.Port(_) = a.useDefMap(qid.id)
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
          _ <- checkGeneralAsyncInput(instance)
        } yield instance
      case specifier : Ast.SpecPortInstance.Special =>
        Right(PortInstance.Special(aNode, specifier))
    }
      
  }

  /** Gets a priority from an AST node */
  private def getPriority(a: Analysis, priorityOpt: Option[AstNode[Ast.Expr]]):
    Result.Result[Option[Int]] = 
    priorityOpt match {
      case Some(priority) => a.getIntValue(priority.id) match {
        case Right(v) => Right(Some(v))
        case Left(e) => Left(e)
      }
      case None => Right(None)
    }

  /** Gets a size from an AST node */
  private def getSize(a: Analysis, sizeOpt: Option[AstNode[Ast.Expr]]):
    Result.Result[Int] = 
    sizeOpt match {
      case Some(size) => a.getArraySize(size.id)
      case None => Right(1)
    }

  /** Gets a queue full behavior from an AST node */
  private def getQueueFull(queueFullOpt: Option[Ast.QueueFull]):
    Ast.QueueFull =
    queueFullOpt match {
      case Some(queueFull) => queueFull
      case None => Ast.QueueFull.Assert
    }

  /** Gets the number of ref params in a formal param list */
  private def getNumRefParams(params: Ast.FormalParamList) =
    params.filter(aNode => {
      val param = aNode._2.data
      param.kind == Ast.FormalParam.Ref
    }).size

  /** Checks general async input uses port definitions */
  private def checkGeneralAsyncInput(instance: PortInstance.General) = {
    val loc = Locations.get(instance.aNode._2.id)
    def checkRefParams(defPort: Ast.DefPort, defLoc: Location) = {
      val numRefParams = getNumRefParams(defPort.params)
      if (numRefParams != 0) Left(
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
        val defPort = node.data
        val defLoc = Locations.get(node.id)
        for {
          _ <- checkRefParams(defPort, defLoc)
          _ <- checkReturnType(defPort, defLoc)
        } yield ()
      }
      case _ => Right(())
    }
  }

}
