package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Compute the list of XML files */
object ComputeXmlFiles extends AstStateVisitor {

  type State = Map[String, Location]

  def getArrayFileName(defArray: Ast.DefArray) = defArray.name ++ "ArrayAi.xml"

  def getEnumFileName(defEnum: Ast.DefEnum) = defEnum.name ++ "EnumAi.xml"

  def getStructFileName(defStruct: Ast.DefStruct) = defStruct.name ++ "SerializableAi.xml"

  override def defArrayAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val loc = Locations.get(node1.getId)
    val fileName = getArrayFileName(data)
    addMapping(s, fileName, loc)
  }

  override def defEnumAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val loc = Locations.get(node1.getId)
    val fileName = getEnumFileName(data)
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

  override def defStructAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    val loc = Locations.get(node1.getId)
    val fileName = getStructFileName(data)
    addMapping(s, fileName, loc)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) =
    visitList(s, tu.members, matchTuMember)

  private def addMapping(s: State, fileName: String, loc: Location) = s.get(fileName) match {
    case Some(prevLoc) => Left(CodeGenError.DuplicateXmlFile(fileName, loc, prevLoc))
    case None => Right(s + (fileName -> loc))
  }

}
