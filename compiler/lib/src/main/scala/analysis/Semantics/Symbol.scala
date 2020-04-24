package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A data structure that represents a definition */
sealed trait Symbol {

  def getLocOpt: Option[Location] = None

  final def getLoc: Location = getLocOpt match {
    case Some(loc) => loc
    case None => throw new InternalError(s"unknown location for symbol ${this}")
  }

}

object Symbol {

  final case class AbsType(node: AstNode[Ast.DefAbsType]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class Array(node: AstNode[Ast.DefArray]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class Component(node: AstNode[Ast.DefComponent]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class ComponentInstance(node: AstNode[Ast.DefComponentInstance]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class Constant(node: AstNode[Ast.DefConstant]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class Enum(node: AstNode[Ast.DefEnum]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class EnumConstant(node: AstNode[Ast.DefEnumConstant]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class Module(name: Name.Qualified) extends Symbol
  final case class Port(node: AstNode[Ast.DefPort]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class Struct(node: AstNode[Ast.DefStruct]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }
  final case class Topology(node: AstNode[Ast.DefTopology]) extends Symbol {
    override def getLocOpt = Locations.getOpt(node.getId)
  }

}
