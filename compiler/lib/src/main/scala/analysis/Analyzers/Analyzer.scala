package fpp.compiler.analysis

import fpp.compiler.ast._

/** A generic analysis visitor */
trait Analyzer extends AstStateVisitor {

  type State = Analysis

}
