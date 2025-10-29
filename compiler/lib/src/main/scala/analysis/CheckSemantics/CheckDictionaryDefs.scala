package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check dictionary definitions */
object CheckDictionaryDefs
  extends Analyzer
  with ModuleAnalyzer
{

  def checkConstantDef(a: Analysis, s: Symbol.Constant) =
    if s.isDictionaryDef
    then
      val id = s.getNodeId
      a.typeMap(id) match {
        case Type.Integer | _: Type.Float | Type.Boolean | _: Type.String =>
        Right(a.copy(dictionarySymbolSet = a.dictionarySymbolSet +  s))
        case Type.Enum(enumNode, _, _) =>
          val enumSymbol = Symbol.Enum(enumNode)
          Right(a.copy(dictionarySymbolSet = a.dictionarySymbolSet + s + enumSymbol))
        case _ =>
          val loc = Locations.get(id)
          Left(
            SemanticError.InvalidType(
              loc, s"dictionary constant defintion must have a primitive, string, or enum type"
            )
          )
      }
    else Right(a)

  def checkTypeDef(a: Analysis, s: Symbol) =
    if (s.isDictionaryDef)
    then for {
      _ <- a.checkDisplayableType(
        s.getNodeId,
        "dictionary type definition must be displayable"
      )
    } yield {
      val ss = UsedSymbols.resolveUses(a, Set(s))
      a.copy(dictionarySymbolSet = a.dictionarySymbolSet ++ ss)
    }
    else Right(a)

  override def defAliasTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]) =
    checkTypeDef(a, Symbol.AliasType(aNode))

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) =
    checkTypeDef(a, Symbol.Array(aNode))

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) =
    checkTypeDef(a, Symbol.Enum(aNode))

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) =
    checkTypeDef(a, Symbol.Struct(aNode))

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) =
    checkConstantDef(a, Symbol.Constant(aNode))

}
