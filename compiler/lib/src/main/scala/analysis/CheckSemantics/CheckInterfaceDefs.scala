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
    val symbol = Symbol.Interface(aNode)
    a.interfaceMap.get(symbol) match {
      case None =>
        // Interface is not in the map: visit it
        val a1 = a.copy(interface = Some(Interface(aNode)))
        for {
          a <- super.defInterfaceAnnotatedNode(a1, aNode)
          iface <- Right(a.interface.get)
          a <- {
            // Resolve interfaces directly imported by iface, updating a
            val ifaces = iface.importMap.toList
            Result.foldLeft (ifaces) (a) ((a, tl) => {
              defInterfaceAnnotatedNode(a, tl._1.node)
            })
          }
          // Use the updated analysis to resolve iface
          iface <- ResolveInterface.resolve(a, iface)
        } yield a.copy(interfaceMap = a.interfaceMap + (symbol -> iface))
      
      // Interface is already in the map: nothing to do
      case _ => Right(a)
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
      iface <- a.getInterfaceSymbol(ifaceNode.id)
      i <- a.interface.get.addImportedInterfaceSymbol(
        iface,
        node.id,
      )
    } yield a.copy(interface = Some(i))
  }

}
