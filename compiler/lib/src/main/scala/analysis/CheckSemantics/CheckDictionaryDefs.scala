package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check dictionary definitions */
object CheckDictionaryDefs
  extends Analyzer
  with ModuleAnalyzer
{

    def checkConstantDictionaryDefs(a: Analysis, id: Int, s: Symbol, isDictionaryDef: Boolean) = {
      val loc = Locations.get(id)
      if(isDictionaryDef) then
        a.typeMap(id) match
          case Type.Integer | _: Type.Float | Type.Boolean | _: Type.String =>
            Right(a.copy(dictionarySymbolSet = a.dictionarySymbolSet +  s))
          case Type.Enum(enumNode, _, _) =>
            val enumSymbol = Symbol.Enum(enumNode)
            Right(a.copy(dictionarySymbolSet = a.dictionarySymbolSet + s + enumSymbol))
          case _ =>
            Left(SemanticError.InvalidType(
              loc, s"constant dictionary defintion must be a primitive type, string, or enum.")
            )
      else
        Right(a)
    }

    def checkTypeDictionaryDefs(a: Analysis, id: Int, s: Symbol, isDictionaryDef: Boolean) = {
      val t = a.typeMap(id)
      val loc = Locations.get(id)
      if(isDictionaryDef) then
        if(t.isDisplayable) then
          Right(a.copy(dictionarySymbolSet = a.dictionarySymbolSet +  s))
        else
          Left(SemanticError.InvalidType(loc, s"type dictionary defintion must be displayable"))
      else
        Right(a)
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
      checkConstantDictionaryDefs(a, id, s, aNode._2.data.isDictionaryDef)
    }

}
