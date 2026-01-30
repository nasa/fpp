package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** An FPP interface */
case class Interface(
  /** The AST node defining the interface */
  aNode: Ast.Annotated[AstNode[Ast.DefInterface]],
  /** The imported interfaces */
  importMap: Map[Symbol.Interface, (AstNode.Id, Location)] = Map(),
  /* The port interface of the component */
  portInterface: PortInterface = PortInterface("interface"),
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

  def addImportedInterfaceSymbol(
    symbol: Symbol.Interface,
    importNodeId: AstNode.Id
  ): Result.Result[Interface] = {
    importMap.get(symbol) match {
      case Some((_, prevLoc)) => Left(
        SemanticError.DuplicateInterface(
          symbol.getUnqualifiedName,
          Locations.get(importNodeId),
          prevLoc
        )
      )
      case None =>
        val map = importMap + (symbol -> (importNodeId, Locations.get(importNodeId)))
        Right(this.copy(importMap = map))
    }
  }
}
