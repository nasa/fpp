package fpp.compiler.analysis

import fpp.compiler.ast._

case class FrameworkDefinitions(
  constantMap: Map[String, Symbol.Constant] = Map(),
  typeMap: Map[String, TypeSymbol] = Map()
) {

  def addConstant(name: String, sym: Symbol.Constant) =
    this.copy(constantMap = this.constantMap + (name -> sym))

  def addType(name: String, sym: TypeSymbol) =
    this.copy(typeMap = this.typeMap + (name -> sym))

}
