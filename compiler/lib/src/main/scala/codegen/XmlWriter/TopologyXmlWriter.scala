package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for topology definitions */
object TopologyXmlWriter extends AstVisitor with LineUtils {

  override def defTopologyAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    // TODO
    Nil
  }

  override def default(s: XmlWriterState) = Nil

  type In = XmlWriterState

  type Out = List[Line]

}
