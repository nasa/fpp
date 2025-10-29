package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check location specifiers */
object CheckSpecLocs
  extends Analyzer
  with ModuleAnalyzer
{

  override def defAbsTypeAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Type, Symbol.AbsType(aNode))

  override def defAliasTypeAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Type, Symbol.AliasType(aNode))

  override def defArrayAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Type, Symbol.Array(aNode))

  override def defComponentAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Component, Symbol.Component(aNode))

  override def defComponentInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = checkSpecLoc(a, Ast.SpecLoc.ComponentInstance, Symbol.ComponentInstance(aNode))

  override def defConstantAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Constant, Symbol.Constant(aNode))

  override def defEnumAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Type, Symbol.Enum(aNode))

  override def defInterfaceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefInterface]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Interface, Symbol.Interface(aNode))

  override def defPortAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Port, Symbol.Port(aNode))

  override def defStructAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Type, Symbol.Struct(aNode))

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = checkSpecLoc(a, Ast.SpecLoc.Topology, Symbol.Topology(aNode))

  private def checkSpecLoc(
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    symbol: Symbol
  ) = for {
    a <- checkPath(a, kind, symbol)
    a <- checkDictionarySpecifier(a, kind, symbol)
  } yield a

  private def checkPath[T](
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    symbol: Symbol
  ): Result = {
    val name = symbol.getUnqualifiedName
    val id = symbol.getNodeId
    val qualifiedName = Name.Qualified(a.scopeNameList.reverse, name)
    val actualLoc = Locations.get(id).tuLocation
    a.locationSpecifierMap.get((kind, qualifiedName)) match {
      case Some(node) => {
        val specLoc = node.data
        val specifierLoc = Locations.get(specLoc.file.id)
        val specifiedJavaPath =
          specifierLoc.getRelativePath(specLoc.file.data)
        val specifiedPath = File.Path(specifiedJavaPath).toString
        val actualPath = actualLoc.file.toString
        if (specifiedPath == actualPath) Right(a)
          else Left(
            SemanticError.IncorrectLocationPath(
              specifierLoc,
              specifiedPath,
              actualLoc
            )
          )
      }
      case None => Right(a)
    }
  }

  private def checkDictionarySpecifier(
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    symbol: Symbol
  ) = {
    val name = a.getQualifiedName(symbol)
    val id = symbol.getNodeId
    val defLoc = Locations.get(id)
    a.locationSpecifierMap.get((kind, name)) match {
      case Some(node) => {
        val specifierLoc = Locations.get(node.id)
        if symbol.isDictionaryDef == node.data.isDictionaryDef
        then Right(a)
        else Left(
          SemanticError.IncorrectDictionarySpecifier(specifierLoc, defLoc)
        )
      }
      case None => Right(a)
    }
  }

}
