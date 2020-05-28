package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Write out F Prime XML */
object XmlWriter extends AstStateVisitor {

  /** XML Writer state */
  case class State(
    /** The result of semantic analysis */
    a: Analysis,
    /** The output directory */
    dir: String,
    /** The list of include prefixes */
    prefixes: List[String],
  )

  override def defArrayAnnotatedNode(s: State, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    // TODO
    default(s)
  }

  override def defEnumAnnotatedNode(s: State, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    // TODO
    default(s)
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val data = node.getData
    visitList(s, data.members, matchModuleMember)
  }

  override def defStructAnnotatedNode(s: State, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val fileName = ComputeXmlFiles.getStructFileName(data)
    val lines = StructXmlWriter.defStructAnnotatedNode(s, aNode)
    writeXmlFile(s, fileName, lines)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) = 
    visitList(s, tu.members, matchTuMember)

  private def writeXmlFile(s: State, fileName: String, lines: List[Line]) = {
    val path = java.nio.file.Paths.get(s.dir, fileName)
    val file = File.Path(path)
    for (writer <- file.openWrite()) yield { 
      lines.map(Line.write(writer) _)
      writer.close()
      s
    }
  }

}
