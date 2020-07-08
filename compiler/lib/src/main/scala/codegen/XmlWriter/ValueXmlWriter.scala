ackage fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP value as XML */
object ValueXmlWriter {

  private object Visitor extends ValueVisitor {

    type In = XmlWriterState

    type Out = String
  }
}