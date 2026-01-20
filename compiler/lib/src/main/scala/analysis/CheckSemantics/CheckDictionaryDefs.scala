package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check dictionary definitions */
object CheckDictionaryDefs
  extends Analyzer
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  def checkConstantDef(a: Analysis, s: Symbol.Constant) =
    if !s.isDictionaryDef
    then Right(a)
    else
      val id = s.getNodeId
      val t = a.typeMap(id)
      def result =
        val a1 = a.copy(dictionarySymbolSet = a.dictionarySymbolSet +  s)
        Right(a1)
      def error =
        val loc = Locations.get(id)
        val msg = "dictionary constant must have a numeric, Boolean, string, or enum type"
        Left(SemanticError.InvalidType(loc, msg))
      t match
        case _: Type.String | Type.Boolean | _: Type.Enum => result
        case _ => if t.isNumeric then result else error

  def checkTypeDef(a: Analysis, s: Symbol) =
    if s.isDictionaryDef
    then
      for {
        _ <- a.checkDisplayableType(
          s.getNodeId,
          "dictionary type is not displayable"
        )
      } yield a.copy(dictionarySymbolSet = a.dictionarySymbolSet + s)
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
