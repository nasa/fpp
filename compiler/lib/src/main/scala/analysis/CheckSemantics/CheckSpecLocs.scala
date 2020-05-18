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
    val name = node.getData.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val name = node.getData.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = aNode
    val name = node.getData.name
    checkSpecLoc(a, Ast.SpecLoc.Constant, name, node)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val name = node.getData.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val name = node.getData.name
    checkSpecLoc(a, Ast.SpecLoc.Type, name, node)
  }

  private def checkSpecLoc[T](
    a: Analysis,
    kind: Ast.SpecLoc.Kind,
    name: Name.Unqualified,
    node: AstNode[T]
  ): Result = {
    val qualifiedName = Name.Qualified(a.moduleNameList.reverse, name)
    val actualLoc = Locations.get(node.getId)
    a.locationSpecifierMap.get((kind, qualifiedName)) match {
      case Some(specLoc) => {
        val specifierLoc = Locations.get(specLoc.file.getId)
        for {
          specifiedJavaPath <- specifierLoc.relativePath(specLoc.file.getData)
          specifiedPath <- Right(File.Path(specifiedJavaPath).toString)
          actualPath <- Right(actualLoc.file.toString)
          _ <- if (specifiedPath == actualPath) Right(()) 
            else Left(SemanticError.IncorrectSpecLoc(specifierLoc, specifiedPath, actualLoc))
        }
        yield a
      }
      case None => Right(a)
    }
  }

}
