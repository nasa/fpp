package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for array definitions */
object ArrayXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defArrayAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
      Nil
  }

  type In = XmlWriterState

  type Out = List[Line]

}
