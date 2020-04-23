package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A data structure that represents a definition */
sealed trait Symbol

object Symbol {

  final case class AbsType(node: AstNode[Ast.DefAbsType]) extends Symbol
  final case class Array(node: AstNode[Ast.DefArray]) extends Symbol
  final case class Component(node: AstNode[Ast.DefComponent]) extends Symbol
  final case class ComponentInstance(node: AstNode[Ast.DefComponentInstance]) extends Symbol
  final case class Constant(node: AstNode[Ast.DefConstant]) extends Symbol
  final case class Enum(node: AstNode[Ast.DefEnum]) extends Symbol
  final case class EnumConstant(node: AstNode[Ast.DefEnumConstant]) extends Symbol
  final case class Module(name: Name.Qualified) extends Symbol
  final case class Port(node: AstNode[Ast.DefPort]) extends Symbol
  final case class Struct(node: AstNode[Ast.DefStruct]) extends Symbol
  final case class Topology(node: AstNode[Ast.DefTopology]) extends Symbol

}
