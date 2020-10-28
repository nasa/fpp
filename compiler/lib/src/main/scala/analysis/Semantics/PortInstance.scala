package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance */
sealed trait PortInstance {
  def direction: PortInstance.Direction
}

object PortInstance {

  /** A port direction */
  sealed trait Direction
  object Direction {
    case object Input extends Direction
    case object Output extends Direction
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

    /** A general port type */
    sealed trait Type
    object Type {
      case class DefPort(symbol: Symbol.Port) extends Type
      case object Serial extends Type
    }

  }

  /** A general port instance */
  case class General(
    kind: General.Kind,
    size: Int,
    ty: General.Type,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]],
    general: Ast.SpecPortInstance.General
  ) extends PortInstance {

    val name = general.name

    override def direction = kind match {
      case General.Kind.Output => Direction.Output
      case _ => Direction.Input
    }

  }

  /** A special port instance */
  case class Special(
    // TODO
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]],
    instance: Ast.SpecPortInstance.Special
  ) extends PortInstance {

    val name = instance.name

    override def direction = Direction.Input

  }

}
