package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Write out F Prime XML for structs */
object StructXmlWriter extends AstVisitor {

  override def default(s: XmlWriter.State) = Nil

  override def transUnit(s: XmlWriter.State, tu: Ast.TransUnit) = tu.members.map(matchTuMember(s, _)).flatten

  type In = XmlWriter.State

  type Out = List[Line]

}
