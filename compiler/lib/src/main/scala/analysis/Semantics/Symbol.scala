package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A data structure that represents a definition */
sealed trait Symbol {

  /** Get the location, if it exists */
  final def getLocOpt: Option[Location] = getNodeIdOpt match {
    case Some(id) => Locations.getOpt(id)
    case None => None
  }

  /** Get the location. Throw an InternalError if the location does not exist. */
  final def getLoc: Location = getLocOpt match {
    case Some(loc) => loc
    case None => throw new InternalError(s"unknown location for symbol ${this}")
  }

  /** Get the AST node ID, if it exists */
  def getNodeIdOpt: Option[AstNode.Id] = None

  /** Get the AST node ID. Throw an InternalError if the ID does not exist. */
  final def getNodeId: AstNode.Id = getNodeIdOpt match {
    case Some(id) => id
    case None => throw new InternalError(s"could not get node ID for symbol ${this}")
  }

  /** Get the unqualified name associated with the definition */
  def getUnqualifiedName: Name.Unqualified

}

object Symbol {

  final case class AbsType(node: Ast.Annotated[AstNode[Ast.DefAbsType]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class Array(node: Ast.Annotated[AstNode[Ast.DefArray]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class Component(node: Ast.Annotated[AstNode[Ast.DefComponent]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class ComponentInstance(node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class Constant(node: Ast.Annotated[AstNode[Ast.DefConstant]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class Enum(node: Ast.Annotated[AstNode[Ast.DefEnum]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class EnumConstant(node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class Module(name: Name.Qualified) extends Symbol {
    override def getUnqualifiedName = name.base
  }
  final case class Port(node: Ast.Annotated[AstNode[Ast.DefPort]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class Struct(node: Ast.Annotated[AstNode[Ast.DefStruct]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }
  final case class Topology(node: Ast.Annotated[AstNode[Ast.DefTopology]]) extends Symbol {
    override def getNodeIdOpt = Some(node._2.getId)
    override def getUnqualifiedName = node._2.getData.name
  }

}
