package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** A data structure that represents a definition */
sealed trait Symbol extends SymbolInterface {
  def isDictionaryDef = false
}

/** A type symbol */
sealed trait TypeSymbol extends Symbol

/** A port interface instance symbol */
sealed trait InterfaceInstanceSymbol extends Symbol

/** A template argument symbol */
sealed trait TemplateArgSymbol extends Symbol

object Symbol {

  final case class AbsType(node: Ast.Annotated[AstNode[Ast.DefAbsType]]) extends TypeSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class AliasType(node: Ast.Annotated[AstNode[Ast.DefAliasType]]) extends TypeSymbol {
    override def isDictionaryDef = node._2.data.isDictionaryDef
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Array(node: Ast.Annotated[AstNode[Ast.DefArray]]) extends TypeSymbol {
    override def isDictionaryDef = node._2.data.isDictionaryDef
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Component(node: Ast.Annotated[AstNode[Ast.DefComponent]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class ComponentInstance(node: Ast.Annotated[AstNode[Ast.DefComponentInstance]]) extends InterfaceInstanceSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Constant(node: Ast.Annotated[AstNode[Ast.DefConstant]]) extends Symbol {
    override def isDictionaryDef = node._2.data.isDictionaryDef
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Enum(node: Ast.Annotated[AstNode[Ast.DefEnum]]) extends TypeSymbol {
    override def isDictionaryDef = node._2.data.isDictionaryDef
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class EnumConstant(node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Interface(node: Ast.Annotated[AstNode[Ast.DefInterface]]) extends Symbol {
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
  final case class Struct(node: Ast.Annotated[AstNode[Ast.DefStruct]]) extends TypeSymbol {
    override def isDictionaryDef = node._2.data.isDictionaryDef
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Topology(node: Ast.Annotated[AstNode[Ast.DefTopology]]) extends InterfaceInstanceSymbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class Template(node: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]) extends Symbol {
    override def getNodeId = node._2.id
    override def getUnqualifiedName = node._2.data.name
  }
  final case class TemplateConstantArg(
    paramDef: Ast.TemplateParam.Constant,
    value: AstNode[Ast.Expr]
  ) extends TemplateArgSymbol {
    override def getUnqualifiedName = paramDef.name
    override def getNodeId = value.id
  }
  final case class TemplateTypeArg(
    paramDef: Ast.TemplateParam.Type,
    value: AstNode[Ast.TypeName]
  ) extends TemplateArgSymbol {
    override def getUnqualifiedName = paramDef.name
    override def getNodeId = value.id
  }
  final case class TemplateInterfaceArg(
    paramDef: Ast.TemplateParam.Interface,
    value: AstNode[Ast.QualIdent]
  ) extends TemplateArgSymbol, InterfaceInstanceSymbol {
    override def getUnqualifiedName = paramDef.name
    override def getNodeId = value.id
  }

}
