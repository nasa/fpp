package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Map uses to locations */
object MapUsesToLocs extends UseAnalyzer {

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"component instance use ${use}")
    analyzeUse(a, Ast.SpecLoc.ComponentInstance, use)
  }

  override def componentUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"component use ${use}")
    analyzeUse(a, Ast.SpecLoc.Component, use)
  }

  override def constantUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    System.out.println(s"constant use ${use}")
    analyzeUse(a, Ast.SpecLoc.Constant, use)
  }

  override def portInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"port instance use ${use}")
    val componentInstanceUse = Name.Qualified.fromIdentList(use.qualifier)
    analyzeUse(a, Ast.SpecLoc.ComponentInstance, componentInstanceUse)
  }

  override def portUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"port use ${use}")
    analyzeUse(a, Ast.SpecLoc.Port, use)
  }

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"topology use ${use}")
    analyzeUse(a, Ast.SpecLoc.Topology, use)
  }

  override def typeUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    System.out.println(s"type name use ${use}")
    analyzeUse(a, Ast.SpecLoc.Type, use)
  }

  private def analyzeUse(a: Analysis, kind: Ast.SpecLoc.Kind, use: Name.Qualified): Result = {
    default(a)
  }

}
