package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check for use-def cycles */
object CheckUseDefCycles extends UseAnalyzer {

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) =
    visitUse(a, node, use)

  override def defArrayAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val symbol = Symbol.Array(node)
    visitDefPost(a, symbol, node, super.defArrayAnnotatedNode)
  }

  override def defConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val symbol = Symbol.Constant(node)
    visitDefPost(a, symbol, node, super.defConstantAnnotatedNode)
  }

  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val symbol = Symbol.Enum(node)
    visitDefPost(a, symbol, node, super.defEnumAnnotatedNode)
  }

  override def defEnumConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]) = {
    val symbol = Symbol.EnumConstant(node)
    visitDefPost(a, symbol, node, super.defEnumConstantAnnotatedNode)
  }

  override def defStructAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val symbol = Symbol.Struct(node)
    visitDefPost(a, symbol, node, super.defStructAnnotatedNode)
  }

  override def defTopologyAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefTopology]]) = {
    val symbol = Symbol.Topology(node)
    visitDefPost(a, symbol, node, super.defTopologyAnnotatedNode)
  }

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) =
    visitUse(a, node, use)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) =
    visitUse(a, node, use)

  private def error(a: Analysis, symbol: Symbol): Result = {
    def msg: String = {
      def printMatching(m: UseDefMatching) = "\n  " ++ m.toString
      "encountered a use-def cycle:" ++ a.useDefMatchingList.reverse.map(printMatching).flatten
    }
    val loc = symbol.getLoc
    Left(SemanticError.UseDefCycle(loc, msg))
  }

  private def visitDefPre(a: Analysis, symbol: Symbol): Result = {
    symbol match {
      case Symbol.Array(node) => defArrayAnnotatedNode(a, node)
      case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
      case Symbol.Enum(node) => defEnumAnnotatedNode(a, node)
      case Symbol.EnumConstant(node) => defEnumConstantAnnotatedNode(a, node)
      case Symbol.Struct(node) => defStructAnnotatedNode(a, node)
      case Symbol.Topology(node) => defTopologyAnnotatedNode(a, node)
      case _ => Right(a)
    }
  }

  private def visitDefPost[T](
    a: Analysis,
    symbol: Symbol,
    node: T,
    visitor: (Analysis, T) => Result
  ): Result = {
    if (a.useDefSymbolSet.contains(symbol)) error(a, symbol)
    else if (!a.visitedSymbolSet.contains(symbol)) {
      val a1 = a.copy(useDefSymbolSet = a.useDefSymbolSet + symbol)
      for (_ <- visitor(a1, node))
      yield a.copy(visitedSymbolSet = a.visitedSymbolSet + symbol)
    }
    else Right(a)
  }

  private def visitUse[T](a: Analysis, node: AstNode[T], use: Name.Qualified): Result = {
    val symbol = a.useDefMap(node.id)
    val m = UseDefMatching(node.id, use, symbol)
    val a1 = a.copy(useDefMatchingList = m :: a.useDefMatchingList)
    visitDefPre(a1, symbol)
  }

}
