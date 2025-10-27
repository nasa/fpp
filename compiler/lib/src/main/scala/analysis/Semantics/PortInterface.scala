package fpp.compiler.analysis

import fpp.compiler.ast.{Ast, AstNode, Locations}
import fpp.compiler.util._

/** An FPP Port Interface (set of port instances) */
case class PortInterface(
  /** The map from port names to port instances */
  portMap: Map[Name.Unqualified, PortInstance] = Map(),
  /** The map from special port kinds to special port instances */
  specialPortMap: Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special] = Map(),
) {
  private def withPortMap(newPortMap: Map[Name.Unqualified, PortInstance]) =
    this.copy(portMap = newPortMap)

  private def withSpecialPortMap(newSpecialPortMap: Map[Ast.SpecPortInstance.SpecialKind, PortInstance.Special]) =
    this.copy(specialPortMap = newSpecialPortMap)

  /* Check if 'this' port interface implements 'other' */
  def implements(other: PortInterface): Result.Result[Unit] =
    for {
      _ <- {
        // Check all the ports in 'other' to make sure they exist and match to 'this'
        Result.foldLeft (other.portMap.toList) (()) ((_, pii) => {
          val (name, pi) = pii
          this.portMap.get(name) match {
            case Some(found) =>
              // Port exists, make sure it matches theirs
              if found == pi then Right(())
              else Left(SemanticError.PortInterfaceInvalidPort(
                found.getLoc,
                pi.getLoc
              ))
            case None =>
              Left(SemanticError.PortInterfaceMissingPort(
                pi.getLoc
              ))
          }
        })
      }

      _ <- {
        Result.foldLeft (other.specialPortMap.toList) (()) ((_, pii) => {
          val (kind, pi) = pii

          this.specialPortMap.get(kind) match {
            case Some(found) =>
              // The port exists, make sure it's the same theirs
              if found == pi then Right(())
              else Left(SemanticError.PortInterfaceInvalidPort(
                found.getLoc,
                pi.getLoc
              ))
            case None =>
              Left(SemanticError.PortInterfaceMissingPort(
                pi.getLoc
              ))
          }
        })
      }
    } yield ()

  /** Gets a port instance by name */
  def getPortInstance(name: AstNode[Ast.Ident], interfaceName: String): Result.Result[PortInstance] =
    portMap.get(name.data) match {
      case Some(portInstance) => Right(portInstance)
      case None => Left(
        SemanticError.InvalidPortInstanceId(
          Locations.get(name.id),
          name.data,
          interfaceName
        )
      )
    }

  /** Add a port instance */
  def addPortInstance(instance: PortInstance): Result.Result[PortInterface] =
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
    importNodeId: AstNode.Id,
  ): Result.Result[PortInterface] = {
    val init = this.withPortMap(portMap)
    Result.foldLeft(interface.portInterface.portMap.values.toList)(init)((c, pi) => {
      c.addPortInstance(pi.withImportSpecifier(importNodeId)) match {
        case Right(cc) => Right(cc)
        case Left(err) => Left(SemanticError.InterfaceImport(
          Locations.get(importNodeId),
          err,
        ))
      }
    })
  }

  /** Add a port instance to the port map */
  private def updatePortMap(instance: PortInstance):
  Result.Result[PortInterface] = {
    val name = instance.getUnqualifiedName
    portMap.get(name) match {
      case Some(prevInstance) =>
        val loc = instance.getLoc
        val importLocs = instance.getImportLocs
        val prevLoc = prevInstance.getLoc
        val prevImportLocs = prevInstance.getImportLocs
        Left(SemanticError.DuplicatePortInstance(name, loc, importLocs, prevLoc, prevImportLocs))
      case None => 
        val portMap = this.portMap + (name -> instance)
        val t = this.withPortMap(portMap)
        Right(t)
    }
  }

  /** Add a port instance to the special port map */
  private def updateSpecialPortMap(instance: PortInstance.Special):
  Result.Result[PortInterface] = {
    val kind = instance.specifier.kind
    specialPortMap.get(kind) match {
      case Some(prevInstance) =>
        val loc = instance.getLoc
        val importLocs = instance.getImportLocs
        val prevLoc = prevInstance.getLoc
        val prevImportLocs = prevInstance.getImportLocs
        Left(SemanticError.DuplicatePortInstance(kind.toString, loc, importLocs, prevLoc, prevImportLocs))
      case None => 
        val specialPortMap = this.specialPortMap + (kind -> instance)
        val t = this.withSpecialPortMap(specialPortMap)
        Right(t)
    }
  }
}
