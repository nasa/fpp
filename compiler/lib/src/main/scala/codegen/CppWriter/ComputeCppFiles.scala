package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** ====================================================================== 
 *  Computes the names of the C++ files to generate
 *  ====================================================================== 
 *  Checks for duplicates that would cause a name collision. 
 *  ======================================================================*/
object ComputeCppFiles extends AstStateVisitor {

  type State = Map[String, Option[Location]]

  /** Gets the generated C++ file name for a constant definition */
  def getConstantName(defConstant: Ast.DefConstant) = "Constants.hpp"

  override def defConstantAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val mapping = (getConstantName(data), None)
    addMapping(s, mapping)
  }

  override def defModuleAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val data = node1.getData
    visitList(s, data.members, matchModuleMember)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) =
    visitList(s, tu.members, matchTuMember)

  private def addMapping(s: State, mapping: (String, Option[Location])) = {
    val (fileName, locOpt) = mapping
    (s.get(fileName), locOpt) match {
      case (Some(Some(prevLoc)), Some(loc)) => Left(CodeGenError.DuplicateCppFile(fileName, loc, prevLoc))
      case _ => Right(s + mapping)
    }
  }

}
