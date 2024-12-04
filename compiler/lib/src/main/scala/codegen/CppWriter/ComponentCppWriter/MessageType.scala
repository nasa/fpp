package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Message type for message send logic */
sealed trait MessageType
object MessageType {
  case object Command extends MessageType {
    override def toString = "command"
  }
  case object Port extends MessageType {
    override def toString = "async input port"
  }
  case object StateMachine extends MessageType {
    override def toString = "state machine"
  }
}

