package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._
import scala.language.implicitConversions

/** Writes out the locations of definitions */
object LocateDefsFppWriter extends AstVisitor with LineUtils {

  case class State(
    /** The base directory for constructing location specifiers */
    val baseDir: Option[String],
    /** The list of enclosing module names */
    val moduleNameList: List[Name.Unqualified] = Nil
  )

  override def default(s: State) = Nil

  override def transUnit(s: State, tu: Ast.TransUnit) = 
    tu.members.map(matchModuleMember(s, _)).flatten

  type In = State

  type Out = List[Line]

}
