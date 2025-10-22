package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check dictionary definitions */
object CheckDictionaryDefs
  extends Analyzer
  with ModuleAnalyzer
{

  def checkConstantDictionaryDefs(a: Analysis, s: Symbol.Constant) = {
    val Symbol.Constant(aNode) = s
    val id = aNode._2.id
    val loc = Locations.get(id)
    if(aNode._2.data.isDictionaryDef) then
      a.typeMap(id) match
        case Type.Integer | _: Type.Float | Type.Boolean | _: Type.String =>
          Right(a.copy(dictionarySymbolSet = a.dictionarySymbolSet +  s))
        case Type.Enum(enumNode, _, _) =>
          val enumSymbol = Symbol.Enum(enumNode)
          Right(a.copy(dictionarySymbolSet = a.dictionarySymbolSet + s + enumSymbol))
        case _ =>
          Left(
            SemanticError.InvalidType(
              loc, s"constant dictionary defintion must be a primitive type, string, or enum."
            )
          )
    else
      Right(a)
  }

  def checkTypeDictionaryDefs(a: Analysis, id: Int, s: Symbol, isDictionaryDef: Boolean) = {
    val t = a.typeMap(id)
    val loc = Locations.get(id)
    (isDictionaryDef, t.isDisplayable) match
      case (true, true) =>
        val usedSymbols = s match
          case Symbol.Array(aNode) =>
            val Right(updatedAnalysis) = UsedSymbols.defArrayAnnotatedNode(a, aNode)
            UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
          case Symbol.AliasType(aNode) =>
            val Right(updatedAnalysis) = UsedSymbols.defAliasTypeAnnotatedNode(a, aNode)
            UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
          case Symbol.Struct(aNode) =>
            val Right(updatedAnalysis) = UsedSymbols.defStructAnnotatedNode(a, aNode)
            UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
          case _ => Set()
        Right(a.copy(dictionarySymbolSet = (a.dictionarySymbolSet ++ usedSymbols) + s))
      case (true, false) => 
        Left(SemanticError.InvalidType(loc, s"type dictionary defintion must be displayable"))
      case _ => Right(a)
  }

  override def defAliasTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]) = {
    val s = Symbol.AliasType(aNode)
    val name = a.getQualifiedName(s)
    val id = aNode._2.id
    checkTypeDictionaryDefs(a, id, s, aNode._2.data.isDictionaryDef)
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val s = Symbol.Array(aNode)
    val name = a.getQualifiedName(s)
    val id = aNode._2.id
    checkTypeDictionaryDefs(a, id, s, aNode._2.data.isDictionaryDef)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val s = Symbol.Enum(aNode)
    val name = a.getQualifiedName(s)
    val id = aNode._2.id
    checkTypeDictionaryDefs(a, id, s, aNode._2.data.isDictionaryDef)
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val s = Symbol.Struct(aNode)
    val name = a.getQualifiedName(s)
    val id = aNode._2.id
    checkTypeDictionaryDefs(a, id, s, aNode._2.data.isDictionaryDef)
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val s = Symbol.Constant(aNode)
    val name = a.getQualifiedName(s)
    val id = aNode._2.id
    checkConstantDictionaryDefs(a, s)
  }

}
