package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** An FPP interface */
case class Interface(
  /** The AST node defining the component */
  aNode: Ast.Annotated[AstNode[Ast.DefInterface]],
  /** The map from port names to port instances */
  portMap: Map[Name.Unqualified, PortInstance] = Map(),
  /** The map from special port kinds to special port instances */
  specialPortMap: Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special] = Map(),
) extends GenericPortInterface[Interface](aNode._2.data.name, portMap, specialPortMap) {
  def withPortMap(newPortMap: Map[Name.Unqualified, PortInstance]): Interface =
    this.copy(portMap = newPortMap)

  def withSpecialPortMap(
    newSpecialPortMap: Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special]
  ): Interface = this.copy(specialPortMap = newSpecialPortMap)

}
