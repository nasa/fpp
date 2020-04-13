package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Map uses to locations */
object MapUsesToLocs extends UseAnalyzer {

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"component instance use ${use}")
    analyzeUse(a, use)
  }

  override def exprUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    System.out.println(s"expr use ${use}")
    analyzeUse(a, use)
  }

  override def portInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"port instance use ${use}")
    analyzeUse(a, use)
  }

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"topology use ${use}")
    analyzeUse(a, use)
  }

  override def typeNameUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    System.out.println(s"type name use ${use}")
    analyzeUse(a, use)
  }

  private def analyzeUse(a: Analysis, use: Name.Qualified): Result = {
    default(a)
  }

}
