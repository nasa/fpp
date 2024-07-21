package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A symbol that represents a definition in a state machine definition */
sealed trait StateMachineSymbol extends SymbolInterface

object StateMachineSymbol {

  final case class Action(node: Ast.Annotated[AstNode[Ast.DefAction]]) extends StateMachineSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }

  final case class Guard(node: Ast.Annotated[AstNode[Ast.DefGuard]]) extends StateMachineSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }

  final case class Junction(node: Ast.Annotated[AstNode[Ast.DefJunction]]) extends StateMachineSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }

  final case class Signal(node: Ast.Annotated[AstNode[Ast.DefSignal]]) extends StateMachineSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }

  final case class State(node: Ast.Annotated[AstNode[Ast.DefState]]) extends StateMachineSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }

}
