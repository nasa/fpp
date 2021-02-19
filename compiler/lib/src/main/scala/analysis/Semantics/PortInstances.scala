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
        val numRefParams = Analysis.getNumRefParams(params)
        if (numRefParams != 0) Left(
          SemanticError.InvalidInternalPort(
            loc,
            "internal port may not have ref parameters",
          )
        )
        else Right(())
      }
      for {
        priority <- a.getIntValueOpt(data.priority)
        _ <- Analysis.checkForDuplicateParameter(data.params)
        _ <- checkRefParams(data.params)
      }
      yield {
        val queueFull = Analysis.getQueueFull(data.queueFull)
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
            case (_, Some(priority)) =>
              val loc = Locations.get(priority.id)
              Left(SemanticError.InvalidPriority(loc))
            case (_, None) => Right(())
          }
          _ <- (specifier.kind, specifier.queueFull) match {
            case (Ast.SpecPortInstance.AsyncInput, _) => Right(())
            case (_, Some(queueFull)) =>
              val loc = Locations.get(queueFull.id)
              Left(SemanticError.InvalidQueueFull(loc))
            case (_, None) => Right(())
          }
          size <- getArraySize(a, specifier.size)
          priority <- a.getIntValueOpt(specifier.priority)
          ty <- specifier.port match {
            case Some(qid) => a.useDefMap(qid.id) match {
              case symbol @ Symbol.Port(_) => 
                Right(PortInstance.General.Type.DefPort(symbol))
              case symbol => Left(SemanticError.InvalidSymbol(
                symbol.getUnqualifiedName,
                Locations.get(qid.id),
                "not a port symbol"
              ))
            }
            case None => Right(PortInstance.General.Type.Serial)
          }
        }
        yield {
          val kind = specifier.kind match {
            case Ast.SpecPortInstance.AsyncInput =>
              val queueFull = Analysis.getQueueFull(specifier.queueFull.map(_.data))
              PortInstance.General.Kind.AsyncInput(priority, queueFull)
            case Ast.SpecPortInstance.GuardedInput =>
              PortInstance.General.Kind.GuardedInput
            case Ast.SpecPortInstance.Output =>
              PortInstance.General.Kind.Output
            case Ast.SpecPortInstance.SyncInput =>
              PortInstance.General.Kind.SyncInput
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
          val symbol @ Symbol.Port(_) = a.useDefMap(node.id)
          Right(PortInstance.Special(aNode, specifier, symbol))
      }
        
    }

  /** Gets an array size from an AST node */
  private def getArraySize(a: Analysis, sizeOpt: Option[AstNode[Ast.Expr]]):
    Result.Result[Int] = 
      sizeOpt match {
        case Some(size) => a.getArraySize(size.id)
        case None => Right(1)
      }

  /** Checks general async input uses port definitions */
  private def checkGeneralAsyncInput(instance: PortInstance.General) = {
    val loc = Locations.get(instance.aNode._2.id)
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
        checkReturnType(defPort, defLoc)
      }
      case _ => Right(())
    }
  }

}
