package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check location specifiers */
object CheckSpecLocs
  extends Analyzer
  with ModuleAnalyzer 
{

  override def defAbsTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    checkSpecLoc(a, Ast.SpecLoc.Constant, name, node)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  override def defPortAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    checkSpecLoc(a, Ast.SpecLoc.Port, name, node)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val name = node.data.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  private def checkSpecLoc[T](
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    name: Name.Unqualified,
    node: AstNode[T]
  ): Result = {
    val qualifiedName = Name.Qualified(a.scopeNameList.reverse, name)
    val actualLoc = Locations.get(node.id).tuLocation
    a.locationSpecifierMap.get((kind, qualifiedName)) match {
      case Some(specLoc) => {
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
