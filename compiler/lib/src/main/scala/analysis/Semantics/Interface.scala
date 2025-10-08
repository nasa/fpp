package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** An FPP interface */
case class Interface(
  /** The AST node defining the component */
  aNode: Ast.Annotated[AstNode[Ast.DefInterface]],
  /* The port interface of the component */
  portInterface: PortInterface = PortInterface(),
) {
  /** Add a port instance */
  def addPortInstance(instance: PortInstance): Result.Result[Interface] =
    for {
      pi <- portInterface.addPortInstance(instance)
    }
    yield this.copy(portInterface = pi)

  def addImportedInterface(
    interface: Interface,
    importNodeId: AstNode.Id,
  ): Result.Result[Interface] = {
    for {
      pi <- portInterface.addImportedInterface(interface, importNodeId)
    }
    yield this.copy(portInterface = pi)
  }
}
