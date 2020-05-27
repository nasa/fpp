package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Write out F Prime XML */
object XmlWriter extends AstVisitor {

  object PathNameAnalyzer extends AstStateVisitor {

    type State = Map[String, Location]

    override def defArrayAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
      val (_, node1, _) = node
      val data = node1.getData
      val loc = Locations.get(node1.getId)
      val fileName = data.name ++ "ArrayAi.xml"
      addMapping(s, fileName, loc)
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

    private def addMapping(s: State, fileName: String, loc: Location) = s.get(fileName) match {
      case Some(prevLoc) => Left(CodeGenError.DuplicateXmlFile(fileName, loc, prevLoc))
      case None => Right(s + (fileName -> loc))
    }

  }

  override def default(s: State) = ()

  override def transUnit(s: State, tu: Ast.TransUnit) = tu.members.map(matchTuMember(s, _))

  case class State(
    /** The result of semantic analysis */
    a: Analysis,
    /** The output directory */
    dir: String
  )

  type In = State

  type Out = Unit

}
