package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A data structure that represents a definition */
sealed trait Symbol {

  /** Gets the location of the symbol */
  final def getLoc: Location = Locations.get(getNodeId)

  /** Gets the AST node ID of the symbol */
  def getNodeId: AstNode.Id

  /** Gets the unqualified name of the symbol */
  def getUnqualifiedName: Name.Unqualified

}

object Symbol {

  final case class AbsType(node: Ast.Annotated[AstNode[Ast.DefAbsType]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Array(node: Ast.Annotated[AstNode[Ast.DefArray]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Component(node: Ast.Annotated[AstNode[Ast.DefComponent]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class ComponentInstance(node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Constant(node: Ast.Annotated[AstNode[Ast.DefConstant]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Enum(node: Ast.Annotated[AstNode[Ast.DefEnum]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class EnumConstant(node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Module(node: Ast.Annotated[AstNode[Ast.DefModule]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Port(node: Ast.Annotated[AstNode[Ast.DefPort]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class StateMachine(node: Ast.Annotated[AstNode[Ast.DefStateMachine]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Struct(node: Ast.Annotated[AstNode[Ast.DefStruct]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Topology(node: Ast.Annotated[AstNode[Ast.DefTopology]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }

}
