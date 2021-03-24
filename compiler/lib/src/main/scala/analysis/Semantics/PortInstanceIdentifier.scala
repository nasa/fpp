package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance identifier */
case class PortInstanceIdentifier(
  /** The component instance */
  componentInstance: ComponentInstance,
  /** The port instance */
  portInstance: PortInstance
) {

  /** Gets the qualified name of a port instance identifier */
  def getQualifiedName = {
    val qn = componentInstance.qualifiedName
    val identList = qn.toIdentList
    Name.Qualified.fromIdentList(identList :+ portInstance.getUnqualifiedName)
  }

}

object PortInstanceIdentifier {

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
      yield PortInstanceIdentifier(
        componentInstance,
        portInstance
      )
    }

}
