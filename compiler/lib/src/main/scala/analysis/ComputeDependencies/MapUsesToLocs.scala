package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Map uses to locations 
 *  Prerequisites: BuildLocSpecMap */
object MapUsesToLocs extends UseAnalyzer {

  override def componentInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"component instance use ${use}")
    default(a)
  }

  override def exprUse(a: Analysis, node: AstNode[Ast.Expr], use: Name.Qualified) = {
    System.out.println(s"expr use ${use}")
    default(a)
  }

  override def portInstanceUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"port instance use ${use}")
    default(a)
  }

  override def topologyUse(a: Analysis, node: AstNode[Ast.QualIdent], use: Name.Qualified) = {
    System.out.println(s"topology use ${use}")
    default(a)
  }

  override def typeNameUse(a: Analysis, node: AstNode[Ast.TypeName], use: Name.Qualified) = {
    System.out.println(s"type name use ${use}")
    default(a)
  }

}
