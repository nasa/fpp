package fpp.compiler.analysis

import fpp.compiler.ast.{Ast, AstNode, Locations}
import fpp.compiler.util._

trait GenericPortInterface[T <: GenericPortInterface[T]](
  /** Name of the parent symbol that defines this interface */
  defName: String,
  /** The map from port names to port instances */
  portMap: Map[Name.Unqualified, PortInstance] = Map(),
  /** The map from special port kinds to special port instances */
  specialPortMap: Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special] = Map(),
) {
  def withPortMap(newPortMap: Map[Name.Unqualified, PortInstance]): T
  def withSpecialPortMap(newSpecialPortMap: Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special]): T

  /** Gets a port instance by name */
  def getPortInstance(name: AstNode[Ast.Ident]): Result.Result[PortInstance] =
    portMap.get(name.data) match {
      case Some(portInstance) => Right(portInstance)
      case None => Left(
        SemanticError.InvalidPortInstanceId(
          Locations.get(name.id),
          name.data,
          defName
        )
      )
    }

  /** Add a port instance */
  def addPortInstance(instance: PortInstance): Result.Result[T] =
    for {
      c <- updatePortMap(instance)
      c <- instance match {
        case special: PortInstance.Special => c.updateSpecialPortMap(special)
        case _ => Right(c)
      }
    }
    yield c

  def addImportedInterface(
    interface: Interface,
    loc: Location
  ): Result.Result[T] = {
    val init = this.withPortMap(portMap)
    Result.foldLeft(interface.portMap.values.toList)(init)((c, pi) => {
      c.addPortInstance(pi) match {
        case Right(cc) => Right(cc)
        case Left(err) => Left(SemanticError.InterfaceImport(loc, err))
      }
    })
  }

  /** Add a port instance to the port map */
  private def updatePortMap(instance: PortInstance):
  Result.Result[T] = {
    val name = instance.getUnqualifiedName
    portMap.get(name) match {
      case Some(prevInstance) =>
        val loc = instance.getLoc
        val prevLoc = prevInstance.getLoc
        Left(SemanticError.DuplicatePortInstance(name, loc, prevLoc))
      case None => 
        val portMap = this.portMap + (name -> instance)
        val t = this.withPortMap(portMap)
        Right(t)
    }
  }

  /** Add a port instance to the special port map */
  private def updateSpecialPortMap(instance: PortInstance.Special):
  Result.Result[T] = {
    val kind = instance.specifier.kind
    specialPortMap.get(kind) match {
      case Some(prevInstance) =>
        val loc = instance.getLoc
        val prevLoc = prevInstance.getLoc
        Left(SemanticError.DuplicatePortInstance(kind.toString, loc, prevLoc))
      case None => 
        val specialPortMap = this.specialPortMap + (kind -> instance)
        val t = this.withSpecialPortMap(specialPortMap)
        Right(t)
    }
  }
}
