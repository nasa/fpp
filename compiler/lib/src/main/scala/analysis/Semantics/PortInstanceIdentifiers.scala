package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check port instance identifiers */
object PortInstanceIdentiers {

  /** Creates a port instance identifier from an AST node */
  def fromNode(a: Analysis, node: AstNode[Ast.PortInstanceIdentifier]):
    Result.Result[PortInstanceIdentifier] = {
      val data = node.data
      for {
        componentInstance <- a.getComponentInstance(
          data.componentInstance.id
        )
        portInstance <- componentInstance.component.getPortInstance(
          data.portName
        )
      }
      yield PortInstanceIdentifier(node, componentInstance, portInstance)
    }

}
