package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** Check component definitions */
object CheckInterfaceDefs
  extends Analyzer
  with InterfaceAnalyzer
  with ModuleAnalyzer
{

  override def defInterfaceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefInterface]]
  ) = {
    val a1 = a.copy(interface = Some(Interface(aNode)))
    for {
      a <- super.defInterfaceAnnotatedNode(a1, aNode)
    }
    yield {
      val symbol = Symbol.Interface(aNode)
      a.copy(interfaceMap = a.interfaceMap + (symbol -> a.interface.get))
    }
  }

  override def specPortInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) = {
    for {
      instance <- PortInstance.fromSpecPortInstance(a, aNode)
      interface <- a.interface.get.addPortInstance(instance)
    }
    yield a.copy(interface = Some(interface))
  }

  override def specInterfaceImportAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecImport]]
  ) = {
    val node = aNode._2
    val ifaceNode = node.data.sym
    for {
      iface <- a.getInterface(ifaceNode.id)
      i <- a.interface.get.addImportedInterface(
        iface,
        Locations.get(node.id),
      )
    } yield a.copy(interface = Some(i))
  }

}
