package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check location specifiers */
object CheckSpecLocs
  extends Analyzer
  with ModuleAnalyzer
{

  override def defAbsTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) =
    checkSpecLoc(a, Ast.SpecLoc.Type, Symbol.AbsType(aNode), false)

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    checkSpecLoc(a, Ast.SpecLoc.Type, Symbol.Array(aNode), node.data.isDictionaryDef)
  }

  override def defAliasTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    for {
      a <- checkSpecLocPath(a, Ast.SpecLoc.Type, Symbol.AliasType(aNode))
      a <- checkSpecLocDictionary(a, Ast.SpecLoc.Type, Symbol.AliasType(aNode), node.data.isDictionaryDef)
    } yield a
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    val qualName = a.getQualifiedName(Symbol.Constant(aNode))
    val id = aNode._2.id
    for {
      a <- checkSpecLocPath(a, Ast.SpecLoc.Constant, Symbol.Constant(aNode))
      a <- checkDictionarySpecLoc(a, Ast.SpecLoc.Constant, qualName, id, node.data.isDictionaryDef)
    } yield a
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    val qualName = a.getQualifiedName(Symbol.Enum(aNode))
    val id = aNode._2.id
    for {
      a <- checkSpecLocPath(a, Ast.SpecLoc.Type, Symbol.Enum(aNode))
      a <- checkDictionarySpecLoc(a, Ast.SpecLoc.Type, qualName, id, node.data.isDictionaryDef)
    } yield a
  }

  override def defPortAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    checkSpecLocPath(a, Ast.SpecLoc.Port, Symbol.Port(aNode))
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    val qualName = a.getQualifiedName(Symbol.Struct(aNode))
    val id = aNode._2.id
    for {
      a <- checkSpecLocPath(a, Ast.SpecLoc.Type, Symbol.Struct(aNode))
      a <- checkDictionarySpecLoc(a, Ast.SpecLoc.Type, qualName, id, node.data.isDictionaryDef)
    } yield a
  }

  private def checkDictionarySpecLoc(
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    name: Name.Qualified,
    id: AstNode.Id,
    isDictionary: Boolean
  ) = {
    val actualLoc = Locations.get(id)
    a.locationSpecifierMap.get((kind, name)) match {
      case Some(node) => {
        val specifierLoc = Locations.get(node.id)
        if isDictionary == node.data.isDictionaryDef
        then Right(a)
        else Left(
          SemanticError.IncorrectDictionarySpecLoc(specifierLoc, actualLoc)
        )
      }
      case None => Right(a)
    }
  }

  private def checkSpecLoc(
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    symbol: Symbol,
    isDictionary: Boolean
  ) = for {
    a <- checkSpecLocPath(a, Ast.SpecLoc.Type, symbol)
    a <- checkSpecLocDictionary(a, Ast.SpecLoc.Type, symbol, isDictionary)
  } yield a

  private def checkSpecLocDictionary(
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    symbol: Symbol,
    isDictionary: Boolean
  ) = {
    val name = a.getQualifiedName(symbol)
    val id = symbol.getNodeId
    val actualLoc = Locations.get(id)
    a.locationSpecifierMap.get((kind, name)) match {
      case Some(node) => {
        val specifierLoc = Locations.get(node.id)
        if isDictionary == node.data.isDictionaryDef
        then Right(a)
        else Left(
          SemanticError.IncorrectDictionarySpecLoc(specifierLoc, actualLoc)
        )
      }
      case None => Right(a)
    }
  }

  private def checkSpecLocPath[T](
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
        val specifiedJavaPath = specifierLoc.getRelativePath(specLoc.file.data)
        val specifiedPath = File.Path(specifiedJavaPath).toString
        val actualPath = actualLoc.file.toString
        if (specifiedPath == actualPath) Right(a)
          else Left(SemanticError.IncorrectSpecLoc(specifierLoc, specifiedPath, actualLoc))
      }
      case None => Right(a)
    }
  }

}
