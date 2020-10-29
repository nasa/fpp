package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance */
sealed trait PortInstance {

  /** Gets the direction of the port instance */
  def getDirection: Option[PortInstance.Direction]

  /** Gets the location of the port definition */
  final def getLoc: Location = Locations.get(getNodeId)

  /** Gets the AST node ID of the port definition */
  def getNodeId: AstNode.Id

  /** Gets the unqualified name of the port instance */
  def getUnqualifiedName: Name.Unqualified

}

final object PortInstance {

  /** A port direction */
  sealed trait Direction
  final object Direction {
    final case object Input extends Direction
    final case object Output extends Direction
  }

  final object General {

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

    /** A general port type */
    sealed trait Type
    final object Type {
      final case class DefPort(symbol: Symbol.Port) extends Type
      final case object Serial extends Type
    }

  }

  /** A general port instance */
  final case class General(
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]],
    specifier: Ast.SpecPortInstance.General,
    kind: General.Kind,
    size: Int,
    ty: General.Type,
  ) extends PortInstance {

    override def getDirection = kind match {
      case General.Kind.Output => Some(Direction.Output)
      case _ => Some(Direction.Input)
    }

    override def getNodeId = aNode._2.getId

    override def getUnqualifiedName = specifier.name

  }

  /** A special port instance */
  final case class Special(
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]],
    specifier: Ast.SpecPortInstance.Special,
    // TODO
  ) extends PortInstance {

    override def getDirection = Some(Direction.Input)

    override def getNodeId = aNode._2.getId

    override def getUnqualifiedName = specifier.name

  }

  final case class Internal(  
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]],
    specifier: Ast.SpecInternalPort,
    priority: Option[Int],
    queueFull: Ast.QueueFull
  ) extends PortInstance {

    override def getDirection = None

    override def getNodeId = aNode._2.getId

    override def getUnqualifiedName = specifier.name

  }

}
