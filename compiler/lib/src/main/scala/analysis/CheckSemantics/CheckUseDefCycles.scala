package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check for use-def cycles */
object CheckUseDefCycles extends UseAnalyzer {

  private def visitDefinition(a: Analysis, symbol: Symbol): Result = {
    symbol match {
      case Symbol.Constant(node) => defConstantAnnotatedNode(a, node)
      case _ => Right(a)
    }
  }

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    val symbol = getDefForUse(a, node)
    val m = UseDefMatching(node.getId, use, symbol)
    val a1 = a.copy(useDefMatchingList = m :: a.useDefMatchingList)
    visitDefinition(a1, symbol)
  }

  override def defConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val symbol = Symbol.Constant(node)
    val (_, node1, _) = node
    val data = node1.getData
    if (a.useDefSymbolSet.contains(symbol)) {
      val loc = Locations.get(node1.getId)
      val msg = errorMessage(a)
      Left(SemanticError.UseDefCycle(loc, msg))
    }
    else if (!a.visitedSymbolSet.contains(symbol)) {
      val a1 = a.copy(useDefSymbolSet = a.useDefSymbolSet + symbol)
      for (_ <- exprNode(a1, data.value))
        yield a.copy(visitedSymbolSet = a.visitedSymbolSet + symbol)
    }
    else Right(a)
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    // TODO
    default(a)
  }

  private def errorMessage(a: Analysis): String = {
    // TODO
    "encountered a use-def cycle"
  }

  private def getDefForUse[T](a: Analysis, use: AstNode[T]): Symbol =
    a.useDefMap.get(use.getId) match {
      case Some(symbol) => symbol
      case None => throw InternalError(s"could not find definition for use ${use}")
    }

}
