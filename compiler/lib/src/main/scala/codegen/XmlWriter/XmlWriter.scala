package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML */
object XmlWriter extends AstStateVisitor {

  type State = XmlWriterState

  override def defArrayAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    // TODO
    default(s)
  }

  override def defEnumAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    // TODO
    default(s)
  }

  override def defModuleAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val data = node.getData
    val a = s.a.copy(moduleNameList = data.name :: s.a.moduleNameList)
    val s1 = s.copy(a = a)
    visitList(s1, data.members, matchModuleMember)
    Right(s)
  }

  override def defStructAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val loc = Locations.get(node.getId)
    val data = node.getData
    val fileName = ComputeXmlFiles.getStructFileName(data)
    val lines = StructXmlWriter.defStructAnnotatedNode(s, aNode)
    for {
      _ <- if (data.members.length == 0) Left(CodeGenError.EmptyStruct(loc)) else Right(())
      s <- writeXmlFile(s, fileName, lines)
    } yield s
  }

  override def transUnit(s: XmlWriterState, tu: Ast.TransUnit) = 
    visitList(s, tu.members, matchTuMember)

  private def writeXmlFile(s: XmlWriterState, fileName: String, lines: List[Line]) = {
    val path = java.nio.file.Paths.get(s.dir, fileName)
    val file = File.Path(path)
    for (writer <- file.openWrite()) yield { 
      lines.map(Line.write(writer) _)
      writer.close()
      s
    }
  }

}
