package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** ====================================================================== 
 *  Compute the names of the XML files to generate
 *  ====================================================================== 
 *  Check for duplicates that would cause a name collision. 
 *  For example, module M { struct S { ... } } and struct S { ... } will both
 *  generate SSerializableAi.xml, so we can't generate XML files for both
 *  in the same place. 
 *  ======================================================================*/
object ComputeXmlFiles extends AstStateVisitor {

  type State = Map[String, Location]

  /** Get the generated XML file name for an array definition */
  def getArrayFileName(defArray: Ast.DefArray) = defArray.name ++ "ArrayAi.xml"

  /** Get the generated XML file name for an enum definition */
  def getEnumFileName(defEnum: Ast.DefEnum) = defEnum.name ++ "EnumAi.xml"

  /** Get the generated XML file name for a port definition */
  def getPortFileName(defPort: Ast.DefPort) = defPort.name ++ "PortAi.xml"

  /** Get the generated XML file name for a struct definition */
  def getStructFileName(defStruct: Ast.DefStruct) = defStruct.name ++ "SerializableAi.xml"

  override def defArrayAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val fileName = getArrayFileName(data)
    addMapping(s, fileName, loc)
  }

  override def defEnumAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val fileName = getEnumFileName(data)
    addMapping(s, fileName, loc)
  }

  override def defModuleAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val data = node1.data
    visitList(s, data.members, matchModuleMember)
  }

  override def defPortAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val fileName = getPortFileName(data)
    addMapping(s, fileName, loc)
  }

  override def defStructAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
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
