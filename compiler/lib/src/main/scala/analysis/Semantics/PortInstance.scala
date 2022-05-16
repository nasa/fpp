package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance */
sealed trait PortInstance {

  override def toString = getUnqualifiedName.toString

  /** Gets the size of the port array */
  def getArraySize: Int = 1

  /** Gets the direction of the port instance */
  def getDirection: Option[PortInstance.Direction] = None

  /** Gets the type of the port instance */
  def getType: Option[PortInstance.Type] = None

  /** Gets the location of the port instance */
  final def getLoc: Location = Locations.get(getNodeId)

  /** Gets the AST node ID of the port instance */
  def getNodeId: AstNode.Id

  /** Gets the unqualified name of the port instance */
  def getUnqualifiedName: Name.Unqualified

  /** Checks whether a port instance may be connected */
  def requireConnectionAt(
    loc: Location // The location whether the connection is requested
  ): Result.Result[Unit] = Right(())

}

object PortInstance {

  /** A port instance type */
  sealed trait Type
  object Type {
    final case class DefPort(symbol: Symbol.Port) extends Type {
      override def toString = symbol.getUnqualifiedName
    }
    case object Serial extends Type {
      override def toString = "serial"
    }
    /** Show a type option */
    def show(typeOpt: Option[Type]): String = typeOpt match {
      case Some(t) => t.toString
      case None => "none"
    }
    /** Check whether types are compatible */
    def areCompatible(to1: Option[Type], to2: Option[Type]): Boolean = 
      (to1, to2) match {
        case (Some(Type.Serial), _) => true
        case (_, Some(Type.Serial)) => true
        case (Some(t1), Some(t2)) => t1 == t2
        case _ => false
      }
  }

  /** A port direction */
  sealed trait Direction
  object Direction {
    case object Input extends Direction {
      override def toString = "input"
    }
    case object Output extends Direction {
      override def toString = "output"
    }
    /** Show a direction option */
    def show(dirOpt: Option[Direction]): String = dirOpt match {
      case Some(t) => t.toString
      case None => "none"
    }
    /** Check whether directions are compatible */
    def areCompatible(dirs: (Option[Direction], Option[Direction])): Boolean = {
      val (out -> in) = dirs
      (out, in) match {
        case (Some(Output), Some(Input)) => true
        case _ => false
      }
    }
  }

  object General {

    /** A general port kind */
    sealed trait Kind
    object Kind {
      case class AsyncInput(
        priority: Option[Int],
        queueFull: Ast.QueueFull
      ) extends Kind
      case object GuardedInput extends Kind
      case object Output extends Kind
      case object SyncInput extends Kind
    }

  }

  /** A general port instance */
  final case class General(
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]],
    specifier: Ast.SpecPortInstance.General,
    kind: General.Kind,
    size: Int,
    ty: Type,
  ) extends PortInstance {

    override def getDirection = kind match {
      case General.Kind.Output => Some(Direction.Output)
      case _ => Some(Direction.Input)
    }

    override def getArraySize = size

    override def getNodeId = aNode._2.id

    override def getType = Some(ty)

    override def getUnqualifiedName = specifier.name

  }

  /** A special port instance */
  final case class Special(
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]],
    specifier: Ast.SpecPortInstance.Special,
    symbol: Symbol.Port
  ) extends PortInstance {

    override def getDirection = {
      import Ast.SpecPortInstance._
      val direction = specifier.kind match {
        case CommandRecv => Direction.Input
        case _ => Direction.Output
      }
      Some(direction)
    }

    override def getNodeId = aNode._2.id

    override def getType = Some(Type.DefPort(symbol))

    override def getUnqualifiedName = specifier.name

  }

  final case class Internal(  
    aNode: Ast.Annotated[AstNode[Ast.SpecInternalPort]],
    priority: Option[Int],
    queueFull: Ast.QueueFull
  ) extends PortInstance {

    override def getNodeId = aNode._2.id

    override def getUnqualifiedName = aNode._2.data.name

    override def requireConnectionAt(loc: Location) = Left(
      SemanticError.InvalidPortKind(
        loc,
        "cannot connect to internal port",
        this.getLoc
      ))

  }

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
                Right(PortInstance.Type.DefPort(symbol))
              case symbol => Left(SemanticError.InvalidSymbol(
                symbol.getUnqualifiedName,
                Locations.get(qid.id),
                "not a port symbol",
                symbol.getLoc
              ))
            }
            case None => Right(PortInstance.Type.Serial)
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
        case Some(size) => a.getUnboundedArraySize(size.id)
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
        PortInstance.Type.DefPort(symbol)
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
