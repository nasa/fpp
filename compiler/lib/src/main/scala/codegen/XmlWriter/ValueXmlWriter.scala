package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis._
import fpp.compiler.util._

/** Write an FPP value as XML */
object ValueXmlWriter {

  /** Write a value */
  def write(s: XmlWriterState, v: Value): String = {
    ValueCppWriter.write(s.cppWriterState, v)
  }

}
